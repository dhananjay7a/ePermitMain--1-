package com.organisation.model;

public class ResponseMessage {

	private String status;
	private String message;
	private long errorCode;
	private Object data;
	private String appendMessage;
	private String commodity;
	private String img;

	public ResponseMessage(){
		
	}
	
	public ResponseMessage(String status, String message) {
		this.status = status;
		this.message = message;
	}
	
	
	public long getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(long errorCode) {
		this.errorCode = errorCode;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	public String getAppendMessage() {
		return appendMessage;
	}

	public void setAppendMessage(String appendMessage) {
		this.appendMessage = appendMessage;
	}

	public String getCommodity() {
		return commodity;
	}

	public void setCommodity(String commodity) {
		this.commodity = commodity;
	}

	public String getImg() {
		return img;
	}

	public void setImg(String img) {
		this.img = img;
	}

}
