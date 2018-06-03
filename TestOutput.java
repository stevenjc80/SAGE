
/**	This class encapsulates the input, expected output, and actual output of a
 *	unit test.
 *
 *	@author Steven J. Castellucci
 *	@version 1.0 - (11/2008)
 *	COPYRIGHT (C) 2008 All Rights Reserved.
 */
public class TestOutput
{
	/**	Indicates the test exceeded its allotted execution time. This is a
	 *	symptom of an infinite loop. */
	protected boolean exceededTimeFlag;

	/**	The results of the tests. */
	protected StringBuffer actualOutput;

	/**	The (i.e., correct) expected results of the tests. */
	protected StringBuffer expectedOutput;

	/**	The input provided for this test. */
	protected StringBuffer testInput;


	/**	Initializes this object.
	 *
	 *	@param testInput the input provided
	 *	@param expectedOutput the correct test result
	 */
	public TestOutput(StringBuffer testInput,
		StringBuffer expectedOutput)
	{
		this.testInput = testInput;
		this.expectedOutput = expectedOutput;
	}


	/**	Records the passed StringBuffer as the output for this test.
	 *
	 *	@param output the result returned by the tested program
	 */
	public void setActualOutput(StringBuffer output)
	{
		actualOutput = output;
	}


	/**	Returns the tested program's result for this test.
	 *
	 *	@return the test's result
	 */
	public StringBuffer getActualOutput()
	{
		return actualOutput;
	}


	/**	Returns the correct result for this test.
	 *
	 *	@return the test's correct result
	 */
	public StringBuffer getExpectedOutput()
	{
		return expectedOutput;
	}


	/**	Returns the input provided for this test.
	 *
	 *	@return the test's input
	 */
	public StringBuffer getTestInput()
	{
		return testInput;
	}


	/**	Returns whether or not this test exceeded the allotted execution time.
	 *	If true, it is a symptom of an infinite loop.
	 *
	 *	@return true if this test exceeded its allotted time
	 */
	public boolean getExceededTime()
	{
		return exceededTimeFlag;
	}


	/**	Sets the "exceeded time" status.
	 *
	 *	@param b the status to set
	 */
	public void setExceededTime(boolean b)
	{
		exceededTimeFlag = b;
	}

}
