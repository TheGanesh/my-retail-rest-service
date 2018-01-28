package com.myRetail.repository

import groovy.transform.CompileStatic
import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.stereotype.Repository

@CompileStatic
@Repository
interface ProductPriceRepository extends CassandraRepository<ProductPrice> {
}
