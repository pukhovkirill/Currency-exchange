package org.cnyex.servlet;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.cnyex.data.ExchangeRate;
import org.cnyex.repository.CurrencyRepository;
import org.cnyex.repository.ExchangeRateRepository;
import org.cnyex.service.JsonService;
import org.cnyex.service.ValidationService;

import java.io.IOException;
import java.math.BigDecimal;

@MultipartConfig
public class ExchangeRatesServlet extends HttpServlet {
    private ExchangeRateRepository exchangeRateRepository;
    private CurrencyRepository currencyRepository;
    private final JsonService<ExchangeRate> exchangeRateJsonService = new JsonService<>();

    @Override
    public void init(ServletConfig config) throws ServletException {
        exchangeRateRepository = (ExchangeRateRepository) config.getServletContext().getAttribute("exchangeRateRepository");
        currencyRepository = (CurrencyRepository) config.getServletContext().getAttribute("currencyRepository");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        getResponse(resp);
    }

    private void getResponse(HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setStatus(resp.SC_OK);

        var exchangeRates = exchangeRateRepository.findAll();

        if(exchangeRates == null){
            resp.sendError(resp.SC_INTERNAL_SERVER_ERROR, "Database unavailable");
            return;
        }

        var json = exchangeRateJsonService.listToJson(exchangeRates);

        resp.getWriter().print(json);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        var base = req.getParameter("baseCurrencyCode");
        var target = req.getParameter("targetCurrencyCode");

        var rate = req.getParameter("rate");
        var decimalRate = BigDecimal.valueOf(Double.parseDouble(rate));

        postResponse(base, target, decimalRate, resp);
    }

    private void postResponse(String base, String target, BigDecimal rate, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setStatus(resp.SC_CREATED);

        if(!ValidationService.isCodeValid(base) || !ValidationService.isCodeValid(target)){
            resp.sendError(resp.SC_BAD_REQUEST, "A required form field is missing or the data is incorrect");
            return;
        }

        var baseCurrency = currencyRepository.findByName(base);
        var targetCurrency = currencyRepository.findByName(target);

        if(baseCurrency.isEmpty() || targetCurrency.isEmpty()){
            resp.sendError(resp.SC_NOT_FOUND, "One (or both) currencies from the currency pair does not exist in the database.");
            return;
        }

        if(exchangeRateRepository.findByCodes(base, target).isPresent()){
            resp.sendError(HttpServletResponse.SC_CONFLICT, "A currency pair with this code already exists");
            return;
        }

        var exchangeRate = new ExchangeRate(0L, baseCurrency.get(), targetCurrency.get(), rate);
        var json = exchangeRateJsonService.toJson(exchangeRate);

        var result = exchangeRateRepository.save(exchangeRate);

        if(!result){
            resp.sendError(resp.SC_INTERNAL_SERVER_ERROR, "Database unavailable");
            return;
        }

        resp.getWriter().print(json);
    }
}
