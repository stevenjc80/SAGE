import java.util.Scanner;
import java.io.*;


/**	This class parses the result files and summarizes each grade.
 *	Each line of output contains a username, student number, and grade,
 *	separated by a comma. The output can be redirected to a file ending
 *	in ".csv", which can be opened by most spreadsheet applications. 
 *
 *	<p>Usage:
 *	<pre>
 *	java Grade <i>results_dir</i>
 *	</pre>
 *	
 *	For example:
 *	<pre>
 *	java Grade ./Results
 *	</pre       
 * 
 *	@author Steven J. Castellucci
 *	@version 1.1 - (11/2008)
 *	@version 1.2 - (09/2012): Added option to specify results directory
 *	@version 1.3 - (09/2014): Removed restriction of "cse" usernames and
 *		fix a bug where Scanner would not read a mixed character encoding
 *		file to its end  
 *	@version 2.0 - (02/2016): Parsing grade now works if TA omits space;
 *		output changed to have username (EECS or PPY), student number,
 *		name, and grade
 *	@version 2.1 - (03/2016): Now ignores backup files created by Nedit (*.bck) 
 *	COPYRIGHT (C) 2008-2016 All Rights Reserved.
 */
public class Grade
{
	public static void main(String[] args) throws Exception
	{
		if (args.length == 0)
		{
			System.out.println("\nUsage: java Grade <results_dir>\n");
			System.exit(0);
		}
		
		File resultsDirectory = new File(args[0]);
		File files[] = resultsDirectory.listFiles();

		String line = "";
		for (File f : files)
		{
			if (!f.getName().endsWith(".bck")) // ignore nedit backup files
			{
				Scanner input = new Scanner(new FileInputStream(f));
				while (input.hasNextLine() && !line.contains("Name:"))
				{
					line = input.nextLine();
				}
				String details1 = line;              // contains name
				String details2 = input.nextLine();  // contains login and stuid
				boolean gradeFound = false;
				while (input.hasNextLine() && !gradeFound)
				{
					line = input.nextLine();
					if (line.startsWith("Grade:"))
					{
						gradeFound = true;
					}
				}
				if (gradeFound)
				{
					String name  = parseName(details1);
					String login = parseLogin(details2);
					String stuid = parseStuID(details2);
					String grade = parseGrade(line);
					System.out.println(stuid + "," + login + "," +
						"\"" + name + "\"" + "," + grade);
				}
				else
				{
					System.out.println("Grade not found for " + f.getName());
				}
			}
		}
	}

	private static String parseName(String line)
	{
		String tag = "Name:";
		int start = line.indexOf(tag) + tag.length() + 1;
		return line.substring(start).trim();
	}
	
	private static String parseLogin(String line)
	{
		String tag = "Login:";
		String nextTag = "YUID:";
		int start = line.indexOf(tag) + tag.length() + 1;
		int end = line.indexOf(nextTag);
		return line.substring(start, end).trim();
	}
	
	private static String parseStuID(String line)
	{
		String tag = "YUID:";
		int start = line.indexOf(tag) + tag.length() + 1;
		return line.substring(start).trim();
	}
	
	private static String parseGrade(String line)
	{
		int start = line.indexOf(":") + 1;
		int end = line.indexOf("/");
		return line.substring(start, end).trim();
	}
}