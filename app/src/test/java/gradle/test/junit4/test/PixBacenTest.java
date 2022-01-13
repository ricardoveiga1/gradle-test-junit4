package gradle.test.junit4.test;

import com.fasterxml.jackson.core.JsonProcessingException;
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
import org.junit.Test;

import java.io.File;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasKey;

public class PixBacenTest {

    private static final String BASE_URL = "http://localhost:3000";
    private static final String BASE_PATH = "/qrcode/v1";

    private static final String CREATE_PIX_ENDPOINT = "/cob";
    private static final String SEARCH_PIX_ENDPOINT = "/cob/pix";

    @BeforeClass
    public static void setup() {
        //habilita log para todo erro no teste, verbosidade
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();

        //define todo contentType de envio json, serialização
        RestAssured.requestSpecification = new RequestSpecBuilder().
                setContentType(ContentType.JSON).
                build();
        //define todo contentType de retorno json
        RestAssured.responseSpecification = new ResponseSpecBuilder().
                expectContentType(ContentType.JSON).
                build();
    }

    @Test
    public void buscarPixBacenTest(){
        String uri = getUri(SEARCH_PIX_ENDPOINT);
        Response response =
        given().
                when().
                get(uri);
        response.
                then().
                statusCode(HttpStatus.SC_OK).
                body(JsonSchemaValidator.matchesJsonSchemaInClasspath("schemas/buscarPix.json")). //teste contrato
                body("$", hasKey("qrcode_type")).
                body("data", hasKey("tx_id")).
                body("data.amount_modifiers", hasKey("interest")).
                body("data.payments_document[0]", hasKey("payment_id")).
                body("data.payments_document[1]", hasKey("launch_id"))
                //body("$", hasKey("trabajo"))//forçando ERRO
        ;
        //response.prettyPrint();
    }

    @Test
   // @Ignore
    public void gerarPixBacenTest(){
        String uri = getUri(CREATE_PIX_ENDPOINT);
        File bodyPix = new File("/Users/ricardoveiga/Documents/zoop-projects/zoop-barcode-services-test/app/src/test/resources/payload/bodyPix.json");
        //String json = bodyPix.toString();
        //System.out.println("Body => " + bodyPix);
        Response response =
                given().
                        header("Accept", "application/json").
                        when().
                        body(bodyPix).
                        with().contentType(ContentType.JSON).
                        when().
                        post(uri);
        response.
                then().
                statusCode(HttpStatus.SC_CREATED).
                body(JsonSchemaValidator.matchesJsonSchemaInClasspath("schemas/createPix.json"));//teste contrato
        response.prettyPrint();
        //System.out.println(response.getBody());
    }


    @Test
    public void criarQrcodeModel() throws JsonProcessingException {
        String uri = getUri(CREATE_PIX_ENDPOINT);
        QrcodeDTO qrcodeDTO = QrcodeDTO.builder().build();
        //ObjectMapper mapper = new ObjectMapper();
        //String json = mapper.writeValueAsString(qrcodeDTO);
       // System.out.println(json);

        Response response =
                given().
                when().log().all().
                    body(qrcodeDTO).
                when().
                    post(uri);
        //response.prettyPrint();
        response.
                then().
                    statusCode(HttpStatus.SC_CREATED).
                    body(JsonSchemaValidator.matchesJsonSchemaInClasspath("schemas/createPix.json"));
    }

    @Test
    public void decodificarQrcode(){
        String uri = getUri(CREATE_PIX_ENDPOINT);
        QrcodeDTO qrcodeDTO = new QrcodeDTO();
        Response response =
                given().
                        when().
                        body(qrcodeDTO).
                        when().
                        post(uri);
        response.
                then().
                statusCode(HttpStatus.SC_CREATED).
                body(JsonSchemaValidator.matchesJsonSchemaInClasspath("schemas/createPix.json"))
                .extract().body().jsonPath();

        String EMV = response.jsonPath().getString("data.emv");
        String NUMBER = response.jsonPath().getString("data.qrcode.recipient.number");
        String QrcodeType = response.jsonPath().getString("qrcode_type");
        System.out.println("EMV: "+ EMV);
        System.out.println("NUMBER: "+ NUMBER);
        System.out.println("QrcodeType: "+ QrcodeType);

        //Decode
//        Map<String, Object> jsonAsData = new HashMap<String, Object>();
//        jsonAsData.put("emv", EMV);
//        jsonAsData.put("number", NUMBER);
//        Map<String, Object> jsonAsMap = new HashMap<String, Object>();
//        jsonAsMap.put("qrcode_type", QrcodeType);
//        jsonAsMap.put("data", jsonAsData);

        DecodeDTO decodeDTO = new DecodeDTO();
        decodeDTO.getData().setEmv(EMV);
        Response responseDecode =
        given().
                when().log().all().
                body(decodeDTO).
                when().
                post(uri);
        responseDecode.prettyPrint();
        responseDecode.
                then().
                statusCode(HttpStatus.SC_CREATED).
                body(JsonSchemaValidator.matchesJsonSchemaInClasspath("schemas/createPix.json"));
    }

    @Test
    public void criarQrcodeComNovoValorDocumentType()  {
        String uri = getUri(CREATE_PIX_ENDPOINT);
        QrcodeDTO qrcodeDTO = new QrcodeDTO();

        qrcodeDTO.setQrcodeType("");
        qrcodeDTO.getData().setAmount(25F);

        Response response =
                given().
                        when().log().all().
                        body(qrcodeDTO).
                        when().
                        post(uri);
        response.prettyPrint();
        response.
                then().
                statusCode(HttpStatus.SC_CREATED).
                body(JsonSchemaValidator.matchesJsonSchemaInClasspath("schemas/createPix.json"));
    }

    private String getUri(String endpoint) {

        return BASE_URL + BASE_PATH + endpoint;
    }
}
