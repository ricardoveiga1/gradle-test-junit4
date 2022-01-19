package gradle.test.junit4.suport;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.javafaker.Faker;
import gradle.test.junit4.domain.DecodeDTO;
import gradle.test.junit4.domain.QrcodeDTO;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.junit.BeforeClass;

import static io.restassured.RestAssured.given;

public class QrcodeUtils {

    private static final String BASE_URL = "http://localhost:3000";
    private static final String BASE_PATH = "/qrcode/v1";

    private static final String CREATE_PIX_ENDPOINT = "/cob";
    private static final String SEARCH_PIX_ENDPOINT = "/cob/pix";


    public static String CriarQrcodDTO()  {
        String uri = getUri(CREATE_PIX_ENDPOINT);
        QrcodeDTO qrcodeDTO = QrcodeDTO.builder().build();
        Faker fake = new Faker();
        qrcodeDTO.setQrcodeType(fake.address().cityName());
        qrcodeDTO.getData().getQrcode().setTypeQrcode(fake.name().lastName());

       return
                given().
                        body(qrcodeDTO).
                        when().
                        post(uri).
                        then().
                        extract().
                        path("data.emv")
                ;
    }

    public static String DecodificarQualquerQrcode()  {
        String uri = getUri(CREATE_PIX_ENDPOINT);
        String EMV = QrcodeUtils.CriarQrcodDTO();

        DecodeDTO decodeDTO = new DecodeDTO();
        decodeDTO.getData().setEmv(EMV);

        return
                given().
                        body(decodeDTO).
                        when().
                        post(uri).
                        then().
                        extract().
                        path("data.qrcode.type")
                ;
    }


    private static String getUri(String endpoint) {

        return BASE_URL + BASE_PATH + endpoint;
    }
}
