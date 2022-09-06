package com.crio.warmup.stock;

public class Trade {
    
    public String symbol;
    public int quantity;    
    public String tradeType;
    public String purchaseDate;

    public Trade() {

    }

    public Trade(String symbol, int quantity, String tradeType, String purchaseDate) {
        this.symbol = symbol;
        this.quantity = quantity;
        this.tradeType = tradeType;
        this.purchaseDate = purchaseDate;
    }

    @Override
    public String toString() {
        return "Trade [purchaseDate=" + purchaseDate + ", quantity=" + quantity + ", symbol="
                + symbol + ", tradeType=" + tradeType + "]";
    }
   
}
