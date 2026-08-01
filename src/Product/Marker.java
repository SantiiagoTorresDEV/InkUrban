package Product;

public class Marker extends Product1 {
    // Color del marcador.
    private String color;
    // Tamano o punta del marcador.
    private String size;

    // Crea un marcador con los datos generales de producto mas color y tamano.
    @SuppressWarnings("this-escape")
    public Marker(String idProduct, String nameProduct, String price, String stock, String color, String size) {
        super(idProduct, nameProduct, price, stock);
        setColor(color);
        setSize(size);
    }

    // Valida y guarda el color del marcador.
    public void setColor(String color) {
        if (color == null || color.trim().isEmpty()) {
            throw new IllegalArgumentException("Este espacio no puede estar vacio");
        }
        this.color = color.trim();
    }

    // Valida y guarda el tamano del marcador.
    public void setSize(String size) {
        if (size == null || size.trim().isEmpty()) {
            throw new IllegalArgumentException("Este espacio no puede estar vacio");
        }
        this.size = size.trim();
    }

    // Agrega color y tamano al texto base del producto.
    @Override
    public String toString() {
        return super.toString() + String.format(" | Color: %s | Tamano: %s", color, size);
    }

    // Retorna el color del marcador.
    public String getColor() {
        return color;
    }

    // Retorna el tamano del marcador.
    public String getSize() {
        return size;
    }
}
