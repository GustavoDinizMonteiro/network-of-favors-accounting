package fogbow.billing.model;

public class VolumeUsage extends Usage{

	// In GB
	private int volumeSize;
	
	public VolumeUsage(String orderId, String userId, long time, int volumeSize) {
		super(orderId, userId, time);
		
		this.volumeSize = volumeSize;
	}

	public int getVolumeSize() {
		return volumeSize;
	}
	
	public void setVolumeSize(int volumeSize) {
		this.volumeSize = volumeSize;
	}
	
	@Override
	public String toString() {
		return super.toString() + "VolumeUsage [volumeSize=" + volumeSize + "]";
	}

}
