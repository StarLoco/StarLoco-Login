package org.starloco.locos.kernel;

import org.starloco.locos.exchange.ExchangeServer;
import org.starloco.locos.login.LoginServer;

import java.io.*;
import java.util.Properties;

public class Config {
    //Config
    public static final long startTime = System.currentTimeMillis();

    //emulator
    public static boolean isRunning;
    public static LoginServer loginServer;
    public static ExchangeServer exchangeServer;

    //database
    public static String databaseHost, databaseUser, databasePass;
    public static int databasePort;
    public static String databaseName;

    //network
    public static String exchangeIp, version;
    public static int loginPort, exchangePort;
    public static String exchangeKey;

    public static void verify(String name) {
        if(new File(name).exists()) load(name);
        else create(name);
    }

    private static void load(String name) {
        Properties properties = new Properties();
        FileInputStream fileInputStream;

        try { fileInputStream = new FileInputStream(name);
        } catch(Exception e) {
            Console.instance.write(" > Config : file not found !");
            verify(name);
            return;
        }
        try { properties.load(fileInputStream);
        } catch(Exception e) {
            Console.instance.write(" > Config : can't load file !");
            verify(name);
            return;
        }
        try { fileInputStream.close();
        } catch (IOException e) {
            Console.instance.write(" > Config : can't close file !");
            verify(name);
            return;
        }

        int i = 5;

        try {
            Config.exchangeIp = properties.getProperty(Params.EXCHANGE_IP.toString()); i = 6;
            Config.exchangePort = Integer.parseInt(properties.getProperty(Params.EXCHANGE_PORT.toString()));i = 9;
            Config.exchangeKey = properties.getProperty(Params.EXCHANGE_KEY.toString()); i = 6;
            Config.loginPort = Integer.parseInt(properties.getProperty(Params.PORT.toString())); i++;
            Config.version = properties.getProperty(Params.VERSION.toString()); i = 12;
            Config.databaseHost = properties.getProperty(Params.LOGIN_DB_HOST.toString()); i++;
            Config.databasePort = Integer.parseInt(properties.getProperty(Params.LOGIN_DB_PORT.toString())); i++;
            Config.databaseUser = properties.getProperty(Params.LOGIN_DB_USER.toString()); i++;
            Config.databasePass = properties.getProperty(Params.LOGIN_DB_PASS.toString()); i++;
            Config.databaseName = properties.getProperty(Params.LOGIN_DB_NAME.toString()); i++;
        } catch(Exception e) {
            Console.instance.write(" > Config : not found or invalid parameters! (line " + i + ")");
            verify(name);
            return;
        }

        Console.instance.write(" > Config : config loaded with success !");
    }

    private static void create(String name) {
        Console.instance.write(" > Config : the configuration file don't exist !");
        BufferedWriter config;

        try { config = new BufferedWriter(new FileWriter(name, true));
        } catch (IOException e) {
            Console.instance.write(" > Config : error on creating !");
            verify(name);
            return;
        }

        Console.instance.write(" > Config : the configuration file was created with successful !");
        write(name, config);
    }

    private static void write(String name, BufferedWriter config) {
        if(config == null) {
            Console.instance.write(" > Config : config is null !");
            verify(name);
            return;
        }

        StringBuilder sb = new StringBuilder();

        sb
            .append("# StarLoco - Login. By Locos.\n")
            .append("#Auto-Generated Config File\n")
            .append("\n")
            .append("#System\n")
            .append(Params.EXCHANGE_IP).append(" 127.0.0.1\n")
            .append(Params.EXCHANGE_PORT).append(" 666\n")
            .append(Params.EXCHANGE_KEY).append(" ChangeMeYn2kjibddFAWtnPJ2AFlL8WXmohJMCvigQggaEypa5E\n")
            .append("\n")
            .append("#Login server\n")
            .append(Params.PORT).append(" 450\n")
            .append(Params.VERSION).append(" 1.37.10\n")
            .append("\n")
            .append(Params.LOGIN_DB_HOST).append(" 127.0.0.1\n")
            .append(Params.LOGIN_DB_PORT).append(" 3306\n")
            .append(Params.LOGIN_DB_USER).append(" root\n")
            .append(Params.LOGIN_DB_PASS).append(" \n")
            .append(Params.LOGIN_DB_NAME).append(" starloco_login\n");
        
        try {
            config.write(sb.toString());
            config.flush();
            config.close();
        } catch (IOException e) {
            Console.instance.write(" > Config : error on writing !");
            verify(name);
            return;
        }

        waiting(name);
    }

    private static void waiting(String name) {
        for(int i = 0; i < 10; i++) {
            Console.instance.write(" > Config : please, set the config properties, reloading into " + (10 - i) + "s.");
            try { Thread.sleep(1000);
            } catch (InterruptedException e) { e.printStackTrace(); }
        }
        verify(name);
    }

    public static boolean isVersionGreaterThan(String version) {
        return Config.version.compareTo(version) >= 0;
    }
    
    private enum Params {
        EXCHANGE_PORT("system.server.exchange.port"),
        EXCHANGE_IP("system.server.exchange.ip"),
        EXCHANGE_KEY("system.server.exchange.key"),
        PORT("system.server.login.port"),
        VERSION("system.server.login.version"),
        LOGIN_DB_HOST("database.login.host"),
        LOGIN_DB_PORT("database.login.port"),
        LOGIN_DB_USER("database.login.user"),
        LOGIN_DB_PASS("database.login.pass"),
        LOGIN_DB_NAME("database.login.name");

        private final String params;

        Params(String params){
            this.params = params;
        }

        public String toString(){
            return params;
        }
    }
}
