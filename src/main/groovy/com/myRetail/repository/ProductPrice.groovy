package com.myRetail.repository

import groovy.transform.CompileStatic
import org.springframework.cassandra.core.PrimaryKeyType
import org.springframework.data.cassandra.mapping.Column
import org.springframework.data.cassandra.mapping.PrimaryKeyColumn
import org.springframework.data.cassandra.mapping.Table
import org.springframework.data.cassandra.repository.MapId
import org.springframework.data.cassandra.repository.MapIdentifiable
import org.springframework.data.cassandra.repository.support.BasicMapId

import java.time.LocalDateTime

@CompileStatic
@Table("product_price")
class ProductPrice implements MapIdentifiable {

    @PrimaryKeyColumn(name = "product_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    Long productId

    @PrimaryKeyColumn(name = "currency_code", ordinal = 1, type = PrimaryKeyType.PARTITIONED)
    String currencyCode

    @PrimaryKeyColumn(name = "updated_time")
    LocalDateTime updatedTime

    @Column("product_name")
    String productName

    @Column("current_price")
    BigDecimal currentPrice

    @Override
    MapId getMapId() {
        return BasicMapId
                .id('productId', productId)
                .with('currencyCode', currencyCode)
    }
}
