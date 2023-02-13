package org.starloco.locos.login.packet;

import org.starloco.locos.kernel.Main;
import org.starloco.locos.login.LoginClient;
import org.starloco.locos.object.Account;
import org.starloco.locos.object.Player;
import org.starloco.locos.object.Server;

class FriendServerList {

    public static void get(LoginClient client, String packet) {
        if (!packet.matches("[A-Za-z0-9.@-]+")) {
            client.send("AF");
            return;
        }

        String name = Main.database.getAccountData().exist(packet);

        if(name != null) {
            Account account = Main.database.getAccountData().load(name);

            if (account != null) {
                Main.database.getPlayerData().load(account);
                client.send("AF" + getList(account));
                return;
            }
        }
        client.send("AF");
    }

    private static String getList(Account account) {
        StringBuilder sb = new StringBuilder();

        for (Server server : Server.servers.values()) {
            int i = getNumber(account, server.getId());
            if (i != 0)
                sb.append(server.getId()).append(",").append(i).append(";");
        }
        return sb.toString();
    }

    private static int getNumber(Account account, int id) {
        int i = 0;
        for (Player character : account.getPlayers().values())
            if (character.getServer() == id)
                i++;
        return i;
    }
}
