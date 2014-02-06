import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import simple.Logger.Logger;

class SockClient6 implements Runnable
{

	private final int		id;

	private final int		num;

	private final boolean	reset;

	private final boolean	shutdown;

	private final Logger		log;

	SockClient6(int id, int numToAdd, boolean reset, boolean shutdown, Logger log)
	{

		this.id= id;
		this.num= numToAdd;
		this.reset= reset;
		this.shutdown= shutdown;
		this.log= log;
	}

	@Override
	public void run()
	{
		Socket sock= null;
		DataOutputStream out= null;
		DataInputStream in= null;
		try
		{
			sock= new Socket("localhost", 8888);
			this.log.log(6, "Got socket!");
			out= new DataOutputStream(sock.getOutputStream());
			in= new DataInputStream(sock.getInputStream());
			out.writeBoolean(this.shutdown);
			out.writeInt(this.id);
			out.writeInt(this.num);
			out.writeBoolean(this.reset);
			this.log.log(6, "Sent Info!");
			this.log.log(4, "Attemptimg to add " + this.num + " to ID: " + this.id + " Reset Command: " + this.reset);
		}
		catch (Exception e)
		{
			/* e.printStackTrace(); */
			this.log.log(7, "Failed to Connect");
			try
			{
				if (out != null)
				{
					out.close();
				}
				if (in != null)
				{
					in.close();
				}
				if (sock != null)
				{
					sock.close();
				}
			}
			catch (IOException e1)
			{
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		if (sock != null && !sock.isClosed())
		{
			try
			{
				int result= in.readInt();
				this.log.log(5, "Result is " + result);
				this.log.log(7, "Success");
			}
			catch (Exception e)
			{/* e.printStackTrace(); */
				this.log.log(7, "Failed to Recieve");
			}
			finally
			{
				try
				{
					if (out != null)
					{
						out.close();
					}
					if (in != null)
					{
						in.close();
					}
					if (sock != null)
					{
						sock.close();
					}
				}
				catch (IOException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		this.log.flush();
	}

	public static void main(String args[]) throws Exception
	{

		Logger log= new Logger(1000, true, 7, false, "ClientLog.log", "Log");
		int id= 0;
		int i1=43;
		boolean reset = false;
		boolean shutdown =false;
		if (!(args.length >= 2))
		{
			log.log(1, "USAGE: java SockClient id int1 \n" + "Where id and int1 are intgers. " + "To reset count use reset as the int1.\n"
					+ "EX: java SockClient id reset\n" + args.length);
			System.exit(1);
		}
		try
		{
			id= Integer.parseInt(args[0]);
			if (args[1].compareToIgnoreCase("reset") == 0)
			{
				reset= true;
			}
			else if (args[1].compareToIgnoreCase("shutdown") == 0)
			{
				shutdown= true;
			}
			else
			{
				i1= Integer.parseInt(args[1]);
			}
		}
		catch (NumberFormatException nfe)
		{
			log.log(1, "Command line args must be integers for the int");
			System.exit(2);
		}
		SockClient6 client= new SockClient6(id, i1, reset, shutdown, log);
		new Thread(client).start();
	}
}
