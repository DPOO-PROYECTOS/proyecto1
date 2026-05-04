package logica;

import modelo.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class UsuarioRegistroTest {

    private Cafe cafe;
    private CafeLogica logica;

    @BeforeEach
    public void setUp() {
        cafe = new Cafe("Uniandes Board", 100);
        logica = new CafeLogica(cafe);
    }

    // Login con contraseña incorrecta lanza excepción
    @Test
    public void testLoginPasswordIncorrecto() {
        logica.registrarCliente("maria", "secreta");

        assertThrows(IllegalArgumentException.class, () -> {
            logica.login("maria", "incorrecta");
        }, "Debe lanzar excepción con contraseña incorrecta");
    }

    // Registrar dos usuarios con el mismo login lanza excepción
    @Test
    public void testLoginDuplicadoLanzaExcepcion() {
        logica.registrarCliente("pedro", "1234");

        assertThrows(IllegalArgumentException.class, () -> {
            logica.registrarCliente("pedro", "abcd");
        }, "No se puede registrar dos usuarios con el mismo login");
    }

    // Cliente nuevo tiene tieneBonoTorneoAmistoso en false
    @Test
    public void testClienteNuevoSinBono() {
        Cliente cliente = logica.registrarCliente("luis", "pass");

        assertFalse(cliente.getTieneBonoTorneoAmistoso(), "Un cliente recién creado no debe tener bono");
    }

    // registrarEmpleado con tipo inválido lanza excepción
    @Test
    public void testRegistrarEmpleadoTipoInvalido() {
        assertThrows(IllegalArgumentException.class, () -> {
            logica.registrarEmpleado("emp1", "pass", "barista");
        }, "Tipo de empleado inválido debe lanzar excepción");
    }

    // Mesero registrado genera un código de descuento no nulo
    @Test
    public void testMeseroTieneCodigoDescuento() {
        Empleado mesero = logica.registrarEmpleado("carlos", "pass", "mesero");

        assertNotNull(mesero.getCodigoDescuento(), "El mesero debe tener un código de descuento");
        assertFalse(mesero.getCodigoDescuento().isBlank(), "El código de descuento no debe estar vacío");
    }

    // Login exitoso devuelve el usuario correcto
    @Test
    public void testLoginExitoso() {
        logica.registrarCliente("sofia", "clave123");

        Usuario logueado = logica.login("sofia", "clave123");

        assertNotNull(logueado, "El usuario logueado no debe ser null");
        assertEquals("sofia", logueado.getLogin(), "Debe devolver el usuario con el login correcto");
        assertInstanceOf(Cliente.class, logueado, "Debe ser instancia de Cliente");
    }
}
