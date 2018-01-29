package com.myRetail.service

import com.myRetail.contract.CurrentPrice
import com.myRetail.contract.ProductDetails
import com.myRetail.exception.ResourceNotFoundException
import spock.lang.Specification
import spock.lang.Unroll

class ProductAggregationServiceSpec extends Specification {

    ProductAggregationService productAggregationService

    ProductPriceService productPriceService
    ProductCatalogService productCatalogService

    def setup() {

        productPriceService = Mock(ProductPriceService)
        productCatalogService = Mock(ProductCatalogService)

        productAggregationService = new ProductAggregationService(
                productPriceService: productPriceService,
                productCatalogService: productCatalogService
        )
    }

    @Unroll
    def "getProductDetails success scenario"() {

        when:

        ProductDetails productDetails = productAggregationService.getProductDetails(13860429L)

        then:

        1 * productCatalogService.getProductName(13860429L) >> "The Big Lebowski"
        1 * productPriceService.getProductCurrentPrice(13860429L) >> new CurrentPrice(value: 1, currency_code: "USD")

        productDetails.id == 13860429L
        productDetails.name == "The Big Lebowski"
        productDetails.current_price.currency_code == "USD"
        productDetails.current_price.value == 1
    }

    @Unroll
    def "getProductDetails exception scenario"() {

        when:

        productAggregationService.getProductDetails(13860429L)

        then:

        1 * productCatalogService.getProductName(13860429L) >> "The Big Lebowski"
        1 * productPriceService.getProductCurrentPrice(13860429L) >> { throw new ResourceNotFoundException("13860429") }

        ResourceNotFoundException resourceNotFoundException = thrown()
        resourceNotFoundException.errorCode == "PRODUCT_NOT_FOUND"
    }
}
