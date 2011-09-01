$.fn.serializeObject = function() {
	var o = {};
	var a = this.serializeArray();
	$.each(a, function() {
		if (o[this.name] !== undefined) {
			if (!o[this.name].push) {
				o[this.name] = [ o[this.name] ];
			}
			o[this.name].push(this.value || '');
		} else {
			o[this.name] = this.value || '';
		}
	});
	return o;
};

/**
 * 폼 값을 JSON 형식으로
 */
$.fn.serializeJSON = function() {
	return JSON.stringify(this.serializeObject());
};

/**
 * tempUpload
 */
$.fn.tempUpload = function(func) {
	this.fileUpload({
		url: CONTEXT_PATH + '/TempUpload',
		type: 'POST',
		success: function (result, status, xhr) {
			func(result);
		}
	});
};

/**
 * 제노바 엔진-Client 실행
 */
function jenovaProcess(data) {
	if (data.status == 'success') { // 성공일 경우
		if (data.service == 'tile') { // 타일 서비스일 경우
			if (data.method == 'list') { // 목록 불러오기
				for (var i in data.content) {
					addTileToList(data.content[i]); // 타일 목록 생성
				}
			} else if (data.method == 'create') { // 타일 생성 처리
				if (data.target == 'all') { // 모든 유저
					addTileToList(data.content); // 타일 목록에 추가
				} else if (data.target == 'this') { // 현재 유저
					tileRegFormUpdateMode(data.content); // 타일 수정 모드로 변환
				}
			} else if (data.method == 'addBlock' || data.method == 'removeBlock') { // 타일 수정
				if (data.target == 'all') { // 모든 유저
					updateTile(data.content); // 타일 내역 수정
				} else if (data.target == 'this') { // 현재 유저
					tileRegFormUpdateMode(data.content); // 타일 수정 모드로 변환
				}
			}
		} else if (data.service == 'map') { // 맵 서비스일 경우
			if (data.method == 'list') { // 목록 불러오기
				for (var i in data.content) {
					addMapToList(data.content[i]); // 맵 목록 생성
				}
			} else if (data.method == 'create') { // 맵 생성 처리
				if (data.target == 'all') { // 모든 유저
					addMapToList(data.content); // 맵 목록에 추가
				} else if (data.target == 'this') { // 현재 유저
					mapRegFormUpdateMode(data.content); // 맵 수정 모드로 변환
				}
			} else if (data.method == 'get') { // 맵 불러오기
				mapView(data.content);
			} else if (data.method == 'addTile') { // 맵에 타일 추가
				addTileImgToMap(data.content);
			} else if (data.method == 'chkBlock') { // 맵 블록 테스트
				alert(data.content);
			}
		} else if (data.service == 'user') { // 유저 서비스일 경우
			if (data.method == 'list') { // 유저 목록 가져옴
				for (var i in data.content) {
					userJoinProc(data.content[i]);
				}
			} else if (data.method == 'join') { // 조인!
				if (data.target == 'map') { // 현재 맵
					userJoinProc(data.content);
				} else if (data.target == 'this') { // 현재 유저
					THIS_USER_ID = data.content.id; // 유저 아이디
					initCharacterMoveEvent();
				}
			} else if (data.method == 'exit') { // 종료!
				userExitProc(data.content);
			} else if (data.method == 'move') { // 이동
				moveCharacter(data.content);
			} else if (data.method == 'talk') { // 대화
				talk(data.content);
			}
		}
	} else if (data.status == 'error') { // 에러일 경우
		var errorMessages = '';
		for (var i in data.content) {
			errorMessages += data.content[i] + '<br>';
		}
		openMessageWin(errorMessages); // 에러 메시지 출력
	}
}

/**
 * Jenova 웹 소켓에 메시지 전송 (스택 방식)
 */
var toSendMessages = new Array();
var isSendingMessages = false;
function sendToJenovaWebSocket(message) {
	if (isSendingMessages) { // 메시지 전송중일때
		toSendMessages.push(message); // 메시지 저장
	} else { // 메시지가 전송중이 아닐때
		isSendingMessages = true; // 메시지 전송중
		JENOVA_WEB_SOCKET.send(message); // 메시지 보냄
	}
}
/**
 * 스택에 저장된 다음 메시지 보냄
 */
function sendNextMessage() {
	if (toSendMessages.length >= 1) { // 보내야할 메시지가 1개 이상일때
		JENOVA_WEB_SOCKET.send(toSendMessages.pop()); // 마지막것을 보낸다.
	} else { // 보낼 메시지가 없을때
		isSendingMessages = false; // 메시지 전송중이 아닙니다.
	}
}

/**
 * Jenova 웹 소켓에 폼 전송
 */
function sendFormToJenovaWebSocket(form) {
	sendToJenovaWebSocket($(form).serializeJSON());
}

var $allLWin;
/**
 * 모든 Left Window 닫기
 */
function closeAllLWin() {
	if (!$allLWin) {
		$allLWin = $('.LWin');
	}
	$allLWin.hide();
}