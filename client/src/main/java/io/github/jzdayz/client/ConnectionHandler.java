package io.github.jzdayz.client;

import io.github.jzdayz.utils.Utils;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

/**
 * 本机Server
 * 本机客户端(连接远程proxyServer)
 * proxyServer
 */
public class ConnectionHandler extends ChannelInboundHandlerAdapter {

    private static final InternalLogger log = InternalLoggerFactory.getInstance(ConnectionHandler.class);
    private final static String HOST = System.getProperty("targetHost", "127.0.0.1");
    private final static Integer PORT = Integer.valueOf(System.getProperty("targetPort", "2080"));
    private Bootstrap b = new Bootstrap();
    private Channel channel = null;
    private NioEventLoopGroup group = new NioEventLoopGroup(1);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        final Channel inboundChannel = ctx.channel();
        if (channel == null) {

            b.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .handler(new ChannelInitializer<Channel>() {
                        @Override
                        protected void initChannel(Channel ch) throws Exception {
//                            SSLEngine engine = SSLContext.getDefault().createSSLEngine();
//                            engine.setUseClientMode(true);
                            ch.pipeline().addFirst(
//                                    new SslHandler(engine, true),
                                    new ChannelInboundHandlerAdapter() {
                                        @Override
                                        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                            inboundChannel.writeAndFlush(msg);
                                        }
                                    }
                            );
                        }
                    });
            ChannelFuture sync = b.connect(HOST, PORT).sync();
            if (sync.isSuccess()) {
                channel = sync.channel();
                channel.writeAndFlush(msg);
                log.info(" connected {}:{} ", HOST, PORT);
            } else {
                Utils.closeOnFlush(ctx.channel());
            }
        } else {
            channel.writeAndFlush(msg);
        }

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        Utils.closeOnFlush(ctx.channel());
        Utils.closeOnFlush(channel);
        group.shutdownGracefully();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Utils.closeOnFlush(channel);
        group.shutdownGracefully();
        super.channelInactive(ctx);
    }
}
