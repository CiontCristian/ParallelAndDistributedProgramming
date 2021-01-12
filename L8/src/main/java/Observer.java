import mpi.MPI;

public class Observer implements Runnable{
    private final DSM dsm;

    public Observer(DSM dsm) {
        this.dsm = dsm;
    }

    @Override
    public void run() {
        while (true){
            Message[] receivedMessage = new Message[1];
            MPI.COMM_WORLD.Recv(receivedMessage, 0, 1, MPI.OBJECT, MPI.ANY_SOURCE, 0);
            Message message = receivedMessage[0];

            switch (message.getAction()){
                case "subscribe":
                    System.out.println("Process " + MPI.COMM_WORLD.Rank() + " - Action: subscribe");
                    dsm.subscribeToOtherProcess(message.getVariable(), message.getValue());
                    System.out.println("Process " + MPI.COMM_WORLD.Rank() + " - " + dsm);
                    break;
                case "unsubscribe":
                    System.exit(0);
                    break;
                case "update":
                    System.out.println("Process " + MPI.COMM_WORLD.Rank() + " - Action: Update Var/Compare And Exchange");
                    dsm.setVariable(message.getVariable(), message.getValue());
                    System.out.println("Process " + MPI.COMM_WORLD.Rank() + " - " + dsm);
                    break;
                default:
                    System.out.println("Process " + MPI.COMM_WORLD.Rank() + " - " + dsm);
            }
        }
    }
}
