import java.io.File;
import java.util.Scanner;

/*
A task in civil engineering is testing the strength of samples of concrete. The
samples are put under increasing pressure until they break. Create a class
called T1330.java that reads in a file with test results and outputs whether or
not all the tests passed. The program prompts for a pass/fail threshold (in MPa)
and an input file. Each line in the file has two comma-delimited fields: the 
sample name, and its breakpoint (in MPa). If a sample fails, output its name. 
After reading the file output the number of tests that failed, or "All tests 
passed!".

Important: Your prompts and other output should match those described here.
Marks will be deducted if there are spelling or spacing discrepancies.

Important: Your program should employ the programming approaches and style
presented in lectures. In particular, marks will be deducted if resources are
not closed.
*/

public class Solution
{

	public static void main(String[] args) throws Exception
	{
		int numFailed = 0;
		double threshold;
		String line;
		
		Scanner input = new Scanner(System.in);
		System.out.print("Please enter the samples file: ");
		Scanner fileInput = new Scanner(new File(input.nextLine()));
		System.out.print("Please enter the pass threshold (in MPa): ");
		threshold = Double.parseDouble(input.next());
		input.close();
		
		String[] fields;
		String sample;
		double breakingPoint;
		while (fileInput.hasNextLine())
		{
			line = fileInput.nextLine();
			fields = line.split(",");
			sample = fields[0];
			breakingPoint = Double.parseDouble(fields[1]);
			if (breakingPoint < threshold)
			{
				numFailed++;
				System.out.println(sample + " failed!");
			}
		}
		fileInput.close();
		
		if (numFailed > 0)
		{
			System.out.println("Tests failed: " + numFailed);
		}
		else
		{
			System.out.println("All tests passed!");
		}
	}

}
