package Tests;

import POJO.CreateUser.CreateUserDeserializationPOJO.CreateUserDeserializationPOJO;
import POJO.CreateUser.CreateUserDeserializationPOJO.UserCreateRegisteredPOJO;
import POJO.CreateUser.CreateUserSerializationPOJO.CreateUserSerializationPOJO;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CreateOrderTest {

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
    public void setUp(){RestAssured.baseURI = "https://stellarburgers.nomoreparties.site/";}

    //Создание заказа с авторизации
    @Test
    public void CreateOrderWithAuthorization(){
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

        // Тут я из accessToken вырезаю "Bearer " вместе с пробелом, оставляю только токен для авторизации.
        String token = responseСreateUserDeserializationPOJO.getAccessToken();
        String jwt = token.replaceFirst("^Bearer ", "");

        Response response = RestAssured
                .given()
                    .headers("Content-type", "application/json")
                    .auth().oauth2(jwt)
                    .body("{\"ingredients\": [\"61c0c5a71d1f82001bdaaa6d\",\"61c0c5a71d1f82001bdaaa6f\"]}")
                .when()
                 .post("api/orders");

        assertEquals(response.statusCode(), 200);
    }

    // Создание заказа без авторизации
    @Test
    public void CreateOrderNotAuthorization(){
        Response response = RestAssured
                .given()
                    .headers("Content-type", "application/json")
                    .and()
                    .body("{\"ingredients\": [\"61c0c5a71d1f82001bdaaa6d\",\"61c0c5a71d1f82001bdaaa6f\"]}")
                .when()
                    .post("api/orders");

        assertEquals(response.statusCode(), 200);
    }

    // Создание заказа без ингредиентов.
    @Test
    public void CreateOrderNotIngredients(){
        UserCreateRegisteredPOJO responseUserCreateRegisteredPOJO = RestAssured
                .given()
                    .headers("Content-type", "application/json")
                    .and()
                    .body("{\"ingredients\": []}")
                .when()
                    .post("api/orders")
                .then()
                    .statusCode(400)
                    .extract()
                    .as(UserCreateRegisteredPOJO.class);

       assertEquals(responseUserCreateRegisteredPOJO.isSuccess(), false);
       assertEquals(responseUserCreateRegisteredPOJO.getMessage(), "Ingredient ids must be provided");
    }

    // Создание заказа с неверным хешем ингредиентов
    @Test
    public void CreateOrderIncorrectHash(){
        UserCreateRegisteredPOJO responseUserCreateRegisteredPOJO = RestAssured
                .given()
                    .headers("Content-type", "application/json")
                    .and()
                    .body("{\"ingredients\": [\"61c0c5a71d1f82001bda1234\",\"61c0c5a71d1f82001bda1234\"]}")
                .when()
                    .post("api/orders")
                .then()
                    .statusCode(400)
                    .extract()
                    .as(UserCreateRegisteredPOJO.class);

        assertEquals(responseUserCreateRegisteredPOJO.isSuccess(), false);
        assertEquals(responseUserCreateRegisteredPOJO.getMessage(), "One or more ids provided are incorrect");
    }

}
