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

    ProductDetails updateProductPriceDetails(ProductDetails priceUpdateRequest) {

        ProductPrice productPrice = productPriceRepository.save(
                new ProductPrice(
                        productId: priceUpdateRequest.id,
                        productName: priceUpdateRequest.name,
                        currentPrice: priceUpdateRequest.current_price.value,
                        currencyCode: priceUpdateRequest.current_price.currency_code,
                        updatedTime: LocalDateTime.now()
                )
        )

        ProductDetails updatedProductDetails = new ProductDetails(
                id: productPrice.productId,
                name: productPrice.productName,
                current_price: new CurrentPrice(
                        value: productPrice.currentPrice,
                        currency_code: productPrice.currencyCode
                )
        )

        log.info("action=updateProductPriceDetails,productId=${updatedProductDetails?.id},currentPrice=${updatedProductDetails?.current_price?.value}")
        return updatedProductDetails
    }
}
