package com.myRetail.exception

import groovy.transform.CompileStatic

@CompileStatic
class GenericException extends RuntimeException{

    String errorCode
    Object[] params

}
