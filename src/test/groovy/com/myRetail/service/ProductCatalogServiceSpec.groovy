package com.myRetail.service

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.web.client.RestTemplate
import spock.lang.Specification
import spock.lang.Unroll

class ProductCatalogServiceSpec extends Specification {

    ProductCatalogService productCatalogService

    RestTemplate redSkyRestTemplate

    def setup() {

        redSkyRestTemplate = Mock(RestTemplate)

        productCatalogService = new ProductCatalogService(
                redSkyRestTemplate: redSkyRestTemplate,
                redSkyBaseUrl: "http://redSky.com",
                redSkyProductPath:"/123",
                objectMapper: new ObjectMapper()
        )
    }

    @Unroll
    def "getProductName success scenario"() {

        setup:

        String sampleRedSkyResponse = getClass().getResource('/redSkyResponse.json').text

        when:

        String productName = productCatalogService.getProductName(13860429L)

        then:

        1 * redSkyRestTemplate.getForObject("http://redSky.com/123",Map.class,13860429L) >> new ObjectMapper().readValue(sampleRedSkyResponse,Map.class)
        productName == "The Big Lebowski (Blu-ray)"

    }

}
