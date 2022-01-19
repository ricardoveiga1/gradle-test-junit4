package gradle.test.junit4.test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.javafaker.Faker;
import gradle.test.junit4.domain.QrcodeDTO;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.junit.BeforeClass;

import static io.restassured.RestAssured.*;

public class BaseTest {

    @BeforeClass
    public static void setup() {
        //habilita log para todo erro no teste, verbosidade
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        baseURI = "https://reqres.in";
        basePath = "/api";

    //define todo content type de envio json serializando, poderia add um header ou parametro  SET_CONTENT_TYPE
        RestAssured.requestSpecification = new RequestSpecBuilder().
                setContentType(ContentType.JSON).
                build();

    //define todo content  type de retorno json, poderia add um header  EXPECT
        RestAssured.responseSpecification = new ResponseSpecBuilder().
                expectContentType(ContentType.JSON).
                build();
    }

}
