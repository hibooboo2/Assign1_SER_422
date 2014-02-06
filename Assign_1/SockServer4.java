import java.net.*;
import java.util.HashMap;
import java.io.*;

class SockServer {
	public static void main(String args[]) throws Exception {

		HashMap<Integer, Integer> counts = new HashMap<Integer, Integer>();
		ServerSocket serv = null;
		DataInputStream in = null;
		DataOutputStream out = null;
		Socket sock = null;

		try {
			serv = new ServerSocket(8888);
		} catch (Exception e) {
			e.printStackTrace();
		}
		while (serv.isBound() && !serv.isClosed()) {
			System.out.println("Ready...");
			try {
				sock = serv.accept();
				in = new DataInputStream(sock.getInputStream());
				out = new DataOutputStream(sock.getOutputStream());
				int id = in.readInt();
				int x = in.readInt();
				boolean reset = in.readBoolean();
				if (!counts.containsKey(id)) {
					counts.put(id, 0);
				}
				if (reset) {
					System.out.println("Server received Reset command for ID:"
							+ id);
					counts.put(id, 0);
				} else {
					System.out.println("Server received " + x + " for ID: "
							+ id);
					counts.put(id, (counts.get(id) + x));
				}
				out.writeInt(counts.get(id));
				out.flush();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (out != null)
					out.close();
				if (in != null)
					in.close();
				if (sock != null)
					sock.close();
			}
		}
	}
}
