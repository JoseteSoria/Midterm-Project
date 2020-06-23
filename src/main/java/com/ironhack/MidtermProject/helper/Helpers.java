package com.ironhack.MidtermProject.helper;

import com.ironhack.MidtermProject.exceptions.CurrencyTypeException;
import com.ironhack.MidtermProject.model.classes.Money;

import java.math.BigDecimal;
import java.util.Currency;

public class Helpers {
    public static Money convertMoney(Money toConvert, Money reference){
        if(!toConvert.getCurrency().getCurrencyCode().equals("USD") && !toConvert.getCurrency().getCurrencyCode().equals("EUR")){
            throw new CurrencyTypeException("Only currency accepted: EUR, USD");
        }
        if(toConvert.getCurrency().equals(reference.getCurrency())){
            return toConvert;
        }
        else if(toConvert.getCurrency().getCurrencyCode().equals("USD") && reference.getCurrency().getCurrencyCode().equals("EUR")){
            Money result = new Money(toConvert.getAmount().divide(new BigDecimal("1.12")).setScale(3),Currency.getInstance("EUR"));
            return result;
        }
        else if(toConvert.getCurrency().getCurrencyCode().equals("EUR") && reference.getCurrency().getCurrencyCode().equals("USD")){
            Money result = new Money(toConvert.getAmount().multiply(new BigDecimal("1.12")).setScale(3),Currency.getInstance("USD"));
            return result;
        }
        else{
            throw new CurrencyTypeException("Check currency references");
        }
    }
}
