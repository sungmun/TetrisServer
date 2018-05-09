package Control;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.UUID;
import java.util.Vector;

import Model.MessageProcessing;
import Model.Ranking;
import Serversynchronization.MessageType;
import Serversynchronization.TotalJsonObject;
import Serversynchronization.User;

public class Server extends Thread {

	public static final int ServerPort = 4160;
	public static Vector<WarRoom> battle_rooms = new Vector<WarRoom>();
	public static HashMap<UUID, Socket> list = new HashMap<UUID, Socket>();
	public static MessageProcessing processing = new MessageProcessing();
	public static Ranking ranking = new Ranking();

	public User client = new User(null, null);
	PrintWriter out;
	BufferedReader in;

	public Server(Socket socket) throws IOException {
		System.out.println("생성자");
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		list.put(client.getUuid(), socket);
	}

	@Override
	public void run() {

		try {
			while (!Thread.currentThread().isInterrupted()) {
				onMessage(list.get(client.getUuid()));
			}
		} finally {
			System.out.println("Thread is dead!");
		}
	}

	public void close() {
		processing.logout(this);
		this.interrupt();
	}

	public void send(String message, Socket socket) {
		try {
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
			send(message, Server.list.get(user.getUuid()));
		}
	}

	public static int searchindex(User user) {
		int index = 0;
		for (WarRoom battleRoom : battle_rooms) {
			if (battleRoom.equals(user)) {
				return index;
			}
			index++;
		}
		return -1;
	}

	public boolean channelMessage(String message) {
		int index = searchindex(client);
		if (index == -1) {
			return false;
		}

		User user = battle_rooms.get(index).opponentUser(client);

		send(message, list.get(user.getUuid()));
		return true;
	}

	public void onMessage(Socket client) {
		try {
			String str = in.readLine();
			
		} catch (IOException e) {
		}

	}

}