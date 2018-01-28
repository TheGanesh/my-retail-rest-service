package com.myRetail.controller

import com.myRetail.contract.ProductDetails
import com.myRetail.exception.InvalidPriceUpdateException
import com.myRetail.exception.PreconditionFailedException
import com.myRetail.service.ProductAggregationService
import com.myRetail.service.ProductPriceService
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import io.swagger.annotations.ApiOperation
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*

import javax.validation.Valid

@RestController
@RequestMapping("/products/{id}")
@Slf4j
@CompileStatic
class ProductDetailsController {

    @Autowired
    private ProductAggregationService productAggregationService

    @Autowired
    ProductPriceService productPriceService

    @ApiOperation(value = "API for fetching product information by productId", response = ProductDetails.class, tags = ["Get Product Information"])

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    ProductDetails getProductDetails(@PathVariable Long id) {
        return productAggregationService.getProductDetails(id)
    }


    @ApiOperation(value = "API for updating product current price ", response = ProductDetails.class, tags = ["Update Product Current Price"])

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    ProductDetails updateProductPrice(@RequestBody @Valid ProductDetails priceUpdateRequest, BindingResult bindingResults, @PathVariable Long id) {

        validatePriceUpdateRequest(bindingResults, id, priceUpdateRequest)

        productPriceService.updateProductPriceDetails(priceUpdateRequest)
        return priceUpdateRequest
    }

    static void validatePriceUpdateRequest(BindingResult bindingResults, Long productId, ProductDetails priceUpdateRequest) {
        if (bindingResults.hasErrors()) {
            throw new InvalidPriceUpdateException(bindingResults)
        } else if (productId != priceUpdateRequest.id) {
            throw new PreconditionFailedException(priceUpdateRequest.id)
        }
    }
}
