package org.starloco.locos.kernel;

public enum EmulatorInfo {
    RELEASE(1.02),
    SOFT_NAME("StarLoco Login v" + RELEASE.value),
    HARD_NAME(SOFT_NAME + " pour Dofus " + Config.version);

    private String string;
    private double value;

    EmulatorInfo(String s) {
        this.string = s;
    }

    EmulatorInfo(double d) {
        this.value = d;
    }

    public static String uptime() {
        long uptime = System.currentTimeMillis() - Config.startTime;
        int jour = (int) (uptime / (1000 * 3600 * 24));
        uptime %= (1000 * 3600 * 24);
        int hour = (int) (uptime / (1000 * 3600));
        uptime %= (1000 * 3600);
        int min = (int) (uptime / (1000 * 60));
        uptime %= (1000 * 60);
        int sec = (int) (uptime / (1000));

        return jour + "j " + hour + "h " + min + "m " + sec + "s";
    }

    @Override
    public String toString() {
        return this.string;
    }
}
