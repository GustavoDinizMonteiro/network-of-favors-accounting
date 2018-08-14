package fogbow.billing.model;

public class UserUsage {
	
	private String userId;
	private String resourceName;
	private double amount;
	private int hours;
	
	public UserUsage(String userId, String resourceName, double amount, int hours) {
		this.userId = userId;
		this.resourceName = resourceName;
		this.amount = amount;
		this.hours = hours;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getResourceName() {
		return resourceName;
	}

	public void setResourceName(String resourceName) {
		this.resourceName = resourceName;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public int getHours() {
		return hours;
	}

	public void setHours(int hours) {
		this.hours = hours;
	}
	

}
