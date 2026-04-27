package org.starloco.locos.login.packet;

import org.starloco.locos.kernel.Config;
import org.starloco.locos.kernel.Console;
import org.starloco.locos.kernel.Main;
import org.starloco.locos.login.LoginClient;
import org.starloco.locos.object.Account;

class ZaapAuth {

    static void verify(LoginClient client, String zaapToken) {
        if (!Config.zaapEnabled) {
            Console.instance.write("[" + client.getIoSession().getId() + "] Zaap auth is disabled. Kicking client.");
            client.send("AlEf");
            client.kick();
            return;
        }

        if (zaapToken == null || zaapToken.isEmpty()) {
            Console.instance.write("[" + client.getIoSession().getId() + "] Empty Zaap token. Kicking client.");
            client.send("AlEf");
            client.kick();
            return;
        }

        Console.instance.write("[" + client.getIoSession().getId() + "] Verifying Zaap auth token.");

        Account account = Main.database.getAccountData().loadByZaapToken(zaapToken);

        if (account == null) {
            Console.instance.write("[" + client.getIoSession().getId() + "] No account found with zaapToken. Kicking client.");
            client.send("AlEf");
            client.kick();
            return;
        }

        String accountName = account.getName();

        if (Config.loginServer.clients.containsKey(accountName)) {
            Console.instance.write("[" + client.getIoSession().getId() + "] Account already logging in. Kicking existing session.");
            Config.loginServer.clients.get(accountName).kick();
        }

        if (!Main.database.getAccountData().consumeZaapToken(account.getUUID())) {
            Console.instance.write("[" + client.getIoSession().getId() + "] Failed to consume Zaap token for account '" + accountName + "'. Kicking client.");
            client.send("AlEf");
            client.kick();
            return;
        }

        client.setAccount(account);
        account.setClient(client);

        Config.loginServer.clients.remove(accountName);
        Config.loginServer.clients.put(accountName, client);

        client.setStatus(LoginClient.Status.SERVER);
        Console.instance.write("[" + client.getIoSession().getId() + "] Zaap auth successful for account '" + accountName + "'.");
    }
}
