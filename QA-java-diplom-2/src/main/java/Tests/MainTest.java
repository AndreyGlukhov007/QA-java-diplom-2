package Tests;

import POJO.CreateUser.CreateUserDeserializationPOJO.CreateUserDeserializationPOJO;
import POJO.CreateUser.CreateUserSerializationPOJO.CreateUserSerializationPOJO;
import io.restassured.RestAssured;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MainTest {

    private String emailTest = "test"+getCurrentDateTime()+"@mail.ru";
    private String nameTest = "name " + getCurrentDateTime();

    //Этот метод нужен чтобы создавать уникальных пользователей. К логину и имени добавляется актуальная дата и время (часы, минуты, секунды).
    public String getCurrentDateTime() {
        // Получаем текущие дату и время
        LocalDateTime now = LocalDateTime.now();

        // Форматируем вывод
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH:mm:ss");

        // Возвращаем отформатированную строку
        return now.format(formatter);
    }

    // Этот метод нужен для создания пользователя
    public CreateUserDeserializationPOJO createUser(String emailTest, String password,String nameTest){
        // Cоздаём пользователя
        CreateUserSerializationPOJO createUserPOJO= new CreateUserSerializationPOJO(emailTest,password, nameTest);
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

        return responseСreateUserDeserializationPOJO;
    }

    public String getEmailTest() {
        return emailTest;
    }

    public String getNameTest() {
        return nameTest;
    }

}
