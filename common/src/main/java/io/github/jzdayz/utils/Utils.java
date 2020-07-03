/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package io.github.jzdayz.utils;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;

public final class Utils {

    /**
     * Closes the specified channel after all queued write requests are flushed.
     */
    public static void closeOnFlush(Channel ch) {
        if (ch.isActive()) {
            ch.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
        }
    }

    public static byte[] toBytes(int i) {
        return new byte[]{
                (byte) (i >>> 24 & 0xff),
                (byte) (i >>> 16 & 0xff),
                (byte) (i >>> 8 & 0xff),
                (byte) (i & 0xff),
        };
    }

    public static int toInt(byte[] b) {
        return b[3] & 0xFF |
                (b[2] & 0xFF) << 8 |
                (b[1] & 0xFF) << 16 |
                (b[0] & 0xFF) << 24;

    }

    public static ByteBuf encryptBuf(ByteBuf ms) throws Exception{
        int length = ms.readableBytes();
        byte[] bytes = Utils.toBytes(length);
        byte[] data = ByteBufUtil.getBytes(ms);
        System.out.println("加密的数据量："+data.length);
        byte[] array = data; //AESUtil.encrypt(data, AESArg.PWD);
        byte[] container = new byte[bytes.length + array.length];
        System.arraycopy(bytes,0,container,0,bytes.length);
        System.arraycopy(array,0,container,bytes.length,array.length);
        return ms.alloc().buffer().writeBytes(container);
    }

    private Utils() { }
}
