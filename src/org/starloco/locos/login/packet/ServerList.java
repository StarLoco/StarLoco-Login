package org.starloco.locos.login.packet;

import org.starloco.locos.kernel.Console;
import org.starloco.locos.login.LoginClient;
import org.starloco.locos.object.Account;
import org.starloco.locos.object.Player;
import org.starloco.locos.object.Server;
import org.starloco.locos.tool.packetfilter.VPNAuthorization;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

class ServerList {

    public static void get(LoginClient client) {
        Console.instance.write("[" + client.getIoSession().getId() + "] Check if he's running with a VPN");

        if(!VPNAuthorization.getInstance().isUnderVPN(client)) {
            client.send("AxK" + serverList(client.getAccount()));
            Console.instance.write("[" + client.getIoSession().getId() + "] wasn't under VPN");
        } else {
            client.send("M029");
            client.kick();
            Console.instance.write("[" + client.getIoSession().getId() + "] was under VPN");
        }
    }

    private static String serverList(Account account) {
        StringBuilder sb = new StringBuilder(account.getSubscribeRemaining() + "");

        for (Server server : Server.servers.values()) {
            int i = characterNumber(account, server.getId());
            if (i == 0) {
                // If you want to show all servers in the list even if no player created
                //sb.append("|").append(server.getId()).append(",").append(1);
                continue;
            }

            sb.append("|").append(server.getId()).append(",").append(i);
        }

        Console.instance.write("[" + account.getClient().getIoSession().getId() + "] Sending list of server of account name " + account.getName() + ". List : '" + sb.toString() + "'");
        return sb.toString();
    }

    private static int characterNumber(Account account, int server) {
        int i = 0;

        for (Player character : account.getPlayers().values())
            if(character != null)
                if (character.getServer() == server)
                    i++;

        return i;
    }
}
