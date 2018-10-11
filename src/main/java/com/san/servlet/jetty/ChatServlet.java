package com.san.servlet.jetty;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.jetty.websocket.WebSocket;
import org.eclipse.jetty.websocket.WebSocketServlet;

import com.san.websocket.jetty.ChatSocket;


// Note : Dependency required => jetty-websocket, servlet-api
public class ChatServlet extends WebSocketServlet {

	private static final long serialVersionUID = 1L;

	private static HashMap<String, ChatSocket> users = new HashMap<>();

	@Override
	public WebSocket doWebSocketConnect(HttpServletRequest request, String protocol) {

		// Its all up to us. Either we can maintain single socket for each HTTP session
		// or we can maintain separate socket for each user
		// (multiple users can be in single session or multiple sessions can be for single user)

		String username = request.getPathInfo();
		if (username != null && username.length() > 1) {
			username = username.substring(1);
		}

		// Implementation 1 : Single socket for each HTTP session
		/*
		ChatSocket socket = users.get(request.getRequestedSessionId());
		
		if (socket != null) {
			return socket;
		} else {
			socket = new ChatSocket(username);
			users.put(request.getRequestedSessionId(), socket);
			return socket;
		}
		*/

		// Implementation 2 : Single socket for each username
		// /*
		ChatSocket socket = users.get(username);

		if (socket != null) {
			return socket;
		} else {
			socket = new ChatSocket(username);
			users.put(username, socket);
			return socket;
		}
		// */
		
		// Implementation 3 : We can also update the username if new user try to connect with same HTTP session
		/*
		ChatSocket socket = users.get(request.getRequestedSessionId());
		
		if (socket != null) {
			socket.username = username;
			return socket;
		} else {
			socket = new ChatSocket(username);
			users.put(request.getRequestedSessionId(), socket);
			return socket;
		}
		*/
	}

}
