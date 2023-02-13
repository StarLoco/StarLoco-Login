package org.starloco.locos.exchange;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.starloco.locos.exchange.packet.PacketHandler;
import org.starloco.locos.object.Server;

public class ExchangeClient {

    private final IoSession ioSession;
    private Server server;

    public ExchangeClient(IoSession ioSession) {
        this.ioSession = ioSession;
    }

    public void send(String packet) {
        byte[] bytes = packet.getBytes();
        IoBuffer ioBuffer = IoBuffer.allocate(bytes.length);
        ioBuffer.put(bytes);
        this.ioSession.write(ioBuffer.flip());
    }

    public IoSession getIoSession() {
        return ioSession;
    }

    public Server getServer() {
        return server;
    }

    public void setServer(Server server) {
        this.server = server;
    }

    void parser(String packet) {
        PacketHandler.parser(this, packet);
    }

    public void kick() {
        this.ioSession.close(true);
    }
}
