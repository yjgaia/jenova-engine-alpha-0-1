function userJoinProc(content) {
	if ($('#user' + content.id).size() == 0) { // 유저가 없을 경우에만 생성
		$div = $('<div id="user' + content.id + '" />');
		$div.text('유저 ' + content.id);
		$('#userList').append($div);
		addCharacterImgToMap(content);
	}
}

function userExitProc(content) {
	$('#user' + content.id).remove();
	removeCharacterImgOnMap(content);
}

function initCharacterMoveEvent() {
	var direction = 0;
	var isMoving = false;
	
	$(document).keyup(function(event) {
		var keycode = (event.keyCode ? event.keyCode : event.which);
		if (
				(keycode == 37 && direction == 1)
			||	(keycode == 38 && direction == 2)
			||	(keycode == 39 && direction == 3)
			||	(keycode == 40 && direction == 4)
			) {
			isMoving = false;
		}
	});
	$(document).keydown(function(event) {
		var keycode = (event.keyCode ? event.keyCode : event.which);
		if (keycode == 37) { // left
			direction = 1;
			isMoving = true;
		} else if (keycode == 38) { // up
			direction = 2;
			isMoving = true;
		} else if (keycode == 39) { // right
			direction = 3;
			isMoving = true;
		} else if (keycode == 40) { // down
			direction = 4;
			isMoving = true;
		} else if (keycode == 13) { // talk
			if($.trim($('#talkList .msg').val()) == '') {
				$('#talkList .msg').focus();
			} else {
				sendToJenovaWebSocket('{"service":"user", "method":"talk", "msg":"' + $.trim($('#talkList .msg').val()) + '"}');
				$('#talkList .msg').val('');
			}
		}
	});
	
	window.setInterval(function() {
		if (direction != 0) {
			sendToJenovaWebSocket('{"service":"user", "method":"move", "direction":' + direction + '}');
			if (!isMoving) {
				direction = 0;
			}
		}
	}, 200);
}

function moveCharacter(content) {
	if (content) {
		var speed = 200;
		var param = {};
		var $character = $('#character' + content.userId);
		if (content.direction == 1) {
			param = {'margin-left' : '-=' + BASIC_TILE_PIXEL};
		} else if (content.direction == 2) {
			param = {
				'margin-top' : '-=' + BASIC_TILE_PIXEL,
				'z-index': '-=1'
			};
		} else if (content.direction == 3) {
			param = {'margin-left' : '+=' + BASIC_TILE_PIXEL};
		} else if (content.direction == 4) {
			param = {
				'margin-top' : '+=' + BASIC_TILE_PIXEL,
				'z-index': '+=1'
			};
		}
		if (param) {
			$character.animate(param, speed, 'linear');
		}
	}
}

function talk(content) {
	$('#talkList p').append(content.userId + ': ' + content.msg + '<br>');
	$('#talkList p').scrollTop(1000000);
	
	$('#character' + content.userId + ' .talkMsgCloud').hide();
	$('#character' + content.userId).append('<table class="talkMsgCloud"><tr><td><p>' + content.msg + '</p></td></tr></table>');
	setTimeout(function() {
		$('#character' + content.userId + ' .talkMsgCloud:first').remove();
	}, 3000);
}