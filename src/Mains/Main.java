package Mains;

import java.util.List;
import java.util.Scanner;

import javax.swing.SwingUtilities;

import Interfaces.MainWindow;
import Product.Product1;
import Product.ProductCatalog;
import User.Admin;
import User.Customer;
import User.UserStore;

public class Main {
    // Punto de entrada del programa. Decide si se abre la interfaz grafica o el modo consola.
    public static void main(String[] args) {
        // Si se ejecuta con --console, se usa el menu por terminal en vez de Swing.
        if (args.length > 0 && args[0].equalsIgnoreCase("--console")) {
            startConsole();
            return;
        }

        // Datos iniciales compartidos por las ventanas: catalogo y clientes registrados.
        List<Product1> products = ProductCatalog.createDefaultProducts();
        UserStore userStore = new UserStore();
        SwingUtilities.invokeLater(() -> new MainWindow(products, userStore).setVisible(true));
    }

    // Menu principal por consola: permite entrar como administrador, cliente o salir.
    private static void startConsole() {
        Scanner ky = new Scanner(System.in);
        List<Product1> products = ProductCatalog.createDefaultProducts();
        UserStore userStore = new UserStore();
        int option = 0;

        do {
            // Se imprime el menu principal cada vez que el usuario vuelve a esta pantalla.
            System.out.println("========================================");
            System.out.println("               INKURBAN");
            System.out.println("            Arte callejero");
            System.out.println("----------------------------------------");
            System.out.println("1. Soy Administrador");
            System.out.println("2. Soy Cliente");
            System.out.println("3. Salir");
            System.out.println("========================================");

            try {
                option = Integer.parseInt(ky.nextLine());

                switch (option) {
                    case 1:
                        // Login fijo del administrador y entrada al menu administrativo.
                        System.out.println("Ingrese su usuario: ");
                        String adminName = ky.nextLine();
                        System.out.println("Ingrese su contrasena: ");
                        String adminPassword = ky.nextLine();
                        Admin admin = new Admin("Administrador", "5284628", "administrador5@inkurban.com", "admin123");
                        admin.login(adminName, adminPassword);
                        MainAdmin mainAdmin = new MainAdmin(admin, products, userStore, ky);
                        mainAdmin.showMenu();
                        break;
                    case 2:
                        // Registro o recuperacion del cliente, manteniendo su carrito si ya existia.
                        System.out.println("Ingrese su name: ");
                        String name = ky.nextLine();
                        System.out.println("Ingrese su id: ");
                        String id = ky.nextLine();
                        System.out.println("Ingrese su correo: ");
                        String mail = ky.nextLine();
                        System.out.println("Ingrese su contrasena: ");
                        String password = ky.nextLine();
                        Customer customer = userStore.registerOrGetCustomer(name, id, mail, password);
                        MainCustomer mainCustomer = new MainCustomer(customer, customer.getShoppingCar(), products, ky);
                        mainCustomer.showMenu();
                        break;
                    case 3:
                        System.out.println("Hasta la proxima!");
                        break;
                    default:
                        // Cualquier numero fuera del rango esperado se considera invalido.
                        System.out.println("Opcion invalida");
                        break;
                }
            } catch (NumberFormatException e) {
                // Captura entradas que no son numeros cuando el menu espera una opcion numerica.
                System.out.println("Por favor ingrese un numero");
                option = 0;
            } catch (IllegalArgumentException e) {
                // Muestra mensajes de validacion enviados por las clases del modelo.
                System.out.println("Error: " + e.getMessage());
            }
        } while (option != 3);

        ky.close();
    }
}
