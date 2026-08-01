package ShoppingCar;

import java.util.ArrayList;
import java.util.List;
import Product.Product1;

public class ShoppingCar1 {
    // Productos agregados por el cliente durante la sesion.
    private List <Product1> products = new ArrayList<>();

    // Agrega un producto al carrito y descuenta una unidad del stock.
    public void addProduct(Product1 product){
        if(product == null){
            throw new IllegalArgumentException("No se puede agregar un producto vacio");
        }
        if(product.getStock() <= 0){
            throw new IllegalArgumentException("Producto sin stock disponible");
        }
        product.updateStock(String.valueOf(product.getStock() - 1));
        products.add(product);
        System.out.println("Producto agregado al carrito: " + product.getNameProduct());
    }

    // Elimina un producto del carrito y devuelve una unidad al stock.
    public void deleteProduct(Product1 product){
        if(product == null){
            throw new IllegalArgumentException("No se puede eliminar un producto vacio");
        }
        if(!products.remove(product)){
            throw new IllegalArgumentException("El producto no esta en el carrito");
        }
        product.updateStock(String.valueOf(product.getStock() + 1));
        System.out.println("Producto eliminado del carrito: " + product.getNameProduct());
    }

    // Vacia el carrito sin modificar stock; se usa despues de una compra confirmada.
    public void clearShoppingCar(){
        products.clear();
        System.out.println("Carrito vacio");

    }

    // Devuelve al inventario los productos pendientes y despues vacia el carrito.
    public void restoreStockAndClear(){
        for(Product1 product : products){
            product.updateStock(String.valueOf(product.getStock() + 1));
        }
        clearShoppingCar();
    }

    // Indica si el carrito contiene un producto especifico.
    public boolean containsProduct(Product1 product){
        return products.contains(product);
    }

    // Suma los precios de todos los productos del carrito.
    public double calculateTotal(){
        double total = 0;
        for(Product1 product : products){
        total += product.getPrice();
    }
        return total;
}

    // Retorna la lista interna de productos del carrito.
    public List <Product1> getProducts(){
        return products;
    }
    
}
