package Serversynchronization;

public interface MessageType {
	int MAP_MESSAGE = 0x1;
	int USER_LIST_MESSAGE = 0x2;
	int GAMEOVER_MESSAGE = 0x3;
	int WAITING_ROOM_CONNECT = 0x4;
	int USER_SELECTING = 0x5;
	int BE_CHOSEN = 0x6;
	int LOGIN = 0x8;
	int LOGOUT = 0x9;
}
