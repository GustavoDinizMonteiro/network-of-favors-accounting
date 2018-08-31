package fogbow.billing.model;

import java.sql.Timestamp;

public class OrderRecord {

	private String orderId, resourceType, spec, userId, userName, requestingMember, providingMember;	
	private Timestamp start_time;
	private int duration;
	
	public OrderRecord(String orderId, String resourceType, String spec,
			String userId, String userName, String requestingMember, String providingMember,
			Timestamp startTime, int duration) {
		
		this.orderId = orderId;
		this.resourceType = resourceType;
		this.spec = spec;
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
		return resourceType.toUpperCase();
	}

	public void setResourceType(String resourceType) {
		this.resourceType = resourceType;
	}

	public String getSpec() {
		return spec;
	}

	public void setSpec(String spec) {
		this.spec = spec;
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
		return "OrderRecord [orderId=" + orderId + ", resourceType=" + resourceType + ", usage=" + spec
				+ ", userId=" + userId + ", userName=" + userName + ", requestingMember=" + requestingMember
				+ ", providingMember=" + providingMember + ", start_time=" + start_time + ", duration=" + duration
				+ "]";
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((orderId == null) ? 0 : orderId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		OrderRecord other = (OrderRecord) obj;
		if (orderId == null) {
			if (other.orderId != null)
				return false;
		} else if (!orderId.equals(other.orderId))
			return false;
		return true;
	}
	
	
	

}
