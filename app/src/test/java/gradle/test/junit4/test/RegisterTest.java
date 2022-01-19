package gradle.test.junit4.test;

import gradle.test.junit4.domain.User;
import io.restassured.RestAssured;
import io.restassured.builder.ResponseSpecBuilder;
import org.apache.http.HttpStatus;
import org.junit.BeforeClass;
import org.junit.Test;


import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

public class RegisterTest extends BaseTest {

    private static final String REGISTER_NEW_USER_ENDPOINT = "/register";
    private static final String LOGIN_USER_ENDPOINT = "/login";

//como a classe estende da baseTest, acaba pegando os dois responseSpecBuilder, o segundo teste o status code é 400
    @BeforeClass
    public static void setupRegister() {
        RestAssured.responseSpecification = new ResponseSpecBuilder().
                expectStatusCode(HttpStatus.SC_OK).  //expect status code, forçando erro
                build();

    }

    @Test
    public void testUnableToRegisterWhenPasswordMissing() {
        User user = new User();
        user.setEmail("eve.holt@reqres.in");

        given().
            body(user).
        when().
            post(REGISTER_NEW_USER_ENDPOINT).
        then().
        log().all().
            //statusCode(HttpStatus.SC_OK). // Didático, não tem necessidade por causa do response SpecBuilder
            body("error", is("Missing password"));
    }

    //Esse teste deveria estar no testeLogin, mas está aqui para mostrar funcionalidade de múltiplos setups e ResponseSetup
    @Test
    public void testUnableToLogin() {
        User user = new User();
        user.setEmail("eve.holt@reqres.in");

        given().
            body(user).
        when().
            post(LOGIN_USER_ENDPOINT).
        then().
                log().all().
            body("error", is("Missing password"));

    }
}
