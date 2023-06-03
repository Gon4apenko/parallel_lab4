package task2.stripe.models;

public class Result {
    private int[][] data;

    public Result(int rows, int columns) {
        this.data = new int[rows][columns];
    }

    public int getRows() {
        return data.length;
    }

    public int getColumns() {
        return data[0].length;
    }

    public int getValueAt(int row, int column) {
        return data[row][column];
    }

    public void setValueAt(int row, int column, int value) {
        data[row][column] = value;
    }
}