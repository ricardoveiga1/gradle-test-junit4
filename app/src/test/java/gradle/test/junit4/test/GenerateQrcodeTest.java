package gradle.test.junit4.test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import gradle.test.junit4.domain.DecodeDTO;
import gradle.test.junit4.domain.QrcodeDTO;
import gradle.test.junit4.suport.DataUtils;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.junit.BeforeClass;
import org.junit.Test;


import java.util.concurrent.TimeUnit;

import static io.restassured.RestAssured.given;

public class GenerateQrcodeTest extends BaseTest {

    private static final String BASE_URL = "https://zoop-qrcode.staging.zoop.tech";
    private static final String BASE_PATH = "/pix/v1";

    private static final String CREATE_QRCODE_ENDPOINT = "/generate";
    private static final String DECODE_QRCODE_ENDPOINT = "/pix/v1/decode";
    private static final String SEARCH_QRCODE_ENDPOINT = "/users/{userId}";

    @BeforeClass
    public static void setup() {
        //habilita log para todo erro no teste, verbosidade
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();

        //define todo content type de envio json, serialização
        RestAssured.requestSpecification = new RequestSpecBuilder().
                setContentType(ContentType.JSON).
                build();
        //define todo content  type de retorno json
        RestAssured.responseSpecification = new ResponseSpecBuilder().
                expectContentType(ContentType.JSON).
                build();
    }

    @Test
    public void postGenerateQrcodeString(){
        String uri = getUri(CREATE_QRCODE_ENDPOINT);

        String expirationData = DataUtils.getDataDiferencaDias(1);
        //BK indicou
        String json = "{nome:variavel}";
        json.replace("data.calendar.expiration", expirationData);

        String payload = "{\n" +
                "    \"qrcode_type\" : \"PIX_ITAU\",\n" +
                "    \"pix_type\": \"checkout\",\n" +
                "    \"data\" : {\n" +
                "        \"dict_key\":{\n" +
                "            \"type\": \"EVP\",\n" +
                "            \"value\":\"82026a7d-ffd7-4b56-bda8-f599a4db711e\"\n" +
                "        },\n" +
                "        \"amount\": 500,\n" +
                "        \"calendar\":{\n" +
                "            \"expiration\":\"{expirationData}\"\n" +
                "        },\n" +
                "        \"qrcode\":{\n" +
                "            \"type\": \"dynamic\",\n" +
                "            \"reusable\": true,\n" +
                "            \"reusable_type\": \"automatic\",\n" +
                "            \"recipient\" :{\n" +
                "                \"document_type\": \"CPF\",\n" +
                "                \"number\": \"34008298460\"\n" +
                "            }\n" +
                "        }\n" +
                "    }\n" +
                "}";


        Response response =
                given().
                        log().all().
                        when().
                        body(payload).
                        when().
                        post(uri);
        response.prettyPrint();
        response.
                then().
                log().all().
                statusCode(HttpStatus.SC_CREATED).
                statusCode(201).
                body(JsonSchemaValidator.matchesJsonSchemaInClasspath("schemas/createPix.json"));
        //DOCUMENT_NUMBER = response.jsonPath().getInt("data");
        //EMV = response.jsonPath().getInt("data.emv");
    }


    @Test
    public void shouldCreateQrcodeDTO() throws JsonProcessingException {
        String uri = getUri(CREATE_QRCODE_ENDPOINT);
        //QrcodeDTO qrcodeDTO = new QrcodeDTO();
        QrcodeDTO qrcodeDTO = QrcodeDTO.builder().build();
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(qrcodeDTO);


        System.out.println(json);

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

    @Test
    public void shouldDecodeQrcodeDTO(){
        //String uri = getUri(CREATE_QRCODE_ENDPOINT);
        String uri = "http://localhost:3000/pix/post";
        QrcodeDTO qrcodeBuilder = new QrcodeDTO();

        Response response =
                given().
                        when().log().all().
                        body(qrcodeBuilder).
                        when().
                        post(uri);
        //response.prettyPrint();
        response.
                then().
                //statusCode(HttpStatus.SC_CREATED).
                //statusCode(200).
                body(JsonSchemaValidator.matchesJsonSchemaInClasspath("schemas/createPix.json"))
                .extract().body().jsonPath();
        //("data.emv");
        String EMV = response.jsonPath().getString("data.emv");
        String NUMBER = response.jsonPath().getString("data.qrcode.recipient.number");
        String QrcodeType = response.jsonPath().getString("qrcode_type");
        System.out.println("EMV: "+EMV);

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
                when().
                body(decodeDTO).
                when().
                post(uri);
        responseDecode.prettyPrint();
        responseDecode.
                then().
                //statusCode(HttpStatus.SC_CREATED).
                //statusCode(201).
                body(JsonSchemaValidator.matchesJsonSchemaInClasspath("schemas/createPix.json"));

    }

    @Test
    public void shouldCreateQrcodeOLD()  {
        String uri = getUri(CREATE_QRCODE_ENDPOINT);
        QrcodeDTO qrcodeDTO = new QrcodeDTO();

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

    @Test
    public void shouldCreateQrcodeDTOFAKER() throws JsonProcessingException {
        String uri = getUri(CREATE_QRCODE_ENDPOINT);
        //QrcodeDTO qrcodeDTO = new QrcodeDTO();
        QrcodeDTO qrcodeDTO = QrcodeDTO.builder().build();
        Faker fake = new Faker();
        qrcodeDTO.setQrcodeType(fake.address().cityName());
        //qrcodeDTO.getData().getCalendar().setExpiration(fake.date().future(50L, TimeUnit.SECONDS));
        qrcodeDTO.getData().getQrcode().setTypeQrcode(fake.name().lastName());

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
