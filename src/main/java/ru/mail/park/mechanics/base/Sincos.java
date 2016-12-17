package ru.mail.park.mechanics.base;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by serqeycheremisin on 16/12/2016.
 */
public class Sincos {

    public final double sin;
    public final double cos;


    public Sincos(@JsonProperty("sin") double sin, @JsonProperty("cos") double cos) {
        this.sin = sin;
        this.cos = cos;
    }


    public double getSin() {
        return sin;
    }

    public double getCos() {
        return cos;
    }


}
