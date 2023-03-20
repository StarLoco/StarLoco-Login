package org.starloco.locos.login.packet;

import org.starloco.locos.kernel.Config;
import org.starloco.locos.kernel.Main;
import org.starloco.locos.login.LoginClient;
import org.starloco.locos.object.Account;

class AccountName {

    static void verify(LoginClient client, String name) {
        if(name.isEmpty() || !name.matches("[A-Za-z0-9.@-]+") || !AccountName.loadAndSetAccount(client, name)) {
            client.send("AlEf");
            client.kick();
            return;
        }
        client.setStatus(LoginClient.Status.WAIT_PASSWORD);
    }

    private static boolean loadAndSetAccount(LoginClient client, String name) {
        Account account = Main.database.getAccountData().load(name.toLowerCase());

        if(account == null) return false;

        client.setAccount(account);
        account.setClient(client);

        if (Config.loginServer.clients.containsKey(name)) // S'il est d�j� en connexion, on le kick : pas bon, il faut v�rifier le mdp avant
            Config.loginServer.clients.get(name).kick();
        Config.loginServer.clients.put(name, client);
        return true;
    }

    static void verifyJWS(LoginClient client,String name, String ip) {
        String currentIP = client.getIoSession().getRemoteAddress().toString().substring(1).split(":")[0];
        if(!ip.equals(currentIP) || !AccountName.loadAndSetAccount(client, name)) {
            client.send("AlEf");
            client.kick();
            return;
        }
        client.setStatus(LoginClient.Status.SERVER);

    }
}
