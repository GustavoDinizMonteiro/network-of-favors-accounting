package fogbow.billing.model;

import java.sql.Timestamp;

public class TimestampTableEntry {
	
	private String orderId, resourceType, usage, userId, userName, requestingMember, providingMember;	
	private Timestamp start_time;
	private int duration;
	
	public TimestampTableEntry(String orderId, String resourceType, String usage,
			String userId, String userName, String requestingMember, String providingMember,
			Timestamp startTime, int duration) {
		
		this.orderId = orderId;
		this.resourceType = resourceType;
		this.usage = usage;
		this.userId = userId;
		this.userName = userName;
		this.requestingMember = requestingMember;
		this.providingMember = providingMember;
		this.start_time = startTime;
		this.duration = duration;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getResourceType() {
		return resourceType;
	}

	public void setResourceType(String resourceType) {
		this.resourceType = resourceType;
	}

	public String getUsage() {
		return usage;
	}

	public void setUsage(String usage) {
		this.usage = usage;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getRequestingMember() {
		return requestingMember;
	}

	public void setRequestingMember(String requestingMember) {
		this.requestingMember = requestingMember;
	}

	public String getProvidingMember() {
		return providingMember;
	}

	public void setProvidingMember(String providingMember) {
		this.providingMember = providingMember;
	}

	public Timestamp getStart_time() {
		return start_time;
	}

	public void setStart_time(Timestamp start_time) {
		this.start_time = start_time;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}
	
	@Override
	public String toString() {
		return "TimestampTableEntry [orderId=" + orderId + ", resourceType=" + resourceType + ", usage=" + usage
				+ ", userId=" + userId + ", userName=" + userName + ", requestingMember=" + requestingMember
				+ ", providingMember=" + providingMember + ", start_time=" + start_time + ", duration=" + duration
				+ "]";
	}
	
	
	

}
