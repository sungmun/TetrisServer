package JavaServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class MainThread {
	public static void main(String[] args) {

		try {
			ServerSocket serverSocket = new ServerSocket(Server.ServerPort);
			Runtime.getRuntime().addShutdownHook(new Thread() {
				@Override
				public void run() {
					super.run();
					Server.ranking.DailyRankingRegistration();
				}
			});
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
