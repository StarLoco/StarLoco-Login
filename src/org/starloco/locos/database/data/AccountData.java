package org.starloco.locos.database.data;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.LoggerFactory;
import org.starloco.locos.database.AbstractDAO;
import org.starloco.locos.database.Result;
import org.starloco.locos.object.Account;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AccountData extends AbstractDAO<Account> {

    public AccountData(HikariDataSource source) {
        super(source);
        logger = (Logger) LoggerFactory.getLogger("factory.Account");
        logger.setLevel(Level.OFF);
    }

    @Override
    public Account load(Object id) {
        Account account = null;
        try {
            String query = "SELECT * FROM `world_accounts` WHERE guid = " + id;
            Result result = super.getData(query);
            account = loadFromResultSet(result.resultSet);
            close(result);
            if (account != null) {
                query = "UPDATE `accounts` SET reload_needed = 0 WHERE guid = "
                        + id;
                super.execute(query);
            }
            logger.debug("Account with id {} successfully loaded", id);
        } catch (Exception e) {
            logger.error("Can't load account with guid" + id, e);
        }
        return account;
    }

    public Account load(String name) {
        Account account = null;
        try {
            String query = "SELECT * FROM `world_accounts` WHERE account LIKE '" + name + "';";
            Result result = super.getData(query);
            account = loadFromResultSet(result.resultSet);
            close(result);
            if (account != null) {
                logger.debug("Account with name {} successfully loaded", name);
            } else {
                logger.debug("Account with name {} failed to load", name);
            }
        } catch (Exception e) {
            logger.error("Can't load account with name " + name, e);
        }
        return account;
    }

    @Override
    public boolean update(Account obj) {
        try {
            String baseQuery = "UPDATE `world_accounts` SET account = '"
                    + obj.getName() + "', banned = '"
                    + (obj.isBanned() ? 1 : 0) + "', bannedTime = '"
                    + obj.getBannedTime() + "', pass = '" + obj.getPass() + "',"
                    + " pseudo = '" + obj.getPseudo() + "', question = '"
                    + obj.getQuestion() + "'," + " logged = '" + obj.getState()
                    + "'," + " subscribe = '" + obj.getSubscribe() + "'"
                    + " WHERE guid = '" + obj.getUUID() + "';";

            PreparedStatement statement = getPreparedStatement(baseQuery);
            execute(statement);

            return true;
        } catch (Exception e) {
            logger.error("SQL ERROR, trying rollback", e);
        }
        return false;
    }

    public String exist(String nickname) {
        String name = null;
        try {
            String query = "SELECT * FROM `world_accounts` WHERE pseudo = '" + nickname
                    + "';";
            Result result = super.getData(query);
            if (result.resultSet.next())
                name = result.resultSet.getString("account");
            close(result);
            logger.debug("Account with pseudo {} exist", nickname);
        } catch (Exception e) {
            logger.error("Can't load account with pseudo like {}" + nickname, e);
        }
        return name;
    }

    public void resetLogged(int guid, int server) {
        try {
            String baseQuery = "UPDATE `world_accounts` SET  logged = '0' WHERE guid = '"
                    + guid + "';";
            PreparedStatement statement = getPreparedStatement(baseQuery);
            execute(statement);

            baseQuery = "UPDATE players SET logged = '0' WHERE server = '"
                    + server + "' AND account = '" + guid + "';";
            statement = getPreparedStatement(baseQuery);
            execute(statement);

        } catch (Exception e) {
            logger.error("SQL ERROR, trying rollback", e);
        }
    }

    public boolean isBanned(String ip) {
        boolean banned = false;
        try {
            String query = "SELECT * FROM `administration_ban_ip` WHERE 'ip' LIKE '" + ip + "';";
            Result result = super.getData(query);
            ResultSet resultSet = result.resultSet;

            if (resultSet.next())
                banned = true;

            close(result);
        } catch (Exception e) {
            logger.error("Can't know if ip {} is banned", ip);
        }
        return banned;
    }

    Account loadFromResultSet(ResultSet resultSet)
            throws SQLException {
        if (resultSet.next())
            return new Account(resultSet.getInt("guid"), resultSet.getString("account").toLowerCase(), resultSet.getString("pass"), resultSet.getString("pseudo"), resultSet.getString("question"), resultSet.getByte("logged"), resultSet.getLong("subscribe"), resultSet.getByte("banned"), resultSet.getLong("bannedTime"));
        return null;
    }
}
