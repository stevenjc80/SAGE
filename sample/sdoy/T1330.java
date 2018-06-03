

import java.io.*;
import java.util.Scanner;

public class T1330
{
	public static void main(String[] args) throws FileNotFoundException
	{
		// Fetch sample file and set up for parsing
		Scanner userInput = new Scanner(System.in);
		System.out.print("Please enter the samples file: ");
		String filename = userInput.nextLine(); 
		FileReader file = new FileReader(filename);
		Scanner fileInput = new Scanner(file);
		
		// Get threshold value
		System.out.print("Please enter the pass threshold (in MPa): ");
		double threshold = userInput.nextDouble();
		
		int testFailed = 0;
		// Analyze each sample line by line
		while(fileInput.hasNextLine()){
			
			// Parse sample name and score for analysis
			String currSample = fileInput.nextLine();
			String sampleName = currSample.substring(0,7);
			String sampleScore = currSample.substring(8);
			double currScore = Double.parseDouble(sampleScore);
			
			// Analyze for pass/fail
			if(currScore < threshold){
				System.out.println(sampleName + " failed!");
				testFailed++;
			}
		}
		
		// Print test results
		if (testFailed > 0){
			System.out.println("Tests failed: " + testFailed);
		} else {
			System.out.println("All tests passed!");
		}
		
		fileInput.close();
		userInput.close();
	}
}
