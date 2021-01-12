import java.util.*;

import static java.util.Collections.shuffle;

public class DG {
    private final List<Set<Integer>> edges;
    private final List<Integer> vertices;

    public DG(int size){
        vertices = new ArrayList<>();
        edges = new ArrayList<>(size);

        for(int i = 0; i < size; i++){
            vertices.add(i);
            edges.add(new HashSet<>());
        }
    }

    public void addEdge(int startNode, int endNode){
        if(startNode != endNode)
            edges.get(startNode).add(endNode);
    }

    public Set<Integer> getNeighbours(int node){
        return edges.get(node);
    }

    public List<Integer> getVertices(){
        return vertices;
    }

    public static DG generateDirectedGraph(int size){
        DG graph = new DG(size);
        Random random = new Random();

        List<Integer> nodes = graph.getVertices();

        shuffle(nodes);

        for (int i = 1; i < nodes.size(); i++){
            graph.addEdge(nodes.get(i - 1),  nodes.get(i));
        }

        graph.addEdge(nodes.get(nodes.size() -1), nodes.get(0));

        for(int i = 0; i < size / 2; i++){
            int startNode = random.nextInt(size - 1);
            int endNode = random.nextInt(size - 1);
            graph.addEdge(startNode, endNode);
        }

        return graph;
    }

    @Override
    public String toString(){
        StringBuilder stringBuilder = new StringBuilder();
        for(int i =0; i< edges.size(); i++){
            for(Integer node: edges.get(i)){
               stringBuilder.append(i).append(" -> ").append(node).append("\n");
            }
        }
        return stringBuilder.toString();
    }
}
