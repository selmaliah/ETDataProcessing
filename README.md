# ETDataProcessing
Cleans and organizes exported data from SMI ET

## Supported Data
### Input
* The program gets `csv` files only.
* First row must be the column names. Second row and below, the values.
* First column must be the subject ID.
* In the data exported from iView, make sure to export trial numbers, such that ach trial in the experiment must have "Trial" columns before the data associated with this trial (e.g., Trial1, NetDwellTrial1, etc.).
* Column names can contain spaces, but those spaces will be removed in the output. For example, "Trial 5" will become "Trial5".
* For each trial, the same variables should be exported, in the same order (e.g., Trial1 - NetDwell, FixationCount, Firstfixduration, Trial 2 - NetDwell, FixationCount, Firstfixduration etc). for all trials and for all AOIs).
* When grouped by a variable, if the variable name have typos, the program will not group then and will refer to them as separated variables. For example, when grouped by 
  `area_of_interest`, and one of the values is `happy_face`, then `hapy_face` will not be counted as the same group and will appear as an independent variable.
* When calculating averages, all values must be numerical! Do not use other characters like letters, '-', '/', ect.
* `CSV` Data files are considered as `input` and should live in the `input` folder.

### Output
* The program generates multiple output files.
* The program will ask you for a desired folder. All the output files goes into the folder `output` and a sub-folder as
  you entered. For example, if you entered `ProjectX` as your output folder, then the output files will be in
  `output/ProjectX` folder.
* One of the output files that will be generated is `script.txt`. This file will include the content from your command line interactions with the software.

## Installation
1. Download and install [Java Runtime Environment (JRE)](https://www.java.com/en/download/). Download the file, double click and
follow the instructions.
1. Navigate to [ETDataProcessing](https://github.com/selmaliah/ETDataProcessing) GitHub project.
1. Click "Code" and then "Download ZIP"

    ![](./readme/download%20zip.png)

1. Extract the zip file.

## How To Use
### Windows
1. Open `ETDataProcessing-main/ready_to_run/DataHelper` folder.
1. Add your `*.csv` input file to the `input` folder.
1. Double-click the `run.bat` file to run the program.
1. Follow the instructor on the screen.
1. Find the output files in the folder created in `DataHelper/`.

### Mac
1. Open `ETDataProcessing-main/ready_to_run/DataHelper` folder.
1. Add your `*.csv` input file to the `input` folder.
1. Go back to `ready_to_run` folder.
1. Right click on `DataHelper`, and then "New Terminal at Folder".
   ![](./readme/open%20terminal.png)
1. In the terminal run `java -jar DataHelper.jar`.
1. Follow the instructor on the screen.
1. Find the output files in the folder created in `DataHelper/`.

## Important Notes:
* Make sure you use numbers in numerical fields (not "-" or " ")
* Make sure fields doesn't have typos, for example: "happy_mouth" and "hapy_mouth".
 In this case they won't be calculated together as the same variable average.
* Errors:
  * Some error messages are informative, and you can use them to fix your input file.
  * If there is an error you can't solve, please contact `jen_lab@gmail.com`.
    Try to be as detailed as you can- attached the error message, and the input file.
