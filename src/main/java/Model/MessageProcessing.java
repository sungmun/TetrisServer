package Model;

import java.util.UUID;

import Control.Server;
import Control.UsersList;
import Control.WarRoom;
import Serversynchronization.MessageType;
import Serversynchronization.TotalJsonObject;
import Serversynchronization.User;

public class MessageProcessing {
	public final static String MessageTypeKey = MessageType.class.getSimpleName();

	public void MessageClassification(String message,Server server) {
		TotalJsonObject msgObject = new TotalJsonObject(message);
		MessageType type = MessageType.valueOf((String) msgObject.get(MessageProcessing.MessageTypeKey));
		String messagesStr = msgObject.toString();
		switch (type) {
		case RANK:
			rankingEvent(server);
			break;
		case GAMEOVER_MESSAGE:
			gameOverEvent(server, message);
			break;
		case LOGIN:
			loginEvent(server, (User) msgObject.getoOject(User.class));
			break;
		case USER_LIST_MESSAGE:
			userListEvent(server);
			break;
		case LOGOUT:
			server.close();
			break;
		case USER_SELECTING:// 사용자가 유저를 선택후 선택한유저 정보 전달
			userSelectingEvent(server, message);
			break;
		case BATTLE_DENIAL:// 선택 당한 유저의 응답 여부
			battleDenialEvent(server);
			break;
		case BATTLE_START:
			battleStartEvent(server);
			break;
		case SCORE_MESSAGE:
		case LEVEL_MESSAGE:
		case SAVE_BLOCK_MESSAGE:
		case NEXT_BLOCK_MESSAGE:
		case MAP_MESSAGE:
		case USER_MESSAGE:
			server.channelMessage(message);
			break;
		default:
			break;
		}
	}
	
	public User loginEvent(Server server, User user) {
		user.setUuid(server.client.getUuid());
		server.client=user;

		sendSerialNum(server, user);
		
		return user;
	}
	public void userListEvent(Server server){
		User user=server.client;
		broadSendUserInfo(server, user);
		sendListInfo(server, user);
		UsersList.add(user);// 접속한 유저의 정보 저장
	}
	private void sendSerialNum(Server server, User user) {
		TotalJsonObject userSerialJson = new TotalJsonObject();
		userSerialJson.addProperty(MessageTypeKey, MessageType.USER_SERIAL_NUM.toString());
		userSerialJson.addProperty(UUID.class.getSimpleName(), user.getUuid().toString());
		server.send(userSerialJson.toString(), Server.list.get(user.getUuid()));

	}

	private void broadSendUserInfo(Server server, User user) {
		TotalJsonObject userInfoJson = new TotalJsonObject();
		userInfoJson.addProperty(MessageTypeKey, MessageType.LOGIN.toString());
		userInfoJson.addProperty(User.class.getSimpleName(), TotalJsonObject.GsonConverter(user));
		server.broadCast(userInfoJson.toString());
		// 다른유저에게 접속한 유저 정보 전달
	}

	private void sendListInfo(Server server, User user) {
		TotalJsonObject usersInfoJson = new TotalJsonObject();
		usersInfoJson.addProperty(MessageTypeKey, MessageType.USER_LIST_MESSAGE.toString());
		usersInfoJson.addProperty(UsersList.getList().getClass().getSimpleName(),
				TotalJsonObject.GsonConverter(UsersList.getList()));
		server.send(usersInfoJson.toString(), Server.list.get(user.getUuid()));
		// 접속한 유저에게 다른 유저 정보 전달
	}

	public void logout(Server server) {
		Server.list.remove(server.client.getUuid());
		UsersList.delete(server.client);// 메모리에서 유저 삭제

		TotalJsonObject msgObject = new TotalJsonObject();
		msgObject.addProperty(MessageProcessing.MessageTypeKey, MessageType.LOGOUT.toString());
		msgObject.addProperty(User.class.getSimpleName(), TotalJsonObject.GsonConverter(server.client));
		server.broadCast(msgObject.toString());
	}

	public void gameOverEvent(Server server, String message) {
		TotalJsonObject jsonObject =new TotalJsonObject(message);
		String userStr=(String) jsonObject.get(User.class.getName());
		if(userStr==null) {
			return;
		}
		
		server.client=TotalJsonObject.GsonConverter(userStr, User.class);
		rankingEvent(server);
		
	}

	public void userSelectingEvent(Server server, User user) {
		Server.battle_rooms.add(new WarRoom(server.client, user));
		// user는 사용자의 정보이고, message.transformJSON()는 선택당한 유저이다

		TotalJsonObject msgJsonObject = new TotalJsonObject();
		msgJsonObject.addProperty(MessageTypeKey, MessageType.BE_CHOSEN.toString());
		msgJsonObject.addProperty(User.class.getSimpleName(), TotalJsonObject.GsonConverter(server.client));
		server.send(msgJsonObject.toString(), Server.list.get(user.getUuid()));
		// 선택당한 유저에게 선택을 당했다고 알려줌
	}

	public void battleStartEvent(Server server) {
		TotalJsonObject msgJsonObject = new TotalJsonObject();
		msgJsonObject.addProperty(MessageTypeKey, MessageType.BATTLE_START.toString());
		msgJsonObject.addProperty(User.class.getSimpleName(), TotalJsonObject.GsonConverter(server.client));
		server.broadCast(msgJsonObject.toString());
	}

	public void battleDenialEvent(Server server) {
		TotalJsonObject msgObject = new TotalJsonObject();
		msgObject.addProperty(MessageType.class.getName(), MessageType.BATTLE_DENIAL.toString());
		server.channelMessage(msgObject.toString());
	}

	public void mapMessageEvent(Server server, String message) {
		server.channelMessage(message);
	}

	public void rankingEvent(Server server) {
		TotalJsonObject bestOfBest = null;
		TotalJsonObject daily =null;
		
		Ranking rank = Server.ranking;
		User user=server.client;
		
		Server.ranking.insertRanking(user);
		
		daily= createRankMessage(rank.dailyRank.searchRanking(user.getUuid()), "DailyRanking");
		bestOfBest = createRankMessage(rank.bestOfBestRank.searchRanking(user.getUuid()), "BestOfBestRanking");

		if (daily == null) {
			return;
		}else if(bestOfBest != null) {
			server.send(bestOfBest.toString(), Server.list.get(user.getUuid()));
		}
		server.send(daily.toString(), Server.list.get(user.getUuid()));

	}

	private TotalJsonObject createRankMessage(int ranking, String RankingType) {
		if (ranking == -1) {
			return null;
		}
		TotalJsonObject msgJsonObject = new TotalJsonObject();
		msgJsonObject.addProperty(MessageProcessing.MessageTypeKey, MessageType.RANK.toString());
		msgJsonObject.addProperty("RankingType", RankingType);
		msgJsonObject.addProperty(int.class.getSimpleName(), ranking);
		return msgJsonObject;
	}
}
