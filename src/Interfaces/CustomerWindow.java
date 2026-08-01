package Interfaces;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.time.LocalDate;
import java.util.List;

import javax.swing.*;
import javax.swing.table.*;
import javax.swing.text.*;

import Order.Order1;
import Payment.Payment1;
import Product.*;
import Shipping.Shipping1;
import ShoppingCar.ShoppingCar1;
import User.Customer;

/*
 * Ventana del cliente.
 * Desde aqui el usuario ve informacion de la tienda, explora productos,
 * agrega productos al carrito, realiza la compra y recibe la factura.
 */
public class CustomerWindow extends JFrame {

    private static final long serialVersionUID = 1L;

    // Datos del cliente actual, su carrito, productos disponibles y regreso al menu principal.
    private transient Customer customer;
    private transient ShoppingCar1 shoppingCar;
    private transient List<Product1> products;
    private transient MainWindow mainWindow;

    // Componentes que se actualizan durante la navegacion del cliente.
    private JPanel productsPanel;
    private JPanel cartItemsPanel;
    private JLabel cartSummaryLabel;
    private JLabel cartTotalLabel;
    private JDialog cartDialog;
    private JScrollPane contentScrollPane;

    // Paleta visual de la interfaz de cliente.
    private final Color panelPurple = new Color(70, 43, 101);
    private final Color activePurple = new Color(138, 82, 214);
    private final Color darkPurple = new Color(25, 16, 35);
    private final Color white = new Color(245, 242, 250);
    private final Color pastelPurple = new Color(241, 232, 250);
    private final Color mediumLilac = new Color(181, 139, 232);
    private final Color softViolet = new Color(112, 74, 151);
    private final Color markerBlack = Color.BLACK;

    @SuppressWarnings("this-escape")
    public CustomerWindow(Customer customer, List<Product1> products, MainWindow mainWindow) {
        this.customer = customer;
        this.shoppingCar = customer.getShoppingCar();
        this.products = products;
        this.mainWindow = mainWindow;

        setTitle("InkUrban - Cliente");
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setMinimumSize(new Dimension(980, 620));
        setLayout(new BorderLayout());

        // Fondo principal. En sus zonas norte, centro y sur se agrega toda la interfaz.
        GraffitiPanel background = new GraffitiPanel();
        background.setLayout(new BorderLayout(24, 24));
        background.setBorder(BorderFactory.createEmptyBorder(34, 44, 34, 44));
        add(background, BorderLayout.CENTER);

        background.add(crearEncabezado(), BorderLayout.NORTH);
        background.add(crearContenido(), BorderLayout.CENTER);
        background.add(crearBarraInferior(), BorderLayout.SOUTH);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                cerrarSesion();
            }
        });

        refrescarProductos();
        actualizarResumenCarrito();
        mostrarContenidoDesdeArriba();
    }

    // Encabezado superior: muestra logo, nombre de la tienda, saludo y boton "Mi carrito".
    private JPanel crearEncabezado() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        JPanel brandPanel = new JPanel(new BorderLayout());
        brandPanel.setOpaque(false);

        ImageIcon logoIcon = cargarLogoPequeno();
        if (logoIcon != null) {
            JLabel logoLabel = new JLabel(logoIcon);
            logoLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 14));
            brandPanel.add(logoLabel, BorderLayout.WEST);
        }

        JLabel title = new JLabel("Tienda InkUrban", SwingConstants.LEFT);
        title.setFont(fuenteGraffiti(38));
        title.setForeground(white);
        brandPanel.add(title, BorderLayout.CENTER);
        panel.add(brandPanel, BorderLayout.WEST);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 4));
        actions.setOpaque(false);

        JLabel subtitle = new JLabel("Bienvenido, " + customer.getName());
        subtitle.setFont(new Font("Arial", Font.BOLD | Font.ITALIC, 16));
        subtitle.setForeground(new Color(220, 211, 232));
        actions.add(subtitle);

        JButton cartButton = crearBoton("Mi carrito");
        cartButton.setPreferredSize(new Dimension(150, 38));
        cartButton.addActionListener(event -> mostrarCarrito());
        actions.add(cartButton);

        panel.add(actions, BorderLayout.EAST);
        return panel;
    }

    // Contenido central: informacion de InkUrban, galeria y catalogo de productos.
    private JPanel crearContenido() {
        JPanel panel = crearPanelDecorado();
        panel.setLayout(new BorderLayout(18, 18));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(markerBlack, 3),
            BorderFactory.createEmptyBorder(22, 22, 22, 22)
        ));

        JPanel bar = new JPanel(new BorderLayout());
        bar.setOpaque(false);

        JLabel title = new JLabel("Ruta urbana");
        title.setFont(fuenteGraffiti(30));
        title.setForeground(white);
        bar.add(title, BorderLayout.WEST);

        cartSummaryLabel = new JLabel("", SwingConstants.RIGHT);
        cartSummaryLabel.setFont(new Font("Arial", Font.BOLD, 16));
        cartSummaryLabel.setForeground(new Color(220, 211, 232));
        bar.add(cartSummaryLabel, BorderLayout.EAST);

        panel.add(bar, BorderLayout.NORTH);

        // Este panel va dentro de un JScrollPane para poder desplazarse verticalmente.
        JPanel scrollContent = new JPanel();
        scrollContent.setLayout(new BoxLayout(scrollContent, BoxLayout.Y_AXIS));
        scrollContent.setBackground(new Color(50, 31, 70));
        scrollContent.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        scrollContent.add(crearBloqueTexto(
            "Quienes somos",
            "Somos InkUrban: una crew de artistas que viene rayando historia desde 1990. Nacimos entre persianas cerradas, " +
            "paredes olvidadas y noches largas de boceto, aerosol y barrio. Lo nuestro no empezo en vitrinas: empezo en la calle, " +
            "donde cada linea tenia que ganarse su lugar y cada color tenia que gritar mas fuerte que el cemento. Pintamos porque " +
            "la ciudad tambien tiene pulso, memoria y rabia bonita. Seguimos aqui con la misma hambre de siempre: convertir muros " +
            "grises en piezas con caracter, mezclar tecnica con calle y darle herramientas a quien quiera soltar su propio trazo."
        ));
        scrollContent.add(crearSeparadorVertical(20));
        scrollContent.add(crearGaleriaArtistas());
        scrollContent.add(crearSeparadorVertical(18));
        scrollContent.add(crearBloqueTexto(
            "Historia del graffiti",
            "El graffiti no aparecio de la nada con el aerosol. La idea de dejar una marca sobre una pared viene de muy lejos: " +
            "en ciudades antiguas ya existian nombres, frases, dibujos y mensajes raspados o escritos sobre piedra, yeso y muros. " +
            "Era una forma directa de decir: pase por aqui, esto pienso, esto vi, esta es mi voz. Con el tiempo esa necesidad de marcar " +
            "el espacio siguio viva, pero cambio de herramienta, velocidad y actitud.\n\n" +
            "El graffiti moderno tomo fuerza en Estados Unidos entre finales de los anos 60 y los 70. Filadelfia fue clave con escritores " +
            "como Cornbread y Cool Earl, que comenzaron a repetir sus nombres por la ciudad hasta volverlos presencia. Casi al mismo tiempo, " +
            "Nueva York convirtio el tag en una fiebre visual: nombres como TAKI 183 empezaron a recorrer calles y estaciones, y el metro se " +
            "volvio una gallery en movimiento. Lo que al principio era firma rapida empezo a crecer: letras mas grandes, contornos, rellenos, " +
            "sombras, personajes, bombas, piezas y estilos cada vez mas dificiles de leer para quien no pertenecia a la cultura.\n\n" +
            "En los 70 y 80 el graffiti se cruzo con el hip hop, el break, el rap y el DJing. No era solo pintar: era pertenecer a una escena, " +
            "competir con estilo, ganar nombre, inventar codigos y transformar el ruido de la ciudad en identidad. Muchos lo vieron como vandalismo; " +
            "otros entendieron que alli habia diseno, caligrafia, color, riesgo, memoria y protesta. Con los anos salto de trenes y callejones a " +
            "murales, libros, fotografias, galerias y festivales, pero su raiz sigue siendo la misma: una voz urbana que se niega a quedarse callada."
        ));
        scrollContent.add(crearSeparadorVertical(18));
        // Aqui se insertan dinamicamente las tarjetas de productos disponibles.
        productsPanel = new JPanel(new GridLayout(0, 3, 18, 18));
        productsPanel.setOpaque(false);
        scrollContent.add(crearTituloSeccion("Arsenal para pintar"));
        scrollContent.add(crearSeparadorVertical(12));
        scrollContent.add(productsPanel);

        contentScrollPane = new JScrollPane(scrollContent);
        contentScrollPane.setBorder(BorderFactory.createLineBorder(markerBlack, 2));
        contentScrollPane.getViewport().setBackground(new Color(50, 31, 70));
        contentScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        contentScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        panel.add(contentScrollPane, BorderLayout.CENTER);

        return panel;
    }

    // Al abrir la ventana, deja el scroll en la parte superior del contenido.
    private void mostrarContenidoDesdeArriba() {
        SwingUtilities.invokeLater(() -> {
            if (contentScrollPane != null) {
                contentScrollPane.getVerticalScrollBar().setValue(0);
                contentScrollPane.getViewport().setViewPosition(new Point(0, 0));
            }
        });
    }

    // Separador invisible usado para dar espacio entre secciones.
    private JPanel crearSeparadorVertical(int height) {
        JPanel separador = new JPanel();
        separador.setOpaque(false);
        separador.setMaximumSize(new Dimension(Integer.MAX_VALUE, height));
        separador.setPreferredSize(new Dimension(1, height));
        return separador;
    }

    // Titulo reutilizable para secciones como "Arsenal para pintar".
    private JLabel crearTituloSeccion(String text) {
        JLabel title = new JLabel(text);
        title.setFont(fuenteGraffiti(30));
        title.setForeground(white);
        title.setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 0));
        title.setAlignmentX(LEFT_ALIGNMENT);
        return title;
    }

    // Barra inferior con el boton para cerrar sesion.
    private JPanel crearBarraInferior() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        JButton logoutButton = crearBoton("Cerrar sesion");
        logoutButton.setPreferredSize(new Dimension(160, 38));
        logoutButton.addActionListener(event -> cerrarSesion());
        panel.add(logoutButton, BorderLayout.EAST);

        return panel;
    }

    // Tarjeta del catalogo: muestra nombre, tipo, detalle, ID, stock, precio y boton Agregar.
    private JPanel crearTarjetaProducto(Product1 product) {
        JPanel card = new JPanel(new BorderLayout(10, 10)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setPaint(new GradientPaint(0, 0, softViolet, getWidth(), getHeight(), new Color(96, 52, 130)));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.setColor(markerBlack);
                g2.drawRoundRect(2, 2, getWidth() - 5, getHeight() - 5, 16, 16);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setPreferredSize(new Dimension(260, 210));
        card.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));

        JLabel name = new JLabel(product.getNameProduct());
        name.setFont(fuenteGraffiti(24));
        name.setForeground(white);
        card.add(name, BorderLayout.NORTH);

        JTextArea detail = new JTextArea();
        detail.setEditable(false);
        detail.setOpaque(false);
        detail.setLineWrap(true);
        detail.setWrapStyleWord(true);
        detail.setFont(new Font("Arial", Font.BOLD, 14));
        detail.setForeground(pastelPurple);
        detail.setText(
            obtenerTipoProducto(product) + "\n" +
            obtenerDetalleProducto(product) + "\n\n" +
            "ID: " + product.getIdProduct() + "\n" +
            "Stock: " + product.getStock()
        );
        card.add(detail, BorderLayout.CENTER);

        JPanel footer = new JPanel(new BorderLayout(10, 0));
        footer.setOpaque(false);

        JLabel priceLabel = new JLabel("$" + String.format("%.2f", product.getPrice()));
        priceLabel.setFont(new Font("Arial", Font.BOLD, 20));
        priceLabel.setForeground(new Color(255, 230, 130));
        footer.add(priceLabel, BorderLayout.WEST);

        JButton addButton = crearBoton("Agregar");
        addButton.setPreferredSize(new Dimension(105, 36));
        addButton.setEnabled(product.getStock() > 0);
        addButton.addActionListener(event -> agregarAlCarrito(product));
        footer.add(addButton, BorderLayout.EAST);

        card.add(footer, BorderLayout.SOUTH);
        return card;
    }

    // Seccion visual con imagenes de trabajos artisticos de InkUrban.
    private JPanel crearGaleriaArtistas() {
        JPanel section = new JPanel(new BorderLayout(14, 14));
        section.setOpaque(false);
        section.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(markerBlack, 3),
            BorderFactory.createEmptyBorder(18, 18, 18, 18)
        ));

        JLabel title = new JLabel("Trabajo de nuestros artistas");
        title.setFont(fuenteGraffiti(30));
        title.setForeground(white);
        section.add(title, BorderLayout.NORTH);

        JPanel gallery = new JPanel(new GridLayout(0, 2, 16, 16));
        gallery.setOpaque(false);

        gallery.add(crearTarjetaImagen("Murales con voz propia", "artist-liberty.jpg"));
        gallery.add(crearTarjetaImagen("Trenes, ritmo y vieja escuela", "artist-train.jpg"));
        gallery.add(crearTarjetaImagen("Tags que llenan la ciudad", "artist-red-wall.jpg"));
        gallery.add(crearTarjetaImagen("Pasillos que respiran color", "artist-stairs.png"));
        gallery.add(crearTarjetaImagen("Piezas grandes, impacto real", "artist-bowery.jpg"));
        gallery.add(crearTarjetaImagen("Muros vivos de barrio", "artist-bushwick.png"));
        gallery.add(crearTarjetaImagen("Wildstyle y movimiento", "artist-wildstyle.jpg"));
        gallery.add(crearTarjetaImagen("Color duro, linea limpia", "artist-blue-piece.jpg"));
        gallery.add(crearTarjetaImagen("Personajes con actitud", "artist-character.jpg"));
        gallery.add(crearTarjetaImagen("Paredes que prenden la calle", "artist-color-wall.jpg"));

        section.add(gallery, BorderLayout.CENTER);
        return section;
    }

    // Tarjeta de imagen: carga una foto desde Assets y coloca su titulo debajo.
    private JPanel crearTarjetaImagen(String title, String file) {
        JPanel card = new JPanel(new BorderLayout(8, 8));
        card.setBackground(new Color(83, 48, 119));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(markerBlack, 2),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        JLabel imageLabel = new JLabel("", SwingConstants.CENTER);
        imageLabel.setPreferredSize(new Dimension(320, 170));
        imageLabel.setOpaque(true);
        imageLabel.setBackground(darkPurple);

        ImageIcon icon = cargarImagenGaleria(file, 320, 170);
        if (icon != null) {
            imageLabel.setIcon(icon);
        } else {
            imageLabel.setText("Imagen no disponible");
            imageLabel.setForeground(white);
            imageLabel.setFont(new Font("Arial", Font.BOLD, 14));
        }
        card.add(imageLabel, BorderLayout.CENTER);

        JLabel text = new JLabel(title, SwingConstants.CENTER);
        text.setFont(new Font("Arial", Font.BOLD, 15));
        text.setForeground(new Color(255, 230, 130));
        card.add(text, BorderLayout.SOUTH);
        return card;
    }

    // Bloque informativo para textos largos como "Quienes somos" o historia del graffiti.
    private JPanel crearBloqueTexto(String title, String text) {
        JPanel block = new JPanel(new BorderLayout(12, 10));
        block.setBackground(new Color(83, 48, 119));
        block.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(markerBlack, 3),
            BorderFactory.createEmptyBorder(20, 22, 20, 22)
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(fuenteGraffiti(30));
        titleLabel.setForeground(white);
        block.add(titleLabel, BorderLayout.NORTH);

        JTextArea body = new JTextArea(text);
        body.setEditable(false);
        body.setOpaque(false);
        body.setLineWrap(true);
        body.setWrapStyleWord(true);
        body.setFont(new Font("Arial", Font.BOLD, 15));
        body.setForeground(pastelPurple);
        block.add(body, BorderLayout.CENTER);

        return block;
    }

    // Intenta usar fuentes con estilo urbano; si no existen en el sistema, usa Arial.
    private Font fuenteGraffiti(int size) {
        String[] fontOptions = {
            "Showcard Gothic",
            "Jokerman",
            "Chiller",
            "Stencil",
            "Impact",
            "Arial Black"
        };

        for (String name : fontOptions) {
            Font font = new Font(name, Font.BOLD, size);
            if (font.getFamily().equalsIgnoreCase(name) || font.getFontName().toLowerCase().contains(name.toLowerCase())) {
                return font;
            }
        }
        return new Font("Arial", Font.BOLD, size);
    }

    // Contenedor base con color morado y borde negro para mantener el estilo de la tienda.
    private JPanel crearPanelDecorado() {
        JPanel panel = new JPanel();
        panel.setBackground(new Color(70, 43, 101, 232));
        panel.setBorder(BorderFactory.createLineBorder(markerBlack, 3));
        return panel;
    }

    // Crea botones con el mismo color, borde y fuente en toda la ventana.
    private JButton crearBoton(String text) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setForeground(white);
        button.setBackground(activePurple);
        button.setFont(new Font("Arial", Font.BOLD, 13));
        button.setBorder(BorderFactory.createLineBorder(darkPurple, 2));
        return button;
    }

    // Campo de texto usado en direccion y datos de pago.
    private JTextField crearCampoDireccion() {
        JTextField field = new JTextField();
        field.setFont(new Font("Arial", Font.PLAIN, 13));
        field.setBackground(pastelPurple);
        field.setForeground(darkPurple);
        field.setBorder(BorderFactory.createLineBorder(markerBlack, 2));
        return field;
    }

    // Tabla alternativa para mostrar productos del carrito con encabezados y columnas fijas.
    private JTable crearTablaCarrito(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setFont(new Font("Arial", Font.PLAIN, 13));
        table.setRowHeight(28);
        table.setShowGrid(true);
        table.setGridColor(new Color(200, 185, 215));
        table.setBackground(pastelPurple);
        table.setForeground(darkPurple);
        table.setSelectionBackground(activePurple);
        table.setSelectionForeground(white);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        JTableHeader tableHeader = table.getTableHeader();
        tableHeader.setFont(new Font("Arial", Font.BOLD, 13));
        tableHeader.setBackground(darkPurple);
        tableHeader.setForeground(white);
        tableHeader.setReorderingAllowed(false);

        table.getColumnModel().getColumn(0).setPreferredWidth(65);
        table.getColumnModel().getColumn(1).setPreferredWidth(170);
        table.getColumnModel().getColumn(2).setPreferredWidth(90);
        table.getColumnModel().getColumn(3).setPreferredWidth(310);
        return table;
    }

    // Carga el logo pequeno del encabezado.
    private ImageIcon cargarLogoPequeno() {
        String[] fileNames = {
            "inkurban-logo-cutout.png",
            "inkurban-logo-clean.png",
            "inkurban-logo.png"
        };

        for (String fileName : fileNames) {
            ImageIcon icon = cargarIconoAsset(fileName);
            if (icon != null) {
                Image imageLabel = icon.getImage().getScaledInstance(82, 54, Image.SCALE_SMOOTH);
                return new ImageIcon(imageLabel);
            }
        }
        return null;
    }

    // Carga y escala imagenes de la galeria artistica.
    private ImageIcon cargarImagenGaleria(String fileName, int maxWidth, int maxHeight) {
        ImageIcon icon = cargarIconoAsset(fileName);
        if (icon != null) {
            Image imageLabel = escalarImagen(icon.getImage(), maxWidth, maxHeight);
            return new ImageIcon(imageLabel);
        }
        return null;
    }

    // Busca archivos en varias rutas posibles de Assets segun desde donde se ejecute el proyecto.
    private ImageIcon cargarIconoAsset(String fileName) {
        String[] paths = {
            "src/Assets/" + fileName,
            "InkUrban/src/Assets/" + fileName,
            "InkUrban/InkUrban/src/Assets/" + fileName
        };

        for (String path : paths) {
            File file = new File(path);
            if (file.exists()) {
                ImageIcon icon = new ImageIcon(file.getAbsolutePath());
                if (icon.getIconWidth() > 0 && icon.getIconHeight() > 0) {
                    return icon;
                }
            }
        }
        return null;
    }

    // Escala imagenes manteniendo proporcion para que no se deformen.
    private Image escalarImagen(Image imageLabel, int maxWidth, int maxHeight) {
        int originalWidth = imageLabel.getWidth(null);
        int originalHeight = imageLabel.getHeight(null);

        if (originalWidth <= 0 || originalHeight <= 0) {
            return imageLabel;
        }

        double scale = Math.min((double) maxWidth / originalWidth, (double) maxHeight / originalHeight);
        int width = Math.max(1, (int) Math.round(originalWidth * scale));
        int height = Math.max(1, (int) Math.round(originalHeight * scale));
        return imageLabel.getScaledInstance(width, height, Image.SCALE_SMOOTH);
    }

    // Agrega un producto al carrito y actualiza tarjetas, resumen y ventana de carrito si esta abierta.
    private void agregarAlCarrito(Product1 product) {
        try {
            shoppingCar.addProduct(product);
            refrescarProductos();
            refrescarCarrito();
            JOptionPane.showMessageDialog(this, "Producto agregado al carrito", "Carrito", JOptionPane.INFORMATION_MESSAGE);
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Abre el dialogo del carrito: lista productos agregados, total, comprar y vaciar.
    private void mostrarCarrito() {
        if (cartDialog == null) {
            cartDialog = new JDialog(this, "Mi carrito", false);
            cartDialog.setSize(780, 560);
            cartDialog.setLocationRelativeTo(this);
            cartDialog.setLayout(new BorderLayout());

            JPanel background = new GraffitiPanel();
            background.setLayout(new BorderLayout(14, 14));
            background.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(markerBlack, 3),
                BorderFactory.createEmptyBorder(24, 24, 24, 24)
            ));
            cartDialog.add(background, BorderLayout.CENTER);

            JLabel title = new JLabel("Mi carrito InkUrban");
            title.setFont(fuenteGraffiti(32));
            title.setForeground(white);
            background.add(title, BorderLayout.NORTH);

            cartItemsPanel = new JPanel();
            cartItemsPanel.setLayout(new BoxLayout(cartItemsPanel, BoxLayout.Y_AXIS));
            cartItemsPanel.setBackground(new Color(50, 31, 70));
            cartItemsPanel.setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));

            JScrollPane scrollPane = new JScrollPane(cartItemsPanel);
            scrollPane.setBorder(BorderFactory.createLineBorder(markerBlack, 2));
            scrollPane.getViewport().setBackground(new Color(50, 31, 70));
            scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
            background.add(scrollPane, BorderLayout.CENTER);

            JPanel inferior = new JPanel(new BorderLayout(12, 12));
            inferior.setOpaque(false);

            cartTotalLabel = new JLabel("", SwingConstants.RIGHT);
            cartTotalLabel.setFont(fuenteGraffiti(24));
            cartTotalLabel.setForeground(new Color(255, 230, 130));
            inferior.add(cartTotalLabel, BorderLayout.NORTH);

            JPanel actions = new JPanel(new GridLayout(1, 2, 12, 0));
            actions.setOpaque(false);

            JButton buyButton = crearBoton("Ir a comprar");
            buyButton.addActionListener(event -> comprar());
            actions.add(buyButton);

            JButton clearButton = crearBoton("Vaciar");
            clearButton.addActionListener(event -> vaciarCarrito());
            actions.add(clearButton);

            inferior.add(actions, BorderLayout.SOUTH);
            background.add(inferior, BorderLayout.SOUTH);
        }

        refrescarCarrito();
        cartDialog.setVisible(true);
    }

    // Quita un producto del carrito y restaura su stock.
    private void quitarDelCarrito(Product1 product) {
        try {
            shoppingCar.deleteProduct(product);
            refrescarProductos();
            refrescarCarrito();
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Tarjeta individual dentro del carrito con informacion del producto y boton "Quitar".
    private JPanel crearTarjetaCarrito(Product1 product) {
        JPanel card = new JPanel(new BorderLayout(12, 8)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setPaint(new GradientPaint(0, 0, new Color(83, 48, 119), getWidth(), getHeight(), new Color(118, 67, 158)));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.setColor(markerBlack);
                g2.drawRoundRect(2, 2, getWidth() - 5, getHeight() - 5, 16, 16);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 118));
        card.setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 16));

        JPanel info = new JPanel(new GridLayout(3, 1, 0, 3));
        info.setOpaque(false);

        JLabel name = new JLabel(product.getNameProduct());
        name.setFont(fuenteGraffiti(22));
        name.setForeground(white);
        info.add(name);

        JLabel detail = new JLabel(obtenerTipoProducto(product) + " | " + obtenerDetalleProducto(product));
        detail.setFont(new Font("Arial", Font.BOLD, 13));
        detail.setForeground(pastelPurple);
        info.add(detail);

        JLabel priceLabel = new JLabel("$" + String.format("%.2f", product.getPrice()));
        priceLabel.setFont(new Font("Arial", Font.BOLD, 18));
        priceLabel.setForeground(new Color(255, 230, 130));
        info.add(priceLabel);

        card.add(info, BorderLayout.CENTER);

        JButton removeButton = crearBoton("Quitar");
        removeButton.setPreferredSize(new Dimension(105, 36));
        removeButton.addActionListener(event -> quitarDelCarrito(product));
        card.add(removeButton, BorderLayout.EAST);

        return card;
    }

    // Elimina todos los productos del carrito y devuelve el stock reservado.
    private void vaciarCarrito() {
        if (shoppingCar.getProducts().isEmpty()) {
            JOptionPane.showMessageDialog(this, "El carrito de compras esta vacio", "Carrito", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        shoppingCar.restoreStockAndClear();
        refrescarProductos();
        refrescarCarrito();
    }

    // Flujo principal de compra: valida carrito, pide datos, crea orden/envio y muestra factura.
    private void comprar() {
        if (shoppingCar.getProducts().isEmpty()) {
            JOptionPane.showMessageDialog(this, "El carrito de compras esta vacio", "Compra", JOptionPane.WARNING_MESSAGE);
            return;
        }

        CompraInfo compraInfo = solicitarDatosCompra();
        if (compraInfo == null) {
            return;
        }

        try {
            String idOrder = generateId();
            String date = LocalDate.now().toString();
            Order1 order = new Order1(idOrder, date);
            order.goToPay();

            String selectedMethod = compraInfo.paymentMethod;
            Payment1 payment = new Payment1(generateId(), selectedMethod);
            payment.processingPay();
            payment.validatePay();

            Shipping1 shipping = new Shipping1(generateId(), compraInfo.address);
            shipping.shipOrder(order);

            double total = shoppingCar.calculateTotal();
            String invoice = construirFactura(order, selectedMethod, shipping, total);
            payment.generateInvoice(order, shoppingCar, shipping);
            order.confirmPay(shoppingCar);
            shoppingCar.clearShoppingCar();
            refrescarProductos();
            refrescarCarrito();
            mostrarFactura(invoice);
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Dialogo de compra: pide metodo de pago, direccion y campos extra segun el metodo elegido.
    private CompraInfo solicitarDatosCompra() {
        JDialog dialog = new JDialog(this, "Finalizar compra", true);
        dialog.setSize(560, 560);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel background = new GraffitiPanel();
        background.setLayout(new BorderLayout(16, 16));
        background.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(markerBlack, 3),
            BorderFactory.createEmptyBorder(24, 28, 24, 28)
        ));
        dialog.add(background, BorderLayout.CENTER);

        JLabel title = new JLabel("Finalizar compra");
        title.setFont(fuenteGraffiti(32));
        title.setForeground(white);
        background.add(title, BorderLayout.NORTH);

        JPanel formulario = new JPanel(new BorderLayout(12, 12));
        formulario.setOpaque(false);
        background.add(formulario, BorderLayout.CENTER);

        JPanel basePanel = new JPanel(new GridLayout(4, 1, 8, 8));
        basePanel.setOpaque(false);

        JComboBox<String> paymentMethod = new JComboBox<>(new String[] {
            "Efectivo",
            "Tarjeta de debito",
            "Tarjeta de credito",
            "Transferencia"
        });
        paymentMethod.setBackground(pastelPurple);
        paymentMethod.setForeground(darkPurple);
        paymentMethod.setFont(new Font("Arial", Font.BOLD, 13));

        JTextField addressField = crearCampoDireccion();

        basePanel.add(crearEtiquetaFormulario("Metodo de pago:"));
        basePanel.add(paymentMethod);
        basePanel.add(crearEtiquetaFormulario("Direccion de entrega:"));
        basePanel.add(addressField);
        formulario.add(basePanel, BorderLayout.NORTH);

        JPanel extrasPanel = new JPanel(new GridLayout(0, 1, 8, 8));
        extrasPanel.setOpaque(false);
        formulario.add(extrasPanel, BorderLayout.CENTER);

        JPanel actions = new JPanel(new GridLayout(1, 2, 12, 0));
        actions.setOpaque(false);

        JButton confirmButton = crearBoton("Confirmar");
        JButton cancelButton = crearBoton("Cancelar");
        actions.add(confirmButton);
        actions.add(cancelButton);
        background.add(actions, BorderLayout.SOUTH);

        CompraInfo[] result = new CompraInfo[1];
        Runnable updateExtras = () -> actualizarCamposPago(extrasPanel, (String) paymentMethod.getSelectedItem());
        paymentMethod.addActionListener(event -> updateExtras.run());
        updateExtras.run();

        confirmButton.addActionListener(event -> {
            try {
                String method = (String) paymentMethod.getSelectedItem();
                validarDatosPago(method, addressField.getText(), extrasPanel);
                result[0] = new CompraInfo(method, addressField.getText().trim());
                dialog.dispose();
            } catch (IllegalArgumentException e) {
                JOptionPane.showMessageDialog(dialog, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(event -> dialog.dispose());
        dialog.setVisible(true);
        return result[0];
    }

    // Etiquetas del formulario de pago y envio.
    private JLabel crearEtiquetaFormulario(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 14));
        label.setForeground(white);
        return label;
    }

    // Campo de pago identificado con name para poder leerlo despues desde validarDatosPago.
    private JTextField crearCampoPago(String name) {
        JTextField field = crearCampoDireccion();
        field.setName(name);
        return field;
    }

    // Campo de pago que solo acepta numeros y limita la cantidad de caracteres.
    private JTextField crearCampoPagoNumerico(String name, int maxCharacters) {
        JTextField field = crearCampoPago(name);
        aplicarFiltroNumerico(field, maxCharacters);
        return field;
    }

    // Campo para fecha de vencimiento con formato limitado a numeros y "/".
    private JTextField crearCampoPagoFecha(String name) {
        JTextField field = crearCampoPago(name);
        aplicarFiltroFecha(field);
        return field;
    }

    // Campo oculto para codigo de seguridad de la tarjeta.
    private JPasswordField crearCampoClavePago(String name) {
        JPasswordField field = new JPasswordField();
        field.setName(name);
        field.setFont(new Font("Arial", Font.PLAIN, 13));
        field.setBackground(pastelPurple);
        field.setForeground(darkPurple);
        field.setBorder(BorderFactory.createLineBorder(markerBlack, 2));
        aplicarFiltroNumerico(field, 4);
        return field;
    }

    // Cambia los campos visibles dependiendo del metodo de pago seleccionado.
    private void actualizarCamposPago(JPanel extrasPanel, String method) {
        extrasPanel.removeAll();

        if ("Tarjeta de debito".equals(method) || "Tarjeta de credito".equals(method)) {
            extrasPanel.add(crearEtiquetaFormulario("Nombre del titular:"));
            extrasPanel.add(crearCampoPago("cardHolder"));
            extrasPanel.add(crearEtiquetaFormulario("Numero de tarjeta:"));
            JTextField cardNumber = crearCampoPago("cardNumber");
            cardNumber.setToolTipText("16 digitos");
            extrasPanel.add(cardNumber);
            extrasPanel.add(crearEtiquetaFormulario("Fecha de vencimiento (MM/AA):"));
            JTextField expirationDate = crearCampoPagoFecha("expirationDate");
            expirationDate.setToolTipText("Formato MM/AA");
            extrasPanel.add(expirationDate);
            extrasPanel.add(crearEtiquetaFormulario("Codigo de seguridad:"));
            extrasPanel.add(crearCampoClavePago("securityCode"));
        } else if ("Transferencia".equals(method)) {
            extrasPanel.add(crearEtiquetaFormulario("Banco:"));
            extrasPanel.add(crearCampoPago("bank"));
            extrasPanel.add(crearEtiquetaFormulario("Numero de cuenta o telefono:"));
            JTextField account = crearCampoPagoNumerico("account", 20);
            account.setToolTipText("Solo numeros");
            extrasPanel.add(account);
            extrasPanel.add(crearEtiquetaFormulario("Referencia de transferencia:"));
            extrasPanel.add(crearCampoPago("reference"));
        } else {
            JTextArea nota = new JTextArea("Pago contra entrega. Ten el valor completo listo al recibir tu pedido.");
            nota.setEditable(false);
            nota.setLineWrap(true);
            nota.setWrapStyleWord(true);
            nota.setOpaque(false);
            nota.setFont(new Font("Arial", Font.BOLD, 14));
            nota.setForeground(pastelPurple);
            extrasPanel.add(nota);
        }

        extrasPanel.revalidate();
        extrasPanel.repaint();
    }

    // Valida direccion y datos de pago antes de permitir confirmar la compra.
    private void validarDatosPago(String method, String address, JPanel extrasPanel) {
        Shipping1.validateShippingAddress(address);

        if ("Tarjeta de debito".equals(method) || "Tarjeta de credito".equals(method)) {
            String cardHolder = obtenerValorCampo(extrasPanel, "cardHolder");
            String cardNumber = obtenerValorCampo(extrasPanel, "cardNumber");
            String expirationDate = obtenerValorCampo(extrasPanel, "expirationDate");
            String securityCode = obtenerValorCampo(extrasPanel, "securityCode");

            Payment1.validateCardPayment(cardHolder, cardNumber, expirationDate, securityCode);
        } else if ("Transferencia".equals(method)) {
            String bank = obtenerValorCampo(extrasPanel, "bank");
            String account = obtenerValorCampo(extrasPanel, "account");
            String reference = obtenerValorCampo(extrasPanel, "reference");

            Payment1.validateTransferPayment(bank, account, reference);
        }
    }

    // DocumentFilter: bloquea letras y limita longitud en campos numericos.
    private void aplicarFiltroNumerico(JTextField field, int maxCharacters) {
        ((AbstractDocument) field.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
                reemplazar(fb, offset, 0, string, attr);
            }

            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                reemplazar(fb, offset, length, text, attrs);
            }

            private void reemplazar(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                if (text == null) {
                    return;
                }
                String cleanText = text.replaceAll("\\D", "");
                int newLength = fb.getDocument().getLength() - length + cleanText.length();
                if (newLength <= maxCharacters) {
                    fb.replace(offset, length, cleanText, attrs);
                }
            }
        });
    }

    // DocumentFilter para fecha: permite solo numeros y slash, maximo 5 caracteres.
    private void aplicarFiltroFecha(JTextField field) {
        ((AbstractDocument) field.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
                reemplazar(fb, offset, 0, string, attr);
            }

            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                reemplazar(fb, offset, length, text, attrs);
            }

            private void reemplazar(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                if (text == null) {
                    return;
                }
                String cleanText = text.replaceAll("[^0-9/]", "");
                int newLength = fb.getDocument().getLength() - length + cleanText.length();
                if (newLength <= 5) {
                    fb.replace(offset, length, cleanText, attrs);
                }
            }
        });
    }

    // Busca dentro del panel el campo con el name indicado y retorna su valor.
    private String obtenerValorCampo(JPanel panel, String name) {
        for (int i = 0; i < panel.getComponentCount(); i++) {
            java.awt.Component component = panel.getComponent(i);
            if (name.equals(component.getName())) {
                if (component instanceof JPasswordField) {
                    return new String(((JPasswordField) component).getPassword()).trim();
                }
                if (component instanceof JTextField) {
                    return ((JTextField) component).getText().trim();
                }
            }
        }
        return "";
    }

    // Genera IDs simples para ordenes o envios durante la compra.
    private String generateId() {
        return String.valueOf((int) (Math.random() * 100000));
    }

    // Arma el texto de la factura con orden, cliente, productos, total, pago y envio.
    private String construirFactura(Order1 order, String paymentMethod, Shipping1 shipping, double total) {
        StringBuilder invoice = new StringBuilder();
        invoice.append("INKURBAN\n");
        invoice.append("Arte callejero\n\n");
        invoice.append(order.toString()).append("\n");
        invoice.append("Cliente: ").append(customer.getName()).append("\n\n");
        invoice.append("Productos:\n");
        for (Product1 product : shoppingCar.getProducts()) {
            invoice.append("- ")
                .append(product.getNameProduct())
                .append(" | $")
                .append(String.format("%.2f", product.getPrice()))
                .append("\n");
        }
        invoice.append("\nTotal: $").append(String.format("%.2f", total)).append("\n");
        invoice.append("Metodo de pago: ").append(paymentMethod).append("\n");
        invoice.append("Direccion de entrega: ").append(shipping.getShippingAddress()).append("\n");
        invoice.append("Id de envio: ").append(shipping.getIdShipping());
        return invoice.toString();
    }

    // Muestra la factura final en un dialogo despues de confirmar la compra.
    private void mostrarFactura(String invoice) {
        JTextArea areaText = new JTextArea(invoice);
        areaText.setEditable(false);
        areaText.setFont(new Font("Monospaced", Font.BOLD, 16));
        areaText.setBackground(pastelPurple);
        areaText.setForeground(darkPurple);
        areaText.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));

        JDialog dialog = new JDialog(this, "Compra realizada", true);
        dialog.setSize(640, 560);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel background = new GraffitiPanel();
        background.setLayout(new BorderLayout(14, 14));
        background.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(markerBlack, 3),
            BorderFactory.createEmptyBorder(24, 24, 24, 24)
        ));
        dialog.add(background, BorderLayout.CENTER);

        JLabel title = new JLabel("Factura InkUrban");
        title.setFont(fuenteGraffiti(32));
        title.setForeground(white);
        background.add(title, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(areaText);
        scrollPane.setBorder(BorderFactory.createLineBorder(markerBlack, 2));
        scrollPane.getViewport().setBackground(pastelPurple);
        background.add(scrollPane, BorderLayout.CENTER);

        JButton closeButton = crearBoton("Cerrar");
        closeButton.addActionListener(event -> dialog.dispose());
        background.add(closeButton, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    // Objeto pequeno para devolver metodo de pago y direccion desde el dialogo de compra.
    private class CompraInfo {
        private String paymentMethod;
        private String address;

        public CompraInfo(String paymentMethod, String address) {
            this.paymentMethod = paymentMethod;
            this.address = address;
        }
    }

    // Reconstruye el catalogo visual cada vez que cambia stock o lista de productos.
    private void refrescarProductos() {
        productsPanel.removeAll();

        if (products.isEmpty()) {
            JLabel emptyLabel = new JLabel("No hay productos disponibles", SwingConstants.CENTER);
            emptyLabel.setFont(new Font("Arial", Font.BOLD, 22));
            emptyLabel.setForeground(white);
            productsPanel.add(emptyLabel);
        } else {
            for (Product1 product : products) {
                productsPanel.add(crearTarjetaProducto(product));
            }
        }

        productsPanel.revalidate();
        productsPanel.repaint();
        actualizarResumenCarrito();
    }

    // Reconstruye el contenido del carrito y recalcula el total mostrado.
    private void refrescarCarrito() {
        if (cartItemsPanel != null) {
            cartItemsPanel.removeAll();

            if (shoppingCar.getProducts().isEmpty()) {
                JLabel emptyLabel = new JLabel("Tu carrito esta vacio", SwingConstants.CENTER);
                emptyLabel.setFont(fuenteGraffiti(26));
                emptyLabel.setForeground(white);
                emptyLabel.setBorder(BorderFactory.createEmptyBorder(42, 12, 42, 12));
                cartItemsPanel.add(emptyLabel);
            } else {
                for (Product1 product : shoppingCar.getProducts()) {
                    cartItemsPanel.add(crearTarjetaCarrito(product));
                    cartItemsPanel.add(crearSeparadorVertical(12));
                }
            }

            cartItemsPanel.revalidate();
            cartItemsPanel.repaint();
        }

        actualizarResumenCarrito();
        if (cartTotalLabel != null) {
            cartTotalLabel.setText("Total: $" + String.format("%.2f", calcularTotalSilencioso()));
        }
    }

    // Actualiza el resumen superior: cantidad de productos y valor total del carrito.
    private void actualizarResumenCarrito() {
        if (cartSummaryLabel != null) {
            cartSummaryLabel.setText(
                shoppingCar.getProducts().size() + " productos en carrito | $" +
                String.format("%.2f", calcularTotalSilencioso())
            );
        }
    }

    // Calcula el total sin mostrar ventanas ni mensajes.
    private double calcularTotalSilencioso() {
        double total = 0;
        for (Product1 product : shoppingCar.getProducts()) {
            total += product.getPrice();
        }
        return total;
    }

    // Convierte la clase concreta del producto en texto para mostrar en pantalla.
    private String obtenerTipoProducto(Product1 product) {
        if (product instanceof Spray) {
            return "Spray";
        }
        if (product instanceof Marker) {
            return "Marcador";
        }
        if (product instanceof SprayCap) {
            return "Tapa";
        }
        return "Producto";
    }

    // Obtiene color/tamano o tipo de tapa segun el producto que se esta mostrando.
    private String obtenerDetalleProducto(Product1 product) {
        if (product instanceof Spray) {
            Spray spray = (Spray) product;
            return "Color: " + spray.getColor() + " | Tamano: " + spray.getSize();
        }
        if (product instanceof Marker) {
            Marker marker = (Marker) product;
            return "Color: " + marker.getColor() + " | Tamano: " + marker.getSize();
        }
        if (product instanceof SprayCap) {
            SprayCap cap = (SprayCap) product;
            return "Tipo: " + cap.getType();
        }
        return "";
    }

    // Cierra sesion, limpia/restaura el carrito pendiente y vuelve a MainWindow.
    private void cerrarSesion() {
        if (!shoppingCar.getProducts().isEmpty()) {
            shoppingCar.restoreStockAndClear();
        }
        customer.logout();
        dispose();
        mainWindow.setVisible(true);
        mainWindow.setExtendedState(JFrame.MAXIMIZED_BOTH);
    }

    // Fondo personalizado: pinta el muro y superpone imagenes de graffiti.
    private class GraffitiPanel extends JPanel {
        private static final long serialVersionUID = 1L;

        private transient Image graffitiFrame = cargarImagen("graffiti-frame.png");
        private transient Image graffitiSignature = cargarImagen("graffiti-tag-large.png");
        private transient Image graffitiDrip = cargarImagen("graffiti-drip-tag.png");
        private transient Image graffitiBlock = cargarImagen("graffiti-block.png");

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);

            g2.setPaint(new GradientPaint(0, 0, new Color(76, 65, 91), getWidth(), getHeight(), new Color(20, 14, 27)));
            g2.fillRect(0, 0, getWidth(), getHeight());

            dibujarMuro(g2);
            dibujarGrafitis(g2);
            g2.dispose();
        }

        private void dibujarMuro(Graphics2D g2) {
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.16f));
            g2.setColor(new Color(210, 203, 220));
            for (int y = 24; y < getHeight(); y += 30) {
                g2.drawLine(0, y, getWidth(), y);
            }
            for (int y = 0; y < getHeight(); y += 30) {
                int offset = (y / 30) % 2 == 0 ? 0 : 45;
                for (int x = -offset; x < getWidth(); x += 90) {
                    g2.drawLine(x, y, x, y + 30);
                }
            }
            g2.setComposite(AlphaComposite.SrcOver);
        }

        private void dibujarGrafitis(Graphics2D g2) {
            dibujarImagen(g2, graffitiFrame, -55, -70, getWidth() + 110, getHeight() + 140, 0.78f);
            dibujarImagen(g2, graffitiSignature, -120, 110, 455, 420, 0.48f);
            dibujarImagen(g2, graffitiDrip, getWidth() - 355, 65, 350, 455, 0.46f);
            dibujarImagen(g2, graffitiSignature, getWidth() - 500, getHeight() - 315, 460, 420, 0.34f);
            dibujarImagen(g2, graffitiBlock, getWidth() / 2 - 250, getHeight() - 285, 500, 330, 0.30f);
        }

        private Image cargarImagen(String fileName) {
            ImageIcon icon = cargarIconoAsset(fileName);
            if (icon != null) {
                return icon.getImage();
            }
            return null;
        }

        private void dibujarImagen(Graphics2D g2, Image imageLabel, int x, int y, int maxWidth, int maxHeight, float opacity) {
            if (imageLabel == null) {
                return;
            }

            int originalWidth = imageLabel.getWidth(null);
            int originalHeight = imageLabel.getHeight(null);
            if (originalWidth <= 0 || originalHeight <= 0) {
                return;
            }

            double scale = Math.min((double) maxWidth / originalWidth, (double) maxHeight / originalHeight);
            int width = Math.max(1, (int) Math.round(originalWidth * scale));
            int height = Math.max(1, (int) Math.round(originalHeight * scale));

            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
            g2.drawImage(imageLabel, x, y, width, height, null);
            g2.setComposite(AlphaComposite.SrcOver);
        }
    }
}
