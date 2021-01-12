import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;

public class Algorithms {

    public static Polynomial simpleSequential(Polynomial p1, Polynomial p2) {
        int resultSize = p1.getDegree() + p2.getDegree() + 1;
        List<Integer> coefficients = new ArrayList<>(Collections.nCopies(resultSize, 0));

        for (int i = 0; i < p1.getCoefficients().size(); i++) {
            for (int j = 0; j < p2.getCoefficients().size(); j++) {
                int index = i + j;
                int value = p1.getCoefficients().get(i) * p2.getCoefficients().get(j);
                coefficients.set(index, coefficients.get(index) + value);
            }
        }
        return new Polynomial(coefficients);
    }

    public static Polynomial simpleParallelized(Polynomial p1, Polynomial p2, int nrOfThreads) throws
            InterruptedException {

        int resultSize = p1.getDegree() + p2.getDegree() + 1;
        List<Integer> coefficients = new ArrayList<>(Collections.nCopies(resultSize, 0));
        Polynomial result = new Polynomial(coefficients);

        ExecutorService executor = Executors.newFixedThreadPool(nrOfThreads);
        int step = result.getLength() / nrOfThreads;
        if (step == 0) {
            step = 1;
        }

        int end;
        for (int start = 0; start < result.getLength(); start += step) {
            end = start + step;
            Task task = new Task(start, end, p1, p2, result);
            executor.execute(task);
        }

        executor.shutdown();
        executor.awaitTermination(50, TimeUnit.SECONDS);

        return result;
    }

    public static int getSplitSize(Polynomial p1, Polynomial p2){
        return Math.max(p1.getDegree(), p2.getDegree()) / 2;
    }

    public static Polynomial karatsubaSequential(Polynomial p1, Polynomial p2, int depth) throws InterruptedException {
        if (p1.getDegree() < 2 || p2.getDegree() < 2) {
            return simpleSequential(p1, p2);
        }

        if(depth > 4){
            return simpleParallelized(p1, p2, 5);
        }

        int m = getSplitSize(p1, p2);
        Polynomial lowP1 = new Polynomial(p1.getCoefficients().subList(0, m));
        Polynomial highP1 = new Polynomial(p1.getCoefficients().subList(m, p1.getLength()));
        Polynomial lowP2 = new Polynomial(p2.getCoefficients().subList(0, m));
        Polynomial highP2 = new Polynomial(p2.getCoefficients().subList(m, p2.getLength()));

        Polynomial z0 = karatsubaSequential(lowP1, lowP2, depth + 1);
        Polynomial z1 = karatsubaSequential(add(lowP1, highP1), add(lowP2, highP2), depth + 1);
        Polynomial z2 = karatsubaSequential(highP1, highP2, depth + 1);

        return add(add(shift(z2, 2 * m), shift(subtract(subtract(z1, z2), z0), m)), z0);
    }

    public static Polynomial karatsubaParallelized(Polynomial p1, Polynomial p2, int depth)
            throws ExecutionException, InterruptedException {

        if(depth > 4){
            return karatsubaSequential(p1, p2, depth);
        }

        if (p1.getDegree() < 2 || p2.getDegree() < 2) {
            return karatsubaSequential(p1, p2, depth);
        }

        int m = getSplitSize(p1, p2);
        Polynomial lowP1 = new Polynomial(p1.getCoefficients().subList(0, m));
        Polynomial highP1 = new Polynomial(p1.getCoefficients().subList(m, p1.getLength()));
        Polynomial lowP2 = new Polynomial(p2.getCoefficients().subList(0, m));
        Polynomial highP2 = new Polynomial(p2.getCoefficients().subList(m, p2.getLength()));

        ExecutorService executor = Executors.newCachedThreadPool();

        Callable<Polynomial> task1 = () -> karatsubaParallelized(lowP1, lowP2, depth + 1);
        Callable<Polynomial> task2 = () -> karatsubaParallelized(add(lowP1, highP1), add(lowP2, highP2), depth + 1);
        Callable<Polynomial> task3 = () -> karatsubaParallelized(highP1, highP2, depth+1);

        Future<Polynomial> f1 = executor.submit(task1);
        Future<Polynomial> f2 = executor.submit(task2);
        Future<Polynomial> f3 = executor.submit(task3);

        executor.shutdown();

        Polynomial z0 = f1.get();
        Polynomial z1 = f2.get();
        Polynomial z2 = f3.get();

        executor.awaitTermination(60, TimeUnit.SECONDS);

        return add(add(shift(z2, 2 * m), shift(subtract(subtract(z1, z2), z0), m)), z0);
    }


    public static Polynomial shift(Polynomial p, int offset) {
        List<Integer> coefficients = new ArrayList<>();
        for (int i = 0; i < offset; i++) {
            coefficients.add(0);
        }
        for (int i = 0; i < p.getLength(); i++) {
            coefficients.add(p.getCoefficients().get(i));
        }
        return new Polynomial(coefficients);
    }


    public static Polynomial add(Polynomial p1, Polynomial p2) {
        int minDegree = Math.min(p1.getDegree(), p2.getDegree());
        int maxDegree = Math.max(p1.getDegree(), p2.getDegree());
        List<Integer> coefficients = new ArrayList<>(maxDegree + 1);

        for (int i = 0; i <= minDegree; i++) {
            coefficients.add(p1.getCoefficients().get(i) + p2.getCoefficients().get(i));
        }

        addRemainingCoefficients(p1, p2, minDegree, maxDegree, coefficients);

        return new Polynomial(coefficients);
    }

    private static void addRemainingCoefficients(Polynomial p1, Polynomial p2, int minDegree, int maxDegree,
                                                 List<Integer> coefficients) {
        if (minDegree != maxDegree) {
            if (maxDegree == p1.getDegree()) {
                for (int i = minDegree + 1; i <= maxDegree; i++) {
                    coefficients.add(p1.getCoefficients().get(i));
                }
            } else {
                for (int i = minDegree + 1; i <= maxDegree; i++) {
                    coefficients.add(p2.getCoefficients().get(i));
                }
            }
        }
    }


    public static Polynomial subtract(Polynomial p1, Polynomial p2) {
        int minDegree = Math.min(p1.getDegree(), p2.getDegree());
        int maxDegree = Math.max(p1.getDegree(), p2.getDegree());
        List<Integer> coefficients = new ArrayList<>(maxDegree + 1);


        for (int i = 0; i <= minDegree; i++) {
            coefficients.add(p1.getCoefficients().get(i) - p2.getCoefficients().get(i));
        }

        addRemainingCoefficients(p1, p2, minDegree, maxDegree, coefficients);

        int i = coefficients.size() - 1;
        while (coefficients.get(i) == 0 && i > 0) {
            coefficients.remove(i);
            i--;
        }

        return new Polynomial(coefficients);
    }
}
