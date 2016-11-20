package ru.mail.park.responseInJson;


import java.io.Serializable;

public class RegistrationRequest implements Serializable {
    private String login;
    private String name;
    private String password;
    private String email;

    public String getLogin() {
        return login;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }
}
