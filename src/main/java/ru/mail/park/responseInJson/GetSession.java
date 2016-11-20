package ru.mail.park.responseInJson;


public class GetSession {
    private Integer userid;

    public GetSession(Integer userid) {
        this.userid = userid;
    }

    public Integer getUserId() {

        return userid;
    }
}
