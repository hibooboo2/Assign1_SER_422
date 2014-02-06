import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

class Sender {

	public static void main(String[] args) throws Exception {

		String msg = args[0];
		InetAddress group = InetAddress.getByName("225.6.7.8");
		MulticastSocket socket = new MulticastSocket(2222);
		String msgS= args[0];
		byte[] msgArray= new byte[1000];

		socket.joinGroup(group);
		DatagramPacket packet= new DatagramPacket(msg.getBytes(), msgS.length(), group, 2222);
		socket.send(packet);
		packet= new DatagramPacket(msgArray, msgArray.length);
		socket.receive(packet);
		System.out.println(new String(packet.getData(), 0, packet.getLength()));
		socket.leaveGroup(group);
	}
}
