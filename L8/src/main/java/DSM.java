import mpi.MPI;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DSM {
    private final Map<String, Integer> variables = new HashMap<>();
    private final Map<String, List<Integer>> subscriptions = new HashMap<>();

    public DSM(){
        variables.put("a", -1);
        variables.put("b", -1);
        variables.put("c", -1);
        subscriptions.put("a", new ArrayList<>());
        subscriptions.put("b", new ArrayList<>());
        subscriptions.put("c", new ArrayList<>());
    }

    private void notifyAllProcesses(Message message){
        int processCount = MPI.COMM_WORLD.Size();
        int currentRank = MPI.COMM_WORLD.Rank();
        for(int i=0; i< processCount; i++){
            if( i == currentRank) continue;
            Message[] sendMessage = new Message[1];
            sendMessage[0] = message;
            MPI.COMM_WORLD.Send(sendMessage, 0, 1, MPI.OBJECT, i, 0);
        }
    }

    private void notifySubscribers(String variable, Message message){
        int processCount = MPI.COMM_WORLD.Size();
        int currentRank = MPI.COMM_WORLD.Rank();
        for(int i=0; i< processCount; i++){
            if( i == currentRank) continue;
            if( !subscriptions.get(variable).contains(i)) continue;
            Message[] sendMessage = new Message[1];
            sendMessage[0] = message;
            MPI.COMM_WORLD.Send(sendMessage, 0, 1, MPI.OBJECT, i, 0);
        }
    }

    public void subscribeToCurrentProcess(String variable){
        int currentProcessId = MPI.COMM_WORLD.Rank();
        this.subscriptions.get(variable).add(currentProcessId);
        Message message = new Message("subscribe", variable, currentProcessId);
        notifyAllProcesses(message);
    }

    public void subscribeToOtherProcess(String variable, Integer rank){
        this.subscriptions.get(variable).add(rank);
    }

    public void unsubscribe(){
        Message message = new Message("unsubscribe", "", 0);
        notifyAllProcesses(message);
    }

    public void setVariable(String variable, Integer value){
        variables.put(variable, value);
    }

    public void updateVariable(String variable, Integer value){
        variables.put(variable, value);
        Message message = new Message("update", variable, value);
        notifySubscribers(variable, message);
    }

    public void compareAndExchange(String variable, Integer oldValue, Integer newValue){
        if(variables.get(variable).equals(oldValue)){
            variables.put(variable, newValue);
            Message message = new Message("update", variable, newValue);
            notifySubscribers(variable, message);
        }
    }

    @Override
    public String toString() {
        return "DSM: variables = " + variables + "\n\t\t\t\tsubscribers = " + subscriptions;
    }
}
