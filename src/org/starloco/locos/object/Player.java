package org.starloco.locos.object;

public class Player {

    private final int id;
    private final int server;
    private final int group;

    public Player(int id, int server, int group) {
        this.id = id;
        this.server = server;
        this.group = group;
    }

    public int getId() {
        return id;
    }

    public int getServer() {
        return server;
    }

    public int getGroup() {
        return group;
    }
}
