package ru.mail.park.mechanics.base;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.NotNull;
import ru.mail.park.mechanics.avatar.Square;
import ru.mail.park.mechanics.game.Snap;
import ru.mail.park.model.Id;
import ru.mail.park.model.UserProfile;

/**
 * Created by Solovyev on 03/11/2016.
 */
@SuppressWarnings({"NullableProblems", "unused"})
public class ServerPlayerSnap {
    @NotNull
    private Long userId;

    @NotNull
    private Snap<Square> playerSquare;

    public Snap<Square> getPlayerSquare() {
        return playerSquare;
    }

    public void setPlayerSquare(Snap<Square> playerSquare) {
        this.playerSquare = playerSquare;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
