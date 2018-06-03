import java.io.*;
import java.util.*;


/**	This is the main class that tests students' submissions. Prior to running
 *	this program, the students' code should be compiled (see Compilation.java
 *	for options). In addition, test cases and other parameters for evaluating
 *	submissions should be specified in a correctly formatted preferences file
 *	(see TestPreferences.java for an example). 
 *	
 *	<p>For each submission, a subprocess is started to run a test. The working
 *	directory is set to the student's directory in the submit directory. The
 *	classpath includes that directory, the parent directory, and the result
 *	of calling 'sysclasspath'. The test input is passed to the subprocess and
 *	its output is compared to the expected output. This continues for all the
 *	tests, and the results are summarized in a text file that is saved to a
 *	newly created Results directory (see TestReport.java for an example report).  
 *
 *	<p>Usage:
 *	<pre>
	java TestDriver <i>submit</i> [-p <i>prefs</i>] [-r <i>results</i>] [-s <i>cse_dir</i>]
		<i>submit</i> is the directory containing all the submissions.
		-p <i>prefs</i> to override the default preferences file with <i>prefs</i>
		-r <i>results</i> to override the default results directory with <i>results</i>
		-s <i>cse_dir</i> to test only the individual submission listed.
 	</pre>
 *	
 *	For example:
 	<pre>
 	java TestDriver ./submit/a1 -p ./submit/a1/prefs.txt -r ./submit/a1/Results
 	java TestDriver . -s cse123456
 	java TestDriver .
 	</pre>       
 *
 *	For each tested submission, the console outputs the number of passed and
 *	failed tests, the number of tests that exceeded allotted time (e.g., due
 *	to an infinite loop), and a warning if the student's CSE username was not
 *	found in the system. The output also includes the start and completion time.
 *	<pre>
 	Processing...
	Current time: Fri Sep 14 12:23:21 EDT 2012
	Results in Results/
	
	cse12345: Passed: 5; Failed: 0
	cse23456: Passed: 4; Failed: 1
	cse34567: Passed: 3; Failed: 2; 1 tests exceeded allotted time
	cse45678: Passed: 0; Failed: 5; Login not found
	...
	
	Process completed successfully.
	Current time: Fri Sep 14 12:25:30 EDT 2012 
 	</pre> 
 *   
 *	@author Steven J. Castellucci
 *	@version 1.0 - (11/2008)
 *	@version 1.1 - (01/2011): Added output from Ma's style checker.
 *	@version 1.2 - (09/2012): Removed style checker option.
 *	@version 1.3 - (09/2014): Removed restriction of "cse" usernames
 *	COPYRIGHT (C) 2008-2014 All Rights Reserved.
 */
public class TestDriver
{
	/**	The directory containing all of the students' submissions. Each
	 *	student's submission should be in a subdirectory with his/her login as
	 *	the directory name. */
	protected String submitDir;

	/**	The directory that will contain the testing reports. */
	protected String resultsDir;

	/**	The submissions to test. Each directory should be named with a "cse"
	 *	login and contain a submission to be tested. */
	protected String[] students;

	/**	Hold the preferences specified for testing. */
	protected TestPreferences prefs;

	/**	The thread that runs the students' programs. */
	protected TestThread testThread;

	/**	The thread that runs the students' programs. */
	public static final String DEFAULT_RESULTS_DIR = "Results";

	/**	The thread that runs the students' programs. */
	public static final String DEFAULT_PREFS_FILE = "prefs.txt";

	/**	The allotted execution time. After executing for this duration, the
	 *	program will be terminated. */
	public static final long EXE_DURATION = 5000;

	/**	The time between successive monitoring of program execution. */
	public static final long EXE_INCREMENT = 100;


	/**	Initializes the object.
	 *
	 *	@param submitDir the directory containing all of the students'
	 *		submissions. Each student's submission should be in a
	 *		subdirectory with his/her login as the directory name.
	 *	@param prefsFile the name of the preference file to use.
	 *	@param resultsDir the name of the results directory. This directory
	 *		will contain the generated reports.
	 *	@throws FileNotFoundException if the preference file cannot be read
	 *	@throws NoSuchElementException if the preference file is not
	 *		orrectly formatted
	 */
	public TestDriver(String submitDir, String prefsFile, String resultsDir,
		String[] students) throws FileNotFoundException, NoSuchElementException
	{
		this.submitDir = submitDir;
		this.resultsDir = resultsDir;
		this.students = students;
		prefs = new TestPreferences(prefsFile);
		testThread = new TestThread();
	}


	public void process()
	{
		System.out.println();
		System.out.println("Processing...");
		System.out.println("Current time: "+(new Date()));
		StringBuffer blank = new StringBuffer(); // used to reset test results


		// Create results directory.
		File rd = new File(resultsDir);
		try
		{
			rd.mkdir();
		}
		catch (SecurityException se) {}
		if (rd.exists() && rd.isDirectory() && rd.canWrite())
		{
			System.out.println("Results in "+resultsDir+File.separator);
		}
		else
		{
			System.out.println("Error: Could not create results directory!");
			return;
		}
		System.out.println();
		
		
		// Prepare dist file, read summary file, read solution file(s).
		File distFile = new File(prefs.getDistName());
		StringBuffer summaryText = null;
		File f = null;
		try
		{
			f = new File(prefs.getSummaryName());
			summaryText = TestPreferences.readFile(new Scanner(
				f));
		}
		catch (FileNotFoundException fnfe)
		{
			System.out.println("Error: Could not open summary file: !");
			System.out.println("Mark: " + f.getAbsolutePath());
			return;
		}
		catch (Exception e) {}
		StringBuffer[] solutionCode = new StringBuffer[
			prefs.getSolutionNames().length];
		for (int i = 0; i < solutionCode.length; i++)
		{
			String filename = submitDir + File.separator + 
				prefs.getSolutionNames()[i];
			try
			{
				solutionCode[i] = TestPreferences.readFile(new Scanner(
					new File(filename)));
			}
			catch (FileNotFoundException fnfe)
			{
				System.out.println("Error: Could not open " + filename + "!");
				return;
			}
		}
		

		// Test each student's submission.
		for (String student : students)
		{
			if (student.indexOf(".") < 0)
			{
				System.out.print(student + ": ");
				String path = submitDir+File.separator+student;
				String compilationErrorsPath = path + File.separator +
					"compilation_errors.txt";


				// Read in source code.
				StringBuffer[] sourceCode = new StringBuffer[
					prefs.getSourceNames().length];
				for (int i = 0; i < sourceCode.length; i++)
				{
					String filename = path + File.separator +
						prefs.getSourceNames()[i];
					StringBuffer contents = new StringBuffer(
						"Error: " + filename + " could not be read!");
					try
					{
						contents = TestPreferences.readFile(new Scanner(
							new File(filename)));
					}
					catch (FileNotFoundException fnfe) {}
					sourceCode[i] = contents;
				}
				
					
				// Read in compilation errors.
				StringBuffer compilationErrors = blank;
				try
				{
					compilationErrors = TestPreferences.readFile(new Scanner(
						new File(compilationErrorsPath)));
				}
				catch (FileNotFoundException fnfe) {}


				// Conduct tests.
				TestOutput[] tests = prefs.getTests();
				long duration;
				for (TestOutput test : tests)
				{
					TestThread tt = new TestThread(path, prefs.getProgName(),
						test.getTestInput(), prefs.getExe());
					tt.start();
					duration = 0;
					try
					{
						// Sleep during execution.
						while (tt.isAlive() && duration < EXE_DURATION)
						{
							duration += EXE_INCREMENT;
							(Thread.currentThread()).sleep(EXE_INCREMENT);
						}
						// If program is still running, interrupt it.
						if (tt.isAlive())
						{
							tt.interrupt();
							test.setExceededTime(true);
							// Wait for thread to halt.
							while (tt.isAlive())
							{
								(Thread.currentThread()).sleep(EXE_INCREMENT);
							}
						}
					}
					catch (Exception e)
					{
						System.out.println("ERROR: TestDriver interrupted!");
						e.printStackTrace();
					}


					// Save test result to TestOutput.
					test.setActualOutput(TestPreferences.readFile(
						tt.getStdOutErr()));
				}


				// Generate report.
				TestReport report = new TestReport(prefs.getAsgnName(), student,
					compilationErrors, sourceCode, tests, distFile, summaryText,
					solutionCode);
				try
				{
					System.out.println(report.generate(resultsDir +
						File.separator + student + ".txt"));
				}
				catch (IOException ioe)
				{
					System.out.println("Error: Could not generate report!");
				}


				// Reset test output results.
				for (TestOutput test : tests)
				{
					test.setActualOutput(blank);
					test.setExceededTime(false);
				}
			}
		}
		System.out.println();
		System.out.println("Process completed successfully.");
		System.out.println("Current time: " + (new Date()));
	}


	public static void main(String[] args) throws FileNotFoundException
	{
		if (args.length < 1)
		{
			System.out.println("\nUsage: java TestDriver <submit> [-p <prefs>] [-r <results>] [-s <cse_dir>]\n");
			System.out.println("<submit> is the directory containing all the submissions.");
			System.out.println("-p <prefs> to override the default preferences file with <prefs>.");
			System.out.println("-r <results> to override the default results directory with <results>.");
			System.out.println("-s <cse_dir> to test only the individual submission listed.\n");

			System.exit(1);
		}

		boolean checkStyle = false;
		File submitDir = new File(args[0]);
		String prefs = DEFAULT_PREFS_FILE;
		String results = DEFAULT_RESULTS_DIR;
		String[] students = submitDir.list();
		for (int i = 1; i < args.length; i = i+2)
		{
			if (args[i].equals("-p"))
			{
				prefs = args[i+1];
			}
			else if (args[i].equals("-r"))
			{
				results = args[i+1];
			}
			else if (args[i].equals("-s"))
			{
				students = new String[1];
				students[0] = args[i+1];
			}
		}

		TestDriver td = new TestDriver(args[0], prefs, results, students);
		td.process();
	}

}
