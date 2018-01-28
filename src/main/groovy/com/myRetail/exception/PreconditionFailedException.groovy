package com.myRetail.exception

import groovy.transform.CompileStatic

@CompileStatic
class PreconditionFailedException extends GenericException {

    PreconditionFailedException(Long id) {
        errorCode = "PRODUCT_ID_MISMATCH"
        params = [id] as String[]
    }
}
