package fogbow.billing.model;

public class Usage {

	private String orderId;
	private String userId;
	private long time;
	
	public Usage(String orderId, String userId, long time) {
		this.orderId = orderId;
		this.userId = userId;
		this.time = time;
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

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}
	
	@Override
	public String toString() {
		return "Usage [orderId=" + orderId + ", userId=" + userId + ", time=" + time + "]";
	}

}
