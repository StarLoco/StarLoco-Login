package org.starloco.locos.tool.packetfilter;

import org.starloco.locos.login.LoginClient;
import vpn.detection.Response;
import vpn.detection.VPNDetection;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Locos on 21/06/2018.
 */
public class VPNAuthorization {
    private static final VPNAuthorization instance = new VPNAuthorization();
    public static VPNAuthorization getInstance() {
        return instance;
    }

    private final VPNDetection vpn;
    private final List<String> ips;
    private boolean active = false;

    private VPNAuthorization() {
        this.vpn = new VPNDetection("");
        this.ips = new ArrayList<>();
    }

    public void toggle() {
        this.active = !this.active;
    }

    public boolean isUnderVPN(LoginClient client) {
        if(!this.active) return false;

        boolean ok = false;
        try {
            synchronized (this.vpn) {
                InetAddress inetAddress = ((InetSocketAddress) client.getIoSession().getRemoteAddress()).getAddress();
                String IP = inetAddress.getHostAddress();

                if(ips.contains(IP)) {
                    return false;
                } else if(client.getAccount().isMj()) {
                    this.ips.add(IP);
                    return false;
                }

                Response answer = this.vpn.getResponse(IP);

                if (answer.status.equals("success"))
                    ok = answer.hostip;
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
        return ok;
    }
}
