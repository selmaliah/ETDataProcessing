import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Utils {
    public static List<String> getAreaTypesFromSubjects(List<List<Trial>> trials) {
        return getAreaTypesFromTrails(trials.get(0));
    }

    public static List<String> getAreaTypesFromTrails(List<Trial> trials) {
        return trials.stream().map(Trial::getAreaOfInterest).distinct().collect(Collectors.toList());
    }

    public static List<String> getAttributeTypes(List<List<Trial>> trials, String attribute) {
        return trials.get(0).stream().map(trial -> trial.getAttribute(attribute)).distinct().collect(Collectors.toList());
    }

    public static List<String> getTrialAttributes(List<String> titles) {
        String TRAIL_COLUMN = "trial";
        int i = 0;
        List<String> trialAttributes = new ArrayList<>();
        while (!titles.get(i).toLowerCase().contains(TRAIL_COLUMN)) {
            i++;
        }
        trialAttributes.add(titles.get(i).toLowerCase());
        i++;
        while (!titles.get(i).toLowerCase().contains(TRAIL_COLUMN)) {
            trialAttributes.add(titles.get(i).toLowerCase());
            i++;
        }
        return trialAttributes;
    }

    public static void printTrials(List<List<Trial>> trialsList) {
        for (List<Trial> trials : trialsList) {
            for (Trial trial : trials) {
                System.out.print(trial.subjectIndex + "," + trial.index + ":");
                System.out.println(trial);
            }
        }
    }
}
