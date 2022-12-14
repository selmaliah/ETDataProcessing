import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.lang.Thread.sleep;

public class Main {
    public static Printer printer = new Printer();

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);

        List<List<String>> fileRows;
        String outputFolder;
        List<String> trialAttributes;
        List<List<Trial>> subjects;
        List<String> prefixList;
        try {
            // Get input file
            fileRows = parseInputData(in);
            // Get output directory
            outputFolder = getOutputFolder(in);
            printer.setScriptName(outputFolder + "/script");
            sleep(1000);
            System.out.println();

            trialAttributes = Utils.getTrialAttributes(fileRows.get(0));
            subjects = new ArrayList<>();
            prefixList = new ArrayList<>();
            MultiValuedMap<Pair<Integer, String>, Trial> trialsStimulusMap = new ArrayListValuedHashMap<>();

            printer.println("Let's start");
            sleep(1000);
            printer.println();

            parseTrials(fileRows, trialAttributes, subjects, prefixList, trialsStimulusMap);
            cleanDataHandler(in, outputFolder, trialAttributes, subjects, trialsStimulusMap);

            // Print Data
            Parser.writeToCSV(
                    subjects,
                    trialAttributes,
                    fileRows.get(0),
                    outputFolder + "/cleanData.csv",
                    prefixList);

            calculateAverageHandler(in, outputFolder, trialAttributes, subjects, prefixList);
            Parser.writeTypeSummary(subjects.get(0), fileRows.get(0), outputFolder + "/typesSummary.csv");
            sleep(1000);
        } catch (Exception e) {
            printer.println("Exit");
            e.printStackTrace();
            printer.close();
            return;
        }

        in.close();
        printer.println("All Done!");
        printer.println("Good Bye!");
        printer.close();
    }

    private static void calculateAverageHandler(Scanner in, String outputFolder, List<String> trialAttributes, List<List<Trial>> subjects, List<String> prefixList) throws Exception {
        printer.println("Do you want to calculate averages? (Y/N)");
        String shouldCalcAverageStr = printer.responseln(in.nextLine());
        boolean shouldCalcAverage = shouldCalcAverageStr.equalsIgnoreCase("y");
        List<String> calcAverageAttributes;
        String calcAverageByAttribute;
        if (shouldCalcAverage){
            printer.println("Please select the variable by which averages will be grouped and calculated (leave blank to use a default of 'area_of_interest')");
            printOptions(trialAttributes);
            printer.println("Enter the field number");
            try{
                calcAverageByAttribute = parseOptionFromString(printer.responseln(in.nextLine()), trialAttributes, true);
            } catch (Exception e){
                printer.println("Couldn't process the property number. Try again.");
                throw e;
            }
            calcAverageByAttribute = calcAverageByAttribute.isEmpty() ? "area_of_interest" : calcAverageByAttribute;
            printer.println("You selected " + calcAverageByAttribute);
            sleep(2000);
            printer.println();

            List<String> trialAttributesToCalcAverage = new ArrayList<>(trialAttributes);
            trialAttributesToCalcAverage.remove(calcAverageByAttribute);

            printer.println("Please select the metrics to calculate their averages");
            printOptions(trialAttributesToCalcAverage);
            printer.println("Enter a list of the metric numbers you want to average. For example: 2, 4-6");
            try{
                calcAverageAttributes = parseOptionsFromString(printer.responseln(in.nextLine()), trialAttributesToCalcAverage, false);
            } catch (Exception e){
                printer.println("Try again.");
                throw e;
            }
            printer.println("You selected " + calcAverageAttributes.toString());
            sleep(2000);
            printer.println();

            List<String> attributeTypes = Utils.getAttributeTypes(subjects, calcAverageByAttribute);
            printer.println("Are there variables that should be skipped and their averages should not be calculated");
            printOptions(attributeTypes);
            printer.println("If yes - enter a list of the variable numbers to be skipped. For example: 2, 4-6. If not - press 'enter'");
            List<String> skippedAreas;
            try{
                skippedAreas = parseOptionsFromString(printer.responseln(in.nextLine()), attributeTypes, true);
            } catch (Exception e){
                printer.println("Try again.");
                throw e;
            }
            printer.println("You selected " + skippedAreas.toString());
            sleep(2000);
            printer.println();

            Parser.writeAverage(subjects, calcAverageByAttribute, calcAverageAttributes, outputFolder + "/average.csv", prefixList, skippedAreas);
        }
    }

    private static void cleanDataHandler(Scanner in, String outputFolder, List<String> trialAttributes, List<List<Trial>> subjects, MultiValuedMap<Pair<Integer, String>, Trial> trialsStimulusMap) throws Exception {
        printer.println("Do you want to clean the data? (Y/N)");
        String shouldCleanStr = printer.responseln(in.nextLine());
        boolean shouldClean = shouldCleanStr.equalsIgnoreCase("y");
        if (shouldClean){
            cleanData(in, outputFolder, trialAttributes, subjects, trialsStimulusMap);
        }
    }

    private static String getOutputFolder(Scanner in) throws Exception {
        printer.println("Enter output folder name:");
        String outputFolder =  "output/" + printer.responseln(in.nextLine());
        File theDir = new File(outputFolder);
        if (!theDir.exists()){
            if (!theDir.mkdirs()) {
                printer.println("Failed to create the folder " + outputFolder);
                throw new Exception();
            }
        }
        return outputFolder;
    }

    private static List<List<String>> parseInputData(Scanner in) throws FileNotFoundException {
        String fileName = getInputFile(in);
        List<List<String>> fileRows;
        try {
            fileRows = Parser.Parse(fileName);
        } catch (Exception e) {
            printer.println("Failed to load the file " + fileName);
            throw e;
        }
        return fileRows;
    }

    private static String getInputFile(Scanner in) {
        printer.println("Enter file name:");
        String fileName = printer.responseln(in.nextLine());
        fileName = "input/" + (fileName.endsWith(".csv") ? fileName : fileName+ ".csv");
        return fileName;
    }

    private static void parseTrials(List<List<String>> fileRows, List<String> trialAttributes, List<List<Trial>> subjects, List<String> prefixList, MultiValuedMap<Pair<Integer, String>, Trial> trialsStimulusMap) {
        List<Trial> subjectTrials;
        int lineNumber = 0;
        int columnNumber;
        int index;
        Trial trial;
        for (List<String> row : fileRows) {
            if (lineNumber == 0) {
                lineNumber++;
                continue;
            }
            subjectTrials = new ArrayList<>();
            columnNumber = 1;
            index = 0;
            String[] arrayRow = row.toArray(new String[0]);
            prefixList.add(arrayRow[0]);
            while (columnNumber < row.size()) {
                trial = new Trial(
                        index,
                        lineNumber,
                        columnNumber,
                        arrayRow[0],
                        arrayRow,
                        columnNumber,
                        trialAttributes);
                subjectTrials.add(trial);

                trialsStimulusMap.put(new Pair<>(trial.subjectIndex, trial.getStimulus()), trial);

                columnNumber += trialAttributes.size();
                index++;
            }
            lineNumber++;
            subjects.add(subjectTrials);
        }
    }

    private static void cleanData(Scanner in, String outputFolder, List<String> trialAttributes, List<List<Trial>> subjects, MultiValuedMap<Pair<Integer, String>, Trial> trialsStimulusMap) throws Exception {
        List<String> cleanAttributes;
        String cleanByAttribute;
        List<String> cleanByAreas;
        int cleanThreshold;
        printer.println("Select the metric by which you'll clean the data (metric values must be numbers):");
        printOptions(trialAttributes);
        printer.println("Enter the metric number");
        try{
            cleanByAttribute = parseOptionFromString(printer.responseln(in.nextLine()), trialAttributes, false);
        } catch (Exception e){
            printer.println("Couldn't process the property number. Try again.");
            throw e;
        }
        printer.println("You selected " + cleanByAttribute);
        sleep(2000);
        printer.println();

        List<String> trialAttributesToClean = new ArrayList<>(trialAttributes);
        trialAttributesToClean.remove(cleanByAttribute);

        printer.println("Please select the metrics you want to clean:");
        printOptions(trialAttributesToClean);
        printer.println("Enter a list of the metric numbers you want to clean. For example: 2, 4-6");
        try{
            cleanAttributes = parseOptionsFromString(printer.responseln(in.nextLine()), trialAttributesToClean, false);
        } catch (Exception e){
            printer.println("Try again");
            throw e;
        }
        printer.println("You selected " + cleanAttributes.toString());
        sleep(2000);
        printer.println();

        printer.println("Enter cleaning threshold (leave blank to use a default of 1500ms):");
        String cleanThresholdStr = printer.responseln(in.nextLine());
        if (cleanThresholdStr != null && !cleanThresholdStr.isEmpty()) {
            try {
                cleanThreshold = Integer.parseInt(cleanThresholdStr);
            } catch (Exception e) {
                printer.println("Failed to parse threshold. Threshold is not a number: " + cleanThresholdStr);
                throw e;
            }
        } else {
            cleanThreshold = 1500;
        }
        printer.println("You selected " + cleanThreshold + " as your threshold");
        sleep(2000);
        printer.println();

        List<String> areaTypes = Utils.getAreaTypesFromSubjects(subjects);
        printer.println("select the areas of interest (AOIs) by which you want to clean the data");
        printOptions(areaTypes);
        printer.println("Enter a list of the AOIs numbers you want to clean based on. For example: 2, 4-6");
        try{
            cleanByAreas = parseOptionsFromString(printer.responseln(in.nextLine()), areaTypes, false);
        } catch (Exception e){
            printer.println("Try again");
            throw e;
        }
        printer.println("You selected " + cleanByAreas.toString());
        sleep(2000);
        printer.println();

        String finalCleanByAttribute = cleanByAttribute;
        List<String> finalCleanByAreas = cleanByAreas;
        int finalCleanThreshold = cleanThreshold;
        List<List<Trial>> invalidTrials =
                subjects
                        .stream()
                        .map(trials -> trials.stream().filter(t-> t.isInvalidTrial(finalCleanByAttribute, finalCleanByAreas, finalCleanThreshold)).collect(Collectors.toList()))
                        .filter(item -> !item.isEmpty())
                        .collect(Collectors.toList());
        printer.println("Total invalid trials: " + invalidTrials.stream().mapToInt(List::size).sum());

        cleanAttributes.add(cleanByAttribute);

        Parser.writeInvalid(invalidTrials, outputFolder + "/invalid.csv", cleanAttributes, cleanByAreas, trialAttributes);
        List<String> finalCleanAttributes = new ArrayList<>(cleanAttributes);
        invalidTrials
                .forEach(trials ->
                    trials.forEach(invalidTrial ->
                        trialsStimulusMap
                                .get(new Pair<>(invalidTrial.subjectIndex, invalidTrial.getStimulus()))
                                .forEach(t -> t.deleteValues(finalCleanAttributes))));
        Parser.writeTotalNumberPerStimuli(subjects, cleanByAreas, outputFolder + "/totalNumberOfStimuli.csv");
    }

    private static String parseOptionFromString(String chosenOptionsStr, List<String> options, boolean allowBlank) throws Exception {
        if (chosenOptionsStr == null || chosenOptionsStr.isEmpty()) {
            if (allowBlank) return "";
            else {
                printer.println("Must enter a value");
                throw new Exception("Must enter a value");
            }
        }
        int cleanByAttributeIndex = Integer.parseInt(chosenOptionsStr.replaceAll("\\s", ""));

        return options.get(cleanByAttributeIndex - 1);
    }

    private static List<String> parseOptionsFromString(String chosenOptionsStr, List<String> options, boolean allowBlank) throws Exception {
        if (chosenOptionsStr == null || chosenOptionsStr.isEmpty()) {
            if (allowBlank) return Collections.emptyList();
            else {
                printer.println("Must enter a value");
                throw new Exception("Must enter a value");
            }
        }
        String[] chosenOptionsArr = chosenOptionsStr
                .replaceAll("\\s", "")
                .split(",");
        List<Integer> chosenOptionsNumbers = new ArrayList<>();
        try {
            for (String cell : chosenOptionsArr) {
                if (!cell.contains("-")) chosenOptionsNumbers.add(Integer.parseInt(cell));
                else {
                    String[] range = cell.split("-");
                    IntStream.range(
                            Integer.parseInt(range[0]), Integer.parseInt(range[1]) + 1).forEach(chosenOptionsNumbers::add);
                }
            }
        } catch (Exception e) {
            printer.println("Unsupported list: " + chosenOptionsStr);
            throw e;
        }

        return chosenOptionsNumbers.stream().map(i -> options.get(i - 1)).collect(Collectors.toList());
    }

    private static void printOptions(List<String> options) {
        for (int i = 1; i <= options.size(); i++) {
            printer.println("[" + i + "] " + options.get(i - 1));
        }
    }
}
