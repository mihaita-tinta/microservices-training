package com.mih.training.invoice.wiremock;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.stubbing.Scenario;
import org.springframework.boot.CommandLineRunner;
import org.springframework.cloud.contract.wiremock.WireMockConfiguration;
import org.springframework.cloud.contract.wiremock.WireMockConfigurationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.Random;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

@Configuration
@Import(WireMockConfiguration.class)
public class WiremockConfig {
    private Random random = new Random();

    @Bean
    public CommandLineRunner runner() {
        return args -> {
            stubFor(get(urlEqualTo("/")).willReturn(aResponse()
                    .withHeader("Content-Type", "text/plain")
                    .withBody("Welcome to wiremock.\n" +
                            "You can use:\n" +
                            "\t http://localhost:8081/v1/users/123/accounts to list all accounts for an user\n" +
                            "\t http://localhost:8081/v1/users/123/accounts/12312345 to see account details")));

            stubFor(get(urlMatching("/v1/users/(?<username>.*?)/accounts")).willReturn(aResponse()
                    .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                    .withBody("[\"$(username)12345\"]")
                    .withTransformers("stub-transformer-with-params")
                    .withTransformerParameter("urlRegex", "/v1/users/(?<username>.*?)/accounts")
            ));


            String accountUrl = "/v1/users/[\\s\\S]+?/accounts/[0-9]+?.*";
            stubFor(WireMock.get(WireMock.urlMatching(accountUrl))
                    .inScenario("get-accounts")
                    .whenScenarioStateIs(Scenario.STARTED)
                    .willReturn(aResponse()
                            .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                            .withBody("{\n" +
                                    "        \"id\": \"$(accountId)\",\n" +
                                    "        \"accountHolder\": \"$(username)\",\n" +
                                    "        \"accountReferences\": {\n" +
                                    "          \"iban\": \"NL79ABNA94$(accountId)\",\n" +
                                    "          \"maskedPan\": \"XXXXXXXXXX1234\"\n" +
                                    "        },\n" +
                                    "        \"balance\": $(!RandomDouble),\n" +
                                    "        \"currency\": \"RON\",\n" +
                                    "        \"interestRate\": 0.04,\n" +
                                    "        \"product\": \"Gold account.\",\n" +
                                    "        \"status\": \"ENABLED\",\n" +
                                    "        \"type\": \"CREDIT_CARD\",\n" +
                                    "        \"usage\": \"PRIV\"\n" +
                                    "      }")
                            .withStatus(HttpStatus.OK.value())
                            .withTransformers("stub-transformer-with-params")
                            .withTransformerParameter("urlRegex", "/v1/users/(?<username>.*?)/accounts/(?<accountId>.*?)")
                    )
                    .willSetStateTo("user-fail")
            );
            stubFor(WireMock.get(WireMock.urlPathMatching(accountUrl))
                    .inScenario("get-accounts")
                    .whenScenarioStateIs("user-fail")
                    .willReturn(aResponse()
                            .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                            .withStatus(HttpStatus.SERVICE_UNAVAILABLE.value())
                    )
                    .willSetStateTo(Scenario.STARTED)
            );
        };
    }

    @Bean
    public WireMockConfigurationCustomizer customizer() {
        return config -> config.extensions(StubResponseTransformerWithParams.class);
    }

}
