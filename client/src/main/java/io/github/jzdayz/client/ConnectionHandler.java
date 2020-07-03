package io.github.jzdayz.client;

import io.github.jzdayz.netty.*;
import io.github.jzdayz.utils.Utils;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.FutureListener;
import io.netty.util.concurrent.Promise;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

/**
 *  本机Server
 *  本机客户端(连接远程proxyServer)
 *  proxyServer
 */
public class ConnectionHandler extends ChannelInboundHandlerAdapter {

    private static final InternalLogger log = InternalLoggerFactory.getInstance(ConnectionHandler.class);

    private Bootstrap b = new Bootstrap();

    private final static String HOST = System.getProperty("host","127.0.0.1");

    private final static Integer PORT = Integer.valueOf(System.getProperty("port","2080"));

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        final Channel inboundChannel = ctx.channel();
        Promise<Channel> promise = ctx.executor().newPromise();
        promise.addListener((FutureListener<Channel>)(future)->{
            // 连接proxy
            final Channel outboundChannel = future.getNow();
            if (future.isSuccess()) {
                ctx.pipeline().remove(ConnectionHandler.this);
                outboundChannel.pipeline().addLast(EncodeHandler.INSTANCE);
                outboundChannel.pipeline().addLast(new DecodeHandler());
                outboundChannel.pipeline().addLast(new RelayHandler(ctx.channel()));

                ctx.pipeline().addLast(new RelayHandlerEncoder(outboundChannel));
                outboundChannel.writeAndFlush(Utils.encryptBuf((ByteBuf) msg));
            } else {
                Utils.closeOnFlush(ctx.channel());
            }
        });
        // 连接远程机器
        b.group(inboundChannel.eventLoop())
                .channel(NioSocketChannel.class)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .handler(new DirectClientHandler(promise));
        b.connect(HOST, PORT).addListener((ChannelFutureListener) future -> {
            if (!future.isSuccess()) {
                Utils.closeOnFlush(ctx.channel());
            }else{
                log.info(" connected {}:{} ",HOST,PORT);
            }
        });

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        Utils.closeOnFlush(ctx.channel());
    }
}
