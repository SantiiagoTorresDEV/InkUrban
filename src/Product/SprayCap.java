package Product;

public class SprayCap extends Product1 {
    // Tipo de tapa, por ejemplo USA, difuminadora o boca abierta.
    private String type;

    // Crea una tapa de spray con datos generales de producto y su tipo.
    @SuppressWarnings("this-escape")
    public SprayCap(String idProduct, String nameProduct, String price, String stock, String type) {
        super(idProduct, nameProduct, price, stock);
        setType(type);
    }

    // Valida y guarda el tipo de tapa.
    public void setType(String type) {
        if (type == null || type.trim().isEmpty()) {
            throw new IllegalArgumentException("Este espacio no puede estar vacio");
        }
        this.type = type.trim();
    }

    // Agrega el tipo de tapa al texto base del producto.
    @Override
    public String toString() {
        return super.toString() + String.format(" | Tapa: %s", type);
    }

    // Retorna el tipo de tapa.
    public String getType() {
        return type;
    }
}
