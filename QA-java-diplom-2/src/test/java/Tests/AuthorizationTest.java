package Tests;

import POJO.Authorization.RequestAuthorizationPOJO;
import POJO.CreateUser.CreateUserDeserializationPOJO.CreateUserDeserializationPOJO;
import POJO.CreateUser.CreateUserDeserializationPOJO.UserCreateRegisteredPOJO;
import POJO.CreateUser.CreateUserSerializationPOJO.CreateUserSerializationPOJO;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AuthorizationTest {

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

    // Логин под существующим пользователем
    @Test
    public void requestAuthorization(){
        // создаём пользователя
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

        // регистрируемся под новым созданным пользователем
        RequestAuthorizationPOJO requestAuthorizationPOJO = new RequestAuthorizationPOJO(emailTest, "qwerty");
        CreateUserDeserializationPOJO response = RestAssured
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


        assertEquals(response.isSuccess(), true); // проверяем что в поле success находится значение true

        Pattern patternAccessToken = Pattern.compile("Bearer [A-Za-z0-9_-]+\\.[A-Za-z0-9_-]+\\.[A-Za-z0-9_-]{43}"); // создаём шаблон с регулярным выражением для поля accessToken
        Matcher matcherAccessToken = patternAccessToken.matcher(response.getAccessToken()); // сравниваем полученный ответ от сервера с шаблоном
        boolean resultAccessToken = matcherAccessToken.matches(); // возвращаем результат сравнения шаблона. Результатом должно быть значение true.
        assertTrue(resultAccessToken); // Проводим тест (убеждаем что в ответ от сервера соответствует шаблону и получаем значение true).

        Pattern patternRefreshToken = Pattern.compile("[a-fA-F0-9]{80}"); // создаём шаблон с регулярным выражением для поля refreshToken
        Matcher matcherRefreshToken = patternRefreshToken.matcher(response.getRefreshToken()); // сравниваем полученный ответ от сервера с шаблоном
        boolean resultRefreshToken = matcherRefreshToken.matches(); // возвращаем результат сравнения шаблона. Результатом должно быть значение true.
        assertTrue(resultRefreshToken); // Проводим тест (убеждаем что в ответ от сервера соответствует шаблону и получаем значение true).

        assertEquals(response.getUser().getEmail(), emailTest); // Проверяем что в ответе приходит тот e-mail который был указан при регистрации
        assertEquals(response.getUser().getName(), nameTest); // Проверяем что в ответе приходит то name которое было указано при регистрации
    }

    @Test
    // Логин с неверным логином
    public void requestAuthorizationErrorLogin(){
        // создаём пользователя
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

        // регистрируемся под новым созданным пользователем, но вводим неверный логин
        RequestAuthorizationPOJO requestAuthorizationPOJO = new RequestAuthorizationPOJO(emailTest+"1", "qwerty");
        UserCreateRegisteredPOJO response = RestAssured
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

        assertEquals(response.isSuccess(), false);
        assertEquals(response.getMessage(), "email or password are incorrect");
    }

    @Test
    // Логин с неверным паролем
    public void requestAuthorizationErrorPassword(){
        // создаём пользователя
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

        // регистрируемся под новым созданным пользователем, но вводим неверный пароль
        RequestAuthorizationPOJO requestAuthorizationPOJO = new RequestAuthorizationPOJO(emailTest, "qwerty1");
        UserCreateRegisteredPOJO response = RestAssured
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

        assertEquals(response.isSuccess(), false);
        assertEquals(response.getMessage(), "email or password are incorrect");
    }

}
