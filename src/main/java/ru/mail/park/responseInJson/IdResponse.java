package ru.mail.park.responseInJson;


public class IdResponse {
  private Long userid;

  public IdResponse(Long id) {
    this.userid = id;
  }

  public void setId(Long id) {
    this.userid = id;
  }

  public Long getId() {

    return userid;
  }
}
