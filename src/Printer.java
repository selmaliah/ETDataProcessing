import org.apache.commons.collections4.queue.CircularFifoQueue;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Queue;

public class Printer {
    FileWriter scriptWriter;
    Queue<String> queue = new CircularFifoQueue<>();

    Printer(){
        scriptWriter = null;
    }

    public void setScriptName(String scriptName) throws IOException {
        scriptWriter = new FileWriter(scriptName+".txt");
        if (queue.size() > 0) scriptFromQueue();
    }

    private void scriptFromQueue() {
        while (!queue.isEmpty()) script(queue.poll());
    }

    public void close() {
        try{
            if (scriptWriter != null) scriptWriter.close();
        } catch (Exception ignored) {}
    }

    public void println() {
        try{
            if (scriptWriter != null) scriptWriter.write(System.lineSeparator());
            else queue.add(System.lineSeparator());
        } catch (Exception ignored) {}
        finally {
            System.out.println();
        }

    }
    public void println(String str) {
        if (str == null) return;

        try{
            if (scriptWriter != null) {
                scriptWriter.write(str);
                scriptWriter.write(System.lineSeparator());
            } else {
                queue.add(str);
                queue.add(System.lineSeparator());
            }
        } catch (Exception ignored) {}
        finally {
            System.out.println(str);
        }
    }

    public String responseln(String str) {
        if (str == null) return null;

        try{
            if (scriptWriter != null) {
                scriptWriter.write("ANSWER: ");
                scriptWriter.write(str);
                scriptWriter.write(System.lineSeparator() + System.lineSeparator());
            } else {
                queue.add("ANSWER: ");
                queue.add(str);
                queue.add(System.lineSeparator() + System.lineSeparator());
            }
        } catch (Exception ignored) {}
        return str;
    }

    public void script(String str) {
        if (str == null) return;

        try{
            if (scriptWriter != null) {
                scriptWriter.write(str);
            }
        } catch (Exception ignored) {}
    }
}
