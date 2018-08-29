package fogbow.billing.model;

public class ComputeUsage extends Usage{

	// In vCPU
	private int cpu;
	
	// in GB
	private int ram;
	
	public ComputeUsage(String orderId, String userId, long time, int cpu, int ram) {
		super(orderId, userId, time);
		
		this.cpu = cpu;
		this.ram = ram;
	}

	public int getCpu() {
		return cpu;
	}

	public void setCpu(int cpu) {
		this.cpu = cpu;
	}

	public int getRam() {
		return ram;
	}

	public void setRam(int ram) {
		this.ram = ram;
	}
	
	@Override
	public String toString() {
		return super.toString() + "ComputeUsage [cpu=" + cpu + ", ram=" + ram + "]";
	}

}
