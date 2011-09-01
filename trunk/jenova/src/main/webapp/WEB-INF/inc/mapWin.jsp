<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<div id="mapWin" class="LWin">
	<div id="mapWin_inner">
		<div id="mapRegForm">
			<h3>맵 등록</h3>
			<ul class="newMapButton">
				<li><a href="javascript:clearMapRegForm();">새로 만들기</a></li>
			</ul>
			<p class="isNowMapMsg"></p>
			<form>
				<input type="hidden" name="service" value="map">
				<input type="hidden" name="method">
				<input type="hidden" name="id">
				<p>
					이름: <input type="text" name="name">
				</p>
				<p>
					높이: <input type="text" name="height">
				</p>
				<p>
					넓이: <input type="text" name="width">
				</p>
				<p>
					<input type="submit">
				</p>
			</form>
		</div>
		<div id="mapList">
			<ul></ul>
		</div>
	</div>
</div>
<script type="text/javascript">
clearMapRegForm(); // 맵 등록 폼 초기화

// Submit 이벤트 등록
$('#mapRegForm form').submit(function () {
	if ($.trim($(this).find('input[name=name]').val()) == '') {
		openMessageWin('맵 이름을 입력해 주세요.');
	} else if ($.trim($(this).find('input[name=width]').val()) == '') {
		openMessageWin('넓이를 입력해 주세요.');
	} else if ($.trim($(this).find('input[name=height]').val()) == '') {
		openMessageWin('높이를 입력해 주세요.');
	} else {
		sendFormToJenovaWebSocket(this);
		clearMapRegForm(); // 맵 등록 폼 초기화
	}
	return false;
});
</script>