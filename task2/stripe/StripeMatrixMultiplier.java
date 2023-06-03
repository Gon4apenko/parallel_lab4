package task2.stripe;

import task2.stripe.models.Matrix;
import task2.stripe.models.Result;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

public class StripeMatrixMultiplier {
    private static final int THREAD_COUNT = 6;

    public Result multiply(Matrix matrix1, Matrix matrix2) {
        int rows = matrix1.getRows();
        int columns = matrix2.getColumns();

        if (matrix1.getColumns() != matrix2.getRows()) {
            throw new IllegalArgumentException("The number of columns in the first matrix must be equal to the number of rows in the second matrix.");
        }

        Result result = new Result(rows, columns);
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);

        int blockSize = rows / THREAD_COUNT;

        IntStream.range(0, THREAD_COUNT).forEach(threadIndex -> {
            int startRow = threadIndex * blockSize;
            int endRow = (threadIndex == THREAD_COUNT - 1) ? rows : (threadIndex + 1) * blockSize;

            executor.submit(new MultiplierThreadStripe(matrix1, matrix2, result, startRow, endRow));
        });

        try {
            executor.shutdown();
            executor.awaitTermination(100L, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return result;
    }
}
