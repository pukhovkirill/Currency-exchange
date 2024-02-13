package org.cnyex.data;

import java.math.BigDecimal;

public class Exchange {
    private final Currency baseCurrency;
    private final Currency targetCurrency;
    private final BigDecimal rate;
    private final BigDecimal amount;
    private final BigDecimal convertedAmount;

    public Exchange(Exchange exchange){
        this(exchange.getBaseCurrency(), exchange.getTargetCurrency(),
                exchange.getRate(), exchange.getAmount(), exchange.getConvertedAmount());
    }

    public Exchange(Currency baseCurrency, Currency targetCurrency, BigDecimal rate, BigDecimal amount, BigDecimal convertedAmount){
        this.baseCurrency = baseCurrency;
        this.targetCurrency = targetCurrency;
        this.rate = rate;
        this.amount = amount;
        this.convertedAmount = convertedAmount;
    }

    public Currency getBaseCurrency(){
        return this.baseCurrency;
    }

    public Currency getTargetCurrency(){
        return this.targetCurrency;
    }

    public BigDecimal getRate(){
        return this.rate;
    }

    public BigDecimal getAmount(){
        return this.amount;
    }

    public BigDecimal getConvertedAmount(){
        return this.convertedAmount;
    }
}
