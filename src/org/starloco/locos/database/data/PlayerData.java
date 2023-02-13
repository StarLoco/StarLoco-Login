package org.starloco.locos.database.data;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.LoggerFactory;
import org.starloco.locos.database.AbstractDAO;
import org.starloco.locos.database.Result;
import org.starloco.locos.object.Account;
import org.starloco.locos.object.Player;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class PlayerData extends AbstractDAO<Player> {

    public PlayerData(HikariDataSource dataSource) {
        super(dataSource);
        logger = (Logger) LoggerFactory.getLogger("factory.player");
        logger.setLevel(Level.OFF);
    }

    public Player load(Object obj) {
        try {
            if (obj instanceof Account) {
                Account account = (Account) obj;
                Result result = getData("SELECT * FROM `world_players` WHERE account = "
                        + account.getUUID());
                ResultSet resultSet = result.resultSet;
                while (resultSet.next()) {
                    account.addPlayer(new Player(resultSet.getInt("id"), resultSet.getInt("server"), resultSet.getInt("groupe")));
                }
                close(result);
                logger.info("Players loaded for account {}", account.getUUID());
            }
        } catch (Exception e) {
            logger.error("Can't load players for account {}", ((Account) obj).getUUID(), e);
        }
        return null;
    }

    @Override
    public boolean update(Player obj) {
        return false;
    }

    public int isLogged(Account account) {
        int logged = 0;
        try {
            Result result = getData("SELECT * FROM `world_players` WHERE account = "
                    + account.getUUID());
            ResultSet resultSet = result.resultSet;
            while (resultSet.next()) {
                if (resultSet.getInt("logged") == 1)
                    logged = resultSet.getInt("server");
            }
            close(result);
            logger.info("Players load for account {}", account.getUUID());
        } catch (Exception e) {
            logger.error("Can't load players for account {}", account.getUUID(), e);
        }
        return logged;
    }

    public void setState(Account account) {
        try {
            String baseQuery = "UPDATE `world_players` SET logged = '" + 0
                    + "' WHERE account = '" + account.getUUID() + "';";
            PreparedStatement statement = getPreparedStatement(baseQuery);
            execute(statement);
            logger.info("New state for account {}", account.getUUID());
        } catch (Exception e) {
            logger.error("Can't change state for account {}", account.getUUID(), e);
        }
    }
}
