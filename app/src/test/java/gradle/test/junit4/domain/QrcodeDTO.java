package gradle.test.junit4.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import gradle.test.junit4.suport.DataUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
//com Builder, se não passar nada na chamada, será utilizado os dados inicializados no builder
public class QrcodeDTO {

    @Builder.Default
    @JsonProperty("qrcode_type")
    private String qrcodeType = "PIX";

    @Builder.Default
    @JsonProperty("pix_type")
    private String pixType = "test";

    @Builder.Default
    @JsonProperty("data")
    private DataQrcode data = new DataQrcode();

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DataQrcode { //para ter uma classe dentro de outra, a classe precisa ser estatica

        @Builder.Default
        @JsonProperty("dict_key")
        private DictKey dictkey = new DictKey();

        @Builder.Default
        @JsonProperty("amount")
        private Float amount = 500F;

        @Builder.Default
        @JsonProperty("calendar")
        private Calendar calendar = new Calendar();

        @Builder.Default
        @JsonProperty("qrcode")
        private Qrcode qrcode = new Qrcode();

    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DictKey {

        @Builder.Default
        @JsonProperty("type")
        private String keyType = "EMAIL";

        @Builder.Default
        @JsonProperty("value")
        private String keyValue = "ricardo@gmail.com";
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Calendar{
        @Builder.Default
        @JsonProperty("expiration")
        private String expiration = DataUtils.getDataDiferencaDias(1);

    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Qrcode{

        @Builder.Default
        @JsonProperty("type")
        private String typeQrcode = "dynamic";
        @Builder.Default
        @JsonProperty("reusable")
        private Boolean reusable = true;
        @Builder.Default
        @JsonProperty("reusable_type")
        private String reusableType = "automatic";
        @Builder.Default
        @JsonProperty("recipient")
        private Recipient recipient = new Recipient();

    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Recipient{

        @Builder.Default
        @JsonProperty("document_type")
        private String documentType = "EMAIL";
        @Builder.Default
        @JsonProperty("number")
        private String number = "ricardo@gmail.com";
    }
}