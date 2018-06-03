import java.io.*;
import java.util.Scanner;


/**	Instances of this class spawn a sub-process that executes a Java program of
 *	the specified name. Input to the program is provided by a specified file.
 *	The program's output can be accessed via a <code>Scanner</code> object.
 *
 *	<p>If the executed program contains an infinite loop, this thread can be
 *	interrupted. Doing so will terminate the sub-process and will gracefully
 *	halt this thread.
 *
 *	@author Steven J. Castellucci
 *	@version 1.0 - (11/2008)
 *	@version 1.1 - (02/2012): Added -ea option to enable assertions.
 *	@version 1.2 - (02/2012): Specified the location of the java exe
 *	@version 1.3 - (09/2012): Uses output of `sysclasspath`
 *	@version 2.0 - (09/2016): Added option to specify path to javac
 *	COPYRIGHT (C) 2008-2016 All Rights Reserved.
 */
public class TestThread extends Thread
{
	/*	The directory containing the program to test. */
	private String workingDir;

	/*	A buffer containing the merged output from <code>stdout</code> and
		<code>stderr</code> streams.  */
	private Scanner stdOutErr;

	/*	The contents of this <code>StringBuffer</code> will be redirected to
	 *	the program's <code>stdin</code>. */
	private StringBuffer input;

	/*	The name of the Java program to run. The name should represent the
	 *	class name, and therefore NOT have the ".class" extension. */
	private String progName;

	/**	The classpath to use. In order, it is the submit directory,
	 *	the student's directory, followed by the output of
	 *	<tt>/cs/local/bin/sysclasspath</tt>. */
	protected String classpath;

	/**	The fully qualified path name for the javac executable. */
	protected String EXE = "//cs//local//bin//java";


	/**	Initializes this object with the passed parameters.
	 *
	 *	@param workingDir the directory containing the program to test.
	 *	@param progName the path+name of the Java program to run. The name
	 *		should represent the class name, and therefore NOT have the
	 *		".class" extension.
	 *	@param input the input to be redirected to the program's
	 *		<code>stdin</code>.
	 */
	public TestThread(String workingDir, String progName, StringBuffer input)
	{
		super();
		this.workingDir = workingDir;
		this.progName = progName;
		this.input = input;
		classpath = ".." + File.pathSeparator + "." +
			File.pathSeparator + getSysClasspath();
	}


	/**	Initializes this object with the passed parameters.
	 *
	 *	@param workingDir the directory containing the program to test.
	 *	@param progName the path+name of the Java program to run. The name
	 *		should represent the class name, and therefore NOT have the
	 *		".class" extension.
	 *	@param input the input to be redirected to the program's
	 *		<code>stdin</code>.
	 *	@param exe The path to the javac executable (eg, if not
	 *		running on PRISM). Uses default if exe is null.
	 */
	public TestThread(String workingDir, String progName, StringBuffer input,
		String exe)
	{
		this(workingDir, progName, input);
		if (exe != null)
		{
			EXE = exe;
		}

	}


	/**	Initializes this object. */
	public TestThread()
	{
		this(".", null, null);
	}


	/**	Returns the buffer containing merged output from <code>stdout</code>
	 *	and <code>stderr</code> streams.
	 *
	 *	@return the output of the program.
	 */
	public Scanner getStdOutErr()
	{
		return stdOutErr;
	}


	/**	Sets the input for this test.
	 *
	 *	@param input the input for this test.
	 */
	public void setInputFile(StringBuffer input)
	{
		this.input = input;
	}


	/**	Sets the program for this test.
	 *
	 *	@param name the program for this test.
	 */
	public void setProgName(String name)
	{
		progName = name;
	}


	public void run()
	{
		// Declare and initialize variables.
		ProcessBuilder test = new ProcessBuilder(
			EXE, "-ea", "-cp", classpath, progName);
		test.redirectErrorStream(true); // combines stdout & stderr
		test.directory(new File(workingDir));
		Process process = null;
		boolean procStarted = false;
		boolean procFinished = false;

		// Run the test.
		try
		{
			// Start process.
			process = test.start();
			stdOutErr = new Scanner(process.getInputStream());
			procStarted = true;

			// Pipe-in input file.
			PrintStream stdIn = new PrintStream(process.getOutputStream(), true);
			stdIn.print(input);

			// Wait for process termination.
			process.waitFor();
			procFinished = true;
		}
		catch (InterruptedException ie)
		{
			process.destroy();
		}
		catch (Exception e)
		{
			System.out.println("ERROR: Unable to start test!");
			e.printStackTrace();
		}
	}


	/**	Spawns a sub-process to run /cs/local/bin/sysclasspath.
	 *
	 *	@return the output of sysclasspath, or the empty string
	 *		 if an error occurred.
	 */
	protected String getSysClasspath()
	{
		String result = "";
		try
		{
			ProcessBuilder pb = new ProcessBuilder(
				"//cs//local//bin//sysclasspath");
			Process p = pb.start();
			Scanner output = new Scanner(p.getInputStream());
			result = output.nextLine();
		}
		catch (Exception e) {}
		return result;
	}


	/**	Demonstrates this class by running a program that contains an
	 *	infinite loop and then terminating it after 10 seconds. This method
	 *	can also serve as an example of how to use this class. */
	public static void main(String[] args)
	{
		final long MAX_DURATION = 10000;
		final long INCREMENT = 100;
		boolean executedTooLong = false;

		System.out.println("Running InfiniteLoop...");
		TestThread tt = new TestThread(".", "InfiniteLoop", new StringBuffer());
		tt.start();
		long duration = 0;

		try
		{
			// Sleep during execution.
			while (tt.isAlive() && duration < MAX_DURATION)
			{
				duration += INCREMENT;
				(Thread.currentThread()).sleep(INCREMENT);
			}
			// If program is still running, interrupt it.
			if (tt.isAlive())
			{
				tt.interrupt();
				executedTooLong = true;
			}
			// Wait for thread to halt.
			while (tt.isAlive())
			{
				(Thread.currentThread()).sleep(INCREMENT);
			}
		}
		catch (Exception e)
		{
			System.out.println("ERROR: TestDriver interrupted.");
			e.printStackTrace();
		}

		System.out.println("Done.");
	}
}


/*	This class encapsulates a program with an infinite loop. */
class InfiniteLoop
{
	public static void main(String[] args) throws Exception
	{
		int i = 0;
		while(true)
		{
			i++;
		}
	}
}
