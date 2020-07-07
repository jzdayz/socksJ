package io.github.jzdayz.client;

import io.github.jzdayz.netty.*;
import io.github.jzdayz.utils.Utils;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.FutureListener;
import io.netty.util.concurrent.Promise;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

import java.util.Arrays;
import java.util.Objects;

/**
 *  本机Server
 *  本机客户端(连接远程proxyServer)
 *  proxyServer
 */
public class ConnectionHandler extends ChannelInboundHandlerAdapter {

    private static final InternalLogger log = InternalLoggerFactory.getInstance(ConnectionHandler.class);

    private Bootstrap b = new Bootstrap();

    private Channel channel = null;

    private final static String HOST = System.getProperty("host","127.0.0.1");

    private final static Integer PORT = Integer.valueOf(System.getProperty("port","2080"));

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        final Channel inboundChannel = ctx.channel();
        msg = encode(msg);
        log.info("编码数据："+ Arrays.toString(ByteBufUtil.getBytes((ByteBuf) msg)));
        if (channel == null) {
            b.group(new NioEventLoopGroup(1))
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .handler(new ChannelInboundHandlerAdapter(){
                        @Override
                        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                            inboundChannel.writeAndFlush(msg);
                        }
                    });
            ChannelFuture sync = b.connect(HOST, PORT).sync();
            if (sync.isSuccess()){
                channel = sync.channel();
                channel.writeAndFlush(msg);
                log.info(" connected {}:{} ", HOST, PORT);
            }else{
                Utils.closeOnFlush(ctx.channel());
            }
        }else{
            channel.writeAndFlush(msg);
        }

    }

    private Object encode(Object msg) throws Exception{
        return Utils.encryptBuf((ByteBuf) msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        Utils.closeOnFlush(ctx.channel());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Utils.closeOnFlush(channel);
        super.channelInactive(ctx);
    }
}
