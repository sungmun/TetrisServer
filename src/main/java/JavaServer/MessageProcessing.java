package JavaServer;

import java.util.UUID;

import Serversynchronization.MessageType;
import Serversynchronization.TotalJsonObject;
import Serversynchronization.User;

public class MessageProcessing {
	public final static String MessageTypeKey = MessageType.class.getSimpleName();

	public User loginEvent(Server server, String message) {
		TotalJsonObject msgObject = new TotalJsonObject(message);
		String userStr = (String) msgObject.get(User.class.getSimpleName());
		User user = TotalJsonObject.GsonConverter(userStr, User.class);

		user.setUuid(server.client.getUuid());

		sendSerialNum(server, user);
		broadSendUserInfo(server, user);
		sendListInfo(server, user);

		UsersList.add(user);// 접속한 유저의 정보 저장
		return user;
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
		Server.channelMessage(server, message);
		TotalJsonObject jsonObject=new  TotalJsonObject(message);
		User user=TotalJsonObject.GsonConverter(jsonObject.get("User").toString(), User.class);

		Server.ranking.insertRanking(user);
		
		rankingEvent(server, message);
	}

	public void userSelectingEvent(Server server, String message) {
		TotalJsonObject msgObject = new TotalJsonObject(message);
		String userStr = (String) msgObject.get(User.class.getSimpleName());
		User user = TotalJsonObject.GsonConverter(userStr, User.class);

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
		Server.channelMessage(server, msgObject.toString());
	}

	public void mapMessageEvent(Server server, String message) {
		Server.channelMessage(server, message);
	}
	
	public void rankingEvent(Server server,String message) {
		TotalJsonObject msgObject = new TotalJsonObject(message);
		String userStr = (String) msgObject.get(User.class.getSimpleName());
		User user = TotalJsonObject.GsonConverter(userStr, User.class);

		int bobRank=server.ranking.searchBOBRanking(user.getUuid());
		if(bobRank!=-1) {
			TotalJsonObject msgJsonObject = new TotalJsonObject();
			msgJsonObject.addProperty(MessageProcessing.MessageTypeKey, MessageType.RANK.toString());
			msgJsonObject.addProperty("RankingType", "BestOfBest");
			msgJsonObject.addProperty(int.class.getSimpleName(), bobRank);
			server.send(msgJsonObject.toString(), Server.list.get(user.getUuid()));	
		}
		int dailyRank=server.ranking.searchDailyRanking(user.getUuid());
		if(dailyRank!=-1) {
			TotalJsonObject msgJsonObject = new TotalJsonObject();
			msgJsonObject.addProperty(MessageProcessing.MessageTypeKey, MessageType.RANK.toString());
			msgJsonObject.addProperty("RankingType", "Daily");
			msgJsonObject.addProperty(int.class.getSimpleName(), dailyRank);
			server.send(msgJsonObject.toString(), Server.list.get(user.getUuid()));	
		}
	}
}
