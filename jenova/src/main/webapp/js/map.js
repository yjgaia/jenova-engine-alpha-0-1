/**
 * 맵 뷰에 맵을 뿌려줍니다.
 */
function mapView(content) {
	var $mapView = $('#mapView');
	if (content == null) { // 없는 맵일 경우
		$mapView.html('존재하지 않는 맵입니다.');
	} else {
		$mapView.html(''); // 맵 뷰 초기화
		$mapView.width(content.width * BASIC_TILE_PIXEL);
		$mapView.height(content.height * BASIC_TILE_PIXEL);
		for (var i = 0 ; i < content.height ; i++) {
			for (var j = 0 ; j < content.width ; j++) {
				$img = $('<img src="' + CONTEXT_PATH + '/images/blankTileBg.png">');
				$img.css({
					'margin-top': i * BASIC_TILE_PIXEL,
					'margin-left': j * BASIC_TILE_PIXEL,
				});
				$mapView.append($img);
			}
		}
		for (var i = 0 ; i < content.height ; i++) {
			for (var j = 0 ; j < content.width ; j++) {
				$img = $('<img class="jenovaMapTile" id="jenovaMapTile' + i + '_' + j + '" x="' + j + '" y="' + i + '" src="' + CONTEXT_PATH + '/images/blankTile.png">');
				$img.css({
					'margin-top': i * BASIC_TILE_PIXEL,
					'margin-left': j * BASIC_TILE_PIXEL,
				});
				$mapView.append($img);
				
				/*
				$img.click(function(){
					sendToJenovaWebSocket('{"service":"map", "method":"chkBlock", "x":' + $(this).attr('x') + ', "y":' + $(this).attr('y') + '}');
				});
				*/
			}
		}
		for (var i in content.mapTiles) {
			addTileImgToMap(content.mapTiles[i]);
		}
	}
}

/**
 * 맵에 타일 추가
 */
function addTileImgToMap(mapTile) {
	var $tile = $('<img src="' + TILE_IMAGE_DIR + '/' + mapTile.tile.id + '">');
	$tile.css({
		'margin-top': mapTile.y * BASIC_TILE_PIXEL,
		'margin-left': mapTile.x * BASIC_TILE_PIXEL
	});
	$('#mapView .jenovaMapTile:first').before($tile);
}

/**
 * 맵에 캐릭터 추가
 */
function addCharacterImgToMap(userInfo) {
	if (userInfo.x >= 0 && userInfo.y >= 0) {
		var $character = $('<div class="character" id="character' + userInfo.id + '"><h3>' + userInfo.id + '</h3></div>');
		$character.css({
			width: BASIC_TILE_PIXEL,
			height: 2 * BASIC_TILE_PIXEL,
			'background-image': 'url("' + CONTEXT_PATH + '/images/manWalk.png")',
			'margin-top': (userInfo.y - 1) * BASIC_TILE_PIXEL,
			'margin-left': userInfo.x * BASIC_TILE_PIXEL,
			'z-index': userInfo.y
		});
		$character.sprite({fps: 12, no_of_frames: 4});
		$('#mapView .jenovaMapTile:first').before($character);
	}
}

/**
 * 맵에서 캐릭터 제거
 */
function removeCharacterImgOnMap(userInfo) {
	$('#character' + userInfo.id).remove();
}