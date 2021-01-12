import java.util.*;

import static java.util.Collections.shuffle;

public class Graph {
    private final List<Set<Integer>> edges;
    private final List<Integer> vertices;
    private final int size;

    public Graph(int size){
        this.size = size;
        vertices = new ArrayList<>();
        edges = new ArrayList<>(this.size);

        for(int i = 0; i < this.size; i++){
            vertices.add(i);
            edges.add(new HashSet<>());
        }
    }

    public void addEdge(int startNode, int endNode){
        if(startNode != endNode){
            edges.get(startNode).add(endNode);
            edges.get(endNode).add(startNode);
        }
    }

    public Set<Integer> getNeighbours(int node){
        return edges.get(node);
    }

    public List<Integer> getVertices(){
        return vertices;
    }

    public int getSize(){return size;}

    public static Graph generateGraph(int size){
        Graph graph = new Graph(size);
        Random random = new Random();

        for(int i = 0; i < size; i++){
            int startNode = random.nextInt(size - 1);
            int endNode = random.nextInt(size - 1);
            graph.addEdge(random.nextInt(size - 1), random.nextInt(size - 1));
            graph.addEdge(random.nextInt(size - 1), random.nextInt(size - 1));
            graph.addEdge(random.nextInt(size - 1), random.nextInt(size - 1));
            graph.addEdge(random.nextInt(size - 1), random.nextInt(size - 1));
            graph.addEdge(random.nextInt(size - 1), random.nextInt(size - 1));
            graph.addEdge(random.nextInt(size - 1), random.nextInt(size - 1));
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
