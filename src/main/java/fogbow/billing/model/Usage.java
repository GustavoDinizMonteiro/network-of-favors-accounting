package fogbow.billing.model;

import java.sql.Timestamp;

public class Usage {

	private String orderId;
	private String userId;
	private String userName;
	private String requestingMember;
	private String providingMember;
	private String resourceType;
	private String spec;
	private Timestamp begin;
	private Timestamp end;
	private long duration;
	
	public Usage(String orderId, String userId, String userName,
			String requestingMember, String providingMember,
			String resourceType, String spec, Timestamp begin, Timestamp end, long duration) {
		this.orderId = orderId;
		this.userId = userId;
		this.userName = userName;
		this.requestingMember = requestingMember;
		this.providingMember = providingMember;
		this.resourceType = resourceType;
		this.spec = spec;
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

	public String getResourceType() {
		return resourceType;
	}

	public void setResourceType(String resourceType) {
		this.resourceType = resourceType;
	}

	public Timestamp getBegin() {
		return begin;
	}

	public void setBegin(Timestamp begin) {
		this.begin = begin;
	}

	public Timestamp getEnd() {
		return end;
	}

	public void setEnd(Timestamp end) {
		this.end = end;
	}

	public long getDuration() {
		return duration;
	}

	public void setDuration(long duration) {
		this.duration = duration;
	}
	
	public String getSpec() {
		return this.spec;
	}

	@Override
	public String toString() {
		return "Usage [orderId=" + orderId + ", userId=" + userId + ", userName=" + userName + ", requestingMember="
				+ requestingMember + ", providingMember=" + providingMember + ", resourceType=" + resourceType
				+ ", spec=" + spec + ", begin=" + begin + ", end=" + end + ", duration=" + duration + "]";
	}

	

}
