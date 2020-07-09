package io.github.jzdayz.netty;

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

    public static final EncodeHandler INSTANCE = new EncodeHandler();
    private static final InternalLogger log = InternalLoggerFactory.getInstance(EncodeHandler.class);

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if (msg instanceof ByteBuf) {
            ByteBuf newMsg = Utils.encryptBuf((ByteBuf) msg);
            ctx.writeAndFlush(newMsg);
            return;
        }
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
