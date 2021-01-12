import mpi.MPI;

import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        Polynomial p = new Polynomial(50);
        Polynomial q = new Polynomial(50);
        //Polynomial p = new Polynomial(Arrays.asList(2, 3, 1, 7, 8));
        //Polynomial q = new Polynomial(Arrays.asList(1, 5, 4, 6, 6));

        RegularMPI regularMPI = new RegularMPI(p, q);
        regularMPI.run(args);

        KaratsubaMPI karatsubaMPI = new KaratsubaMPI(p, q);
        karatsubaMPI.run(args);

    }
}
