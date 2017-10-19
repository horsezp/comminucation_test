package testNSocket;

import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class NioSocketServer {

	public static void startServer() throws Exception {

		ServerSocketChannel channel = ServerSocketChannel.open();
		channel.configureBlocking(false);

		ServerSocket serverSocket = channel.socket();
		serverSocket.setReuseAddress(true);
		serverSocket.bind(new InetSocketAddress(83));
		Selector selector = Selector.open();
		channel.register(selector, SelectionKey.OP_ACCEPT);

		try {

			while (true) {
				if (selector.select(100) == 0) {
					continue;
				}

				Iterator<SelectionKey> it = selector.selectedKeys().iterator();

				while (it.hasNext()) {

					SelectionKey selectionKey = it.next();
					it.remove();

					SelectableChannel selectableChannel = selectionKey.channel();
					if (selectionKey.isValid() && selectionKey.isAcceptable()) {
						ServerSocketChannel serverChannel = (ServerSocketChannel) selectableChannel;

						SocketChannel socketChannel = serverChannel.accept();
						socketChannel.configureBlocking(false);
						socketChannel.register(selector, SelectionKey.OP_READ, ByteBuffer.allocate(2048));
					} else if (selectionKey.isValid() && selectionKey.isConnectable()) {

					} else if (selectionKey.isValid() && selectionKey.isReadable()) {

						ByteBuffer byteBuffer = (ByteBuffer) selectionKey.attachment();

						SocketChannel clientSocketChannel = (SocketChannel) selectionKey.channel();

						int readlength = clientSocketChannel.read(byteBuffer);

						byteBuffer.flip();

						byte[] array = byteBuffer.array();

						String value = new String(array, "UTF-8");

						System.out.println(value);

						byteBuffer.clear();

						ByteBuffer sendBuffer = ByteBuffer.wrap(URLEncoder.encode("回发处理结果", "UTF-8").getBytes());
						clientSocketChannel.write(sendBuffer);
						clientSocketChannel.close();

					}
				}

			}

		} catch (Exception e) {

		} finally {
			serverSocket.close();
		}

	}

}
