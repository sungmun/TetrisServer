package nothing.JavaWebSocketServer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import com.google.gson.Gson;

import Serversynchronization.MessageType;
import Serversynchronization.SocketMessage;
import Serversynchronization.User;
import Serversynchronization.UsersList;

public class Server implements MessageType, Runnable {

	public static final int ServerPort = 8000;
	public static final String ServerIP = "localhost";
	Socket client;
	PrintWriter out;
	BufferedReader in;
	@Override
	public void run() {

		try {
			System.out.println("S: Connecting...");
			ServerSocket serverSocket = new ServerSocket(ServerPort);

			while (true) {
				Socket client = serverSocket.accept();
				
				System.out.println("S: Receiving...");
				try {
					BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
					onMessage(client);

				} catch (Exception e) {
					System.out.println("S: Error");
					e.printStackTrace();
				} finally {
					System.out.println("S: Done.");
					client.close();
				}
			}
		} catch (Exception e) {
			System.out.println("S: Error");
			e.printStackTrace();
		}
	}

	public void send(Object message,Socket socket) {
		String msg=new Gson().toJson(message);
		try {
			out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
			out.write(msg);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void onMessage(Socket client) {
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));

			SocketMessage message = new Gson().fromJson(in.readLine(),SocketMessage.class);
			switch (message.getMessageType()) {
			case LOGIN:
				User user=(User)message.transformJSON();
				user.setSocket(client);
				UsersList.add(user);
				SocketMessage socket=new SocketMessage(USER_LIST_MESSAGE, UsersList.getList());
				send(socket,client);
				break;
			case LOGOUT:
				User user1=(User)message.transformJSON();
				UsersList.delete(user1);
				break;
			case USER_SELECTING:
				
			case BE_CHOSEN:
				
				break;

			default:
				break;
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {

		Thread desktopServerThread = new Thread(new Server());
		desktopServerThread.start();

	}

}