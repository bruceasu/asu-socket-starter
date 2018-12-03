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
import java.util.Arrays;
import me.asu.util.Bytes;
import me.asu.util.Hex;

/**
 * SimpleMessage.
 * <code><pre>
 * format:
 *      |---------+-----------|
 *      | 4 bytes | N byte(s) |
 *      |---------+-----------|
 *      | bodyLen | body data |
 *      |---------+-----------|
 * header:
 *      lengthFieldLength   = 4 only data length
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
public class SimpleMessage implements IMessage, Cloneable {

    public int bodyLen;
    public byte[] body = EMPTY_BODY;

    /**
     * pack.
     *
     * @return 字节数组(byte[])
     * @throws IOException 异常
     */
    @Override
    public byte[] pack() throws IOException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        // adjust
        this.bodyLen = body.length;

        stream.write(Bytes.toBytes(bodyLen));
        stream.write(body);
        return stream.toByteArray();
    }

    /**
     * unpack.
     *
     * @param bytes 字节数组(byte[])
     * @return 包长度，0 表示失败。
     */
    @Override
    public int unpack(byte[] bytes) {
        if (bytes == null) {
            return 0;
        }
        return unpack(bytes, 0, bytes.length);
    }

    /**
     * unpack.
     *
     * @param bytes  字节数组(byte[])
     * @param offset 开始位置
     * @param length 数据长度
     * @return 包长度，0 表示失败。
     */
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

    /**
     * raw data.
     *
     * @return byte[]
     */
    public byte[] body() {
        return body;
    }

    public void setBody(byte[] body) {
        if (body == null) {
            this.body = EMPTY_BODY;
        } else {
            this.body = body;
        }
        this.bodyLen = this.body.length;
    }

    public void reset() {
        bodyLen = 0;
        body = EMPTY_BODY;
    }

    @Override
    public String toString() {
        return "bodyLen: " + bodyLen + ", body: " + encodeBody();
    }

    /**
     * 用于打印显示内容。
     *
     * @return 如果能用utf-8转字符串的，转字符串返回，否则用十六进制数表示。
     */
    private String encodeBody() {
        try {
            return "String data(utf-8) => " + Bytes.toString(body);
        } catch (Exception e) {
            return "Raw data(hex format) => " + Hex.encodeHexString(body);
        }
    }

    private boolean canReadHeader(ByteBuffer byteBuffer) {
        return byteBuffer.remaining() >= getHeaderLength();
    }

    private boolean readHeader(ByteBuffer byteBuffer) {
        if (!canReadHeader(byteBuffer)) {
            return false;
        }
        bodyLen = byteBuffer.getInt();
        return true;
    }

    private boolean readPackage(ByteBuffer byteBuffer) {
        if (!readHeader(byteBuffer)) {
            return false;
        }
        if (!readBody(byteBuffer)) {
            reset();
            return false;
        }
        return true;
    }

    private boolean canReadBody(ByteBuffer byteBuffer) {
        return byteBuffer.remaining() >= bodyLen;
    }

    private boolean readBody(ByteBuffer byteBuffer) {
        if (!canReadBody(byteBuffer)) {
            return false;
        }
        body = new byte[bodyLen];
        byteBuffer.get(body);
        return true;
    }


    private int getHeaderLength() {
        return 4;
    }

    private int getBodyLength() {
        if (body != null && body.length > 0 && bodyLen != body.length) {
            // reset
            bodyLen = body.length;
        }
        return bodyLen;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        SimpleMessage that = (SimpleMessage) o;

        if (bodyLen != that.bodyLen) {
            return false;
        }
        return Arrays.equals(body, that.body);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + bodyLen;
        result = 31 * result + Arrays.hashCode(body);
        return result;
    }
}

