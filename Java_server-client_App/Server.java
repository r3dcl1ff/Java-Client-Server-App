
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;


public class Server {
	
	/// testing probability to lose packets during transmission
	public static final double prob = 0.1;

	public static void main(String[] args) throws Exception{
		//specifying a port for socket, in this case port 4545
		DatagramSocket serverSocket = new DatagramSocket(4546);
		
		// base size in bytes of a serialized RDTPacket object 
		byte[] receiveData = new byte[1024];
		// creating a variable for incoming packet number
		int waitingFor = 0;
		
		ArrayList<UDPPacket> receiveBuffer = new ArrayList<UDPPacket>();
		
		boolean end = false;
		
		while(!end){
			
			System.out.println("Waiting for packet...");
			
			// receiving the packet
			DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
			serverSocket.receive(receivePacket);
			
			// converting the received packet from bytes to an UDPPacket object
			UDPPacket packet = (UDPPacket) ByteConvert.toObject(receivePacket.getData());
			
			System.out.println("Packet with sequence number " + packet.getSN() + " received (last: " + packet.isLast() + " )");
		
			/*checking if the packet received is the last one. 
			 If it is, store it in the buffer and exit the loop
			 If it is not the last, check if waitingFor value matches the received packets sequence number, then store it in buffer and increase waitingFor
			 If waitingFr and received packet sequence number do not match, discard the packet
			 */
			if(packet.getSN() == waitingFor && packet.isLast()){
				
				waitingFor++;
				receiveBuffer.add(packet);
				
				System.out.println("Last packet received");
				
				end = true;
				
			}else if(packet.getSN() == waitingFor){
				waitingFor++;
				receiveBuffer.add(packet);
				System.out.println("Packed stored in buffer");
			}else{
				System.out.println("Packet discarded for not being in order)");
			}
			
			// create an Acknowledge object
			UDPAck ackObject = new UDPAck(waitingFor);
			
			// converting the Acknowledge object to bytes before transfer
			byte[] ackBytes = ByteConvert.toBytes(ackObject);
			
			
			DatagramPacket ackPacket = new DatagramPacket(ackBytes, ackBytes.length, receivePacket.getAddress(), receivePacket.getPort());
			
			// transmission after testing for probability of packet loss
			if(Math.random() > prob){
				serverSocket.send(ackPacket);
				System.out.println("Sent acknowledgemnt for sequence number:"+ackObject.getPacket());
			}else{
				System.out.println("ERROR: Lost acknowledgement with sequence number " + ackObject.getPacket()+"\n\n");
			}
			
			
			

		}
		
		serverSocket.close();
		// printout the data received
		System.out.println("\n\n ------------ DATA ---------------- ");
		
		for(UDPPacket p : receiveBuffer){
			for(byte b: p.getData()){
				System.out.print((char) b);
			}
		}
		
	}
	
	
}//end of program
