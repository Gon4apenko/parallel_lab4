package task3;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class Main {

    private static class WordSearchTask extends RecursiveTask<Set<String>> {

        private String[] documents;
        private int start;
        private int end;

        public WordSearchTask(String[] documents, int start, int end) {
            this.documents = documents;
            this.start = start;
            this.end = end;
        }

        @Override
        protected Set<String> compute() {
            if (end - start <= 1) {
                return findUniqueWords(documents[start]);
            } else {
                int mid = (start + end) / 2;

                WordSearchTask leftTask = new WordSearchTask(documents, start, mid);
                WordSearchTask rightTask = new WordSearchTask(documents, mid, end);

                leftTask.fork();
                Set<String> rightResult = rightTask.compute();
                Set<String> leftResult = leftTask.join();

                leftResult.retainAll(rightResult);

                return leftResult;
            }
        }

        private Set<String> findUniqueWords(String document) {
            Set<String> uniqueWords = new HashSet<>();
            String[] words = document.split("\\s+");
            for (String word : words) {
                if (word.matches("[a-zA-Z]+")) {
                    uniqueWords.add(word.toLowerCase());
                }
            }
            return uniqueWords;
        }
    }

    public static void main(String[] args) {
        String[] documents = readDocumentsFromFiles("data/task3/input1.txt", "data/task3/input2.txt");
        ForkJoinPool forkJoinPool = new ForkJoinPool();
        WordSearchTask task = new WordSearchTask(documents, 0, documents.length);
        Set<String> commonWords = forkJoinPool.invoke(task);

        if (commonWords.isEmpty()) {
            System.out.println("No common words found between the documents.");
        } else {
            System.out.println("Common words: " + commonWords);
        }
    }

    private static String[] readDocumentsFromFiles(String filePath1, String filePath2) {
        String[] documents = new String[2];

        StringBuilder sb1 = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath1))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb1.append(line).append(" ");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        documents[0] = sb1.toString();

        StringBuilder sb2 = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath2))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb2.append(line).append(" ");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        documents[1] = sb2.toString();

        return documents;
    }
}
