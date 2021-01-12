import model.Matrix;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Main {
    private static final int NR_THREADS = 128;

    private static Matrix multiplyWithThreads(Matrix first, Matrix second) throws InterruptedException {
        Matrix result = new Matrix(first.getNrRows(), second.getNrCols());

        List<MatrixMultiplication> threads = new ArrayList<>();

        for(int i=0;i<NR_THREADS; i++){
            threads.add(new MatrixMultiplication(first,second, result));
        }

        for(int i=0;i<result.getNrRows();i++){
            for (int j=0;j<result.getNrCols();j++){
                threads.get(result.getRowIndex(i, j) % NR_THREADS).addToThreadProcessingQueue(i, j);
            }
        }

        for (int i = 0; i < NR_THREADS; i++){
            threads.get(i).start();
        }

        for (int i = 0; i < NR_THREADS; i++){
            threads.get(i).join();
        }
        return result;

    }

    private static Matrix multiplyWithThreadPool(Matrix first, Matrix second) throws InterruptedException, ExecutionException {
        Matrix result = new Matrix(first.getNrRows(), second.getNrCols());

        List<MatrixMultiplication> threads = new ArrayList<>();

        for(int i=0;i<NR_THREADS; i++){
            threads.add(new MatrixMultiplication(first,second, result));
        }

        for(int i=0;i<result.getNrRows();i++){
            for (int j=0;j<result.getNrCols();j++){
                threads.get(result.getRowIndex(i, j) % NR_THREADS).addToThreadProcessingQueue(i, j);
            }
        }

        ExecutorService executorService = Executors.newFixedThreadPool(64);
        //ExecutorService executorService = Executors.newCachedThreadPool();
        List<Future<?>> futures = new ArrayList<>();
        threads.forEach(task -> futures.add(executorService.submit(task)));
        futures.forEach(future -> {
            try{
                future.get();
            }catch (Exception e){
                System.out.println(e.getMessage());
            }
        });
        executorService.shutdown();

        return result;
    }

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        Matrix first = new Matrix(800,900);
        Matrix second = new Matrix(900, 800);

        float startTime = System.nanoTime() / 1000000;
        Matrix result1 = multiplyWithThreads(first, second);
        float endTime = System.nanoTime() / 1000000;

        //System.out.println(result1);
        System.out.println("Time elapsed with normal threads: " + (endTime - startTime)/1000 + " seconds with " + NR_THREADS + " threads");

        startTime = System.nanoTime() / 1000000;
        Matrix result2 = multiplyWithThreadPool(first, second);
        endTime = System.nanoTime() / 1000000;

        //System.out.println(result2);
        System.out.println("Time elapsed with thread pool: " + (endTime - startTime)/1000 + " seconds with " + NR_THREADS + " threads");

    }
}
