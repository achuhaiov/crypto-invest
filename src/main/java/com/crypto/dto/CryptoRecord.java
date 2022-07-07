package com.crypto.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CryptoRecord {
    private Timestamp timestamp;
    private String cryptoName;
    private BigDecimal price;
}
