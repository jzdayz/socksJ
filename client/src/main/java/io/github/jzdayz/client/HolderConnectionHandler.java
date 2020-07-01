package io.github.jzdayz.client;

import io.github.jzdayz.netty.EncodeHandler;
import io.github.jzdayz.utils.Utils;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class HolderConnectionHandler extends ChannelInboundHandlerAdapter {

    private volatile boolean active = false;

    private Bootstrap b = new Bootstrap();

    private final static String HOST = System.getProperty("host");

    private final static Integer PORT = Integer.valueOf(System.getProperty("port"));

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        final Channel inboundChannel = ctx.channel();
        b.group(inboundChannel.eventLoop())
                .channel(NioSocketChannel.class)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline()
                                .addLast(EncodeHandler.INSTANCE);
                    }
                });

        b.connect(HOST, PORT).addListener((ChannelFutureListener) future -> {
            if (future.isSuccess()) {

                // Connection established use handler provided results
            } else {
                // Close the connection if the connection attempt has failed.
                Utils.closeOnFlush(ctx.channel());
            }
        });
        active = true;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        Utils.closeOnFlush(ctx.channel());
    }
}
