import java.io.Serializable;


public class UDPAck implements Serializable {
	
	private int packet;

	public UDPAck(int packet) {
		super();
		this.packet = packet;
	}

	public int getPacket() {
		return packet;
	}

	public void setPacket(int packet) {
		this.packet = packet;
	}
	
	

}