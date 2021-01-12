import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;

public class Task implements Runnable{
    private final DG graph;
    private final Lock lock;
    private final int startNode;
    private final List<Integer> hamiltonianCycle;

    public Task(DG _graph, Lock _lock, int _currentNode, List<Integer> _cycle){
        graph = _graph;
        lock = _lock;
        startNode = _currentNode;
        hamiltonianCycle = _cycle;
    }

    @Override
    public void run() {
        try {
            visitParallelized(startNode, new ArrayList<>(), 0);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void addToCycle(List<Integer> path){
        lock.lock();
        hamiltonianCycle.clear();
        hamiltonianCycle.addAll(path);
        hamiltonianCycle.add(startNode);
        lock.unlock();
    }

    private void visit(int node, List<Integer> auxPath){
        auxPath.add(node);

        if (auxPath.size() == graph.getVertices().size()) {
            if (graph.getNeighbours(node).contains(startNode)){
                addToCycle(auxPath);
            }
            return;
        }

        for (int neighbour : graph.getNeighbours(node)) {
            if (!auxPath.contains(neighbour)){
                visit(neighbour, auxPath);
                auxPath.remove(auxPath.size() - 1);
            }
        }
    }

    private void visitParallelized(int node, List<Integer> auxPath, int depth) throws InterruptedException {
        if (depth > 3)
            visit(node, auxPath);

        else {
            auxPath.add(node);

            if (auxPath.size() == graph.getVertices().size()) {
                if (graph.getNeighbours(node).contains(startNode)) {
                    addToCycle(auxPath);
                }
                return;
            }
            List<Thread> tasks = new ArrayList<>();
            for (int neighbour : graph.getNeighbours(node)) {
                if (!auxPath.contains(neighbour)) {
                    List<Integer> copy = new ArrayList<>(auxPath);
                    tasks.add(new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                visitParallelized(neighbour, copy, depth + 1);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }));
                    auxPath.remove(auxPath.size() - 1);
                }
            }

            for (Thread task : tasks) {
                task.start();
            }
            for (Thread task : tasks) {
                task.join();
            }
        }
    }
}
