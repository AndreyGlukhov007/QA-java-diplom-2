package Tests;

import POJO.ChangeUser.ChangeUserDeserializationPOJO;
import POJO.CreateUser.CreateUserDeserializationPOJO.CreateUserDeserializationPOJO;
import POJO.CreateUser.CreateUserDeserializationPOJO.User;
import POJO.CreateUser.CreateUserDeserializationPOJO.UserCreateRegisteredPOJO;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ChangeUserTest extends MainTest{

    private CreateUserDeserializationPOJO responseСreateUserDeserializationPOJO;
    private String emailTest = getEmailTest();
    private String nameTest = getNameTest();

    @BeforeEach
    public void setUp(){
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site/";

        // Создаём пользователя
        responseСreateUserDeserializationPOJO = createUser(emailTest,"qwerty", nameTest);
    }

    // Изменение данных пользователя с авторизацией.
    @Test
    public void requestAuthorization(){
        // Тут я из accessToken вырезаю "Bearer " вместе с пробелом, оставляю только токен для авторизации.
        String token = responseСreateUserDeserializationPOJO.getAccessToken();
        String jwt = token.replaceFirst("^Bearer ", "");

        // Изменение данных пользователя с авторизацией.
        User user = new User(emailTest+"_new", nameTest+"_new");
        ChangeUserDeserializationPOJO response = RestAssured
                .given()
                    .header("Content-type", "application/json")
                    .auth().oauth2(jwt)
                    .body(user)
                .when()
                    .patch("api/auth/user")
                .then()
                    .statusCode(200)
                    .extract()
                    .as(ChangeUserDeserializationPOJO.class);

        assertEquals(response.getUser().getEmail(), emailTest+"_new");
        assertEquals(response.getUser().getName(), nameTest+"_new");
    }

    // Изменение данных пользователя без авторизации.
    @Test
    public void requestNotAuthorization(){
        // Изменение данных пользователя без авторизацией.
        User user = new User(emailTest+"_new", nameTest+"_new");
        UserCreateRegisteredPOJO response = RestAssured
                .given()
                    .header("Content-type", "application/json")
                    .body(user)
                .when()
                    .patch("api/auth/user")
                .then()
                    .statusCode(401)
                    .extract()
                    .as(UserCreateRegisteredPOJO.class);
        assertEquals(response.isSuccess(), false);
        assertEquals(response.getMessage(), "You should be authorised");
    }

    // Изменение данных пользователя без авторизации.
    @Test
    public void requestNotAuthorizationEmail() {
        // Тут я из accessToken вырезаю "Bearer " вместе с пробелом, оставляю только токен для авторизации.
        String token = responseСreateUserDeserializationPOJO.getAccessToken();
        String jwt = token.replaceFirst("^Bearer ", "");

        // Изменение данных пользователя с авторизацией но с пустым email.
        User user = new User("");
        UserCreateRegisteredPOJO response = RestAssured
                .given()
                    .header("Content-type", "application/json")
                    .auth().oauth2(jwt)
                    .body(user)
                .when()
                    .patch("api/auth/user")
                .then()
                    .statusCode(403)
                    .extract()
                    .as(UserCreateRegisteredPOJO.class);

        assertEquals(response.isSuccess(), false);
        assertEquals(response.getMessage(), "User with such email already exists");
    }

}
