package Tests;

import POJO.ChangeUser.ChangeUserDeserializationPOJO;
import POJO.CreateUser.CreateUserDeserializationPOJO.CreateUserDeserializationPOJO;
import POJO.CreateUser.CreateUserDeserializationPOJO.User;
import POJO.CreateUser.CreateUserDeserializationPOJO.UserCreateRegisteredPOJO;
import POJO.CreateUser.CreateUserSerializationPOJO.CreateUserSerializationPOJO;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ChangeUserTest {

    //Этот метод нужен чтобы создавать уникальных пользователей. К логину и имени добавляется актуальная дата и время (часы, минуты, секунды).
    public static String getCurrentDateTime() {
        // Получаем текущие дату и время
        LocalDateTime now = LocalDateTime.now();

        // Форматируем вывод
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH:mm:ss");

        // Возвращаем отформатированную строку
        return now.format(formatter);
    }

    @BeforeEach
    public void setUp(){
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site/";
    }

    // Изменение данных пользователя с авторизацией.
    @Test
    public void requestAuthorization(){
        // Создаём пользователя
        String emailTest = "test"+getCurrentDateTime()+"@mail.ru";
        String nameTest = "name " + getCurrentDateTime();

        CreateUserSerializationPOJO createUserPOJO= new CreateUserSerializationPOJO(emailTest, "qwerty", nameTest);
        CreateUserDeserializationPOJO responseСreateUserDeserializationPOJO = RestAssured
                .given()
                    .header("Content-type", "application/json")
                    .and()
                    .body(createUserPOJO)
                .when()
                    .post("api/auth/register")
                .then()
                    .statusCode(200) // Проверка кода ответа
                    .extract()
                    .as(CreateUserDeserializationPOJO.class);

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
        // Создаём пользователя
        String emailTest = "test"+getCurrentDateTime()+"@mail.ru";
        String nameTest = "name " + getCurrentDateTime();

        CreateUserSerializationPOJO createUserPOJO= new CreateUserSerializationPOJO(emailTest, "qwerty", nameTest);
        CreateUserDeserializationPOJO responseСreateUserDeserializationPOJO = RestAssured
                .given()
                    .header("Content-type", "application/json")
                    .and()
                    .body(createUserPOJO)
                .when()
                    .post("api/auth/register")
                .then()
                    .statusCode(200) // Проверка кода ответа
                    .extract()
                    .as(CreateUserDeserializationPOJO.class);

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
        // Создаём пользователя
        String emailTest = "test" + getCurrentDateTime() + "@mail.ru";
        String nameTest = "name " + getCurrentDateTime();

        CreateUserSerializationPOJO createUserPOJO = new CreateUserSerializationPOJO(emailTest, "qwerty", nameTest);
        CreateUserDeserializationPOJO responseСreateUserDeserializationPOJO = RestAssured
                .given()
                    .header("Content-type", "application/json")
                    .and()
                    .body(createUserPOJO)
                .when()
                    .post("api/auth/register")
                .then()
                    .statusCode(200) // Проверка кода ответа
                    .extract()
                    .as(CreateUserDeserializationPOJO.class);

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
