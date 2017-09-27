package nothing.JavaWebSocketServer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Vector;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import Serversynchronization.MessageType;
import Serversynchronization.SocketMessage;
import Serversynchronization.User;
import Serversynchronization.UsersList;

public class Server extends Thread implements MessageType {

	public static final int ServerPort = 8000;
	public static final String ServerIP = "localhost";

	public static Vector<WarRoom> war_rooms = new Vector<>();
	public static HashMap<Integer, Socket> list = new HashMap<>();

	User client = new User(null, null);
	PrintWriter out;
	BufferedReader in;
	static int indexnum = 0;

	public Server(Socket socket) throws IOException {
		System.out.println("생성자");
		Integer num = new Integer(indexnum++);
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		client.setUserNumber(num);
		list.put(num, socket);
	}

	@Override
	public void run() {

		while (true) {
			try {
				onMessage(list.get(client.getUserNumber()));
			} catch (JsonSyntaxException | IOException e) {
				close();
				System.err.println(e.getMessage());
				break;
			}
		}
	}

	private void close() {
		try {
			in.close();
			out.close();
			list.remove(client.getUserNumber());
		} catch (IOException | ArrayIndexOutOfBoundsException e) {
		}
	}

	public void send(Object message, Socket socket) throws IOException {
		out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())));
		System.out.println("Server.send()");
		String msg = new Gson().toJson(message);
		System.out.println("보내는 정보" + msg);
		out.println(msg);
		out.flush();
	
	}

	public void loginEvent(SocketMessage message) throws IOException {
		User user = new Gson().fromJson(message.getMessage(), User.class);
		user.setUserNumber(client.getUserNumber());
		send(new SocketMessage(USER_SERIAL_NUM, user.getUserNumber()), list.get(user.getUserNumber()));
		broadCast(new SocketMessage(LOGIN, user));// 다른유저에게 접속한 유저 정보 전달
		System.out.println("Server.loginEvent(): 4");
		send(new SocketMessage(USER_LIST_MESSAGE, UsersList.getList()), list.get(user.getUserNumber()));
		System.out.println("Server.loginEvent(): 5");
		// 접속한 유저에게 다른 유저 정보 전달
		UsersList.add(user);// 접속한 유저의 정보 저장
		client = user;
	}

	public void logoutEvent(SocketMessage message) throws IOException {

		System.out.println("Server.logoutEvent()");
		User user = new Gson().fromJson(message.getMessage(), User.class);
		UsersList.delete(user);// 메모리에서 유저 삭제
		broadCast(new SocketMessage(LOGOUT, user));// 다른 유저에게 로그아웃한 유저정보 전달
	}

	public void userSelectingEvent(SocketMessage message) throws JsonSyntaxException, IOException {
		System.out.println("Server.userSelectingEvent()");
		User user = new Gson().fromJson(message.getMessage(), User.class);

		message = new SocketMessage(BE_CHOSEN, message.getMessage());// 선택당한 유저의 정보를 객체로 저장

		war_rooms.add(new WarRoom(client, user));
		// user는 사용자의 정보이고, message.transformJSON()는 선택당한 유저이다
		send(message, list.get(user.getUserNumber()));
		// 선택당한 유저에게 선택을 당했다고 알려줌
	}

	public void warAcceptEvent() throws IOException {
		System.out.println("Server.warAcceptEvent()");
		channelMessage(new SocketMessage(WAR_ACCEPT, null));
	}

	public void warDenialEvent() throws IOException {
		System.out.println("Server.warDenialEvent()");
		channelMessage(new SocketMessage(WAR_DENIAL, null));
	}

	public void mapMessageEvent(SocketMessage message) throws IOException {
		System.out.println("Server.mapMessageEvent()");
		channelMessage(message);
	}

	public void channelMessage(SocketMessage message) throws IOException {
		System.out.println("Server.channelMessage()");
		int index = war_rooms.indexOf(client);
		User user = war_rooms.get(index).opponentUser(client);

		send(message, list.get(user.getUserNumber()));
	}

	public void onMessage(Socket client) throws JsonSyntaxException, IOException {

		SocketMessage message = new Gson().fromJson(in.readLine(), SocketMessage.class);
		System.out.println(message.getMessage());
		switch (message.getMessageType()) {
		case LOGIN:
			loginEvent(message);
			break;
		case LOGOUT:
			logoutEvent(message);
			break;
		case USER_SELECTING:// 사용자가 유저를 선택후 선택한유저 정보 전달
			userSelectingEvent(message);
			break;
		case WAR_ACCEPT:// 선택 당한 유저의 응답 여부
			warAcceptEvent();
			break;
		case WAR_DENIAL:// 선택 당한 유저의 응답 여부
			warDenialEvent();
			break;
		case MAP_MESSAGE:// war_room으로 연결되 두 클라이언트 간의 데이터 전송
			mapMessageEvent(message);
			break;
		default:
			break;
		}
	}

	public void broadCast(SocketMessage message) throws IOException {
		System.out.println("Server.broadCast()");
		Vector<User> list = UsersList.getList();
		for (User user : list) {
			send(message, Server.list.get(user.getUserNumber()));
		}
	}

	public static void main(String[] args) {

		try {
			@SuppressWarnings("resource")
			ServerSocket serverSocket = new ServerSocket(ServerPort);
			while (true) {
				try {
					Socket socket = serverSocket.accept();
					System.out.println("Accept");
					Thread server = new Server(socket);
					server.start();
				} catch (NullPointerException e) {
				}

			}
		} catch (IOException e) {
		}

	}

}