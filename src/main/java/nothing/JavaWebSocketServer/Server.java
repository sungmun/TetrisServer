package nothing.JavaWebSocketServer;

import java.net.InetSocketAddress;
import java.util.HashSet;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import com.google.gson.Gson;

import Serversynchronization.MessageType;
import Serversynchronization.SocketMessage;
import Serversynchronization.User;
import Serversynchronization.UsersList;

/**
 * Hello world!
 *
 */
public class Server extends WebSocketServer implements MessageType{
	UsersList list=new UsersList();
	public Server(InetSocketAddress address) {
		super(address);
	}

	@Override
	public void onOpen(WebSocket conn, ClientHandshake handshake) {
		System.out.println("new connection to " + conn.getRemoteSocketAddress());
	}

	@Override
	public void onClose(WebSocket conn, int code, String reason, boolean remote) {
		System.out.println("closed " + conn.getRemoteSocketAddress() + " with exit code " + code + " additional info: " + reason);
	}

	@Override
	public void onMessage(WebSocket conn, String message) {
		SocketMessage msg=new Gson().fromJson(message, SocketMessage.class);
		switch (msg.getMessageType()) {
		case MAP_MESSAGE:
			
			break;
		case USER_LIST_MESSAGE:
			UsersList.setList((HashSet<User>) SocketMessage.transformJSON(msg.getMessage()));
			break;
		case WAITING_ROOM_CONNECT:
			list.add((User) SocketMessage.transformJSON(msg.getMessage()));
			break;
		case USER_SELECTING:
			User user=(User) SocketMessage.transformJSON(msg.getMessage());
		
			break;
		case GAMEOVER_MESSAGE:
			
			break;
		default:
			break;
		}
	}

	@Override
	public void onError(WebSocket conn, Exception ex) {
		System.err.println("an error occured on connection " + conn.getRemoteSocketAddress()  + ":" + ex);
	}
	
	@Override
	public void onStart() {
		System.out.println("server started successfully");
	}


	public static void main(String[] args) {
		String host = "localhost";
		int port = 8887;

		WebSocketServer server = new Server(new InetSocketAddress(host, port));
		server.run();
	}
}