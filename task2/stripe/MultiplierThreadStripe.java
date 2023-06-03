package task2.stripe;

import task2.stripe.models.Result;
import task2.stripe.models.Matrix;

class MultiplierThreadStripe implements Runnable {
    private Matrix first;
    private Matrix second;
    private Result result;
    private int startRow;
    private int endRow;

    public MultiplierThreadStripe(Matrix first, Matrix second, Result result, int startRow, int endRow) {
        this.first = first;
        this.second = second;
        this.result = result;
        this.startRow = startRow;
        this.endRow = endRow;
    }

    @Override
    public void run() {
        int columns = second.getColumns();
        int commonDimension = first.getColumns();

        for (int i = startRow; i < endRow; i++) {
            for (int j = 0; j < columns; j++) {
                int value = 0;
                for (int k = 0; k < commonDimension; k++) {
                    value += first.getValueAt(i, k) * second.getValueAt(k, j);
                }
                result.setValueAt(i, j, value);
            }
        }
    }
}