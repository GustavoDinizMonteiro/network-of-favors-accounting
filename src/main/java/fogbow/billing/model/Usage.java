package fogbow.billing.model;

import java.sql.Timestamp;

public class Usage {

	private String orderId;
	private String userId;
	private Timestamp begin;
	private Timestamp end;
	private long duration;
	
	public Usage(String orderId, String userId, Timestamp begin, Timestamp end, long duration) {
		this.orderId = orderId;
		this.userId = userId;
		this.begin = begin;
		this.end = end;
		this.duration = duration;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public long getDuration() {
		return duration;
	}

	public void setDuration(long duration) {
		this.duration = duration;
	}
	
	public Timestamp getBegin() {
		return this.begin;
	}
	
	public Timestamp getEnd() {
		return this.end;
	}
	
	@Override
	public String toString() {
		return "Usage [orderId=" + orderId + ", userId=" + userId + ", time=" + duration + "]";
	}

}
