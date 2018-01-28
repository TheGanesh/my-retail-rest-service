package com.ganesh.controller

import com.github.tomakehurst.wiremock.http.Request
import com.github.tomakehurst.wiremock.http.RequestMethod
import com.github.tomakehurst.wiremock.junit.WireMockRule
import com.github.tomakehurst.wiremock.matching.MatchResult
import com.myRetail.MyRetailApplication
import com.myRetail.contract.CurrentPrice
import com.myRetail.contract.ProductDetails
import com.myRetail.exception.ErrorResponse
import com.myRetail.service.ProductCatalogService
import groovy.util.logging.Slf4j
import org.junit.Rule
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.*
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification
import util.EmbeddedCassandra

import javax.annotation.PostConstruct

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse
import static com.github.tomakehurst.wiremock.client.WireMock.requestMatching
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig

@Slf4j
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = [MyRetailApplication, TestConfig],
        properties = ['spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.aop.AopAutoConfiguration']
)
@ContextConfiguration
class ProductsInfoIntegrationSpec extends Specification {

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(wireMockConfig().dynamicPort().dynamicHttpsPort())

    @Autowired
    protected TestRestTemplate testRestTemplate

    @Autowired
    protected ProductCatalogService productCatalogService

    def 'application health check'() {

        when:
        Map response = testRestTemplate.getForObject("/info/health", Map.class)
        then:
        response.status == "UP"
    }

    def 'Product GET API success scenario'() {

        setup:

        //Redirecting to wiremock instead actual redSky url
        productCatalogService.redSkyBaseUrl = "http://localhost:${wireMockRule.port()}"

        //mocked redSky response
        String sampleRedSkyResponse = getClass().getResource('/redSkyResponse.json').text

        wireMockRule.stubFor(requestMatching { Request request ->
            MatchResult.of(request.absoluteUrl.contains("13860428") && request.method == RequestMethod.GET)
        }.willReturn(aResponse()
                .withBody(sampleRedSkyResponse)
                .withHeader("Content-Type", "application/json")
                .withStatus(200)))


        when:

        ProductDetails productDetails = testRestTemplate.exchange("/products/13860428", HttpMethod.GET, new HttpEntity(null, headers()), ProductDetails).body

        then:

        productDetails.id == 13860428L
        productDetails.name == "The Big Lebowski (Blu-ray)"
        productDetails.current_price.value == 10.11
        productDetails.current_price.currency_code == "USD"

    }


    def 'Product GET API failure scenario'() {

        setup:

        //Redirecting to wiremock instead actual redSky url
        productCatalogService.redSkyBaseUrl = "http://localhost:${wireMockRule.port()}"

        wireMockRule.stubFor(requestMatching { Request request ->
            MatchResult.of(request.absoluteUrl.contains("11111") && request.method == RequestMethod.GET)
        }.willReturn(aResponse()
                .withBody("""{"IN_VALID":"XX"}""")
                .withHeader("Content-Type", "application/json")
                .withStatus(400)))


        when:

        ResponseEntity<ErrorResponse> responseEntity = testRestTemplate.exchange("/products/11111", HttpMethod.GET, new HttpEntity(null, headers()), ErrorResponse)

        then:
        responseEntity.statusCode == HttpStatus.NOT_FOUND
        responseEntity.body.errorCode == "INVALID_PRODUCT_ID"

    }


    def 'Product PUT API success scenario'() {

        setup:

        ProductDetails productDetails = new ProductDetails(
                id: 13860429L,
                name: "The Big Lebowski (Blu-ray)",
                current_price: new CurrentPrice(
                        value: 100.11,
                        currency_code: "USD"
                )

        )
        HttpEntity<ProductDetails> requestEntity = new HttpEntity<>(productDetails, headers())
        when:

        ProductDetails response = testRestTemplate.exchange("/products/13860429", HttpMethod.PUT, requestEntity, ProductDetails).body

        then:

        response.id == 13860429L
        response.name == "The Big Lebowski (Blu-ray)"
        response.current_price.value == 100.11
        response.current_price.currency_code == "USD"

    }

    def 'Product PUT API failure scenario'() {

        setup:

        ProductDetails productDetails = new ProductDetails(
                id: 2222L,
                name: "The Big Lebowski (Blu-ray)",
                current_price: new CurrentPrice(
                        value: 10.11,
                        currency_code: "USD"
                )

        )
        HttpEntity<ProductDetails> requestEntity = new HttpEntity<>(productDetails, headers())
        when:

        ResponseEntity<ErrorResponse> responseEntity = testRestTemplate.exchange("/products/3333", HttpMethod.PUT, requestEntity, ErrorResponse)

        then:

        responseEntity.statusCode == HttpStatus.PRECONDITION_FAILED
        responseEntity.body.errorCode == "PRODUCT_ID_MISMATCH"
    }


    @TestConfiguration
    static class TestConfig {

        private EmbeddedCassandra embeddedCassandra

        @PostConstruct
        void postConstruct() {
            log.info('\n\n\tstarting cassandra for unit tests.\n')
            embeddedCassandra = new EmbeddedCassandra()
            embeddedCassandra.start()

        }
    }

    protected HttpHeaders headers() {
        HttpHeaders requestHeaders = new HttpHeaders()
        requestHeaders.add('X-CLIENT-ID', 'XXXX')
        requestHeaders.add('X-CLIENT-SECRET', '9x44bun4fc9b96ugswhhwfe4p6mb3qe6')
        requestHeaders.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
        requestHeaders.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        return requestHeaders
    }
}