package org.starloco.locos.login.packet;

import org.starloco.locos.kernel.Config;
import org.starloco.locos.login.LoginClient;
import org.starloco.locos.login.LoginClient.Status;

class Version {
    public final int major;
    public final int minor;
    public final int revision;
    public final boolean isElectron;

    private Version(int major, int minor, int revision, boolean isElectron) {
        this.major = major;
        this.minor = minor;
        this.revision = revision;
        this.isElectron = isElectron;
    }

    public boolean greaterThan(Version other) {
        if(this.major < other.major) return false;
        if(this.major > other.major) return true;
        if(this.minor < other.minor) return false;
        if(this.minor > other.minor) return true;
        if(this.revision < other.revision) return false;
        if(this.revision > other.revision) return true;
        return true;
    }

    static Version fromString(String str) {
        boolean isElectron = str.endsWith("e");
        if(isElectron) str = str.substring(0, str.length()-1);

        String[] parts = str.split("\\.");
        int major = Integer.parseInt(parts[0]);
        int minor = Integer.parseInt(parts[1]);
        int revision = Integer.parseInt(parts[2]);

        return new Version(major, minor, revision, isElectron);
    }

    static void verify(LoginClient client, String version) {
        Version clientVersion = fromString(version);
        Version minVersion = fromString(Config.version);

        if(!clientVersion.greaterThan(minVersion)) {
            System.out.println("[" + client.getIoSession().getId() + "] The version of the client '" + version + "' is not like the server '" + Config.version + "'. The client going to be kicked.");
            client.send("AlEv" + Config.version);
            client.kick();
            return;
        }

        client.setStatus(Status.WAIT_ACCOUNT);
    }
}
