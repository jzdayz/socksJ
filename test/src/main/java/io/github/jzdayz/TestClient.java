package io.github.jzdayz;

import io.github.jzdayz.netty.DecodeHandler;
import io.github.jzdayz.netty.EncodeHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.Scanner;

public class TestClient {
    public static void main(String[] args) throws InterruptedException {
        EventLoopGroup bossGroup = new NioEventLoopGroup(10);
        Bootstrap bootstrap = new Bootstrap();
        ChannelFuture sync = bootstrap.group(bossGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .handler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        ch.pipeline().addLast(
                                EncodeHandler.INSTANCE,
                                new DecodeHandler()
                        );
                    }
                })
                .connect("127.0.0.1", 9999).sync().addListener(ff -> {
                    if (!ff.isSuccess()) {
                        System.out.println("连接失败");
                        return;
                    }
                    System.out.println("连接成功");
                });

        Scanner scanner = new Scanner(System.in);

        System.out.println("client up");
        new Thread(() -> {
            while (true) {
                String next = scanner.next();
                ByteBuf msg = sync.channel().alloc().buffer().writeBytes(next.getBytes());
                System.out.println("发送数据：" + new String(ByteBufUtil.getBytes(msg)));
                sync.channel().writeAndFlush(msg).addListener((f) -> {
                    if (f.isSuccess()) {
                        System.out.println("发送成功");
                        return;
                    }
                    System.err.println("发送事变");
                });
            }
        }).start();


    }
}
