package model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;
import java.util.stream.Collectors;

@AllArgsConstructor
@Data
public class Bill {
    private Map<Product, Integer> products;

    public Bill(){
        products = new HashMap<>();
    }

    public List<Product> getProducts(){
        return new ArrayList<>(products.keySet());
    }

    public Integer getQuantity(Product product){
        return products.get(product);
    }

    public void add(Product product, Integer quantity){
        if(products.containsKey(product)){
            products.replace(product, products.get(product) + quantity);
        }
        else{
            products.put(product, quantity);
        }
    }

    public void remove(Product product, Integer quantity){
        if(products.containsKey(product)){
            if(products.get(product) < quantity){
                throw new RuntimeException("Insufficient stock of " + product.getName() );
            }
            products.replace(product, products.get(product) - quantity);
            if(products.get(product) == 0){
                products.remove(product);
            }
        }
        else{
            throw new RuntimeException("Product already sold!");
        }
    }

}
