package org.starloco.locos.login.packet;

import org.starloco.locos.kernel.Console;
import org.starloco.locos.login.LoginClient;

public class PacketHandler {

    public static void parser(LoginClient client, String packet) {
        switch (client.getStatus()) {
            case WAIT_VERSION: // ok
                Console.instance.write("[" + client.getIoSession().getId() + "] Checking for version '" + packet + "'.");
                Version.verify(client, packet);
                break;

            case WAIT_ACCOUNT: // a modifier
                if (packet.length() < 3) {
                    Console.instance.write("[" + client.getIoSession().getId() + "] Sending of packet '" + packet + "' to verify the account. The client going to be kicked.");
                    client.send("AlEf");
                    client.kick();
                    return;
                }

                Console.instance.write("[" + client.getIoSession().getId() + "] Verification of account '" + packet + "'.");
                AccountName.verify(client, packet);
                break;

            case WAIT_PASSWORD: // ok
                if (packet.length() < 3) {
                    Console.instance.write("[" + client.getIoSession().getId() + "] Sending of packet '" + packet + "' to verify the password. The client going to be kicked.");
                    client.send("AlEf");
                    client.kick();
                    return;
                }

                Console.instance.write("[" + client.getIoSession().getId() + "] Verification of password '" + packet + "'.");
                Password.verify(client, packet);
                break;

            case WAIT_NICKNAME: // ok
                Console.instance.write("[" + client.getIoSession().getId() + "] Verification of nickname '" + packet + "'.");
                ChooseNickName.verify(client, packet);
                break;

            case SERVER:
                switch (packet.substring(0, 2)) {
                    case "AF":
                        FriendServerList.get(client, packet.substring(2));
                        break;

                    case "Af": // ok
                        AccountQueue.verify(client);
                        break;

                    case "AX":
                        ServerSelected.get(client, packet.substring(2));
                        break;

                    case "Ax":
                        ServerList.get(client);
                        break;

                    case "BA":
                        BasicAdministration.execute(client, packet.substring(2));
                        break;

                    case "Ap":
                    case"Ai":
                        break;

                    default:
                        client.kick();
                        break;
                }
                break;

        }
    }
}
