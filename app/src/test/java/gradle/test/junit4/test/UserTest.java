package gradle.test.junit4.test;

import gradle.test.junit4.domain.User;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.junit.Test;


import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasKey;

public class UserTest extends BaseTest {

    private static final String LIST_USERS_ENDPOINT = "/users";
    private static final String CREATE_USER_ENDPOINT = "/user";
    private static final String SHOW_USER_ENDPOINT = "/users/{userId}";

    @Test
    public void testSpecificPageIsDisplayed() {
        given().
            param("page","2").
        when().
            get(LIST_USERS_ENDPOINT).
        then().
            statusCode(HttpStatus.SC_OK).
            body("page", is(2)).
            body("data", is(notNullValue()));
    }

    @Test //Neste teste api rejeita atributos desconhecidos, mas supondo que não aceitasse, teríamos que montar um Hashmap, passando body certo
    public void testAbleToCreateNewUser() {
        Map<String, String> user = new HashMap<>();  //Instanciando objeto user e serializando para json
        user.put("nombre", "rafael");
        user.put("trabajo", "eng test");

        given().
            body(user).
        when().
            post(CREATE_USER_ENDPOINT).
        then().
                log().all().
            statusCode(HttpStatus.SC_CREATED).
            body("nombre", is("rafael"));
    }

    @Test//Utilizando json schema estático
    public  void testeCreateUserSchemaStatic(){
        File bodyUser = new File("/Users/ricardoveiga/Documents/zoop-projects/zoop-barcode-services-test/app/src/test/resources/payload/bodyUser.json");
        Response response =
                given().
                log().all().
                body(bodyUser).
                when().
                post(CREATE_USER_ENDPOINT);
        response.prettyPrint();//Imprime o request
        response.
                then().
                //log().all().
                statusCode(HttpStatus.SC_CREATED).
                //Validação funcional
                body("nombre", is("Ricardo")).
                body("trabajo", is("QA Engine")).
                //Outra forma de validar contrato
                body("$", hasKey("nombre")).
                body("$", hasKey("trabajo")).
                body("$", hasKey("id")).
                body("$", hasKey("createdAt"))
        ;
        String idOld = response.jsonPath().getString("id");
        System.out.print("idOld => " + idOld);
        String idNew = response.path("id");
        System.out.println("idNew => " + idNew);

    }

    @Test
    public void testSizeOfItemsDisplayedAreTheSameAsPerPage() {
        int expectedPage = 2;

        //Foi transofrmado em um método
        int expectedItemsPerPage = getExpectedItemsPerPage(expectedPage);

        //Selecione tudo>Botao direito> Refector > Extract Method  ctrl + alt+ M
//        int perPage = given().
//                    param("page", 2).
//                when().
//                    get(LIST_USERS_ENDPOINT).
//                then().log().all().
//                    statusCode(HttpStatus.SC_OK).
//                extract().
//                    path("per_page");

        given().
        log().all().
            param("page",expectedPage).
        when().
            get(LIST_USERS_ENDPOINT).
        then().log().all().
            statusCode(HttpStatus.SC_OK).
            body(  //USANDO GROOVE COLLECTION APICANDO FILTRO, FIND ALL TRANSFORMA TODO ITEM DA CONDICAO Q PASSARMOS  E O RETORNO SERÁ UM ARRAY COM TUDO Q RETORNA
                "page", is(expectedPage),
                "data.size()", is(expectedItemsPerPage ),
                "data.findAll { it.avatar.startsWith('https://reqres.in') }.size()", is(expectedItemsPerPage)   //it = for LOOP / size é referente ao array de retorno
            );
    }

    @Test
    public void testShowSpecificUser() {
        User user = given().
            pathParam("userId", 2).
        when().
                log().all().
            get(SHOW_USER_ENDPOINT).
        then().
                log().all().
            statusCode(HttpStatus.SC_OK).
        extract().//Extraindo o body cpm json Path, onde pego objetivo e o path Data dele é igual a classe User
            body().jsonPath().getObject("data", User.class);
        //@JsonIgnoreProperties(ignoreUnknown = true)  foi colocado na classe para ignorar id e avatar


        assertThat(user.getEmail(), containsString("@reqres.in"));
        assertThat(user.getName(), is("Janet"));
        assertThat(user.getLastName(), is("Weaver"));
    }

    private int getExpectedItemsPerPage(int page) { //passando como parametro
        return given().
                    param("page", page).
                when().
                    get(LIST_USERS_ENDPOINT).
                then().log().all().
                    statusCode(HttpStatus.SC_OK).
                extract().
                    path("per_page");
    }
}
