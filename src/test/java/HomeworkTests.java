import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;


public class HomeworkTests {

    @Test
    public void testHelloForEx3() {
        System.out.println("Hello from Valeria");
    }

    @Test
    public void testForEx4() {
        Response response = RestAssured
                .get("https://playground.learnqa.ru/api/get_text")
                .andReturn();
        response.prettyPrint();
    }

    @Test
    public void testJsonParsingForEx5() {

        JsonPath response = RestAssured
                .given()
                .get("https://playground.learnqa.ru/api/get_json_homework")
                .jsonPath();
        response.prettyPrint();

        Object messages = response.get("messages");
        ArrayList<Object> messagesList = (ArrayList<Object>) messages;
        Object secondMessage = messagesList.get(1);
        System.out.println(secondMessage.toString());
    }

    @Test
    public void testRedirectForEx6() {
        Response response = RestAssured
                .given()
                .redirects()
                .follow(false)
                .get("https://playground.learnqa.ru/api/long_redirect")
                .andReturn();

        String locationHeader = response.getHeader("Location");
        System.out.println("\n" + locationHeader);
    }

    @Test
    public void testLongRedirectForEx7() {
        String urlForRedirect = "https://playground.learnqa.ru/api/long_redirect";
        int statusCode = 0;

        while (statusCode != 200) {
            System.out.println("Url to call: " + urlForRedirect);

            Response response = RestAssured
                    .given()
                    .redirects()
                    .follow(false)
                    .get(urlForRedirect)
                    .andReturn();
            urlForRedirect = response.getHeader("Location");

            if (urlForRedirect == null) {
                urlForRedirect = response.getHeader("X-Host");
            }
            statusCode = response.getStatusCode();
        }
    }

    @Test
    public void testTokensForEx8() {

    }

    @Test
    public void testPasswordGuessingForEx9() {

    }
}
