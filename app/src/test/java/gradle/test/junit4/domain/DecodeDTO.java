package gradle.test.junit4.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
//com Builder, se não passar nada na chamada, será utilizado os dados inicializados no builder
public class DecodeDTO {

    @Builder.Default
    @JsonProperty("qrcode_type")
    private String qrcodeType = "PIX";

    @Builder.Default
    @JsonProperty("data")
    private DataQrcode data = new DataQrcode();

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DataQrcode {

        @Builder.Default
        @JsonProperty("number")
        private String number = "ricardo@gmail.com";

        @Builder.Default
        @JsonProperty("emv")
        private String emv = "test";

    }


}
