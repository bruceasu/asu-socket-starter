package me.asu.socket.server;

import static org.junit.Assert.*;

import me.asu.socket.message.*;
import me.asu.socket.server.Handler.HandlerAdapter;
import org.junit.Test;

/**
 * @author Suk.
 * @since 2018/11/13
 */
public class ServerTest {

    @Test
    public void run() throws Exception {
        Server server = new Server(4444);
        server.withHandler(new Echo()).start();
        Thread.sleep(20000);
    }

    public class Echo extends HandlerAdapter {

        @Override
        public void onRecv(IMessage message, ChannelContext ctx) {
            ctx.send(message, new SendCallBack() {
                @Override
                public void onComplete(boolean suc) {
                    System.out.println("send " + suc);
                }

                @Override
                public void onError(Throwable throwable) {
                    System.out.println("send " + throwable);
                }
            });
        }

        @Override
        public IProtoMessage createMessage() {
            return new ProtoMessage();
        }
    }

}