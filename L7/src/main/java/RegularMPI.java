import mpi.MPI;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RegularMPI {
    Polynomial p;
    Polynomial q;
    public RegularMPI(Polynomial p, Polynomial q) {
        this.p = p;
        this.q = q;
    }

    public void run(String[] args) {
        MPI.Init(args);
        int rank = MPI.COMM_WORLD.Rank();
        int processCount = MPI.COMM_WORLD.Size();
        if (rank == 0) {
            Polynomial result;
            long start = System.currentTimeMillis();
            result = parent(rank, processCount, p, q);
            long end = System.currentTimeMillis();
            System.out.println("Regular: " + (end - start) + " ms");
            //System.out.println("Result: " + result + "\n\n");
        }
        else {
            child();
        }
        MPI.Finalize();
    }

    private Polynomial parent(int rank, int processCount, Polynomial p, Polynomial q) {
        int step = (p.getDegree() + q.getDegree() + 1)/(processCount - 1);//processCount - 1 bc parent is excluded

        int start = 0;
        for (int i = 1; i < processCount; i++) {
            Polynomial[] sendP = new Polynomial[1];
            Polynomial[] sendQ = new Polynomial[1];
            int[] sendStart = new int[1];
            int[] sendEnd = new int[1];
            sendP[0] = p;
            sendQ[0] = q;
            sendStart[0] = start;

            if (start + step >= p.getDegree() + q.getDegree() - 1)
                sendEnd[0] = p.getDegree() + q.getDegree() + 1;
            else {
                sendEnd[0] = start + step;
            }


            MPI.COMM_WORLD.Send(sendP, 0, 1, MPI.OBJECT, i, 0);
            MPI.COMM_WORLD.Send(sendQ, 0, 1, MPI.OBJECT, i, 0);
            MPI.COMM_WORLD.Send(sendStart, 0, 1, MPI.INT, i, 0);
            MPI.COMM_WORLD.Send(sendEnd, 0, 1, MPI.INT, i, 0);
            start = start + step;
        }

        Polynomial[] received = new Polynomial[1];
        ArrayList<Polynomial> results = new ArrayList<>();
        for (int i = 1; i < processCount; i++) {
            MPI.COMM_WORLD.Recv(received, 0, 1, MPI.OBJECT, i, 0);
            results.add(received[0]);
        }

        Polynomial result;
        result = Polynomial.add(results.get(0), results.get(1));

        return result;
    }

    private void child(){
        Polynomial p;
        Polynomial q;
        Polynomial[] receiveP = new Polynomial[1];
        Polynomial[] receiveQ = new Polynomial[1];
        Polynomial[] sendResult = new Polynomial[1];
        int[] receiveStart = new int[1];
        int[] receiveEnd = new int[1];
        int startPos, endPos;

        MPI.COMM_WORLD.Recv(receiveP, 0, 1, MPI.OBJECT, 0, 0);
        MPI.COMM_WORLD.Recv(receiveQ, 0, 1, MPI.OBJECT, 0, 0);
        MPI.COMM_WORLD.Recv(receiveStart, 0, 1, MPI.INT, 0, 0);
        MPI.COMM_WORLD.Recv(receiveEnd, 0, 1, MPI.INT, 0, 0);

        p = receiveP[0];
        q = receiveQ[0];
        startPos = receiveStart[0];
        endPos = receiveEnd[0];

        int resultSize = p.getDegree() + q.getDegree() + 1;
        List<Integer> coefficients = new ArrayList<>(Collections.nCopies(resultSize, 0));
        Polynomial result = new Polynomial(coefficients);

        for (int i = startPos; i < endPos; i++) {
            for (int j = 0; j <= i; j++) {
                if (j < p.getLength() && (i - j) < q.getLength()) {
                    int value = p.getCoefficients().get(j) * q.getCoefficients().get(i - j);
                    result.getCoefficients().set(i, result.getCoefficients().get(i) + value);
                }
            }
        }

        sendResult[0] = result;
        MPI.COMM_WORLD.Send(sendResult, 0, 1, MPI.OBJECT, 0, 0);
    }


}
