package org.cnyex.servlet;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.cnyex.data.Exchange;
import org.cnyex.data.ExchangeRate;
import org.cnyex.repository.ExchangeRateRepository;
import org.cnyex.service.JsonService;
import org.cnyex.service.ValidationService;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

@MultipartConfig
public class ExchangeServlet extends HttpServlet {
    private ExchangeRateRepository exchangeRateRepository;
    private final JsonService<Exchange> exchangeJsonService = new JsonService<>();

    @Override
    public void init(ServletConfig config) throws ServletException {
        exchangeRateRepository = (ExchangeRateRepository) config.getServletContext().getAttribute("exchangeRateRepository");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        var base = req.getParameter("from");
        var target = req.getParameter("to");

        var amount = req.getParameter("amount");
        var decimalAmount = BigDecimal.valueOf(Double.parseDouble(amount));

        getResponse(base, target, decimalAmount, resp);
    }

    private void getResponse(String base, String target, BigDecimal amount, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setStatus(resp.SC_OK);

        if(!ValidationService.isCodeValid(base) || !ValidationService.isCodeValid(target) || amount == null){
            resp.sendError(resp.SC_BAD_REQUEST, "A required form field is missing or the data is incorrect");
            return;
        }

        var exchangeRate = findExchangeRate(base, target);

        if(exchangeRate.isEmpty()){
            resp.sendError(resp.SC_NOT_FOUND, "Could not find a suitable exchange rate");
            return;
        }

        var exchange = buildExchange(exchangeRate.get(), amount);
        var json = exchangeJsonService.toJson(exchange);

        resp.getWriter().print(json);
    }

    private Exchange buildExchange(ExchangeRate exchangeRate, BigDecimal amount){
        var convertedAmount = amount.multiply(exchangeRate.getRate());

        return new Exchange(exchangeRate.getBaseCurrency(),
                exchangeRate.getTargetCurrency(), exchangeRate.getRate(), amount, convertedAmount);
    }

    private Optional<ExchangeRate> findExchangeRate(String base, String target){
        var exchangeRate = exchangeRateRepository.findByCodes(base, target);

        if(exchangeRate.isEmpty())
            exchangeRate = findReverseExchangeRate(base, target);

        if(exchangeRate.isEmpty())
            exchangeRate = findTransitiveExchangeRate(base, target);

        return exchangeRate;
    }

    private Optional<ExchangeRate> findReverseExchangeRate(String base, String target){
        var reverseExchangeRate = exchangeRateRepository.findByCodes(target, base);

        ExchangeRate exchangeRate = null;

        if(reverseExchangeRate.isPresent()){
            var baseCurrency = reverseExchangeRate.get().getTargetCurrency();
            var targetCurrency = reverseExchangeRate.get().getBaseCurrency();
            var rate = reverseRate(reverseExchangeRate.get().getRate());

            exchangeRate = new ExchangeRate(0L, baseCurrency, targetCurrency, rate);
        }

        return Optional.ofNullable(exchangeRate);
    }

    private Optional<ExchangeRate> findTransitiveExchangeRate(String base, String target){
        var transfer = "USD";

        ExchangeRate resultExchangeRate = null;

        var transferToBase = exchangeRateRepository.findByCodes(transfer, base);
        var transferToTarget = exchangeRateRepository.findByCodes(transfer, target);

        if(transferToBase.isPresent() && transferToTarget.isPresent()){
            var baseCurrency = transferToBase.get().getTargetCurrency();
            var targetCurrency = transferToTarget.get().getBaseCurrency();
            var resultRate = transferRate(transferToBase.get().getRate(), transferToTarget.get().getRate());

            resultExchangeRate = new ExchangeRate(0L, baseCurrency, targetCurrency, resultRate);
        }

        return Optional.ofNullable(resultExchangeRate);
    }

    private BigDecimal reverseRate(BigDecimal rate){
        var one = BigDecimal.valueOf(1);
        return one.divide(rate, 2, RoundingMode.HALF_UP);
    }

    private BigDecimal transferRate(BigDecimal baseRate, BigDecimal targetRate){
        var result = reverseRate(baseRate);
        return result.multiply(targetRate);
    }
}
