import java.net.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.io.*;

class SockServer
{
	public static void main(String args[]) throws Exception
	{
		ServerSocket serv= null;
		ConcurrentHashMap<String,AtomicInteger> counts= new ConcurrentHashMap<String,AtomicInteger>();
		int cons= 0;
		int sleep= 1000;
		if (args.length == 1)
		{
			try
			{
				sleep= Integer.parseInt(args[0]);
			}
			catch (Exception e)
			{
				System.out.print("The sleep delay should be an intger. Starting server with delay of 1 sec.");
			}
		}
		serv= new ServerSocket(8888);
		System.out.println("Started...");
		while (serv.isBound() && !serv.isClosed())
		{
			System.out.println("Ready...");
			try
			{
				cons++;
				(new Connection(serv.accept(), counts, sleep, cons)).start();
			}
			catch (IOException e)
			{
				System.out.println("Failed to accept connection.");
				e.printStackTrace();
			}
		}
	}
}

class Connection extends Thread
{

	private Socket							sock	= null;
	ConcurrentHashMap<String,AtomicInteger>	counts;
	private int								sleep	= 0;
	private int								num;

	Connection(Socket sock, ConcurrentHashMap<String,AtomicInteger> counts, int sleep, int num)
	{
		this.sock= sock;
		this.counts= counts;
		this.sleep= sleep;
		this.num= num;
	}

	Connection()
	{
	}

	public void run()
	{
		DataInputStream in= null;
		DataOutputStream out= null;
		try
		{
			in= new DataInputStream(sock.getInputStream());
			out= new DataOutputStream(sock.getOutputStream());
			String id= in.readUTF();
			int x= in.readInt();
			boolean reset= in.readBoolean();
			if (!counts.containsKey(id))
			{
				counts.put(id, new AtomicInteger());
			}
			synchronized (counts.get(id))
			{
				if (reset)
				{
					System.out.println("Server received Reset command for ID:" + id);
					counts.get(id).set(0);
					Connection.sleep(sleep);
				}
				else
				{
					System.out.println("Server received " + x + " for ID: " + id + " " + this.num);
					counts.get(id).addAndGet(x);
					Connection.sleep(sleep);
				}
				System.out.println(counts.get(id).get() + " " + num + " Synced");
				out.writeInt(counts.get(id).get());
			}
			out.flush();
			System.out.println("Map: " + counts.toString() + "Cons: " + num);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if (out != null)
					out.close();
				if (in != null)
					in.close();
				if (sock != null)
					sock.close();
			}
			catch (IOException e)
			{
				System.out.println("Failed to close something. IOException.");
			}
		}
	}
}