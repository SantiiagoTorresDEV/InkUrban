package User;

import java.util.ArrayList;
import java.util.List;

import ShoppingCar.ShoppingCar1;

public class UserStore {
    // Clientes registrados durante la ejecucion del programa.
    private final List<Customer> customers = new ArrayList<>();

    // Si el cliente ya existe, inicia sesion; si no existe, lo registra y lo guarda.
    public Customer registerOrGetCustomer(String name, String id, String mail, String password) {
        Customer existing = findCustomerById(id);
        if (existing != null) {
            existing.login(name, password);
            return existing;
        }

        Customer customer = new Customer(name, id, mail, password, new ShoppingCar1());
        customers.add(customer);
        customer.login(name, password);
        return customer;
    }

    // Registra un cliente nuevo y rechaza IDs repetidos.
    public Customer registerCustomer(String name, String id, String mail, String password) {
        if (findCustomerById(id) != null) {
            throw new IllegalArgumentException("Ya existe un cliente con ese id");
        }

        Customer customer = new Customer(name, id, mail, password, new ShoppingCar1());
        customers.add(customer);
        customer.login(name, password);
        return customer;
    }

    // Revisa todos los carritos para saber si un producto esta siendo usado.
    public boolean productIsInAnyCart(Product.Product1 product) {
        for (Customer customer : customers) {
            if (customer.getShoppingCar().containsProduct(product)) {
                return true;
            }
        }
        return false;
    }

    // Busca un cliente por ID; retorna null si no lo encuentra.
    public Customer findCustomerById(String id) {
        if (id == null) {
            return null;
        }

        String cleanId = id.trim();
        for (Customer customer : customers) {
            if (String.valueOf(customer.getId()).equals(cleanId)) {
                return customer;
            }
        }
        return null;
    }

    // Retorna la lista de clientes registrados.
    public List<Customer> getCustomers() {
        return customers;
    }
}
