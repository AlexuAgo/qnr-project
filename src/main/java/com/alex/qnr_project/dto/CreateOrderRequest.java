package com.alex.qnr_project.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CreateOrderRequest {

    private String description;
    private String status;
    private BigDecimal quantity;
}
