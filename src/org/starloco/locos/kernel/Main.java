package org.starloco.locos.kernel;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;
import org.starloco.locos.database.Database;
import org.starloco.locos.exchange.ExchangeServer;
import org.starloco.locos.login.LoginServer;
import org.starloco.locos.object.Server;

import java.util.Timer;
import java.util.TimerTask;

public class Main {

    public static final Database database = new Database();
    private static final Logger logger = (Logger) LoggerFactory.getLogger(Main.class);

    static {
        System.setProperty("logback.configurationFile", "logback.xml");
        Runtime.getRuntime().addShutdownHook(new Thread(Main::exit));
    }

    public static void main(String[] arg) {
        start();
    }

    private static void start() {
        logger.info("You use " + System.getProperty("java.vendor") + " with the version " + System.getProperty("java.version"));
        Console.instance = new Console();

        Logging.getInstance().initialize();
        Logging.getInstance().write("Login", "starting");

        logger.debug(EmulatorInfo.HARD_NAME.toString());
        Config.verify("login.config.properties");

        Main.database.initializeConnection();
        logger.setLevel(Level.OFF);

        Console.instance.write(" > Creation of connexion server.");

        Main.database.getServerData().load(null);

        Config.exchangeServer = new ExchangeServer();
        Config.exchangeServer.start();

        Config.loginServer = new LoginServer();
        Config.loginServer.start();

        ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) org.slf4j.LoggerFactory.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
        root.setLevel(Level.OFF);

        Console.instance.write(" > The login server started in " + (System.currentTimeMillis() - Config.startTime) + " ms.");
        Config.isRunning = true;
        Console.instance.initialize();

        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Server.servers.values().stream().filter(server -> server != null && server.getClient() != null).forEach(Server::setFreePlaces);
            }
        }, 1000 * 30, 1000 * 30);

    }

    public static void exit() {
        Console.instance.write(" > The server going to be closed.");
        Logging.getInstance().write("Login", "exiting");
        Logging.getInstance().stop();

        if (Config.isRunning) {
            Config.isRunning = false;
            Config.loginServer.stop();
            Config.exchangeServer.stop();
            Console.instance.interrupt();
        }

        Console.instance.write(" > The emulator is now closed.");
    }
}