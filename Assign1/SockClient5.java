import java.net.*;
import java.io.*;

class SockClient
{
	public static void main(String args[]) throws Exception
	{
		Socket sock= null;
		DataOutputStream out= null;
		DataInputStream in= null;
		int i1= 0;
		String id= "Hi";
		boolean reset= false;
		if (!(args.length >= 2))
		{
			System.out.println("USAGE: java SockClient id int1 \n" + "Where id is a string and int1 is an integer. "
					+ "To reset count use reset as the int1.\n" + "EX: java SockClient id reset\n" + args.length);
			System.exit(1);
		}
		try
		{
			id= args[0];
			if (args[1].compareToIgnoreCase("reset") == 0)
			{
				reset= true;
			}
			else
			{
				i1= Integer.parseInt(args[1]);
			}
		}
		catch (NumberFormatException nfe)
		{

			System.out.println("Command line args must be integers for the int");
			System.exit(2);
		}
		try
		{
			sock= new Socket("localhost", 8888);
			System.out.println("Got socket!");
			out= new DataOutputStream(sock.getOutputStream());
			in= new DataInputStream(sock.getInputStream());
			out.writeUTF(id);
			out.writeInt(i1);
			out.writeBoolean(reset);
			System.out.println("Sent Info!");
			System.out.println("Attemptimg to add " + i1 + " to ID: " + id + " Reset Command: " + reset);
		}
		catch (Exception e)
		{
			// e.printStackTrace();
			System.out.println("Failed successful connect");
		}
		try
		{
			int result= in.readInt();
			System.out.println("Result is " + result);
			System.out.println("Success");
		}
		catch (Exception e)
		{
			// e.printStackTrace();
			System.out.println("Failed to Recieve");
		}
		finally
		{
			if (out != null)
				out.close();
			if (in != null)
				in.close();
			if (sock != null)
				sock.close();
		}
	}
}
