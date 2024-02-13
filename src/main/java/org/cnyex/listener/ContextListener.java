package org.cnyex.listener;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import org.cnyex.repository.CurrencyRepository;
import org.cnyex.repository.ExchangeRateRepository;
import org.cnyex.service.JDBCPostgresService;
import org.cnyex.service.JDBCService;

import java.io.IOException;
import java.util.Properties;

@WebListener
public class ContextListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        var context = sce.getServletContext();

        var jdbcService = createJDBCService(context);

        var currencyRepository = new CurrencyRepository(jdbcService);
        var exchangeRateRepository = new ExchangeRateRepository(jdbcService);

        context.setAttribute("currencyRepository", currencyRepository);
        context.setAttribute("exchangeRateRepository", exchangeRateRepository);
    }

    private JDBCService createJDBCService(ServletContext context){
        JDBCService jdbcService;
        try {
            Properties properties = new Properties();
            properties.load(context.getResourceAsStream("WEB-INF/properties/db.properties"));

            var jdbcUrl = properties.getProperty("db.url");
            var jdbcUsername = properties.getProperty("db.username");
            var jdbcPassword = properties.getProperty("db.password");

            jdbcService = new JDBCPostgresService(jdbcUrl, jdbcUsername, jdbcPassword);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return jdbcService;
    }
}
