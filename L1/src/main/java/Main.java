import model.Bill;
import model.Mutex;
import model.Product;
import model.Sale;
import org.apache.commons.math3.util.Precision;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main {
    private final static List<Sale> sales = new ArrayList<>();
    private final static List<Product> products = new ArrayList<>();
    private final static Bill stock = new Bill();
    private final static Mutex mutex = new Mutex();
    public static  double initialStockPrice = 0.0d;
    public static  double finalStockPrice = 0.0d;
    public static double salesPrice = 0.0d;
    public static int nrOfProducts = 1000;
    public static final int NR_THREADS = 1;

    public static void generateProducts(){
        double rangeMinPrice = 0.0d;
        double rangeMaxPrice = 99.9d;
        int rangeMinQuantity = 50;
        int rangeMaxQuantity = 100;

        for(int i=0; i<nrOfProducts; i++){
            String productName = "Product" + i;
            Random r = new Random();
            Double randomProductPrice = Precision.round(rangeMinPrice + (rangeMaxPrice - rangeMinPrice) * r.nextDouble(), 2);
            Integer randomQuantity = r.nextInt(rangeMaxQuantity - rangeMinQuantity + 1) + rangeMinQuantity;
            Integer randomQuantity2 = r.nextInt(rangeMaxQuantity - rangeMinQuantity + 1) + rangeMinQuantity;
            Product product = new Product(productName, randomProductPrice);

            stock.add(product, randomQuantity);
            stock.add(product, randomQuantity2);
            products.add(product);
        }
    }

    public static double checkConsistency(){
        double stockPrice = 0;
        for(Product product: stock.getProducts()){
            stockPrice += product.getPrice() * stock.getQuantity(product);
        }
        stockPrice = Precision.round(stockPrice, 2);
        return stockPrice;
    }

    public static void main(String[] args) {
        double startTime =  System.nanoTime() / 1000000;

        generateProducts();
        initialStockPrice = checkConsistency();

        int step = nrOfProducts / NR_THREADS;
        for(int i=0; i<NR_THREADS; i++){
            String saleName = "Sale"+i;
            List<Product> productsToSell = new ArrayList<>();
            for(int j=0;j<step;j++){
                productsToSell.add(products.get(j));
            }
            Sale sale = new Sale(saleName, stock, mutex, productsToSell);
            sales.add(sale);
        }

        List<Thread> threads = new ArrayList<>();

        sales.forEach(sale -> threads.add(new Thread(sale)));

        for (Thread thread : threads){
            thread.start();
        }

        for (Thread thread : threads){
            try {
                thread.join();
            } catch (InterruptedException e) {
                System.out.println(e.getMessage());
            }
        }

        finalStockPrice = checkConsistency();

        for(Sale sale: sales){
            salesPrice += sale.getPrice();
        }
        salesPrice = Precision.round(salesPrice, 2);

        if(initialStockPrice - finalStockPrice != salesPrice){
            System.out.println("Stock verification failed!");
        }
        else{
            System.out.println("Stock verification successful!");
        }

        double endTime = System.nanoTime() / 1000000;

        System.out.println("\n Time passed: " + (endTime - startTime) / 1000 + " seconds");
    }
}
