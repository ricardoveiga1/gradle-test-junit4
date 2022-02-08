package gradle.test.junit4.test;

import com.github.javafaker.Faker;
import gradle.test.junit4.domain.DecodeDTO;
import gradle.test.junit4.domain.QrcodeDTO;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasKey;

public class PixTestExtendBaseTest extends BaseTestProductionPix{

    private static final String GET_PIX = "/1";
    private static final String GENERATE_PIX = "/generate";
    private static final String DECODE_PIX = "/decode";


    @Test
    public void getOnePix(){
        //String uri = getUri(GET_PIX);
        Response response =
                given().
                        log().all().
                        when().
                        get(GET_PIX);
        response.
                then().
                statusCode(HttpStatus.SC_OK).
                body(JsonSchemaValidator.matchesJsonSchemaInClasspath("schemas/getPixSimple.json")). //teste contrato
                body("$", hasKey("qrcode_type")).
                body("data", hasKey("tx_id")).
                body("data.amount_modifiers", hasKey("interest")).
                body("data.payments_document[0]", hasKey("payment_id")).
                body("data.payments_document[1]", hasKey("launch_id"))
        ;
    }


    @Test
    public  void  gerarPix(){
        String EmvPix = generateQrcodeDTO();
        System.out.println("EMV do método do package Utils: " + EmvPix);
    }

    public static String generateQrcodeDTO()  {
        // String uri = getUri(CREATE_PIX_ENDPOINT);
        QrcodeDTO qrcodeDTO = QrcodeDTO.builder().build();

        Faker fake = new Faker();
        qrcodeDTO.setQrcodeType(fake.address().cityName());

        return
                given().
                        body(qrcodeDTO).
                        when().
                        post(GENERATE_PIX).
                        then().
                        extract().
                        path("data.emv")
                ;
    }

    @Test
    public  void  decodePix(){
        String EmvPix = generateExtractQrcodeDTO();//Metodo de geração abaixo
        System.out.println("EMV do método do package Utils: " + EmvPix);

        DecodeDTO decodeDTO = new DecodeDTO();
        decodeDTO.getData().setEmv(EmvPix);
    //Decodificando
        Response responseDecode =
                given().
                        when().log().all().
                        body(decodeDTO).
                        when().
                        post(DECODE_PIX);
        responseDecode.prettyPrint();
        responseDecode.
                then().
                statusCode(HttpStatus.SC_CREATED).
                body(JsonSchemaValidator.matchesJsonSchemaInClasspath("schemas/decodePix.json"));
    }



    public static String generateExtractQrcodeDTO()  {
        // String uri = getUri(CREATE_PIX_ENDPOINT);
        QrcodeDTO qrcodeDTO = QrcodeDTO.builder().build();

        Faker fake = new Faker();
        qrcodeDTO.setQrcodeType(fake.address().cityName());

        return
                given().
                        body(qrcodeDTO).
                        when().
                        post(GENERATE_PIX).
                        then().
                        extract().
                        path("data.emv")
                ;
    }

    @Test
    public void gerarPixResponseTest(){
        decodePix();


    }


    public static void gerarPixResponse()  {
        // String uri = getUri(CREATE_PIX_ENDPOINT);
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
