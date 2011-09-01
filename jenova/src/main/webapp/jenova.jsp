<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="co.hanul.jenova.JenovaConfig" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title>Jenova World</title>
		<style type="text/css">
			@import url(${pageContext.request.contextPath}/styles/jenova.css);
			@import url(${pageContext.request.contextPath}/styles/messageWin.css);
			@import url(${pageContext.request.contextPath}/styles/tileWin.css);
			@import url(${pageContext.request.contextPath}/styles/mapWin.css);
		</style>
		<script src="${pageContext.request.contextPath}/js/jquery-1.6.2.min.js" type="text/javascript"></script>
		<script src="${pageContext.request.contextPath}/js/jquery.html5-fileupload.js" type="text/javascript"></script>
		<script src="${pageContext.request.contextPath}/js/jquery.spritely-0.5.js" type="text/javascript"></script>
		<script src="${pageContext.request.contextPath}/js/jenova.js" type="text/javascript"></script>
		<script src="${pageContext.request.contextPath}/js/messageWin.js" type="text/javascript"></script>
		<script src="${pageContext.request.contextPath}/js/tileWin.js" type="text/javascript"></script>
		<script src="${pageContext.request.contextPath}/js/mapWin.js" type="text/javascript"></script>
		<script src="${pageContext.request.contextPath}/js/map.js" type="text/javascript"></script>
		<script src="${pageContext.request.contextPath}/js/user.js" type="text/javascript"></script>
		<script type="text/javascript">
		var SERVER_NAME = '${pageContext.request.serverName}';
		var SERVER_PORT = '${pageContext.request.serverPort}';
		var CONTEXT_PATH = '${pageContext.request.contextPath}';
		var BASIC_TILE_PIXEL = <%= JenovaConfig.BASIC_TILE_PIXEL %>;
		var TEMP_UPLOAD_DIR = CONTEXT_PATH + '<%= JenovaConfig.TEMP_UPLOAD_DIR %>';
		var TILE_IMAGE_DIR = CONTEXT_PATH + '<%= JenovaConfig.TILE_IMAGE_DIR %>';
		
		var ROOT_MAP_ID = '${param.map == null ? "1" : param.map}'; // 루트 맵 아이디
		var ROOT_PATH = CONTEXT_PATH + '/jenova.jsp'; // 루트 페이지
		
		var THIS_USER_ID; // 이 유저의 아이디
		var JENOVA_WEB_SOCKET; // 제노바 웹 소켓
		$(document).ready(function(){
			// 제노바 웹 소켓을 엽니다.
			JENOVA_WEB_SOCKET = new WebSocket('ws://' + SERVER_NAME + ':' + SERVER_PORT + CONTEXT_PATH + '/JenovaWebSocket?map=' + ROOT_MAP_ID);
			JENOVA_WEB_SOCKET.onopen = function(event) {
				// 맵 불러오기
				sendToJenovaWebSocket('{"service":"map", "method":"get", "id":"' + ROOT_MAP_ID + '"}');
				// 회원 목록 불러오기
				sendToJenovaWebSocket('{"service":"user", "method":"list"}');
				// 회원 조인
				sendToJenovaWebSocket('{"service":"user", "method":"join"}');
				// 타일 목록 불러오기
				sendToJenovaWebSocket('{"service":"tile", "method":"list"}');
				// 맵 목록 불러오기
				sendToJenovaWebSocket('{"service":"map", "method":"list"}');
			};
			JENOVA_WEB_SOCKET.onmessage = function(event) { // 메시징 도착
				jenovaProcess($.parseJSON(event.data)); // JSON 파싱 후 실행
				sendNextMessage(); // 스택에 저장된 다음 메시지 보냄
			};
	
			closeAllLWin();
			closeMessageWin();
		});
		</script>
	</head>
	<body>
		<table id="mapViewWrapper"><tr><td><div id="mapView">
			Map Loading...
		</div></td></tr></table>
		<table id="LWinGroup"><tr><td>
			<ul id="LWinMenu">
				<li><a href="javascript:toggleTileWin();"><img alt="타일 윈도우 열기" src="${pageContext.request.contextPath}/images/tileWinOpenButton.png"></a></li>
				<li><a href="javascript:toggleMapWin();"><img alt="맵 윈도우 열기" src="${pageContext.request.contextPath}/images/mapWinOpenButton.png"></a></li>
			</ul>
		</td><td>
			<jsp:include page="WEB-INF/inc/tileWin.jsp" />
			<jsp:include page="WEB-INF/inc/mapWin.jsp" />
		</td></tr></table>
		<jsp:include page="WEB-INF/inc/messageWin.jsp" />
		<div id="welcomeMsgWin">
			<h3>Jenova Web Game Engine A-0.1</h3>
			<h4>Mr. 하늘</h4>
			<p>
				알파 0.1 버전입니다.<br>
				<font color="red">가끔 움직임이 멈추는 버그가 있습니다.</font><br>
				버그 발생시 F5를 눌러 초기화 해 주시기 바랍니다. 
			</p>
		</div>
		<div id="userList">
			<h3>유저 목록</h3>
		</div>
		<div id="talkList">
			<h3>대화 목록</h3>
			<p></p>
			<input class="msg" name="msg" autocomplete="off">
		</div>
	</body>
</html>