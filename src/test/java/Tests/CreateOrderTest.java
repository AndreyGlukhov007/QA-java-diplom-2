package Tests;

import POJO.CreateOrder.CreateOrderDeserializationPOJO;
import POJO.CreateUser.CreateUserDeserializationPOJO.CreateUserDeserializationPOJO;
import POJO.CreateUser.CreateUserDeserializationPOJO.UserCreateRegisteredPOJO;
import POJO.CreateUser.CreateUserSerializationPOJO.CreateUserSerializationPOJO;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CreateOrderTest extends MainTest{

    /*
    Обращаю внимание на строчки 21 и 30. Похожие строчки есть и в других классах где нужно создавать пользователя.
    Однако почему-то именно в этом классе метод CreateOrderWithAuthorization стабильно падает с ошибкой. Почему так происходит я так и не разобрался.
    Поэтому с 36 ро 48 строчки я создаю пользователя прямо в тесте, но зато тест стабильно отрабатывает.
     */

    //private CreateUserDeserializationPOJO responseСreateUserDeserializationPOJO;
    private String emailTest = getEmailTest();
    private String nameTest = getNameTest();

    @BeforeEach
    public void setUp(){
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site/";

        // Создаём пользователя
        //responseСreateUserDeserializationPOJO = createUser(emailTest,"qwerty", nameTest);
    }

    //Создание заказа с авторизации
    @Test
    public void CreateOrderWithAuthorization(){
        // Cоздаём пользователя
        CreateUserSerializationPOJO createUserPOJO= new CreateUserSerializationPOJO(emailTest,"qwerty", nameTest);
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

        // Получение данных об ингредиентах
        CreateOrderDeserializationPOJO сreateOrderDeserializationPOJO =  RestAssured
                .given()
                .headers("Content-type", "application/json")
                .body("")
                .when()
                .get("api/ingredients")
                .as(CreateOrderDeserializationPOJO.class);

        String Ingredient1 = сreateOrderDeserializationPOJO.getData().get(0).get_id();
        String Ingredient2 = сreateOrderDeserializationPOJO.getData().get(1).get_id();

        // Создание заказа
        Response response = RestAssured
                .given()
                .headers("Content-type", "application/json")
                .auth().oauth2(jwt)
                .body("{\"ingredients\": [\"" + Ingredient1 + "\",\"" + Ingredient2 + "\"]}")
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