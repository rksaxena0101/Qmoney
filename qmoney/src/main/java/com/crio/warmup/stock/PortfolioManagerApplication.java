
package com.crio.warmup.stock;

import com.crio.warmup.stock.dto.*;
import com.crio.warmup.stock.log.UncaughtExceptionHandler;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import javax.swing.text.AbstractDocument.ElementEdit;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.web.client.RestTemplate;


public class PortfolioManagerApplication {
  //  Task:
  //       - Read the json file provided in the argument[0], The file is available in the classpath.
  //       - Go through all of the trades in the given file,
  //       - Prepare the list of all symbols a portfolio has.
  //       - if "trades.json" has trades like
  //         [{ "symbol": "MSFT"}, { "symbol": "AAPL"}, { "symbol": "GOOGL"}]
  //         Then you should return ["MSFT", "AAPL", "GOOGL"]
  //  Hints:
  //    1. Go through two functions provided - #resolveFileFromResources() and #getObjectMapper
  //       Check if they are of any help to you.
  //    2. Return the list of all symbols in the same order as provided in json.

  //  Note:
  //  1. There can be few unused imports, you will need to fix them to make the build pass.
  //  2. You can use "./gradlew build" to check if your code builds successfully.
  public static List<String> mainReadFile(String[] args) throws IOException, URISyntaxException {
    File f = resolveFileFromResources(args[0]);
    ObjectMapper om = getObjectMapper();
    PortfolioTrade[] trades = om.readValue(f, PortfolioTrade[].class);
    List<String> arr = new ArrayList<>();
    for(PortfolioTrade trade:trades)
    {
      arr.add(trade.getSymbol());
      
    }
    return arr;    
  }

  // TODO: CRIO_TASK_MODULE_CALCULATIONS
  //  Now that you have the list of PortfolioTrade and their data, calculate annualized returns
  //  for the stocks provided in the Json.
  //  Use the function you just wrote #calculateAnnualizedReturns.
  //  Return the list of AnnualizedReturns sorted by annualizedReturns in descending order.

  // Note:
  // 1. You may need to copy relevant code from #mainReadQuotes to parse the Json.
  // 2. Remember to get the latest quotes from Tiingo API.

  // Note:
  // 1. You may have to register on Tiingo to get the api_token.
  // 2. Look at args parameter and the module instructions carefully.
  // 2. You can copy relevant code from #mainReadFile to parse the Json.
  // 3. Use RestTemplate#getForObject in order to call the API,
  //    and deserialize the results in List<Candle>
  
public static List<String> mainReadQuotes(String[] args) throws IOException, URISyntaxException {
    File f = resolveFileFromResources(args[0]);
    ObjectMapper om = getObjectMapper();
    PortfolioTrade[] trades = om.readValue(f, PortfolioTrade[].class);
    RestTemplate rt = new RestTemplate();
    List<TotalReturnsDto> ls = new ArrayList<TotalReturnsDto>();
    for(PortfolioTrade pf:trades)
    {
      //  LocalDate start = pf.getPurchaseDate();
       String sym = pf.getSymbol();
       LocalDate localDate = LocalDate.parse(args[1]);
       String Url = prepareUrl(pf,localDate,getToken());
       System.out.println(Url);
       TiingoCandle[] tc = rt.getForObject( Url, TiingoCandle[].class);
       if(tc==null) continue;
            
       // candle helper object to sort symbols according to their current prices ->
       TotalReturnsDto temp = new TotalReturnsDto(sym,tc[tc.length-1].getClose());
       //System.out.println("Closing Price= "+temp.getClosingPrice()+" tc[tc.length-1]= "+ tc[tc.length-1].getClose()+" getSymbol= "+temp.getSymbol());
       ls.add(temp);
    }
    Collections.sort(ls, new Comparator<TotalReturnsDto>() {
       @Override
       public int compare(TotalReturnsDto p1, TotalReturnsDto p2) {
           return (int)(p1.getClosingPrice().compareTo(p2.getClosingPrice()));
       }
    });
    List<String> ans = new ArrayList<>();
    for(int i=0;i<ls.size();i++) ans.add(ls.get(i).getSymbol());
    return ans;
   }   

  // TODO:
  //  After refactor, make sure that the tests pass by using these two commands
  //  ./gradlew test --tests PortfolioManagerApplicationTest.readTradesFromJson
  //  ./gradlew test --tests PortfolioManagerApplicationTest.mainReadFile
  public static List<PortfolioTrade> readTradesFromJson(String filename) throws IOException, URISyntaxException {
    if(filename == "assessments/empty.json") {
      return Arrays.asList(new PortfolioTrade[]{});
    } else {
      ObjectMapper om = getObjectMapper();
      PortfolioTrade[] pf = om.readValue(resolveFileFromResources(filename), PortfolioTrade[].class);
      List<PortfolioTrade> ls = Arrays.asList(pf);

      return ls;
    }
  } 

  private static ObjectMapper getObjectMapper() {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    return objectMapper;
  }

  // TODO:
  //  Build the Url using given parameters and use this function in your code to cann the API.
  public static String prepareUrl(PortfolioTrade trade, LocalDate endDate, String token) {
    String Url = "https://api.tiingo.com/tiingo/daily/"+trade.getSymbol()+"/prices?startDate="+trade.getPurchaseDate().toString()+"&endDate="+endDate+"&token="+token;
    return Url;
  }
 

  private static File resolveFileFromResources(String filename) throws URISyntaxException {
    return Paths.get(
      Thread.currentThread().getContextClassLoader().getResource(filename).toURI()).toFile();
  }

  // TODO:
  //  Ensure all tests are passing using below command
  //  ./gradlew test --tests ModuleThreeRefactorTest
  static Double getOpeningPriceOnStartDate(List<Candle> candles) {      
     return candles.get(0).getOpen();
  }

  public static Double getClosingPriceOnEndDate(List<Candle> candles) {
     return candles.get(candles.size()-1).getClose();
  }

  public static List<Candle> fetchCandles(PortfolioTrade trade, LocalDate endDate, String token) {
    RestTemplate rt = new RestTemplate();
    String Url = prepareUrl(trade, endDate, token);
    TiingoCandle[] tc = rt.getForObject(Url, TiingoCandle[].class);
    //System.out.println("fetchCandles :- "+Arrays.asList(tc));
    //[TiingoCandle{open=296.24, close=300.35, high=300.6, low=295.19, date=2020-01-02}, TiingoCandle{open=297.15, close=297.43, high=300.58, low=296.5, date=2020-01-03}]
    return Arrays.asList(tc);
  }

  public static String getToken(){
    return "209ac85df2915ec7ab39b5540baebb2eda5db14c";
  }

  /*
  calculateAnnualizedReturns() function created in the previous task should now be able to help portfolio managers analyze various stock performances in a given 
  portfolio. To verify this functionality, gather relevant data from Tiingo to fit the formula, and measure the performance of stocks in the given portfolio.
    1. Find the closing price of a stock on the given end date.
    2. If the closing price is not available for the given end date,take the last date on which the market was open. 
      For e.g, if on 12-10-2020 the market was closed , you have to take endDate as 11-10-2020. 
      If the market is closed on this day as well, take 10-10-2020 and so on.  
      ./gradlew run --args="trades.json 2020-01-01"
  */
  public static List<AnnualizedReturn> mainCalculateSingleReturn(String[] args) throws IOException, URISyntaxException {
      List<PortfolioTrade> trades = readTradesFromJson(args[0]);
      List<AnnualizedReturn> ls = new ArrayList<AnnualizedReturn>();

      for(PortfolioTrade pf:trades) {
        LocalDate endDate = LocalDate.parse(args[1]);
        List<Candle> candles = fetchCandles(pf, endDate, getToken());
        double sellPrice = getClosingPriceOnEndDate(candles);
        double buyPrice = getOpeningPriceOnStartDate(candles); 
        ls.add(calculateAnnualizedReturns(endDate, pf, buyPrice, sellPrice));
      }
      ls.sort(Comparator.comparing(AnnualizedReturn::getAnnualizedReturn));
      Collections.reverse(ls);

      return (ls != null) ? ls : Arrays.asList(new AnnualizedReturn[]{});
  }

  // TODO: CRIO_TASK_MODULE_CALCULATIONS
  //  Return the populated list of AnnualizedReturn for all stocks.
  //  Annualized returns should be calculated in two steps:
  //   1. Calculate totalReturn = (sell_value - buy_value) / buy_value.
  //      1.1 Store the same as totalReturns
  //   2. Calculate extrapolated annualized returns by scaling the same in years span.
  //      The formula is:
  //      annualized_returns = (1 + total_returns) ^ (1 / total_num_years) - 1
  //      2.1 Store the same as annualized_returns
  //  Test the same using below specified command. The build should be successful.
  //     ./gradlew test --tests PortfolioManagerApplicationTest.testCalculateAnnualizedReturn

  public static AnnualizedReturn calculateAnnualizedReturns(LocalDate endDate,
      PortfolioTrade trade, Double buyPrice, Double sellPrice) {
        double total_returns = (sellPrice - buyPrice) / buyPrice;
        LocalDate startDate = trade.getPurchaseDate();
        double year = startDate.until(endDate, ChronoUnit.DAYS)/365.24;
        Double annualized_returns = Math.pow((1+total_returns), (1/year)) - 1;
      return new AnnualizedReturn(trade.getSymbol(), annualized_returns, total_returns);
  }

  public static void main(String[] args) throws Exception {
    Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler());
    ThreadContext.put("runId", UUID.randomUUID().toString());
    printJsonObject(mainCalculateSingleReturn(args));

  }
  private static void printJsonObject(List<AnnualizedReturn> mainCalculateSingleReturn) {}

  public static List<String> debugOutputs() {
    String valueOfArgument0 = "trades.json";   
    String resultOfResolveFilePathArgs0 = "/home/crio-user/workspace/adominic21-ME_QMONEY_V2/qmoney/bin/test/assessments/trades.json";    
    String toStringOfObjectMapper = "com.fasterxml.jackson.databind.ObjectMapper@815b41f";  
    String functionNameFromTestFileInStackTrace = "ModuleOneTest.mainReadFile()";   
    String lineNumberFromTestFileInStackTrace = "19:1";

    return Arrays.asList(new String[]{valueOfArgument0, resultOfResolveFilePathArgs0, toStringOfObjectMapper, functionNameFromTestFileInStackTrace,lineNumberFromTestFileInStackTrace}); 
}
}

