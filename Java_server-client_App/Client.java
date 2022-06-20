
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;

import java.util.Scanner;


public class Client{
	
	// Testing variables and probability to lose packets during transmission
	public static double prob = 0.1;
	// timeout in ms before re-sending non-acknowledged packets
	public static int TIMER = 30;
	
	public static void transfer(String data, int window) throws IOException, ClassNotFoundException {
		// sequence number for the last packet sent 
				int SN = 0;
				
				// sequence number for the last acknowledged packet
				int waitingForAck = 0;
												
				int segmentSize=window*2;
				
				// converting input data to bytes
				byte[] fileBytes = data.getBytes();
				// receiver address set to localhost
				
				DatagramSocket clientSocket= new DatagramSocket();
				InetAddress IPAddress=InetAddress.getByName("localhost");
				
				// creating a list to store all the sent packets
				ArrayList<UDPPacket> sent = new ArrayList<UDPPacket>();
								
				// determining the sequence number of the last packet 
				int lastSeq = (int) Math.ceil( (double) fileBytes.length / segmentSize);

				System.out.println("Number of packets to send: " + lastSeq);

				while(true){

					// creating a loop to transmit packets to the server
					while(SN - waitingForAck < window && SN < lastSeq){
						// creating an array to store part of the bytes to send
						byte[] packet = new byte[segmentSize];

						// Copying a segment of data bytes to an array
						packet = Arrays.copyOfRange(fileBytes, SN*segmentSize, SN*segmentSize + segmentSize);

						// creating a UDPPacket object
						UDPPacket packetBlock = new UDPPacket(SN, packet, (SN == lastSeq-1) ? true : false);

						// converting the UDPPacket object to bytes
						byte[] sendData = ByteConvert.toBytes(packetBlock);

						// crafting a packet to transmit
						DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 4546 );

						System.out.println("Sending packet with sequence number " + SN );

						// adding a packet to the sent list
						sent.add(packetBlock);
						
						// testing for probability of packet loss
						if(Math.random() > prob){
							clientSocket.send(sendPacket);
						}else{
							System.out.println("\n \n ERROR: Lost packet with sequence number " + SN+"\n\n");
						}

						// increasing sequence number
						SN++;

					} // terminating sending sequence while byte array for the ACK is sent by the receiving endpoint
					byte[] ack = new byte[40];
					
					// crafting a packet for the ACK 
					DatagramPacket ackReceived = new DatagramPacket(ack, ack.length);
					
					try{
						// setting time for acknowledgement, in the event of failure, jump to catch
						clientSocket.setSoTimeout(TIMER);
						
						// receiving ack packet
						clientSocket.receive(ackReceived);
						
						// converting from bytes to object
						UDPAck ackObject = (UDPAck) ByteConvert.toObject(ackReceived.getData());
						
						System.out.println("Received ACK for " + ackObject.getPacket());
						
						// stop transmission if the acknowledgement received is for the last frame
						if(ackObject.getPacket() == lastSeq){
							break;
						}
						// resetting the value for variable waitingForAck to the next frame for which acknowledgement is expected
						waitingForAck = Math.max(waitingForAck, ackObject.getPacket());
						
					}catch(SocketTimeoutException e){
						// resending all sent packets which have not been acknowledged
						
						for(int i = waitingForAck; i < SN; i++){
							
							// converting UDPPacket object to bytes
							byte[] sendData = ByteConvert.toBytes(sent.get(i));

							// crafting the packet
							DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 4546 );
							
							// transmission after possible packet loss over transfer 
							if(Math.random() > prob){
								clientSocket.send(sendPacket);
							}else{
								System.out.println("\n\n ERROR: Lost packet with sequence number: " + sent.get(i).getSN());
							}

							System.out.println("Resending packet with sequence number " + sent.get(i).getSN() +  " and size " + sendData.length + " bytes");
						}
					}
					
				
				}
				clientSocket.close();
				System.out.println("____________________________________\nSuccesfully transfered");
	}

	public static void main(String[] args) throws Exception{
		
		
		Scanner in = new Scanner(System.in);
		Boolean indicator=true;
		//read data from file
		System.out.println("Reading data from file....");
		String data=new String(Files.readAllBytes(Paths.get("/home/redcliff/Desktop/CAMN/file.txt")));
		System.out.println("Data read...."+"Data size: " + data.length() + " bytes\n\n");
		
		
		while(indicator) {
			System.out.println("Transfer methods available:\n");
			System.out.println("1. STOP and WAIT");
			System.out.println("2. GoBackN with windows size 5");
			System.out.println("Input your choice:");
			int choice=in.nextInt();
		
			switch(choice) {
			case 1:
				transfer(data,1);
				indicator=false;
				break;
		
			case 2:
				transfer(data,5);
				indicator=false;
				break;
			
			default:
				System.out.print("Wrong input ! Try Again");
			
			} // end of switch-case
			
		}//end of while loop for the menu
	}//end of main() function

}//end of current program
