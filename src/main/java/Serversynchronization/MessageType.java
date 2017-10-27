package Serversynchronization;

public interface MessageType {
	int LOGIN                = 0x0;	// 로그인
	int USER_SERIAL_NUM      = 0x1;	// 접속한 유저에게 당사자의 시리얼번호를 돌려준다.
	int WAITING_ROOM_CONNECT = 0x2;	// 대기실 연결
	int USER_LIST_MESSAGE    = 0x3;	// 현제 대기실 목록
	int USER_SELECTING       = 0x4;	// 대결을 요청함
	int BE_CHOSEN            = 0x5;	// 대결을 요청 당함
	int WAR_ACCEPT           = 0x6;	// 대결 수락
	int WAR_DENIAL           = 0x7;	// 대결 거절
	int WAR_START            = 0x8;	// 대결시작
	int WAR_END              = 0x9;	// 대결끝
	int LOGOUT               = 0xA;	// 로그아웃
	int GAMEOVER_MESSAGE     = 0xB;	// 게임 종료
	int RANK				 = 0xC; // 랭킹
	
	int USER_MESSAGE         = 0x10; 	// 유저의 정보
	int MAP_MESSAGE          = 0x11; 	// 유저의 실시간 맵정보
	int NEXT_BLOCK_MESSAGE   = 0x12; 	// 유저의 다음에 나올 블럭의 정보
	int SAVE_BLOCK_MESSAGE   = 0x13; 	// 유저가 저장한 블럭의 정보
	int LEVEL_MESSAGE        = 0x14; 	// 유저의 레벨
	int SCORE_MESSAGE        = 0x15; 	// 유저의 점수
}
