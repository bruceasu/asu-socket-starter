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


import java.io.IOException;
import java.nio.charset.Charset;

/**
 * IMessage.
 * <p>2017 Suk All rights reserved.</p>
 *
 * @author Suk
 * @version 1.0.0
 * @since 2017-09-11 11:17
 */
public interface IMessage {

    byte[]  EMPTY_BODY = new byte[0];
    Charset CS_UTF8    = Charset.forName("utf-8");

    /**
     * pack.
     *
     * @return 字节数组(byte[])
     * @throws IOException 异常
     */
    byte[] pack() throws IOException;

    /**
     * unpack.
     *
     * @param bytes 字节数组(byte[])
     * @return 包长度，0 表示失败。
     */
    int unpack(byte[] bytes);

    /**
     * unpack.
     *
     * @param bytes  字节数组(byte[])
     * @param offset 开始位置
     * @param length 数据长度
     * @return 包长度，0 表示失败。
     */
    int unpack(byte[] bytes, int offset, int length);

    int getPackageLength();
}

