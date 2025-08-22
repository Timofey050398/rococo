package timofeyqa.rococo.controller;

import com.nimbusds.jwt.SignedJWT;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import timofeyqa.rococo.config.RococoGatewayServiceConfig;
import timofeyqa.rococo.model.UserJson;
import timofeyqa.rococo.service.api.RestUserdataClient;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {
  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private RestUserdataClient userdataClient;

  private UserController userController;

  @MockitoBean
  private RococoGatewayServiceConfig config;

  private Jwt jwt;

  @BeforeEach
  void setUp() {
    jwt = mock(Jwt.class);
    when(jwt.getSubject()).thenReturn("user123");

    userController = new UserController(userdataClient);
  }

  @Test
  void getUser_shouldReturnUserJson() {
    UserJson expectedUser = new UserJson(
        UUID.randomUUID(),
        "user123",
        "Timofey",
        "QA",
        null);

    when(userdataClient.getUser("user123")).thenReturn(expectedUser);

    UserJson actualUser = userController.getUser(jwt);

    assertEquals(expectedUser, actualUser);
    verify(userdataClient).getUser("user123");
  }

  @Test
  void testGetUser_withRealToken() throws Exception {

    String tokenValue = "eyJraWQiOiIxODcyMzI0Ni0zZWMxLTQ0MzktYmQyZS0zMTUxMGZmYmI1MTkiLCJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJUaW1vZmV5IiwiYXVkIjoiY2xpZW50IiwiYXpwIjoiY2xpZW50IiwiYXV0aF90aW1lIjoxNzU0NTYyNDU3LCJpc3MiOiJodHRwOi8vYXV0aC5yb2NvY28uZGM6OTAwMCIsImV4cCI6MTc1NDU2NDI1NywiaWF0IjoxNzU0NTYyNDU3LCJqdGkiOiI1MTNkOWJjYS0xZmU3LTQzNmEtOTJhMS05MDc3YjI5YmI0MzQiLCJzaWQiOiJxNW5neHM1ZTU2ajNDQVRMMjBOVG5mbUg3RFpPWjlPclVtcUdYaWpCODlnIn0.LlesdKOFGNb-iiEZ3AS1MTPUPKZCpbHp2aH_T6hIcADSY5tVUZdqSGBeXkMkYDmmcyOfTWt-zWG2NqujLjJ8vbM-baZlgpIRP200pjOlSBbh25UoaYSVQDcPhQ2CUZ2aauB33qRTsIwWQkom6FhWo5PS-T0vQH-5vqEPTbHTV581QQW3wMA5K7oaZHWo8OFc88k80D-1tMKU_dasG4G0k1i1WKDK1QS7D0-M9bWG7aEswMeGqHgsUMuWCx73cJbqpGAkOn8CTJnU3MatDN-Drz-DvMdHp9IIKWEW1WlJ0-dhVTdwsgyffNk8KeZg4zZhSHcbEcdcaPqH0j18DyRE-A";
    SignedJWT signedJWT = SignedJWT.parse(tokenValue);

    Map<String, Object> headers = signedJWT.getHeader().toJSONObject();
    Map<String, Object> claims = signedJWT.getJWTClaimsSet().getClaims();

    Instant issuedAt = signedJWT.getJWTClaimsSet().getIssueTime().toInstant();
    Instant expiresAt = signedJWT.getJWTClaimsSet().getExpirationTime().toInstant();

    Jwt jwt = new Jwt(tokenValue, issuedAt, expiresAt, headers, claims);

    // Подготовка мока
    UserJson mockedUser = new UserJson(
        UUID.randomUUID(),
        "Timofey",
        "firstname",
        "lastname",
        null
    );

    when(userdataClient.getUser("Timofey")).thenReturn(mockedUser);

    mockMvc.perform(get("/api/user")
            .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(jwt)))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.username").value("Timofey"))
        .andExpect(jsonPath("$.firstname").value("firstname"))
        .andExpect(jsonPath("$.lastname").value("lastname"));
  }



  @Test
  void updateUser_shouldReturnUpdatedUserJson() {
    UserJson inputUser = new UserJson(
        UUID.randomUUID(),
        "user123",
        "Timofey",
        "QA",
        null);

    UserJson updatedUser = new UserJson(
        inputUser.id(),
        inputUser.username(),
        "TimofeyUpdated",
        "QAUpdated",
        null);

    when(userdataClient.updateUser(inputUser, "user123")).thenReturn(updatedUser);

    UserJson actualUpdatedUser = userController.updateUser(inputUser, jwt);

    assertEquals(updatedUser, actualUpdatedUser);
    verify(userdataClient).updateUser(inputUser, "user123");
  }
}
