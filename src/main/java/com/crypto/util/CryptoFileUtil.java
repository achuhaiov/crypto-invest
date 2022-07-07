package com.crypto.util;

import com.crypto.CryptoInvestmentApplication;
import com.crypto.dto.CryptoRecord;
import org.springframework.util.CollectionUtils;

import java.io.*;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CryptoFileUtil {

    private static final String SEPARATOR = ",";
    private static final String NOT_SUPPORTED_CRYPTOS_FILE = "not-supported-cryptos.csv";

    public static Set<String> getFileNames(String directory) throws URISyntaxException {
        URL statisticUrl = CryptoInvestmentApplication.class.getClassLoader().getResource(directory);
        return Stream.of(Objects.requireNonNull(
                        new File(Objects.requireNonNull(statisticUrl).toURI()).listFiles()))
                .map(File::getName)
                .collect(Collectors.toSet());
    }

    public static List<CryptoRecord> readCryptoStatistic(String relativePath, boolean containsHeader) throws IOException {
        List<CryptoRecord> results = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(
                Objects.requireNonNull(CryptoInvestmentApplication.class.getClassLoader().getResource(relativePath)).getFile()
        ));
        if (containsHeader) {
            reader.readLine();
        }

        String line = reader.readLine();
        while (line != null) {
            String[] rowValues = line.split(SEPARATOR);
            Timestamp ts = new Timestamp(Long.parseLong(rowValues[0]));
            results.add(new CryptoRecord(
                    ts, rowValues[1], new BigDecimal(rowValues[2])));
            line = reader.readLine();
        }

        return results;
    }

    public static void writeNotSupportedCryptos(List<String> notSupportedCryptos) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(
                Objects.requireNonNull(CryptoInvestmentApplication.class.getClassLoader().getResource(NOT_SUPPORTED_CRYPTOS_FILE))
                        .getFile(), false))) {
            writer.write(CollectionUtils.isEmpty(notSupportedCryptos) ? "" :
                    notSupportedCryptos.stream().map(String::toUpperCase).collect(Collectors.joining(SEPARATOR)));
        }
    }

    public static List<String> readNotSupportedCryptos() throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(
                Objects.requireNonNull(CryptoInvestmentApplication.class.getClassLoader().getResource(NOT_SUPPORTED_CRYPTOS_FILE))
                        .getFile()));
        String line = reader.readLine();
        return line == null ? Collections.emptyList() : List.of(line.split(SEPARATOR));
    }
}
