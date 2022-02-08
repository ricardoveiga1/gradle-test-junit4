package gradle.test.junit4.test;

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
import org.junit.Test;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.hasKey;

public class BaseTestProductionPix {

    private static final String GENERATE_PIX = "/generate";

    @BeforeClass
    public static void setup() {

        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        baseURI = "http://localhost:9999";
        basePath = "/qrcode/v1/cob/pix";


        RestAssured.requestSpecification = new RequestSpecBuilder().
                setContentType(ContentType.JSON).
                build();

        RestAssured.responseSpecification = new ResponseSpecBuilder().
                expectContentType(ContentType.JSON).
                build();

    }

    public static void gerarPixResponse()  {
        QrcodeDTO qrcodeDTO = QrcodeDTO.builder().build();
        Faker fake = new Faker();
        qrcodeDTO.setQrcodeType(fake.address().cityName());

        String Response =
                given().
                        body(qrcodeDTO).
                        when().
                        post(GENERATE_PIX).
                        then().
                        log().all().
                        extract().
                        path("data.emv")
                ;
    }

}
