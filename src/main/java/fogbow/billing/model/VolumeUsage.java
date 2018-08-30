package fogbow.billing.model;

import java.sql.Timestamp;

public class VolumeUsage extends Usage{

	// In GB
	private int volumeSize;
	
	public VolumeUsage(String orderId, String userId, Timestamp begin, Timestamp end, long duration, int volumeSize) {
		super(orderId, userId, begin, end, duration);
		
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
