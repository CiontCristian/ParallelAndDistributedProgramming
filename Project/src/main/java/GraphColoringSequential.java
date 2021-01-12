import java.util.ArrayList;
import java.util.List;

public class GraphColoringSequential {
    private final List<Integer> colors;
    private final Graph graph;
    private final int nrColors;

    public GraphColoringSequential(Graph graph, int nrColors) {
        this.graph = graph;
        this.nrColors = nrColors;
        colors = new ArrayList<>();
        for (int i = 0; i < graph.getSize(); i++)
            colors.add(0);
    }

    private boolean isSafe(int vertice, int currentColor)
    {
        var neighbours = graph.getNeighbours(vertice);
        for(int neighbour : neighbours){
            if( currentColor == colors.get(neighbour))
                return false;
        }
        return true;
    }

    private boolean graphColoringUtil(int vertice)
    {
        if (vertice == graph.getSize())
            return true;
        for (int c = 1; c <= nrColors; c++) {
            if (isSafe(vertice, c)) {
                colors.set(vertice, c);
                if (graphColoringUtil(vertice + 1))
                    return true;
                colors.set(vertice, 0);
            }
        }
        return false;
    }

    public void graphColoring()
    {
        if (!graphColoringUtil( 0)) {
            System.out.println("Solution does not exist");
            return;
        }

        for(int color: colors){
            System.out.print(color + " ");
        }

    }

}
