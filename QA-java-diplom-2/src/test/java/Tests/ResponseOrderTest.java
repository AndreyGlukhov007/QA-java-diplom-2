package Tests;

import POJO.CreateUser.CreateUserDeserializationPOJO.CreateUserDeserializationPOJO;
import POJO.CreateUser.CreateUserDeserializationPOJO.UserCreateRegisteredPOJO;
import POJO.CreateUser.CreateUserSerializationPOJO.CreateUserSerializationPOJO;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ResponseOrderTest extends MainTest{

    private CreateUserDeserializationPOJO responseСreateUserDeserializationPOJO;
    private String emailTest = getEmailTest();
    private String nameTest = getNameTest();

    @BeforeEach
    public void setUp(){
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site/";

        // Создаём пользователя
        responseСreateUserDeserializationPOJO = createUser(emailTest,"qwerty", nameTest);
    }

    // Получение заказов конкретного пользователя; авторизованный пользователь.
    @Test
    public void requestAuthorization(){
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


        Response responseOrder = RestAssured
                .given()
                    .headers("Content-type", "application/json")
                    .auth().oauth2(jwt)
                    .body("{\"ingredients\": [\"61c0c5a71d1f82001bdaaa6d\",\"61c0c5a71d1f82001bdaaa6f\"]}")
                .when()
                    .get("api/orders");

        assertEquals(responseOrder.getStatusCode(), 200);
    }

    // Получение заказов конкретного пользователя; неавторизованный пользователь.
    @Test
    public void requestNotAuthorization(){
        UserCreateRegisteredPOJO responseOrder = RestAssured
                .given()
                    .headers("Content-type", "application/json")
                    .body("{\"ingredients\": [\"61c0c5a71d1f82001bdaaa6d\",\"61c0c5a71d1f82001bdaaa6f\"]}")
                .when()
                    .get("api/orders")
                .then()
                    .statusCode(401)
                    .extract()
                    .as(UserCreateRegisteredPOJO.class);

        assertEquals(responseOrder.isSuccess(), false);
        assertEquals(responseOrder.getMessage(), "You should be authorised");
    }

}
