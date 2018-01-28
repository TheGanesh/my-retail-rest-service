package com.myRetail.service

import com.myRetail.contract.CurrentPrice
import com.myRetail.contract.ProductDetails
import groovy.util.logging.Slf4j
import groovyx.gpars.GParsPool
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import java.util.concurrent.ExecutionException
import java.util.concurrent.Future

@Service
@Slf4j
class ProductAggregationService {

    @Autowired
    ProductPriceService productPriceService

    @Autowired
    ProductCatalogService productCatalogService

    ProductDetails getProductDetails(Long productId) {

        Future<String> productNameFuture = null
        Future<CurrentPrice> currentPriceFuture = null

        GParsPool.withPool {
            productNameFuture =  { productCatalogService.getProductName(productId) }.callAsync() as Future
            currentPriceFuture =  { productPriceService.getProductCurrentPrice(productId) }.callAsync() as Future
        }

        try {

            String productName = productNameFuture.get()
            CurrentPrice currentPrice = currentPriceFuture.get()

            return new ProductDetails(
                    id: productId,
                    name: productName,
                    current_price: currentPrice
            )

        } catch (ExecutionException | InterruptedException exception) {
            log.error("executeParallelException=getProductDetails,exceptionClass=${exception?.cause?.class?.simpleName}", exception?.cause)
            throw exception?.cause
        }

    }


}
