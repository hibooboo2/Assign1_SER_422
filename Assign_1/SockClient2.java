import java.net.*;
import java.io.*;

class SockClient {
	public static void main(String args[]) throws Exception {
		Socket sock = null;
		OutputStream out = null;
		InputStream in = null;
		int i1 = 0;

		if (args.length != 1) {
			System.out.println("USAGE: java SockClient int1 \n"
					+ "To reset count \n"
					+ "Use: java SockClient 0\n"
					+ "Meaning you cant add 0 or any multiple of 256");
			System.exit(1);
		}
		try {
			i1 = Integer.parseInt(args[0]);
		} catch (NumberFormatException nfe) {
			
			System.out.println("Command line args must be integers");
			System.exit(2);
		}
		try {
			sock = new Socket("localhost", 8888);
			out = sock.getOutputStream();
			in = sock.getInputStream();

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
