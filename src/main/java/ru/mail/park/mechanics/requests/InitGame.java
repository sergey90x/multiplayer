package ru.mail.park.mechanics.requests;

import org.jetbrains.annotations.NotNull;
import ru.mail.park.mechanics.base.ServerPlayerSnap;
import ru.mail.park.mechanics.game.GameObject;
import ru.mail.park.model.Id;
import ru.mail.park.model.UserProfile;

import java.util.List;
import java.util.Map;

/**
 * Created by Solovyev on 03/11/2016.
 */
public class InitGame {
    @SuppressWarnings("NullableProblems")
    public static final class Request {
        @NotNull
        private Long self;
        @NotNull
        private Id<GameObject> selfSquareId;
        @NotNull
        private List<ServerPlayerSnap> players;
//        @NotNull
//        private Map<Long, String> colors;                         // delete
//        @NotNull
//        private Map<Long, String> gunColors;                        // delete
//        @NotNull
//        private Map<Long, String> names;                        // delete

//        public Map<Long, String> getNames() {
//            return names;
//        }
//
//        public void setNames(Map<Long, String> names) {
//            this.names = names;
//        }

        @NotNull
        public Long getSelf() {
            return self;
        }

        public void setSelf(@NotNull Long self) {
            this.self = self;
        }
        @NotNull
        public List<ServerPlayerSnap> getPlayers() {
            return players;
        }
        @NotNull
        public void setPlayers(@NotNull List<ServerPlayerSnap> players) {
            this.players = players;
        }
//        @NotNull
//        public Map<Long, String> getColors() {
//            return colors;
//        }

//        public void setColors(@NotNull Map<Long, String> colors) {
//            this.colors = colors;
//        }
//        @NotNull
//        public Map<Long, String> getGunColors() {
//            return gunColors;
//        }
//
//        public void setGunColors(@NotNull Map<Long, String> gunColors) {
//            this.gunColors = gunColors;
//        }

        public Id<GameObject> getSelfSquareId() {
            return selfSquareId;
        }

        public void setSelfSquareId(Id<GameObject> selfSquareId) {
            this.selfSquareId = selfSquareId;
        }
    }

}
