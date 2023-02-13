package org.starloco.locos.login.packet;

import org.starloco.locos.kernel.Config;
import org.starloco.locos.login.LoginClient;
import org.starloco.locos.object.Server;

class BasicAdministration {

    public static void execute(LoginClient client, String command) {
        if(client.getAccount() != null && client.getAccount().isMj()) {
            String[] info = command.split(" ");

            switch (info[0].toUpperCase()) {
                case "AUTHORIZED":
                    if (info.length >= 2) {
                        String IP = info[1];
                        Config.loginServer.authorizedIp.add(IP);
                        sendSuccessMessage(client, "You've authorized the IP (" + IP + "). It can connect to all accounts of the game.");
                    } else {
                        sendErrorMessage(client, "Invalid syntax -> AUTHORIZED [ENTER_IP (str)]");
                    }
                    break;
                case "UNAUTHORIZED":
                    if (info.length >= 2) {
                        String IP = info[1];
                        Config.loginServer.authorizedIp.remove(IP);
                        sendSuccessMessage(client, "You've unauthorized the IP (" + IP + "). It can connect to all accounts of the game.");
                    } else {
                        sendErrorMessage(client, "Invalid syntax -> UNAUTHORIZED [ENTER_IP (str)]");
                    }
                    break;
                case "LISTAUTHORIZED":
                    if(Config.loginServer.authorizedIp.isEmpty()) {
                        sendMessage(client, "Listing of all ips authorized to connect to all accouts :");
                        for (String ip : Config.loginServer.authorizedIp) {
                            sendMessage(client, "- " + ip);
                        }
                    } else {
                        sendErrorMessage(client, "The list of authorized accounts is empty.");
                    }
                    break;
                case "EMPTYAUTHORIZED":
                    sendSuccessMessage(client, "You removed " + Config.loginServer.authorizedIp.size() + " account(s) authorized.");
                    Config.loginServer.authorizedIp.clear();
                    break;
                case "SERVERSTATE":
                    if (info.length >= 3) {
                        short serverId;
                        byte stateId;

                        try {
                            serverId = Short.parseShort(info[1]);
                            stateId = Byte.parseByte(info[2]);
                        } catch (Exception e) {
                            sendErrorMessage(client, "Invalid syntax -> SERVERSTATE [ID_SERVER (int)] [ID_STATE (int)]");
                            break;
                        }

                        Server server = Server.get(serverId);

                        if(server == null) {
                            sendMessage(client, "Unknown server. List of servers :");
                            for(Server s : Server.servers.values())
                                sendMessage(client, "- id:" + s.getId() + " | key:" + s.getKey());
                            break;
                        }

                        server.setState(stateId);
                        sendSuccessMessage(client, "You've set the server (" + serverId + ") to the state (" + stateId + ").");
                    } else {
                        sendErrorMessage(client, "Invalid syntaxCommannd -> SERVERSTATE [ID_SERVER (int)] [ID_STATE (int)]");
                    }
                    break;

                default:
                    client.send(command);
                    sendMessage(client, "You send to your client : " + command);
                    break;
            }
        }
    }

    private static void sendMessage(LoginClient client, String message) {
        client.send(buildBAT(0, message));
    }

    private static void sendErrorMessage(LoginClient client, String message) {
        client.send(buildBAT(1, message));
    }

    private static void sendSuccessMessage(LoginClient client, String message) {
        client.send(buildBAT(2, message));
    }

    private static String buildBAT(int flag, String message) {
        return "BAT"
                .concat(String.valueOf(flag))
                .concat(Config.isVersionGreaterThan("1.35.0") ? "|12||" : "")
                .concat(message);
    }
}
