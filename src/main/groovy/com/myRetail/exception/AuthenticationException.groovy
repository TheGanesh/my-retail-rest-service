package com.myRetail.exception

import groovy.transform.CompileStatic

@CompileStatic
class AuthenticationException extends GenericException {

   AuthenticationException(String clientSecret) {
        errorCode = "INVALID_CREDENTIALS"
        params = [clientSecret] as String[]
    }
}
