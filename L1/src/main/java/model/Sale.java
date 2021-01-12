package model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.apache.commons.math3.util.Precision;

import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Sale implements Runnable{
    private Bill bill;
    private String name;
    private Mutex lock;
    private Double price;
    private List<Product> productsToSell;

    public Sale(String _name, Bill _bill, Mutex _mutex, List<Product> _prods){
        name = _name;
        bill = _bill;
        lock = _mutex;
        price = 0.0d;
        productsToSell = _prods;
    }

    public Double getPrice(){
        return price;
    }

    @Override
    public void run() {
        int rangeMinQuantity = 1;
        int rangeMaxQuantity = 50;

        for (Product product : productsToSell) {
            Random r = new Random();
            Integer randomQuantity = r.nextInt(rangeMaxQuantity - rangeMinQuantity + 1) + rangeMinQuantity;

            lock.lock();

            try {
                bill.remove(product, randomQuantity);
                this.price += Precision.round(product.getPrice() * randomQuantity, 2);
                System.out.println("Took from " + product.getName() + " " + randomQuantity + " pieces");
            }catch (RuntimeException runtimeException){
                System.out.println(runtimeException.getMessage());
            }

            lock.unlock();
        }
    }
}
