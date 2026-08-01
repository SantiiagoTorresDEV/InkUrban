package User;

import Product.Product1;

public class Admin extends User1{

    // Crea un administrador usando los datos definidos por la clase base User1.
    public Admin (String name, String id, String mail, String password){
        super(name, id, mail, password);
    }

// Actualiza el stock de un producto desde las herramientas del administrador.
public void controllStock(Product1 product, String newStock){
    product.updateStock(newStock);
    System.out.println("Stock actualizado correctamente | Stock: "+ newStock);

}

// Mensaje de confirmacion cuando se agrega un producto al catalogo.
public void addProduct(Product1 product){
    System.out.println("Producto agregado: "+ product.getNameProduct());

}

// Mensaje de confirmacion cuando se elimina un producto del catalogo.
public void deleteProduct(Product1 product){
    System.out.println("Producto eliminado "+ product.getNameProduct());
}
}
