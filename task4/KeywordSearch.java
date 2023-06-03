package task4;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;


public class KeywordSearch {

    private static class FolderSearchTask extends RecursiveAction {
        private String folderPath;
        private List<String> keywords;
        private Set<String> matchedFiles;

        public FolderSearchTask(String folderPath, List<String> keywords, Set<String> matchedFiles) {
            this.folderPath = folderPath;
            this.keywords = keywords;
            this.matchedFiles = matchedFiles;
        }

        @Override
        protected void compute() {
            List<RecursiveAction> tasks = new ArrayList<>();

            File folder = new File(folderPath);
            File[] files = folder.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        FolderSearchTask task = new FolderSearchTask(file.getAbsolutePath(), keywords, matchedFiles);
                        tasks.add(task);
                    } else {
                        FileSearchTask task = new FileSearchTask(file.getAbsolutePath(), keywords, matchedFiles);
                        tasks.add(task);
                    }
                }
            }

            invokeAll(tasks);
        }
    }

    private static class FileSearchTask extends RecursiveAction {
        private String filePath;
        private List<String> keywords;
        private Set<String> matchedFiles;

        public FileSearchTask(String filePath, List<String> keywords, Set<String> matchedFiles) {
            this.filePath = filePath;
            this.keywords = keywords;
            this.matchedFiles = matchedFiles;
        }

        @Override
        protected void compute() {
            try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    for (String keyword : keywords) {
                        if (line.contains(keyword)) {
                            matchedFiles.add(filePath);
                            break;
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        String folderPath = "data";
        String[] keywords = {"IoT", "Wireless", "code"};
        Set<String> matchedFiles = new HashSet<>();
        ForkJoinPool forkJoinPool = new ForkJoinPool();
        FolderSearchTask task = new FolderSearchTask(folderPath, List.of(keywords), matchedFiles);
        forkJoinPool.invoke(task);

        for (String keyword : keywords) {
            System.out.println("Files containing keyword '" + keyword + "':");
            for (String file : matchedFiles) {
                System.out.println(file);
            }
            System.out.println();
        }
    }
}
