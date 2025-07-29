package Tests;

import POJO.CreateUser.CreateUserDeserializationPOJO.UserCreateRegisteredPOJO;
import POJO.CreateUser.CreateUserSerializationPOJO.CreateUserSerializationPOJO;
import POJO.CreateUser.CreateUserDeserializationPOJO.CreateUserDeserializationPOJO;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CreateUserTest extends MainTest{

    private CreateUserDeserializationPOJO response;
    private String emailTest = getEmailTest();
    private String nameTest = getNameTest();

    @BeforeEach
    public void setUp(){
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site/";
    }

    // создать уникального пользователя;
    @Test
    public void createUniqueUser() throws InterruptedException {

        // Cоздаём пользователя
        response = createUser(emailTest,"qwerty", nameTest);

        assertEquals(response.isSuccess(), true); // Проверяем что в ответе приходит значение true
        assertEquals(emailTest, response.getUser().getEmail()); // Проверяем что в ответе приходит тот e-mail который был указан при регистрации
        assertEquals(nameTest, response.getUser().getName()); // Проверяем что в ответе приходит то name которое было указано при регистрации

        Pattern patternAccessToken = Pattern.compile("Bearer [A-Za-z0-9_-]+\\.[A-Za-z0-9_-]+\\.[A-Za-z0-9_-]{43}"); // создаём шаблон с регулярным выражением для поля accessToken
        Matcher matcherAccessToken = patternAccessToken.matcher(response.getAccessToken()); // сравниваем полученный ответ от сервера с шаблоном
        boolean resultAccessToken = matcherAccessToken.matches(); // возвращаем результат сравнения шаблона. Результатом должно быть значение true.
        assertTrue(resultAccessToken); // Проводим тест (убеждаем что в ответ от сервера соответствует шаблону и получаем значение true).

        Pattern patternRefreshToken = Pattern.compile("[a-fA-F0-9]{80}"); // создаём шаблон с регулярным выражением для поля refreshToken
        Matcher matcherRefreshToken = patternRefreshToken.matcher(response.getRefreshToken()); // сравниваем полученный ответ от сервера с шаблоном
        boolean resultRefreshToken = matcherRefreshToken.matches(); // возвращаем результат сравнения шаблона. Результатом должно быть значение true.
        assertTrue(resultRefreshToken); // Проводим тест (убеждаем что в ответ от сервера соответствует шаблону и получаем значение true).
    }

    // создать пользователя, который уже зарегистрирован;
    @Test
    public void createRegisteredUser(){

        CreateUserSerializationPOJO createUserPOJO= new CreateUserSerializationPOJO("Test@mail.ru", "qwerty", "Name " + getCurrentDateTime());
        UserCreateRegisteredPOJO userCreateRegisteredPOJO = RestAssured
                .given()
                    .header("Content-type", "application/json")
                    .and()
                    .body(createUserPOJO)
                .when()
                    .post("api/auth/register")
                .then()
                        .statusCode(403)
                        .extract()
                        .as(UserCreateRegisteredPOJO.class);

        assertEquals(userCreateRegisteredPOJO.isSuccess(), false); // проверяем значение в поле success
        assertEquals(userCreateRegisteredPOJO.getMessage(), "User already exists"); // проверяем значение в поле message
    }

    // создать пользователя и не заполнить одно из обязательных полей (в данном случае не заполняется поле email);
    @Test
    public void createUserNotEmail(){
        CreateUserSerializationPOJO createUserPOJO= new CreateUserSerializationPOJO("", "qwerty", "Name " + getCurrentDateTime());
        UserCreateRegisteredPOJO userCreateRegisteredPOJO = RestAssured
                .given()
                    .header("Content-type", "application/json")
                    .and()
                    .body(createUserPOJO)
                .when()
                    .post("api/auth/register")
                .then()
                    .statusCode(403)
                    .extract()
                    .as(UserCreateRegisteredPOJO.class);

        assertEquals(userCreateRegisteredPOJO.isSuccess(), false); // проверяем значение в поле success
        assertEquals(userCreateRegisteredPOJO.getMessage(), "Email, password and name are required fields"); // проверяем значение в поле message
    }

    // создать пользователя и не заполнить одно из обязательных полей (в данном случае не заполняется поле password);
    @Test
    public void createUserNotPassword(){
        CreateUserSerializationPOJO createUserPOJO= new CreateUserSerializationPOJO("Test"+getCurrentDateTime()+"@mail.ru", "", "Name " + getCurrentDateTime());
        UserCreateRegisteredPOJO userCreateRegisteredPOJO = RestAssured
                .given()
                    .header("Content-type", "application/json")
                    .and()
                    .body(createUserPOJO)
                .when()
                    .post("api/auth/register")
                .then()
                    .statusCode(403)
                    .extract()
                    .as(UserCreateRegisteredPOJO.class);

        assertEquals(userCreateRegisteredPOJO.isSuccess(), false); // проверяем значение в поле success
        assertEquals(userCreateRegisteredPOJO.getMessage(), "Email, password and name are required fields"); // проверяем значение в поле message
    }

    // создать пользователя и не заполнить одно из обязательных полей (в данном случае не заполняется поле name);
    @Test
    public void createUserNotName(){
        CreateUserSerializationPOJO createUserPOJO= new CreateUserSerializationPOJO("Test"+getCurrentDateTime()+"@mail.ru", "qwerty", "");
        UserCreateRegisteredPOJO userCreateRegisteredPOJO = RestAssured
                .given()
                    .header("Content-type", "application/json")
                    .and()
                    .body(createUserPOJO)
                .when()
                    .post("api/auth/register")
                .then()
                    .statusCode(403)
                    .extract()
                    .as(UserCreateRegisteredPOJO.class);

        assertEquals(userCreateRegisteredPOJO.isSuccess(), false); // проверяем значение в поле success
        assertEquals(userCreateRegisteredPOJO.getMessage(), "Email, password and name are required fields"); // проверяем значение в поле message
    }

}
