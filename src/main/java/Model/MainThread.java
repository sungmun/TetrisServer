package Model;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import Control.Server;

public class MainThread {
	public static void main(String[] args) {

		try {
			ServerSocket serverSocket = new ServerSocket(Server.ServerPort);
			Runtime.getRuntime().addShutdownHook(new Thread() {
				@Override
				public void run() {
					super.run();
					Server.ranking.dailyRank.RankingRegistration();
				}
			});
			while (true) {
				Socket socket = serverSocket.accept();
				System.out.println("Accept");
				Thread server = new Server(socket);
				server.start();
			}
		} catch (IOException e) {
		}
	}
}
