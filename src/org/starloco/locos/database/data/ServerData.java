package org.starloco.locos.database.data;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.LoggerFactory;
import org.starloco.locos.database.AbstractDAO;
import org.starloco.locos.database.Result;
import org.starloco.locos.object.Server;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ServerData extends AbstractDAO<Server> {

    public ServerData(HikariDataSource dataSource) {
        super(dataSource);
        logger = (Logger) LoggerFactory.getLogger("factory.Server");
        logger.setLevel(Level.OFF);
    }

    @Override
    public Server load(Object obj) {
        try {
            String query = "SELECT * FROM world_servers;";
            Result result = super.getData(query);
            ResultSet resultSet = result.resultSet;

            while (resultSet.next())
                new Server(resultSet.getInt("id"), resultSet.getString("key"), resultSet.getInt("isSubscriberServer"));

            close(result);
            logger.debug("Servers successfully loaded");
        } catch (SQLException e) {
            logger.debug("Can't load server");
        }
        return null;
    }

    @Override
    public boolean update(Server obj) {
        return false;
    }
}
