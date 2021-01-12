import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        DG graph = DG.generateDirectedGraph(15);
        System.out.println(graph);
        System.out.println(findHamiltonianCycle(graph, 4));
    }

    public static List<Integer> findHamiltonianCycle(DG graph, int threadCount) throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        Lock lock = new ReentrantLock();
        int size = graph.getVertices().size();
        List<Integer> hamiltonianCycle = new ArrayList<>(size);
        Random random = new Random();

        executorService.execute(new Task(graph, lock, random.nextInt(size - 1), hamiltonianCycle));


        executorService.shutdown();

        executorService.awaitTermination(10, TimeUnit.SECONDS);

        return hamiltonianCycle;
    }
}
