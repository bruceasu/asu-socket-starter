package me.asu.socket.client;

import static org.junit.Assert.*;

import java.util.concurrent.atomic.AtomicInteger;
import me.asu.socket.message.IMessage;
import me.asu.socket.message.ProtoMessage;
import me.asu.util.Bytes;
import org.junit.Test;

/**
 * @author Suk.
 * @since 2018/11/13
 */
public class SimpleClientTest {
    AtomicInteger cnt = new AtomicInteger(0);

    @Test
    public void send() throws Exception {
        SimpleClient client = new  SimpleClient("localhost", 4444);
        ProtoMessage m = new ProtoMessage();
        m.getHeader().setCmdId(0);
        m.getHeader().setSeqId(cnt.getAndIncrement());
        m.setBody(Bytes.toBytes("hello"));
        for (int i = 0; i < 10; i++) {
            client.send(m);
        }
        for (int i = 0; i < 10; i++) {
            IMessage receive = client.receive(new ProtoMessage());
            System.out.println("receive = " + receive);
        }

    }

}