import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Polynomial {
    private final List<Integer> coefficients;

    public Polynomial(List<Integer> coefficients) {
        this.coefficients = coefficients;
    }

    public Polynomial(int degree) {
        //[0..degree]
        coefficients = new ArrayList<>(degree + 1);

        Random r = new Random();
        for (int i = 0; i < degree; i++) {
            coefficients.add(r.nextInt(9));
        }

        coefficients.add(r.nextInt(9) + 1);
    }

    public int getDegree() {
        return this.coefficients.size() - 1;
    }

    public int getLength() {
        return this.coefficients.size();
    }

    public List<Integer> getCoefficients() {
        return coefficients;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        int power = getDegree();
        for (int i = getDegree(); i >= 0; i--) {
            if ( coefficients.get(i) == 0)
                continue;
            str.append(" ").append(coefficients.get(i)).append("x^").append(power).append(" +");
            power--;
        }
        str.deleteCharAt(str.length() - 1);
        return str.toString();
    }
}
