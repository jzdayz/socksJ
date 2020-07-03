package io.github.jzdayz.netty;

import io.github.jzdayz.utils.AESArg;
import io.github.jzdayz.utils.AESUtil;
import io.github.jzdayz.utils.Utils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

@ChannelHandler.Sharable
public class EncodeHandler extends ChannelOutboundHandlerAdapter {

    private static final InternalLogger log = InternalLoggerFactory.getInstance(EncodeHandler.class);

    public static final EncodeHandler INSTANCE = new EncodeHandler();



    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if (msg instanceof ByteBuf) {
            msg = Utils.encryptBuf((ByteBuf) msg);
            ctx.writeAndFlush(msg);
        }
        throw new RuntimeException();
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
