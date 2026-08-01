package Interfaces;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.List;

import javax.swing.*;
import javax.swing.table.*;

import Product.*;
import User.*;

/*
 * Ventana del administrador.
 * Se abre despues del login de admin y permite registrar productos,
 * verlos en una tabla, eliminarlos y actualizar su stock.
 */
public class AdminWindow extends JFrame {

    private static final long serialVersionUID = 1L;

    // Referencias al administrador actual, productos del sistema y ventana principal.
    private transient Admin admin;
    private transient List<Product1> products;
    private transient MainWindow mainWindow;
    private transient UserStore userStore;

    // Componentes del formulario de registro ubicado a la izquierda.
    private JComboBox<String> productType;
    private JTextField idField;
    private JTextField nameField;
    private JTextField priceField;
    private JTextField stockField;
    private JTextField extraField1;
    private JTextField extraField2;
    private JLabel extraLabel1;
    private JLabel extraLabel2;

    // Tabla ubicada a la derecha, donde se muestran los productos ya registrados.
    private JTable productsTable;
    private DefaultTableModel productsModel;

    // Colores usados por todos los componentes de esta interfaz.
    private final Color panelPurple = new Color(70, 43, 101);
    private final Color activePurple = new Color(138, 82, 214);
    private final Color darkPurple = new Color(25, 16, 35);
    private final Color white = new Color(245, 242, 250);
    private final Color pastelPurple = new Color(241, 232, 250);
    private final Color markerBlack = Color.BLACK;

    @SuppressWarnings("this-escape")
    public AdminWindow(Admin admin, List<Product1> products, MainWindow mainWindow) {
        this(admin, products, mainWindow, new UserStore());
    }

    @SuppressWarnings("this-escape")
    public AdminWindow(Admin admin, List<Product1> products, MainWindow mainWindow, UserStore userStore) {
        this.admin = admin;
        this.products = products;
        this.mainWindow = mainWindow;
        this.userStore = userStore;

        setTitle("InkUrban - Administrador");
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setMinimumSize(new Dimension(980, 620));
        setLayout(new BorderLayout());

        // Fondo general de la ventana. Encima se ubican encabezado, contenido y barra inferior.
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
    }

    // Parte superior: logo pequeno, titulo "Panel Administrador" y subtitulo.
    private JPanel crearEncabezado() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        ImageIcon logoIcon = cargarLogoPequeno();
        if (logoIcon != null) {
            JLabel logoLabel = new JLabel(logoIcon);
            logoLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 14));
            panel.add(logoLabel, BorderLayout.WEST);
        }

        JLabel title = new JLabel("Panel Administrador", SwingConstants.LEFT);
        title.setFont(new Font("Arial", Font.BOLD, 34));
        title.setForeground(white);
        panel.add(title, BorderLayout.CENTER);

        JLabel subtitle = new JLabel("Gestion de productos InkUrban", SwingConstants.RIGHT);
        subtitle.setFont(new Font("Arial", Font.BOLD | Font.ITALIC, 16));
        subtitle.setForeground(new Color(220, 211, 232));
        panel.add(subtitle, BorderLayout.EAST);

        return panel;
    }

    // Cuerpo central dividido en dos columnas: formulario y listado de productos.
    private JPanel crearContenido() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 24, 0));
        panel.setOpaque(false);
        panel.add(crearFormularioProducto());
        panel.add(crearListadoProductos());
        return panel;
    }

    // Formulario izquierdo: recibe los datos necesarios para crear Spray, Marcador o Tapa.
    private JPanel crearFormularioProducto() {
        JPanel panel = crearPanelDecorado();
        panel.setLayout(null);

        JLabel title = new JLabel("Registrar producto");
        title.setBounds(30, 22, 320, 30);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setForeground(white);
        panel.add(title);

        JLabel typeLabel = crearEtiqueta("Tipo:", 35, 78, panel);
        // ComboBox que define el tipo de producto; cambia los campos extra segun la opcion.
        productType = new JComboBox<>(new String[] {"Spray", "Marcador", "Tapa de spray"});
        productType.setBounds(155, typeLabel.getY(), 230, 30);
        productType.setFont(new Font("Arial", Font.PLAIN, 13));
        productType.setBackground(pastelPurple);
        productType.setForeground(darkPurple);
        productType.addActionListener(event -> actualizarCamposExtra());
        panel.add(productType);

        crearEtiqueta("Id:", 35, 122, panel);
        idField = crearCampo(155, 122, panel);

        crearEtiqueta("Nombre:", 35, 166, panel);
        nameField = crearCampo(155, 166, panel);

        crearEtiqueta("Precio:", 35, 210, panel);
        priceField = crearCampo(155, 210, panel);

        crearEtiqueta("Stock:", 35, 254, panel);
        stockField = crearCampo(155, 254, panel);

        extraLabel1 = crearEtiqueta("Color:", 35, 298, panel);
        extraField1 = crearCampo(155, 298, panel);

        extraLabel2 = crearEtiqueta("Tamano:", 35, 342, panel);
        extraField2 = crearCampo(155, 342, panel);

        JButton addButton = crearBoton("Registrar producto");
        addButton.setBounds(55, 405, 165, 38);
        addButton.addActionListener(event -> registrarProducto());
        panel.add(addButton);

        JButton clearButton = crearBoton("Limpiar");
        clearButton.setBounds(235, 405, 120, 38);
        clearButton.addActionListener(event -> limpiarFormulario());
        panel.add(clearButton);

        actualizarCamposExtra();
        return panel;
    }

    // Panel derecho: muestra la tabla y los botones para eliminar o actualizar stock.
    private JPanel crearListadoProductos() {
        JPanel panel = crearPanelDecorado();
        panel.setLayout(new BorderLayout(12, 12));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(markerBlack, 3),
            BorderFactory.createEmptyBorder(22, 22, 22, 22)
        ));

        JLabel title = new JLabel("Productos registrados");
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setForeground(white);
        panel.add(title, BorderLayout.NORTH);

        // Modelo de la tabla: define columnas y evita que el usuario edite celdas directamente.
        productsModel = new DefaultTableModel(
            new String[] {"ID", "Nombre", "Tipo", "Precio", "Stock", "Detalle"},
            0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        productsTable = new JTable(productsModel);
        productsTable.setFont(new Font("Arial", Font.PLAIN, 13));
        productsTable.setRowHeight(26);
        productsTable.setShowGrid(true);
        productsTable.setGridColor(new Color(200, 185, 215));
        productsTable.setBackground(pastelPurple);
        productsTable.setForeground(darkPurple);
        productsTable.setSelectionBackground(activePurple);
        productsTable.setSelectionForeground(white);
        productsTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        JTableHeader tableHeader = productsTable.getTableHeader();
        tableHeader.setFont(new Font("Arial", Font.BOLD, 13));
        tableHeader.setBackground(darkPurple);
        tableHeader.setForeground(white);
        tableHeader.setReorderingAllowed(false);

        productsTable.getColumnModel().getColumn(0).setPreferredWidth(55);
        productsTable.getColumnModel().getColumn(1).setPreferredWidth(150);
        productsTable.getColumnModel().getColumn(2).setPreferredWidth(95);
        productsTable.getColumnModel().getColumn(3).setPreferredWidth(75);
        productsTable.getColumnModel().getColumn(4).setPreferredWidth(65);
        productsTable.getColumnModel().getColumn(5).setPreferredWidth(280);

        JScrollPane scrollPane = new JScrollPane(productsTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(markerBlack, 2));
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getViewport().setBackground(pastelPurple);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel actions = new JPanel(new GridLayout(1, 2, 12, 0));
        actions.setOpaque(false);

        JButton deleteButton = crearBoton("Eliminar");
        deleteButton.addActionListener(event -> eliminarProducto());
        actions.add(deleteButton);

        JButton stockButton = crearBoton("Actualizar stock");
        stockButton.addActionListener(event -> controlarStock());
        actions.add(stockButton);

        panel.add(actions, BorderLayout.SOUTH);
        return panel;
    }

    // Barra inferior con el boton de cerrar sesion.
    private JPanel crearBarraInferior() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        JButton logoutButton = crearBoton("Cerrar sesion");
        logoutButton.setPreferredSize(new Dimension(160, 38));
        logoutButton.addActionListener(event -> cerrarSesion());
        panel.add(logoutButton, BorderLayout.EAST);

        return panel;
    }

    // Crea paneles con fondo morado y borde negro, usados como contenedores principales.
    private JPanel crearPanelDecorado() {
        JPanel panel = new JPanel();
        panel.setBackground(new Color(70, 43, 101, 232));
        panel.setBorder(BorderFactory.createLineBorder(markerBlack, 3));
        return panel;
    }

    // Crea etiquetas del formulario con posicion absoluta.
    private JLabel crearEtiqueta(String text, int x, int y, JPanel panel) {
        JLabel label = new JLabel(text);
        label.setBounds(x, y, 110, 28);
        label.setFont(new Font("Arial", Font.BOLD, 13));
        label.setForeground(white);
        panel.add(label);
        return label;
    }

    // Crea campos de texto del formulario con el mismo estilo visual.
    private JTextField crearCampo(int x, int y, JPanel panel) {
        JTextField field = new JTextField();
        field.setBounds(x, y, 230, 30);
        field.setFont(new Font("Arial", Font.PLAIN, 13));
        field.setBackground(pastelPurple);
        field.setForeground(darkPurple);
        field.setBorder(BorderFactory.createLineBorder(markerBlack, 2));
        panel.add(field);
        return field;
    }

    // Centraliza el estilo de todos los botones de esta ventana.
    private JButton crearBoton(String text) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setForeground(white);
        button.setBackground(activePurple);
        button.setFont(new Font("Arial", Font.BOLD, 13));
        button.setBorder(BorderFactory.createLineBorder(darkPurple, 2));
        return button;
    }

    // Busca y escala el logo pequeno que aparece en el encabezado.
    private ImageIcon cargarLogoPequeno() {
        String[] paths = {
            "src/Assets/inkurban-logo-cutout.png",
            "InkUrban/src/Assets/inkurban-logo-cutout.png",
            "src/Assets/inkurban-logo-clean.png",
            "InkUrban/src/Assets/inkurban-logo-clean.png",
            "src/Assets/inkurban-logo.png",
            "InkUrban/src/Assets/inkurban-logo.png"
        };

        for (String path : paths) {
            File file = new File(path);
            if (file.exists()) {
                ImageIcon icon = new ImageIcon(file.getAbsolutePath());
                if (icon.getIconWidth() > 0) {
                    Image image = icon.getImage().getScaledInstance(82, 54, Image.SCALE_SMOOTH);
                    return new ImageIcon(image);
                }
            }
        }
        return null;
    }

    // Cambia los campos extra: Spray/Marcador usan color y tamano; Tapa usa tipo de tapa.
    private void actualizarCamposExtra() {
        boolean isCap = productType.getSelectedIndex() == 2;
        extraLabel1.setText(isCap ? "Tipo tapa:" : "Color:");
        extraLabel2.setVisible(!isCap);
        extraField2.setVisible(!isCap);
    }

    // Lee el formulario, crea el objeto de producto correcto y lo agrega al catalogo.
    private void registrarProducto() {
        try {
            Product1 product = null;
            String selectedType = (String) productType.getSelectedItem();

            if ("Spray".equals(selectedType)) {
                product = new Spray(
                    idField.getText(),
                    nameField.getText(),
                    priceField.getText(),
                    stockField.getText(),
                    extraField1.getText(),
                    extraField2.getText()
                );
            } else if ("Marcador".equals(selectedType)) {
                product = new Marker(
                    idField.getText(),
                    nameField.getText(),
                    priceField.getText(),
                    stockField.getText(),
                    extraField1.getText(),
                    extraField2.getText()
                );
            } else {
                product = new SprayCap(
                    idField.getText(),
                    nameField.getText(),
                    priceField.getText(),
                    stockField.getText(),
                    extraField1.getText()
                );
            }

            if (productIdExists(product.getIdProduct())) {
                throw new IllegalArgumentException("Ya existe un producto con ese id");
            }

            products.add(product);
            admin.addProduct(product);
            limpiarFormulario();
            refrescarProductos();
            JOptionPane.showMessageDialog(this, "Producto registrado correctamente", "Producto", JOptionPane.INFORMATION_MESSAGE);
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Solicita un ID y elimina el producto si existe y no esta dentro de ningun carrito.
    private void eliminarProducto() {
        try {
            String idProduct = JOptionPane.showInputDialog(this, "Ingrese el id del producto que va a eliminar");
            if (idProduct == null) {
                return;
            }

            Product1 product = searchProduct(idProduct);
            if (product != null) {
                if (userStore.productIsInAnyCart(product)) {
                    throw new IllegalArgumentException("No se puede eliminar un producto que esta en un carrito");
                }
                products.remove(product);
                admin.deleteProduct(product);
                refrescarProductos();
                JOptionPane.showMessageDialog(this, "Producto eliminado correctamente", "Producto", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Producto no encontrado", "Producto", JOptionPane.WARNING_MESSAGE);
            }
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Solicita un ID y un nuevo stock para actualizar un producto existente.
    private void controlarStock() {
        try {
            String idProduct = JOptionPane.showInputDialog(this, "Ingrese el id del producto");
            if (idProduct == null) {
                return;
            }

            Product1 product = searchProduct(idProduct);
            if (product != null) {
                String newStock = JOptionPane.showInputDialog(this, "Ingrese el nuevo stock");
                if (newStock == null) {
                    return;
                }
                admin.controllStock(product, newStock);
                refrescarProductos();
                JOptionPane.showMessageDialog(this, "Stock actualizado correctamente", "Stock", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Producto no encontrado", "Stock", JOptionPane.WARNING_MESSAGE);
            }
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Busca un producto en la lista general usando el ID escrito por el usuario.
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

    // Verifica que no se repita el ID antes de registrar un nuevo producto.
    private boolean productIdExists(int idProduct) {
        for (Product1 product : products) {
            if (product.getIdProduct() == idProduct) {
                return true;
            }
        }
        return false;
    }

    // Limpia y vuelve a cargar la tabla para que refleje el estado actual de products.
    private void refrescarProductos() {
        productsModel.setRowCount(0);

        if (products.isEmpty()) {
            productsModel.addRow(new Object[] {"", "No hay productos registrados", "", "", "", ""});
            return;
        }

        for (Product1 product : products) {
            productsModel.addRow(new Object[] {
                product.getIdProduct(),
                product.getNameProduct(),
                obtenerTipoProducto(product),
                String.format("%.2f", product.getPrice()),
                product.getStock(),
                obtenerDetalleProducto(product)
            });
        }
    }

    // Convierte la clase real del producto en un texto entendible para la tabla.
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

    // Construye la columna "Detalle" segun los atributos propios de cada tipo de producto.
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

    // Borra todos los campos despues de registrar o cuando el admin presiona "Limpiar".
    private void limpiarFormulario() {
        idField.setText("");
        nameField.setText("");
        priceField.setText("");
        stockField.setText("");
        extraField1.setText("");
        extraField2.setText("");
    }

    // Cierra la ventana de administrador y regresa al menu principal.
    private void cerrarSesion() {
        admin.logout();
        dispose();
        mainWindow.setVisible(true);
        mainWindow.setExtendedState(JFrame.MAXIMIZED_BOTH);
    }

    // Fondo personalizado de esta ventana: muro, lineas y graffitis decorativos.
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
            String[] paths = {
                "src/Assets/" + fileName,
                "InkUrban/src/Assets/" + fileName
            };

            for (String path : paths) {
                File file = new File(path);
                if (file.exists()) {
                    ImageIcon icon = new ImageIcon(file.getAbsolutePath());
                    if (icon.getIconWidth() > 0) {
                        return icon.getImage();
                    }
                }
            }
            return null;
        }

        private void dibujarImagen(Graphics2D g2, Image image, int x, int y, int maxWidth, int maxHeight, float opacity) {
            if (image == null) {
                return;
            }

            int originalWidth = image.getWidth(null);
            int originalHeight = image.getHeight(null);
            if (originalWidth <= 0 || originalHeight <= 0) {
                return;
            }

            double scale = Math.min((double) maxWidth / originalWidth, (double) maxHeight / originalHeight);
            int width = Math.max(1, (int) Math.round(originalWidth * scale));
            int height = Math.max(1, (int) Math.round(originalHeight * scale));

            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
            g2.drawImage(image, x, y, width, height, null);
            g2.setComposite(AlphaComposite.SrcOver);
        }
    }
}
