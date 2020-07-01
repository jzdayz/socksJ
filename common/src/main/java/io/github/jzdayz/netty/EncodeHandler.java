package io.github.jzdayz.netty;

import io.github.jzdayz.utils.AESArg;
import io.github.jzdayz.utils.AESUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;

@ChannelHandler.Sharable
public class EncodeHandler extends ChannelOutboundHandlerAdapter {

    public static final EncodeHandler INSTANCE = new EncodeHandler();

    public static void main(String[] args) {

        for (int i = 0; i < Integer.MAX_VALUE; i++) {

            if (i != toInt(toBytes(i))){
                throw new RuntimeException(i + " ERROR");
            }

            // 1000000000000000

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

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if (msg instanceof ByteBuf) {
            ByteBuf data = (ByteBuf) msg;
            byte[] bytes = data.array();
            byte[] encrypt = AESUtil.encrypt(bytes, AESArg.PWD);

            msg = ctx.alloc().heapBuffer().writeBytes(encrypt);
        }
        super.write(ctx, msg, promise);
    }

}
