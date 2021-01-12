import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Main {
    private static final List<Integer> v1 = new ArrayList<>();
    private static final List<Integer> v2 = new ArrayList<>();

    public static void generateRandomNumbers(){
        int minNr = 0;
        int maxNr = 100;
        Random r = new Random();
        int randomInt1, randomInt2;
        for(int i = 0; i < 100 ; i++){
            randomInt1 = r.nextInt(maxNr - minNr + 1) + minNr;
            randomInt2 = r.nextInt(maxNr - minNr + 1) + minNr;
            v1.add(randomInt1);
            v2.add(randomInt2);
        }
    }


    public static void main(String[] args) throws InterruptedException {
        generateRandomNumbers();

        Operation operation = new Operation(Arrays.asList(1,2,3), Arrays.asList(0,1,2));
        //Operation operation = new Operation(v1, v2);

        Thread producer = new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    operation.produce();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        Thread consumer = new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    operation.consume();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        producer.start();
        consumer.start();

        producer.join();
        consumer.join();

        System.out.println("The scalar product is: "+ operation.getScalarProd());

    }
}
