import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class GraphColoringParallelized {
    private List<Integer> colors;
    private final Graph graph;
    private final int nrColors;
    private final ExecutorService executor;
    private final Lock lock;

    public GraphColoringParallelized(Graph graph, int nrColors) {
        this.graph = graph;
        this.nrColors = nrColors;
        colors = new ArrayList<>();
        for (int i = 0; i < graph.getSize(); i++)
            colors.add(0);
        executor = Executors.newCachedThreadPool();
        lock = new ReentrantLock();
    }

    private boolean isSafe(int vertice, int currentColor, List<Integer> auxColors)
    {
        var neighbours = graph.getNeighbours(vertice);
        for(int neighbour : neighbours){
            if( currentColor == auxColors.get(neighbour))
                return false;
        }
        return true;
    }

    private boolean graphColoringUtil(int vertice, List<Integer> auxColors)
    {
        if (vertice == graph.getSize()){
            lock.lock();
            this.colors = auxColors;
            lock.unlock();
            return true;
        }

        for (int c = 1; c <= nrColors; c++) {
            if (isSafe(vertice, c, auxColors)) {
                auxColors.set(vertice, c);
                if (graphColoringUtil(vertice + 1, auxColors))
                    return true;
                auxColors.set(vertice, 0);
            }
        }
        return false;
    }

    private Future<Boolean> check(int vertice, int currentColor, int currentDepth, List<Integer> auxColors){
        return executor.submit( () -> {
            auxColors.set(vertice, currentColor);
            if(graphColoringUtilParallel(vertice + 1, currentDepth + 1, auxColors))
                return true;
            auxColors.set(vertice, 0);
            return false;
                }
        );
    }

    private boolean graphColoringUtilParallel(int vertice, int currentDepth, List<Integer> auxColors) throws ExecutionException, InterruptedException {
        if (currentDepth > 2)
            return graphColoringUtil(vertice,auxColors);
        if (vertice == graph.getSize())
            return true;
        List<Future<Boolean>> tasks = new ArrayList<>();
        for (int c = 1; c <= nrColors; c++)
        {
            if (isSafe(vertice, c,auxColors))
            {
                List<Integer> newAuxColors = new ArrayList<>(auxColors);
                tasks.add(check(vertice, c, currentDepth, newAuxColors));
                for(Future<Boolean> task : tasks){
                    if(task.get()){
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void graphColoring() throws ExecutionException, InterruptedException {
        if (!graphColoringUtilParallel( 0, 0, colors)) {
            System.out.println("Solution does not exist");
            executor.shutdown();
            return;
        }
        executor.shutdown();
        for(int color: colors){
            System.out.print(color + " ");
        }

    }


}
