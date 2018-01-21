package JavaServer;

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

import Serversynchronization.MessageType;
import Serversynchronization.TotalJsonObject;
import Serversynchronization.User;

public class Server extends Thread {

	public static final int ServerPort = 4160;
	public static Vector<WarRoom> battle_rooms = new Vector<WarRoom>();
	public static HashMap<Integer, Socket> list = new HashMap<Integer, Socket>();
	public static final String MessageTypeKey = MessageType.class.getSimpleName();

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

	public int searchindex(User user) {
		int index = 0;
		for (WarRoom battleRoom : battle_rooms) {
			if (battleRoom.equals(user)) {
				return index;
			}
			index++;
		}
		return -1;
	}

	@Override
	public void run() {

		try {
			while (!Thread.currentThread().isInterrupted()) {
				onMessage(list.get(client.getUserNumber()));
			}
		} finally {
			System.out.println("Thread is dead!");
		}
	}

	private void close() {

		try {
			this.interrupt();
			in.close();
			out.close();
			list.remove(client.getUserNumber());
			UsersList.delete(client);// 메모리에서 유저 삭제
			TotalJsonObject msgObject = new TotalJsonObject();
			msgObject.addProperty(MessageTypeKey, MessageType.LOGOUT.toString());
			msgObject.addProperty(User.class.getSimpleName(), TotalJsonObject.GsonConverter(client));
			broadCast(msgObject.toString());
		} catch (IOException e) {
		}
	}

	public void send(String message, Socket socket) {
		try {
			System.out.println("Server.send()");
			System.out.println(message);
			out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())));
			out.println(message);
			out.flush();
			System.gc();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void broadCast(String message) {
		User[] list = UsersList.getList();
		for (User user : list) {
			send(message, Server.list.get(user.getUserNumber()));
		}
	}

	public void loginEvent(String message) {
		TotalJsonObject msgObject = new TotalJsonObject(message);
		String userStr = (String) msgObject.get(User.class.getSimpleName());

		User user = TotalJsonObject.GsonConverter(userStr, User.class);
		user.setUserNumber(client.getUserNumber());

		TotalJsonObject userSerialJson = new TotalJsonObject();
		userSerialJson.addProperty(MessageTypeKey, MessageType.USER_SERIAL_NUM.toString());
		userSerialJson.addProperty(Integer.class.getSimpleName(), user.getUserNumber().toString());
		send(userSerialJson.toString(), list.get(user.getUserNumber()));

		TotalJsonObject userInfoJson = new TotalJsonObject();
		userInfoJson.addProperty(MessageTypeKey, MessageType.LOGIN.toString());
		userInfoJson.addProperty(User.class.getSimpleName(), TotalJsonObject.GsonConverter(user));
		broadCast(userInfoJson.toString());// 다른유저에게 접속한 유저 정보 전달

		TotalJsonObject usersInfoJson = new TotalJsonObject();
		usersInfoJson.addProperty(MessageTypeKey, MessageType.USER_LIST_MESSAGE.toString());
		usersInfoJson.addProperty(UsersList.getList().getClass().getSimpleName(),
				TotalJsonObject.GsonConverter(UsersList.getList()));
		send(usersInfoJson.toString(), list.get(user.getUserNumber()));
		// 접속한 유저에게 다른 유저 정보 전달
		UsersList.add(user);// 접속한 유저의 정보 저장
		client = user;
	}

	public void logoutEvent() {
		close();
	}

	public void userSelectingEvent(String message) {
		TotalJsonObject msgObject = new TotalJsonObject(message);
		String userStr = (String) msgObject.get(User.class.getSimpleName());
		User user = TotalJsonObject.GsonConverter(userStr, User.class);

		battle_rooms.add(new WarRoom(client, user));
		// user는 사용자의 정보이고, message.transformJSON()는 선택당한 유저이다

		TotalJsonObject msgJsonObject = new TotalJsonObject();
		msgJsonObject.addProperty(MessageTypeKey, MessageType.BE_CHOSEN.toString());
		msgJsonObject.addProperty(User.class.getSimpleName(), TotalJsonObject.GsonConverter(client));
		send(msgJsonObject.toString(), list.get(user.getUserNumber()));
		// 선택당한 유저에게 선택을 당했다고 알려줌
	}

	public void battleStartEvent() {
		TotalJsonObject msgJsonObject = new TotalJsonObject();
		msgJsonObject.addProperty(MessageTypeKey, MessageType.BATTLE_START.toString());
		msgJsonObject.addProperty(User.class.getSimpleName(), TotalJsonObject.GsonConverter(client));
		broadCast(msgJsonObject.toString());
	}

	public void battleDenialEvent() {
		TotalJsonObject msgObject = new TotalJsonObject();
		msgObject.addProperty(MessageType.class.getName(), MessageType.BATTLE_DENIAL.toString());
		channelMessage(msgObject.toString());
	}

	public void mapMessageEvent(String message) {
		channelMessage(message);
	}

	public void channelMessage(String message) {
		int index = searchindex(client);
		User user = battle_rooms.get(index).opponentUser(client);

		send(message, list.get(user.getUserNumber()));
	}

	public void gameOverEvent(String message) {
		channelMessage(message);
		int index = searchindex(client);
		if (battle_rooms.get(index).connencting) {
			battle_rooms.get(index).connencting = false;
		} else {
			User user = battle_rooms.get(index).opponentUser(client);
			try {
				list.get(user.getUserNumber()).close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			this.close();
		}

	}

	public void RankingEvent(String message) {
		TotalJsonObject msgObject = new TotalJsonObject(message);
		String userStr = (String) msgObject.get(User.class.getSimpleName());
		User user = TotalJsonObject.GsonConverter(userStr, User.class);

		int ranking = -1;
		if (user.getInfo() == null) {
			return;
		}
		TotalJsonObject msgJsonObject = new TotalJsonObject();
		msgJsonObject.addProperty(MessageTypeKey, MessageType.RANK.toString());
		msgJsonObject.addProperty(Integer.class.getSimpleName(), ranking);
		send(msgJsonObject.toString(), list.get(user.getUserNumber()));

	}

	public void onMessage(Socket client) {
		try {
			System.out.println("Server.onMessage()");
			String str = in.readLine();
			System.out.println(str);
			TotalJsonObject msgObject = new TotalJsonObject(str);
			MessageType type = MessageType.valueOf((String) msgObject.get(MessageTypeKey));
			String message = msgObject.toString();

			switch (type) {
			case RANK:
				RankingEvent(message);
				break;
			case GAMEOVER_MESSAGE:
				gameOverEvent(message);
				break;
			case LOGIN:
				loginEvent(message);
				break;
			case LOGOUT:
				logoutEvent();
				break;
			case USER_SELECTING:// 사용자가 유저를 선택후 선택한유저 정보 전달
				userSelectingEvent(message);
				break;
			case BATTLE_DENIAL:// 선택 당한 유저의 응답 여부
				battleDenialEvent();
				break;
			case BATTLE_START:
				battleStartEvent();
				break;
			case SCORE_MESSAGE:
			case LEVEL_MESSAGE:
			case SAVE_BLOCK_MESSAGE:
			case NEXT_BLOCK_MESSAGE:
			case MAP_MESSAGE:
			case USER_MESSAGE:
				channelMessage(message);
				break;
			default:
				break;
			}
		} catch (NullPointerException | IOException e) {
			e.printStackTrace();
			close();
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