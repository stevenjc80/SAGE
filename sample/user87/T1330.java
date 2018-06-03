import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class T1330
{
	private static int a;
	private static int i;

	public static void main(String[] args) throws FileNotFoundException
	{
		System.out.println("Please input the samples file:");
		Scanner sf = new Scanner(System.in);
		String sampleFile = sf.next();
		System.out.println("Please enter the pass threshold (in Mpa):");
		double threshold = sf.nextDouble();
		Scanner sc = new Scanner(new File(sampleFile));
		String fullText;
		fullText = sc.nextLine();
		String[] field = fullText.split("\n");

		String[] field1 = field[0].split(",");
		Double sampleAmountOne = Double.parseDouble(field1[1]);
		if (sampleAmountOne < threshold){
			System.out.println( field1[0] + " failed!");
			i = i + 1;
		}
		
		for (; sc.hasNextLine();){
			String line = sc.nextLine();
			String[] fields = line.split(",");
			String sample = fields[0];
			Double sampleAmount = Double.parseDouble(fields[1]);
		
			if (sampleAmount < threshold){
				System.out.println(sample + " failed!");
				i = i + 1;
			}
			
		}
		sc.close();
		sf.close();
		
		a = i;

		if (a == 0){
				System.out.println("All tests passed!");
			}
			else
				System.out.println("Tests failed: " + a );

			}

			
			
		
		
		
		
	
}
