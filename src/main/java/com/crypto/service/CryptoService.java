package com.crypto.service;

import com.crypto.dto.CryptoStatistic;
import com.crypto.dto.NormalizedCrypto;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public interface CryptoService {

    List<NormalizedCrypto> getNormalizedCryptos() throws Exception;

    NormalizedCrypto getMaxNormalizedCrypto(LocalDate date) throws Exception;

    List<CryptoStatistic> getCryptoStatistics() throws Exception;

    CryptoStatistic getCryptoStatistic(String cryptoName) throws Exception;

    List<String> getNotSupportedCryptos() throws IOException;

    void addNotSupportedCryptos(List<String> notSupportedCryptos) throws IOException;

}
