# Documento de Implementación — Board Game Café
**Proyecto 1 | ISIS-1226 Diseño y Programación Orientado a Objetos**

---

## 1. Visión General de la Arquitectura

El sistema está organizado en dos capas claramente separadas:

| Capa | Paquete | Responsabilidad |
|---|---|---|
| **Modelo** | `modelo` | Representar las entidades del dominio con sus datos y operaciones propias |
| **Lógica** | `logica` | Coordinar las reglas de negocio, validaciones y casos de uso del sistema |

Esta separación permite que el modelo no sepa nada sobre las reglas del negocio (por ejemplo, `JuegoDeMesa` no sabe si una mesa tiene menores; eso lo resuelve la lógica). El modelo solo gestiona su propio estado. La lógica orquesta múltiples objetos del modelo para cumplir un caso de uso completo.

---

## 2. Capa de Modelo

### 2.1 Jerarquía de Usuarios

```
Usuario (login, password, favoritos)
├── Cliente        (puntosFidelidad)
├── Admin
└── Empleado       (tipo, enTurno, codigoDescuento)
    └── Mesero     (juegosConocidos)
```

**Decisiones:**

- `Usuario` es clase base concreta (no abstracta) porque `Admin` y `Cliente` comparten los mismos atributos de autenticación y también la lista de favoritos. El PDF dice que *"cada cliente o empleado puede tener guardados sus juegos favoritos"*, por lo que se ubicó en la clase base para que `Empleado` también lo herede sin duplicar código.

- `favoritos` vive en `Usuario` y no en `Cliente` porque el requerimiento incluye explícitamente a los empleados. Haberlo dejado solo en `Cliente` habría sido incorrecto.

- `enTurno` en `Empleado` es un flag que debe ser gestionado externamente por la lógica al iniciar o terminar una jornada. No se activa automáticamente al crear un turno porque un turno puede crearse con anticipación para la próxima semana.

- `codigoDescuento` se genera automáticamente en el constructor de `Empleado` con el formato `"EMP-<login>"`. Esto garantiza que todo empleado tenga siempre un código único sin necesidad de asignación manual.

- `Mesero` extiende `Empleado` y fuerza `tipo = "mesero"` en su constructor. Un cocinero es simplemente un `Empleado` con `tipo = "cocinero"` porque el PDF no describe características adicionales para los cocineros más allá del tipo.

---

### 2.2 Jerarquía de Juegos

```
JuegoDeMesa (abstract)
├── JuegoDeCartas
├── JuegoTablero
├── JuegoDeAccion
└── JuegoDificil
```

**Atributos de `JuegoDeMesa`:**

| Atributo | Uso en el sistema |
|---|---|
| `nombre`, `anioPublicacion`, `empresaMatriz` | Identificación básica del catálogo |
| `minJugadores`, `maxJugadores` | Validación al prestar: el juego debe soportar el número de personas de la mesa |
| `aptaMenores5` | Si es `false`, no se presta a mesas con niños menores de 5 años |
| `soloAdultos` | Si es `true`, no se presta a mesas con niños ni jóvenes |
| `estado` | Texto libre: "Nuevo", "Bueno", "Falta una pieza", "desaparecido". Solo visible para el admin |
| `disponible` | Flag que controla si el juego puede prestarse. Se gestiona automáticamente en `Prestamo` |
| `vecesPrestado` | Contador histórico incremental. Se incrementa en `Prestamo.agregarJuego()` |
| `precioVenta` | Precio del juego cuando está en el inventario de ventas. Es 0 por defecto para juegos solo de préstamo |

**`esAptoParaMesa(numPersonas, tieneNinos, tieneJovenes)`:** método de conveniencia que centraliza ambas validaciones (jugadores y edad) en un solo lugar. La lógica lo llama antes de registrar cualquier préstamo.

**`JuegoDificil`** extiende `JuegoDeMesa` directamente siguiendo el UML del proyecto. En la práctica representa un juego que requiere presentación por parte de un mesero capacitado. La lista `juegosConocidos` en `Mesero` contiene instancias de `JuegoDificil`, lo que permite verificar en tiempo de ejecución si hay alguien disponible para explicarlo.

---

### 2.3 Inventarios

```
InventarioPrestamo  →  List<JuegoDeMesa>
InventarioVenta     →  List<JuegoDeMesa>
```

Los dos inventarios son completamente independientes. Un mismo objeto `JuegoDeMesa` solo puede estar en uno a la vez. Esto modela la realidad física: un juego que está en la estantería de venta no puede prestarse, y uno que se mueve a préstamo ya no está disponible para venta.

**`buscarDisponiblePorNombre(nombre)`** en `InventarioPrestamo` fue añadido específicamente para el caso de uso de préstamo, donde puede haber múltiples copias del mismo juego pero solo se necesita una que esté libre. `buscarPorNombre` devuelve cualquier copia; `buscarDisponiblePorNombre` garantiza que la copia retornada puede prestarse ahora.

---

### 2.4 Préstamos

```
Prestamo (abstract)  →  fechaInicio, fechaFin, List<JuegoDeMesa>
├── PrestamoCliente  →  cliente, mesa
└── PrestamoEmpleado →  empleado
```

**`Prestamo` es abstracto** porque un préstamo siempre pertenece a alguien (cliente o empleado) y tiene reglas distintas de validación. Sin embargo, la lógica de estado (activo/finalizado, agregar/devolver juegos) es compartida.

**`estaActivo()`** retorna `fechaFin == null`. Un préstamo está activo mientras no haya sido finalizado explícitamente.

**`agregarJuego(juego)`** hace dos cosas simultáneamente:
1. Marca el juego como `disponible = false` para que nadie más lo pida.
2. Incrementa `vecesPrestado` para mantener el historial del admin.

**`finalizar()`** marca `fechaFin` y restaura `disponible = true` en todos los juegos pendientes. Esto cubre el caso en que el cliente se va y se libera la mesa sin haber devuelto juego por juego.

**`devolverJuego(juego)`** maneja la devolución individual. Si el cliente devuelve un juego pero quiere seguir con el otro, el préstamo sigue activo.

---

### 2.5 Ventas

```
Venta (abstract)  →  fecha, usuario, empleadoDescuento, List<LineaVenta>
├── VentaJuego       →  IVA = 19%
└── VentaCafeteria   →  IMPUESTO_CONSUMO = 8%, propina
```

**`Venta` es abstracta** y define `calcularTotal()` como método abstracto porque cada tipo de venta tiene su propio esquema impositivo y de descuentos. `calcularSubtotal()` sí está implementado en la base porque la suma de líneas es igual para ambos tipos.

**`LineaVenta`** tiene dos constructores separados, uno para `JuegoDeMesa` y otro para `ItemMenu`. Una línea representa *o* un juego *o* un ítem de menú, nunca ambos. Los métodos `esDeJuego()` y `esDeMenu()` permiten distinguirlos al generar reportes.

**`VentaJuego.calcularDescuento(subtotal)`:**
- Si no hay `empleadoDescuento` → 0%
- Si el comprador es `Empleado` → 20% (empleado usando su propio código)
- Si el comprador es `Cliente` → 10% (cliente con código compartido por un empleado)

**`VentaCafeteria.calcularTotal()`:**
```
total = (subtotal - descuento_empleado) × 1.08 + propina
```
La propina se aplica *antes* del impuesto (sobre el subtotal con descuento), siguiendo la indicación del PDF. El método `calcularPropinaSugerida()` devuelve el 10% del subtotal y debe llamarse *después* de agregar todas las líneas, no en el constructor.

---

### 2.6 Menú

```
ItemMenu (abstract)  →  nombre, precio
├── Bebida           →  esAlcoholica, esCaliente
└── Pasteleria       →  List<String> alergenos
```

`Bebida` tiene dos flags booleanos que la lógica usa para aplicar las restricciones del PDF:
- `esAlcoholica` → no se sirve si la mesa tiene menores.
- `esCaliente` → no se sirve si la mesa tiene un juego de Acción activo; y simétricamente, no se presta un `JuegoDeAccion` si la mesa ya tiene una bebida caliente.

`Pasteleria` almacena alérgenos como `List<String>`. Los métodos `agregarAlergeno`, `quitarAlergeno` y `tieneAlergeno` permiten gestión granular. La lógica emite una advertencia informativa (no bloqueante) cuando el cliente pide un platillo con alérgenos.

---

### 2.7 Turnos

```
PlanSemanal  →  List<Turno>
Turno        →  dia, horaInicio, horaFin, Empleado
SolicitudCambioTurno → turno, solicitante, intercambiarCon, estado
```

`PlanSemanal` centraliza los métodos de validación de mínimos:
- `contarMeserosEnDia(dia)` → cuenta turnos donde el empleado es instancia de `Mesero`
- `contarCocinerosEnDia(dia)` → cuenta turnos donde `tipo == "cocinero"`
- `cumpleMinimosDia(dia)` → `meseros >= 2 && cocineros >= 1`

Esta validación se usa en tres momentos distintos: al eliminar un turno, al modificarlo, y al aprobar una solicitud de cambio general.

`SolicitudCambioTurno` tiene dos modalidades:
- **Cambio general** (`intercambiarCon == null`): el empleado quiere librar ese día.
- **Intercambio** (`intercambiarCon != null`): quiere cambiar su turno con el de otro empleado.

El estado del ciclo de vida de la solicitud es: `"pendiente"` → `"aprobada"` | `"rechazada"`.

---

### 2.8 Café (raíz del sistema)

`Cafe` es el agregado raíz. Contiene y da acceso a todos los objetos del sistema:

| Colección | Contenido |
|---|---|
| `mesas` | Las mesas físicas del local |
| `usuarios` | Todos los usuarios registrados (clientes, empleados, admin) |
| `menu` | Catálogo de ítems disponibles para ordenar |
| `inventarioPrestamo` | Juegos disponibles para prestar |
| `inventarioVenta` | Juegos disponibles para vender |
| `planSemanal` | Turnos semanales de los empleados |
| `ventas` | Historial de todas las ventas (juegos y cafetería) |
| `historialPrestamos` | Historial de todos los préstamos (activos e históricos) |
| `solicitudesCambioTurno` | Solicitudes de cambio de turno pendientes y procesadas |
| `sugerencias` | Sugerencias de platillos de empleados |

Los métodos de búsqueda en `Cafe` (`buscarUsuarioPorLogin`, `buscarMesaDisponible`, `buscarEmpleadoPorCodigo`) evitan que la lógica itere listas directamente, manteniendo la responsabilidad de acceso a datos en el modelo.

---

## 3. Capa de Lógica — `CafeLogica`

`CafeLogica` recibe un `Cafe` en su constructor y opera sobre él. Es el único punto de entrada para todas las operaciones del sistema. La interfaz de usuario (proyecto 2) solo llamará métodos de esta clase.

---

### 3.1 Autenticación y Usuarios

**`login(login, password)`**
Busca el usuario por login y verifica la contraseña con `verificarPassword()`. Lanza excepción si el usuario no existe o la contraseña es incorrecta. Retorna el objeto `Usuario` para que la capa superior sepa qué tipo es (Cliente, Empleado, Admin) y muestre la interfaz correspondiente.

**`registrarCliente` / `registrarEmpleado`**
Verifican unicidad de login antes de crear el objeto. `registrarEmpleado` distingue entre `Mesero` y `Empleado` genérico (cocinero) según el tipo indicado.

---

### 3.2 Gestión de Mesas

**`asignarMesa(cliente, numPersonas, tieneNinos, tieneJovenes)`**

Flujo de validaciones en orden:
1. `numPersonas > 0`
2. `cafe.hayCapacidad(numPersonas)` — verifica que sumando las personas actuales en todas las mesas ocupadas no se supere el máximo del café
3. `cafe.buscarMesaDisponible(numPersonas)` — encuentra una mesa libre con capacidad suficiente

Si todo es válido, asigna el cliente y configura la mesa.

**`liberarMesa(mesa)`**

Al liberar, primero verifica si el cliente tiene préstamos activos y los finaliza automáticamente. Esto asegura que los juegos queden disponibles aunque el cliente no los haya devuelto explícitamente. Luego llama a `mesa.liberar()` que limpia todos los campos de la mesa.

---

### 3.3 Gestión de Préstamos

**`solicitarPrestamoCliente(cliente, nombreJuego)`**

Es la operación más compleja del sistema. Aplica las siguientes validaciones en orden estricto:

```
1. ¿Tiene mesa asignada?                     → requerido por el PDF
2. ¿Ya tiene 2 juegos prestados?             → máximo permitido
3. ¿Existe el juego y está disponible?       → distinción entre "no existe" y "prestado"
4. ¿Es apto para la mesa? (jugadores/edad)  → esAptoParaMesa()
5. ¿Es JuegoDeAccion con bebida caliente?   → restricción del PDF
6. ¿Es JuegoDificil?                         → advertencia (no bloquea) si no hay mesero
```

Si el cliente ya tiene un préstamo activo con 1 juego, se agrega el segundo al mismo `PrestamoCliente` en lugar de crear uno nuevo. Un cliente solo puede tener un `PrestamoCliente` activo a la vez.

**`solicitarPrestamoEmpleado(empleado, nombreJuego)`**

Valida que el empleado puede prestar: `enTurno == false` OR no hay clientes en el café. Los empleados no necesitan mesa. Se crea un `PrestamoEmpleado` nuevo por solicitud.

**`devolverJuego(prestamo, juego)`**

Devuelve un juego individual. Si al devolverlo el préstamo queda sin juegos, lo finaliza automáticamente. Esto soporta el caso natural de devolución progresiva.

---

### 3.4 Ventas de Juegos

**`venderJuegos(usuario, juegos, codigoDescuento, puntosAUsar)`**

Flujo completo:
1. Bloquea al `Admin` (no puede comprar).
2. Valida y aplica código de descuento si se provee.
3. Crea una `LineaVenta` por cada juego con su `precioVenta`.
4. Si se usan puntos, los descuenta de la cuenta del cliente **antes** de registrar la venta (si no alcanza, falla y se revierte todo).
5. Retira los juegos del `InventarioVenta`.
6. Otorga 1% de puntos de fidelidad sobre `calcularTotal()` (el total con descuento de código, antes del descuento de puntos).
7. Registra la venta en el historial.

**Nota sobre los puntos de fidelidad:** se otorgan sobre el total real pagado por el código de descuento (post-descuento de empleado, post-IVA), pero antes de restar los puntos usados como descuento adicional. Esto evita circularidad (ganar puntos por usar puntos).

---

### 3.5 Ventas de Cafetería

**`realizarPedidoCafe(usuario, mesa, items, propina, puntosAUsar)`**

El parámetro `propina`:
- `< 0` → se aplica la propina sugerida (10% del subtotal, calculada en ese momento)
- `= 0` → sin propina
- `> 0` → monto personalizado del comensal

**`validarItemParaMesa(item, mesa)`** — validaciones del PDF:

| Condición | Restricción |
|---|---|
| `Bebida.esAlcoholica && mesa.tieneMenores()` | Bloqueado |
| `Bebida.esCaliente && mesa tiene JuegoDeAccion activo` | Bloqueado |
| `Pasteleria.alergenos != vacío` | Advertencia informativa (no bloqueante) |

Nótese que la restricción de bebida caliente y juego de acción es **bidireccional**: se aplica tanto al intentar ordenar una bebida caliente con un juego de acción activo (aquí), como al intentar pedir prestado un `JuegoDeAccion` cuando la mesa ya tiene una bebida caliente (en `solicitarPrestamoCliente`).

Las bebidas ordenadas se registran en `mesa.bebidas` para que las validaciones posteriores de préstamo puedan consultarlas.

---

### 3.6 Gestión de Inventario (Admin)

| Operación | Descripción |
|---|---|
| `agregarJuegoInventarioPrestamo(juego)` | Agrega al inventario de préstamo y marca `disponible = true` |
| `agregarJuegoInventarioVenta(juego, precio)` | Agrega al inventario de venta y asigna `precioVenta` |
| `moverDeVentaAPrestamo(juego)` | Transfiere físicamente el objeto entre inventarios; el PDF permite este movimiento unidireccional |
| `repararJuego(juegoRoto)` | Busca una copia del mismo nombre en ventas, retira el roto del préstamo, retira la copia de ventas e ingresa la copia al préstamo. Preserva `vecesPrestado` del juego roto |
| `marcarComoRobado(juego)` | Establece `estado = "desaparecido"`, `disponible = false`, y lo retira del inventario de préstamo. El historial lo conserva |
| `actualizarEstadoJuego(juego, estado)` | Permite al admin actualizar el estado de cualquier juego del inventario |

---

### 3.7 Gestión de Turnos

**`crearTurno`** simplemente instancia el `Turno` y lo agrega al `PlanSemanal`. No activa `enTurno` porque ese flag refleja si el empleado está trabajando *en este momento*, no si tiene un turno programado.

**`eliminarTurno`** hace la validación de mínimos *después* de quitar el turno temporalmente. Si falla, lo restaura y lanza excepción. Este patrón de "intentar y revertir" se repite en `modificarTurno` y `solicitarCambioTurno`.

**`modificarTurno`** guarda los tres valores originales (`dia`, `horaInicio`, `horaFin`) antes de modificar, para poder hacer un rollback completo y consistente si la validación falla.

**`solicitarCambioTurno`** valida que:
1. El turno pertenece al solicitante.
2. Si es cambio general (sin intercambio), el día quedaría con los mínimos cumplidos.
Para el intercambio no se valida mínimos porque los empleados se intercambian entre sí, sin quitar personal.

**`aprobarCambioTurno`** verifica que la solicitud esté en estado `"pendiente"` antes de procesarla. Luego:
- **Intercambio:** busca el turno del otro empleado en el mismo día y hace el swap bidireccional.
- **Cambio general:** quita el turno del plan y marca al empleado con `enTurno = false`.

---

### 3.8 Menú y Sugerencias

`sugerirPlatillo(empleado, descripcion, item)` crea la sugerencia con el `ItemMenu` concreto ya construido. Cuando el admin la aprueba con `aprobarSugerencia`, ese mismo objeto se agrega directamente al menú del café. Si la rechaza, solo se marca el estado; el objeto nunca llega al menú.

`getSugerenciasPendientes()` filtra por `admin == null`, que es el indicador de que ningún admin ha tomado acción aún. Una sugerencia rechazada tiene `aprobada = false` pero `admin != null`.

---

### 3.9 Reportes

Todos los reportes están construidos sobre `filtrarVentas(desde, hasta)` que retorna las ventas en un rango de fechas. Sobre ese resultado se aplican filtros adicionales según el tipo de reporte:

| Método | Qué calcula |
|---|---|
| `getTotalVentasDia(fecha)` | Total de todas las ventas en un día específico |
| `getTotalVentasSemana(inicio)` | Total de las ventas en los 7 días a partir de `inicio` |
| `getTotalVentasMes(mes, anio)` | Total del mes usando `getMonthValue()` y `getYear()` |
| `getTotalVentasJuegos(desde, hasta)` | Solo `VentaJuego` en el rango |
| `getTotalVentasCafe(desde, hasta)` | Solo `VentaCafeteria` en el rango |
| `getTotalImpuestos(desde, hasta)` | IVA de juegos + impuesto al consumo de cafetería |
| `getTotalPropinas(desde, hasta)` | Suma de propinas de `VentaCafeteria` en el rango |
| `getTotalCostos(desde, hasta)` | Suma de subtotales (precio base sin impuestos ni propinas) |

El cálculo de impuestos para `VentaJuego` es `(subtotal - descuento) × IVA` para reflejar que el IVA se aplica sobre el precio real pagado (post-descuento de código), no sobre el precio de lista.

---

## 4. Puntos de Diseño Destacados

### Bidireccionalidad bebida caliente ↔ juego de acción
El PDF establece que no puede coexistir una bebida caliente con un juego de acción en la misma mesa. Esto se implementa en dos puntos distintos:
- Al **ordenar bebida caliente** → se consulta si hay `JuegoDeAccion` activo en el préstamo de la mesa.
- Al **pedir prestado `JuegoDeAccion`** → se consulta si la mesa tiene bebidas calientes registradas.

Ambas validaciones apuntan al mismo estado (la lista de bebidas de la mesa y el préstamo activo), garantizando consistencia en cualquier orden que ocurran.

### Historial de préstamos en `Cafe`
Todos los préstamos (activos e históricos) viven en `cafe.historialPrestamos`. Un préstamo activo tiene `fechaFin == null`. Esto permite que el admin consulte el historial completo sin perder registros al finalizar préstamos.

### Puntos de fidelidad
Los puntos se otorgan sobre el monto pagado *después* del descuento por código de empleado pero *antes* de descontar puntos usados. Esto garantiza que siempre se ganen puntos proporcionales al valor real de la transacción, evitando que el uso de puntos reduzca los puntos ganados (lo que crearía un ciclo de reducción).

### Estado del empleado (`enTurno`)
El flag `enTurno` es gestionado por la lógica, no por el modelo de turno. Esto se debe a que un turno es una planificación futura; el estado actual del empleado depende de si su jornada ha comenzado en el día de hoy, lo cual es responsabilidad de la capa superior (lógica o interfaz).

### Rollback de operaciones de turno
Cualquier modificación que pueda violar los mínimos de personal usa el patrón:
```
1. Aplicar cambio temporalmente
2. Validar mínimos
3. Si falla → revertir al estado original + lanzar excepción
4. Si pasa → el cambio queda aplicado
```
Esto garantiza que el `PlanSemanal` nunca quede en estado inválido.
