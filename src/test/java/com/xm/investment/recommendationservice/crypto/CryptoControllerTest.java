package com.xm.investment.recommendationservice.crypto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = "crypto.repository.path=./data-test/prices")
class CryptoControllerTest {

    @Autowired
    private MockMvc mvc;

    @Test
    void cryptos_btcWithHighNormalizedRangeThanEth_returnBtcFollowedByEth() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/cryptos").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("[\"BTC\",\"ETH\"]"));
    }

    @Test
    void cryptos_filterOnDateForWhichOnlyEthHasData_returnEth() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/cryptos?date=2022-01-02").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("[\"ETH\"]"));
    }

    @Test
    void cryptoBasicStatistics_forBtc_returnOldestAndNewestTimestampAndMinAndMaxValues() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/cryptos/BTC").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(
                        "{\"oldest\":\"2022-01-01T04:00:00Z\"," +
                                "\"newest\":\"2022-01-01T04:00:02Z\"," +
                                "\"min\":200.01," +
                                "\"max\":201}"));
    }

}
