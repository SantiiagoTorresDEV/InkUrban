package Order;

import ShoppingCar.ShoppingCar1;

public class Order1 {
    // Identificador de la orden generada durante la compra.
    private int idOrder;
    // Fecha en la que se crea la orden.
    private String date;

// Constructor: valida y guarda el ID y la fecha de la orden.
@SuppressWarnings("this-escape")
public Order1(String idOrder, String date){
    setIdOrder(idOrder);
    setDate(date);

}

// Valida que el ID de la orden sea numerico y positivo.
public void setIdOrder(String idOrder){
    if(idOrder == null || idOrder.trim().isEmpty()){
        throw new IllegalArgumentException("El id de la orden no puede estar vacio");
    }
    String cleanId = idOrder.trim();
    if(cleanId.startsWith("-")){
        throw new IllegalArgumentException("No se permiten numeros negativos");
    }
    if(!cleanId.matches("\\d+")){
        throw new IllegalArgumentException("El id de la orden solo puede contener numeros");
    }
    try {
        this.idOrder = Integer.parseInt(cleanId);
    } catch (NumberFormatException e) {
        throw new IllegalArgumentException("El id de la orden es demasiado grande");
    }
}

// Valida y guarda la fecha de la orden.
public void setDate(String date){
    if(date == null || date.trim().isEmpty()){
    throw new IllegalArgumentException("La fecha no puede estar vacia");
    }
    this.date = date.trim();
}

// Confirma el pago mostrando el valor final del carrito.
public void confirmPay(ShoppingCar1 shoppingCar){
    System.out.println("Pago realizado con exito "+
                        " Valor de compra es: "+ shoppingCar.calculateTotal());
}

// Mensaje usado para indicar que el flujo pasa a la etapa de pago.
public void goToPay(){
    System.out.println("Redirigiendo al pago");
}

// Retorna el total a pagar consultando el carrito recibido.
public double totalToPay(ShoppingCar1 shoppingCar){
    return shoppingCar.calculateTotal();
}

// Texto resumido de la orden para consola, envio y factura.
@Override
public String toString(){
    return String.format("Orden #%d | Fecha: %s", idOrder, date);
}

}


