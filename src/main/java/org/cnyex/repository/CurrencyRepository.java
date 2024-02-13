package org.cnyex.repository;

import org.cnyex.data.Currency;
import org.cnyex.service.JDBCService;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class CurrencyRepository implements CrudLayer<Currency>{

    private final JDBCService jdbcService;

    public CurrencyRepository(JDBCService jdbcService){
        this.jdbcService = jdbcService;
    }

    @Override
    public Optional<Currency> findById(Long id) {
        Currency currency = null;
        String readStatement = "select * from currencies where id = ?";
        try(var connection = jdbcService.getConnection(); var statement = connection.prepareStatement(readStatement)){
            statement.setLong(1, id);
            var resultSet = statement.executeQuery();

            if(resultSet.next())
                currency = buildCurrency(resultSet);
        }catch(SQLException ex){
            jdbcService.showSQLException(ex);
        }

        return Optional.ofNullable(currency);
    }

    public Optional<Currency> findByName(String name){
        Currency currency = null;
        String readStatement = "select * from currencies where code = ?";
        try(var connection = jdbcService.getConnection(); var statement = connection.prepareStatement(readStatement)){
            statement.setString(1, name);
            var resultSet = statement.executeQuery();

            if(resultSet.next())
                currency = buildCurrency(resultSet);
        }catch(SQLException ex){
            jdbcService.showSQLException(ex);
        }

        return Optional.ofNullable(currency);
    }

    @Override
    public List<Currency> findAll() {
        List<Currency> currencies = new LinkedList<>();
        String readStatement = "select * from currencies";
        try(var connection = jdbcService.getConnection(); var statement = connection.prepareStatement(readStatement)){
            var resultSet = statement.executeQuery();

            while(resultSet.next())
                currencies.add(buildCurrency(resultSet));
        }catch(SQLException ex){
            jdbcService.showSQLException(ex);
        }

        return currencies;
    }

    @Override
    public boolean save(Currency entity) {
        String createStatement = "insert into currencies (id, code, fullname, sign) values (default,?,?,?)";
        try(var connection = jdbcService.getConnection(); var statement = connection.prepareStatement(createStatement)){
            statement.setString(1, entity.getCode());
            statement.setString(2, entity.getFullName());
            statement.setString(3, entity.getSign());

            return statement.execute();
        }catch(SQLException ex){
            jdbcService.showSQLException(ex);
        }

        return false;
    }

    @Override
    public boolean update(Currency entity) {
        String updateStatement = "update currencies set code = ?, fullname = ?, sign = ? where id = ?";
        try(var connection = jdbcService.getConnection(); var statement = connection.prepareStatement(updateStatement)){
            statement.setString(1, entity.getCode());
            statement.setString(2, entity.getFullName());
            statement.setString(3, entity.getSign());

            statement.setLong(4, entity.getId());

            return statement.execute();
        }catch (SQLException ex){
            jdbcService.showSQLException(ex);
        }

        return false;
    }

    @Override
    public boolean delete(Long id) {
        String deleteStatement = "delete from currencies where id = ?";
        try(var connection = jdbcService.getConnection(); var statement = connection.prepareStatement(deleteStatement)){
            statement.setLong(1, id);
            return statement.execute();
        }catch (SQLException ex){
            jdbcService.showSQLException(ex);
        }

        return false;
    }

    private Currency buildCurrency(ResultSet resultSet) throws SQLException {
        var modelId = resultSet.getLong(1);
        var modelCode = resultSet.getString(2);
        var modelFullName = resultSet.getString(3);
        var modelSign = resultSet.getString(4);

        return new Currency(modelId, modelCode, modelFullName, modelSign);
    }
}
