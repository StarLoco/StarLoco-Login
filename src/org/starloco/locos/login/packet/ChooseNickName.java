package org.starloco.locos.login.packet;

import org.starloco.locos.kernel.Main;
import org.starloco.locos.login.LoginClient;
import org.starloco.locos.login.LoginClient.Status;
import org.starloco.locos.object.Account;

class ChooseNickName {

    static void verify(LoginClient client, String nickname) {
        final Account account = client.getAccount();

        if (!account.getPseudo().isEmpty()) {
            client.kick();
            return;
        }

        if (nickname.equalsIgnoreCase(account.getName())) {
            client.send("AlEr");
            return;
        }

        if (!nickname.matches("[A-Za-z0-9.@-]+") || Main.database.getAccountData().exist(nickname) != null) {
            client.send("AlEs");
            return;
        }

        client.getAccount().setPseudo(nickname);
        client.setStatus(Status.SERVER);
        client.getAccount().setState(0);
        AccountQueue.verify(client);
    }
}
