package org.starloco.locos.login.packet;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.starloco.locos.kernel.Config;
import org.starloco.locos.kernel.Console;
import org.starloco.locos.login.LoginClient;

import java.util.Base64;

public class PacketHandler {

    public static void parser(LoginClient client, String packet) {
        switch (client.getStatus()) {
            case WAIT_VERSION: // ok
                String[] parts = packet.split("\\|");
                String version = parts[0];
                String lang = parts[1];

                Console.instance.write("[" + client.getIoSession().getId() + "] Checking for version '" + version + "'.");
                Version.verify(client, version);
                break;

            case WAIT_ACCOUNT: // a modifier
                if (packet.equals("#S")) {
                    // 1.39 character switch
                    client.setStatus(LoginClient.Status.WAIT_GAMESERVER_JWS);
                    return;
                }
                if (packet.length() < 3) {
                    Console.instance.write("[" + client.getIoSession().getId() + "] Sending of packet '" + packet + "' to verify the account. The client going to be kicked.");
                    client.send("AlEf");
                    client.kick();
                    return;
                }

                Console.instance.write("[" + client.getIoSession().getId() + "] Verification of account '" + packet + "'.");
                AccountName.verify(client, packet);
                break;
            case WAIT_GAMESERVER_JWS:
                try{
                    Claims result = Jwts.parserBuilder()
                            .requireIssuer("StarLocoGameServer")
                            .setAllowedClockSkewSeconds(5)
                            .setSigningKey(Keys.hmacShaKeyFor(Base64.getDecoder().decode(Config.exchangeKey)))
                            .build().parseClaimsJws(packet).getBody();

                    String accID = result.getSubject();
                    String ip = result.get("ip", String.class);

                    AccountName.verifyJWS(client, accID, ip);
                    return;
                }catch(Exception e) {
                    client.send("AlEf");
                    client.kick();
                    return;
                }


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
