import java.io.*;
import java.util.Scanner;
import java.util.StringTokenizer;


/**	This class encapsulates a report based on the testing of a student's
 *	program.
 *	
 *	For example:
 *	<xmp>
	********************************************************************
	    EECS 1021 E, 2015-16 W: Lab Test #1
	    Name: John Smith
	    Login: jsmith         YUID: 123456789
	********************************************************************
	
	  __________________________
	 /  Test 1: Passed          \
	/____________________________\______________________________________
	
	  __________________________
	 /  Test 2:                 \
	/____________________________\______________________________________
	
	--- Input ----------------------------------------------------------
	1
	
	--- Expected Output ------------------------------------------------
	Type an Integer to Factor:
	The smallest factor is: 1
	
	--- Tested Output --------------------------------------------------
	Type an Integer to Factor:
	The smallest factor is: 0
	
	--- Grader's Feedback ----------------------------------------------
	 - 
	____________________________________________________________________
	
	  __________________________
	 /  Tested Code             \
	/____________________________\______________________________________
	
	  1 | //
	  2 | // This utility class provides a method that returns the
	  3 | // smallest factor of an integer.
	  4 | //
	  5 | public class Factor
	  6 | {
	  7 | 	//
	  8 | 	// This function takes as its argument an integer C, and
	  9 | 	// it returns smallest integer that is a factor of C,
	 10 | 	// other than 1.
	 11 | 	//
	 12 | 	public static int smallestFactor(int C)
	 13 | 	{
	 14 | 		int fact = 0;
	 15 | 		for (int i = 2; i <= C; i++)
	 16 | 		{
	 17 | 			if (C % i == 0)
	 18 | 			{
	 19 | 				fact =  i;
	 20 | 				break;
	 21 | 			}
	 22 | 			
	 23 | 		}
	 24 | 		return fact;
	 25 | 		
	 26 | 	}
	 27 | }
	
	--- Grader's Feedback ----------------------------------------------
	 - 
	____________________________________________________________________
	
	  __________________________
	 /  Summary                 \
	/____________________________\______________________________________
	
	 [] (Outline deductions made. Include amount of deduction in [].)
	
	Grade: X/10
	____________________________________________________________________
 *	</xmp>
 *
 *	@author Steven J. Castellucci
 *	@version 1.0 - (11/2008)
 *	@version 1.1 - (01/2011): Added output from Ma's style checker.
 *	@version 1.2 - (02/2012): Changed output encoding to Unicode.
 *	@version 1.3 - (09/2012): Removed style checker option and
 *		added multiple source code files.  
 *	@version 2.0 - (02/2016): Changed header, added html tags, and
 *		added notes and solution sections.
 *	@version 2.0a - (02/2016): Changed encoding, removed total marks, used
 *		the <xmp> tag to ensure '<' in student's code is not interpreted
 *		as HTML (<pre> and <code> didn't work) .
 *	COPYRIGHT (C) 2008-2016 All Rights Reserved.
 */
public class TestReport
{
	/**	The width of the header decorations. */
	protected static final int WIDTH = 68;

	/**	The max width of the section title. */
	protected static final int MAX_SEC_TITLE = 24;

	/**	A description of the associated assignment. */
	protected String asgnName;

	/**	The student's CSE account name. */
	protected String studentLogin;

	/**	The student's name. */
	protected String studentName;

	/**	The student's ID. */
	protected String studentID;

	/**	The errors identified by the compiler. If the buffer is empty, the
	 *	program compiled without error. */
	protected StringBuffer compilationErrors;

	/**	The marking rubric or notes to be placed in the summary section. */
	protected StringBuffer summaryText;

	/**	The source code file(s) of the program being tested. */
	protected StringBuffer[] sourceCode;

	/**	The source code file(s) of the solution programs. */
	protected StringBuffer[] solutionCode;

	/**	An array of TestOutput objects. Each encapsulates the input, expected
	 *	result, and actual result of a test. */
	protected TestOutput[] tests;

	/**	The file to search for student information. */
	protected File distFile;


	/**	Initializes this object with the passed parameters.
	 *
	 *	@param asgnName the assignment name to use in the title
	 *	@param studentLogin the login of the student associated with this report
	 *	@param compilationErrors the output of javac (empty if compilation
	 *		completed without error).
	 *	@param sourceCode the program's source code
	 *	@param tests a TestOutput encapsulating each unit test conducted
	 *	@param distFile the class distribution file to search for student
	 *		name and id; /etc/passwd used if null	 	 
	 */
	public TestReport(String asgnName, String studentLogin,
		StringBuffer compilationErrors, StringBuffer[] sourceCode,
		TestOutput[] tests, File distFile)
	{
		this.asgnName = asgnName;
		this.studentLogin = studentLogin;
		this.compilationErrors = compilationErrors;
		this.sourceCode = sourceCode;
		this.tests = tests;
		this.distFile = distFile;
		String[] info = getStudentInfo(studentLogin, distFile).split("\t");
		if (distFile == null)
		{
			studentName = getStudentName(studentLogin);
		}
		else if (info.length > 3)
		{
			studentName = info[3];
			studentID = info[2];
		}
	}


	/**	Initializes this object with the passed parameters.
	 *
	 *	@param asgnName the assignment name to use in the title
	 *	@param studentLogin the login of the student associated with this report
	 *	@param compilationErrors the output of javac (empty if compilation
	 *		completed without error).
	 *	@param sourceCode the program's source code
	 *	@param tests a TestOutput encapsulating each unit test conducted
	 *	@param distFile the class distribution file to search for student
	 *		name and id; /etc/passwd used if null	 	 
	 *	@param summaryText text to place in the summary section
	 *	@param solutionCode the solution to the assignment
	 */
	public TestReport(String asgnName, String studentLogin,
		StringBuffer compilationErrors, StringBuffer[] sourceCode,
		TestOutput[] tests, File distFile, StringBuffer summaryText,
		StringBuffer[] solutionCode)
	{
		this(asgnName, studentLogin, compilationErrors, sourceCode, tests, distFile);
		this.summaryText = summaryText;
		this.solutionCode = solutionCode;
	}


	/**	Wraps the OutputStream in a PrintWriter and calls
	 *	generate(PrintWriter report).
	 *
	 *	@param out the OutputStream to which to print the report
	 *	@return a summary of the generated report
	 */
	public String generate(OutputStream out)
	{
		PrintWriter report = new PrintWriter(out);
		return generate(report);
	}


	/**	Creates a file with the student's login as its name, wraps
	 *	it in a PrintWriter and calls generate(PrintWriter report).
	 *
	 *	@param path the desired location of the report file
	 *	@return a summary of the generated report
	 *	@throws IOException if unable to create file
	 */
	public String generate(String path) throws IOException
	{
		PrintWriter report = new PrintWriter(path, "ISO-8859-1");
		//PrintWriter report = new PrintWriter(path, "UTF-8");
		// PrintWriter report = new PrintWriter(path, "Cp1252");
		return generate(report);
	}


	/**	Generates a testing report and writes it to the passed PrintWriter.
	 *
	 *	@param report the PrintWriter to which to write the report
	 *	@return a summary of the generated report
	 */
	public String generate(PrintWriter report)
	{
		int numPassed = 0;
		int numFailed = 0;
		int numExceededTime = 0;

		report.println("<xmp>");
		printTitle(report);
		report.println();
		report.println();
		report.println();

		if (compilationErrors.length() > 1)
		{
			printSectionHeader(report, "Compilation Errors");
			printLines(report, compilationErrors);
			printSectionFooter(report);
			report.println();
			report.println();
			report.println();
		}

		for (int i = 0; i < tests.length; i++)
		{
			String expOut = tests[i].getExpectedOutput().toString();
			String actOut = tests[i].getActualOutput().toString();

			if (tests[i].getExceededTime())
			{
				numExceededTime++;
				tests[i].getActualOutput().append(
					"\nAttention: Execution exceeded allotted time!\n");
			}

			if (expOut.equals(actOut))
			{
				numPassed++;
				printSectionHeader(report, "Test "+(i+1)+": Passed");
				report.println();
				report.println();
				report.println();
			}
			else
			{
				numFailed++;
				printSectionHeader(report, "Test "+(i+1)+":");
				report.println();

				printSubSectionHeader(report, "Input");
				printWhitespace(report, tests[i].getTestInput());
				report.println();

				printSubSectionHeader(report, "Expected Output");
				printWhitespace(report, tests[i].getExpectedOutput());
				report.println();

				printSubSectionHeader(report, "Tested Output");
				printWhitespace(report, tests[i].getActualOutput());
				report.println();

				printSubSectionHeader(report, "Grader's Feedback");
				report.println(" - ");
				report.println();
				printSectionFooter(report);
				report.println();
				report.println();
				report.println();
			}
		}

		for (StringBuffer file : sourceCode)
		{
			printSectionHeader(report, "Tested Code");
			report.println();
			printNumberedLines(report, file);
			report.println();
		}
		
		printSubSectionHeader(report, "Grader's Feedback");
		report.println(" - ");
		printSectionFooter(report);
		report.println();
		report.println();
		report.println();

		printSectionHeader(report, "Summary");
		report.println();
		report.println(" - ");
		report.println();
		if (summaryText != null)
		{
			report.println(summaryText.toString());
		}
		//report.println("Grade: X/10");
		printSectionFooter(report);
		report.println();
		report.println();
		report.println();
		
		for (StringBuffer file : solutionCode)
		{
			printSectionHeader(report, "Solution Code");
			report.println();
			printNumberedLines(report, file);
			report.println();
		}
		printSectionFooter(report);
		report.println();
		
		report.println("</xmp>");

		report.flush();
		report.close();

		StringBuilder summary = new StringBuilder("Passed: " + numPassed + "; Failed: " + numFailed);
		if (studentName == null || studentName.length() == 0)
		{
			summary.append("; Login not found");
		}
		if (compilationErrors.length() > 1)
		{
			summary.append("; Compilation errors occured");
		}
		if (numExceededTime > 0)
		{
			summary.append("; " + numExceededTime + " tests exceeded allotted time");
		}
		return summary.toString();
	}


	/**	Retrieves a student's name from the /etc/passwd file.
	 *
	 *	@param login the student's login
	 *	@return the student's name, or the empty string if the
	 *		passed login is not found
	 */
	protected String getStudentName(String login)
	{
		String name = "";

		try
		{
			ProcessBuilder pb = new ProcessBuilder("grep", "-w", login,
				"/etc/passwd");
			Process p = pb.start();
			Scanner result = new Scanner(p.getInputStream());

			if (result.hasNextLine())
			{
				// Entry found, parse name.
				StringTokenizer st = new StringTokenizer(result.nextLine(), ":");
				st.nextToken(); // login
				st.nextToken(); // password flag
				st.nextToken(); // user id
				st.nextToken(); // group id
				name = st.nextToken();
			}
		}
		catch (Exception e) {}

		return name;
	}


	/**	Retrieves a student's information from the passed distribution file.
	 *
	 *	@param login the student's login
	 *	@return the line in the file containing the student's information, or
	 *		the empty string if the	passed login is not found
	 */
	protected String getStudentInfo(String login, File distFile)
	{
		String line = "";
		boolean found = false;
		try
		{
			Scanner fileInput = new Scanner(distFile);
			while (!found && fileInput.hasNextLine())
			{
				line = fileInput.nextLine();
				String[] info = line.split("\t");
				// Check EECS and PPY
				found = login.equals(info[0].trim()) ||
					login.equals(info[1].trim());
			}
		}
		catch (Exception e) {}
		return found ? line : "";
	}


	/**	Prints a title for this report. The title consists of a course
	 *	assignment description and the student's login and name. The title has
	 *	a top and bottom border of * characters.
	 *
	 *	@param report the PrintWriter to which to write the footer
	 */
	protected void printTitle(PrintWriter report)
	{
		String border = repeatChar('*', WIDTH);
		report.println(border);
		report.println("    "+asgnName);
		report.println("    Name: "+studentName);
		if (studentID == null)
		{
			report.println("    Login: "+studentLogin);
		}
		else
		{
			report.println("    Login: "+String.format("%-15s", studentLogin)+
				"   YUID: "+studentID);
		}
		report.println(border);
	}


	/**	Prints the passed text. This method reads the StringBuffer line by line
	 *	and prints lines to the PrintWriter. Simply printing the entire
	 *	StringBuffer does not always output the correct newline character.
	 *
	 *	@param report the PrintWriter to which to write the text
	 *	@param text the text to print
	 */
	protected void printLines(PrintWriter report, StringBuffer text)
	{
		Scanner lines = new Scanner(text.toString());

		while (lines.hasNextLine())
		{
			report.println(lines.nextLine());
		}
	}


	/**	Prints the passed text with line numbers (e.g., 999 | text).
	 *
	 *	@param report the PrintWriter to which to write the text
	 *	@param text the text to print
	 */
	protected void printNumberedLines(PrintWriter report,
		StringBuffer text)
	{
		Scanner lines = new Scanner(text.toString());

		int n = 0;
		while (lines.hasNextLine())
		{
			n++;
			report.printf("%3d | ", n);
			report.println(lines.nextLine());
		}
	}


	/**	Prints a section header resembling a folder tab.
	 *
	 *	@param report the PrintWriter to which to write the header
	 *	@param sectionName the text to print in the header
	 */
	protected void printSectionHeader(PrintWriter report,
		String sectionName)
	{
		report.println("  "+repeatChar('_', 2+MAX_SEC_TITLE));

		// Truncate section name if < max.
		if (sectionName.length() > MAX_SEC_TITLE)
			sectionName = sectionName.substring(0, MAX_SEC_TITLE);

		report.println(" /  "+sectionName+
			repeatChar(' ', MAX_SEC_TITLE-sectionName.length())+"\\");
		report.println("/"+repeatChar('_', 2+MAX_SEC_TITLE+2)+"\\"+
			repeatChar('_', WIDTH-(2+2+MAX_SEC_TITLE+2)));
	}


	/**	Prints a subsection header consisting of - characters.
	 *
	 *	@param report the PrintWriter to which to write the header
	 *	@param sectionName the text to print in the header
	 */
	protected void printSubSectionHeader(PrintWriter report,
		String sectionName)
	{
		StringBuilder header = new StringBuilder(WIDTH);
		header.append("--- "+sectionName+" ");
		int tailLength = WIDTH - header.length();
		header.append(repeatChar('-', tailLength));
		report.println(header);
	}


	/**	Prints a section footer consisting of _ characters.
	 *
	 *	@param report the PrintWriter to which to write the footer
	 */
	protected void printSectionFooter(PrintWriter report)
	{
		report.println(repeatChar('_', WIDTH));
	}


	/**	Prints the passed text with characters inserted to view whitespace.
	 *
	 *	@param report the PrintWriter to which to write the text
	 *	@param text the text to print
	 */
	protected void printWhitespace(PrintWriter report, StringBuffer text)
	{
		StringBuffer sb = new StringBuffer(text.toString());

		// Insert characters to indicate whitespace.
		for (int i = 0; i < sb.length(); i++)
		{
			if (sb.charAt(i) == ' ')
			{
				sb.setCharAt(i, '\u00b7');
			}
			else if (sb.charAt(i) == '\t')
			{
				sb.insert(i, '\u00bb');
				i++;
			}
			else if (sb.charAt(i) == '\n')
			{
				sb.insert(i, '\u00b6');
				i++;
			}
		}

		printLines(report, sb);
	}


	private String repeatChar(char c, int n)
	{
		StringBuilder sb = new StringBuilder(n);
		for (int i = 0; i < n; i++)
		{
			sb.append(c);
		}
		return sb.toString();
	}


	/**	Used for debugging. */
	public static void main(String[] args)
	{
		StringBuffer ti1 = new StringBuffer("1 1\n2 2\n3 2\n");
		StringBuffer eo1 = new StringBuffer("blah blah\nblah\nblah\n");
		StringBuffer ao1 = new StringBuffer("duh\tduh\nduh\nduh\n");
		TestOutput to1 = new TestOutput(ti1, eo1);
		to1.setActualOutput(ao1);

		StringBuffer ti2 = new StringBuffer("123");
		StringBuffer eo2 = new StringBuffer("blah\nblah\nblah\n");
		StringBuffer ao2 = new StringBuffer("blah\nblah\nblah\n");
		TestOutput to2 = new TestOutput(ti2, eo2);
		to2.setActualOutput(ao2);

		TestOutput[] to = new TestOutput[2];
		to[0] = to1;
		to[1] = to2;

		StringBuffer[] sc = new StringBuffer[1];
		sc[0] = new StringBuffer("code\ncode\ncode\n");

		TestReport test = new TestReport("Lab Test 1", "cse91xxx",
			new StringBuffer(), sc, to, null);
		test.generate(System.out);
	}

}
