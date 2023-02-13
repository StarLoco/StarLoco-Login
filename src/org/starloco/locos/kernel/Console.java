package org.starloco.locos.kernel;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;
import org.starloco.locos.login.LoginClient;
import org.starloco.locos.tool.packetfilter.VPNAuthorization;

import java.util.NoSuchElementException;
import java.util.Scanner;

public class Console extends Thread {

    public static Console instance;
    private static final Logger logger = (Logger) LoggerFactory.getLogger(Console.class);
    private final Scanner scanner = new Scanner(System.in);

    public void initialize() {
        super.setDaemon(true);
        super.start();
        logger.setLevel(Level.ALL);
        this.write(" > Console >");
    }

    @Override
    public void run() {
        while (Config.isRunning) {
            try {
                this.parse(scanner.nextLine());
            } catch (NoSuchElementException ignored) {}
        }
    }

    void parse(String line) {
        if(!line.isEmpty()) {
            char space = ' ';
            String[] infos = line.split(String.valueOf(space));

            if(infos.length > 1) {
                switch (infos[0].toUpperCase()) {
                    case "VPN":
                        VPNAuthorization.getInstance().toggle();
                        break;
                    case "AUTHORIZED":
                        Config.loginServer.authorizedIp.add(infos[1]);
                        this.write(" > You've authorized the IP : " + infos[1] + " !");
                        break;
                    case "MAINTAIN":
                        String name = infos[1];
                        if(Config.loginServer.containsMaintainAccount(name))
                            Config.loginServer.removeMaintainAccount(name);
                        else
                            Config.loginServer.addMaintainAccount(infos[1]);
                        this.write(" > You've maintain the account : " + infos[1] + " !");
                        break;

                    case "UPTIME":
                        this.write(EmulatorInfo.uptime());
                        break;
                    case "SEND":
                        String infos1 = infos[1];
                        String replace = line.substring(5).replace(infos1 + space, "");
                        LoginClient client = Config.loginServer.getClient(Long.parseLong(infos1));
                        client.send(replace);
                        this.write("Send : " + replace);
                        break;
                    default:
                        this.write(" > Unknown command '" + infos[0].toUpperCase() + "' !");
                        break;
                }
            }

            this.write(" > Console >");
        }
    }

    public void write(String msg) {
        if (!msg.isEmpty())
            logger.info(msg);
    }
}