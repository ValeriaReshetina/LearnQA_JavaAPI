package tests;

import io.qameta.allure.*;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import lib.Assertions;
import lib.BaseTestCase;
import lib.DataGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import lib.ApiCoreRequests;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static lib.Assertions.*;

@Epic("Registration cases")
public class UserRegisterTest extends BaseTestCase {

    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();

    @Test
    @Feature("Registration")
    @Severity(SeverityLevel.CRITICAL)
    public void testCreateUserWithExistingEmail() {
        String email = "vinkotov@example.com";

        Map<String, String> userData = new HashMap<>();
        userData.put("email", email);
        userData = DataGenerator.getRegistrationData(userData);

        Response responseCreateAuth = RestAssured
                .given()
                .body(userData)
                .post("https://playground.learnqa.ru/api/user/")
                .andReturn();

        assertResponseCodeEquals(responseCreateAuth, 400);
        assertResponseTextEquals(responseCreateAuth, "Users with email '" + email + "' already exists");
    }

    @Test
    @Feature("Registration")
    @Severity(SeverityLevel.BLOCKER)
    public void testCreateUserSuccessfully() {
        String email = DataGenerator.getRandomEmail();

        Map<String, String> userData = DataGenerator.getRegistrationData();

        Response responseCreateAuth = RestAssured
                .given()
                .body(userData)
                .post("https://playground.learnqa.ru/api/user/")
                .andReturn();

        assertResponseCodeEquals(responseCreateAuth, 200);
        assertJsonHasField(responseCreateAuth, "id");
    }

    @Test
    @Description("This test checks registration w/o typing '@' symbol in user email")
    @DisplayName("Test negative registration user")
    @Feature("Registration")
    @Severity(SeverityLevel.MINOR)
    public void testCreateUserWithIncorrectEmailForEx15_1() {
        String email = "vinkotovexample.com";
        String url = "https://playground.learnqa.ru/api/user";

        Map<String, String> userData = new HashMap<>();
        userData.put("email", email);
        userData = DataGenerator.getRegistrationData(userData);
        Response response = apiCoreRequests.makePostRequest(url, userData);

        assertResponseCodeEquals(response, 400);
        assertResponseTextEquals(response, "Invalid email format");
    }

    @Description("This test checks registration w/o sending one of necessary fields")
    @DisplayName("Test negative registration user")
    @Feature("Registration")
    @Severity(SeverityLevel.CRITICAL)
    @ParameterizedTest
    @ValueSource(strings = {"email", "password", "username", "firstName", "lastName"})
    public void testCreateUserWithoutFieldForEx15_2(String condition) {
        String url = "https://playground.learnqa.ru/api/user";
        Map<String, String> userData = new HashMap<>();
        userData = DataGenerator.getRegistrationData(userData);

        switch (condition) {
            case ("email"):
                userData.keySet().removeIf(key -> key == "email");
                break;
            case ("password"):
                userData.keySet().removeIf(key -> key == "password");
                break;
            case ("username"):
                userData.keySet().removeIf(key -> key == "username");
                break;
            case ("firstName"):
                userData.keySet().removeIf(key -> key == "firstName");
                break;
            case ("lastName"):
                userData.keySet().removeIf(key -> key == "lastName");
                break;
        }
        Response response = apiCoreRequests.makePostRequest(url, userData);

        Assertions.assertResponseCodeEquals(response, 400);
        Assertions.assertResponseTextEquals(response,
                "The following required params are missed: " + condition);
    }

    @Test
    @Description("This test checks creation of user with only one symbol in email")
    @DisplayName("Test negative registration user")
    @Feature("Registration")
    @Severity(SeverityLevel.NORMAL)
    public void testCreateUserWithOneSymbolInEmailForEx15_3() {
        String email = "a";
        String url = "https://playground.learnqa.ru/api/user";

        Map<String, String> userData = new HashMap<>();
        userData.put("email", email);
        userData = DataGenerator.getRegistrationData(userData);
        Response response = apiCoreRequests.makePostRequest(url, userData);

        Assertions.assertResponseCodeEquals(response, 400);
        Assertions.assertResponseTextEquals(response, "The value of 'email' field is too short");
    }

    @Test
    @Description("This test checks creation of user with a very long username, longer than 250 symbols")
    @DisplayName("Test negative registration user")
    @Feature("Registration")
    @Severity(SeverityLevel.NORMAL)
    public void testCreateUserWithLongUsernameForEx15_4() {
        String email = "Сложно представить аналогию абстрактного класса в реальной жизни. " +
                "Обычно класс является моделью какой-нибудь сущности. " +
                "Но абстрактный класс содержит не только реализованные методы, но и не реализованные. " +
                "Что же это значит? Аналогом чего является абстрактный класс и есть ли у него аналоги в реальном мире?";
        String url = "https://playground.learnqa.ru/api/user";

        Map<String, String> userData = new HashMap<>();
        userData.put("email", email);
        userData = DataGenerator.getRegistrationData(userData);

        Response response = apiCoreRequests.makePostRequest(url, userData);

        Assertions.assertResponseCodeEquals(response, 400);
        Assertions.assertResponseTextEquals(response, "The value of 'email' field is too long");
    }

    @Test
    @Description("This test login with one user, but receives data from another")
    @DisplayName("Test negative registration user")
    @Feature("Registration")
    @Severity(SeverityLevel.CRITICAL)
    public void testRequestOfAnotherUserDataForEx16() {
        Map<String, String> authData = new HashMap<>();
        authData.put("email", "vinkotov@example.com");
        authData.put("password", "1234");
        String id = "1";

        Response responseAuth = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user/login", authData);

        assertResponseCodeEquals(responseAuth, 200);
        assertJsonByName(responseAuth, "user_id", 2);

        Response responseUserData = apiCoreRequests
                .makeGetRequestWithoutTokenAndCookie("https://playground.learnqa.ru/api/user/" + id);

        assertResponseCodeEquals(responseAuth, 200);
        assertJsonHasField(responseUserData, "username");
        assertJsonHasNoField(responseUserData, "firstname");
        assertJsonHasNoField(responseUserData, "lastname");
        assertJsonHasNoField(responseUserData, "email");
    }
}
