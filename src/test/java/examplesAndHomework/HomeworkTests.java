package examplesAndHomework;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasLength;
import static org.junit.jupiter.api.Assertions.*;


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
            System.out.println("Url for redirect: " + urlForRedirect);

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
    public void testTokensForEx8() throws InterruptedException {
        Response response = RestAssured
                .given()
                .get("https://playground.learnqa.ru/ajax/api/longtime_job")
                .andReturn();
        String token = response.jsonPath().getString("token");

        response = RestAssured
                .given()
                .param("token", token)
                .get("https://playground.learnqa.ru/ajax/api/longtime_job")
                .andReturn();
        String status = response.jsonPath().getString("status");
        assertEquals("Job is NOT ready", status);
        Thread.sleep(18000);

        response = RestAssured
                .given()
                .param("token", token)
                .get("https://playground.learnqa.ru/ajax/api/longtime_job")
                .andReturn();
        status = response.jsonPath().getString("status");
        String result = response.jsonPath().getString("result");

        assertEquals("Job is ready", status);
        assertNotNull(result);
    }

    @Test
    public void testPasswordGuessingForEx9() {
        String mostCommonPasswords = "\tpassword\tpassword\t123456\t123456\t123456\t123456\t123456\t123456\t123456\n" +
                "\t123456\t123456\tpassword\tpassword\tpassword\tpassword\tpassword\tpassword\t123456789\n" +
                "\t12345678\t12345678\t12345678\t12345\t12345678\t12345\t12345678\t123456789\tqwerty\n" +
                "\tqwerty\tabc123\tqwerty\t12345678\tqwerty\t12345678\tqwerty\t12345678\tpassword\n" +
                "\tabc123\tqwerty\tabc123\tqwerty\t12345\tfootball\t12345\t12345\t1234567\n" +
                "\tmonkey\tmonkey\t123456789\t123456789\t123456789\tqwerty\t123456789\t111111\t12345678\n" +
                "\t1234567\tletmein\t111111\t1234\tfootball\t1234567890\tletmein\t1234567\t12345\n" +
                "\tletmein\tdragon\t1234567\tbaseball\t1234\t1234567\t1234567\tsunshine\tiloveyou\n" +
                "\ttrustno1\t111111\tiloveyou\tdragon\t1234567\tprincess\tfootball\tqwerty\t111111\n" +
                "\tdragon\tbaseball\tadobe123[a]\tfootball\tbaseball\t1234\tiloveyou\tiloveyou\t123123\n" +
                "\tbaseball\tiloveyou\t123123\t1234567\twelcome\tlogin\tadmin\tprincess\tabc123\n" +
                "\t111111\ttrustno1\tadmin\tmonkey\t1234567890\twelcome\twelcome\tadmin\tqwerty123\n" +
                "\tiloveyou\t1234567\t1234567890\tletmein\tabc123\tsolo\tmonkey\twelcome\t1q2w3e4r\n" +
                "\tmaster\tsunshine\tletmein\tabc123\t111111\tabc123\tlogin\t666666\tadmin\n" +
                "\tsunshine\tmaster\tphotoshop[a]\t111111\t1qaz2wsx\tadmin\tabc123\tabc123\tqwertyuiop\n" +
                "\tashley\t123123\t1234\tmustang\tdragon\t121212\tstarwars\tfootball\t654321\n" +
                "\tbailey\twelcome\tmonkey\taccess\tmaster\tflower\t123123\t123123\t555555\n" +
                "\tpassw0rd\tshadow\tshadow\tshadow\tmonkey\tpassw0rd\tdragon\tmonkey\tlovely\n" +
                "\tshadow\tashley\tsunshine\tmaster\tletmein\tdragon\tpassw0rd\t654321\t7777777\n" +
                "\t123123\tfootball\t12345\tmichael\tlogin\tsunshine\tmaster\t!@#$%^&*\twelcome\n" +
                "\t654321\tjesus\tpassword1\tsuperman\tprincess\tmaster\thello\tcharlie\t888888\n" +
                "\tsuperman\tmichael\tprincess\t696969\tqwertyuiop\thottie\tfreedom\taa123456\tprincess\n" +
                "\tqazwsx\tninja\tazerty\t123123\tsolo\tloveme\twhatever\tdonald\tdragon\n" +
                "\tmichael\tmustang\ttrustno1\tbatman\tpassw0rd\tzaq1zaq1\tqazwsx\tpassword1\tpassword1\n" +
                "\tFootball\tpassword1\t000000\ttrustno1\tstarwars\tpassword1\ttrustno1\tqwerty123\t123qwe\n";

        mostCommonPasswords = mostCommonPasswords.replaceAll("\n", "");
        String[] words = mostCommonPasswords.split("\t");

        Set<String> mostCommonPasswordsSet = new HashSet<>();
        mostCommonPasswordsSet.addAll(Arrays.asList(words));

        String realPassword = null;

        for (String iteratedPassword : mostCommonPasswordsSet) {
            Response response = RestAssured
                    .given()
                    .param("login", "super_admin")
                    .param("password", iteratedPassword)
                    .post("https://playground.learnqa.ru/ajax/api/get_secret_password_homework")
                    .andReturn();
            if (response.getStatusCode() == 500) {
                continue;
            }
            Map<String, String> cookies = response.getCookies();

            response = RestAssured
                    .given()
                    .cookies(cookies)
                    .get("https://playground.learnqa.ru/ajax/api/check_auth_cookie")
                    .andReturn();
            if (response.asString().contains("You are authorized")) {
                realPassword = iteratedPassword;
                break;
            }
        }
        assertNotNull(realPassword);
        System.out.println("Real password is: " + realPassword);
    }

//        for (String password : mostCommonPasswordsSet) {
//            Response response = RestAssured
//                    .given()
//                    .param("login", "super_admin")
//                    .param("password", password)
//                    .post("https://playground.learnqa.ru/ajax/api/get_secret_password_homework");
//            System.out.println(password);
//            System.out.println(response.asString() + "\n");
//
//            JsonPath jsonPath = response.jsonPath();
//            Object objectToCompare = jsonPath.get("equals");
//
//            if ((Boolean) objectToCompare == true) {
//                System.out.println("This password is correct: " + password);
//                break;
//            }

    @Test
    public void testCheckingLengthOfStringForEx10() {
        String myString = "Test for checking length of string";
        assertThat(myString, hasLength(34));
    }
}
