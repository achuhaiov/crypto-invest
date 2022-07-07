package com.crypto.controller;

import com.crypto.dto.CryptoStatistic;
import com.crypto.dto.NormalizedCrypto;
import com.crypto.dto.NotSupportedCryptos;
import com.crypto.service.impl.CryptoServiceImpl;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class CryptoController {
    private final CryptoServiceImpl cryptoStatisticService;

    public CryptoController(CryptoServiceImpl cryptoStatisticService) {
        this.cryptoStatisticService = cryptoStatisticService;
    }

    @ApiOperation(value = "Return a descending sorted list of all the cryptos, comparing the normalized range (i.e. (max-min)/min)",
            notes = "Return a descending sorted list of all the cryptos, comparing the normalized range (i.e. (max-min)/min)")
    @GetMapping("/crypto")
    public List<NormalizedCrypto> getCryptos() throws Exception {
        return cryptoStatisticService.getNormalizedCryptos();
    }

    @ApiOperation(value = "Return the crypto with the highest normalized range for a specific day",
            notes = "Return the crypto with the highest normalized range for a specific day")
    @ApiImplicitParam(name = "date", value = "2022-01-04", required = true, dataType = "Date", paramType = "query")
    @GetMapping("/crypto/maxNormalizedRange")
    public NormalizedCrypto getMaxNormalizedRangeCrypto(@RequestParam("date")
                                                        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) throws Exception {
        return cryptoStatisticService.getMaxNormalizedCrypto(date);
    }

    @ApiOperation(value = "Return the oldest/newest/min/max values for a requested crypto",
            notes = "Return the oldest/newest/min/max values for a requested crypto")
    @ApiImplicitParam(name = "cryptoName", value = "BTC", required = true, dataType = "String", paramType = "path")
    @GetMapping("/crypto/statistic/{cryptoName}")
    public CryptoStatistic getCryptoStatistic(@PathVariable String cryptoName) throws Exception {
        return cryptoStatisticService.getCryptoStatistic(cryptoName);
    }

    @ApiOperation(value = "Return oldest/newest/min/max for each crypto for the whole month",
            notes = "Return oldest/newest/min/max for each crypto for the whole month")
    @GetMapping("/crypto/statistic")
    public List<CryptoStatistic> getCryptoStatistic() throws Exception {
        return cryptoStatisticService.getCryptoStatistics();
    }

    @ApiOperation(value = "Get not supported cryptos",
            notes = "Get not supported cryptos")
    @GetMapping("/crypto/notSupported")
    public List<String> getNotSupportedCryptos() throws Exception {
        return cryptoStatisticService.getNotSupportedCryptos();
    }

    @ApiOperation(value = "Init not supported cryptos",
            notes = "Init not supported cryptos")
    @ApiImplicitParams({ @ApiImplicitParam(name = "notSupportedCryptos",
            value = "List of strings", paramType = "body", dataType = "NotSupportedCryptos") })
    @PostMapping("/crypto/notSupported")
    public void getNotSupportedCryptos(@RequestBody NotSupportedCryptos notSupportedCryptos) throws Exception {
        cryptoStatisticService.addNotSupportedCryptos(notSupportedCryptos.getNotSupportedCryptos());
    }
}
