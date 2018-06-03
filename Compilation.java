import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;


/**	This class traverses the directories containing students'
 *	submitted Java programs. For each submission, a subprocess
 *	is started to compile the specified program. The working
 *	directory is set to the student's directory in the submit
 *	directory. The classpath includes that directory, the parent
 *	directory, and the result of calling 'sysclasspath'.
 *	
 *  <p>If compilation returns any errors/notes/warnings, they
 *  are sent to both the console and a file called
 *  "compilation_errors.txt". The file is placed in the same
 *  directory as the program that failed to compile.
 *	
 *	<p>Usage:
 *	<pre>
 *	java Compilation [-check | -a] [-j pathToJavac] <i>program_name submit_dir</i> 
 *		-check checks that the class file exists
 *		-a append any compilation errors to an existing log
 *	</pre>
 *	
 *	For example:
 *	<pre>
 *	java Compilation LT1 /cse/course/1020/submit/LT1
 *	java Compilation -check A1 .
 *	</pre       
 *
 *	@author Steven J. Castellucci
 *	@version 1.1 - (11/2008)
 *	@version 1.2 - (02/2012): Specified the location of the javac exe 
 *	@version 1.3 - (09/2012): Uses output of `sysclasspath`
 *	@version 1.4 - (10/2012): Allows appending to existing logs 
 *	@version 1.5 - (09/2014): Removed restriction of "cse" usernames
 *	@version 2.0 - (02/2016): Added option to specify path to javac
 *	 COPYRIGHT (C) 2008-2016 All Rights Reserved.
 */
public class Compilation
{
	/**	Indicates whether or not to append to existing compile
	 *	error log. */
	protected boolean append;

	/**	Indicates whether to compile or just check that a class
	 *	file is present. */
	protected boolean onlyCheck;

	/**	The directory containing students' submissions. */
	protected String submitDir;

	/**	The name of the program that should be compiled. */
	protected String programName;

	/**	The classpath to use. In order, it is the submit directory,
	 *	the student's directory, followed by the output of
	 *	<tt>/cs/local/bin/sysclasspath</tt>. */
	protected String classpath;

	/**	The fully qualified path name for the javac executable. */
	protected String EXE = "//cs//local//bin//javac";


	/**	Initializes the object.
	 *
	 *	@param dir The submit directory.
	 *	@param programName The name of the program to compile.
	 *	@param onlyCheck Indicates that no compilation should
	 *		occur. Only checks that a class file exists.
	 *	@param append Indicates that errors should be appended
	 *		to any existing compilation_error file.
	 */
	public Compilation(String dir, String programName,
		boolean onlyCheck, boolean append)
	{
		submitDir = dir;
		this.programName = programName;
		this.onlyCheck = onlyCheck;
		this.append = append;
		classpath = ".." + File.pathSeparator + "." +
			File.pathSeparator + getSysClasspath();
	}


	/**	Initializes the object.
	 *
	 *	@param dir The submit directory.
	 *	@param programName The name of the program to compile.
	 *	@param onlyCheck Indicates that no compilation should
	 *		occur. Only checks that a class file exists.
	 *	@param append Indicates that errors should be appended
	 *		to any existing compilation_error file.
	 *	@param exe The path to the javac executable (eg, if not
	 *		running on PRISM). Uses default if exe is null.
	 */
	public Compilation(String dir, String programName,
		boolean onlyCheck, boolean append, String exe)
	{
		this(dir, programName, onlyCheck, append);
		if (exe != null)
		{
			EXE = exe;
		}
	}


	public void process()
	{
		System.out.println();
		System.out.println("Processing...");
		try
		{
			String[] students = (new File(submitDir)).list();
			for (String s : students)
			{
				if (s.indexOf(".") < 0)
				{
					System.out.print(s + "...");
					String path = submitDir+File.separator+s;
					if (onlyCheck && isCompiled(path))
					{
						System.out.println("Present.");
					}
					else
					{
						compile(path);
					}
				}
			}
			System.out.println();
			System.out.println("Process completed successfully.");
		}
		catch (Exception e)
		{
			System.out.println("Error while processing!");
			e.printStackTrace();
		}
	}


	/**	Tests if the directory contains a class file corresponding
	 *	to the specified program.
	 *
	 *	@param dir The student directory containing the program.
	 */
	protected boolean isCompiled(String dir)
	{
		File classFile = new File(
			dir+File.separator+programName+".class");
		return classFile.exists();
	}


	/**	Spawns a sub-process to compile the Java program. If any
	 *	errors/Notes/Warnings are returned, they are sent to the
	 *	console.
	 *
	 *	@param dir The student directory containing the file for
	 *		 compilation.
	 *	@throws IOException if unable to start subprocess
	 *	@throws FileNotFoundException if unable to create
	 *		"compilation_errors.txt" file
	 */
	protected void compile(String dir) throws IOException
	{
		// Compile program.
		ProcessBuilder pb = new ProcessBuilder(
			EXE, "-cp", classpath, programName+".java");
		pb.redirectErrorStream(true);
		pb.directory(new File(dir));
		Process p = pb.start();
		Scanner result = new Scanner(p.getInputStream());

		// Output result.
		if (!result.hasNextLine())
		{
			System.out.println("Done.");
		}
		else
		{
			System.out.println("Attention!");
			//PrintWriter errorFile = new PrintWriter(
			//	dir+File.separator+"compilation_errors.txt");
			PrintWriter errorFile = new PrintWriter(
				new BufferedWriter(new FileWriter(
				dir+File.separator+"compilation_errors.txt", append)));
			while (result.hasNextLine())
			{
				String line = result.nextLine();
				System.out.println("\t" + line);
				errorFile.println(line);
			}
			errorFile.println();
			errorFile.flush();
		}
	}
	
	
	/**	Spawns a sub-process to run <tt>/cs/local/bin/sysclasspath</tt>.
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


	public static void main(String[] args)
	{
		boolean onlyCheck = false;
		boolean append = false;
		String dir = null;
		String name = null;
		String exe = null;

		// Process command-line arguments.
		if (args.length == 2)
		{
			onlyCheck = false;
			name = args[0];
			dir = args[1];
		}
		else if (args.length > 2)
		{
			for (int i = 0; i < args.length; i++)
			{
				if (args[i].equals("-check"))
				{
					onlyCheck = true;
				}
				else if (args[i].equals("-a"))
				{
					append = true;
				}
				else if (args[i].equals("-j"))
				{
					exe = args[i+1];
				}
			}
			name = args[args.length - 2];
			dir = args[args.length - 1];
		}
		else
		{
			System.out.println("\nUsage: java Compilation [-check | -a] " +
				"[-j pathToJavac] <program_name> <submit_dir>\n" +
				"\t-check checks that the class file exists\n" +
				"\t-a append any compilation errors to an existing logn\n" +
				"\t-j specify the path to the javac executable " +
				"(ie, when not running on PRISM)\n");
			System.exit(0);
		}

		// Process directories.
		Compilation c = new Compilation(dir, name, onlyCheck, append, exe);
		c.process();
	}
}
