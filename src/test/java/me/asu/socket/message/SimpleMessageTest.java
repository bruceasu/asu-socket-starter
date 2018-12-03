package me.asu.socket.message;

import static org.junit.Assert.*;

import me.asu.util.Bytes;
import org.junit.Before;
import org.junit.Test;

/**
 * @author victor.
 * @since 2018/12/3
 */
public class SimpleMessageTest {
    SimpleMessage message = new SimpleMessage();

    @Before
    public void setup() {
        message.setBody(Bytes.toBytes("hello"));
    }

    @Test
    public void pack() throws Exception {
        byte[] pack = message.pack();
        SimpleMessage message2 = new SimpleMessage();
        message2.unpack(pack);
        System.out.println(message);
        System.out.println("----------------");
        System.out.println(message2);
        System.out.println("----------------");
        System.out.println("message2 equals message : " + (message2.equals(message)));
        assertEquals(message, message2);
    }

}