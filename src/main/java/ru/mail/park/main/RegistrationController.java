package ru.mail.park.main;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import ru.mail.park.responseInJson.IdResponse;
import ru.mail.park.responseInJson.RegistrationRequest;
import ru.mail.park.responseInJson.SuccessResponse;
import ru.mail.park.model.UserProfile;
import ru.mail.park.service.AccountService;

import java.util.logging.Level;
import java.util.logging.Logger;

import static java.util.Objects.isNull;

@RestController
public class RegistrationController {

  @Autowired
  private HttpSession httpSession;

  @Autowired
  private AccountService accountService;

  ObjectMapper objectMapper = new ObjectMapper();


  @RequestMapping(value = "/api/users", method = RequestMethod.GET)
  public ResponseEntity<String> getAllUsers() {
      objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

        String arrayToJson = null;
      try {
             arrayToJson = objectMapper.writeValueAsString(accountService.getAllUsers());
      }catch (JsonProcessingException e) {
          e.printStackTrace();
        final Logger log = Logger.getLogger(RegistrationController.class.getName());
        log.log(Level.INFO, "JsonProcessingException in getAllUsers");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\":\"Internal server error\"}");
      }
      return ResponseEntity.ok(arrayToJson);
  }

  @RequestMapping(value = "/api/users/{id}", method = RequestMethod.GET)
  public ResponseEntity<?> getUserById(@PathVariable("id") Long id) {

    final UserProfile user = accountService.getUserById(id);
    if (user == null) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\":\"User not exist\"}");
    }
    return ResponseEntity.ok(new SuccessResponse(user.getLogin()));
  }

  @RequestMapping(value = "/api/users/{id}", method = RequestMethod.DELETE)
  public ResponseEntity<String> removeUserById(@PathVariable("id") Long id) {

    if (!isNull(httpSession.getAttribute("userId")) && !id.equals((Integer) httpSession.getAttribute("userId"))) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Cannot remove other user");
    }
    accountService.removeUserById(id);
    return ResponseEntity.ok("User removed");
  }

  @RequestMapping(value = "/api/users", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> signup(@RequestBody RegistrationRequest body) {

    final String login = body.getLogin();
    final String password = body.getPassword();
    final String email = body.getEmail();
    final String name = body.getName();

    if (StringUtils.isEmpty(login) || StringUtils.isEmpty(password) || StringUtils.isEmpty(email)) {
      return ResponseEntity.status(HttpStatus.NO_CONTENT).body("{\"error\":\"empty data\"}");
    }

    final UserProfile existingUser = accountService.existingUserByLogin(login);
    if (existingUser != null) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
          "{\"error\":\"User with this login already exist\"}");
    }

    final UserProfile duplicateEmail = accountService.duplicateEmail(email);
    if (duplicateEmail != null) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
              "{\"error\":\"User with this email already exist\"}");
    }

    final Long id = accountService.addUser(login, name, password, email);


    return ResponseEntity.ok(new IdResponse(id));
  }

  @RequestMapping(value = "/api/sessions", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, headers="Accept=*/*")
  public ResponseEntity signin(@RequestBody RegistrationRequest body) {
    final String login = body.getLogin();
    final String password = body.getPassword();

    if (StringUtils.isEmpty(login) || StringUtils.isEmpty(password)) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\":\"invalid data\"}");
    }

    final UserProfile user = accountService.existingUserByLogin(login);

    if(user != null && !password.equals(user.getPassword())){
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\":\"invalid login or password\"}");
    }

    httpSession.setAttribute("userId", user.getId());

    return ResponseEntity.ok().body("{\"sessionId\":\" " + httpSession.getId() + " \"}");
  }

  @RequestMapping(value = "/api/auth", method = RequestMethod.GET)
  public ResponseEntity isauth(){
    if(isNull(httpSession.getAttribute("userId"))){
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("{\"error\":\"Access allowed only for registered users\"}");
    }

    return ResponseEntity.ok().body("User logged in");
  }

}
