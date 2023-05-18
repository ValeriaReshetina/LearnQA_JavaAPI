package tests;

import io.qameta.allure.Description;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import lib.ApiCoreRequests;
import lib.Assertions;
import lib.BaseTestCase;
import lib.DataGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static lib.Assertions.assertJsonByName;

public class UserEditTest extends BaseTestCase {

    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();

    @Test
    public void testEditJustCreatedTest() {
        //GENERATE USER
        Map<String,String> userData = DataGenerator.getRegistrationData();

        JsonPath responseCreateAuth = RestAssured
                .given()
                .body(userData)
                .post("https://playground.learnqa.ru/api/user/")
                .jsonPath();

        String userId = responseCreateAuth.getString("id");

        //LOGIN
        Map<String,String> authData = new HashMap<>();
        authData.put("email", userData.get("email"));
        authData.put("password", userData.get("password"));

        Response responseGetAuth = RestAssured
                .given()
                .body(authData)
                .post("https://playground.learnqa.ru/api/user/login")
                .andReturn();

        //EDIT
        String newName = "Changed Name";
        Map<String,String> editData = new HashMap<>();
        editData.put("firstName", newName);

        Response responseEditUser = RestAssured
                .given()
                .header("x-csrf-token", this.getHeader(responseGetAuth, "x-csrf-token"))
                .cookie("auth_sid", this.getCookie(responseGetAuth, "auth_sid"))
                .body(editData)
                .put("https://playground.learnqa.ru/api/user/" + userId)
                .andReturn();

        //GET
        Response responseUserData = RestAssured
                .given()
                .header("x-csrf-token", this.getHeader(responseGetAuth, "x-csrf-token"))
                .cookie("auth_sid", this.getCookie(responseGetAuth, "auth_sid"))
                .get("https://playground.learnqa.ru/api/user/" + userId)
                .andReturn();

        assertJsonByName(responseUserData, "firstName", newName);
    }

    @Description("This test is trying to change user data, being unauthorized")
    @DisplayName("Negative tests for PUT")
    @Test
    public void testRequestOfAnotherUserDataForEx17_1() {
        Map<String, String> userData = DataGenerator.getRegistrationData();
        String urlCreate = "https://playground.learnqa.ru/api/user";

        JsonPath responseCreateUser =
                apiCoreRequests.makePostRequestToCreateUser(urlCreate, userData);
        String userId = responseCreateUser.getString("id");
        String newName = "Changed_name";
        Map<String, String> body = new HashMap<>();
        body.put("firstName", newName);

        String urlUser = "https://playground.learnqa.ru/api/user/" + userId;
        Response responseEdit = apiCoreRequests.makePutRequestToEditUser(urlUser, body);

        Map<String, String> authData = new HashMap<>();
        authData.put("email", userData.get("email"));
        authData.put("password", userData.get("password"));

        String urlLogin = "https://playground.learnqa.ru/api/user/login";
        Response responseGetAuth = apiCoreRequests.makePostRequest(urlLogin, authData);

        String cookie = getCookie(responseGetAuth, "auth_sid");
        String header = getHeader(responseGetAuth, "x-csrf-token");
        Response responseUserData = apiCoreRequests.makeGetRequest(urlUser, header, cookie);

        assertJsonByName(responseUserData, "firstName", "learnqa");
    }

    @Description("This test is trying to change the user's data while being authorized by another user")
    @DisplayName("Negative tests for PUT")
    @Test
    public void testRequestOfAnotherUserDataForEx17_2() {
        String urlCreate = "https://playground.learnqa.ru/api/user";

        Map<String, String> firstUserData = DataGenerator.getRegistrationData();
        JsonPath responseCreateUserFirst = apiCoreRequests.makePostRequestToCreateUser(urlCreate, firstUserData);
        String firstUserId = responseCreateUserFirst.getString("id");

        Map<String, String> secondUserData = DataGenerator.getRegistrationData();
        JsonPath responseCreateUserSecond = apiCoreRequests.makePostRequestToCreateUser(urlCreate, secondUserData);
        String secondUserId = responseCreateUserSecond.getString("id");

        Map<String, String> authDataSecond = new HashMap<>();
        authDataSecond.put("email", secondUserData.get("email"));
        authDataSecond.put("password", secondUserData.get("password"));

        String urlLogin = "https://playground.learnqa.ru/api/user/login";
        Response responseGetAuthSecond = apiCoreRequests.makePostRequest(urlLogin, authDataSecond);

        String newName = "Changed_name";
        Map<String, String> body = new HashMap<>();
        body.put("firstName", newName);

        String urlUser = "https://playground.learnqa.ru/api/user/" + firstUserId;
        Response responseEdit = apiCoreRequests.makePutRequestToEditUser(urlUser, body);

        Map<String, String> authDataFirst = new HashMap<>();
        authDataFirst.put("email", firstUserData.get("email"));
        authDataFirst.put("password", firstUserData.get("password"));
        Response responseGetAuthFirst = apiCoreRequests.makePostRequest(urlLogin, authDataFirst);

        String firstCookie = getCookie(responseGetAuthFirst, "auth_sid");
        String firstHeader = getHeader(responseGetAuthFirst, "x-csrf-token");
        Response responseUserData = apiCoreRequests.makeGetRequest(urlUser, firstHeader, firstCookie);

        System.out.println(responseUserData.asString());
        assertJsonByName(responseUserData, "firstName", "learnqa");
    }

    @Description("This test is trying to change the user's email, " +
            "being authorized by the same user, to a new email without the symbol'@'")
    @DisplayName("Negative tests for PUT")
    @Test
    public void testEditWrongEmailInJustCreatedTestForEx17_3(){
        Map<String, String> userData = DataGenerator.getRegistrationData();
        String urlCreate = "https://playground.learnqa.ru/api/user";

        JsonPath responseCreateUser = apiCoreRequests.makePostRequestToCreateUser(urlCreate, userData);
        String userId = responseCreateUser.getString("id");

        Map<String, String> authData = new HashMap<>();
        authData.put("email", userData.get("email"));
        authData.put("password", userData.get("password"));

        String urlLogin = "https://playground.learnqa.ru/api/user/login";
        Response responseGetAuth = apiCoreRequests.makePostRequest(urlLogin, authData);

        String cookie = getCookie(responseGetAuth, "auth_sid");
        String header = getHeader(responseGetAuth, "x-csrf-token");
        String newEmail = "Changed_name.ru";
        Map<String, String> body = new HashMap<>();
        body.put("email", newEmail);

        String urlUser = "https://playground.learnqa.ru/api/user/" + userId;
        Response responseEdit = apiCoreRequests.makePutRequestToEditUser(urlUser, body);
        Response responseUserData = apiCoreRequests.makeGetRequest(urlUser, header, cookie);

        assertJsonByName(responseUserData, "email", userData.get("email"));
    }

    @Description("This test is trying to change first name of the user, being authorized by the same user, " +
            "to a very short value of one symbol")
    @DisplayName("Negative tests for PUT")
    @Test
    public void testEditOfUserFirstNameToOneSymbolForEx17_4(){
        Map<String, String> userData = DataGenerator.getRegistrationData();
        String urlCreate = "https://playground.learnqa.ru/api/user";

        JsonPath responseCreateUser = apiCoreRequests.makePostRequestToCreateUser(urlCreate, userData);
        String userId = responseCreateUser.getString("id");

        Map<String, String> authData = new HashMap<>();
        authData.put("email", userData.get("email"));
        authData.put("password", userData.get("password"));
        String urlLogin = "https://playground.learnqa.ru/api/user/login";
        Response responseGetAuth = apiCoreRequests.makePostRequest(urlLogin, authData);

        String cookie = getCookie(responseGetAuth, "auth_sid");
        String header = getHeader(responseGetAuth, "x-csrf-token");
        String newName = "a";
        Map<String, String> body = new HashMap<>();
        body.put("firstName", newName);

        String urlUser = "https://playground.learnqa.ru/api/user/" + userId;
        Response responseEdit = apiCoreRequests.makePutRequestToEditUser(urlUser, body);
        Response responseUserData = apiCoreRequests.makeGetRequest(urlUser, header, cookie);

        assertJsonByName(responseUserData, "firstName", userData.get("firstName"));
    }
}
