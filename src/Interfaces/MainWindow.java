package Interfaces;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;

import javax.swing.*;

import Product.Product1;
import User.*;

/*
 * Ventana principal de InkUrban.
 * Aqui se muestra la primera pantalla del programa: logo, subtitulo y botones
 * para entrar como administrador, entrar como cliente o salir de la aplicacion.
 */
public class MainWindow extends JFrame implements ActionListener {

    private static final long serialVersionUID = 1L;

    // Botones y elementos visuales que aparecen en el menu principal.
    private JButton adminButton;
    private JButton customerButton;
    private JButton exitButton;
    private JLabel logoLabel;
    private JLabel subtitle;
    private JPanel optionsPanel;

    // Datos compartidos con las otras ventanas: productos disponibles y usuarios registrados.
    private transient List<Product1> products;
    private transient UserStore userStore;

    // Paleta de colores usada para mantener el estilo visual de InkUrban.
    private final Color backgroundPurple = new Color(37, 21, 54);
    private final Color panelPurple = new Color(70, 43, 101);
    private final Color activePurple = new Color(138, 82, 214);
    private final Color darkPurple = new Color(25, 16, 35);
    private final Color white = new Color(245, 242, 250);
    private final Color pastelPurple = new Color(241, 232, 250);

    @SuppressWarnings("this-escape")
    public MainWindow(List<Product1> products, UserStore userStore) {
        this.products = products;
        this.userStore = userStore;

        setTitle("InkUrban");
        setSize(560, 720);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(true);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        // Panel de fondo: ocupa toda la pantalla y dibuja el muro/graffiti.
        JPanel content = new GraffitiPanel();
        content.setLayout(null);
        content.setBackground(backgroundPurple);
        add(content, BorderLayout.CENTER);

        ImageIcon logoIcon = cargarLogo();
        if (logoIcon != null) {
            setIconImage(logoIcon.getImage());
        }

        // Logo principal, ubicado en la parte superior central de la ventana.
        logoLabel = new JLabel("", SwingConstants.CENTER);
        logoLabel.setOpaque(false);
        logoLabel.setBackground(new Color(0, 0, 0, 0));
        logoLabel.setBounds(25, 18, 510, 350);
        if (logoIcon != null) {
            Image logoSinFondoGris = limpiarFondoGris(logoIcon.getImage());
            logoLabel.setIcon(new ImageIcon(escalarImagenProporcional(logoSinFondoGris, 510, 350)));
        } else {
            logoLabel.setText("INKURBAN");
            logoLabel.setFont(new Font("Arial", Font.BOLD, 32));
            logoLabel.setForeground(white);
        }
        content.add(logoLabel);

        subtitle = new JLabel("Arte callejero", SwingConstants.CENTER);
        subtitle.setBounds(75, 372, 410, 28);
        subtitle.setFont(new Font("Arial", Font.BOLD | Font.ITALIC, 16));
        subtitle.setForeground(white);
        content.add(subtitle);

        // Panel central con borde estilo marcador; dentro van los botones de entrada.
        optionsPanel = new MarkerPanel();
        optionsPanel.setLayout(null);
        optionsPanel.setBounds(125, 420, 310, 235);
        optionsPanel.setBackground(panelPurple);
        optionsPanel.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
        content.add(optionsPanel);

        Font buttonFont = new Font("Arial", Font.BOLD, 14);

        adminButton = crearBoton("Iniciar Sesion Admin", buttonFont);
        adminButton.setBounds(45, 32, 220, 44);
        optionsPanel.add(adminButton);

        customerButton = crearBoton("Cliente", buttonFont);
        customerButton.setBounds(45, 96, 220, 44);
        optionsPanel.add(customerButton);

        exitButton = crearBoton("Salir", buttonFont);
        exitButton.setBounds(45, 160, 220, 44);
        optionsPanel.add(exitButton);

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                actualizarLayoutPrincipal();
            }
        });

        setLocationRelativeTo(null);
    }

    // Recalcula posiciones cuando la ventana cambia de tamano para mantener todo centrado.
    private void actualizarLayoutPrincipal() {
        int width = getContentPane().getWidth();
        int height = getContentPane().getHeight();
        int centroX = width / 2;
        int inicioY = Math.max(28, (height - 675) / 2);

        logoLabel.setBounds(centroX - 255, inicioY, 510, 350);
        subtitle.setBounds(centroX - 205, inicioY + 354, 410, 28);
        optionsPanel.setBounds(centroX - 155, inicioY + 402, 310, 235);
    }

    // Busca el logo en Assets usando varios nombres posibles.
    private ImageIcon cargarLogo() {
        String[] fileNames = {
            "inkurban-logo-cutout.png",
            "inkurban-logo-clean.png",
            "inkurban-logo.png"
        };

        for (String fileName : fileNames) {
            ImageIcon icon = cargarIconoAsset(fileName);
            if (icon != null) {
                return icon;
            }
        }
        return null;
    }

    // Recorre rutas posibles porque el proyecto puede ejecutarse desde carpetas distintas.
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
                if (icon.getIconWidth() > 0) {
                    return icon;
                }
            }
        }
        return null;
    }

    // Ajusta una imagen sin deformarla para que quepa en el espacio indicado.
    private Image escalarImagenProporcional(Image imageLabel, int maxWidth, int maxHeight) {
        int originalWidth = imageLabel.getWidth(null);
        int originalHeight = imageLabel.getHeight(null);

        if (originalWidth <= 0 || originalHeight <= 0) {
            return imageLabel;
        }

        double scale = Math.min(
            (double) maxWidth / originalWidth,
            (double) maxHeight / originalHeight
        );
        int width = Math.max(1, (int) Math.round(originalWidth * scale));
        int height = Math.max(1, (int) Math.round(originalHeight * scale));

        BufferedImage escalada = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = escalada.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.drawImage(imageLabel, 0, 0, width, height, null);
        g2.dispose();
        return escalada;
    }

    // Convierte pixeles grises del logo en transparentes para integrarlo mejor al fondo.
    private Image limpiarFondoGris(Image image) {
        int width = image.getWidth(null);
        int height = image.getHeight(null);

        if (width <= 0 || height <= 0) {
            return image;
        }

        BufferedImage source = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = source.createGraphics();
        g2.drawImage(image, 0, 0, null);
        g2.dispose();

        BufferedImage clean = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int argb = source.getRGB(x, y);
                int alpha = (argb >> 24) & 0xff;
                int red = (argb >> 16) & 0xff;
                int green = (argb >> 8) & 0xff;
                int blue = argb & 0xff;
                int max = Math.max(red, Math.max(green, blue));
                int min = Math.min(red, Math.min(green, blue));
                boolean isNeutralGray = max - min <= 14 && max >= 48 && max <= 135;

                if (alpha < 25 || isNeutralGray) {
                    clean.setRGB(x, y, 0x00000000);
                } else {
                    clean.setRGB(x, y, argb);
                }
            }
        }
        return clean;
    }

    // Crea botones con el mismo estilo y los conecta al actionPerformed de esta ventana.
    private JButton crearBoton(String text, Font font) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setForeground(white);
        button.setBackground(activePurple);
        button.setFont(font);
        button.setBorder(BorderFactory.createLineBorder(darkPurple, 2));
        button.addActionListener(this);
        return button;
    }

    // Crea una etiqueta de formulario en una posicion fija dentro del panel recibido.
    private JLabel crearEtiqueta(String text, int x, int y, JPanel panel) {
        JLabel label = new JLabel(text);
        label.setBounds(x, y, 125, 28);
        label.setFont(new Font("Arial", Font.BOLD, 13));
        label.setForeground(white);
        panel.add(label);
        return label;
    }

    // Crea campos de texto reutilizados en login y registro.
    private JTextField crearCampoTexto(int x, int y, JPanel panel) {
        JTextField field = new JTextField();
        field.setBounds(x, y, 190, 30);
        field.setFont(new Font("Arial", Font.PLAIN, 13));
        field.setBackground(pastelPurple);
        field.setForeground(darkPurple);
        field.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        panel.add(field);
        return field;
    }

    // Crea campos de contrasena que ocultan el texto ingresado.
    private JPasswordField crearCampoPassword(int x, int y, JPanel panel) {
        JPasswordField field = new JPasswordField();
        field.setBounds(x, y, 190, 30);
        field.setFont(new Font("Arial", Font.PLAIN, 13));
        field.setBackground(pastelPurple);
        field.setForeground(darkPurple);
        field.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        panel.add(field);
        return field;
    }

    // Checkbox que permite mostrar u ocultar la contrasena en los formularios.
    private JCheckBox crearOpcionVerPassword(JPasswordField passwordField, int x, int y, JPanel panel) {
        JCheckBox checkBox = new JCheckBox("Ver contrasena");
        char echoChar = passwordField.getEchoChar();
        checkBox.setBounds(x, y, 140, 24);
        checkBox.setFont(new Font("Arial", Font.BOLD, 12));
        checkBox.setForeground(white);
        checkBox.setOpaque(false);
        checkBox.setFocusPainted(false);
        checkBox.addActionListener(event -> {
            if (checkBox.isSelected()) {
                passwordField.setEchoChar((char) 0);
            } else {
                passwordField.setEchoChar(echoChar);
            }
        });
        panel.add(checkBox);
        return checkBox;
    }

    // Crea ventanas emergentes modales para login y registro.
    private JDialog crearDialogo(String title, int width, int height) {
        JDialog dialog = new JDialog(this, title, true);
        dialog.setSize(width, height);
        dialog.setLayout(new BorderLayout());
        dialog.setResizable(false);
        dialog.setLocationRelativeTo(this);
        return dialog;
    }

    // Base visual de los formularios emergentes.
    private JPanel crearPanelFormulario() {
        JPanel panel = new MarkerPanel();
        panel.setLayout(null);
        panel.setBackground(panelPurple);
        panel.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
        return panel;
    }

    // Panel personalizado que dibuja un borde irregular, como hecho con marcador.
    private class MarkerPanel extends JPanel {
        private static final long serialVersionUID = 1L;

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Color.BLACK);
            g2.setStroke(new BasicStroke(5, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            int w = getWidth() - 7;
            int h = getHeight() - 7;
            g2.drawRoundRect(3, 3, w, h, 12, 12);
            g2.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.drawLine(8, 7, getWidth() - 18, 4);
            g2.drawLine(5, getHeight() - 12, getWidth() - 10, getHeight() - 7);
            g2.dispose();
        }
    }

    // Panel de fondo que pinta el muro y las imagenes de graffiti.
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

            g2.setPaint(new GradientPaint(0, 0, darkPurple, getWidth(), getHeight(), new Color(8, 8, 12)));
            g2.fillRect(0, 0, getWidth(), getHeight());

            dibujarMuro(g2);
            dibujarGrafitis(g2);

            g2.dispose();
        }

        private void dibujarMuro(Graphics2D g2) {
            g2.setPaint(new GradientPaint(0, 0, new Color(76, 65, 91), getWidth(), getHeight(), new Color(31, 24, 39)));
            g2.fillRect(0, 0, getWidth(), getHeight());

            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.18f));
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
            dibujarImagen(g2, graffitiFrame, -55, -64, getWidth() + 110, getHeight() + 128, 0.86f);
            dibujarImagen(g2, graffitiSignature, -95, 90, Math.max(360, getWidth() / 3), 360, 0.58f);
            dibujarImagen(g2, graffitiDrip, getWidth() - 315, 42, 315, 420, 0.58f);
            dibujarImagen(g2, graffitiSignature, getWidth() - 485, getHeight() - 345, 455, 410, 0.44f);
            dibujarImagen(g2, graffitiBlock, getWidth() / 2 - 245, getHeight() - 285, 490, 325, 0.36f);
            dibujarImagen(g2, graffitiDrip, 55, getHeight() - 315, 245, 335, 0.34f);
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

            double scale = Math.min(
                (double) maxWidth / originalWidth,
                (double) maxHeight / originalHeight
            );
            int width = Math.max(1, (int) Math.round(originalWidth * scale));
            int height = Math.max(1, (int) Math.round(originalHeight * scale));

            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
            g2.drawImage(imageLabel, x, y, width, height, null);
            g2.setComposite(AlphaComposite.SrcOver);
        }
    }

    // Abre el formulario de administrador y, si las credenciales son correctas, carga AdminWindow.
    private void mostrarLoginAdmin() {
        JDialog dialog = crearDialogo("Inicio de sesion administrador", 390, 285);
        JPanel panel = crearPanelFormulario();
        dialog.add(panel, BorderLayout.CENTER);

        JLabel title = new JLabel("Administrador", SwingConstants.CENTER);
        title.setBounds(35, 25, 310, 30);
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setForeground(white);
        panel.add(title);

        crearEtiqueta("Usuario:", 45, 80, panel);
        JTextField userField = crearCampoTexto(145, 80, panel);

        crearEtiqueta("Contrasena:", 45, 125, panel);
        JPasswordField passwordField = crearCampoPassword(145, 125, panel);
        crearOpcionVerPassword(passwordField, 145, 155, panel);

        JButton loginButton = crearBoton("Iniciar Sesion", new Font("Arial", Font.BOLD, 13));
        loginButton.setBounds(70, 185, 125, 34);
        panel.add(loginButton);

        JButton cancelButton = crearBoton("Cancelar", new Font("Arial", Font.BOLD, 13));
        cancelButton.setBounds(205, 185, 105, 34);
        panel.add(cancelButton);

        loginButton.addActionListener(event -> {
            try {
                String adminName = userField.getText();
                String adminPassword = new String(passwordField.getPassword());
                Admin admin = new Admin("Administrador", "5284628", "administrador5@inkurban.com", "admin123");
                admin.login(adminName, adminPassword);
                dialog.dispose();
                setVisible(false);
                new AdminWindow(admin, products, this, userStore).setVisible(true);
            } catch (IllegalArgumentException e) {
                JOptionPane.showMessageDialog(dialog, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(event -> dialog.dispose());
        dialog.setVisible(true);
    }

    // Abre el registro de cliente; al registrarse crea el Customer y entra a CustomerWindow.
    private void mostrarRegistroCliente() {
        JDialog dialog = crearDialogo("Registro de cliente", 430, 395);
        JPanel panel = crearPanelFormulario();
        dialog.add(panel, BorderLayout.CENTER);

        JLabel title = new JLabel("Registro Cliente", SwingConstants.CENTER);
        title.setBounds(45, 25, 330, 30);
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setForeground(white);
        panel.add(title);

        crearEtiqueta("Nombre:", 45, 80, panel);
        JTextField nameField = crearCampoTexto(165, 80, panel);

        crearEtiqueta("Id:", 45, 125, panel);
        JTextField idField = crearCampoTexto(165, 125, panel);

        crearEtiqueta("Correo:", 45, 170, panel);
        JTextField mailField = crearCampoTexto(165, 170, panel);

        crearEtiqueta("Contrasena:", 45, 215, panel);
        JPasswordField passwordField = crearCampoPassword(165, 215, panel);
        crearOpcionVerPassword(passwordField, 165, 245, panel);

        JButton registerButton = crearBoton("Registrar e Iniciar", new Font("Arial", Font.BOLD, 13));
        registerButton.setBounds(70, 285, 150, 34);
        panel.add(registerButton);

        JButton cancelButton = crearBoton("Cancelar", new Font("Arial", Font.BOLD, 13));
        cancelButton.setBounds(235, 285, 105, 34);
        panel.add(cancelButton);

        registerButton.addActionListener(event -> {
            try {
                String name = nameField.getText();
                String id = idField.getText();
                String mail = mailField.getText();
                String password = new String(passwordField.getPassword());
                Customer customer = userStore.registerCustomer(name, id, mail, password);
                dialog.dispose();
                setVisible(false);
                new CustomerWindow(customer, products, this).setVisible(true);
            } catch (IllegalArgumentException e) {
                JOptionPane.showMessageDialog(dialog, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(event -> dialog.dispose());
        dialog.setVisible(true);
    }

    // Ventana intermedia del cliente: permite elegir entre iniciar sesion o registrarse.
    private void mostrarOpcionesCliente() {
        JDialog dialog = crearDialogo("Cliente InkUrban", 390, 300);
        JPanel panel = crearPanelFormulario();
        dialog.add(panel, BorderLayout.CENTER);

        JLabel title = new JLabel("Cliente", SwingConstants.CENTER);
        title.setBounds(45, 28, 300, 32);
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setForeground(white);
        panel.add(title);

        JButton loginButton = crearBoton("Iniciar Sesion", new Font("Arial", Font.BOLD, 13));
        loginButton.setBounds(90, 86, 210, 40);
        panel.add(loginButton);

        JButton registerButton = crearBoton("Registrarme", new Font("Arial", Font.BOLD, 13));
        registerButton.setBounds(90, 142, 210, 40);
        panel.add(registerButton);

        JButton cancelButton = crearBoton("Cancelar", new Font("Arial", Font.BOLD, 13));
        cancelButton.setBounds(130, 202, 130, 36);
        panel.add(cancelButton);

        loginButton.addActionListener(event -> {
            dialog.dispose();
            mostrarLoginCliente();
        });

        registerButton.addActionListener(event -> {
            dialog.dispose();
            mostrarRegistroCliente();
        });

        cancelButton.addActionListener(event -> dialog.dispose());
        dialog.setVisible(true);
    }

    // Login de cliente: busca el usuario por id y valida usuario/contrasena.
    private void mostrarLoginCliente() {
        JDialog dialog = crearDialogo("Inicio de sesion cliente", 390, 330);
        JPanel panel = crearPanelFormulario();
        dialog.add(panel, BorderLayout.CENTER);

        JLabel title = new JLabel("Iniciar Sesion Cliente", SwingConstants.CENTER);
        title.setBounds(35, 25, 310, 30);
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setForeground(white);
        panel.add(title);

        crearEtiqueta("Id:", 45, 82, panel);
        JTextField idField = crearCampoTexto(145, 82, panel);

        crearEtiqueta("Usuario:", 45, 127, panel);
        JTextField userField = crearCampoTexto(145, 127, panel);

        crearEtiqueta("Contrasena:", 45, 172, panel);
        JPasswordField passwordField = crearCampoPassword(145, 172, panel);
        crearOpcionVerPassword(passwordField, 145, 202, panel);

        JButton loginButton = crearBoton("Iniciar Sesion", new Font("Arial", Font.BOLD, 13));
        loginButton.setBounds(70, 235, 125, 34);
        panel.add(loginButton);

        JButton cancelButton = crearBoton("Cancelar", new Font("Arial", Font.BOLD, 13));
        cancelButton.setBounds(205, 235, 105, 34);
        panel.add(cancelButton);

        loginButton.addActionListener(event -> {
            try {
                Customer customer = userStore.findCustomerById(idField.getText());
                if (customer == null) {
                    throw new IllegalArgumentException("Cliente no encontrado");
                }
                customer.login(userField.getText(), new String(passwordField.getPassword()));
                dialog.dispose();
                setVisible(false);
                new CustomerWindow(customer, products, this).setVisible(true);
            } catch (IllegalArgumentException e) {
                JOptionPane.showMessageDialog(dialog, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(event -> dialog.dispose());
        dialog.setVisible(true);
    }

    @Override
    // Detecta cual boton del menu principal fue presionado y ejecuta la accion correspondiente.
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == adminButton) {
            mostrarLoginAdmin();
        } else if (e.getSource() == customerButton) {
            mostrarOpcionesCliente();
        } else if (e.getSource() == exitButton) {
            dispose();
        }
    }
}
