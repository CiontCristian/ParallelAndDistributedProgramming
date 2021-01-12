import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

public class Main {

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        //Polynomial p = new Polynomial(50);
        //Polynomial q = new Polynomial(50);
        Polynomial p = new Polynomial(Arrays.asList(2, 3, 1));
        Polynomial q = new Polynomial(Arrays.asList(1, 5));
        System.out.println(p);
        System.out.println(q);
        testSimpleSequential(p, q);
        testSimpleParallelized(p,q);
        testKaratsubaSequential(p, q);
        testKaratsubaParallelized(p,q);

    }

    private static Polynomial testSimpleParallelized(Polynomial p, Polynomial q) throws InterruptedException {
        long startTime = System.currentTimeMillis();
        Polynomial result2 = Algorithms.simpleParallelized(p, q, 5);
        long endTime = System.currentTimeMillis();
        System.out.println("Simple parallel : " + (endTime - startTime) + " ms");
        System.out.println(result2);
        return result2;
    }

    private static Polynomial testSimpleSequential(Polynomial p, Polynomial q) {
        long startTime = System.currentTimeMillis();
        Polynomial result1 = Algorithms.simpleSequential(p, q);
        long endTime = System.currentTimeMillis();
        System.out.println("Simple sequential : " + (endTime - startTime) + " ms");
        return result1;
    }

    private static Polynomial testKaratsubaParallelized(Polynomial p, Polynomial q) throws ExecutionException,
            InterruptedException, ExecutionException {
        long startTime = System.currentTimeMillis();
        Polynomial result4 = Algorithms.karatsubaParallelized(p, q, 1);
        long endTime = System.currentTimeMillis();
        System.out.println("Karatsuba parallel : " + (endTime - startTime) + " ms");
        return result4;
    }

    private static Polynomial testKaratsubaSequential(Polynomial p, Polynomial q) throws InterruptedException {
        long startTime = System.currentTimeMillis();
        Polynomial result3 = Algorithms.karatsubaSequential(p, q, 1);
        long endTime = System.currentTimeMillis();
        System.out.println("Karatsuba sequential : " + (endTime - startTime) + " ms");
        System.out.println(result3);
        return result3;
    }
}
