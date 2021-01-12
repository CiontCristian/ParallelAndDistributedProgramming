import java.io.Serializable;

public class Message implements Serializable {
    private final String action;
    private final String variable;
    private final Integer value;

    public Message(String action, String variable, Integer value) {
        this.action = action;
        this.variable = variable;
        this.value = value;
    }

    public String getAction() {
        return action;
    }

    public String getVariable() {
        return variable;
    }

    public Integer getValue() {
        return value;
    }

}
