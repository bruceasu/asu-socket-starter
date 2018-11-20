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


/**
 * ProtoMessage.
 * <code><pre>
 * format:
 *      4-cmdId,4-seqId,4-bodyLen,1-cmdType,1-bodyType,1-code,1-ttl, N-data.
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

public interface IProtoMessage extends IMessage {

    /**
     * quick get cmdId.
     *
     * @return cmdId.
     */
    int cmdId();

    /**
     * quick get seqId.
     *
     * @return seqId.
     */
    int seqId();

    /**
     * quick get header.
     *
     * @return header
     */
    ProtoMessageHeader header();

    /**
     * get header.
     *
     * @return header.
     */
    ProtoMessageHeader getHeader();

    /**
     * set header.
     *
     * @param header ProtoMessageHeader
     */
    void setHeader(ProtoMessageHeader header);

    /**
     * quick get body.
     *
     * @return body.
     */
    byte[] body();

    /**
     * get body.
     *
     * @return body.
     */
    byte[] getBody();

    /**
     * set body
     *
     * @param body byte[]
     */
    void setBody(byte[] body);
}

