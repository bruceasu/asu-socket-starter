/*
 * Copyright (c) 2017 Suk Honzeon
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package me.asu.socket.message;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import me.asu.util.Bytes;
import me.asu.util.Hex;

/**
 * ProtoMessage.
 * <code><pre>
 * format:
 *      |---------+---------+---------+---------+----------+--------+--------+-----------|
 *      | 4 bytes | 4 bytes | 4 bytes | 1 byte  | 1 byte   | 1 byte | 1 byte | N byte(s) |
 *      |---------+---------+---------+---------+----------+--------+--------+-----------|
 *      | cmdId   | seqId   | bodyLen | cmdType | bodyType | code   | ttl    | body data |
 *      |---------+---------+---------+---------+----------+--------+--------+-----------|
 * header:
 *      lengthFieldOffset   = 8 pre header
 *      lengthFieldLength   = 4 only data length
 *      lengthAdjustment    = 4 after header
 *      initialBytesToStrip = 0
 * body:
 *      byte array.
 * </pre></code>
 * <p>2017 Suk All rights reserved.</p>
 *
 * @author Suk
 * @version 1.0.0
 * @since 2017-09-11 11:17
 */
@lombok.Data
public class ProtoMessage implements IProtoMessage, Cloneable {

    public ProtoMessageHeader header = new ProtoMessageHeader();
    /**
     * pb 字节序 或者json字符串.
     */
    public byte[]             body   = EMPTY_BODY;

    public static ProtoMessage create(IProtoMessage message) {
        ProtoMessage newMsg = new ProtoMessage();
        newMsg.getHeader().readFrom(message.getHeader());

        byte[] newBody;
        byte[] body = message.getBody();
        if (body == null || body.length == 0) {
            newBody = EMPTY_BODY;
        } else {
            newBody = new byte[body.length];
            System.arraycopy(body, 0, newBody, 0, body.length);
        }
        newMsg.setBody(newBody);
        return newMsg;
    }

    @Override
    public int cmdId() {
        return header.getCmdId();
    }

    @Override
    public byte[] pack() throws IOException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        // adjust
        header.bodyLen = body.length;

        stream.write(header.pack());
        stream.write(body);
        return stream.toByteArray();
    }

    @Override
    public int unpack(byte[] bytes) {
        if (bytes == null) {
            return 0;
        }
        return unpack(bytes, 0, bytes.length);
    }

    @Override
    public int unpack(byte[] bytes, int offset, int length) {
        if (bytes == null) {
            return 0;
        }
        if (readPackage(ByteBuffer.wrap(bytes, offset, length))) {
            return getPackageLength();
        } else {
            return 0;
        }
    }

    @Override
    public int getPackageLength() {
        return getHeaderLength() + getBodyLength();
    }

    @Override
    public int seqId() {
        return header.getSeqId();
    }

    @Override
    public ProtoMessageHeader header() {
        return header;
    }

    @Override
    public byte[] body() {
        return body;
    }

    @Override
    public void setBody(byte[] body) {
        if (body == null) {
            this.body = EMPTY_BODY;
        } else {
            this.body = body;
        }
        header.setBodyLen(this.body.length);
    }

    @Override
    public String toString() {
        return "{header: " + header.toString() + ", body: \"" + encodeBody() + "\"}";
    }

    private String encodeBody() {
        return header().getBodyType() == 1 ? Bytes.toString(body) : Hex.encodeHexString(body);
    }

    public void resetBody() {
        body = EMPTY_BODY;
    }

    private boolean readPackage(ByteBuffer byteBuffer) {
        if (!header.readHeader(byteBuffer)) {
            return false;
        }
        if (!readBody(byteBuffer)) {
            clear();
            return false;
        }
        return true;
    }

    private boolean canReadBody(ByteBuffer byteBuffer) {
        int bodyLength = header.bodyLen;
        return byteBuffer.remaining() >= bodyLength;
    }

    private boolean readBody(ByteBuffer byteBuffer) {
        if (!canReadBody(byteBuffer)) {
            return false;
        }
        body = new byte[header.bodyLen];
        byteBuffer.get(body);
        return true;
    }

    private void clear() {
        resetHeader();
        resetBody();
    }

    private void resetHeader() {
        getHeader().clear();
    }

    private int getHeaderLength() {
        return getHeader().getLength();
    }

    private int getBodyLength() {
        if (body != null && body.length > 0) {
            return body.length;
        } else {
            return getHeader().getBodyLen();
        }
    }

}

