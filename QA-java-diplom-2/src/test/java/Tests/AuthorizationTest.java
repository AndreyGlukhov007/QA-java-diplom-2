package Tests;

import POJO.Authorization.RequestAuthorizationPOJO;
import POJO.CreateUser.CreateUserDeserializationPOJO.CreateUserDeserializationPOJO;
import POJO.CreateUser.CreateUserDeserializationPOJO.UserCreateRegisteredPOJO;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

// Строчка с изменениями 1 для того чтобы сделать Pull request

public class AuthorizationTest extends MainTest{

    private CreateUserDeserializationPOJO responseСreateUserDeserializationPOJO;
    private String emailTest = getEmailTest();
    private String nameTest = getNameTest();
    private CreateUserDeserializationPOJO responseCreateUserDeserializationPOJO;
    private boolean flag;

    @BeforeEach
    public void setUp(){
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site/";

        // Cоздаём пользователя
        responseСreateUserDeserializationPOJO = createUser(emailTest,"qwerty", nameTest);
    }

    // Логин под существующим пользователем
    @Test
    public void requestAuthorization(){
        // Регистрируемся под новым созданным пользователем
        RequestAuthorizationPOJO requestAuthorizationPOJO = new RequestAuthorizationPOJO(emailTest, "qwerty");
        responseCreateUserDeserializationPOJO = RestAssured
                .given()
                    .header("Content-type", "application/json")
                    .and()
                    .body(requestAuthorizationPOJO)
                .when()
                    .post("api/auth/login")
                .then()
                    .statusCode(200)
                    .extract()
                    .as(CreateUserDeserializationPOJO.class);

        assertEquals(responseCreateUserDeserializationPOJO.isSuccess(), true); // проверяем что в поле success находится значение true

        Pattern patternAccessToken = Pattern.compile("Bearer [A-Za-z0-9_-]+\\.[A-Za-z0-9_-]+\\.[A-Za-z0-9_-]{43}"); // создаём шаблон с регулярным выражением для поля accessToken
        Matcher matcherAccessToken = patternAccessToken.matcher(responseCreateUserDeserializationPOJO.getAccessToken()); // сравниваем полученный ответ от сервера с шаблоном
        boolean resultAccessToken = matcherAccessToken.matches(); // возвращаем результат сравнения шаблона. Результатом должно быть значение true.
        assertTrue(resultAccessToken); // Проводим тест (убеждаем что в ответ от сервера соответствует шаблону и получаем значение true).

        Pattern patternRefreshToken = Pattern.compile("[a-fA-F0-9]{80}"); // создаём шаблон с регулярным выражением для поля refreshToken
        Matcher matcherRefreshToken = patternRefreshToken.matcher(responseCreateUserDeserializationPOJO.getRefreshToken()); // сравниваем полученный ответ от сервера с шаблоном
        boolean resultRefreshToken = matcherRefreshToken.matches(); // возвращаем результат сравнения шаблона. Результатом должно быть значение true.
        assertTrue(resultRefreshToken); // Проводим тест (убеждаем что в ответ от сервера соответствует шаблону и получаем значение true).

        assertEquals(responseCreateUserDeserializationPOJO.getUser().getEmail(), emailTest); // Проверяем что в ответе приходит тот e-mail который был указан при регистрации
        assertEquals(responseCreateUserDeserializationPOJO.getUser().getName(), nameTest); // Проверяем что в ответе приходит то name которое было указано при регистрации

        flag = true;
    }

    @Test
    // Логин с неверным логином
    public void requestAuthorizationErrorLogin(){
        // регистрируемся под новым созданным пользователем, но вводим неверный логин
        RequestAuthorizationPOJO requestAuthorizationPOJO = new RequestAuthorizationPOJO(emailTest+"1", "qwerty");
        UserCreateRegisteredPOJO responseUserCreateRegisteredPOJO = RestAssured
                .given()
                    .header("Content-type", "application/json")
                    .and()
                    .body(requestAuthorizationPOJO)
                .when()
                    .post("api/auth/login")
                .then()
                    .statusCode(401)
                    .extract()
                    .as(UserCreateRegisteredPOJO.class);

        assertEquals(responseUserCreateRegisteredPOJO.isSuccess(), false);
        assertEquals(responseUserCreateRegisteredPOJO.getMessage(), "email or password are incorrect");
    }

    @Test
    // Логин с неверным паролем
    public void requestAuthorizationErrorPassword(){
        // регистрируемся под новым созданным пользователем, но вводим неверный пароль
        RequestAuthorizationPOJO requestAuthorizationPOJO = new RequestAuthorizationPOJO(emailTest, "qwerty1");
        UserCreateRegisteredPOJO responseUserCreateRegisteredPOJO = RestAssured
                .given()
                    .header("Content-type", "application/json")
                    .and()
                    .body(requestAuthorizationPOJO)
                .when()
                    .post("api/auth/login")
                .then()
                    .statusCode(401)
                    .extract()
                    .as(UserCreateRegisteredPOJO.class);

        assertEquals(responseUserCreateRegisteredPOJO.isSuccess(), false);
        assertEquals(responseUserCreateRegisteredPOJO.getMessage(), "email or password are incorrect");
    }

    // Этот метод выполняется после каждого теста. Он удаляет созданного пользователя.
    @AfterEach
    public void deleteUser(){
        // Удаляем пользователя
        if(flag){
            Response responseDeleted = RestAssured
                .given()
                    .header("Authorization", responseCreateUserDeserializationPOJO.getAccessToken())
                .when()
                    .delete("api/auth/user");
        } else{
            // Регистрируемся под новым созданным пользователем
            RequestAuthorizationPOJO requestAuthorizationPOJO = new RequestAuthorizationPOJO(emailTest, "qwerty");
            responseCreateUserDeserializationPOJO = RestAssured
                .given()
                    .header("Content-type", "application/json")
                    .and()
                    .body(requestAuthorizationPOJO)
                .when()
                    .post("api/auth/login")
                .then()
                    .statusCode(200)
                    .extract()
                    .as(CreateUserDeserializationPOJO.class);
            // Удаляем пользователя
            Response responseDeleted = RestAssured
                .given()
                    .header("Authorization", responseCreateUserDeserializationPOJO.getAccessToken())
                .when()
                    .delete("api/auth/user");
        }

    }

}