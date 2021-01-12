import java.util.List;

public class Operation {
    private final List<Integer> v1;
    private final List<Integer> v2;
    private int scalarProd;
    private boolean available;
    private int pos;
    private int prod;

    public Operation(List<Integer> _v1, List<Integer> _v2) {
        v1 = _v1;
        v2 = _v2;
        scalarProd = 0;
        available = false;
        pos = 0;
    }

    public int getScalarProd() {
        return scalarProd;
    }

    public void produce() throws InterruptedException{
        while (pos < v1.size()){
            synchronized (this){
                while (available){
                    wait();
                }

                prod = v1.get(pos) * v2.get(pos);

                System.out.println("Product#"+pos+" is: "+prod);

                pos++;

                available = true;

                notify();
           }
        }
    }

    public void consume() throws InterruptedException{
        while (pos < v1.size()){
            synchronized (this){
                while (!available){
                    wait();
                }

                scalarProd += prod;

                System.out.println("Consumer#"+ (pos-1) + " is: " + prod);
                System.out.println("Consumed so far: "+scalarProd);

                available = false;

                notify();
            }
        }
    }

}
