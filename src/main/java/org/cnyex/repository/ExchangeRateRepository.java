package org.cnyex.repository;

import org.cnyex.data.Currency;
import org.cnyex.data.ExchangeRate;
import org.cnyex.service.JDBCService;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class ExchangeRateRepository implements CrudLayer<ExchangeRate>{

    private final JDBCService jdbcService;

    public ExchangeRateRepository(JDBCService jdbcService){
        this.jdbcService = jdbcService;
    }

    @Override
    public Optional<ExchangeRate> findById(Long id) {
        ExchangeRate exchangeRate = null;
        String readStatement =
                "select er.id, c.id, c.code, c.fullname, c.sign, " +
                "c2.id, c2.code, c2.fullname, c2.sign, er.rate from exchange_rates er " +
                "join currencies c on c.id = base_currency_id " +
                "join currencies c2 on c2.id = target_currency_id " +
                "where er.id = ?";
        try(var connection = jdbcService.getConnection(); var statement = connection.prepareStatement(readStatement)){
            statement.setLong(1, id);
            var resultSet = statement.executeQuery();

            if(resultSet.next())
                exchangeRate = buildExchangeRate(resultSet);
        }catch(SQLException ex){
            jdbcService.showSQLException(ex);
        }

        return Optional.ofNullable(exchangeRate);
    }

    public Optional<ExchangeRate> findByCodes(String baseCode, String targetCode){
        ExchangeRate exchangeRate = null;
        String readStatement =
                "select er.id, c.id, c.code, c.fullname, c.sign, " +
                        "c2.id, c2.code, c2.fullname, c2.sign, er.rate from exchange_rates er " +
                        "join currencies c on c.id = base_currency_id " +
                        "join currencies c2 on c2.id = target_currency_id " +
                        "where c.code = ? and c2.code = ?";
        try(var connection = jdbcService.getConnection(); var statement = connection.prepareStatement(readStatement)){
            statement.setString(1, baseCode);
            statement.setString(2, targetCode);
            var resultSet = statement.executeQuery();

            if(resultSet.next())
                exchangeRate = buildExchangeRate(resultSet);
        }catch(SQLException ex){
            jdbcService.showSQLException(ex);
        }

        return Optional.ofNullable(exchangeRate);
    }

    @Override
    public List<ExchangeRate> findAll() {
        List<ExchangeRate> rates = new LinkedList<>();
        String readStatement =
                "select er.id, c.id, c.code, c.fullname, c.sign, " +
                "c2.id, c2.code, c2.fullname, c2.sign, er.rate from exchange_rates er " +
                "join currencies c on c.id = base_currency_id " +
                "join currencies c2 on c2.id = target_currency_id";
        try(var connection = jdbcService.getConnection(); var statement = connection.prepareStatement(readStatement)){
            var resultSet = statement.executeQuery();

            while(resultSet.next())
                rates.add(buildExchangeRate(resultSet));
        }catch(SQLException ex){
            jdbcService.showSQLException(ex);
        }

        return rates;
    }

    @Override
    public boolean save(ExchangeRate entity) {
        String createStatement = "insert into exchange_rates (id, base_currency_id, target_currency_id, rate) " +
                "values (default,?,?,?)";
        try(var connection = jdbcService.getConnection(); var statement = connection.prepareStatement(createStatement)){
            statement.setLong(1, entity.getBaseCurrency().getId());
            statement.setLong(2, entity.getTargetCurrency().getId());
            statement.setBigDecimal(3, entity.getRate());

            return statement.execute();
        }catch(SQLException ex){
            jdbcService.showSQLException(ex);
        }

        return false;
    }

    @Override
    public boolean update(ExchangeRate entity) {
        String updateStatement = "update exchange_rates set base_currency_id = ?, " +
                "target_currency_id = ?, rate = ? where id = ?";
        try(var connection = jdbcService.getConnection(); var statement = connection.prepareStatement(updateStatement)){
            statement.setLong(1, entity.getBaseCurrency().getId());
            statement.setLong(2, entity.getTargetCurrency().getId());
            statement.setBigDecimal(3, entity.getRate());

            statement.setLong(4, entity.getId());

            return statement.execute();
        }catch (SQLException ex){
            jdbcService.showSQLException(ex);
        }

        return false;
    }

    @Override
    public boolean delete(Long id) {
        String deleteStatement = "delete from exchange_rates where id = ?";
        try(var connection = jdbcService.getConnection(); var statement = connection.prepareStatement(deleteStatement)){
            statement.setLong(1, id);
            return statement.execute();
        }catch (SQLException ex){
            jdbcService.showSQLException(ex);
        }

        return false;
    }

    private ExchangeRate buildExchangeRate(ResultSet resultSet) throws SQLException{
        var modelId = resultSet.getLong(1);

        var modelBaseId = resultSet.getLong(2);
        var modelBaseCode = resultSet.getString(3);
        var modelBaseFullname = resultSet.getString(4);
        var modelBaseSign = resultSet.getString(5);

        var modelTargetId = resultSet.getLong(6);
        var modelTargetCode = resultSet.getString(7);
        var modelTargetFullname = resultSet.getString(8);
        var modelTargetSign = resultSet.getString(9);

        var modelRate = resultSet.getBigDecimal(10);

        var modelBaseCurrency = new Currency(modelBaseId, modelBaseCode, modelBaseFullname, modelBaseSign);
        var modelTargetCurrency = new Currency(modelTargetId, modelTargetCode, modelTargetFullname, modelTargetSign);

        return new ExchangeRate(modelId, modelBaseCurrency, modelTargetCurrency, modelRate);
    }
}
