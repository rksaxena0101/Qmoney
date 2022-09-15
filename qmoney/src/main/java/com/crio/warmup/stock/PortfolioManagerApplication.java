
package com.crio.warmup.stock;

import com.crio.warmup.stock.dto.*;
import com.crio.warmup.stock.log.UncaughtExceptionHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.time.LocalDate;
//import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
//import java.util.logging.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.web.client.RestTemplate;


public class PortfolioManagerApplication {
  // TODO: CRIO_TASK_MODULE_JSON_PARSING
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
    List<String> lst = new ArrayList<String>();
    ObjectMapper om = getObjectMapper();
    PortfolioTrade[] trades = om.readValue(f, PortfolioTrade[].class);
    for(PortfolioTrade pf:trades) lst.add(pf.getSymbol());

    return (lst == null) ? Arrays.asList(new String[]{}) : lst;
  }


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
       String token="209ac85df2915ec7ab39b5540baebb2eda5db14c";
       String Url = prepareUrl(pf,localDate,token);
       System.out.println(Url);
       TiingoCandle[] tc = rt.getForObject( Url, TiingoCandle[].class);
       if(tc==null)
       {
         continue;
       }           // candle helper object to sort symbols according to their current prices ->
       TotalReturnsDto temp = new TotalReturnsDto(sym,tc[tc.length-1].getClose());
       System.out.println("Closing Price= "+temp.getClosingPrice()+" tc[tc.length-1]= "+ tc[tc.length-1].getClose()+" getSymbol= "+temp.getSymbol());
       ls.add(temp);
    }
    Collections.sort(ls, new Comparator<TotalReturnsDto>() {
       @Override
       public int compare(TotalReturnsDto p1, TotalReturnsDto p2) {
           return (int)(p1.getClosingPrice().compareTo(p2.getClosingPrice()));
       }
    });
    List<String> ans = new ArrayList<>();
    for(int i=0;i<ls.size();i++)
    {
       ans.add(ls.get(i).getSymbol());
    }
    return ans;
   }
   

  // TODO:
  //  After refactor, make sure that the tests pass by using these two commands
  //  ./gradlew test --tests PortfolioManagerApplicationTest.readTradesFromJson
  //  ./gradlew test --tests PortfolioManagerApplicationTest.mainReadFile
  public static List<PortfolioTrade> readTradesFromJson(String filename) throws IOException, URISyntaxException {
    ObjectMapper om = getObjectMapper();
    PortfolioTrade[] pf = om.readValue(resolveFileFromResources(filename), PortfolioTrade[].class);
    List<PortfolioTrade> ls = Arrays.asList(pf);

    //System.out.println("Inside read Trades from JSON::-"+ls);
    return ls; 
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

  public static void main(String[] args) throws Exception {
    Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler());
    ThreadContext.put("runId", UUID.randomUUID().toString());


    printJsonObject(mainReadQuotes(args));
    

  }

  private static void printJsonObject(List<String> mainReadQuotes) {  }
  
  public static List<String> debugOutputs() {    
    //System.out.println(this.fileNameNew);
    return Arrays.asList("trades.json");
  }
}

