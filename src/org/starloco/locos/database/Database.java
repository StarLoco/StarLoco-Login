package org.starloco.locos.database;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.LoggerFactory;
import org.starloco.locos.database.data.AccountData;
import org.starloco.locos.database.data.PlayerData;
import org.starloco.locos.database.data.ServerData;
import org.starloco.locos.kernel.Config;
import org.starloco.locos.kernel.Main;

import java.sql.Connection;

public class Database {
    private static final Logger logger = (Logger) LoggerFactory.getLogger(Database.class);
    private HikariDataSource dataSource;
    private AccountData accountData;
    private PlayerData playerData;
    private ServerData serverData;

    void initializeData() {
        this.accountData = new AccountData(dataSource);
        this.playerData = new PlayerData(dataSource);
        this.serverData = new ServerData(dataSource);
    }

    public void initializeConnection() {
        logger.setLevel(Level.ALL);
        logger.trace("Reading database config");

        HikariConfig config = new HikariConfig();

        config.setDataSourceClassName("com.mysql.jdbc.jdbc2.optional.MysqlDataSource");
        //config.setDataSourceClassName("org.mariadb.jdbc.MySQLDataSource");
        config.addDataSourceProperty("serverName", Config.databaseHost);
        config.addDataSourceProperty("port", Config.databasePort);
        config.addDataSourceProperty("databaseName", Config.databaseName);
        config.addDataSourceProperty("user", Config.databaseUser);
        config.addDataSourceProperty("password", Config.databasePass);

        System.out.println("est");
        dataSource = new HikariDataSource(config);
        System.out.println("est");
        if (!testConnection(dataSource)) {
            logger.error("Please check your username and password and database connection");
            Main.exit();
            return;
        }
        logger.info("Database connection established");
        this.initializeData();
    }

    public AccountData getAccountData() {
        return accountData;
    }

    public PlayerData getPlayerData() {
        return playerData;
    }

    public ServerData getServerData() {
        return serverData;
    }

    private boolean testConnection(HikariDataSource dataSource) {
        try {
            Connection connection = dataSource.getConnection();
            connection.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
