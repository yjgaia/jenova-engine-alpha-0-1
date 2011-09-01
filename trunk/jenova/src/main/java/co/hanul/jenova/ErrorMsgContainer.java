package co.hanul.jenova;

import java.util.ArrayList;
import java.util.List;

public class ErrorMsgContainer {

	private List<String> errorMsgList;

	public void addErrorMsg(String errorMsg) {
		if (errorMsgList == null) {
			errorMsgList = new ArrayList<String>();
		}
		errorMsgList.add(errorMsg);
	}

	public List<String> getErrorMsgList() {
		return errorMsgList;
	}

	public boolean hasErrorMsgs() {
		return errorMsgList != null && errorMsgList.size() > 0;
	}

}
