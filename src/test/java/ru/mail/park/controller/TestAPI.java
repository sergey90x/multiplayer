package ru.mail.park.controller;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ru.mail.park.TestUtil;

import ru.mail.park.model.UserProfile;
import ru.mail.park.service.AccountService;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest(webEnvironment = RANDOM_PORT)
@RunWith(SpringRunner.class)
public class TestAPI {

    private MockMvc mockMvc;

    @MockBean
    private AccountService accountServiceMock;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private TestRestTemplate restTemplate;

    @Before
    public void setUp() {
        Mockito.reset(accountServiceMock);

        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }


    @Test
    public void getAllUsersTest() throws Exception{
        UserProfile user1 = new UserProfile("Sergey", "cheremisin.sergey@yandex.ru", "password11");
        UserProfile user2 = new UserProfile("Ilya", "nikitin.ilya@mail.ru", "password22");
        UserProfile user3 = new UserProfile("Ephrosiniya", "zerminova.phrosia@gmial.com", "password33");

        when(accountServiceMock.getAllUsers()).thenReturn(Arrays.asList(user1, user2, user3));
        List<UserProfile> allUsers = accountServiceMock.getAllUsers();


        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].login", is("Sergey")))
                .andExpect(jsonPath("$[0].name", is("cheremisin.sergey@yandex.ru")))
                .andExpect(jsonPath("$[0].email", is("password11")))
                .andExpect(jsonPath("$[1].login", is("Ilya")))
                .andExpect(jsonPath("$[1].name", is("nikitin.ilya@mail.ru")))
                .andExpect(jsonPath("$[1].email", is("password22")))
                .andExpect(jsonPath("$[2].login", is("Ephrosiniya")))
                .andExpect(jsonPath("$[2].name", is("zerminova.phrosia@gmial.com")))
                .andExpect(jsonPath("$[2].email", is("password33")));

    }

    @Test
    public void getUserByIdTest404() throws Exception {

    when(accountServiceMock.getUserById(4L)).thenThrow(new RuntimeException(""));

        mockMvc.perform(get("/api/users/{id}", 1L))
               .andExpect(status().isNotFound());

    verify(accountServiceMock, times(1)).getUserById(1L);
    verifyNoMoreInteractions(accountServiceMock);
}

    @Test
    public void getUserByIdTest1() throws Exception{
        UserProfile user = new UserProfile("Sergey", "cheremisin.sergey@yandex.ru", "password11");

        when(accountServiceMock.getUserById(1L)).thenReturn(user);

        mockMvc.perform(get("/api/users/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.login", is("Sergey")));

        verify(accountServiceMock, times(1)).getUserById(1L);
        verifyNoMoreInteractions(accountServiceMock);
    }


    @Test
    public void getUserByidTest2(){
        when(accountServiceMock.getUserById(1L)).thenReturn(new UserProfile("Ilya", "nikitin.ilya@mail.ru", "password22"));

        ResponseEntity<UserProfile> userResp = restTemplate.getForEntity("/api/users/1", UserProfile.class);
        assertEquals(HttpStatus.OK, userResp.getStatusCode());

        UserProfile user = userResp.getBody();
        assertNotNull(user);

        assertEquals("Ilya", user.getLogin());
    }


    @Test
    public void signinTestParametersError() throws Exception{
        String login = TestUtil.createStringWithLength(100);
        String email = TestUtil.createStringWithLength(100);
        String password = TestUtil.createStringWithLength(100);

        UserProfile user = new UserProfile(login, email, password);

        mockMvc.perform(post("/api/users")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)

        )
                .andExpect(status().isBadRequest());
        verifyZeroInteractions(accountServiceMock);
    }



    @Test
    public void signinTest() throws Exception{

        UserProfile user1 = new UserProfile("Pasha", "ddd", "pasha@mail.ru", "password44");

        when(accountServiceMock.addUser(user1.getLogin(), user1.getName(), user1.getEmail(), user1.getPassword())).thenReturn(4L);

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(user1))
        )
                .andExpect(status().isOk());

    }

    @Test
    public void removeUserById() throws Exception {
        UserProfile deleted = new UserProfile("Sergey", "cheremisin.sergey@yandex.ru", "password11");
        boolean res = false;
        when(accountServiceMock.removeUserById(1L)).thenReturn(res);

        assertEquals(false, res);
    }


    @Test
    public void removeUserByIdException() throws Exception {
        when(accountServiceMock.removeUserById(5L)).thenThrow(new RuntimeException(""));

        mockMvc.perform(delete("/api/todo/{id}", 5))
                .andExpect(status().isNotFound());

        verifyZeroInteractions(accountServiceMock);
    }

}
