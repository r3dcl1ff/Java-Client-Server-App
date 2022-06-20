import java.io.Serializable;
import java.util.Arrays;


public class UDPPacket implements Serializable {

	public int SN;
	
	public byte[] data;
	
	public boolean last;

	public UDPPacket(int seq, byte[] data, boolean last) {
		super();
		this.SN = seq;
		this.data = data;
		this.last = last;
	}

	public int getSN() {
		return SN;
	}

	public void setSN(int seq) {
		this.SN = seq;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	public boolean isLast() {
		return last;
	}

	public void setLast(boolean last) {
		this.last = last;
	}

	
	
}
