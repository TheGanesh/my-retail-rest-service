package com.myRetail.service

import com.myRetail.contract.CurrentPrice
import com.myRetail.contract.ProductDetails
import com.myRetail.exception.ResourceNotFoundException
import com.myRetail.repository.ProductPrice
import com.myRetail.repository.ProductPriceRepository
import org.springframework.data.cassandra.repository.MapId
import spock.lang.Specification
import spock.lang.Unroll

import java.time.LocalDateTime

class ProductPriceServiceSpec extends Specification {

    ProductPriceService productPriceService

    ProductPriceRepository productPriceRepository

    def setup() {

        productPriceRepository = Mock(ProductPriceRepository)

        productPriceService = new ProductPriceService(
                productPriceRepository: productPriceRepository
        )
    }

    @Unroll
    def "getProductCurrentPrice success scenario"() {

        when:

        CurrentPrice currentPrice = productPriceService.getProductCurrentPrice(13860429L)

        then:

        1 * productPriceRepository.findOne(_ as MapId) >> new ProductPrice(
                currentPrice: 10.11,
                currencyCode: "USD"
        )

        currentPrice.value == 10.11
        currentPrice.currency_code == "USD"

    }

    @Unroll
    def "getProductCurrentPrice exception scenario"() {

        when:

         productPriceService.getProductCurrentPrice(13860429L)

        then:

        1 * productPriceRepository.findOne(_ as MapId) >> null

        ResourceNotFoundException resourceNotFoundException = thrown()
        resourceNotFoundException.errorCode == "PRODUCT_NOT_FOUND"

    }

    @Unroll
    def "updateProductPriceDetails success scenario"() {

        setup:

        ProductDetails priceUpdateRequest = new ProductDetails(
                id: 13860429L,
                name: "The Big Lebowski (Blu-ray)",
                current_price: new CurrentPrice(
                        value: 100.11,
                        currency_code: "USD"
                )

        )

        when:

        ProductDetails priceUpdateResponse = productPriceService.updateProductPriceDetails(priceUpdateRequest)

        then:

        1 * productPriceRepository.save(_ as ProductPrice) >>  new ProductPrice(
                productId: 13860429L,
                productName: "The Big Lebowski (Blu-ray)",
                currentPrice: 100.11,
                currencyCode: "USD",
                updatedTime: LocalDateTime.now()
        )

        priceUpdateResponse.id == 13860429L
        priceUpdateResponse.name == "The Big Lebowski (Blu-ray)"
        priceUpdateResponse.current_price.value == 100.11
        priceUpdateResponse.current_price.currency_code == "USD"

    }

}
