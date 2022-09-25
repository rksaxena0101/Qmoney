
package com.crio.warmup.stock.dto;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import java.time.LocalDate;
import java.util.Map;
import com.crio.warmup.stock.dto.AlphavantageCandle;

// @Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AlphavantageDailyResponse {

  @JsonProperty(value = "Time Series (Daily)")
  private Map<LocalDate, AlphavantageCandle> candles;

  @JsonGetter
  public Map<LocalDate, AlphavantageCandle> getCandles() {
    return candles;
  }

  @JsonSetter
  public void setCandles(
      Map<LocalDate, AlphavantageCandle> candles) {
    this.candles = candles;
  }
}
