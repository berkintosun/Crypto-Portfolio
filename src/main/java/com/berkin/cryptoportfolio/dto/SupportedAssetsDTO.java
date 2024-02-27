package com.berkin.cryptoportfolio.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SupportedAssetsDTO {
    @JsonProperty("id")
    private String name;
    @JsonProperty("name")
    private String displayName;
}
