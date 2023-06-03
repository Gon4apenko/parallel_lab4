package task2.stripe.models;

public class Matrix {
    private int[][] data;
    private int rows;
    private int columns;

    public Matrix(int rows, int columns) {
        this.rows = rows;
        this.columns = columns;
        this.data = new int[rows][columns];
    }

    public int getRows() {
        return rows;
    }

    public int getColumns() {
        return columns;
    }

    public int getValueAt(int row, int column) {
        return data[row][column];
    }

    public void setValueAt(int row, int column, int value) {
        data[row][column] = value;
    }
}
