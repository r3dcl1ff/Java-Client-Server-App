# Java-Client-Server-App

The Server/Client application comprises 5 classes:

1] UDPPacket for packet transmission with sequence number.
This class has functions that allow for fetching/setup of sequence numbers and data and  check/set the packet as the last segment 
indicator variable is added to determine if the packet is the last one being sent.

2] UDPAck: used to acknowledge reception for server/client side.

3] ByteConvert: this class provides two functions:

 toBytes(): uses ByteArrayOutStream class to covert the packet into bytes.

 toObject(): uses ByteArrayInputStream used to convert the packet back to its original data form.

4-5]Main programs, Server.java and Client.java perform the sending and receiving function.

Java in-built functions/package include:

•DatagramPacket(): encapsulation of messages 
•DatagramSocket(): sockets for interface between client and server
•InetAddress(): to retrieve the IPaddress of the server

The application includes the option to transmit data over UDP either as a STOP-and-Wait protocol (window size or using GoBackN protocol). 
In either case, the data is read from a file “file.txt”. 
In option 1 client does not send the next packet until an acknowledgement for the previous packet sent is received. 
In the event of packet loss the client will resend the last segment.
In option 2 the server sends a burst of 5 packets (window size) and then waits for acknowledgment for the first packet in the transferred window.
Once Ack is received, the client will send the next packet and move the corresponding window.
As default state, if the input is other than 1 or 2, the application will show an error message and ask for an input.
In order to test with actual network traffic, testing elements (prob, TIMER) have been incorporated to force such errors and check for flaws.
