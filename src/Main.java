import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Main {

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);

        List<List<String>> fileRows;
        String outputFolder;
        List<String> trialAttributes;
        List<List<Trial>> subjects;
        List<String> prefixList;
        try {
            // Get input file
            fileRows = parseInputDate(in);
            outputFolder = getOutputFolder(in);

            trialAttributes = Utils.getTrialAttributes(fileRows.get(0));
            subjects = new ArrayList<>();
            prefixList = new ArrayList<>();
            MultiValuedMap<Pair<Integer, String>, Trial> trialsStimulusMap = new ArrayListValuedHashMap<>();

            System.out.println("Here we start");
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
        } catch (Exception e) {
            System.out.println("Exit");
            return;
        }

        in.close();
        System.out.println("All Done!");
        System.out.println("Good Bye!");
    }

    private static void calculateAverageHandler(Scanner in, String outputFolder, List<String> trialAttributes, List<List<Trial>> subjects, List<String> prefixList) throws IOException {
        System.out.println("Do you want to calculate average? (Y/N)");
        String shouldCalcAverageStr = in.nextLine();
        boolean shouldCalcAverage = false;
        if (shouldCalcAverageStr.toLowerCase().equals("y")){
            shouldCalcAverage = true;
        }
        List<String> calcAverageAttributes;
        String calcAverageByAttribute;
        if (shouldCalcAverage){
            System.out.println("Please choose the attribute to base average on:");
            printOptions(trialAttributes);
            System.out.println("Enter the field number");
            try{
                calcAverageByAttribute = parseOptionFromString(in.nextLine(), trialAttributes);
            } catch (Exception e){
                System.out.println("Couldn't process the property number. Try again. Bye Bye.");
                throw e;
            }
            System.out.println("You choose " + calcAverageByAttribute);

            List<String> trialAttributesToCalcAverage = new ArrayList<>(trialAttributes);
            trialAttributesToCalcAverage.remove(calcAverageByAttribute);

            System.out.println("Please choose the attributes you want to calculate averages of:");
            printOptions(trialAttributesToCalcAverage);
            System.out.println("Enter a list of the field numbers you want to clean. For example: 2, 4-6");
            try{
                calcAverageAttributes = parseOptionsFromString(in.nextLine(), trialAttributesToCalcAverage);
            } catch (Exception e){
                System.out.println("Try again. Bye Bye.");
                throw e;
            }
            System.out.println("You choose " + calcAverageAttributes.toString());

            Parser.writeAverage(subjects, calcAverageByAttribute, calcAverageAttributes, outputFolder + "/average.csv", prefixList);
        }
    }

    private static void cleanDataHandler(Scanner in, String outputFolder, List<String> trialAttributes, List<List<Trial>> subjects, MultiValuedMap<Pair<Integer, String>, Trial> trialsStimulusMap) throws IOException {
        System.out.println("Do you want to clean? (Y/N)");
        String shouldCleanStr = in.nextLine();
        boolean shouldClean = false;
        if (shouldCleanStr.toLowerCase().equals("y")){
            shouldClean = true;
        }
        if (shouldClean){
            cleanData(in, outputFolder, trialAttributes, subjects, trialsStimulusMap);
        }
    }

    private static String getOutputFolder(Scanner in) throws Exception {
        System.out.println("Enter output name:");
        String outputFolder =  "output/" + in.nextLine();
        File theDir = new File(outputFolder);
        if (!theDir.exists()){
            if (!theDir.mkdirs()) {
                System.out.println("Failed to create the folder " + outputFolder);
                throw new Exception();
            }
        }
        return outputFolder;
    }

    private static List<List<String>> parseInputDate(Scanner in) throws FileNotFoundException {
        String fileName = getInputFile(in);
        List<List<String>> fileRows;
        try {
            fileRows = Parser.Parse(fileName);
        } catch (Exception e) {
            System.out.println("Failed to load the file " + fileName);
            throw e;
        }
        return fileRows;
    }

    private static String getInputFile(Scanner in) {
        System.out.println("Enter file name:");
        String fileName = in.nextLine();
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
            String[] arrayRow = row.toArray(String[]::new);
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

                columnNumber += 9;
                index++;
            }
            lineNumber++;
            subjects.add(subjectTrials);
        }
    }

    private static void cleanData(Scanner in, String outputFolder, List<String> trialAttributes, List<List<Trial>> subjects, MultiValuedMap<Pair<Integer, String>, Trial> trialsStimulusMap) throws IOException {
        List<String> cleanAttributes;
        String cleanByAttribute;
        List<String> cleanByAreas;
        int cleanThreshold;
        System.out.println("Please choose the attribute to based the clean on:");
        printOptions(trialAttributes);
        System.out.println("Enter the field number");
        try{
            cleanByAttribute = parseOptionFromString(in.nextLine(), trialAttributes);
        } catch (Exception e){
            System.out.println("Couldn't process the property number. Try again. Bye Bye.");
            throw e;
        }
        System.out.println("You choose " + cleanByAttribute);

        List<String> trialAttributesToClean = new ArrayList<>(trialAttributes);
        trialAttributesToClean.remove(cleanByAttribute);

        System.out.println("Please choose the attributes you want to clean:");
        printOptions(trialAttributesToClean);
        System.out.println("Enter a list of the field numbers you want to clean. For example: 2, 4-6");
        try{
            cleanAttributes = parseOptionsFromString(in.nextLine(), trialAttributesToClean);
        } catch (Exception e){
            System.out.println("Try again. Bye Bye.");
            throw e;
        }
        System.out.println("You choose " + cleanAttributes.toString());

        System.out.println("Enter cleaning threshold (if no value entered, use 1500):");
        String cleanThresholdStr = in.nextLine();
        if (!cleanThresholdStr.isEmpty()) {
            try {
                cleanThreshold = Integer.parseInt(cleanThresholdStr);
            } catch (Exception e) {
                System.out.println("Failed to parse threshold. Threshold is not a number: " + cleanThresholdStr);
                throw e;
            }
        } else {
            cleanThreshold = 1500;
        }
        System.out.println("You choose " + cleanThreshold + " as your threshold");

        List<String> areaTypes = Utils.getAreaTypesFromSubjects(subjects);
        System.out.println("Based on what areas should I clean?");
        printOptions(areaTypes);
        System.out.println("Enter a list of the area numbers you want to clean based on. For example: 2, 4-6");
        try{
            cleanByAreas = parseOptionsFromString(in.nextLine(), areaTypes);
        } catch (Exception e){
            System.out.println("Try again. Bye Bye.");
            throw e;
        }
        System.out.println("You choose " + cleanByAreas.toString());

        String finalCleanByAttribute = cleanByAttribute;
        List<String> finalCleanByAreas = cleanByAreas;
        int finalCleanThreshold = cleanThreshold;
        List<List<Trial>> invalidTrials =
                subjects
                        .stream()
                        .map(trials -> trials.stream().filter(t-> t.isInvalidTrial(finalCleanByAttribute, finalCleanByAreas, finalCleanThreshold)).collect(Collectors.toList()))
                        .filter(item -> !item.isEmpty())
                        .collect(Collectors.toList());
        System.out.println("Total invalid trials: " + invalidTrials.stream().mapToInt(List::size).sum());

        Parser.writeInvalid(invalidTrials, outputFolder + "/invalid.csv", cleanByAttribute, cleanAttributes, cleanByAreas, trialAttributes);
        //        printTrials(invalidTrials);
        List<String> finalCleanAttributes = cleanAttributes;
        invalidTrials
                .forEach(trials ->
                    trials.forEach(invalidTrial ->
                        trialsStimulusMap
                                .get(new Pair<>(invalidTrial.subjectIndex, invalidTrial.getStimulus()))
                                .forEach(t -> t.deleteValues(finalCleanAttributes))));
        Parser.writeTotalNumberPerStimuli(subjects, cleanByAreas, outputFolder + "/totalNumberOfStimuli.csv");
    }

    private static String parseOptionFromString(String chosenOptionsStr, List<String> options) {
        int cleanByAttributeIndex = Integer.parseInt(chosenOptionsStr.replaceAll("\\s", ""));

        return options.get(cleanByAttributeIndex - 1);
    }

    private static List<String> parseOptionsFromString(String chosenOptionsStr, List<String> options){
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
            System.out.println("Unsupported list: " + chosenOptionsStr);
            throw e;
        }

        return chosenOptionsNumbers.stream().map(i -> options.get(i - 1)).collect(Collectors.toList());
    }

    private static void printOptions(List<String> options) {
        for (int i = 1; i <= options.size(); i++) {
            System.out.println("[" + i + "] " + options.get(i - 1));
        }
    }
}
