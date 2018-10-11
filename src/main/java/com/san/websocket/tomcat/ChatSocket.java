package com.san.websocket.tomcat;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.apache.catalina.websocket.MessageInbound;
import org.apache.catalina.websocket.WsOutbound;

// Ref : https://coderwall.com/p/dahxya/implement-websocket-in-a-java-servlet

public class ChatSocket extends MessageInbound {

	private static final Set<ChatSocket> sockets = new CopyOnWriteArraySet<>();
	public String username;
	private WsOutbound outbound;

	public ChatSocket(String username) {
		this.username = username;
	}

	@Override
	public void onOpen(WsOutbound outbound) {
		sockets.add(this);
		this.outbound = outbound;
		try {
			sendMessage("{\"from\":\"server\",\"content\":\"Hi " + username + "! Server received Web Socket upgrade and added it to Receiver List.\"}");
			broadcast("{\"from\":\"server\",\"content\":\"" + username + " joined conversation\"}");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onClose(int closeCode) {
		sockets.remove(this);
		try {
			broadcast("{\"from\":\"server\",\"content\":\"" + username + " left conversation\"}");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onBinaryMessage(ByteBuffer message) throws IOException {
		throw new UnsupportedOperationException("Binary message handling not implemented for WebSockets");
	}

	@Override
	protected void onTextMessage(CharBuffer message) throws IOException {
		try {
			broadcast("{\"from\":\"" + username + "\",\"content\":\"" + message + "\"}");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void sendMessage(String data) {
		try {
			outbound.writeTextMessage(CharBuffer.wrap((data).toCharArray()));
		} catch (IOException ioException) {
			System.out.println("error opening websocket");
		}
	}

	private static void broadcast(String message) throws IOException {
		sockets.forEach(socket -> {
			synchronized (socket) {
				socket.sendMessage(message);
			}
		});
	}

}