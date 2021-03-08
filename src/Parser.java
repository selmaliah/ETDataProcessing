import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Parser {

    public static List<List<String>> Parse(String fileName) throws FileNotFoundException {
        File file= new File(fileName);

        // this gives you a 2-dimensional array of strings
        List<List<String>> lines = new ArrayList<>();
        Scanner inputStream;

        inputStream = new Scanner(file);

        while(inputStream.hasNext()){
            String line= inputStream.next();
            String[] values = line.split(",");
            // this adds the currently parsed line to the 2-dimensional string array
            lines.add(Arrays.asList(values));
        }

        inputStream.close();
        return lines;
    }

    public static void writeToCSV(List<List<Trial>> subjects, List<String> trialAttributes, List<String> headers, String fileName, List<String> firstColumn) throws IOException {
        FileWriter writer = new FileWriter(fileName);
        writer.write(headers.stream().collect(Collectors.joining(",")));
        subjects.forEach(list -> {
            try {
                writer.write(System.lineSeparator());
                writer.write(firstColumn.get(subjects.indexOf(list))+",");
                writer.write(list.stream().map(trial -> trial.toString(trialAttributes)).collect(Collectors.joining(",")));
                writer.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        writer.close();
    }

    public static void writeInvalid(List<List<Trial>> invalids, String fileName, String cleanByAttribute, List<String> cleanAttributes, List<String> cleanByAreas, List<String> trialAttributes) throws IOException {
        OptionalInt longest = invalids
                .stream()
                .mapToInt(List::size).boxed()
                .sorted(Comparator.reverseOrder())
                .mapToInt(i -> i)
                .findFirst();
        FileWriter writer = new FileWriter(fileName);
        writer.write("subject,total_failures");
        writer.write(",");
        writer.write(cleanByAreas.stream().sorted().map(area -> "total_" + area).collect(Collectors.joining(",")));
        writer.write(",");
        List<String> attributesToPrint = trialAttributes
                .stream()
                .filter(att -> !cleanAttributes.contains(att) || cleanByAttribute.equals(att))
                .sorted()
                .collect(Collectors.toList());
        writer.write(
                IntStream
                        .range(0, longest.getAsInt())
                        .boxed()
                        .map(i -> attributesToPrint
                                .stream()
                                .map(str -> str + "_" + i)
                                .collect(Collectors.joining(",")))
                        .collect(Collectors.joining(",")));

        writer.flush();
        invalids.forEach(list -> {
            try {
                List<String> trialsAreaOfInterest = list.stream().map(Trial::getAreaOfInterest).collect(Collectors.toList());
                writer.write(System.lineSeparator());
                writer.write(list.get(0).getSubject() + "," + list.size());
                writer.write(",");
                writer.write(
                        cleanByAreas
                                .stream()
                                .sorted()
                                .map(area -> Collections.frequency(trialsAreaOfInterest, area))
                                .map(Objects::toString)
                                .collect(Collectors.joining(","))
                );
                writer.write(",");
                writer.write(
                        list
                                .stream()
                                .map(t -> t.invalidToString(cleanAttributes, cleanByAttribute))
                                .collect(Collectors.joining(",")));
                writer.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        writer.close();
    }

    public static void writeTypeSummary(List<Trial> trials, List<String> headers, String fileName) throws IOException {
        FileWriter writer = new FileWriter(fileName);
        for (String type: Utils.getAreaTypesFromTrails(trials)) {
            writer.write(type + ",");
            writer.write(trials
                    .stream()
                    .filter(trial -> trial.getAreaOfInterest().equalsIgnoreCase(type))
                    .map(Trial::getIndex)
                    .map(Objects::toString)
                    .collect(Collectors.joining(",")));
            writer.write(System.lineSeparator());
        }
        writer.close();
    }

    public static void writeAverage(List<List<Trial>> subjects, String calcAverageByAttribute, List<String> calcAverageAttributes, String fileName, List<String> firstColumn) throws IOException {
        FileWriter writer = new FileWriter(fileName);
        List<String> types = Utils.getAttributeTypes(subjects, calcAverageByAttribute);
        String titles = calcAverageByAttribute + "," + String.join(",", calcAverageAttributes);
        String headers = "Subject";
        for (int i = 0; i < types.size(); i++) {
            headers = headers + "," + titles;
        }
        writer.write(headers);

        subjects.forEach(list -> {
            try {
            writer.write(System.lineSeparator());
            writer.write(firstColumn.get(subjects.indexOf(list)));
            List<Trial> trialsByType;
            for (String type : types) {
                trialsByType = list
                        .stream()
                        .filter(trial -> type.equalsIgnoreCase(trial.getAttribute(calcAverageByAttribute)))
                        .collect(Collectors.toList());
                writer.write(",");
                writer.write(trialsByType.get(0).getAttribute(calcAverageByAttribute));
                writer.write(",");
                List<Trial> finalTrialsByType = trialsByType;
                writer.write(
                        calcAverageAttributes
                                .stream()
                                .map(att ->
                                        finalTrialsByType
                                                .stream()
                                                .filter(Trial::isNotEmpty)
                                                .map(trial -> trial.getAttribute(att))
//                                                .filter(str -> !str.equals(""))
                                                .mapToDouble(Double::valueOf)
                                                .average()
                                                .orElse(-1))
                                .map(Object::toString)
                                .collect(Collectors.joining(",")));
                writer.flush();
            }
            } catch (IOException e) {
                e.printStackTrace();
            }

        });
        writer.close();

    }

    public static void writeTotalNumberPerStimuli(List<List<Trial>> subjects, List<String> areas, String fileName) throws IOException {
        FileWriter writer = new FileWriter(fileName);

        String headers = "subject," + String.join(",", areas);
        writer.write(headers);
        subjects.forEach((list)-> {
            try {
                writer.write(System.lineSeparator());
                writer.write(list.get(1).getSubject());
                writer.write(",");
                writer.write(areas.stream().map(area -> list.stream().filter(trial -> !trial.isEmpty() && trial.getAreaOfInterest().equalsIgnoreCase(area)).count()).map(Object::toString).collect(Collectors.joining(",")));
                writer.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        writer.close();
    }
}
