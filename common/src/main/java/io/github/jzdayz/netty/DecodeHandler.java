package io.github.jzdayz.netty;

import io.github.jzdayz.utils.AESUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

import static io.github.jzdayz.utils.AESArg.PWD;

public class DecodeHandler extends LengthFieldBasedFrameDecoder {

  public DecodeHandler(){
    super(Integer.MAX_VALUE,0,4,0,4);
  }

  @Override
  protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
    try {
      byte[] data = in.array();
      return AESUtil.decrypt(data, PWD);
    } catch (Exception e) {
      ctx.channel().close().addListener((ChannelFutureListener) future -> System.out.println("closeChannel"));
    }
    return null;
  }

}
