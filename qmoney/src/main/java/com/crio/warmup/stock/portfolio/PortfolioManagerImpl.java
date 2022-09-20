
package com.crio.warmup.stock.portfolio;

import com.crio.warmup.stock.dto.AnnualizedReturn;
import com.crio.warmup.stock.dto.Candle;
import com.crio.warmup.stock.dto.PortfolioTrade;
import com.crio.warmup.stock.dto.TiingoCandle;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.web.client.RestTemplate;

public class PortfolioManagerImpl implements PortfolioManager {
  private RestTemplate restTemplate;
  
  // Caution: Do not delete or modify the constructor, or else your build will break!
  // This is absolutely necessary for backward compatibility
  protected PortfolioManagerImpl(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  //TODO: CRIO_TASK_MODULE_REFACTOR
  // 1. Now we want to convert our code into a module, so we will not call it from main anymore.
  //    Copy your code from Module#3 PortfolioManagerApplication#calculateAnnualizedReturn
  //    into #calculateAnnualizedReturn function here and ensure it follows the method signature.
  // 2. Logic to read Json file and convert them into Objects will not be required further as our
  //    clients will take care of it, going forward.

  // Note:
  // Make sure to exercise the tests inside PortfolioManagerTest using command below:
  // ./gradlew test --tests PortfolioManagerTest

  //CHECKSTYLE:OFF
  @Override
  public List<AnnualizedReturn> calculateAnnualizedReturn(List<PortfolioTrade> portfolioTrades, LocalDate endDate) {
      List<AnnualizedReturn> ls = new ArrayList<AnnualizedReturn>();
      for(PortfolioTrade each: portfolioTrades) {
        List<Candle> Candles;
        try {
          Candles = getStockQuote(each.getSymbol(), each.getPurchaseDate(), endDate);
          ls.add(getAnnualizedReturn(endDate, each, Candles.get(0).getOpen(), Candles.get(Candles.size()-1).getClose()));
        } catch (JsonProcessingException e) {
          e.printStackTrace();
        }
      }
      return ls.stream().sorted(getComparator()).collect(Collectors.toList());
    }

  public AnnualizedReturn getAnnualizedReturn(LocalDate endDate, PortfolioTrade trade, Double buyPrice, Double sellPrice) {
    double total_returns = (sellPrice - buyPrice) / buyPrice;
    LocalDate startDate = trade.getPurchaseDate();
    double year = startDate.until(endDate, ChronoUnit.DAYS)/365.24;
    Double annualized_returns = Math.pow((1+total_returns), (1/year)) - 1;
    return new AnnualizedReturn(trade.getSymbol(), annualized_returns, total_returns);
  }

   //CHECKSTYLE:OFF

  // TODO: CRIO_TASK_MODULE_REFACTOR
  //  Extract the logic to call Tiingo third-party APIs to a separate function.
  //  Remember to fill out the buildUri function and use that.

  public List<Candle> getStockQuote(String symbol, LocalDate from, LocalDate to) throws JsonProcessingException {
    RestTemplate rt = new RestTemplate();
    String Url = buildUri(symbol, from, to);
    TiingoCandle[] tc = rt.getForObject(Url, TiingoCandle[].class);
    return Arrays.asList(tc);
  }

  private Comparator<AnnualizedReturn> getComparator() {
    return Comparator.comparing(AnnualizedReturn::getAnnualizedReturn).reversed();
  }

  protected String buildUri(String symbol, LocalDate startDate, LocalDate endDate) {
    String uriTemplate = "https:api.tiingo.com/tiingo/daily/$SYMBOL/prices?"
            + "startDate=$STARTDATE&endDate=$ENDDATE&token=$APIKEY";
            return uriTemplate;
  }

}
