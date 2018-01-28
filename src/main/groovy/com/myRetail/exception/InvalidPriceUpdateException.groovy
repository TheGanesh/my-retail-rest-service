package com.myRetail.exception

import groovy.transform.CompileStatic
import org.springframework.validation.BindingResult

@CompileStatic
class InvalidPriceUpdateException extends GenericException {

    InvalidPriceUpdateException(BindingResult bindingResults) {
        errorCode = "INVALID_PRICE_UPDATE"
        params = [bindingResults?.fieldErrors?.first()?.field, bindingResults?.fieldErrors?.first()?.defaultMessage] as String[]
    }
}
