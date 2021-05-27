package de.telekom.bzarei.sst;

public class Event {
	
	private String message;
	private int messageId;

	public void setEventMsg(String msg) {
		message = msg;
	}
	
	public String getEventMsg() {
		return message;
	}
	
	public void setEventId(int id) {
		messageId = id;
	}
	
	public int getEventId() {
		return messageId;
	}
}
