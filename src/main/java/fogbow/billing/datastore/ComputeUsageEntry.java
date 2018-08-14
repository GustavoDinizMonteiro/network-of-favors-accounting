package fogbow.billing.datastore;

import java.time.LocalDate;

public class ComputeUsageEntry {
	
	private String userId, orderId;
	
	private double vcpu, ram, disk;
	
	private LocalDate fulfilledDate, closedDate;
	
	public ComputeUsageEntry(String userId, String orderId, double vcpu, double ram, double disk,
			LocalDate fulfilledDate, LocalDate closedDate) {
		this.userId = userId;
		this.orderId = orderId;
		this.vcpu = vcpu;
		this.ram = ram;
		this.disk = disk;
		this.fulfilledDate = fulfilledDate;
		this.closedDate = closedDate;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public LocalDate getFulfilledDate() {
		return fulfilledDate;
	}

	public void setFulfilledDate(LocalDate fulfilledDate) {
		this.fulfilledDate = fulfilledDate;
	}

	public LocalDate getClosedDate() {
		return closedDate;
	}

	public void setClosedDate(LocalDate closedDate) {
		this.closedDate = closedDate;
	}

	public double getVcpu() {
		return vcpu;
	}

	public void setVcpu(double vcpu) {
		this.vcpu = vcpu;
	}

	public double getRam() {
		return ram;
	}

	public void setRam(double ram) {
		this.ram = ram;
	}

	public double getDisk() {
		return disk;
	}

	public void setDisk(double disk) {
		this.disk = disk;
	}
	
	
	

}
