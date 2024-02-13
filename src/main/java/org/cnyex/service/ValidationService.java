package org.cnyex.service;

public class ValidationService {
    public static boolean isCurrenciesArgsValid(String code, String name, String sign){
        if(code == null || name == null || sign == null)
            return false;
        
        if(code.isEmpty() || name.isEmpty() || sign.isEmpty())
            return false;
        
        if(code.length() != 3 || name.length() < 3 || name.length() > 100 || sign.length() >5)
            return false;
        
        return true;
    }

    public static boolean isCodeValid(String code){
        if(code == null)
            return false;

        if(code.isEmpty())
            return false;

        if(code.length() != 3)
            return false;

        return true;
    }

    public static boolean isCodesPair(String codes){
        if(codes == null)
            return false;

        if(codes.isEmpty())
            return false;

        if(codes.length() != 6)
            return false;

        for(var ch : codes.toCharArray())
            if(!Character.isAlphabetic(ch))
                return false;

        return true;
    }

    public static boolean isExchangeArgsValid(String base, String target, String rate){
        if(base == null || target == null || rate == null)
            return false;

        if(base.isEmpty() || target.isEmpty() || rate.isEmpty())
            return false;

        if(base.length() != 3 || target.length() != 3)
            return false;

        return true;
    }
}
