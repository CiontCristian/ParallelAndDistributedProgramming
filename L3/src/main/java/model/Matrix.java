package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Matrix {
    private final int nrRows;
    private final int nrCols;
    private final List<List<Integer>> matrix;

    public Matrix(int nrRows, int nrCols) {
        this.nrRows = nrRows;
        this.nrCols = nrCols;

        Random r = new Random();

        matrix = new ArrayList<>();
        for(int i=0;i<nrRows;i++){
            matrix.add(new ArrayList<>());
            for(int j=0;j<nrCols;j++){
                matrix.get(i).add(r.nextInt((999-100+1)+100));
                //matrix.get(i).add(5);
            }
        }
    }

    public int getNrRows() {
        return nrRows;
    }

    public int getNrCols() {
        return nrCols;
    }

    public int getRowIndex(int row, int col){
        return row * nrCols + col;
    }

    public int getElement(int row, int col){
        return matrix.get(row).get(col);
    }

    public void setElement(int row, int col, int value){
        this.matrix.get(row).set(col, value);
    }

    @Override
    public String toString() {
        StringBuilder ss = new StringBuilder();
        for (int i = 0; i < this.nrRows; i++){
            ss.append(this.matrix.get(i).toString()).append("\n");
        }

        return ss.toString();
    }
}
