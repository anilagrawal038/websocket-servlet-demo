package com.san.websocket.jetty;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.eclipse.jetty.websocket.WebSocket;

// Ref : https://docs.oracle.com/javaee/7/tutorial/websocket.htm
// Ref : https://benas.github.io/2016/02/21/using-the-java-api-for-webSocket-to-create-a-chat-server.html
// Ref : https://www.oracle.com/webfolder/technetwork/tutorials/obe/java/HomeWebsocket/WebsocketHome.html
// Ref : https://blog.openshift.com/how-to-build-java-websocket-applications-using-the-jsr-356-api/

public class ChatSocket implements WebSocket.OnTextMessage {

	private static final Set<ChatSocket> sockets = new CopyOnWriteArraySet<>();
	private Connection _connection;
	public String username;

	public ChatSocket(String username) {
		this.username = username;
	}

	@Override
	public void onOpen(Connection connection) {
		this._connection = connection;
		sockets.add(this);
		try {
			connection.sendMessage("{\"from\":\"server\",\"content\":\"Hi " + username + "! Server received Web Socket upgrade and added it to Receiver List.\"}");
			broadcast("{\"from\":\"server\",\"content\":\"" + username + " joined conversation\"}");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onClose(int closeCode, String message) {
		sockets.remove(this);
		try {
			broadcast("{\"from\":\"server\",\"content\":\"" + username + " left conversation\"}");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onMessage(String data) {
		// System.out.println("{\"from\":\"server\",\"content\":\"" + username + " received message : " + data + "\"}");
		try {
			broadcast("{\"from\":\"" + username + "\",\"content\":\"" + data + "\"}");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void sendMessage(String data) throws IOException {
		if (_connection.isOpen()) {
			_connection.sendMessage(data);
		}
	}

	private static void broadcast(String message) throws IOException {
		sockets.forEach(socket -> {
			synchronized (socket) {
				try {
					socket.sendMessage(message);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
	}

}
