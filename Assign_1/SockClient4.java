import java.net.*;
import java.io.*;

class SockClient {
	public static void main(String args[]) throws Exception {
		Socket sock = null;
		DataOutputStream out = null;
		DataInputStream in = null;
		int i1 = 0;
		int id = 0;
		boolean reset = false;
		if (args.length != 2) {
			System.out.println("USAGE: java SockClient id int1 \n"
					+ "Where id and int1 are intgers\n" + "To reset count \n"
					+ "Use: java SockClient id reset\n");
			System.exit(1);
		}
		try {
			id = Integer.parseInt(args[0]);
			if (args[1].compareToIgnoreCase("reset") == 0) {
				reset = true;
			} else {
				i1 = Integer.parseInt(args[1]);
			}
		} catch (NumberFormatException nfe) {

			System.out
					.println("Command line args must be integers, int1 may be reset or an intger");
			System.exit(2);
		}
		try {
			sock = new Socket("localhost", 8888);
			out = new DataOutputStream(sock.getOutputStream());
			in = new DataInputStream(sock.getInputStream());
			out.writeInt(id);
			out.writeInt(i1);
			out.writeBoolean(reset);
			int result = in.readInt();
			System.out.println("Result is " + result);
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
