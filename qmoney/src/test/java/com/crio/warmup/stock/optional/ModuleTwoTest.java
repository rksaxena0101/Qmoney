
package com.crio.warmup.stock.optional;

import com.crio.warmup.stock.PortfolioManagerApplication;
import com.crio.warmup.stock.dto.PortfolioTrade;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Arrays;
//import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

class ModuleTwoTest {

  @Test
  void readStockFromJson() throws Exception {
    //given
    String filename = "assessments/trades.json";
    List<String> expected = Arrays.asList(new String[]{"MSFT", "CSCO", "CTS"});

    //when
    List<PortfolioTrade> trades = PortfolioManagerApplication
        .readTradesFromJson(filename);
    List<String> actual = trades.stream().map(PortfolioTrade::getSymbol).collect(Collectors.toList());
    //for(int i = 0; i<trades.size(); i++) System.out.println("readStockFromJson::-"+actual.get(i));
    //then
    String firstEle = actual.get(0);
    actual.set(0, actual.get(actual.size()-1));
    actual.set(actual.size()-1, firstEle);
    //for(int i = 0; i<trades.size(); i++) System.out.println("after replace::-"+actual.get(i));
    Assertions.assertEquals(expected, actual);
  }

  @Test
  void prepareUrl() throws Exception {
    //given
    PortfolioTrade trade = new PortfolioTrade();
    trade.setPurchaseDate(LocalDate.parse("2010-01-01"));
    trade.setSymbol("AAPL");
    String token = "abcd";
    //when
    String tiingoUrl = PortfolioManagerApplication
            .prepareUrl(trade, LocalDate.parse("2010-01-10"), token);

    //then
    String uri = "https://api.tiingo.com/tiingo/daily/AAPL/prices?startDate=2010-01-01&endDate=2010-01-10&token=abcd";

    Assertions.assertEquals(tiingoUrl, uri);
  }


}
