package gradle.test.junit4.test;

import gradle.test.junit4.domain.User;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.apache.http.HttpStatus;
import org.junit.BeforeClass;
import org.junit.Test;


import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

public class AlternativeUserTest {

    private static final String BASE_URL = "https://reqres.in";
    private static final String BASE_PATH = "/api";

    private static final String LIST_USERS_ENDPOINT = "/users";
    private static final String CREATE_USER_ENDPOINT = "/user";
    private static final String SHOW_USER_ENDPOINT = "/users/{userId}";

    @BeforeClass//Loga os erros
    public static void setup() {

        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @Test
    public void testSpecificPageIsDisplayed() {
        String uri = getUri(LIST_USERS_ENDPOINT);

        given().
            param("page","2").
        when().
            get(uri).
        then().
            contentType(ContentType.JSON).
            statusCode(HttpStatus.SC_OK).
            body("page", is(2)).
            body("data", is(notNullValue()));
    }


    @Test
    public void testAbleToCreateNewUser() {
        String uri = getUri(CREATE_USER_ENDPOINT);

        Map<String, String> user = new HashMap<>();
        user.put("name", "rafael");
        user.put("job", "eng test");

        given().
            contentType(ContentType.JSON).
            body(user).
        when().
            post(uri).
        then().
                log().all().
            contentType(ContentType.JSON).
            statusCode(HttpStatus.SC_CREATED).
            body("name", is("rafael1"));//forçando erro para visualizar relatório
    }

    @Test
    public void testSizeOfItemsDisplayedAreTheSameAsPerPage() {
        String uri = getUri(LIST_USERS_ENDPOINT);

        int expectedPage = 2;

        int expectedItemsPerPage = getExpectedItemsPerPage(expectedPage);

        given().
            param("page",expectedPage).
        when().
            get(uri).
        then().log().all().
            statusCode(HttpStatus.SC_OK).
            contentType(ContentType.JSON).
            body(
                "page", is(expectedPage),
                "data.size()", is(expectedItemsPerPage ),
                "data.findAll { it.avatar.startsWith('https://s3.amazonaws.com') }.size()", is(expectedItemsPerPage)
            );
    }

    @Test
    public void testShowSpecificUser() {
        String uri = getUri(SHOW_USER_ENDPOINT);

        User user = given().
            pathParam("userId", 2).
        when().
            get(uri).
        then().
            contentType(ContentType.JSON).
            statusCode(HttpStatus.SC_OK).
        extract().
            body().jsonPath().getObject("data", User.class);


        assertThat(user.getEmail(), containsString("@reqres.in"));
        assertThat(user.getName(), containsString("Janet"));
        assertThat(user.getLastName(), containsString("Weaver"));
    }

    private int getExpectedItemsPerPage(int page) {
        String uri = getUri(LIST_USERS_ENDPOINT);

        return given().
                    param("page", page).
                when().
                    get(uri).
                then().
                    contentType(ContentType.JSON).
                    statusCode(HttpStatus.SC_OK).
                extract().
                    path("per_page");
    }

    private String getUri(String endpoint) {

        return BASE_URL + BASE_PATH + endpoint;
    }
}
