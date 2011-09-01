/**
 * 메시지 윈도우 열기
 */
function openMessageWin(msg) {
	$('#messageWin p').html(msg);
	$('#messageWinWrapper').show();
}

/**
 * 메시지 윈도우 닫기
 */
function closeMessageWin() {
	$('#messageWinWrapper').hide();
}