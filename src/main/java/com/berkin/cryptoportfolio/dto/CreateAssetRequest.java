package com.berkin.cryptoportfolio.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
public class CreateAssetRequest {
    private String cryptoType;
    private BigDecimal amount;
    private OffsetDateTime purchaseDate;
}
