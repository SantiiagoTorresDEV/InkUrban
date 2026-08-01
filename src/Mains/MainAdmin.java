package Mains;

import java.util.List;
import java.util.Scanner;

import Product.Marker;
import Product.Product1;
import Product.Spray;
import Product.SprayCap;
import User.Admin;
import User.UserStore;

public class MainAdmin {
    // Scanner reutilizado para leer todas las entradas del administrador por consola.
    private Scanner ky;
    // Administrador autenticado que ejecuta las acciones.
    private Admin admin;
    // Lista compartida de productos disponibles en la tienda.
    private List<Product1> products;
    // Guarda clientes y permite saber si un producto esta en algun carrito.
    private UserStore userStore;

    // Constructor simple: crea un UserStore y Scanner propios.
    public MainAdmin(Admin admin, List<Product1> products) {
        this(admin, products, new UserStore(), new Scanner(System.in));
    }

    // Constructor que permite reutilizar el Scanner del menu principal.
    public MainAdmin(Admin admin, List<Product1> products, Scanner ky) {
        this(admin, products, new UserStore(), ky);
    }

    // Constructor completo usado cuando se comparte estado entre admin, clientes y consola.
    public MainAdmin(Admin admin, List<Product1> products, UserStore userStore, Scanner ky) {
        this.admin = admin;
        this.products = products;
        this.userStore = userStore;
        this.ky = ky;
    }

    // Muestra el menu administrativo hasta que el usuario decide cerrar sesion.
    public void showMenu() {
        int option = 0;
        do {
            System.out.println("Que desea hacer?");
            System.out.println("1. Registrar producto");
            System.out.println("2. Eliminar producto");
            System.out.println("3. Controlar stock");
            System.out.println("4. Cerrar sesion");
            try {
                option = Integer.parseInt(ky.nextLine());
                switch (option) {
                    case 1:
                        // Registra un producto nuevo segun el tipo elegido.
                        addProducts();
                        break;
                    case 2:
                        // Elimina un producto existente si no esta en carritos activos.
                        deleteProducts();
                        break;
                    case 3:
                        // Cambia el stock de un producto ya registrado.
                        controllStocks();
                        break;
                    case 4:
                        // Finaliza la sesion del administrador.
                        admin.logout();
                        break;
                    default:
                        System.out.println("Opcion invalida");
                        break;
                }
            } catch (NumberFormatException e) {
                // Evita que el programa se cierre si se escribe texto en vez de numero.
                System.out.println("Por favor ingrese un numero");
                option = 0;
            }
        } while (option != 4);
    }

    // Solicita los datos del producto, crea el objeto correcto y lo agrega al catalogo.
    private void addProducts() {
        try {
            System.out.println("Que tipo de producto desea registrar?");
            System.out.println("1. Spray");
            System.out.println("2. Marcador");
            System.out.println("3. Tapa de spray");
            int op = Integer.parseInt(ky.nextLine());

            System.out.println("Ingrese el id del producto");
            String idProduct = ky.nextLine();
            System.out.println("Ingrese el nombre del producto");
            String nameProduct = ky.nextLine();
            System.out.println("Ingrese el precio del producto");
            String price = ky.nextLine();
            System.out.println("Ingrese el stock del producto");
            String stock = ky.nextLine();

            Product1 product = null;

            switch (op) {
                case 1:
                    // Los sprays requieren color y tamano.
                    System.out.println("Ingrese el color: ");
                    String colorSpray = ky.nextLine();
                    System.out.println("Ingrese el tamano: ");
                    String sizeSpray = ky.nextLine();
                    product = new Spray(idProduct, nameProduct, price, stock, colorSpray, sizeSpray);
                    break;
                case 2:
                    // Los marcadores tambien requieren color y tamano.
                    System.out.println("Ingrese el color: ");
                    String colorMarker = ky.nextLine();
                    System.out.println("Ingrese el tamano: ");
                    String sizeMarker = ky.nextLine();
                    product = new Marker(idProduct, nameProduct, price, stock, colorMarker, sizeMarker);
                    break;
                case 3:
                    // Las tapas solo necesitan indicar su tipo.
                    System.out.println("Ingrese el tipo de tapa");
                    String type = ky.nextLine();
                    product = new SprayCap(idProduct, nameProduct, price, stock, type);
                    break;
                default:
                    System.out.println("Opcion invalida");
                    break;
            }

            if (product != null) {
                // Antes de agregar, se valida que no exista otro producto con el mismo ID.
                if (productIdExists(product.getIdProduct())) {
                    throw new IllegalArgumentException("Ya existe un producto con ese id");
                }
                products.add(product);
                admin.addProduct(product);
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // Busca un producto por ID y lo elimina si no esta en ningun carrito.
    private void deleteProducts() {
        try {
            System.out.println("Ingrese el id del producto que va a eliminar");
            String idProduct = ky.nextLine();
            Product1 product = searchProduct(idProduct);
            if (product != null) {
                // Protege los carritos de clientes para no borrar productos que ya agregaron.
                if (userStore.productIsInAnyCart(product)) {
                    throw new IllegalArgumentException("No se puede eliminar un producto que esta en un carrito");
                }
                products.remove(product);
                admin.deleteProduct(product);
            } else {
                System.out.println("Producto no encontrado");
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // Actualiza el stock de un producto encontrado por ID.
    private void controllStocks() {
        try {
            System.out.println("Ingrese el id del producto");
            String idProduct = ky.nextLine();
            Product1 product = searchProduct(idProduct);
            if (product != null) {
                System.out.println("Ingrese el nuevo stock");
                String newStock = ky.nextLine();
                admin.controllStock(product, newStock);
            } else {
                System.out.println("Producto no encontrado");
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // Recorre la lista de productos y retorna el que coincide con el ID recibido.
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

    // Indica si ya existe un producto con el mismo ID numerico.
    private boolean productIdExists(int idProduct) {
        for (Product1 product : products) {
            if (product.getIdProduct() == idProduct) {
                return true;
            }
        }
        return false;
    }
}
