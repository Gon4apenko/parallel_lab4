package task1;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class Main {

    private static final int THRESHOLD = 100;

    private static class TextAnalysisTask extends RecursiveTask<Map<Integer, Integer>> {

        private String[] words;
        private int start;
        private int end;

        public TextAnalysisTask(String[] words, int start, int end) {
            this.words = words;
            this.start = start;
            this.end = end;
        }

        @Override
        protected Map<Integer, Integer> compute() {
            if (end - start <= THRESHOLD) {

                Map<Integer, Integer> stats = new HashMap<>();
                for (int i = start; i < end; i++) {
                    int length = words[i].length();
                    stats.merge(length, 1, Integer::sum);
                }

                return stats;

            } else {

                int mid = (start + end) / 2;
                TextAnalysisTask leftTask = new TextAnalysisTask(words, start, mid);
                TextAnalysisTask rightTask = new TextAnalysisTask(words, mid, end);

                leftTask.fork();
                Map<Integer, Integer> rightResult = rightTask.compute();
                Map<Integer, Integer> leftResult = leftTask.join();


                for (Map.Entry<Integer, Integer> entry : rightResult.entrySet()) {
                    leftResult.merge(entry.getKey(), entry.getValue(), Integer::sum);
                }

                return leftResult;
            }
        }
    }

    public static void main(String[] args) {
        String filePath = "data/task1/input.txt";
        String[] words = readTextFromFile(filePath);

        ForkJoinPool forkJoinPool = new ForkJoinPool();
        TextAnalysisTask task = new TextAnalysisTask(words, 0, words.length);

        long startTime = System.currentTimeMillis();
        Map<Integer, Integer> parallelStats = forkJoinPool.invoke(task);
        long executionTimeMultiThreaded = System.currentTimeMillis() - startTime;

        System.out.println("Statistics: " + parallelStats);
        System.out.println("Execution time (multi-threaded): " + executionTimeMultiThreaded + " ms");

        Map<Integer, Integer> stats = analyzeTextSequentially(words);
        long executionTimeSingleThreaded = System.currentTimeMillis() - startTime;

        System.out.println("Statistics (one thread): " + stats);
        System.out.println("Execution time (single-threaded): " + executionTimeSingleThreaded + " ms");


    }

    private static String[] readTextFromFile(String filePath) {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append(" ");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString().split(" ");
    }

    private static Map<Integer, Integer> analyzeTextSequentially(String[] words) {
        Map<Integer, Integer> stats = new HashMap<>();
        for (String word : words) {
            int length = word.length();
            stats.merge(length, 1, Integer::sum);
        }
        return stats;
    }
}
