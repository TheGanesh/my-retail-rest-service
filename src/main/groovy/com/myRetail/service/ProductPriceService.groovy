package com.myRetail.service

import com.myRetail.contract.CurrentPrice
import com.myRetail.contract.ProductDetails
import com.myRetail.exception.ResourceNotFoundException
import com.myRetail.repository.ProductPrice
import com.myRetail.repository.ProductPriceRepository
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import java.time.LocalDateTime

/**
 * This service reads/writes price related information to Cassandra Data store
 */
@Service
@Slf4j
class ProductPriceService {

    @Autowired
    ProductPriceRepository productPriceRepository

    CurrentPrice getProductCurrentPrice(Long productId) {

        ProductPrice productPrice = productPriceRepository.findOne(new ProductPrice(productId: productId, currencyCode: "USD").mapId)
        if (!productPrice) {
            throw new ResourceNotFoundException(productId.toString())
        }

        log.info("action=getProductCurrentPrice,productId=${productId},currentPrice=${productPrice?.currentPrice}")
        return new CurrentPrice(
                value: productPrice.currentPrice,
                currency_code: productPrice.currencyCode
        )
    }

    void updateProductPriceDetails(ProductDetails priceUpdateRequest) {

        productPriceRepository.save(
                priceUpdateRequest.with {
                    new ProductPrice(
                            productId: id,
                            productName: name,
                            currentPrice: current_price.value,
                            currencyCode: current_price.currency_code,
                            updatedTime: LocalDateTime.now()
                    )
                }
        )

        log.info("action=updateProductPriceDetails,productId=${priceUpdateRequest?.id},currentPrice=${priceUpdateRequest?.current_price?.value}")
    }
}
