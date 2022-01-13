package gradle.test.junit4.domain;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//Utilização do Lombok
@JsonIgnoreProperties(ignoreUnknown = true) //config do Jackson para dizer para deconsiderar propriedades desconhecidas como id e avatar, feito para o teste da UserTest
@Data// Serve para gerar Getter e Setter juntos do Lombok(Lombok serve para reduzir as linhas e não criar o Pojo padrão antigo)
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @JsonAlias("first_name") //Deserialização, no contrato o campo é first_name, para teste de UserTest
    private String name;
    private String job;
    private String email;

    @JsonAlias("last_name")
    private String lastName;
}

//Poderia usar algumas coisas do jackson como @JsonGetter("first_name")Serealização(transformar em um json)
// e @JsonSetter("last_name")(Usado na deserialização transforma um Json em um Objeto) faria o Alias ser usado apenas em cada método