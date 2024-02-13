package org.cnyex.servlet;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.cnyex.data.Currency;
import org.cnyex.repository.CurrencyRepository;
import org.cnyex.service.JsonService;
import org.cnyex.service.ValidationService;

import java.io.IOException;

@MultipartConfig
public class CurrenciesServlet extends HttpServlet {

    private CurrencyRepository currencyRepository;
    private final JsonService<Currency> jsonService = new JsonService<>();

    @Override
    public void init(ServletConfig config) throws ServletException {
        currencyRepository = (CurrencyRepository) config.getServletContext().getAttribute("currencyRepository");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        getResponse(resp);
    }

    private void getResponse(HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setStatus(resp.SC_OK);

        var currencies = currencyRepository.findAll();

        if(currencies == null){
            resp.sendError(resp.SC_INTERNAL_SERVER_ERROR, "Database unavailable");
            return;
        }

        var json = jsonService.listToJson(currencies);

        resp.getWriter().print(json);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        postResponse(req, resp);
    }

    private void postResponse(HttpServletRequest req, HttpServletResponse resp) throws IOException{
        resp.setContentType("application/json");
        resp.setStatus(resp.SC_CREATED);

        var name = req.getParameter("name");
        var code = req.getParameter("code");
        var sign = req.getParameter("sign");

        if(!ValidationService.isCurrenciesArgsValid(name, code, sign)){
            resp.sendError(resp.SC_BAD_REQUEST, "A required form field is missing or the data is incorrect");
            return;
        }

        if(currencyRepository.findByName(code).isPresent()){
            resp.sendError(resp.SC_CONFLICT, "A currency with this code already exists");
            return;
        }

        var currency = new Currency(0L, name, code, sign);
        var json = jsonService.toJson(currency);

        var result = currencyRepository.save(currency);

        if(!result){
            resp.sendError(resp.SC_INTERNAL_SERVER_ERROR, "Database unavailable");
            return;
        }

        resp.getWriter().print(json);
    }
}
