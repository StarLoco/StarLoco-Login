package org.starloco.locos.object;

import org.starloco.locos.kernel.Main;
import org.starloco.locos.login.LoginClient;

import java.util.HashMap;
import java.util.Map;

public class Account {

    private final int UUID;
    private final String name, pass, question;
    private String pseudo;
    private byte state;
    private LoginClient client;
    private final long subscribe;
    private long bannedTime;
    private boolean banned = false;
    private boolean isMj = false;
    private final Map<Integer, Player> players = new HashMap<>();

    public Account(int UUID, String name, String pass, String pseudo,
                   String question, byte state, long subscribe, byte banned, long bannedTime) {
        this.UUID = UUID;
        this.name = name;
        this.pass = pass;
        this.pseudo = pseudo;
        this.question = question;
        this.state = state;
        this.subscribe = subscribe;
        this.banned = (banned != 0);
        if(this.banned) this.bannedTime = bannedTime;
    }

    public int getUUID() {
        return UUID;
    }

    public String getName() {
        return name;
    }

    public String getPass() {
        return pass;
    }

    public String getPseudo() {
        return pseudo;
    }

    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
        Main.database.getAccountData().update(this);
    }

    public String getQuestion() {
        return question;
    }

    public boolean isMj() {
        return this.isMj;
    }

    public LoginClient getClient() {
        return client;
    }

    public void setClient(LoginClient client) {
        this.client = client;
    }

    public byte getState() {
        return this.state;
    }

    public void setState(int state) {
        this.state = (byte) state;
        Main.database.getAccountData().update(this);
    }

    public long getSubscribeRemaining() {
        long remaining = (subscribe - System.currentTimeMillis());
        return remaining <= 0 ? 0 : remaining;
    }

    public long getSubscribe() {
        long remaining = (subscribe - System.currentTimeMillis());
        return remaining <= 0 ? 0 : subscribe;
    }

    public boolean isBanned() {
        return this.banned;
    }

    public void setBanned(boolean banned) {
        this.banned = banned;
    }

    public long getBannedTime() {
        return this.bannedTime;
    }

    public void setBannedTime(long bannedTime) {
        this.bannedTime = bannedTime;
    }

    public void addPlayer(Player player) {
        if (players.containsKey(player.getId()))
            return;
        players.put(player.getId(), player);
        if (!isMj) {
            if (player.getGroup() > 0)
                this.isMj = true;
        }
    }

    public Map<Integer, Player> getPlayers() {
        return players;
    }
}
