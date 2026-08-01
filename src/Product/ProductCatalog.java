package Product;

import java.util.ArrayList;
import java.util.List;

public class ProductCatalog {
    // Crea el catalogo inicial que aparece al abrir la aplicacion.
    public static List<Product1> createDefaultProducts() {
        List<Product1> products = new ArrayList<>();
        // Arreglos base usados para generar varios productos sin repetir manualmente cada linea.
        String[] colors = {"Negro", "Morado", "Azul", "Rojo", "Amarillo", "Naranja", "Plateado", "Blanco"};
        String[] spraySizes = {"Pequeno", "Mediano"};
        String[] markerSizes = {"Grande", "Mediano", "Pequeno"};

        // Primera linea de sprays BullDog, con IDs desde 1001.
        for (int i = 0; i < colors.length; i++) {
            String color = colors[i];
            products.add(new Spray(
                String.valueOf(1001 + i),
                "Spray BullDog " + color,
                "30000",
                "20",
                color,
                spraySizes[i % spraySizes.length]
            ));
        }

        // Segunda linea de sprays NewYork, con IDs desde 2001.
        for (int i = 0; i < colors.length; i++) {
            String color = colors[i];
            products.add(new Spray(
                String.valueOf(2001 + i),
                "Spray NewYork " + color,
                "35000",
                "20",
                color,
                spraySizes[i % spraySizes.length]
            ));
        }

        // Linea de marcadores FlipFlop, con IDs desde 3001.
        for (int i = 0; i < colors.length; i++) {
            String color = colors[i];
            products.add(new Marker(
                String.valueOf(3001 + i),
                "Marcador FlipFlop " + color,
                "20000",
                "30",
                color,
                markerSizes[i % markerSizes.length]
            ));
        }

        // Tapas de spray agregadas de forma individual.
        products.add(new SprayCap("4001", "Tapa USA", "5000", "50", "USA"));
        products.add(new SprayCap("4002", "Tapa Boca Abierta", "7000", "50", "Boca Abierta"));
        products.add(new SprayCap("4003", "Tapa Difuminadora", "6000", "50", "Difuminadora"));

        return products;
    }
}
