package com.myRetail.exception

import groovy.transform.CompileStatic

@CompileStatic
class ResourceNotFoundException extends GenericException {

    ResourceNotFoundException(String productId) {
        errorCode = "PRODUCT_NOT_FOUND"
        params = [productId] as String[]
    }
}
