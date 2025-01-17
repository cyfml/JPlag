
# JPlag - End-To-End Testing
With the help of the end-to-end module, changes to the detection of JPlag are to be tested.
With the help of elaborated plagiarism, which has been worked out from suggestions in the literature on the topic of "plagiarism detection and avoidance", a wide range of detectable changes can be covered. The selected plagiarisms are the decisive factor here as to whether a change in recognition can be perceived. 

## References
These elaborations provide basic ideas on how a modification of the plagiarized source code can look or be adapted.
These code adaptations refer to a wide range of changes starting from
adding/removing comments to architectural changes in the deliverables.

The following elaborations were used to be able to create the plagiarisms with the largest coverage:
- [Detecting Source Code Plagiarism on Introductory Programming Course Assignments Using a Bytecode Approach - Oscar Karnalim](https://ieeexplore.ieee.org/abstract/document/7910274 "Detecting Source Code Plagiarism on Introductory Programming Course Assignments Using a Bytecode Approach - Oscar Karnalim")
- [Detecting Disguised Plagiarism - Hatem A. Mahmoud](https://arxiv.org/abs/1711.02149 "Detecting Disguised Plagiarism - Hatem A. Mahmoud")
- [Instructor-centric source code plagiarism detection and plagiarism corpus](https://dl.acm.org/doi/abs/10.1145/2325296.2325328 "Instructor-centric source code plagiarism detection and plagiarism corpus")

## Steps Towards Plagiarism
The following changes were applied to sample tasks to create test cases:
<ul type="1">
	<li>Inserting comments or empty lines (normalization level)</li>
	<li>Changing variable names or function names (normalization level)</li>
	<li>Insertion of unnecessary or changed code lines (token generation)</li>
	<li>Changing the program flow (token generation) (statments and functions must be independent of each other)</li>
		<ul type="1">
			<li>Variable declaration at the beginning of the program</li>
			<li>Combining declarations of variables</li>
			<li>Reuse of the same variable for other functions</li>
		</ul>
	<li>Changing control structures</li>
		<ul type="1">
			<li>for(...) to while(...)</li>
			<li>if(...) to switch-case</li>
		</ul>
	<li>Modification of expressions</li>
		<ul type="1">
			<li>(X < Y) to !(X >= Y) and ++x to x = x + 1</li>
		</ul>
	<li>Splitting and merging statements</li>
		<ul type="1">
			<li>x = getSomeValue(); y = x- z; to y = (getSomeValue() - Z;</li>
		</ul>
</ul>

More detailed information about the creation as well as about the subject of the issue can be found in the issue [Develop an end-to-end testing strategy](https://github.com/jplag/JPlag/issues/193 "Develop an end-to-end testing strategy").

**The changes listed above have been developed and evaluated for purely scientific purposes and are not intended to be used for plagiarism in the public or private domain.**

Software is according to [§ 2 of the copyright law](https://www.gesetze-im-internet.de/urhg/__2.html "§ 2 of the copyright law") a protected work that may not be plagiarized. 

## JPlag - End-To-End TestSuite Structure
The construction of an end-to-end test is done with the help of the JPlag api. 
The tests are generated dynamically according to the existing test data and allow the creation of end-to-end tests for all supported languages of JPlag without having to make any changes to the code.
The helper loads the existing test data from the designated directory and creates dynamic tests for the individual directories. It is therefore possible to create different test classes for the different languages.
- JPlagTestSuiteHelper:

```JAVA 
public static Map<LanguageOption, Map<String, Path>> getAllLanguageResources()
```

The list of languages created in this way and their associated data are dynamically generated into test cases in the Test Suite.

```JAVA
 Collection<DynamicTest> dynamicOverAllTest()
```

To be able to distinguish in which domain of the recognition changes have occurred, fine granular test cases are used. These are composed of the changes already mentioned above. The plagiarism is compared with the original delivery and thus it is possible to detect and test small sections of the recognition. 

The comparative values were discussed and tested. The following results of the JPlag scan are used for the comparison:
1. minimal similarity as `float`
2. maximum similarity as `float`
3. matched token number as `int`

The comparative values were disscussed and elaborated in the issue [End-to-end testing - "comparative values"](https://github.com/jplag/JPlag/issues/548 "End-to-end testing - \"comparative values\""). 

Additionally, it is possible to create several options for the test data. More information about the test options can be found at [JPlag - option variants for the end-to-end tests #590](https://github.com/jplag/JPlag/issues/590 "JPlag - option variants for the end-to-end tests #590"). Currently, various settings are supported by the `minimumTokenMatch`.  This can be extended as desired in the record class `Options`.

The current JPlag scans will be compared with the stored ones.
This was done by storing the data in a *.json file which is read at the beginning of each test run.

### JSON Result Structure

The structures of the Json file can be traced using the individual record classen which can be found under `de.jplag.endtoend.model`.
The outer structure of the JSON file is recorded in the `ResultDescription` record. 
The record contains a map of several options and the corresponding results. 
The internal structure consists of several `Option` records, each of which contains information about the current configuration for the test run. 
Thus the results can be kept apart from the other configurations. 
The test results for the specified options are also specified in the object. This consists of the `ExpectedResult` record which contains the results of the detection.

Here the herachie is as follows:

```JSON
[{
      "options":{
         "minimum_token_match":"int"
      },
      "tests":{
         "languageIdentifier":{
            "minimal_similarity":"float",
            "maximum_similarity":"float",
            "matched_token_number":"int"
         },
         "/..."
      }
   },
   "options":{
      "minimum_token_match":"int"
   },
   "tests":{
      "languageIdentifier":{
         "minimal_similarity":"float",
         "maximum_similarity":"float",
         "matched_token_number":"int"
      },
      {
         "/..."
      }
   }
}]
```

--- 

## Create New Language End-To-End Tests

This section explains how to create new end-to-end tests in the existing test suite. 
### Creating The Plagiarism
Before you add a new language to the end-to-end tests I would like to point out that the quality of the tests depends dreadfully on the plagiarism techniques you choose which were explained in section [Steps Towards Plagiarism](#steps-towards-plagiarism).
If you need more information about the creation of plans for this purpose, you can also read the elaborations that can be found under [References](#references).
The more varied changes you apply, the more accurate the end-to-end tests for the language will be.

In the following an example is shown which is in the JavaEndToEnd tests and is used.

**Changing control structures for(…) to while(…):**

```JAVA
//base class
public class SortAlgo {
//...
public void BubbleSortWithoutRecursion(Integer arr[]) {
		for(int i = arr.length; i > 1 ; i--) {
			for(int innerCounter = 0; innerCounter < arr.length-1; innerCounter++)
			{
				if (arr[innerCounter] > arr[innerCounter + 1]) {
					swap(arr, innerCounter, (innerCounter + 1));
				}
			}
		}
	}
//...
}
```

```JAVA
//created plagiarism
public class SortAlgo5{
//...
public void BubbleSortWithoutRecursion(Integer arr[]) {
		int i = arr.length;
		while(i > 1)
		{
			int innerCounter = 0;
			while(innerCounter < arr.length -1)
			{
				if (arr[innerCounter] > arr[innerCounter + 1]) {
					swap(arr, innerCounter, (innerCounter + 1));
				}
				innerCounter++;
			}
			I--;
		}
	}
//...
}
```
### Copying Plagiarism To The Resources

The plagiarisms created in [Creating The Plagiarism](#creating-the-plagiarism) must now be copied to the corresponding resources folder. It is important not to mix the languages of the plagiarisms or to copy the data into bottle resource paths.

- At the path `JPlag/jplag.endToEndTesting/src/test/resources/languageTestFiles` a new folder for the language should be created if it does not already exist. For example `[...]/resources/languageTestFiles/JAVA`. If you have plagiarized several different code samples, you can also create additional subfolders under the newly created folder for example `[...]/resources/languageTestFiles/JAVA/sortAlgo`.

It is important to note that the resource folder name must be the same as the language identifier name in JPlag/Language. 
Otherwise, the language option cannot be parsed correctly to the enum-type which you can found in every language module under `Language.java` `IDENTIFIER`

Once the tests have been run for the first time, the information for the tests is stored in the folder `../target/testing-directory-submission/LANGUAGE`.  This data can be copied to the path `[...]/resources/results/LANGUAGE`. Each subdirectory gets its result JSON file as `[...]/resources/results/LANGUAGE/TEST_SUITE_NAME.json`. Once the test data has been copied, the end-to-end tests can be successfully tested. As soon as a change in the detection takes place, the results will differ from the stored results and the tests will fail if the results have changed.

### Extending The Comparison Value

As already described, the current comparisons in the end-to-end test treat the values of `minimal similarity`, `maximum similarity`, and `matched token number`.
As soon as there is a need to extend these comparison values, this section describes how this can be achieved.
Beforehand, however, this should be discussed in a new issue about this need.

- For new comparison values these properties must be extended in the `ExpectedResult` record at the package `de.jplag.endtoend.model`. Here it is sufficient to add the values in the record and to enter the JSON name as `@JsonProperty("json_name")`.

```JAVA
public record ExpectedResult(
		@JsonProperty("minimal_similarity") float resultSimilarityMinimum,
        @JsonProperty("maximum_similarity") float resultSimilarityMaximum, 
		@JsonProperty("matched_token_number") int resultMatchedTokenNumber) {
}
```

- To be able to include the new value in the tests, they must be added to the `EndToEndSuiteTest` as a comparison operation at the package `de.jplag.endtoend`. The `runJPlagTestSuite()` function provided for this purpose must be extended to include the new comparison value. To do this, create the comparison as shown in the code example below.

```JAVA
//...
            if (Float.compare(result.resultSimilarityMaximum(), jPlagComparison.maximalSimilarity()) != 0) {
                addToValidationErrors("maximalSimilarity", String.valueOf(result.resultSimilarityMaximum()),
                        String.valueOf(jPlagComparison.maximalSimilarity()));
            }
//...
```

- Once the tests run the first time they will fail due to the missing values in the old JSON result file used for the test cases. The old results must then be replaced with new ones. 
For this purpose, the last section of the chapter [Copying Plagiarism To The Resources](#copying-plagiarism-to-the-resources) can be used as help. 

###  Extending JPlar Test Run Options
The endToEnd tests support the possible scan options of the JPlag API. Currently `minimumTokenMatch` is used in the end-to-end tests. These values are also stored in the JSON as configuration to keep the test cases at the options apart. Likewise, also changes in the logic of the different options are to be determined to be able.

- To extend new options to the end-to-end tests they have to be added to the record object `Options` in the package `de.jplag.endtoend.model`.  Here it is sufficient to add the values in the record and to enter the JSON name as `@JsonProperty("json_name")`.

```JAVA
public record Options(
@JsonProperty("minimum_token_match") Integer minimumTokenMatch) {
}
```

- After the new value has been added to the record, the creation of the object must now also be adjusted in the `EndToEndSuiteTest`.  The 'setRunOptions' function is provided for this purpose. The options can be added in any order and combination. It should be noted that each test case is run with these options. 

```JAVA
    private void setRunOptions() {
        options = new ArrayList<>();
        options.add(new Options(1));
        options.add(new Options(15));
    }
```

- If you want to create individual test cases by testing the options only on a specific set of dates, a new test case must be created for this purpose. For the new test cases, the options in the transfer parameters can be adjusted and specified. This can then be tested with the function `runTests`. 
 ```JAVA 
 runTests(directoryName, option, currentLanguageIdentifier, testCase, currentResultDescription);
```

- Once the tests run the first time they will fail due to the missing values in the old JSON result file used for the test cases. The old results must then be replaced with new ones. 
For this purpose, the last section of the chapter [Copying Plagiarism To The Resources](#copying-plagiarism-to-the-resources) can be used as help. 