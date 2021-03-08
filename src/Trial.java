import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Trial {
    int index;
    int subjectIndex;
    private final int columnIndex;
    private final String subject;
    Map<String, String> attributes;
    boolean isEmpty;

    public Trial(int index, int subjectIndex, int columnIndex, String subject, String[] arrayRow, int columnNo, List<String> trialAttributes) {
        this.index = index;
        this.subjectIndex = subjectIndex;
        this.columnIndex = columnIndex;
        this.subject = subject;
        attributes = new HashMap<>();
        isEmpty = false;
        for (int i = 0; i < trialAttributes.size(); i++) {
            attributes.put(trialAttributes.get(i), arrayRow[columnNo + i].toLowerCase());
        }
    }

    public String getTrial() {
        return getAttribute("trial");
    }

    public String getStimulus() {
        return getAttribute("stimulus");
    }

    public String getAreaOfInterest() {
        return getAttribute("area_of_interest");
    }


    public boolean underThreshold(int threshold, String cleanByAttribute) {
        if (attributes.get(cleanByAttribute).equals("")) return true;
        return Double.parseDouble(attributes.get(cleanByAttribute)) <= threshold;
    }

    public void deleteValues(List<String> cleanAttributes) {
        for(String att: cleanAttributes){
            attributes.replace(att, "");
        }
        isEmpty = true;
    }

    public boolean isInvalidTrial(String cleanByAttribute, List<String> cleanByArea, int threshold) {
        return (cleanByArea.stream().anyMatch(area -> this.getAreaOfInterest().equalsIgnoreCase(area))) && underThreshold(threshold, cleanByAttribute);
    }

    public String toString(List<String> trialAttributes) {
        return trialAttributes.stream().map(this::getAttribute).collect(Collectors.joining(","));
    }

    public String invalidToString(List<String> cleanAttributes, String cleanByAttribute) {
        List<String> keysToString = attributes.keySet().stream().filter(att -> !cleanAttributes.contains(att) || cleanByAttribute.toLowerCase().equals(att)).collect(Collectors.toList());
        return keysToString.stream().sorted().map(k -> attributes.get(k)).collect(Collectors.joining(","));
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Trial)
            return getStimulus().equals(((Trial) obj).getStimulus());
        else return false;
    }

    public boolean isEmpty() {
        return isEmpty;
    }

    public boolean isNotEmpty() {
        return !isEmpty;
    }

    public String getSubject() {
        return subject;
    }

    public int getColumnIndex() {
        return columnIndex;
    }

    public String getAttribute(String att) {
        return attributes.get(att);
    }

    public int getSubjectIndex() {
        return subjectIndex;
    }

    public int getIndex() {
        return index;
    }
}

