package User;

import ShoppingCar.ShoppingCar1;

public class Customer extends User1 {
    // Carrito propio del cliente; se conserva mientras el cliente exista en UserStore.
    private ShoppingCar1 shoppingCar;

    // Crea un cliente con sus datos personales y un carrito asociado.
    public Customer(String name, String id, String mail, String password, ShoppingCar1 shoppingCar) {
        super(name, id, mail, password);
        this.shoppingCar = shoppingCar;
    }

    // Compra rapida usada por la logica del modelo: muestra total y limpia el carrito.
    public void buy() {
        System.out.println("Total a pagar: " + shoppingCar.calculateTotal());
        shoppingCar.clearShoppingCar();
    }

    // Retorna el carrito del cliente.
    public ShoppingCar1 getShoppingCar() {
        return shoppingCar;
    }
}
