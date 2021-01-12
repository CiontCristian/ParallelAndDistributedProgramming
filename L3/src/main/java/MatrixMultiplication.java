import model.Matrix;
import model.Pair;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Callable;

public class MatrixMultiplication extends Thread{
    private final Matrix first;
    private final Matrix second;
    private final Matrix result;
    private final Queue<Pair<Integer, Integer>>  queue;

    public MatrixMultiplication(Matrix first, Matrix second, Matrix result) {
        this.first = first;
        this.second = second;
        this.result = result;
        this.queue = new LinkedList<>();
    }

    public void addToThreadProcessingQueue(int row, int col){
        this.queue.add(new Pair<>(row, col));
    }

    @Override
    public void run() {
        for(Pair<Integer, Integer> pair : queue){
            int cell = 0;
            for(int i =0; i<second.getNrRows();i++){
                cell += first.getElement(pair.getKey(), i) *  second.getElement(i, pair.getValue());
            }
            result.setElement(pair.getKey(), pair.getValue(), cell);
        }

    }
}
