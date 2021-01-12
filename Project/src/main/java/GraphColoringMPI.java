import mpi.MPI;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GraphColoringMPI {
    private final Graph graph;
    private final int nrColors;
    private final List<Integer> colors;
    public GraphColoringMPI(Graph graph, int nrColors){
        this.graph = graph;
        this.nrColors = nrColors;
        colors = new ArrayList<>();
        for (int i = 0; i < graph.getSize(); i++)
            colors.add(0);
    }
    //rank 0 porneste
    // 0-9
    //(0 1 2) (3 4 5) (6 7 8 ) 9
    //
    /*
     * 18 culori
     * 0,1,2 vertex 1 culori 0-5
     * 3 4 5 vertex 1 culori 6-11
     * 6 7 8 vertex 1 culori 12-17
     * 0 vertex 1 culori 0-5 vertex 2 culori 0-5
     * 1 vertex 1 culori 0-5 vertex 2 culori 6-11
     * 2 vertex 1 culori 0-5 vertex 2 culori 12-17
     * 3 vertex 1 culori 6-11 vertex 2 culori 0-5
     * */

    /*
    0-5 0-5 0-17 0-17 ..
    0-5 6-11 0-17 0-17 ..
    0-5 12-17 0-17 0-17 ..
    6-11 0-5 0-17 0-17 ..
    6-11 6-11 0-17 0-17 ..
    6-11 12-17 0-17 0-17 ..
    12-17 0-5 0-17 0-17 ..
    12-17 6-11 0-17 0-17 ..
    12-17 12-17 0-17 0-17 ..
    */
    public void run(String[] args) {
        MPI.Init(args);
        int rank = MPI.COMM_WORLD.Rank();
        int processCount = MPI.COMM_WORLD.Size();

        if (rank == 0){
            int end=processCount-1; //verifica end-0+1=3k
            Interval[] verticesInterval = new Interval[graph.getSize()];
            for(int i=0;i<verticesInterval.length;i++){
                verticesInterval[i]=new Interval(1,nrColors);
            }
            splitWork(0,end,0,verticesInterval);
            //recv la solutii
            List<int[]> solutions = new ArrayList<>();
       
            for(int i=1; i<processCount; i++){
                int[] result = new int[colors.size()];
                MPI.COMM_WORLD.Recv(result, 0, graph.getSize(), MPI.INT, i, 0);
                solutions.add(result);
            }
            int[] auxColors = new int[colors.size()];
            for(int c=0;c<colors.size();c++){
                auxColors[c] = colors.get(c);
            }
            solutions.add(auxColors);

            for(int[] sol: solutions){
                System.out.println(Arrays.toString(sol));
            }


        }
        else {
            Interval[] verticesInterval = new Interval[graph.getSize()];
            MPI.COMM_WORLD.Recv(verticesInterval, 0, graph.getSize(), MPI.OBJECT, 0, 0);
            doWork(verticesInterval);
        }
        MPI.Finalize();
    }
    //endsplit-startsplit+1 =3k
    private void splitWork(int startSplit, int endSplit, int vertice, Interval[] verticesInterval){
        int childProcesses = endSplit-startSplit+1;//childProcesses+parent
        if(childProcesses>=3){
            int step= childProcesses/3;
            Interval[] child1=Arrays.copyOf(verticesInterval,graph.getSize());
            child1[vertice]=new Interval(1,nrColors/3);
            Interval[] child2=Arrays.copyOf(verticesInterval,graph.getSize());
            child2[vertice]=new Interval(nrColors/3+1,2*nrColors/3);
            Interval[] child3=Arrays.copyOf(verticesInterval,graph.getSize());
            child3[vertice]=new Interval(2*nrColors/3+1,nrColors);

            splitWork(startSplit+step+step,endSplit,vertice+1,child3);
            splitWork(startSplit+step,startSplit+step+step-1,vertice+1,child2);
            splitWork(startSplit,startSplit+step-1,vertice+1,child1);

        }
        else{
            if(endSplit!=0) // do work for process0
                MPI.COMM_WORLD.Send(verticesInterval, 0, graph.getSize(), MPI.OBJECT, endSplit, 0);
            else doWork(verticesInterval);
        }

    }
    private void doWork(Interval[] verticesInterval){
        if (!graphColoringUtil( 0,verticesInterval)) {
            System.out.println("Solution does not exist");
            return;
        }
        int rank = MPI.COMM_WORLD.Rank();
        int[] auxColors = new int[colors.size()];
        for(int c=0;c<colors.size();c++){
            auxColors[c] = colors.get(c);
        }
        if(rank != 0)
            MPI.COMM_WORLD.Send(auxColors, 0, graph.getSize(), MPI.INT, 0, 0);

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
    private boolean graphColoringUtil(int vertice,Interval[] verticesInterval)
    {
        if (vertice == graph.getSize())
            return true;
        for (int c = verticesInterval[vertice].getStart(); c <= verticesInterval[vertice].getEnd(); c++) {
            if (isSafe(vertice, c)) {
                colors.set(vertice, c);
                if (graphColoringUtil(vertice + 1,verticesInterval))
                    return true;
                colors.set(vertice, 0);
            }
        }
        return false;
    }
    //
    private void printInterval(Interval[] verticesInterval){
        StringBuilder s= new StringBuilder();
        for (Interval interval : verticesInterval) {
            if(interval!=null)
                s.append(interval.toString()).append(" ");
            else
                s.append(" null ");
        }
        System.out.println(s);
    }
}
