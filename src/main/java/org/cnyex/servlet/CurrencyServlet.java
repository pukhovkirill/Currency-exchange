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
import org.cnyex.utils.Utils;

import java.io.IOException;

@MultipartConfig
public class CurrencyServlet extends HttpServlet {

    private CurrencyRepository currencyRepository;
    private final JsonService<Currency> jsonService = new JsonService<>();

    @Override
    public void init(ServletConfig config) throws ServletException {
        currencyRepository = (CurrencyRepository) config.getServletContext().getAttribute("currencyRepository");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        var code = Utils.getCodeFromUri(req.getRequestURI());

        getResponse(code, resp);
    }

    private void getResponse(String code, HttpServletResponse resp) throws IOException{
        resp.setContentType("application/json");
        resp.setStatus(resp.SC_OK);

        if(!ValidationService.isCodeValid(code)){
            resp.sendError(resp.SC_BAD_REQUEST, "The currency code is incorrect or missing");
            return;
        }

        var currency = currencyRepository.findByName(code);

        if(currency.isEmpty()){
            resp.sendError(resp.SC_NOT_FOUND, "Currency not found");
            return;
        }

        var json = jsonService.toJson(currency.get());

        resp.getWriter().print(json);
    }
}
