import java.util.Arrays;
import java.util.concurrent.ExecutionException;

public class Main {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        Graph graph = Graph.generateGraph(11);
        //GraphColoringSequential graphColoringSequential = new GraphColoringSequential(graph, 3);
        //graphColoringSequential.graphColoring();

        //GraphColoringParallelized graphColoringParallelized = new GraphColoringParallelized(graph, 3);
        //graphColoringParallelized.graphColoring();

        GraphColoringMPI graphColoringMPI = new GraphColoringMPI(graph, 18);
        graphColoringMPI.run(args);
    }
}
