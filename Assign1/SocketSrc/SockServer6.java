import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import simple.Logger.Logger;

class SockServer6
{

	public static void main(String args[]) throws Exception
	{

		Logger log= new Logger(100, true, 6, true, "ServerLog.log", "Log");
		log.resetLog();
		ServerSocket serv= null;
		ConcurrentHashMap<Integer,AtomicInteger> counts= new ConcurrentHashMap<Integer,AtomicInteger>();
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
		log.log(2, "Started...");
		ExecutorService executor= Executors.newCachedThreadPool();

		while (serv.isBound() && !serv.isClosed())
		{
			log.log(3, "Ready...");
			try
			{
				cons++;
				executor.execute(new Connection(serv.accept(), counts, sleep, cons, log, serv));
				log.log(8, "" + Thread.activeCount());
			}
			catch (IOException e)
			{
				log.log(4, "Failed to accept connection.");
				e.printStackTrace();
			}
		}
		log.log(1, "Server no longer accepting connections. Finishing all tasks, then exiting.");
		executor.shutdown();
		executor.awaitTermination(2, TimeUnit.MINUTES);
		log.flush();
	}
}

class Connection implements Runnable
{

	private Socket								sock	= null;

	ConcurrentHashMap<Integer,AtomicInteger>	counts;

	private int									sleep	= 0;

	private int									num;

	private Logger									log		= new Logger();

	private ServerSocket						serv	= null;

	/**
	 * @param sock
	 *            This is the socket being used.
	 * @param counts
	 *            ConcrrentHashMap where the key is an Integer and the value is and AtomicIntger.
	 * @param sleep
	 * @param num
	 * @param log
	 * @param serv
	 */
	Connection(Socket sock, ConcurrentHashMap<Integer,AtomicInteger> counts, int sleep, int num, Logger log, ServerSocket serv)
	{

		this.sock= sock;
		this.counts= counts;
		this.sleep= sleep;
		this.num= num;
		this.log= log;
		this.serv= serv;
	}

	Connection()
	{

	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run()
	{

		DataInputStream in= null;
		DataOutputStream out= null;
		try
		{
			in= new DataInputStream(this.sock.getInputStream());
			out= new DataOutputStream(this.sock.getOutputStream());
			if (in.readBoolean())
			{
				this.log.log(1, "Server recevied shutdown signal.");
				this.serv.close();
			}
			int id= in.readInt();
			int x= in.readInt();
			boolean reset= in.readBoolean();
			if (!this.counts.containsKey(id))
			{
				this.counts.put(id, new AtomicInteger());
			}
			out.writeInt(this.addToMap(id, x, reset).get());
			this.log.log(5, this.counts.get(id).get() + " " + this.num + " Synced");
			out.flush();
			this.log.log(4, "Map: " + this.counts.toString() + "Cons: " + this.num);
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
				{
					out.close();
				}
				if (in != null)
				{
					in.close();
				}
				if (this.sock != null)
				{
					this.sock.close();
				}
			}
			catch (IOException e)
			{
				this.log.log(10, "Failed to close something. IOException.");
			}
		}
	}

	/**
	 * Adds the Int x to the ConcurrentHashmap for the server.
	 * 
	 * @param id
	 *            Key for the client in the ConcurrentHashmap.
	 * @param x
	 *            Int to add to the value for the client.
	 * @param reset
	 *            Decides if the value gets reset.
	 * @return The new Value for that key.
	 * @throws InterruptedException
	 */
	private AtomicInteger addToMap(int id, int x, boolean reset) throws InterruptedException
	{

		synchronized (this.counts.get(id))
		{
			if (reset)
			{
				this.log.log(5, "Server received Reset command for ID:" + id);
				this.counts.get(id).set(0);
				Thread.sleep(this.sleep);
			}
			else
			{
				this.log.log(5, "Server received " + x + " for ID: " + id + " " + this.num);
				this.counts.get(id).addAndGet(x);
				Thread.sleep(this.sleep);
			}
			return this.counts.get(id);
		}
	}
}
