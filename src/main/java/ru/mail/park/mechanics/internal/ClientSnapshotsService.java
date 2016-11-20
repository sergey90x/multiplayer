package ru.mail.park.mechanics.internal;

import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import ru.mail.park.mechanics.Config;
import ru.mail.park.mechanics.GameSession;
import ru.mail.park.mechanics.avatar.GameUser;
import ru.mail.park.mechanics.avatar.MousePart;
import ru.mail.park.mechanics.avatar.PositionPart;
import ru.mail.park.mechanics.avatar.Square;
import ru.mail.park.mechanics.base.ClientSnap;
import ru.mail.park.mechanics.base.Coords;
import ru.mail.park.mechanics.base.Way;
import ru.mail.park.model.Id;
import ru.mail.park.model.UserProfile;

import java.util.*;

/**
 * Not thread safe! Per game mechanic service
 * Created by Solovyev on 03/11/2016.
 */
@Service
public class ClientSnapshotsService {

    private final Map<Long, List<ClientSnap>> snaps = new HashMap<>();

    @NotNull
    private final MovementService movementService;

    public ClientSnapshotsService(@NotNull MovementService movementService) {
        this.movementService = movementService;
    }

    public void pushClientSnap(@NotNull Long user, @NotNull ClientSnap snap) {
        this.snaps.putIfAbsent(user, new ArrayList<>());
        final List<ClientSnap> clientSnaps = snaps.get(user);
        clientSnaps.add(snap);
    }

    @NotNull
    public List<ClientSnap> getSnapForUser(@NotNull Long user) {
        return snaps.getOrDefault(user, Collections.emptyList());
    }

    public void processSnapshotsFor(@NotNull GameSession gameSession) {
        final Collection<GameUser> players = new ArrayList<>();
        players.add(gameSession.getFirst());
        players.add(gameSession.getSecond());
        for (GameUser player : players) {
            final List<ClientSnap> playerSnaps = getSnapForUser(player.getId());
            if (playerSnaps.isEmpty()) {
                continue;
            }
            for (ClientSnap snap : playerSnaps) {
//                processMovement(player, snap.getDirection(), snap.getFrameTime());

                moveSquareBy(player.getSquare(), snap.getCoords());
            }
//            final ClientSnap lastSnap = playerSnaps.get(playerSnaps.size() - 1);
//            processMouseMove(player, lastSnap.getMouse());

            //TODO:Firing
        }
    }


//    private void processMovement(@NotNull GameUser gameUser, @NotNull Way way, long frameTime) {
//        final PositionPart positionPart = gameUser.getSquare().claimPart(PositionPart.class);
//        final Coords body = positionPart.getBody();
//        switch (way) {
//            case Left: {
//                moveSquareBy(gameUser.getSquare(), -Config.SQUARE_SPEED * frameTime, 0);
//                break;
//            }
//            case Right: {
//                moveSquareBy(gameUser.getSquare(), Config.SQUARE_SPEED * frameTime, 0);
//                break;
//            }
//            case Up: {
//                moveSquareBy(gameUser.getSquare(), 0, -Config.SQUARE_SPEED * frameTime);
//                break;
//            }
//            case Down: {
//                moveSquareBy(gameUser.getSquare(), 0, Config.SQUARE_SPEED * frameTime);
//                break;
//            }
//            case None: {
//                break;
//            }
//        }
//    }

    private void moveSquareBy(@NotNull Square square, Coords body) {

        final PositionPart positionPart = square.claimPart(PositionPart.class);
        positionPart.setBody(body);
//        final Coords lastDesirablePoint = positionPart.getLastDesirablePoint();
//        final double newX = Math.min(Config.PLAYGROUND_WIDTH - Config.SQUARE_SIZE, lastDesirablePoint.x + dx);
//        final double newY = Math.min(Config.PLAYGROUND_HEIGHT - Config.SQUARE_SIZE, lastDesirablePoint.y + dy);
//        positionPart.addDesirableCoords(new Coords(newX, newY));
        movementService.registerObjectToMove(square);
    }

    private void processMouseMove(@NotNull GameUser gameUser, @NotNull Coords mouse) {
        gameUser.getSquare().claimPart(MousePart.class).setMouse(mouse);
    }


    public void clear() {
        snaps.clear();
    }

}
