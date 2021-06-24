import java.io.IOException;
import java.io.File;
import java.io.BufferedWriter;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.regex.Pattern;

public class SomeClass {
    public Future<HashMap<String, Integer>> startAsync(
            String directoryName, String searchWord, String newFileName, String fileWithForbiddenWords){
        return CompletableFuture.supplyAsync(() -> {
            return this.findFiles(directoryName, searchWord);
        }).thenApply((files) -> {
            HashMap<String, Integer> details = null;
            try {
                this.mergeFiles(files, newFileName);
                details = cutForbiddenWords(newFileName, this.getForbiddenWords(fileWithForbiddenWords));
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            return details;
        });
    }

    private HashMap<String, Integer> cutForbiddenWords(String fileName, String[] forbiddenWords) throws IOException {
        var forbiddenWordsMap = new HashMap<String, Integer>();
        for (var el : forbiddenWords) forbiddenWordsMap.put(el, 0);

        BufferedReader bufferedReader = new BufferedReader(
                new FileReader(fileName));
        var newTextBuilder = new StringBuilder();

        String line;
        while ((line = bufferedReader.readLine()) != null) {
            for (var el : forbiddenWords) {
                String regex = "(?i)\\b" + el + "\\b";

                var pattern = Pattern.compile(regex);
                var matcher = pattern.matcher(line);

                while (matcher.find()) {
                    forbiddenWordsMap.replace(el, forbiddenWordsMap.get(el) + 1);
                    line = line.replaceAll(regex, this.printStarSymbols(el.length()));
                }
            }

            newTextBuilder.append(line).append("\n");
        }
        bufferedReader.close();

        this.writeFile(fileName, newTextBuilder.toString());
        return forbiddenWordsMap;
    }

    private void writeFile(String fileName, String text){
        BufferedWriter bufferedWriter = null;
        try {
            bufferedWriter = new BufferedWriter(
                    new FileWriter(fileName));

            bufferedWriter.write(text);
            bufferedWriter.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String[] getForbiddenWords(String fileName) throws IOException{
        BufferedReader bufferedReader = new BufferedReader(
                new FileReader(fileName));

        var forbiddenWords =  bufferedReader.lines().toArray(String[]::new);
        bufferedReader.close();

        return forbiddenWords;
    }

    private String printStarSymbols(int quantity){
        var stringBuilder = new StringBuilder();
        for (int i = 0; i < quantity; i++)
            stringBuilder.append('*');

        return stringBuilder.toString();
    }

    private void mergeFiles(ArrayList<String> fileList, String newFileName) throws IOException {
        BufferedWriter bufferedWriter =
                new BufferedWriter(new FileWriter(newFileName));

        for (var fileName : fileList) {
            BufferedReader bufferedReader =
                    new BufferedReader(new FileReader(fileName));

            var stringBuilder = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
            bufferedReader.close();
            bufferedWriter.write(stringBuilder.toString());
        }

        bufferedWriter.close();
    }

    private ArrayList<String> findFiles(String directoryName, String searchWord){
        var fileNamesList = new ArrayList<String>();
        for (var el : new File(directoryName).list()) {
            if (el.contains(searchWord)){
                fileNamesList.add(directoryName + el);
            }
        }

        return fileNamesList;
    }
}
