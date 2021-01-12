import mpi.MPI;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class KaratsubaMPI {
    Polynomial p;
    Polynomial q;
    public KaratsubaMPI(Polynomial p, Polynomial q) {
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
            System.out.println("Karatsuba: " + (end - start) + " ms");
            //System.out.println("Result: " + result + "\n\n");
        }
        else {
            child();
        }
        MPI.Finalize();
    }

    private Polynomial parent(int rank, int processCount, Polynomial p, Polynomial q) {
        int m = getSplitSize(p, q);
        Polynomial lowP = new Polynomial(new ArrayList<>(p.getCoefficients().subList(0, m)));
        Polynomial highP = new Polynomial(new ArrayList<>(p.getCoefficients().subList(m, p.getLength())));
        Polynomial lowQ = new Polynomial(new ArrayList<>(q.getCoefficients().subList(0, m)));
        Polynomial highQ = new Polynomial(new ArrayList<>(q.getCoefficients().subList(m, q.getLength())));

        for (int i = 1; i < processCount; i++) {
            Polynomial[] sendP = new Polynomial[1];
            Polynomial[] sendQ = new Polynomial[1];

            if(i==1){
                sendP[0] = lowP;
                sendQ[0] = lowQ;
            }
            else{
                sendP[0] = Polynomial.add(lowP, highP);
                sendQ[0] = Polynomial.add(lowQ, highQ);
            }

            MPI.COMM_WORLD.Send(sendP, 0, 1, MPI.OBJECT, i, 0);
            MPI.COMM_WORLD.Send(sendQ, 0, 1, MPI.OBJECT, i, 0);
        }

        Polynomial z0;
        Polynomial z1;
        Polynomial z2;

        ArrayList<Polynomial> results = new ArrayList<>();
        for (int i = 1; i < processCount; i++) {
            Polynomial[] receiveResult = new Polynomial[1];
            MPI.COMM_WORLD.Recv(receiveResult, 0, 1, MPI.OBJECT, i, 0);
            results.add(receiveResult[0]);

        }
        z0 = results.get(0);
        z1 = results.get(1);
        z2 = simpleSequential(highP, highQ);

        return Polynomial.add(Polynomial.add(Polynomial.shift(z2, 2 * m), Polynomial.shift(Polynomial.subtract(Polynomial.subtract(z1, z2), z0), m)), z0);
    }

    private void child() {
        Polynomial p;
        Polynomial q;
        Polynomial[] receiveP = new Polynomial[1];
        Polynomial[] receiveQ = new Polynomial[1];
        Polynomial[] sendResult = new Polynomial[1];

        MPI.COMM_WORLD.Recv(receiveP, 0, 1, MPI.OBJECT, 0, 0);
        MPI.COMM_WORLD.Recv(receiveQ, 0, 1, MPI.OBJECT, 0, 0);

        p = receiveP[0];
        q = receiveQ[0];

        Polynomial result;
        result = simpleSequential(p, q);

        sendResult[0] = result;
        MPI.COMM_WORLD.Send(sendResult, 0, 1, MPI.OBJECT, 0, 0);
    }

    public static int getSplitSize(Polynomial p1, Polynomial p2){
        return Math.max(p1.getDegree(), p2.getDegree()) / 2;
    }

    public static Polynomial simpleSequential(Polynomial p1, Polynomial p2) {
        int resultSize = p1.getDegree() + p2.getDegree() + 1;
        List<Integer> coefficients = new ArrayList<>(Collections.nCopies(resultSize, 0));

        for (int i = 0; i < p1.getCoefficients().size(); i++) {
            for (int j = 0; j < p2.getCoefficients().size(); j++) {
                int index = i + j;
                int value = p1.getCoefficients().get(i) * p2.getCoefficients().get(j);
                coefficients.set(index, coefficients.get(index) + value);
            }
        }
        return new Polynomial(coefficients);
    }
}
