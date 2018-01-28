package com.myRetail.contract;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductDetails {

  @ApiModelProperty(value = "Product Id", example = "13860428",required = true)
  @NotNull
  private Long id;

  @ApiModelProperty(value = "Product Name", example = "The Big Lebowski (Blu-ray)",required = true)
  @NotNull
  private String name;

  @ApiModelProperty(value = "Product Current Price",required = true)
  @NotNull
  @Valid
  private CurrentPrice current_price;


  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public CurrentPrice getCurrent_price() {
    return current_price;
  }

  public void setCurrent_price(CurrentPrice current_price) {
    this.current_price = current_price;
  }

}
