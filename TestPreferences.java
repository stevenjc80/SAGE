import java.io.*;
import java.util.*;


/**	This class encapsulates the parameters used for testing. The parameters are
 *	provided by a text file with the following format:
 *	<pre>
 *		Assignment Name
 *		Dist file for student info (put "default" to use /etc/passwd) 
 *		Location of java executable (put "default" to use /cs/local/bin/javac) 
 *		Main Class Name (don't include ".class")
 *		Source filenames to include in report (comma separated) 
 *		Solution filenames to include in report (comma separated, or "none")
 *		File containing summary notes or marking scheme (or put "none")
 *		test1_input_file.txt
 *		test1_output_file.txt
 *		test2_input_file.txt
 *		test2_output_file.txt
 *		... (additional tests as necessary)
 *	</pre>
 *	
 *	For example:
 *	<pre>
		CSE 1030 A, 2012-13 F: Assignment #1
		default
		default
		a1
		Factor.java
		Solution.java
		none
		in1.txt
		out1.txt
		in2.txt
		out2.txt
		in3.txt
		out3.txt
 *	</pre>
 *	     
 *	Paths to input and output files are relative to the directory containing the
 *	preferences file.
 *
 *	<p>Comments are prefaced with #.
 *
 *	@author Steven J. Castellucci
 *	@version 1.0 - (11/2008)
 *	@version 1.1 - (09/2012): Added options for a separate main class and for
 *		multiple source files. 
 *	@version 2.0 - (02/2016): Added options for class dist file, summary notes,
 *		and appending solution files  
 *	COPYRIGHT (C) 2008-2016 All Rights Reserved.
 */
public class TestPreferences
{
	/**	This assignment's name/description. */
	protected String asgnName;

	/**	The Dist file containing student information. */
	protected String distName;

	/**	The location of the java executable. */
	protected String exe;

	/**	The program to test. */
	protected String progName;

	/**	The source code files to include in the report. */
	protected String[] sourcesNames;

	/**	The solution files to include in the report. */
	protected String[] solutionNames;

	/**	The file containing text for the summary section. */
	protected String summaryName;

	/**	The directory containing the preference file. The path to the input and
	 *	output files are relative to this directory. */
	protected String workingDir;

	/**	The tests to use for testing. Each object encapsulates the test input,
	 *	expected output, and the actual output (to be provided later). */
	protected TestOutput[] tests;


	/**	Initializes this object.
	 *
	 *	@param filename the file (or path+file) containing testing preferences
	 *	@throws FileNotFoundException if file cannot be read
	 *	@throws NoSuchElementException if the preference file is not correctly
	 *		formatted
	 */
	public TestPreferences(String filename)
		throws FileNotFoundException, NoSuchElementException
	{
		this.workingDir = getWorkingDir(filename);
		parse(filename);
	}


	/**	Returns the name of the assignment as specified in the test
	 *	preferences.
	 *
	 *	@return the assignment name
	 */
	public String getAsgnName()
	{
		return asgnName;
	}


	/**	Returns the name of the dist file as specified in the test
	 *	preferences.
	 *
	 *	@return the dist filename
	 */
	public String getDistName()
	{
		return distName;
	}


	/**	Returns the path to the java exectuable.
	 *
	 *	@return the path to java
	 */
	public String getExe()
	{
		return exe;
	}


	/**	Returns the name of the program (main class) to run.
	 *
	 *	@return the name of the main class
	 */
	public String getProgName()
	{
		return progName;
	}


	/**	Returns the names of souce files to include in the report.
	 *
	 *	@return the names of souce files to include in the report
	 */
	public String[] getSourceNames()
	{
		return sourcesNames;
	}


	/**	Returns the names of solution files to include in the report.
	 *
	 *	@return the names of solution files to include in the report
	 */
	public String[] getSolutionNames()
	{
		return solutionNames;
	}


	/**	Returns the input provided for this test.
	 *
	 *	@return the test's input
	 */
	public TestOutput[] getTests()
	{
		return tests;
	}


	/**	Returns the name of summary file to include in the report.
	 *
	 *	@return the name of summary file to include in the report.
	 */
	public String getSummaryName()
	{
		return summaryName;
	}


	/**	Determines the path to the input/output files. */
	protected String getWorkingDir(String prefsPath)
	{
		int indexOfFileSeparator = prefsPath.lastIndexOf(File.separator);
		if (indexOfFileSeparator > -1)
		{
			return prefsPath.substring(0, indexOfFileSeparator+1);
		}
		else
		{
			return "";
		}
	}


	/**	Parses the passed parameter file and initializes this object according
	 *	to its contents.
	 *
	 *	@param filename the parameter filename
	 *	@throws FileNotFoundException if file cannot be read
	 *	@throws NoSuchElementException if the preference file is not correctly
	 *		formatted
	 */
	protected void parse(String filename)
		throws FileNotFoundException, NoSuchElementException
	{
		String line;
		Scanner parseFile = new Scanner(new File(filename));
		Vector<TestOutput> v = new Vector<TestOutput>();

		// Parse assignment name.
		line = parseFile.nextLine();
		while (line.length() == 0 || line.startsWith("#"))
		{
			line = parseFile.nextLine();
		}
		asgnName = line;

		// Parse dist filename.
		line = parseFile.nextLine();
		while (line.length() == 0 || line.startsWith("#"))
		{
			line = parseFile.nextLine();
		}
		distName = line.equals("default") ? null : workingDir + line;

		// Parse java path.
		line = parseFile.nextLine();
		while (line.length() == 0 || line.startsWith("#"))
		{
			line = parseFile.nextLine();
		}
		exe = line.equals("default") ? null : line;

		// Parse program name.
		line = parseFile.nextLine();
		while (line.length() == 0 || line.startsWith("#"))
		{
			line = parseFile.nextLine();
		}
		progName = line;

		// Parse source names.
		line = parseFile.nextLine();
		while (line.length() == 0 || line.startsWith("#"))
		{
			line = parseFile.nextLine();
		}
		sourcesNames = line.split(",");

		// Parse solution names.
		line = parseFile.nextLine();
		while (line.length() == 0 || line.startsWith("#"))
		{
			line = parseFile.nextLine();
		}
		solutionNames = line.equals("none") ? null : line.split(",");

		// Parse summary filename.
		line = parseFile.nextLine();
		while (line.length() == 0 || line.startsWith("#"))
		{
			line = parseFile.nextLine();
		}
		summaryName = line.equals("none") ? null : workingDir + line;

		// Parse tests.
		line = parseFile.nextLine();
		while (line.length() == 0 || line.startsWith("#"))
		{
			line = parseFile.nextLine();
		}
		do
		{
			v.add(parseTest(
				new Scanner(new File(workingDir + line.trim())),
				new Scanner(new File(workingDir +
					parseFile.nextLine().trim()))));
			if (parseFile.hasNextLine())
			{
				line = parseFile.nextLine();
			}
			while (parseFile.hasNextLine() &&
				(line.length() == 0 || line.startsWith("#")))
			{
				line = parseFile.nextLine();
			}
		}
		while (parseFile.hasNextLine());
		// Convert vector to array.
		tests = new TestOutput[v.size()];
		tests = v.toArray(tests);
	}


	/**	Creates a TestOutput object containing the test input and expected
	 *	output contained in the passed files.
	 *
	 *	@param inputFile the file containing the test input
	 *	@param expOutputFile the file containing the test's expected output
	 */
	protected TestOutput parseTest(Scanner inputFile, Scanner expOutputFile)
	{
		StringBuffer testInput = readFile(inputFile);
		StringBuffer expOutput = readFile(expOutputFile);
		return new TestOutput(testInput, expOutput);
	}


	/**	Reads-in the file represented by the Scanner.
	 *
	 *	@param file the file to read-in
	 *	@return a StringBuffer containing the file's contents
	 */
	public static StringBuffer readFile(Scanner file)
	{
		StringBuffer content = new StringBuffer();
		while (file.hasNextLine())
		{
			content.append(file.nextLine()+"\n");
		}
		return content;
	}


	/**	Used for debugging. */
	public static void main(String[] args) throws Exception
	{
		TestPreferences tp = new TestPreferences("prefs2.txt");
		System.out.println(tp.getAsgnName());
		System.out.println(tp.getProgName());
		for (TestOutput t : tp.getTests())
		{
			System.out.println("--- In ---");
			System.out.println(t.getTestInput().toString());
			System.out.println("--- Out ---");
			System.out.println(t.getExpectedOutput().toString());
		}
	}

}
