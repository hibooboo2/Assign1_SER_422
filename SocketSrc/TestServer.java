import jamesLogger.Log;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


public class TestServer
{

	public static void
	testConfiguration(int passesPerConfig, int passDelay, int times, int totalIDs, int samenumArg, int resetArg, Log log)
	{

		int passesForThisConfig= passesPerConfig;
		float totalTimeTaken= 0;
		long startTests= System.currentTimeMillis() + passesForThisConfig * passDelay;
		for (int i= 0; i < passesForThisConfig; i++)
		{
			ExecutorService executor= Executors.newCachedThreadPool();
			long start= System.currentTimeMillis();
			for (int testNum= 0; testNum < times; testNum++)
			{
				executor.execute(new Tester(testNum, samenumArg, totalIDs, resetArg, log));
			}
			executor.shutdown();
			try
			{
				executor.awaitTermination(2, TimeUnit.MINUTES);
			}
			catch (InterruptedException e1)
			{
				log.log(3, "Waiting for threads to complete Timedout.");
			}
			long end= System.currentTimeMillis();
			float timeTaken= end - start;
			totalTimeTaken+= timeTaken;
			timeTaken/= 1000;
			log.log(2, "Pass:" + " Took: " + String.valueOf(timeTaken) + " Secs. ");
			try
			{
				Thread.sleep(passDelay);
			}
			catch (InterruptedException e)
			{
				// TODO Auto-generated catch block
				log.log(3, "Sleep failed.... Why?");
			}
			log.flush();
		}
		long endTests= System.currentTimeMillis();
		log.log(1, "Average Time: " + String.valueOf(totalTimeTaken / passesForThisConfig / 1000) + " Secs. "
				+ "Configuration: Connections:" + times + " TotalIDs:" + totalIDs + " Reset:" + resetArg + " SameNum:" + samenumArg);
		float timeTakenAll= endTests - startTests;
		log.log(2, "Total Time for all tests this configuration: " + String.valueOf(timeTakenAll / 1000) + " Secs.");
		log.flush();
	}

	public static void main(String[] args)
	{

		Log log= new Log(1000, true, 2, true, "Tester.log");
		int passDelay= 0;
		log.resetLog();
		if (args.length != 3)
		{
			log.log(1, "Must have 3 args to run.\n USAGE: java testMultipleConfigs numConfigs passesPerconfig");
		}
		int numConfigs= 12;
		int passesPerconfig= 5;
		int testMultipleConfigs= 1;
		try
		{
			testMultipleConfigs= Integer.parseInt(args[0]);
			numConfigs= Integer.parseInt(args[1]);
			passesPerconfig= Integer.parseInt(args[2]);
		}
		catch (Exception e)
		{
			log.log(1, "One of the args is not valid. Used defaults.");
		}
		int[][] testConfigs=
			{ {100, 1, 1, 0}, {100, 2, 1, 0}, {100, 4, 1, 0}, {100, 10, 1, 0}, {100, 10, 1, 0}, {100, 10, 0, 0}, {100, 10, 1, 1},
				{100, 10, 0, 1}, {100, 10, 0, 1}, {200, 10, 0, 1}, {300, 10, 0, 1}, {400, 10, 0, 1}, {1000, 25, 1, 0},
				{1000, 50, 1, 0}, {1000, 75, 1, 0}, {1000, 100, 1, 0}, {1000, 100, 1, 0}, {1000, 100, 0, 0}, {1000, 100, 1, 1},
				{1000, 100, 0, 1}, {1000, 100, 0, 1}, {2000, 100, 0, 1}, {3000, 100, 0, 1}, {4000, 100, 0, 1}};
		long start= System.currentTimeMillis();
		int cons= 0;
		if (testMultipleConfigs > 0 ? true : false)
		{
			for (int i= 0; i < numConfigs; i++)
			{
				TestServer.testConfiguration(passesPerconfig, passDelay, testConfigs[i][0], testConfigs[i][1], testConfigs[i][2],
						testConfigs[i][3], log);
				cons+= (passesPerconfig * (testConfigs[i][0]));
			}
		}
		else
		{
			TestServer.testConfiguration(passesPerconfig, passDelay, testConfigs[numConfigs - 1][0], testConfigs[numConfigs - 1][1],
					testConfigs[numConfigs - 1][2], testConfigs[numConfigs - 1][3], log);
			cons+= (passesPerconfig * (testConfigs[numConfigs - 1][0]));
		}
		long end= System.currentTimeMillis();
		start+= numConfigs * passesPerconfig * passDelay;
		float timeTaken= end - start;
		log.log(1, "Total Time for all Configurations: " + String.valueOf(timeTaken / 1000) + " Secs.");
		log.log(1, "Total Connections Attempted:" + cons);
		log.flush();
	}
}

class Tester implements Runnable
{

	private int		testnum			= 0;

	private boolean	samenum			= false;

	private int		totalClients	= 0;

	private boolean	resets			= false;

	private Log		log				= new Log();

	Tester(int num, int same, int totalIDs, int reset, Log log)
	{
		this.testnum= num;
		this.samenum= same > 0 ? true : false;
		this.totalClients= totalIDs;
		this.resets= reset > 0 ? true : false;
		this.log= log;
	}

	@Override
	public void run()
	{
		int id= 1;
		if (this.totalClients != 1)
		{
			id= (int) (Math.random() * this.totalClients);
		}
		int toadd= 10;
		if (this.samenum != true)
		{
			toadd= (int) (Math.random() * 100);
		}
		String[] theargs;
		if (this.resets && this.testnum % 20 == 0)
		{
			theargs= new String[] {String.valueOf(id), "reset"};
		}
		else
		{
			theargs= new String[] {String.valueOf(id), String.valueOf(toadd)};
		}
		try
		{
			SockClient6.main(theargs);
			this.log.log(3, "Test:" + this.testnum + " done");
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			this.log.log(3, "Failed to run test" + this.testnum);
		}
	}
}
