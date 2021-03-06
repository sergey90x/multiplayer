package ru.mail.park.mechanics.base;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.NotNull;

/**
 * Created by Solovyev on 03/11/2016.
 */
@SuppressWarnings({"NullableProblems", "unused"})
public class ClientSnap {

//    @NotNull
//    private Coords coords;

    @NotNull
    private Sincos sincos;

    @NotNull
    private String button;

    private long frameTime;

    public Sincos getSincos() {
        return sincos;
    }

    public void setSincos(Sincos sincos) {
        this.sincos = sincos;
    }

    //    public void setCoords(Coords coords) {
//        this.coords = coords;
//    }
//
//    public Coords getCoords() {
//
//        return coords;
//    }

    public ClientSnap() {
    }

    public ClientSnap(@NotNull String button) {
        this.button = button;
    }

    public String getButton() {
        return button;
    }

    public void setButton(String button) {
        this.button = button;
    }

    //        @NotNull
//    private Way direction;
//
//    @NotNull
//    private Coords mouse;
//    private boolean isFiring;

//    @NotNull
//    public Way getDirection() {
//        return direction;
//    }

//    @NotNull
//    public Coords getMouse() {
//        return mouse;
//    }

//    @JsonProperty("isFiring")
//    public boolean isFiring() {
//        return isFiring;
//    }


    public long getFrameTime() {
        return frameTime;
    }

//    public void setDirection(@NotNull Way direction) {
//        this.direction = direction;
//    }

//    public void setMouse(@NotNull Coords mouse) {
//        this.mouse = mouse;
//    }
//
//    public void setFiring(boolean firing) {
//        isFiring = firing;
//    }
//
    public void setFrameTime(long frameTime) {
        this.frameTime = frameTime;
    }
}
