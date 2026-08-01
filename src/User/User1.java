package User;

abstract class User1 {
    // Nombre de usuario usado para iniciar sesion.
    private String name;
    // Identificacion numerica del usuario.
    private int id;
    // Correo de contacto del usuario.
    private String mail;
    // Contrasena del usuario.
    private String password;

    // Constructor base para administradores y clientes.
    public User1(String name, String id, String mail, String password) {
        setName(name);
        setId(id);
        setMail(mail);
        setPassword(password);
    }

    // Valida y guarda el nombre del usuario.
    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Este espacio no puede estar vacio");
        }
        this.name = name.trim();
    }

    // Valida y guarda el ID como numero positivo.
    public void setId(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("El ID no puede estar vacio");
        }
        String cleanId = id.trim();
        if (cleanId.startsWith("-")) {
            throw new IllegalArgumentException("No se permiten numeros negativos");
        }
        if (!cleanId.matches("\\d+")) {
            throw new IllegalArgumentException("El ID solo debe contener numeros");
        }
        try {
            this.id = Integer.parseInt(cleanId);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("El ID es demasiado grande");
        }
    }

    // Valida un formato basico de correo y lo guarda.
    public void setMail(String mail) {
        if (mail == null || !mail.contains("@") || !mail.contains(".")) {
            throw new IllegalArgumentException("Correo invalido: \"" + mail + "\" (formato esperado: usuario@dominio.com)");
        }
        this.mail = mail.trim();
    }

    // Valida que la contrasena no este vacia y tenga minimo 8 caracteres.
    public void setPassword(String password) {
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Este espacio no puede estar vacio");
        }
        if (password.length() < 8) {
            throw new IllegalArgumentException("La contrasena debe tener minimo 8 caracteres");
        }
        this.password = password.trim();
    }

    // Representacion general del usuario sin exponer la contrasena.
    @Override
    public String toString() {
        return String.format("%s | ID: %d | Correo: %s", name, id, mail);
    }

    // Verifica usuario y contrasena antes de iniciar sesion.
    public void login(String nameIngresado, String passwordIngresado) {
        if (nameIngresado == null || nameIngresado.trim().isEmpty()) {
            throw new IllegalArgumentException("Ingrese el usuario");
        }
        if (passwordIngresado == null || passwordIngresado.trim().isEmpty()) {
            throw new IllegalArgumentException("Ingrese la contrasena");
        }
        if (!nameIngresado.trim().equals(name)) {
            throw new IllegalArgumentException("Usuario no encontrado");
        }
        if (!passwordIngresado.trim().equals(password)) {
            throw new IllegalArgumentException("Contrasena incorrecta");
        }
        System.out.println("Iniciando sesion, bienvenido " + name);
    }

    // Mensaje comun para cerrar sesion.
    public void logout() {
        System.out.println("Sesion cerrada correctamente");
    }

    // Retorna el nombre del usuario.
    public String getName() {
        return name;
    }

    // Retorna el ID del usuario.
    public int getId() {
        return id;
    }

    // Retorna el correo del usuario.
    public String getMail() {
        return mail;
    }
}
