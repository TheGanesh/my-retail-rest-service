package com.myRetail.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.myRetail.contract.CurrentPrice
import com.myRetail.contract.ProductDetails
import com.myRetail.exception.InvalidPriceUpdateException
import com.myRetail.exception.PreconditionFailedException
import com.myRetail.service.ProductAggregationService
import com.myRetail.service.ProductPriceService
import org.springframework.validation.BindingResult
import spock.lang.Specification
import spock.lang.Unroll

class ProductDetailsControllerSpec extends Specification {

    ProductDetailsController productDetailsController
    ProductAggregationService productAggregationService
    ProductPriceService productPriceService

    def setup() {

        productAggregationService = Mock(ProductAggregationService)
        productPriceService = Mock(ProductPriceService)

        productDetailsController = new ProductDetailsController(
                productAggregationService: productAggregationService,
                productPriceService: productPriceService,
                objectMapper: new ObjectMapper()
        )
    }

    @Unroll
    def "getProductDetails success scenario"() {

        setup:

        ProductDetails productDetails = new ProductDetails(
                id: 13860429L,
                name: "The Big Lebowski (Blu-ray)",
                current_price: new CurrentPrice(
                        value: 100.11,
                        currency_code: "USD"
                )

        )

        when:

        ProductDetails response = productDetailsController.getProductDetails(13860429L)

        then:

        1 * productAggregationService.getProductDetails(13860429L) >> productDetails
        response.id == 13860429L
        response.name == "The Big Lebowski (Blu-ray)"
        response.current_price.value == 100.11
        response.current_price.currency_code == "USD"
    }

    @Unroll
    def "updateProductPrice success scenario"() {

        setup:

        BindingResult bindingResults = Mock(BindingResult)
        bindingResults.hasErrors() >> false

        ProductDetails productDetails = new ProductDetails(
                id: 13860429L,
                name: "The Big Lebowski (Blu-ray)",
                current_price: new CurrentPrice(
                        value: 100.11,
                        currency_code: "USD"
                )

        )

        when:

        ProductDetails response = productDetailsController.updateProductPrice(productDetails, bindingResults, 13860429L)

        then:

        1 * productPriceService.updateProductPriceDetails(productDetails) >> new ProductDetails(
                id: 13860429L,
                name: "The Big Lebowski (Blu-ray)",
                current_price: new CurrentPrice(
                        value: 110.11,
                        currency_code: "USD"
                )

        )
        response.id == 13860429L
        response.name == "The Big Lebowski (Blu-ray)"
        response.current_price.value == 110.11
        response.current_price.currency_code == "USD"
    }


    @Unroll
    def "updateProductPrice InvalidPriceUpdateException scenario"() {

        setup:

        BindingResult bindingResults = Mock(BindingResult)
        bindingResults.hasErrors() >> true

        ProductDetails productDetails = new ProductDetails()

        when:

        productDetailsController.updateProductPrice(productDetails, bindingResults, 13860429L)

        then:

        0 * productPriceService.updateProductPriceDetails(productDetails)
        InvalidPriceUpdateException invalidPriceUpdateException = thrown()
        invalidPriceUpdateException.errorCode == "INVALID_PRICE_UPDATE"
    }

    @Unroll
    def "updateProductPrice PreconditionFailedException scenario"() {

        setup:

        BindingResult bindingResults = Mock(BindingResult)
        bindingResults.hasErrors() >> false

        ProductDetails productDetails = new ProductDetails(id: 111L)

        when:

        productDetailsController.updateProductPrice(productDetails, bindingResults, 13860429L)

        then:

        0 * productPriceService.updateProductPriceDetails(productDetails)
        PreconditionFailedException preconditionFailedException = thrown()
        preconditionFailedException.errorCode == "PRODUCT_ID_MISMATCH"
    }


}
