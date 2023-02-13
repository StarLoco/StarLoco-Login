package org.starloco.locos.login;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.LineDelimiter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.starloco.locos.kernel.Config;
import org.starloco.locos.kernel.Console;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.Map.Entry;

public class LoginServer {

    private List<String> maintainAccounts;
    public final List<String> authorizedIp = new ArrayList<>();
    public final Map<String, LoginClient> clients = new TreeMap<>();
    private final NioSocketAcceptor acceptor;

    public LoginServer() {
        acceptor = new NioSocketAcceptor();
        acceptor.getFilterChain().addLast("codec", new ProtocolCodecFilter(new TextLineCodecFactory(StandardCharsets.UTF_8, LineDelimiter.NUL, new LineDelimiter("\n\0"))));
        acceptor.setHandler(new LoginHandler());
        acceptor.setCloseOnDeactivation(true);
    }

    public void start() {
        if (acceptor.isActive())
            return;

        try {
            acceptor.bind(new InetSocketAddress(Config.loginPort));
        } catch (IOException e) {
            Console.instance.write(e.toString());
            Console.instance.write(" > Fail to bind acceptor : " + e);
        } finally {
            Console.instance.write(" > Login server started on port "
                    + Config.loginPort);
        }
    }

    public void stop() {
        if (!acceptor.isActive())
            return;

        acceptor.unbind();
        acceptor.getManagedSessions().values().stream().filter(session -> session.isConnected() || !session.isClosing()).forEach(session -> session.close(true));
        acceptor.dispose();

        Console.instance.write(" > Login server stopped.");
    }

    Map<Long, LoginClient> getClients() {
        Map<Long, LoginClient> clients = new HashMap<>();
        try {
            for (Entry<Long, IoSession> entry : acceptor.getManagedSessions().entrySet()) {
                Long id = entry.getKey();
                IoSession session = entry.getValue();
                if (session == null)
                    continue;
                if (session.getAttribute("client") instanceof LoginClient) {
                    LoginClient client = (LoginClient) session.getAttribute("client");
                    if (client == null)
                        continue;
                    clients.put(id, client);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return clients;
    }

    public LoginClient getClient(long id) {
        IoSession session = acceptor.getManagedSessions().get(id);
        if (session.getAttribute("client") instanceof LoginClient)
            return (LoginClient) session.getAttribute("client");
        return null;
    }

    public void addMaintainAccount(String name) {
        if(this.maintainAccounts == null)
            this.maintainAccounts = new ArrayList<>();
        this.maintainAccounts.add(name);
    }

    public void removeMaintainAccount(String name) {
        if(this.maintainAccounts != null) {
            if(this.maintainAccounts.contains(name)) {
                this.maintainAccounts.remove(name);
                if (this.maintainAccounts.isEmpty())
                    this.maintainAccounts = null;
            }
        }
    }

    public boolean containsMaintainAccount(String name) {
        return this.maintainAccounts != null && this.maintainAccounts.contains(name);
    }
}
