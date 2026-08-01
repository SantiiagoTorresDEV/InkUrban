package Product;

public class Product1 {
    // Identificador unico del producto dentro del catalogo.
    private int idProduct;
    // Nombre comercial que se muestra al usuario.
    private String nameProduct;
    // Precio unitario del producto.
    private double price;
    // Cantidad disponible en inventario.
    private int stock;

    // Constructor base: recibe datos como texto y delega la validacion a los setters.
    @SuppressWarnings("this-escape")
    public Product1(String idProduct, String nameProduct, String price, String stock) {
        setIdProduct(idProduct);
        setNameProduct(nameProduct);
        setPrice(price);
        setStock(stock);
    }

    // Valida y guarda el ID del producto, aceptando solo numeros positivos.
    public void setIdProduct(String idProduct) {
        if (idProduct == null || idProduct.trim().isEmpty()) {
            throw new IllegalArgumentException("El id del producto no puede estar vacio");
        }
        String cleanId = idProduct.trim();
        if (cleanId.startsWith("-")) {
            throw new IllegalArgumentException("No se permiten numeros negativos");
        }
        if (!cleanId.matches("\\d+")) {
            throw new IllegalArgumentException("El id del producto solo debe contener numeros");
        }
        try {
            this.idProduct = Integer.parseInt(cleanId);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("El id del producto es demasiado grande");
        }
    }

    // Valida y guarda el nombre del producto sin espacios extra.
    public void setNameProduct(String nameProduct) {
        if (nameProduct == null || nameProduct.trim().isEmpty()) {
            throw new IllegalArgumentException("Este espacio no puede estar vacio");
        }
        this.nameProduct = nameProduct.trim();
    }

    // Valida y guarda el precio; permite enteros o decimales positivos.
    public void setPrice(String price) {
        if (price == null || price.trim().isEmpty()) {
            throw new IllegalArgumentException("El precio no puede estar vacio");
        }
        String cleanPrice = price.trim();
        if (cleanPrice.startsWith("-")) {
            throw new IllegalArgumentException("No se permiten numeros negativos");
        }
        if (!cleanPrice.matches("\\d+(\\.\\d+)?")) {
            throw new IllegalArgumentException("El precio solo puede contener numeros");
        }
        this.price = Double.parseDouble(cleanPrice);
    }

    // Valida y guarda el stock como numero entero positivo o cero.
    public void setStock(String stock) {
        if (stock == null || stock.trim().isEmpty()) {
            throw new IllegalArgumentException("El stock no puede estar vacio");
        }
        String cleanStock = stock.trim();
        if (cleanStock.startsWith("-")) {
            throw new IllegalArgumentException("No se permiten numeros negativos");
        }
        if (!cleanStock.matches("\\d+")) {
            throw new IllegalArgumentException("El stock solo puede contener numeros");
        }
        try {
            this.stock = Integer.parseInt(cleanStock);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("El stock es demasiado grande");
        }
    }

    // Representacion legible usada en consola, tablas y carrito.
    @Override
    public String toString() {
        return String.format("#%d | %s | $%.2f | Stock: %d", idProduct, nameProduct, price, stock);
    }

    // Actualiza el stock reutilizando las mismas reglas de validacion.
    public void updateStock(String newStock) {
        setStock(newStock);
    }

    // Retorna el precio unitario.
    public double getPrice() {
        return price;
    }

    // Retorna el nombre del producto.
    public String getNameProduct() {
        return nameProduct;
    }

    // Retorna el ID numerico del producto.
    public int getIdProduct() {
        return idProduct;
    }

    // Retorna la cantidad disponible en inventario.
    public int getStock() {
        return stock;
    }
}
