package org.starloco.locos.object;

import org.starloco.locos.exchange.ExchangeClient;
import org.starloco.locos.login.LoginHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Server {

    public static final Map<Integer, Server> servers = new HashMap<>();
    private final int id;
    private int port;
    private int state;
    private final int sub;
    private int freePlaces;
    private String ip;
    private final String key;
    private ExchangeClient client;

    public Server(int id, String key, int sub) {
        this.id = id;
        this.key = key;
        this.state = 0;
        this.sub = sub;
        servers.put(this.id, this);
    }

    public static Server get(int id) {
        return servers.getOrDefault(id, null);
    }

    private static void sendHostListToAll() {
        LoginHandler.sendToAll(getHostList());
    }

    public static String getHostList() {
        StringBuilder sb = new StringBuilder("AH");
        Server.servers.values().stream().filter(Objects::nonNull).forEach(server ->
                sb.append(sb.length() > 2 ? "|" : "").append(server.getId()).append(";").append(server.getState()).append(";110;1"));
        return sb.toString();
    }

    public int getId() {
        return id;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
        sendHostListToAll();
    }

    public String getKey() {
        return key;
    }

    public int getSub() {
        return sub;
    }

    public ExchangeClient getClient() {
        return client;
    }

    public void setClient(ExchangeClient client) {
        this.client = client;
    }

    public int getFreePlaces() {
        return freePlaces;
    }

    public void setFreePlaces() {
        this.client.send("F?");
    }

    public void setFreePlaces(int freePlaces) {
        this.freePlaces = freePlaces;
    }

    public void send(Object arg0) {
        if(this.getClient() == null) return;
        if (arg0 instanceof String) {
            this.getClient().send((String) arg0);
        } else {
            this.getClient().getIoSession().write(arg0);
        }
    }
}
