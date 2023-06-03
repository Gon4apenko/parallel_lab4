package task2.fork;

import task2.stripe.models.Matrix;
import task2.stripe.models.Result;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

public class BlockMatrixMultiplier {
    private static final int THRESHOLD = 16;

    public Result multiply(Matrix matrix1, Matrix matrix2) {
        if (matrix1.getColumns() != matrix2.getRows()) {
            throw new IllegalArgumentException("The number of columns in the first matrix must be equal to the number of rows in the second matrix.");
        }

        int rows = matrix1.getRows();
        int columns = matrix2.getColumns();
        int commonDimension = matrix1.getColumns();

        Result result = new Result(rows, columns);

        ForkJoinPool forkJoinPool = new ForkJoinPool();
        MultiplyTask task = new MultiplyTask(matrix1, matrix2, result, 0, rows, 0, columns, 0, commonDimension);
        forkJoinPool.invoke(task);

        return result;
    }

    private static class MultiplyTask extends RecursiveAction {
        private Matrix matrix1;
        private Matrix matrix2;
        private Result result;
        private int startRow;
        private int endRow;
        private int startColumn;
        private int endColumn;
        private int startCommon;
        private int endCommon;
        public MultiplyTask(Matrix matrix1, Matrix matrix2, Result result,
                            int startRow, int endRow, int startColumn, int endColumn,
                            int startCommon, int endCommon) {
            this.matrix1 = matrix1;
            this.matrix2 = matrix2;
            this.result = result;
            this.startRow = startRow;
            this.endRow = endRow;
            this.startColumn = startColumn;
            this.endColumn = endColumn;
            this.startCommon = startCommon;
            this.endCommon = endCommon;
        }

        @Override
        protected void compute() {
            if ((endRow - startRow) <= THRESHOLD && (endColumn - startColumn) <= THRESHOLD && (endCommon - startCommon) <= THRESHOLD) {
                // Perform matrix multiplication for the given block
                for (int i = startRow; i < endRow; i++) {
                    for (int j = startColumn; j < endColumn; j++) {
                        int value = 0;
                        for (int k = startCommon; k < endCommon; k++) {
                            value += matrix1.getValueAt(i, k) * matrix2.getValueAt(k, j);
                        }
                        result.setValueAt(i, j, value);
                    }
                }
            } else {
                // Split the block into smaller blocks and fork the tasks
                int midRow = (startRow + endRow) / 2;
                int midColumn = (startColumn + endColumn) / 2;
                int midCommon = (startCommon + endCommon) / 2;

                MultiplyTask topLeft = new MultiplyTask(matrix1, matrix2, result,
                        startRow, midRow, startColumn, midColumn, startCommon, midCommon);
                MultiplyTask topRight = new MultiplyTask(matrix1, matrix2, result,
                        startRow, midRow, midColumn, endColumn, midCommon, endCommon);
                MultiplyTask bottomLeft = new MultiplyTask(matrix1, matrix2, result,
                        midRow, endRow, startColumn, midColumn, startCommon, midCommon);
                MultiplyTask bottomRight = new MultiplyTask(matrix1, matrix2, result,
                        midRow, endRow, midColumn, endColumn, midCommon, endCommon);

                invokeAll(topLeft, topRight, bottomLeft, bottomRight);
            }
        }
    }
}
