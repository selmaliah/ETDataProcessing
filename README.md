# ETDataProcessing
Cleans and organizes exported data from SMI ET

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