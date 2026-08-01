package Payment;

import java.time.YearMonth;

import Order.Order1;
import Product.Product1;
import Shipping.Shipping1;
import ShoppingCar.ShoppingCar1;

public class Payment1 {
    // Identificador del pago realizado por el cliente.
    private int idPay;
    // Metodo elegido: efectivo, tarjeta debito, tarjeta credito o transferencia.
    private String paymentMethod;

    // Constructor: valida y guarda los datos principales del pago.
    @SuppressWarnings("this-escape")
    public Payment1(String idPay, String paymentMethod) {
        setIdPay(idPay);
        setPaymentMethod(paymentMethod);
    }

    // Valida que el ID del pago sea numerico y positivo.
    public void setIdPay(String idPay) {
        if (idPay == null || idPay.trim().isEmpty()) {
            throw new IllegalArgumentException("El id del pago no puede estar vacio");
        }
        String cleanId = idPay.trim();
        if (cleanId.startsWith("-")) {
            throw new IllegalArgumentException("No se permiten numeros negativos");
        }
        if (!cleanId.matches("\\d+")) {
            throw new IllegalArgumentException("El id del pago solo puede contener numeros");
        }
        try {
            this.idPay = Integer.parseInt(cleanId);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("El id del pago es demasiado grande");
        }
    }

    // Valida que el metodo de pago pertenezca a las opciones aceptadas.
    public void setPaymentMethod(String paymentMethod) {
        if (paymentMethod == null || paymentMethod.trim().isEmpty()) {
            throw new IllegalArgumentException("Este espacio no puede estar vacio");
        }
        String cleanPaymentMethod = paymentMethod.trim();
        if (!cleanPaymentMethod.equals("Efectivo")
                && !cleanPaymentMethod.equals("Tarjeta de debito")
                && !cleanPaymentMethod.equals("Tarjeta de credito")
                && !cleanPaymentMethod.equals("Transferencia")) {
            throw new IllegalArgumentException("Los metodos de pago aceptados son: Efectivo, Tarjeta de debito, Tarjeta de credito y Transferencia");
        }
        this.paymentMethod = cleanPaymentMethod;
    }

    // Simula el inicio del procesamiento del pago.
    public void processingPay() {
        System.out.println("Procesando el pago. Esta accion tardara unos segundos");
    }

    // Simula la confirmacion exitosa del pago.
    public void validatePay() {
        System.out.println("Pago realizado con exito");
    }

    // Agrupa todas las validaciones necesarias para pagos con tarjeta.
    public static void validateCardPayment(String cardHolder, String cardNumber, String expirationDate, String securityCode) {
        validateCardHolder(cardHolder);
        validateCardNumber(cardNumber);
        validateExpirationDate(expirationDate);
        validateSecurityCode(securityCode);
    }

    // Agrupa todas las validaciones necesarias para pagos por transferencia.
    public static void validateTransferPayment(String bank, String accountOrPhone, String reference) {
        validateBank(bank);
        validateAccountOrPhone(accountOrPhone);
        validateTransferReference(reference);
    }

    // Valida que el nombre del titular tenga solo letras y espacios.
    private static void validateCardHolder(String cardHolder) {
        if (cardHolder == null || cardHolder.trim().isEmpty()) {
            throw new IllegalArgumentException("Ingrese el nombre del titular");
        }
        if (!cardHolder.trim().matches("[A-Za-z ]{3,}")) {
            throw new IllegalArgumentException("El nombre del titular solo debe contener letras y espacios");
        }
    }

    // Valida que la tarjeta tenga exactamente 16 digitos numericos.
    private static void validateCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("El numero de tarjeta no puede estar vacio");
        }
        String cleanCardNumber = cardNumber.trim();
        if (cleanCardNumber.startsWith("-")) {
            throw new IllegalArgumentException("No se permiten numeros negativos");
        }
        if (!cleanCardNumber.matches("\\d+")) {
            throw new IllegalArgumentException("El numero de tarjeta solo debe contener numeros");
        }
        if (cleanCardNumber.length() != 16) {
            throw new IllegalArgumentException("El numero de tarjeta debe tener exactamente 16 digitos");
        }
    }

    // Valida el formato MM/AA y verifica que la tarjeta no este vencida.
    private static void validateExpirationDate(String expirationDate) {
        if (expirationDate == null || expirationDate.trim().isEmpty()) {
            throw new IllegalArgumentException("La fecha de vencimiento no puede estar vacia");
        }
        String cleanExpirationDate = expirationDate.trim();
        if (!cleanExpirationDate.matches("(0[1-9]|1[0-2])/\\d{2}")) {
            throw new IllegalArgumentException("La fecha de vencimiento debe tener formato MM/AA");
        }
        String[] parts = cleanExpirationDate.split("/");
        int month = Integer.parseInt(parts[0]);
        int year = 2000 + Integer.parseInt(parts[1]);
        if (YearMonth.of(year, month).isBefore(YearMonth.now())) {
            throw new IllegalArgumentException("La tarjeta esta vencida");
        }
    }

    // Valida que el codigo de seguridad tenga 3 o 4 digitos.
    private static void validateSecurityCode(String securityCode) {
        if (securityCode == null || securityCode.trim().isEmpty()) {
            throw new IllegalArgumentException("El codigo de seguridad no puede estar vacio");
        }
        String cleanSecurityCode = securityCode.trim();
        if (cleanSecurityCode.startsWith("-")) {
            throw new IllegalArgumentException("No se permiten numeros negativos");
        }
        if (!cleanSecurityCode.matches("\\d+")) {
            throw new IllegalArgumentException("El codigo de seguridad solo debe contener numeros");
        }
        if (!cleanSecurityCode.matches("\\d{3,4}")) {
            throw new IllegalArgumentException("El codigo de seguridad debe tener 3 o 4 digitos");
        }
    }

    // Valida que el nombre del banco tenga una longitud minima.
    private static void validateBank(String bank) {
        if (bank == null || bank.trim().isEmpty()) {
            throw new IllegalArgumentException("Ingrese el banco");
        }
        if (bank.trim().length() < 3) {
            throw new IllegalArgumentException("El nombre del banco debe tener minimo 3 caracteres");
        }
    }

    // Valida la cuenta o telefono usado para transferencia.
    private static void validateAccountOrPhone(String accountOrPhone) {
        if (accountOrPhone == null || accountOrPhone.trim().isEmpty()) {
            throw new IllegalArgumentException("Ingrese la cuenta o telefono");
        }
        String cleanAccountOrPhone = accountOrPhone.trim();
        if (cleanAccountOrPhone.startsWith("-")) {
            throw new IllegalArgumentException("No se permiten numeros negativos");
        }
        if (!cleanAccountOrPhone.matches("\\d+")) {
            throw new IllegalArgumentException("La cuenta o telefono solo debe contener numeros");
        }
        if (!cleanAccountOrPhone.matches("\\d{6,20}")) {
            throw new IllegalArgumentException("La cuenta o telefono debe tener entre 6 y 20 digitos");
        }
    }

    // Valida la referencia de transferencia con caracteres permitidos.
    private static void validateTransferReference(String reference) {
        if (reference == null || reference.trim().isEmpty()) {
            throw new IllegalArgumentException("Ingrese la referencia de transferencia");
        }
        if (!reference.trim().matches("[A-Za-z0-9 -]{4,30}")) {
            throw new IllegalArgumentException("La referencia debe tener entre 4 y 30 caracteres validos");
        }
    }

    // Imprime en consola una factura con orden, productos, pago y envio.
    public void generateInvoice(Order1 order, ShoppingCar1 shoppingCar, Shipping1 shipping) {
        System.out.println("========================================");
        System.out.println("               INKURBAN");
        System.out.println("            Arte callejero");
        System.out.println("----------------------------------------");
        System.out.println(order.toString());
        System.out.println("----------------------------------------");
        System.out.println("PRODUCTOS:");
        for (Product1 product : shoppingCar.getProducts()) {
            System.out.printf("- %-20s | $%.2f%n", product.getNameProduct(), product.getPrice());
        }
        System.out.println("----------------------------------------");
        System.out.printf("Total: $%.2f%n", shoppingCar.calculateTotal());
        System.out.printf("Metodo de pago: %s%n", paymentMethod);
        System.out.printf("Id del pago: %d%n", idPay);
        System.out.printf("Direccion de entrega: %s%n", shipping.getShippingAddress());
        System.out.printf("Id del envio: %d%n", shipping.getIdShipping());
        System.out.println("========================================");
    }
}
