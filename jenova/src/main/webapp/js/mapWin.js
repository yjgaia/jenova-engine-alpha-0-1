/**
 * 맵 윈도우 열기
 */
function toggleMapWin() {
	var isCloesd = $('#mapWin').css('display') == 'none';
	closeAllLWin();	
	if (isCloesd) {
		if (ROOT_MAP_CONTENT) { // ROOT 맵 내용이 있을 때
			mapRegFormUpdateMode(ROOT_MAP_CONTENT);
		}
		$('#mapWin').show();
	}
}

/**
 * 맵 내부 내용 넣기
 */
var ROOT_MAP_CONTENT;
function putMapContentHtml($map, content) {
	if (content.id == ROOT_MAP_ID) { // 선택한 맵이 ROOT 맵인 경우
		ROOT_MAP_CONTENT = content;
	}
	var $a = $('<a href="#" />');
	$a.append(content.name + ' <span>(' + content.height + '*' + content.width + ')</span>');
	$a.click(function(){
		mapRegFormUpdateMode(content);
	});
	$map.html($a);
	$map.append('\n');
	var $a = $('<a href="#">보기</a>');
	$a.click(function(){
		location.href = ROOT_PATH + '?map=' + content.id;
	});
	$map.append($a);
}

/**
 * 리스트에 맵 더하기
 */
function addMapToList(content) {
	var $map = $('<li id="jenovaMap' + content.id + '" />');
	putMapContentHtml($map, content);
	
	$('#mapList ul').append($map);
}

/**
 * 맵 내용 수정
 */
function updateMap(content) {
	var $map = $('#jenovaMap' + content.id);
	putMapContentHtml($map, content);
}

/**
 * 맵 등록 폼 초기화
 */
function clearMapRegForm() {
	var $mapRegForm = $('#mapRegForm');
	// 새 맵 만들기 버튼 숨기기
	$mapRegForm.find('.newMapButton').hide();
	
	// 초기화
	$mapRegForm.find('input[name=method]').val('create');
	$mapRegForm.find('input[name=id]').val('');
	$mapRegForm.find('input[name=name]').val('');
	$mapRegForm.find('input[name=height]').val('');
	$mapRegForm.find('input[name=width]').val('');
	$mapRegForm.find('input[type=submit]').val('맵 생성');
	
	$mapRegForm.find('.isNowMapMsg').text('');
}

/**
 * 맵 등록 폼 업데이트 모드
 */
function mapRegFormUpdateMode(content) {
	var $mapRegForm = $('#mapRegForm');
	// 새 맵 만들기 버튼 열기
	$mapRegForm.find('.newMapButton').show();
	
	// 값 세팅
	$mapRegForm.find('input[name=method]').val('update');
	$mapRegForm.find('input[name=id]').val(content.id);
	$mapRegForm.find('input[name=name]').val(content.name);
	$mapRegForm.find('input[name=height]').val(content.height);
	$mapRegForm.find('input[name=width]').val(content.width);
	$mapRegForm.find('input[type=submit]').val('맵 수정');
	
	if (content.id == ROOT_MAP_ID) { // 선택한 맵이 ROOT 맵일 경우
		$mapRegForm.find('.isNowMapMsg').text('현재 맵입니다.');
	} else {
		$mapRegForm.find('.isNowMapMsg').text('');
	}
}