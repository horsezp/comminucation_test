package testNetty;
import java.net.InetSocketAddress;
import java.nio.channels.spi.SelectorProvider;
import java.util.concurrent.ThreadFactory;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.DefaultThreadFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.BasicConfigurator;

public class TestHTTPNetty {
    static {
        BasicConfigurator.configure();
    }

    public static void main(String[] args) throws Exception {
        //�������Ҫ�ķ���������
        ServerBootstrap serverBootstrap = new ServerBootstrap();

        //=======================�������������̳߳أ������Ѿ���ϸ��������Ͳ���׸���ˣ�
        EventLoopGroup bossLoopGroup = new NioEventLoopGroup(1);
        ThreadFactory threadFactory = new DefaultThreadFactory("work thread pool");
        int processorsNumber = Runtime.getRuntime().availableProcessors();
        EventLoopGroup workLoogGroup = new NioEventLoopGroup(processorsNumber * 2, threadFactory, SelectorProvider.provider());
        serverBootstrap.group(bossLoopGroup , workLoogGroup);

        //========================���������������Ƿ����ͨ�����ͣ������Ѿ���ϸ��������Ͳ���׸���ˣ�
        serverBootstrap.channel(NioServerSocketChannel.class);

        //========================���ô�����
        serverBootstrap.childHandler(new ChannelInitializer<NioSocketChannel>() {
            /* (non-Javadoc)
             * @see io.netty.channel.ChannelInitializer#initChannel(io.netty.channel.Channel)
             */
            @Override
            protected void initChannel(NioSocketChannel ch) throws Exception {
                //������socket channel pipeline�м���http�ı���ͽ�����
                ch.pipeline().addLast(new HttpResponseEncoder());
                ch.pipeline().addLast(new HttpRequestDecoder());
                ch.pipeline().addLast(new HTTPServerHandler());
            }
        });

        serverBootstrap.option(ChannelOption.SO_BACKLOG, 128);
        serverBootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
        serverBootstrap.bind(new InetSocketAddress("0.0.0.0", 83));
    }
}

/**
 * @author yinwenjie
 */
@Sharable
class HTTPServerHandler extends ChannelInboundHandlerAdapter {
    /**
     * ��־
     */
    private static Log LOGGER = LogFactory.getLog(HTTPServerHandler.class);

    /**
     * ����һ��httpcontent����û�д�����ȫ����������Ϣ����������Ҫ��һ�������ļ�¼
     * Ȼ����channelReadComplete�����У�ִ�����������˵��������е�http���ݶ��������ˣ����д���
     */
    private static AttributeKey<StringBuffer> CONNTENT = AttributeKey.valueOf("content");

    /* (non-Javadoc)
     * @see io.netty.channel.ChannelInboundHandlerAdapter#channelRead(io.netty.channel.ChannelHandlerContext, java.lang.Object)
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        /*
         * �ڲ����У���������ȡ���ͻ��˴����Ĳ�����URL��Ϣ�����ҷ��ظ�һ��ȷ����Ϣ��
         * Ҫʹ��HTTP������������Ҫ�˽�Netty��http�ĸ�ʽ�����£�
         * ----------------------------------------------
         * | http request | http content | http content |
         * ----------------------------------------------
         * 
         * ����ͨ��HttpRequestDecoder channel handler������msg�������������ͣ�
         * HttpRquest���������������head�������url����Ϣ
         * HttpContent���������������
         * */
        if(msg instanceof HttpRequest) {
            HttpRequest request = (HttpRequest)msg;
            HttpMethod method = request.getMethod();

            String methodName = method.name();
            String url = request.getUri();
            HTTPServerHandler.LOGGER.info("methodName = " + methodName + " && url = " + url);
        } 

        //�������������������������ʵ��http�������ݵ��ۼ�
        if(msg instanceof HttpContent) {
            StringBuffer content = ctx.attr(HTTPServerHandler.CONNTENT).get();
            if(content == null) {
                content = new StringBuffer();
                ctx.attr(HTTPServerHandler.CONNTENT).set(content);
            }

            HttpContent httpContent = (HttpContent)msg;
            ByteBuf contentBuf = httpContent.content();
            String preContent = contentBuf.toString(io.netty.util.CharsetUtil.UTF_8);
            content.append(preContent);
        }
    } 

    /* (non-Javadoc)
     * @see io.netty.channel.ChannelInboundHandlerAdapter#channelReadComplete(io.netty.channel.ChannelHandlerContext)
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        HTTPServerHandler.LOGGER.info("super.channelReadComplete(ChannelHandlerContext ctx)");

        /*
         * һ������http��������ɣ�����Խ���ҵ�����ˡ�
         * ���ҷ�����Ӧ
         * */
        StringBuffer content = ctx.attr(HTTPServerHandler.CONNTENT).get();
        HTTPServerHandler.LOGGER.info("http�ͻ��˴�������ϢΪ��" + content);

        //��ʼ������Ϣ��
        String returnValue = "return response";
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
        HttpHeaders httpHeaders = response.headers();
        //��Щ����http response ��head��Ϣ�����μ�http�淶�������������������Լ���head����
        httpHeaders.add("param", "value");
        response.headers().set(HttpHeaders.Names.CONTENT_TYPE, "text/plain");
        //һ��Ҫ���ó��ȣ�����http�ͻ��˻�һֱ�ȴ�����Ϊ���ص���Ϣ���ȿͻ��˲�֪����
        response.headers().set(HttpHeaders.Names.CONTENT_LENGTH, returnValue.length());

        ByteBuf responseContent = response.content();
        responseContent.writeBytes(returnValue.getBytes("UTF-8"));

        //��ʼ����
        ctx.writeAndFlush(response);
    } 
}