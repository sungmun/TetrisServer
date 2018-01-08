package Serversynchronization;

public enum MessageType {
	LOGIN, 					// 로그인
	USER_SERIAL_NUM, 		// 현재 접속되있는 유저에게 지금 접속한 유저의 시리얼번호
	WAITING_ROOM_CONNECT, 	// 대기실 연결
	USER_LIST_MESSAGE, 		// 현재 대기실 목록
	USER_SELECTING, // 대결을 요청
	BE_CHOSEN, 		// 대결을 요청 받음
	BATTLE_ACCEPT, 	// 대결 수락
	BATTLE_DENIAL, 	// 대결 거절
	BATTLE_START, 		// 대결 시작
	BATTLE_END, 		// 대결 종료
	LOGOUT, 			// 로그아웃
	GAMEOVER_MESSAGE, 	// 게임종료
	RANK, 				// 랭킹
	/*--------------대결의 시작후 채널로 통신 메세지--------------*/
	USER_MESSAGE, 	// 유저의 정보
	MAP_MESSAGE, 	// 유저의 맵정보
	NEXT_BLOCK_MESSAGE, // 다음 블럭
	SAVE_BLOCK_MESSAGE, // 저장 블럭
	LEVEL_MESSAGE, // 레벨
	SCORE_MESSAGE; // 점수

}
