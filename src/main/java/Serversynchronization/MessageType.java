package Serversynchronization;

public interface MessageType {
	int MAP_MESSAGE = 0x1; 			// 맵
	int USER_LIST_MESSAGE = 0x2;	// 현제 대기실 목록
	int GAMEOVER_MESSAGE = 0x3;		// 게임 종료
	int WAITING_ROOM_CONNECT = 0x4;	// 대기실 연결
	int USER_SELECTING = 0x5;		// 대결을 요청함
	int BE_CHOSEN = 0x6;			// 대결을 요청 당함
	int LOGIN = 0x8;				// 로그인
	int LOGOUT = 0x9;				// 로그아웃
	int WAR_ACCEPT = 0xA;			// 대결 수락
	int WAR_DENIAL = 0XB;			// 대결 거절
	int USER_SERIAL_NUM = 0xC;		// 접속한 유저에게 당사자의 시리얼번호를 돌려준다.
	int WAR_START=0xD;				// 대결시작
	int WAR_END=0xE;				// 대결끝
	int CONNECT=0xF;				// 연결
}
