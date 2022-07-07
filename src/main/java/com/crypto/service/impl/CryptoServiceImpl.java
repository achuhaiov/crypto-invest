package com.crypto.service.impl;

import com.crypto.dto.CryptoRecord;
import com.crypto.dto.CryptoStatistic;
import com.crypto.dto.NormalizedCrypto;
import com.crypto.service.CryptoService;
import com.crypto.util.CryptoFileUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CryptoServiceImpl implements CryptoService {

    private static final String STATISTIC_DIRECTORY = "statistic";
    private static final String STATISTIC_FILE_NAME_SUFFIX = "_values.csv";

    @Override
    public List<NormalizedCrypto> getNormalizedCryptos() throws Exception {
        Set<String> fileNames = CryptoFileUtil.getFileNames(STATISTIC_DIRECTORY);
        Set<String> validatedFileNames = validateFileNames(fileNames);
        List<NormalizedCrypto> results = new ArrayList<>();

        for (String fileName : validatedFileNames) {
            List<CryptoRecord> cryptoStatistics = getCryptoRecordsByFileName(fileName);
            BigDecimal min = calculateMin(cryptoStatistics);
            BigDecimal max = calculateMax(cryptoStatistics);
            results.add(new NormalizedCrypto(StringUtils.substringBefore(fileName, STATISTIC_FILE_NAME_SUFFIX),
                    max.subtract(min, MathContext.DECIMAL64).divide(min, MathContext.DECIMAL64)));
        }

        return results.stream().sorted(Comparator.comparing(NormalizedCrypto::getNormalizedRange).reversed()).collect(Collectors.toList());
    }

    @Override
    public NormalizedCrypto getMaxNormalizedCrypto(LocalDate date) throws Exception {
        Set<String> fileNames = CryptoFileUtil.getFileNames(STATISTIC_DIRECTORY);
        Set<String> validatedFileNames = validateFileNames(fileNames);
        List<NormalizedCrypto> results = new ArrayList<>();

        for (String fileName : validatedFileNames) {
            List<CryptoRecord> cryptoStatistics = getCryptoRecordsByFileName(fileName);
            BigDecimal min = cryptoStatistics.stream().filter(cr -> compareTimestampDay(cr.getTimestamp(), date)).map(CryptoRecord::getPrice).min(BigDecimal::compareTo).orElse(BigDecimal.ZERO);
            BigDecimal max = cryptoStatistics.stream().filter(cr -> compareTimestampDay(cr.getTimestamp(), date)).map(CryptoRecord::getPrice).max(BigDecimal::compareTo).orElse(BigDecimal.ZERO);
            results.add(new NormalizedCrypto(StringUtils.substringBefore(fileName, STATISTIC_FILE_NAME_SUFFIX), max.subtract(min, MathContext.DECIMAL64).divide(min, MathContext.DECIMAL64)));
        }
        return results.stream().max(Comparator.comparing(NormalizedCrypto::getNormalizedRange)).get();
    }

    @Override
    public List<CryptoStatistic> getCryptoStatistics() throws Exception {
        Set<String> fileNames = CryptoFileUtil.getFileNames(STATISTIC_DIRECTORY);
        Set<String> validatedFileNames = validateFileNames(fileNames);
        List<CryptoStatistic> results = new ArrayList<>();
        for (String fileName : validatedFileNames) {
            List<CryptoRecord> cryptoStatistics = getCryptoRecordsByFileName(fileName);
            BigDecimal min = calculateMin(cryptoStatistics);
            BigDecimal max = calculateMax(cryptoStatistics);
            BigDecimal oldest = calculateOldest(cryptoStatistics);
            BigDecimal newest = calculateNewest(cryptoStatistics);
            results.add(new CryptoStatistic(StringUtils.substringBefore(fileName, STATISTIC_FILE_NAME_SUFFIX), oldest, newest, min, max));
        }
        return results;
    }

    @Override
    public CryptoStatistic getCryptoStatistic(String cryptoName) throws Exception {
        List<String> notSupportedCryptos = CryptoFileUtil.readNotSupportedCryptos();
        if (notSupportedCryptos.contains(cryptoName)) {
            throw new UnsupportedOperationException();
        }
        List<CryptoRecord> cryptoStatistics = getCryptoRecordsByCryptoName(cryptoName);
        BigDecimal min = calculateMin(cryptoStatistics);
        BigDecimal max = calculateMax(cryptoStatistics);
        BigDecimal oldest = calculateOldest(cryptoStatistics);
        BigDecimal newest = calculateNewest(cryptoStatistics);

        return new CryptoStatistic(cryptoName.toUpperCase(), oldest, newest, min, max);
    }

    @Override
    public List<String> getNotSupportedCryptos() throws IOException {
        return CryptoFileUtil.readNotSupportedCryptos();
    }

    @Override
    public void addNotSupportedCryptos(List<String> notSupportedCryptos) throws IOException {
        CryptoFileUtil.writeNotSupportedCryptos(notSupportedCryptos);
    }

    private Set<String> validateFileNames(Set<String> fileNames) throws Exception {
        List<String> notSupportedCryptos = CryptoFileUtil.readNotSupportedCryptos();
        Set<String> results = new HashSet<>();
        for (String fileName : fileNames) {
            if (!notSupportedCryptos.contains(StringUtils.substringBefore(fileName, STATISTIC_FILE_NAME_SUFFIX))) {
                results.add(fileName);
            }
        }
        return results;
    }

    private BigDecimal calculateMin(List<CryptoRecord> cryptoStatistics) {
        return cryptoStatistics.stream().map(CryptoRecord::getPrice).min(BigDecimal::compareTo).orElse(BigDecimal.ZERO);
    }

    private BigDecimal calculateMax(List<CryptoRecord> cryptoStatistics) {
        return cryptoStatistics.stream().map(CryptoRecord::getPrice).max(BigDecimal::compareTo).orElse(BigDecimal.ZERO);
    }

    private BigDecimal calculateOldest(List<CryptoRecord> cryptoStatistics) {
        return cryptoStatistics.stream().min(Comparator.comparing(CryptoRecord::getTimestamp)).orElse(new CryptoRecord()).getPrice();
    }

    private BigDecimal calculateNewest(List<CryptoRecord> cryptoStatistics) {
        return cryptoStatistics.stream().max(Comparator.comparing(CryptoRecord::getTimestamp)).orElse(new CryptoRecord()).getPrice();
    }

    private List<CryptoRecord> getCryptoRecordsByFileName(String fileName) throws Exception {
        return getCryptoRecordsByPath(STATISTIC_DIRECTORY + "/" + fileName);
    }

    private List<CryptoRecord> getCryptoRecordsByCryptoName(String cryptoName) throws Exception {
        return getCryptoRecordsByPath(STATISTIC_DIRECTORY + "/" + cryptoName.toUpperCase() + STATISTIC_FILE_NAME_SUFFIX);
    }

    private List<CryptoRecord> getCryptoRecordsByPath(String relativePath) throws Exception {
        return CryptoFileUtil.readCryptoStatistic(relativePath, true);
    }

    private boolean compareTimestampDay(Timestamp timestamp, LocalDate date) {
        LocalDateTime time1 = timestamp.toLocalDateTime();
        return time1.getYear() == date.getYear() && time1.getMonthValue() == date.getMonthValue() && time1.getDayOfMonth() == date.getDayOfMonth();
    }
}
