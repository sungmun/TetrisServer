package JavaServer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
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
	TetrisRankingDataBase database = new TetrisRankingDataBase();
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
		for (WarRoom warRoom : war_rooms) {
			if (warRoom.equals(user)) {
				return index;
			}
			index++;
		}
		return -1;
	}

	@Override
	public void run() {

		while (true) {
			try {
				onMessage(list.get(client.getUserNumber()));
			} catch (JsonSyntaxException | IOException e) {
				close();
				System.out.println(e.getMessage());
				break;
			}
		}
	}

	private void close() {

		try {
			in.close();
			out.close();
			list.remove(client.getUserNumber());
			UsersList.delete(client);// 메모리에서 유저 삭제
			broadCast(new SocketMessage(LOGOUT, client));
		} catch (IOException | ArrayIndexOutOfBoundsException e) {
		}
	}

	public void send(SocketMessage message, Socket socket) throws IOException {
		out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())));
		String msg = new Gson().toJson(message);
		out.println(msg);
		out.flush();
		System.gc();
	}

	public void broadCast(SocketMessage message) throws IOException {
		User[] list = UsersList.getList();
		for (User user : list) {
			send(message, Server.list.get(user.getUserNumber()));
		}
	}

	public void loginEvent(SocketMessage message) throws IOException {
		User user = new Gson().fromJson(message.getMessage(), User.class);
		user.setUserNumber(client.getUserNumber());
		send(new SocketMessage(USER_SERIAL_NUM, user.getUserNumber()), list.get(user.getUserNumber()));
		broadCast(new SocketMessage(LOGIN, user));// 다른유저에게 접속한 유저 정보 전달
		send(new SocketMessage(USER_LIST_MESSAGE, UsersList.getList()), list.get(user.getUserNumber()));
		// 접속한 유저에게 다른 유저 정보 전달
		UsersList.add(user);// 접속한 유저의 정보 저장
		client = user;
	}

	public void logoutEvent() throws IOException {
		close();
	}

	public void userSelectingEvent(SocketMessage message) throws JsonSyntaxException, IOException {
		User user = new Gson().fromJson(message.getMessage(), User.class);

		message.changeMessageType(BE_CHOSEN);

		war_rooms.add(new WarRoom(client, user));
		// user는 사용자의 정보이고, message.transformJSON()는 선택당한 유저이다
		send(new SocketMessage(BE_CHOSEN, client), list.get(user.getUserNumber()));
		// 선택당한 유저에게 선택을 당했다고 알려줌
	}

	public void warAcceptEvent(SocketMessage message) throws IOException {
		warStartEvent();
		channelMessage(message);
	}

	public void warStartEvent() throws IOException {
		broadCast(new SocketMessage(WAR_START, client));
	}

	public void warDenialEvent() throws IOException {
		channelMessage(new SocketMessage(WAR_DENIAL, null));
	}

	public void mapMessageEvent(SocketMessage message) throws IOException {
		channelMessage(message);
	}

	public void channelMessage(SocketMessage message) throws IOException {
		int index = searchindex(client);
		User user = war_rooms.get(index).opponentUser(client);

		send(message, list.get(user.getUserNumber()));
	}

	public void gameOverEvent(SocketMessage message) throws IOException {
		channelMessage(message);
		RankingEvent(message);
	}
	public void RankingEvent(SocketMessage message) throws IOException {
		User user = new Gson().fromJson(message.getMessage(), User.class);
		int ranking = -1;
		if(user.getInfo()==null) {
			return;
		}
		try {
			ranking=database.insertRankingSQL(user);
		} catch (SQLException e) {
			System.err.println("insertRanking Error");
		}
		send(new SocketMessage(RANK, ranking), list.get(client));
		
	}
	public void onMessage(Socket client) throws JsonSyntaxException, IOException {

		SocketMessage message = new Gson().fromJson(in.readLine(), SocketMessage.class);
		try {
			switch (message.getMessageType()) {
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
			case WAR_ACCEPT:// 선택 당한 유저의 응답 여부
				warAcceptEvent(message);
				break;
			case WAR_DENIAL:// 선택 당한 유저의 응답 여부
				warDenialEvent();
				break;
			case WAR_START:
				warStartEvent();
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
		} catch (NullPointerException e) {
			System.err.println(e.getMessage());
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