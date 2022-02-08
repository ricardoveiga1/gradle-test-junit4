## POC com framework rest-assured para teste de APis e contrato

## Requisitos
- Java 8+
- Gradle 7+

## Referências
- https://github.com/rest-assured/rest-assured/wiki/GettingStarted
- https://github.com/rest-assured/rest-assured/wiki/Usage
- https://gradle.org/install/
- https://docs.gradle.org/current/samples/sample_building_java_applications.html
- https://docs.gradle.org/current/userguide/java_testing.html
- https://bacen.github.io/pix-api/#/Cob/post_cob
- https://jsonschema.net/home
- https://www.jenkins.io/download/weekly/macos/ (local)
- https://github.com/jenkinsci/docker/blob/master/README.md (docker) - http://localhost:8080/


## Comandos 
- gradle init - creates the gradle project
- gradle wrapper - necessário para criar o Gradle wrapper para poder rodar o gradlew
- ./gradlew tasks - mostra todas as tarefas disponíveis
- ./gradlew test - roda todos os testes do pacote 'src/test'

## Executar testes

- gradle test --tests org.gradle.SomeTest.someSpecificFeature
- gradle test --tests *SomeTest.someSpecificFeature
- gradle test --tests *SomeSpecificTest
- gradle test --tests all.in.specific.package*
- gradle test --tests *IntegTest
- gradle test --tests *IntegTest*ui*
- gradle test --tests *IntegTest.singleMethod  // gradle test --tests *AlternativeUserTest.testAbleToCreateNewUser
- gradle someTestTask --tests *UiTest someOtherTestTask --tests *WebTest*ui
- gradle test  --tests UserTest
- gradle test  --tests '*UserTest'(filtro todos)
- ./gradlew test  --tests '*.UserTest'(filtro)
- ./gradlew test --tests '*AppTest'
- ./gradlew test --tests '*.UserTest'(somente a classe)
- ./gradlew test --tests '*.UserTest.testeCreateUserSchemaStatic'(test específico)
- gradle test  --tests '*PixExtendBaseTest' --stacktrace
- ./gradlew test  --tests '*PixExtendBaseTest' --stacktrace

## Aprendizado

### Report
- app/build/reports/index.html

### Criar novo projeto
- 1- gradle init
- 2- Selecione opções em seguida: application
- 3- java
- 1- only aplication projetct
- 1- groovy
- 1- junit4 ou 5
- manter o resto padrão
- ./gradlew wrapper(IMPORTANTE, gera o jar do projeto local, olhando versão do gradle e ativa o gradlew tasks, necessário ter gradle instaado)
  para garantir que está rodando com as config do gradle wrapper
- ./gradlew tasks (exibe tudo que é possivel com shell script do gradlew)
- ./gradlew run
- ./gradlew test


### Configurações Adicionais

- Devemos adicionar as dependencias no build.gradle
- Para remover todos imports que não são usados, vá no Menu Code: Optimze Imports

### Melhorias(próximos passos)
- inserir relative path do body estático
- ver uma terceira opção de como criar uma função para validar os contratos, só se fizer sentido
- implementar tags, groups, para poder auxiliar a execução dos testes(ex somente regressão) será necessário para o CI/CI
- aprender expressões em groovy para melhorar os testes, se necessário
- não esquecer de transformar coisas repetidas em métodos para posteriormente instanciar