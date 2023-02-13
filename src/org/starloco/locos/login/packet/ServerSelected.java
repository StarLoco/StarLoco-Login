package org.starloco.locos.login.packet;

import org.starloco.locos.database.data.PlayerData;
import org.starloco.locos.exchange.ExchangeClient;
import org.starloco.locos.kernel.Config;
import org.starloco.locos.kernel.Console;
import org.starloco.locos.kernel.Main;
import org.starloco.locos.login.LoginClient;
import org.starloco.locos.object.Account;
import org.starloco.locos.object.Server;

import java.util.Map.Entry;

class ServerSelected {

    /*
     * AXEr : You're not authorized to join the server.
     * AXEd : The selected server isn't available.
     * AXEf : Server full.
     * AXEs : Impossible to select this player because it's used as seller on a server.
     */
    public static void get(LoginClient client, String packet) {
        Server server;
        Account account = client.getAccount();

        try {
            int i = Integer.parseInt(packet);
            server = Server.get(i);
            Console.instance.write("[" + client.getIoSession().getId() + "] Selection of server " + i + " for the account name " + account.getName() + ".");
        } catch (Exception e) {
            Console.instance.write("[" + client.getIoSession().getId() + "] The selection have failed with account name " + account.getName() + ".");
            client.send("AXEr");
            client.kick();
            return;
        }

        if (server == null) {
            Console.instance.write("[" + client.getIoSession().getId() + "] The server selected doesn't exist for account name " + account.getName() + ".");
            client.send("AXEr");
            return;
        }

        if (server.getState() != 1) {
            Console.instance.write("[" + client.getIoSession().getId() + "] The state server is not available for account name " + account.getName() + ".");
            client.send("AXEd");
            return;
        }

        if(!account.isMj()) {
            if ((account.getSubscribeRemaining() == 0 && server.getFreePlaces() <= 0) || (server.getSub() == 1 && account.getSubscribeRemaining() == 0)) {
                Console.instance.write("[" + client.getIoSession().getId() + "] The server selected is full or the account name " + account.getName() + " needed to be subscribe.");
                client.send(getFreeServer());
                return;
            }
        }

        PlayerData playerData = Main.database.getPlayerData();
        int logged = playerData.isLogged(account);
        Server serverLogged = Server.get(logged);

        if (logged > 0 && serverLogged != null && serverLogged.getClient() != null) {
            Console.instance.write("[" + client.getIoSession().getId() + "] The account " + account.getName() + " have some players always connected. The client going to be kicked.");
            account.setState(0);
            playerData.setState(account);
            client.send("AlEd");
            serverLogged.send("WK" + account.getUUID() + "");
            client.kick();
            return;
        }

        server.send("WA" + account.getUUID() + "");

        StringBuilder sb = new StringBuilder();
        String ip = client.getIoSession().getLocalAddress().toString().replace("/", "").split(":")[0];

        sb.append("AYK").append((ip.equals("127.0.0.1") ? "127.0.0.1" : server.getIp())).append(":").append(server.getPort()).append(";").append(account.getUUID());

        client.send(sb.toString());
        client.getAccount().setState(0);
        Console.instance.write("[" + client.getIoSession().getId() + "] The account name " + account.getName() + " has chosen the server successfully.");
    }

    private static String getFreeServer() {
        StringBuilder sb = new StringBuilder("AXEf");
        for (Entry<Long, ExchangeClient> entry : Config.exchangeServer.getClients().entrySet()) {
            ExchangeClient client = entry.getValue();
            if (client == null)
                continue;
            Server server = client.getServer();
            if (server == null)
                continue;
            if (server.getSub() == 0 && server.getFreePlaces() <= 0)
                sb.append(server.getId()).append("|");
        }

        return sb.toString();
    }
}
