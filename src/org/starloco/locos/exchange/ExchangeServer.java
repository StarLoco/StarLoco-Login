package org.starloco.locos.exchange;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.transport.socket.SocketAcceptor;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.starloco.locos.kernel.Config;
import org.starloco.locos.kernel.Console;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class ExchangeServer {

    private final SocketAcceptor acceptor = new NioSocketAcceptor();

    public ExchangeServer() {
        this.acceptor.setReuseAddress(true);
        this.acceptor.setHandler(new ExchangeHandler());
    }

    public void start() {
        if (this.acceptor.isActive())
            return;

        try {
            this.acceptor.bind(new InetSocketAddress(Config.exchangeIp, Config.exchangePort));
        } catch (IOException e) {
            Console.instance.write(e.toString());
            Console.instance.write("Fail to bind acceptor : " + e);
        } finally {
            Console.instance.write(" > Exchange server started on port "
                    + Config.exchangePort);
        }
    }

    public void stop() {
        if (!this.acceptor.isActive())
            return;

        this.acceptor.unbind();
        this.acceptor.getManagedSessions().values().stream().filter(session -> session.isConnected() || !session.isClosing()).forEach(session -> session.close(true));
        this.acceptor.dispose();

        Console.instance.write("Exchange server stopped");
    }

    public Map<Long, ExchangeClient> getClients() {
        Map<Long, ExchangeClient> clients = new HashMap<>();
        try {
            for (Entry<Long, IoSession> entry : this.acceptor.getManagedSessions().entrySet()) {
                Long id = entry.getKey();
                IoSession session = entry.getValue();
                if (session == null)
                    continue;
                if (session.getAttribute("client") instanceof ExchangeClient) {
                    ExchangeClient client = (ExchangeClient) session.getAttribute("client");
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
}