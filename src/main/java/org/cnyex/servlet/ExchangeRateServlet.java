package org.cnyex.servlet;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.cnyex.data.ExchangeRate;
import org.cnyex.repository.ExchangeRateRepository;
import org.cnyex.service.JsonService;
import org.cnyex.service.ValidationService;
import org.cnyex.utils.Utils;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Optional;

@MultipartConfig
public class ExchangeRateServlet extends HttpServlet {
    private ExchangeRateRepository exchangeRateRepository;
    private final JsonService<ExchangeRate> exchangeRateJsonService = new JsonService<>();

    @Override
    public void init(ServletConfig config) throws ServletException {
        exchangeRateRepository = (ExchangeRateRepository) config.getServletContext().getAttribute("exchangeRateRepository");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        var codes = Utils.getCodeFromUri(req.getRequestURI());

        getResponse(codes, resp);
    }

    private void getResponse(String codes, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setStatus(resp.SC_OK);

        if(!ValidationService.isCodesPair(codes)){
            resp.sendError(resp.SC_BAD_REQUEST, "The currency code pairs are incorrect or missing");
            return;
        }

        var exchangeRate = findExchangeRate(codes);

        if(exchangeRate.isEmpty()){
            resp.sendError(resp.SC_NOT_FOUND, "Exchange rate for pair not found");
            return;
        }

        var json = exchangeRateJsonService.toJson(exchangeRate.get());

        resp.getWriter().print(json);
    }

    private Optional<ExchangeRate> findExchangeRate(String codes){
        var baseCode = codes.substring(0,3);
        var targetCode = codes.substring(3,6);

        return exchangeRateRepository.findByCodes(baseCode, targetCode);
    }

    @Override
    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        var uri = req.getRequestURI();
        var codes = uri.split("/")[2];

        var rate = req.getParameter("rate");
        var decimalRate = BigDecimal.valueOf(Double.parseDouble(rate));

        patchResponse(codes, decimalRate, resp);
    }

    private void patchResponse(String codes, BigDecimal rate, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setStatus(resp.SC_OK);

        if(rate == null){
            resp.sendError(resp.SC_BAD_REQUEST, "A required form field is missing");
            return;
        }

        if(!ValidationService.isCodesPair(codes)){
            resp.sendError(resp.SC_BAD_REQUEST, "The currency code pairs are incorrect or missing");
            return;
        }

        var exchangeRate = findExchangeRate(codes);

        if(exchangeRate.isEmpty()){
            resp.sendError(resp.SC_NOT_FOUND, "Exchange rate for pair not found");
            return;
        }

        var updateExchangeRate = updateExchangeRate(exchangeRate.get(), rate);
        var json = exchangeRateJsonService.toJson(updateExchangeRate);

        if(updateExchangeRate == null){
            resp.sendError(resp.SC_INTERNAL_SERVER_ERROR, "Database unavailable");
            return;
        }

        resp.getWriter().print(json);
    }

    private ExchangeRate updateExchangeRate(ExchangeRate old, BigDecimal newRate){
        var exchangeRate = new ExchangeRate(old.getId(), old.getBaseCurrency(), old.getTargetCurrency(), newRate);

        var result = exchangeRateRepository.update(exchangeRate);

        return !result ? null : exchangeRate;
    }
}