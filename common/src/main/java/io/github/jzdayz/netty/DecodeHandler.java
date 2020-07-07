package io.github.jzdayz.netty;

import io.github.jzdayz.utils.AESUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

import java.util.Arrays;

import static io.github.jzdayz.utils.AESArg.PWD;

public class DecodeHandler extends LengthFieldBasedFrameDecoder {

  private static final InternalLogger log = InternalLoggerFactory.getInstance(DecodeHandler.class);

  public DecodeHandler(){
    super(Integer.MAX_VALUE,0,4,0,0);
  }

  @Override
  protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
    ByteBuf frame = null;
    try {
      frame = (ByteBuf) super.decode(ctx, in);
      if (null == frame) {
        return null;
      }
      log.info("解码数据："+ Arrays.toString(ByteBufUtil.getBytes(frame)));
      int length = frame.readInt();
      ByteBuf data = frame.readBytes(length);
      return ctx.alloc().buffer().writeBytes(AESUtil.decrypt(ByteBufUtil.getBytes(data), PWD));
    } catch (Exception e) {
      log.error(e.getMessage(),e);
      ctx.channel().close().addListener((ChannelFutureListener) future -> System.out.println("closeChannel"));
    }finally {
      if (null != frame) {
        frame.release();
      }
    }
    return null;
  }


  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    cause.printStackTrace();
    ctx.close();
  }

}
