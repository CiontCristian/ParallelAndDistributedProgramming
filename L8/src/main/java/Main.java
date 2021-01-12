import mpi.MPI;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        MPI.Init(args);
        int rank = MPI.COMM_WORLD.Rank();

        DSM dsm = new DSM();
        Runnable runnable = new Observer(dsm);
        Thread thread = new Thread(runnable);
        thread.start();
        if (rank == 0) {
            dsm.subscribeToCurrentProcess("a");
            dsm.subscribeToCurrentProcess("b");
            dsm.subscribeToCurrentProcess("c");

            dsm.updateVariable("a", 10);
            System.out.println("Process 0 - " + dsm);
            dsm.updateVariable("b", 20);
            System.out.println("Process 0 - " + dsm);
            dsm.updateVariable("c", 30);
            System.out.println("Process 0 - " + dsm);

            dsm.compareAndExchange("a", 10, 100);
            System.out.println("Process 0 - " + dsm);
            dsm.unsubscribe();
        }
        if (rank == 1) {
            dsm.subscribeToCurrentProcess("a");

            dsm.updateVariable("b", 50);
            System.out.println("Process 1 - " + dsm);
        }
        if (rank == 2) {
            dsm.subscribeToCurrentProcess("a");

            dsm.updateVariable("b", 500);
            System.out.println("Process 1 - " + dsm);
        }
        thread.join();
        MPI.Finalize();
    }
}
