package com.crypto.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel
public class NotSupportedCryptos {
    @ApiModelProperty(name = "notSupportedCryptos", dataType = "List", example = "[\"BTC\"]")
    private List<String> notSupportedCryptos;
}
