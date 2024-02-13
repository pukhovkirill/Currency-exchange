package org.cnyex.data;

import java.math.BigDecimal;

public class ExchangeRate {
    private final Long id;
    private final Currency baseCurrency;
    private final Currency targetCurrency;
    private final BigDecimal rate;

    public ExchangeRate(ExchangeRate exchangeRate){
        this(exchangeRate.getId(), exchangeRate.getBaseCurrency(),
                exchangeRate.getTargetCurrency(), exchangeRate.getRate());
    }

    public ExchangeRate(Long id, Currency baseCurrency, Currency targetCurrency, BigDecimal rate){
        this.id = id;
        this.baseCurrency = baseCurrency;
        this.targetCurrency = targetCurrency;
        this.rate = rate;
    }

    public Long getId(){
        return this.id;
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
}
