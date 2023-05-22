package tests;

import io.qameta.allure.Description;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import lib.ApiCoreRequests;
import lib.BaseTestCase;
import lib.DataGenerator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static lib.Assertions.assertResponseCodeEquals;
import static lib.Assertions.assertResponseTextEquals;

public class UserDeleteTest extends BaseTestCase {

    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();

    @Description("This test is trying to DELETE user by ID 2")
    @DisplayName("Negative test for DELETE")
    @Test
    public void testDeleteUserById2ForEx18_1() {
        Map<String, String> userData = new HashMap<>();
        userData.put("email", "vinkotov@example.com");
        userData.put("password", "1234");

        Response makeLogin = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user/login", userData);
        String cookie = makeLogin.getCookie("auth_sid");
        String token = makeLogin.getHeader("x-csrf-token");

        Response response = apiCoreRequests
                .makeRequestToDeleteUser("https://playground.learnqa.ru/api/user/2", userData, token, cookie);

        assertResponseCodeEquals(response, 400);
        assertResponseTextEquals(response, "Please, do not delete test users with ID 1, 2, 3, 4 or 5.");
    }

    @Description("This test is trying to create a user, login, DELETE, " +
            "then try to get his data by ID and make sure that the user is really deleted")
    @DisplayName("Positive test for DELETE")
    @Test
    public void testDeleteJustCreatedUserForEx18_2(){
        Map <String, String> userData = DataGenerator.getRegistrationData();
        Response responseCreateAuth = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user/", userData);
        String userId = responseCreateAuth.jsonPath().getString("id");

        Map <String, String> authData = new HashMap<>();
        authData.put("email", userData.get("email"));
        authData.put("password", userData.get("password"));

        Response responseGetAuth = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user/login", authData);

        String cookie = responseGetAuth.getCookie("auth_sid");
        String token = responseGetAuth.getHeader("x-csrf-token");
        String url = "https://playground.learnqa.ru/api/user/" + userId;

        Response response = apiCoreRequests
                .makeRequestToDeleteUser(url, userData, token, cookie);
        assertResponseCodeEquals(response, 200);

        Response responseAfterDeletion = apiCoreRequests
                .makeGetRequest(url, token, cookie);

        assertResponseCodeEquals(responseAfterDeletion, 404);
        assertResponseTextEquals(responseAfterDeletion, "User not found");
    }

    @Description("This test is trying to DELETE user while being logged in by another user")
    @DisplayName("Negative test for DELETE")
    @Test
    public void testDeleteUserBeingLoggedInByAnotherUserForEx18_3() {
        Map<String, String> userData = new HashMap<>();
        userData.put("email", "flyingscarlett@yandex.ru");
        userData.put("password", "1234");

        Response makeLogin = apiCoreRequests.makePostRequest("https://playground.learnqa.ru/api/user/login", userData);
        String cookie = makeLogin.getCookie("auth_sid");
        String token = makeLogin.getHeader("x-csrf-token");
        String url = "https://playground.learnqa.ru/api/user/" + 15;

        Response response = apiCoreRequests
                .makeRequestToDeleteUser(url, userData, token, cookie);

        assertResponseCodeEquals(response, 400);
        System.out.println(response.asString());
    }
}
