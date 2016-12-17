package ru.mail.park.mechanics;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.mail.park.mechanics.base.ClientSnap;
import ru.mail.park.mechanics.internal.*;
import ru.mail.park.model.UserProfile;
import ru.mail.park.service.AccountService;
import ru.mail.park.websocket.RemotePointService;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author k.solovyev
 */
@SuppressWarnings({"unused", "FieldMayBeFinal"})
@Service
public class GameMechanicsImpl implements GameMechanics {
    @NotNull
    private static final Logger LOGGER = LoggerFactory.getLogger(GameMechanicsImpl.class);

    @NotNull
    private AccountService accountService;

    @NotNull
    private ClientSnapshotsService clientSnapshotsService;

    @NotNull
    private ServerSnapshotService serverSnapshotService;

    @NotNull
    private RemotePointService remotePointService;

    @NotNull
    private MovementService movementService;

    @NotNull
    private GameSessionService gameSessionService;

    @NotNull
    private Set<Long> playingUsers = new HashSet<>();

    @NotNull
    private ConcurrentLinkedQueue<Long> waiters = new ConcurrentLinkedQueue<>();

    @NotNull
    private final Queue<Runnable> tasks = new ConcurrentLinkedQueue<>();

    @SuppressWarnings("LongLine")
    public GameMechanicsImpl(@NotNull AccountService accountService, @NotNull ServerSnapshotService serverSnapshotService,
                             @NotNull RemotePointService remotePointService, @NotNull MovementService movementService,
                             @NotNull GameSessionService gameSessionService) {
        this.accountService = accountService;
        this.serverSnapshotService = serverSnapshotService;
        this.remotePointService = remotePointService;
        this.movementService = movementService;
        this.gameSessionService = gameSessionService;
        this.clientSnapshotsService = new ClientSnapshotsService(movementService);
    }

    @Override
    public void addClientSnapshot(@NotNull Long userId, @NotNull ClientSnap clientSnap) {
        tasks.add(() -> clientSnapshotsService.pushClientSnap(userId, clientSnap));
    }

    @Override
    public void addUser(@NotNull Long user) {
        if (gameSessionService.isPlaying(user)) {
            return;
        }
        waiters.add(user);
    }

    private void tryStartGames() {    // Найти пару игроков и если пара есть начать игру.
        final Set<UserProfile> matchedPlayers = new LinkedHashSet<>();

        while (waiters.size() >= 2 || waiters.size() >= 1 && matchedPlayers.size() >= 1) {
            final Long candidate = waiters.poll();
            if (!insureCandidate(candidate)) {
                continue;
            }
            matchedPlayers.add(accountService.getUserById(candidate));
            if(matchedPlayers.size() == 2) {
                final Iterator<UserProfile> iterator = matchedPlayers.iterator();
                gameSessionService.startGame(iterator.next(), iterator.next());
                matchedPlayers.clear();
            }
        }
        matchedPlayers.stream().map(UserProfile::getId).forEach(waiters::add);
    }

    private boolean insureCandidate(@NotNull Long candidate) {
        return remotePointService.isConnected(candidate) &&
                accountService.getUserById(candidate) != null;
    }

    @Override
    public void gmStep(long frameTime) {
        while (!tasks.isEmpty()) {
            final Runnable nextTask = tasks.poll();
            if (nextTask != null) {
                try {
                    nextTask.run();
                } catch (RuntimeException ex) {
                    LOGGER.error("Cant handle game task", ex);
                }
            }
        }

        for (GameSession session : gameSessionService.getSessions()) {
            clientSnapshotsService.processSnapshotsFor(session);
        }

        movementService.executeMoves();

        final Iterator<GameSession> iterator = gameSessionService.getSessions().iterator();
        final List<GameSession> sessionsToTerminate = new ArrayList<>();
        while (iterator.hasNext()) {
            final GameSession session = iterator.next();
            try {
//                serverSnapshotService.sendSnapshotsFor(session, frameTime);
                serverSnapshotService.sendSnapshotsFor(session);
            } catch (RuntimeException ex) {
                LOGGER.error("Failed send snapshots, terminating the session", ex);
                sessionsToTerminate.add(session);
            }
        }
        sessionsToTerminate.forEach(gameSessionService::notifyGameIsOver);

        tryStartGames();
        clientSnapshotsService.clear();
        movementService.clear();
    }

    @Override
    public void reset() {

    }
}
