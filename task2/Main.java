package task2;

import task2.fork.BlockMatrixMultiplier;
import task2.stripe.models.Result;
import task2.stripe.StripeMatrixMultiplier;
import task2.stripe.models.Matrix;

public class Main {
    public static void main(String[] args) {

        int[] sizes = {100,500,2000};

        for (int size : sizes) {

            Matrix matrix = generateRandomMatrix(size, size);

            StripeMatrixMultiplier stripeMultiplier = new StripeMatrixMultiplier();
            long startTimeStripe = System.currentTimeMillis();
            task2.stripe.models.Result resultStripe = stripeMultiplier.multiply(matrix, matrix);
            long endTimeStripe = System.currentTimeMillis();
            long executionTimeStripe = endTimeStripe - startTimeStripe;

            BlockMatrixMultiplier forkMultiplier = new BlockMatrixMultiplier();
            long startTimeFork = System.currentTimeMillis();
            Result resultFork = forkMultiplier.multiply(matrix, matrix);
            long endTimeFork = System.currentTimeMillis();
            long executionTimeFork = endTimeFork - startTimeFork;

            System.out.println("Matrix size: " + size);
            System.out.println("Execution time (Stripe): " + executionTimeStripe + " ms");
            System.out.println("Execution time (Fork/Join): " + executionTimeFork + " ms");
            System.out.println("--------------------------------------------");
        }
    }

    private static Matrix generateRandomMatrix(int rows, int columns) {
        Matrix matrix = new Matrix(rows, columns);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                matrix.setValueAt(i, j, (int) (Math.random() * 10));
            }
        }
        return matrix;
    }
}
