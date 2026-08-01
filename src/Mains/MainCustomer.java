package Mains;

import java.util.List;
import java.util.Scanner;

import Order.Order1;
import Payment.Payment1;
import Product.Product1;
import Shipping.Shipping1;
import ShoppingCar.ShoppingCar1;
import User.Customer;

public class MainCustomer {
    // Scanner usado para leer las opciones y datos del cliente en consola.
    private Scanner ky;
    // Cliente que inicio sesion.
    private Customer customer;
    // Carrito asociado al cliente actual.
    private ShoppingCar1 shoppingCar;
    // Catalogo compartido de productos disponibles.
    private List<Product1> products;

    // Constructor simple: crea un Scanner propio.
    public MainCustomer(Customer customer, ShoppingCar1 shoppingCar, List<Product1> products) {
        this(customer, shoppingCar, products, new Scanner(System.in));
    }

    // Constructor usado cuando se quiere compartir el Scanner del menu principal.
    public MainCustomer(Customer customer, ShoppingCar1 shoppingCar, List<Product1> products, Scanner ky) {
        this.customer = customer;
        this.shoppingCar = shoppingCar;
        this.products = products;
        this.ky = ky;
    }

    // Muestra el menu del cliente hasta que decide cerrar sesion.
    public void showMenu() {
        int option = 0;
        do {
            System.out.println("Que desea hacer?");
            System.out.println("1. Ver productos");
            System.out.println("2. Agregar producto al carrito");
            System.out.println("3. Ver carrito");
            System.out.println("4. Realizar compra");
            System.out.println("5. Cerrar sesion");

            try {
                option = Integer.parseInt(ky.nextLine());
                switch (option) {
                    case 1:
                        // Lista todos los productos disponibles.
                        showProducts();
                        break;
                    case 2:
                        // Permite escoger un producto y agregarlo al carrito.
                        addToCart();
                        break;
                    case 3:
                        // Muestra productos agregados y total.
                        showCart();
                        break;
                    case 4:
                        // Inicia el flujo de compra, pago y envio.
                        buy();
                        break;
                    case 5:
                        // Cierra sesion y restaura stock si quedaron productos en carrito.
                        closeSession();
                        break;
                    default:
                        System.out.println("Opcion invalida");
                }
            } catch (NumberFormatException e) {
                // Controla entradas no numericas en el menu.
                System.out.println("Por favor ingrese un numero");
                option = 0;
            }
        } while (option != 5);
    }

    // Imprime el catalogo actual en consola.
    private void showProducts() {
        if (products.isEmpty()) {
            System.out.println("No hay productos disponibles");
        } else {
            System.out.println("---- PRODUCTOS DISPONIBLES ----");
            for (Product1 product : products) {
                System.out.println(product.toString());
            }
        }
    }

    // Agrega al carrito el producto cuyo ID escriba el cliente.
    public void addToCart() {
        if (products.isEmpty()) {
            System.out.println("No hay productos disponibles");
            return;
        }

        try {
            showProducts();
            System.out.println("Ingrese el id del producto que desea agregar");
            String idProduct = ky.nextLine();
            Product1 product = searchProduct(idProduct);
            if (product != null) {
                shoppingCar.addProduct(product);
            } else {
                System.out.println("Producto no encontrado");
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // Muestra el contenido del carrito y el total a pagar.
    private void showCart() {
        if (shoppingCar.getProducts().isEmpty()) {
            System.out.println("El carrito de compras esta vacio");
            return;
        }

        System.out.println("---- CARRITO ----");
        for (Product1 product : shoppingCar.getProducts()) {
            System.out.println(product.toString());
        }
        System.out.println("Total: " + shoppingCar.calculateTotal());
    }

    // Ejecuta la compra: crea orden, valida pago, crea envio, factura y limpia carrito.
    private void buy() {
        if (shoppingCar.getProducts().isEmpty()) {
            System.out.println("El carrito de compras esta vacio");
            return;
        }

        try {
            // IDs simples generados para la orden y objetos relacionados.
            String idOrder = String.valueOf((int) (Math.random() * 100000));
            String date = java.time.LocalDate.now().toString();
            Order1 order = new Order1(idOrder, date);
            order.goToPay();

            String paymentMethod = selectPaymentMethod();
            Payment1 payment = new Payment1(generateId(), paymentMethod);
            validatePaymentMethodData(paymentMethod);

            // La direccion se usa para construir el envio.
            System.out.println("Ingrese la direccion de entrega");
            String address = ky.nextLine();
            Shipping1 shipping = new Shipping1(generateId(), address);

            payment.processingPay();
            payment.validatePay();
            shipping.shipOrder(order);

            payment.generateInvoice(order, shoppingCar, shipping);
            order.confirmPay(shoppingCar);
            shoppingCar.clearShoppingCar();
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // Cierra la sesion del cliente y devuelve stock si el carrito no fue comprado.
    private void closeSession() {
        if (!shoppingCar.getProducts().isEmpty()) {
            shoppingCar.restoreStockAndClear();
        }
        customer.logout();
    }

    // Muestra las opciones de pago hasta que el cliente elige una valida.
    private String selectPaymentMethod() {
        String paymentMethod = null;
        do {
            System.out.println("Seleccione el metodo de pago");
            System.out.println("1. Efectivo / Contra-entrega");
            System.out.println("2. Tarjeta de debito");
            System.out.println("3. Tarjeta de credito");
            System.out.println("4. Transferencia");
            try {
                int paymentOption = Integer.parseInt(ky.nextLine());
                switch (paymentOption) {
                    case 1:
                        paymentMethod = "Efectivo";
                        break;
                    case 2:
                        paymentMethod = "Tarjeta de debito";
                        break;
                    case 3:
                        paymentMethod = "Tarjeta de credito";
                        break;
                    case 4:
                        paymentMethod = "Transferencia";
                        break;
                    default:
                        System.out.println("Opcion invalida, intente de nuevo");
                        break;
                }
            } catch (NumberFormatException e) {
                System.out.println("Por favor ingrese un numero");
            }
        } while (paymentMethod == null);
        return paymentMethod;
    }

    // Solicita y valida los datos extra requeridos por tarjeta o transferencia.
    private void validatePaymentMethodData(String paymentMethod) {
        if (paymentMethod.equals("Tarjeta de debito") || paymentMethod.equals("Tarjeta de credito")) {
            System.out.println("Ingrese el nombre del titular");
            String cardHolder = ky.nextLine();
            System.out.println("Ingrese el numero de tarjeta (16 digitos)");
            String cardNumber = ky.nextLine();
            System.out.println("Ingrese la fecha de vencimiento (MM/AA)");
            String expirationDate = ky.nextLine();
            System.out.println("Ingrese el codigo de seguridad");
            String securityCode = ky.nextLine();
            Payment1.validateCardPayment(cardHolder, cardNumber, expirationDate, securityCode);
        } else if (paymentMethod.equals("Transferencia")) {
            System.out.println("Ingrese el banco");
            String bank = ky.nextLine();
            System.out.println("Ingrese el numero de cuenta o telefono");
            String accountOrPhone = ky.nextLine();
            System.out.println("Ingrese la referencia de transferencia");
            String reference = ky.nextLine();
            Payment1.validateTransferPayment(bank, accountOrPhone, reference);
        }
    }

    // Busca en el catalogo el producto cuyo ID coincida con el texto recibido.
    private Product1 searchProduct(String idProduct) {
        if (idProduct == null) {
            return null;
        }
        String cleanId = idProduct.trim();
        for (Product1 product : products) {
            if (String.valueOf(product.getIdProduct()).equals(cleanId)) {
                return product;
            }
        }
        return null;
    }

    // Genera un identificador numerico simple para pagos o envios.
    private String generateId() {
        return String.valueOf((int) (Math.random() * 100000));
    }
}
