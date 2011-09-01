package co.hanul.jenova.web;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.servlet.ServletContext;

import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.util.CycleDetectionStrategy;

import org.eclipse.jetty.websocket.WebSocket.OnTextMessage;
import org.springframework.context.ApplicationContext;

import co.hanul.jenova.ErrorMsgContainer;
import co.hanul.jenova.JenovaConfig;
import co.hanul.jenova.ReturnMessage;
import co.hanul.jenova.UserInfo;
import co.hanul.jenova.domain.MapInfo;
import co.hanul.jenova.domain.MapTile;
import co.hanul.jenova.domain.Tile;
import co.hanul.jenova.domain.TileBlock;
import co.hanul.jenova.service.MapService;
import co.hanul.jenova.service.TileService;

/**
 * 제노바 웹소켓
 * 
 * @author Mr. 하늘
 */
public class JenovaWebSocket implements OnTextMessage {

	// 맵의 소켓들
	private Map<Long, Set<JenovaWebSocket>> mapSockets;
	// 맵의 유저들
	private Map<Long, Set<UserInfo>> mapUsers;
	// 맵의 지나갈 수 없는 부분
	private Map<Long, boolean[][]> mapBlocks;
	
	private UserInfo userInfo;
	private Long userId;
	private Long mapId;
	
	private JsonConfig jc;

	// 이 방에 들어있는 유저들
	private Set<JenovaWebSocket> users;

	private ServletContext servletContext;
	private ApplicationContext applicationContext;

	private Connection connection;

	public JenovaWebSocket(Map<Long, Set<JenovaWebSocket>> mapSockets, Map<Long, Set<UserInfo>> mapUsers, Map<Long, boolean[][]> mapBlocks, Long userId, Long mapId, ServletContext servletContext, ApplicationContext applicationContext) {
		if (mapSockets.get(mapId) == null) { // 맵 소켓 목록이 없으면
			mapSockets.put(mapId, new CopyOnWriteArraySet<JenovaWebSocket>()); // 생성
		}
		if (mapUsers.get(mapId) == null) { // 맵 유저 목록이 없으면
			mapUsers.put(mapId, new HashSet<UserInfo>()); // 생성
		}
		this.mapSockets = mapSockets;
		this.mapUsers = mapUsers;
		this.mapBlocks = mapBlocks;
		this.userId = userId;
		this.mapId = mapId;
		this.users = mapSockets.get(mapId);
		this.servletContext = servletContext;
		this.applicationContext = applicationContext;
		
		this.jc = new JsonConfig();
		this.jc.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
		
		if (mapBlocks.get(mapId) == null) {
			updateMapBlocks(mapId); // 맵 블록 업데이트
		}
	}

	@Override
	public void onMessage(String jsonString) {
		ErrorMsgContainer errorMsgContainer = new ErrorMsgContainer();
		ReturnMessage returnMessage = new ReturnMessage();

		JSONObject jsonData = JSONObject.fromObject(jsonString);
		String service = jsonData.getString("service"); // 서비스 이름 세팅
		String method = jsonData.getString("method"); // 메소드 이름 세팅
		
		returnMessage.setService(service);
		returnMessage.setMethod(method);

		if (service.equals("tile")) { // 타일 서비스를 이용합니다.
			TileService tileService = (TileService) applicationContext.getBean(TileService.class);
			if (method.equals("list")) { // 타일 목록
				returnMessage.setContent(Tile.findAllTiles()); // 타일 목록을
				sendMessageToThisUser(returnMessage); // 현재 유저에게 전송
			} else if (method.equals("create")) { // 타일 생성
				Tile tile = tileService.create(
						jsonData.getString("name"),
						servletContext.getRealPath(JenovaConfig.TEMP_UPLOAD_DIR) + File.separator + jsonData.getString("tempFileName"),
						servletContext.getRealPath(JenovaConfig.TILE_IMAGE_DIR),
						errorMsgContainer);
				if (!errorMsgContainer.hasErrorMsgs()) { // 에러가 없으면
					returnMessage.setContent(tile); // 타일을
					sendMessageToAllUsers(returnMessage); // 모든 유저에게 전송
					sendMessageToThisUser(returnMessage); // 현재 유저에게 전송
				}
			} else if (method.equals("update")) { // 타일 수정
				// TODO: 해야함...
				// 어떤 맵에 이 타일이 쓰일 경우 타일 크기 변경 불가!
			} else if (method.equals("addBlock")) { // 블록 추가
				Tile tile = tileService.addBlock(
						jsonData.getLong("id"),
						jsonData.getInt("x"),
						jsonData.getInt("y"),
						errorMsgContainer);
				if (!errorMsgContainer.hasErrorMsgs()) { // 에러가 없으면
					returnMessage.setContent(tile); // 타일을
					sendMessageToAllUsers(returnMessage); // 모든 유저에게 전송
					sendMessageToThisUser(returnMessage); // 현재 유저에게 전송
				}
			} else if (method.equals("removeBlock")) { // 블록 제거
				Tile tile = tileService.removeBlock(
						jsonData.getLong("id"),
						jsonData.getInt("x"),
						jsonData.getInt("y"),
						errorMsgContainer);
				if (!errorMsgContainer.hasErrorMsgs()) { // 에러가 없으면
					returnMessage.setContent(tile); // 타일을
					sendMessageToAllUsers(returnMessage); // 모든 유저에게 전송
					sendMessageToThisUser(returnMessage); // 현재 유저에게 전송
				}
			}
		} else if (service.equals("map")) { // 맵 서비스를 이용합니다.
			MapService mapService = (MapService) applicationContext.getBean(MapService.class);
			if (method.equals("list")) { // 타일 목록
				returnMessage.setContent(MapInfo.findAllMapInfoes()); // 맵 목록을
				sendMessageToThisUser(returnMessage); // 현재 유저에게 전송
			} else if (method.equals("create")) { // 맵 생성
				MapInfo mapInfo = mapService.create(
						jsonData.getString("name"),
						jsonData.getInt("width"),
						jsonData.getInt("height"),
						errorMsgContainer);
				if (!errorMsgContainer.hasErrorMsgs()) { // 에러가 없으면
					updateMapBlocks(mapId); // 맵 블록 업데이트
					returnMessage.setContent(mapInfo); // 맵을
					sendMessageToAllUsers(returnMessage); // 모든 유저에게 전송
					sendMessageToThisUser(returnMessage); // 현재 유저에게 전송
				}
			} else if (method.equals("update")) { // 맵 수정
				// TODO: 해야함...
				// 맵 크기 변경 시 타일을 먹으면 안됨.
			} else if (method.equals("get")) { // 맵 가져오기
				returnMessage.setContent(MapInfo.findMapInfo(jsonData.getLong("id"))); // 맵을
				sendMessageToThisUser(returnMessage); // 현재 유저에게 전송
			} else if (method.equals("addTile")) { // 타일 추가
				MapTile mapTile = mapService.addTile(
						mapId,
						jsonData.getLong("id"),
						jsonData.getInt("x"),
						jsonData.getInt("y"),
						errorMsgContainer);
				if (!errorMsgContainer.hasErrorMsgs()) { // 에러가 없으면
					updateMapBlocks(mapId); // 맵 블록 업데이트
					returnMessage.setContent(mapTile); // 맵 타일을
					sendMessageToMapUsers(returnMessage); // 현재 맵의 유저들에게 전송
				}
			} else if (method.equals("chkBlock")) { // 맵 블록 확인
				if (mapBlocks.get(mapId)[jsonData.getInt("y")][jsonData.getInt("x")]) {
					returnMessage.setContent("지나 갈 수 없음");
				} else {
					returnMessage.setContent("지나갈 수 있음");
				}
				sendMessageToThisUser(returnMessage); // 현재 유저에게 전송
			}
		} else if (service.equals("user")) { // 유저 서비스
			if (method.equals("join")) { // 조인
				returnMessage.setContent(userInfo);
				sendMessageToThisUser(returnMessage); // 현재 유저에게 전송
				sendMessageToMapUsers(returnMessage); // 현재 맵의 유저들에게 전송
			} else if (method.equals("list")) { // 유저 목록 반환
				returnMessage.setContent(mapUsers.get(mapId));
				sendMessageToThisUser(returnMessage); // 현재 유저에게 전송
			} else if (method.equals("move")) { // 이동
				boolean go = false;
				int direction = jsonData.getInt("direction");
				if (direction == 1 && userInfo.getX() - 1 >= 0 && !mapBlocks.get(mapId)[userInfo.getY()][userInfo.getX() - 1]) {
					go = true;
					for (UserInfo ui2 : mapUsers.get(mapId)) {
						if (ui2.getX() == userInfo.getX() - 1 && ui2.getY() == userInfo.getY()) {
							go = false;
							break;
						}
					}
					if (go) userInfo.setX(userInfo.getX() - 1);
				} else if (direction == 2 && userInfo.getY() - 1 >= 0 && !mapBlocks.get(mapId)[userInfo.getY() - 1][userInfo.getX()]) {
					go = true;
					for (UserInfo ui2 : mapUsers.get(mapId)) {
						if (ui2.getX() == userInfo.getX() && ui2.getY() == userInfo.getY() - 1) {
							go = false;
							break;
						}
					}
					if (go) userInfo.setY(userInfo.getY() - 1);
				} else if (direction == 3 && userInfo.getX() + 1 < mapBlocks.get(mapId)[userInfo.getY()].length && !mapBlocks.get(mapId)[userInfo.getY()][userInfo.getX() + 1]) {
					go = true;
					for (UserInfo ui2 : mapUsers.get(mapId)) {
						if (ui2.getX() == userInfo.getX() + 1 && ui2.getY() == userInfo.getY()) {
							go = false;
							break;
						}
					}
					if (go) userInfo.setX(userInfo.getX() + 1);
				} else if (direction == 4 && userInfo.getY() + 1 < mapBlocks.get(mapId).length && !mapBlocks.get(mapId)[userInfo.getY() + 1][userInfo.getX()]) {
					go = true;
					for (UserInfo ui2 : mapUsers.get(mapId)) {
						if (ui2.getX() == userInfo.getX() && ui2.getY() == userInfo.getY() + 1) {
							go = false;
							break;
						}
					}
					if (go) userInfo.setY(userInfo.getY() + 1);
				}
				if (go) {
					Map<String, Object> mv = new HashMap<String, Object>();
					mv.put("userId", userId);
					mv.put("direction", direction);
					returnMessage.setContent(mv);
				}
				sendMessageToMapUsers(returnMessage); // 현재 맵의 유저들에게 전송
			} else if (method.equals("talk")) { // 대화
				Map<String, Object> mv = new HashMap<String, Object>();
				mv.put("userId", userId);
				mv.put("msg", jsonData.getString("msg"));
				returnMessage.setContent(mv);
				sendMessageToMapUsers(returnMessage); // 현재 맵의 유저들에게 전송
			}
		}

		if (errorMsgContainer.hasErrorMsgs()) { // 에러가 있다면
			returnMessage.setStatus("error"); // 상태를 에러로 바꾸고
			returnMessage.setContent(errorMsgContainer.getErrorMsgList()); // 에러 메시지를
			sendMessageToThisUser(returnMessage); // 현재 유저에게 전송
		}
	}

	@Override
	public void onOpen(Connection connection) {
		this.connection = connection;
		users.add(this);
		
		ReturnMessage returnMessage = new ReturnMessage();
		returnMessage.setService("user");
		returnMessage.setMethod("join");
		
		userInfo = new UserInfo(); // 유저 생성
		userInfo.setId(userId); // 아이디 등록
		
		int y = 0, x = 0;
		for (boolean[] bs : mapBlocks.get(mapId)) {
			boolean found = false;
			for (boolean b : bs) {
				if (!b) {
					found = true;
					for (UserInfo ui2 : mapUsers.get(mapId)) {
						if (ui2.getX() == x && ui2.getY() == y) {
							found = false;
							break;
						}
					}
					if (found) {
						userInfo.setX(x);
						userInfo.setY(y);
						break;
					}
				}
				x++;
			}
			y++;
			if (found) break;
		}

		mapUsers.get(mapId).add(userInfo); // 유저 등록
	}

	@Override
	public void onClose(int closeCode, String message) {
		users.remove(this);
		
		ReturnMessage returnMessage = new ReturnMessage();
		returnMessage.setService("user");
		returnMessage.setMethod("exit");
		returnMessage.setContent(userInfo);
		sendMessageToMapUsers(returnMessage); // 현재 맵의 유저들에게 전송
		
		mapUsers.get(mapId).remove(userInfo); // 유저 삭제
	}

	/**
	 * 모든 유저들에게 방송
	 */
	private void sendMessageToAllUsers(ReturnMessage returnMessage) {
		returnMessage.setTarget("all");
		Set<Long> keySet = mapSockets.keySet();
		for (Long key : keySet) {
			for (JenovaWebSocket user : mapSockets.get(key)) {
				try {
					user.connection.sendMessage(JSONObject.fromObject(returnMessage, jc).toString());
				} catch (Exception e) {
				}
			}
		}
	}

	/**
	 * 현재 맵의 유저들에게 방송
	 */
	private void sendMessageToMapUsers(ReturnMessage returnMessage) {
		returnMessage.setTarget("map");
		for (JenovaWebSocket user : users) {
			try {
				user.connection.sendMessage(JSONObject.fromObject(returnMessage, jc).toString());
			} catch (Exception e) {
			}
		}
	}

	/**
	 * 현재 유저에게 방송
	 */
	private void sendMessageToThisUser(ReturnMessage returnMessage) {
		returnMessage.setTarget("this");
		try {
			connection.sendMessage(JSONObject.fromObject(returnMessage, jc).toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 맵 블록 업데이트
	 */
	private void updateMapBlocks(Long id) {
		MapInfo mapInfo = MapInfo.findMapInfo(id);
		if (mapInfo != null) {
			boolean[][] blocks = new boolean[mapInfo.getHeight()][mapInfo.getWidth()];
			mapBlocks.put(mapInfo.getId(), blocks);
			for (MapTile mapTile : mapInfo.getMapTiles()) {
				Tile tile = mapTile.getTile();
				for (TileBlock tileBlock : tile.getTileBlocks()) {
					try {
						blocks[mapTile.getY() + tileBlock.getX()][mapTile.getX() + tileBlock.getY()] = true;
					} catch (Exception e) { // 오류 발생 시
						// 무시
					}
				}
			}
		}
	}
}
