package co.hanul.jenova.web;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.eclipse.jetty.websocket.WebSocket;
import org.eclipse.jetty.websocket.WebSocketServlet;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import co.hanul.jenova.IdWrapper;
import co.hanul.jenova.UserInfo;

/**
 * 제노바 웹소켓 서블릿
 * 
 * @author Mr. 하늘
 */
public class JenovaWebSocketServlet extends WebSocketServlet {
	private static final long serialVersionUID = 1L;

	// 맵의 소켓들
	public final Map<Long, Set<JenovaWebSocket>> mapSockets = new HashMap<Long, Set<JenovaWebSocket>>();
	// 맵의 유저들
	public final Map<Long, Set<UserInfo>> mapUsers = new HashMap<Long, Set<UserInfo>>();
	// 맵의 지나갈 수 없는 부분
	public final Map<Long, boolean[][]> mapBlocks = new HashMap<Long, boolean[][]>();
	
	// 유저 아이디 Wrapper
	public final IdWrapper userIdWrapper = new IdWrapper(0l);

	private ApplicationContext applicationContext;

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);

		// 일반적인 Servlet에서 Spring Bean을 가져옵니다.
		applicationContext = WebApplicationContextUtils.getWebApplicationContext(getServletContext());
	}

	@Override
	public WebSocket doWebSocketConnect(HttpServletRequest request, String protocol) {
		Long mapId = Long.valueOf(request.getParameter("map"));
		userIdWrapper.idIncrease(); // 유저 아이디 증가
		return new JenovaWebSocket(mapSockets, mapUsers, mapBlocks, userIdWrapper.getId(), mapId, getServletContext(), applicationContext);
	}
}
