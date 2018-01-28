package com.myRetail.service

import com.fasterxml.jackson.databind.ObjectMapper
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

import javax.annotation.PostConstruct

/**
 * This service fetches product information by id from RedSky REST API.
 */
@Service
@Slf4j
class ProductCatalogService {

    @Value('${redSky.base.url}')
    String redSkyBaseUrl

    @Value('${redSky.product.path}')
    private String redSkyProductPath

    @Value('${redSky.connect.timeout}')
    private int connectTimeout

    @Value('${redSky.read.timeout}')
    private int readTimeout

    @Autowired
    RestTemplateBuilder restTemplateBuilder

    @Autowired
    ObjectMapper objectMapper

    RestTemplate redSkyRestTemplate

    @PostConstruct
    void initRedSkyRestTemplate() {
        redSkyRestTemplate = restTemplateBuilder.setConnectTimeout(connectTimeout).setReadTimeout(readTimeout).build()
    }

    @Cacheable(cacheNames = "productNameCache", key = "#productId")
    String getProductName(Long productId) {
        Map productInfo = redSkyRestTemplate.getForObject(redSkyBaseUrl + redSkyProductPath, Map.class, productId)
        log.info("RedSkyResponseFor $productId:${objectMapper.writeValueAsString(productInfo)}")
        return productInfo?.product?.item?.product_description?.title
    }

}
