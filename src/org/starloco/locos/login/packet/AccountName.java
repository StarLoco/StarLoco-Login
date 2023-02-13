package org.starloco.locos.login.packet;

import org.starloco.locos.kernel.Config;
import org.starloco.locos.kernel.Main;
import org.starloco.locos.login.LoginClient;
import org.starloco.locos.object.Account;

class AccountName {

    static void verify(LoginClient client, String name) {
        if(!name.isEmpty() && name.matches("[A-Za-z0-9.@-]+")) {
            Account account = Main.database.getAccountData().load(name.toLowerCase());

            if(account != null) {
                client.setAccount(account);
                account.setClient(client);

                if (Config.loginServer.clients.containsKey(name)) // S'il est d�j� en connexion, on le kick : pas bon, il faut v�rifier le mdp avant
                    Config.loginServer.clients.get(name).kick();
                Config.loginServer.clients.put(name, client);
                client.setStatus(LoginClient.Status.WAIT_PASSWORD);
                return;
            }
        }

        client.send("AlEf");
        client.kick();
    }
}
