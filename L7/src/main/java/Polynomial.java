import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Polynomial implements Serializable {
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

    public static Polynomial shift(Polynomial p, int offset) {
        List<Integer> coefficients = new ArrayList<>();
        for (int i = 0; i < offset; i++) {
            coefficients.add(0);
        }
        for (int i = 0; i < p.getLength(); i++) {
            coefficients.add(p.getCoefficients().get(i));
        }
        return new Polynomial(coefficients);
    }


    public static Polynomial add(Polynomial p1, Polynomial p2) {
        int minDegree = Math.min(p1.getDegree(), p2.getDegree());
        int maxDegree = Math.max(p1.getDegree(), p2.getDegree());
        List<Integer> coefficients = new ArrayList<>(maxDegree + 1);

        for (int i = 0; i <= minDegree; i++) {
            coefficients.add(p1.getCoefficients().get(i) + p2.getCoefficients().get(i));
        }

        addRemainingCoefficients(p1, p2, minDegree, maxDegree, coefficients);

        return new Polynomial(coefficients);
    }

    private static void addRemainingCoefficients(Polynomial p1, Polynomial p2, int minDegree, int maxDegree,
                                                 List<Integer> coefficients) {
        if (minDegree != maxDegree) {
            if (maxDegree == p1.getDegree()) {
                for (int i = minDegree + 1; i <= maxDegree; i++) {
                    coefficients.add(p1.getCoefficients().get(i));
                }
            } else {
                for (int i = minDegree + 1; i <= maxDegree; i++) {
                    coefficients.add(p2.getCoefficients().get(i));
                }
            }
        }
    }


    public static Polynomial subtract(Polynomial p1, Polynomial p2) {
        int minDegree = Math.min(p1.getDegree(), p2.getDegree());
        int maxDegree = Math.max(p1.getDegree(), p2.getDegree());
        List<Integer> coefficients = new ArrayList<>(maxDegree + 1);


        for (int i = 0; i <= minDegree; i++) {
            coefficients.add(p1.getCoefficients().get(i) - p2.getCoefficients().get(i));
        }

        addRemainingCoefficients(p1, p2, minDegree, maxDegree, coefficients);

        int i = coefficients.size() - 1;
        while (coefficients.get(i) == 0 && i > 0) {
            coefficients.remove(i);
            i--;
        }

        return new Polynomial(coefficients);
    }
}
