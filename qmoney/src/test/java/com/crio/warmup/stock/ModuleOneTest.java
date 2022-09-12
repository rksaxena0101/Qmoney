
package com.crio.warmup.stock;

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ModuleOneTest {

  @Test
  void mainReadFile() throws Exception {
    //given
    String filename = "assessments/trades.json";
    List<String> expected = Arrays.asList(new String[]{"CTS", "CSCO", "MSFT"});

    //when
    List<String> results = PortfolioManagerApplication
        .mainReadFile(new String[]{filename});
        //for(int i = 0; i < results.size(); i++) System.out.println("Trades symbols:::- " + results.get(i));
    //then
    Assertions.assertEquals(expected, results);
  }

  @Test
  void mainReadFileEdgecase() throws Exception {
    //given
    String filename = "assessments/empty.json";
    List<String> expected = Arrays.asList(new String[]{});

    //when
    List<String> results = PortfolioManagerApplication
        .mainReadFile(new String[]{filename});

    //then
    Assertions.assertEquals(expected, results);
  }

}
