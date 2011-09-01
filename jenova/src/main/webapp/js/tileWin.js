/**
 * 타일 윈도우 열기
 */
function toggleTileWin() {
	var isCloesd = $('#tileWin').css('display') == 'none';
	closeAllLWin();	
	if (isCloesd) {
		clearTileRegForm();
		$('#tileWin').show();
	}
}

/**
 * 타일 내부 내용 넣기
 */
function putTileContentHtml($tile, content) {
	var $a = $('<a href="#" />');
	$a.append('<img src="' + TILE_IMAGE_DIR + '/' + content.id + '">');
	$a.append(content.name);
	$a.click(function(){
		tileRegFormUpdateMode(content);
	});
	$tile.html($a);
	$tile.append('\n');
	var $a = $('<a href="#">선택</a>');
	$a.click(function(){
		toggleTileWin();
		
		var $mapView = $('#mapView');
		var $newTile = $('<img src="' + TILE_IMAGE_DIR + '/' + content.id + '">').hide();
		$mapView.find('.jenovaMapTile:first').before($newTile);
		$mapView.find('.jenovaMapTile').each(function(){
			$(this).unbind(); // 모든 이벤트 제거
			$(this).mouseover(function(){
				$newTile.css({
					'margin-top': $(this).css('margin-top'),
					'margin-left': $(this).css('margin-left')
				});
				$newTile.show();
			});
			$(this).mouseout(function(){
				$newTile.hide();
			});
			$(this).click(function(){
				// 맵에 타일 추가
				sendToJenovaWebSocket('{"service":"map", "method":"addTile", "id":"' + content.id + '", "x":' + $(this).attr('x') + ', "y":' + $(this).attr('y') + '}');
			});
		});
	});
	$tile.append($a);
}

/**
 * 리스트에 타일 더하기
 */
function addTileToList(content) {
	var $tile = $('<li id="jenovaTile' + content.id + '" />');
	putTileContentHtml($tile, content);
	
	$('#tileList ul').append($tile);
}

/**
 * 타일 내용 수정
 */
function updateTile(content) {
	var $tile = $('#jenovaTile' + content.id);
	putTileContentHtml($tile, content);
}

/**
 * 타일 블록 뷰어 닫기
 */
function closeTileBlockViewer() {
	$('#tileBlockViewer').hide();
	$('#tileBlockViewer').html('');
}

/**
 * 타일 블록 뷰어 열기
 */
function openTileBlockViewer(content) {
	$('#tileBlockViewer').show();
	
	// 타일 블록 뷰어 초기화
	var $tileBlockMap = $('<div />');
	$tileBlockMap.css('width', content.width * BASIC_TILE_PIXEL);
	$tileBlockMap.css('height', content.height * BASIC_TILE_PIXEL);
	$tileBlockMap.css('margin', '0 auto');
	$tileBlockMap.css('background-image', 'url("' + TILE_IMAGE_DIR + '/' + content.id + '")');
	var blockMap = new Array();
	for (var i = 0 ; i < content.width ; i++) {
		blockMap[i] = new Array();
	}
	for (var i in content.tileBlocks) {
		blockMap[content.tileBlocks[i].x][content.tileBlocks[i].y] = true;
	}
	for (var i = 0 ; i < content.width ; i++) {
		for (var j = 0 ; j < content.height ; j++) {
			var $img = $('<img x="' + i + '" y="' + j + '">');
			if (blockMap[i][j]) { // 블록일 경우
				$img.attr('src', CONTEXT_PATH + '/images/tileBlock.png');
				$img.mouseover(function(){
					$(this).attr('src', CONTEXT_PATH + '/images/blankTileBlock.png');
				});
				$img.mouseout(function(){
					$(this).attr('src', CONTEXT_PATH + '/images/tileBlock.png');
				});
				$img.click(function(){
					// 블록 제거
					sendToJenovaWebSocket('{"service":"tile", "method":"removeBlock", "id":"' + content.id + '", "x":' + $(this).attr('x') + ', "y":' + $(this).attr('y') + '}');
				});
			} else { // 아닐 경우
				$img.attr('src', CONTEXT_PATH + '/images/blankTileBlock.png');
				$img.mouseover(function(){
					$(this).attr('src', CONTEXT_PATH + '/images/tileBlock.png');
				});
				$img.mouseout(function(){
					$(this).attr('src', CONTEXT_PATH + '/images/blankTileBlock.png');
				});
				$img.click(function(){
					// 블록 추가
					sendToJenovaWebSocket('{"service":"tile", "method":"addBlock", "id":"' + content.id + '", "x":' + $(this).attr('x') + ', "y":' + $(this).attr('y') + '}');
				});
			}
			$tileBlockMap.append($img);
		}
	}
	$('#tileBlockViewer').html($tileBlockMap);
}

/**
 * 타일 등록 폼 초기화
 */
function clearTileRegForm() {
	var $tileRegForm = $('#tileRegForm');
	// 새 타일 만들기 버튼 숨기기
	$tileRegForm.find('.newTileButton').hide();
	// 블록 뷰 닫기
	closeTileBlockViewer();
	
	// 초기화
	$tileRegForm.find('input[name=method]').val('create');
	$tileRegForm.find('input[name=id]').val('');
	$tileRegForm.find('input[name=tempFileName]').val('');
	$tileRegForm.find('input[name=name]').val('');
	$tileRegForm.find('input[type=file]').val('');
	$tileRegForm.find('input[type=submit]').val('타일 생성');
	$('#tileImgViewer').html('');
}

/**
 * 타일 등록 폼 업데이트 모드
 */
function tileRegFormUpdateMode(content) {
	var $tileRegForm = $('#tileRegForm');
	// 새 타일 만들기 버튼 열기
	$tileRegForm.find('.newTileButton').show();
	// 블록 뷰 열기
	openTileBlockViewer(content);
	
	// 값 세팅
	$tileRegForm.find('input[name=method]').val('update');
	$tileRegForm.find('input[name=id]').val(content.id);
	$tileRegForm.find('input[name=tempFileName]').val('');
	$tileRegForm.find('input[name=name]').val(content.name);
	$tileRegForm.find('input[type=file]').val('');
	$tileRegForm.find('input[type=submit]').val('타일 수정');
	$('#tileImgViewer').html('<img src="' + TILE_IMAGE_DIR + '/' + content.id + '">');
}