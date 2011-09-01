<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<div id="tileWin" class="LWin">
	<div id="tileWin_inner">
		<div id="tileRegForm">
			<h3>타일 등록</h3>
			<ul class="newTileButton">
				<li><a href="javascript:clearTileRegForm();">새로 만들기</a></li>
			</ul>
			<form>
				<input type="hidden" name="service" value="tile">
				<input type="hidden" name="method">
				<input type="hidden" name="id">
				<input type="hidden" name="tempFileName">
				<p>
					이름: <input type="text" name="name">
				</p>
				<table>
					<tr>
						<th>미리보기</th>
						<th>이동가능</th>
					</tr>
					<tr>
						<td id="tileImgViewer"></td>
						<td id="tileBlockViewer"></td>
					</tr>
				</table>
				<p>
					<input type="file">
				</p>
				<p>
					<input type="submit">
				</p>
			</form>
		</div>
		<div id="tileList">
			<ul></ul>
		</div>
	</div>
</div>
<script type="text/javascript">
clearTileRegForm(); // 타일 등록 폼 초기화

// 업로드 이벤트 등록
$tileRegFormFileInput = $('#tileRegForm form input[type=file]');
$tileRegFormFileInput.tempUpload(function (result) {
	$('#tileRegForm form input[name=tempFileName]').val(result);
	$('#tileImgViewer').html('<img src="' + TEMP_UPLOAD_DIR + '/' + result + '">');
});

// Submit 이벤트 등록
$('#tileRegForm form').submit(function () {
	if ($.trim($(this).find('input[name=name]').val()) == '') {
		openMessageWin('타일 이름을 입력해 주세요.');
	} else {
		sendFormToJenovaWebSocket(this);
		clearTileRegForm(); // 타일 등록 폼 초기화
	}
	return false;
});
</script>