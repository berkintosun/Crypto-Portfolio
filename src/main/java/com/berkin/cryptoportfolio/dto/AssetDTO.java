package com.berkin.cryptoportfolio.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
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
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy/MM/dd'T'HH:mm:ssXXX", timezone = "UTC")
    private OffsetDateTime purchaseDate;
    private BigDecimal marketValue;
}
