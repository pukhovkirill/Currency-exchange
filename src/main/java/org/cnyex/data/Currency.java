package org.cnyex.data;

public class Currency {
    private final Long id;
    private final String code;
    private final String fullName;
    private final String sign;

    public Currency(Currency currency){
        this(currency.getId(), currency.getCode(),
                currency.getFullName(), currency.getSign());
    }

    public Currency(Long id, String code, String fullName, String sign){
        this.id = id;
        this.code = code;
        this.fullName = fullName;
        this.sign = sign;
    }

    public Long getId(){
        return this.id;
    }

    public String getCode(){
        return this.code;
    }

    public String getFullName(){
        return this.fullName;
    }

    public String getSign(){
        return this.sign;
    }
}