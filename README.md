________________________________________________________________________

SAGEv2, Copyright (C) 2014, Steven J. Castellucci
________________________________________________________________________

SAGE was written to to automate the repetitive and cumbersome tasks
associated with grading CS1 and CS2 Java assignments. It features the
following benefits:

    - Batch compilation of all submissions and recording of any
      compilation errors
    - Evaluation of all submissions using user-specified test cases
    - Monitoring of programs for infinite loops
    - Generation of plain-text reports that summarizes compilation
      errors, testing results, and the source code for each submission

The generated reports can then be passed to TAs to provide additional
feedback and constructive comments. Each file in this project has its
own purpose, as listed below. Additional details are included in each
file.


Compilation.java:  Traverses the directories containing students'
    submitted Java programs, and compiles the specified program. If 
    compilation returns any errors/notes/warnings, they are sent to both
    the console and a file called "compilation_errors.txt".

TestPreferences.java:  Encapsulates the parameters used for testing.
    These include, assignment name, unit tests' input and output, name
    of main class, marking scheme, and solution code.

TestDriver.java:  This main class tests students' submissions. Prior to
    running this program, the students' code should be compiled (see
    Compilation.java). In addition, unit tests and other parameters
    should be specified in a correctly formatted preferences file (see
    TestPreferences.java).

TestThread.java:  Instances of this class spawn a sub-process that
    runs a unit test on student code.

TestOutput.java:  Encapsulates the input, expected output, and actual
    output of a unit test.

TestReport.java:  Encapsulates a report based on unit testing results of
    a student's program. If a "compilation_error.txt" file exists, its
    contents are included in the report. The generated reports are then
    given to TAs for grading and feedback.

Grade.java:  Parses each result file and outputs students' usernames,
    and grades, separated by commas. Each result file is summarized on a
    separate line.

mailResults.sh:  A Bash script to email students their results.
