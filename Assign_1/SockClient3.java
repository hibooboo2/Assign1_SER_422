import java.net.*;
import java.io.*;

class SockClient {
	public static void main(String args[]) throws Exception {
		Socket sock = null;
		OutputStream out = null;
		InputStream in = null;
		int i1 = 0;
		int id = 0;

		if (args.length != 2) {
			System.out.println("USAGE: java SockClient id int1 \n"
					+ "Where id is an int from 0 to 255\n"
					+ "To reset count \n"
					+ "Use: java SockClient id 0\n"
					+ "Meaning you cant add 0 or any multiple of 256");
			System.exit(1);
		}
		try {
			i1 = Integer.parseInt(args[1]);
			id = Integer.parseInt(args[0]);
		} catch (NumberFormatException nfe) {
			
			System.out.println("Command line args must be integers " + args[0] + " " + args[1] );
			System.exit(2);
		}
		try {
			sock = new Socket("localhost", 8888);
			out = sock.getOutputStream();
			in = sock.getInputStream();
			out.write(id);
			out.write(i1);
			int result = in.read();
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
