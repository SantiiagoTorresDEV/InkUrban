package Shipping;

import Order.Order1;

public class Shipping1 {
    // Identificador del envio asociado a una compra.
    private int idShipping;
    // Direccion de entrega escrita por el cliente.
    private String shippingAddress;

// Constructor: valida y guarda los datos del envio.
@SuppressWarnings("this-escape")
public Shipping1(String idShipping, String shippingAddress){
    setIdShipping(idShipping);
    setShippingAddress(shippingAddress);
}

// Valida que el ID del envio sea numerico y positivo.
public void setIdShipping(String idShipping){
    if(idShipping == null || idShipping.trim().isEmpty()){
        throw new IllegalArgumentException("El id del envio no puede estar vacio");
    }
    String cleanId = idShipping.trim();
    if(cleanId.startsWith("-")){
        throw new IllegalArgumentException("No se permiten numeros negativos");
    }
    if(!cleanId.matches("\\d+")){
        throw new IllegalArgumentException("El id del envio solo puede contener numeros");
    }
    try {
        this.idShipping=Integer.parseInt(cleanId);
    } catch (NumberFormatException e) {
        throw new IllegalArgumentException("El id del envio es demasiado grande");
    }
}

// Valida y guarda la direccion de entrega.
public void setShippingAddress(String shippingAddress){
    validateShippingAddress(shippingAddress);
    this.shippingAddress=shippingAddress.trim();
}

// Regla reutilizable para validar direcciones desde consola o interfaz grafica.
public static void validateShippingAddress(String shippingAddress){
    if(shippingAddress == null || shippingAddress.trim().isEmpty()){
        throw new IllegalArgumentException("La direccion de entrega no puede estar vacia");
    }
    if(shippingAddress.trim().length() < 6){
        throw new IllegalArgumentException("La direccion de entrega debe ser mas especifica");
    }
}

// Retorna el ID numerico del envio.
public int getIdShipping(){
    return idShipping;
}

// Retorna la direccion de entrega.
public String getShippingAddress(){
    return shippingAddress;
}

// Muestra en consola el estado del envio de una orden.
public void shipOrder(Order1 order){
    System.out.println("Orden enviada. " + order.toString() +
                       " | Id de envio: " + idShipping +
                       " | Direccion de entrega: " + shippingAddress);
}
}
