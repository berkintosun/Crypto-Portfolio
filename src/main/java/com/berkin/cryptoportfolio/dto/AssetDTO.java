package com.berkin.cryptoportfolio.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
public class AssetDTO {
    private long id;
    private String cryptoType;
    private BigDecimal amount;
    private OffsetDateTime purchaseDate;
    private BigDecimal marketValue;
}
