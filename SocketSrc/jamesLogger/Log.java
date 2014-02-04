
package jamesLogger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * This class is used to avoid just printing to the console. A simple logger that is easy to use and light weight,
 * as well as thread safe. Great for debugging and watching what happens as your program executes.
 * 
 * @author James Carl Harris
 * @version 0.5
 * 
 */
public class Log
{

	/**
	 * Pos is used with {@link Log#buffer} to manage the Buffer for writing lines to the log file.
	 * 
	 * @see Log#msgBuffer
	 */
	private int			pos			= 0;

	/**
	 * This is used as the size of the buffer for messages to be written.
	 * 
	 * @see Log#msgBuffer
	 */
	private int			buffer		= 100;

	/**
	 * This is the buffer for the logger. Used to minimize the number of file open and closes.
	 */
	private String[]	msgBuffer	= new String[this.buffer];

	/**
	 * This determines if the log writes to a file.
	 */
	private boolean		toFile		= false;

	/**
	 * Used to determine what level messages are logged. Similar to setting a log to info or debug, however this has
	 * no predetermined log levels. Think of level 1 as Highest priority, and lower levels of priority any thing greater.
	 */
	private int			maxLevel	= 3;

	/**
	 * This determines if the log logs all messages from 1 to the max, or just the max and 1. True logs all levels.
	 */
	private boolean		tree		= true;

	/**
	 * This is the name of the log file for this logger.
	 */
	private String		logFileName	= "LogFile.txt";

	/**
	 * The folder where the logs are stored. Starts from Current Directory.
	 */
	private String		logFolder	= System.getProperty("user.dir") + "\\Log";

	/**
	 * This is the constructor to create a custom logger.
	 * 
	 * @param logBufferSize
	 *            Determines log buffer size.
	 * @param writeToFile
	 *            Determines weather or not to write to file.
	 * @param lvl
	 *            Max level of messages to log. Not maximum number of messages to log.
	 * @param tree
	 *            Determines if the logger logs as a tree or just max level and level 1. True logs all levels
	 * @param logFileName
	 */
	public Log(int logBufferSize, boolean writeToFile, int lvl, boolean tree, String logFileName)
	{

		this.buffer= logBufferSize;
		this.msgBuffer= new String[this.buffer];
		this.toFile= writeToFile;
		this.tree= tree;
		this.maxLevel= lvl;
		this.logFileName= logFileName;
		this.makeFile();
	}

	/**
	 * Makes a new logger with defaults and user defined write to file or not.
	 * 
	 * @param writeToFile
	 */
	public Log(boolean writeToFile)
	{

		this.toFile= writeToFile;
		this.makeFile();
	}

	/**
	 * Makes a generic logger. That prints to console with buffer of 100 and logging all messages up to Level 3.
	 */
	public Log()
	{

		this.msgBuffer= null;
		this.toFile= false;
		this.makeFile();
	}

	/**
	 * This function returns the current location of the log folder.
	 * 
	 * @return the logFolder
	 */
	public String getLogFolder()
	{

		return this.logFolder;
	}

	/**
	 * Sets the log folder location.
	 * 
	 * @param folderName
	 *            Folder where the log will be stored relative to the Current Working directory.
	 */
	public void setLogFolder(String folderName)
	{

		this.logFolder= System.getProperty("user.dir") + "\\" + folderName;
	}

	/**
	 * Adds the message to the log buffer. lvl is the priority level of the message.
	 * 
	 * @param lvl
	 *            Level of message.
	 * @param msg
	 *            Message to be written.
	 */
	public synchronized void log(int lvl, String msg)
	{

		if (!this.toFile)
		{
			if ((this.tree && lvl <= this.maxLevel) || lvl == 1)
			{
				System.out.println(msg);
			}
			else if (!this.tree && lvl == this.maxLevel)
			{
				System.out.println(msg);
			}
		}
		else
		{
			if ((this.tree && lvl <= this.maxLevel) || lvl == 1)
			{
				this.addToLog(msg);
			}
			else if (!this.tree && lvl == this.maxLevel)
			{
				this.addToLog(msg);
			}
		}
	}

	/**
	 * Writes all messages in the <code>this.msgBuffer</code> to the log file.
	 */
	private synchronized void writeToFile()
	{

		File logFile= new File(this.logFolder + "\\" + this.logFileName);
		if (!logFile.exists())
		{
			try
			{
				logFile.createNewFile();
			}
			catch (IOException e)
			{
				System.out.println("IOExeception from writing to log. Can't access log file or make one." + logFile.getAbsolutePath());
			}
		}
		PrintWriter out= null;
		try
		{
			out= new PrintWriter(new BufferedWriter(new FileWriter(logFile, true)));
			for (int i= 0; i < this.pos; i++)
			{
				out.println(this.msgBuffer[i]);
			}
			out.flush();
			this.msgBuffer= new String[this.buffer];
			this.pos= 0;
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally
		{
			if (out != null)
			{
				out.close();
			}
		}
	}

	/**
	 * Used to add messages to the log buffer.
	 * 
	 * @param msg
	 *            Message to add.
	 */
	private synchronized void addToLog(String msg)
	{

		if (this.pos == this.buffer)
		{
			this.writeToFile();
		}
		this.msgBuffer[this.pos]= msg;
		this.pos++;
	}

	/**
	 * Flushes the log buffer. Used to ensure all messages are written to file.
	 */
	public synchronized void flush()
	{

		this.writeToFile();
	}

	/**
	 * Clears the log file for this logger.
	 */
	public synchronized void resetLog()
	{

		File logFile= new File(this.logFileName);
		logFile.delete();
	}

	/**
	 * Makes a file for the logger to use.
	 */
	private void makeFile()
	{

		this.makeFolder();
		File logFile= new File(this.logFolder + "\\" + this.logFileName);
		if (!logFile.exists())
		{
			try
			{
				logFile.createNewFile();
			}
			catch (IOException e)
			{
				System.out.println("IOExeception from writing to log. Can't access log file or make one." + logFile.getAbsolutePath());
				e.printStackTrace();
			}
		}
	}

	private void makeFolder()
	{

		File f= new File(this.logFolder);
		try
		{
			if (f.mkdirs())
			{
				System.out.println("Directory Created");
			}
			else
			{
				System.out.println("Directory is not created");
				System.out.println("Working Directory = " + System.getProperty("user.dir"));
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
