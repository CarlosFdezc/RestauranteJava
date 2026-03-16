# Simulador de Restaurante con Semáforos en Java

Esta práctica es una simulación de un restaurante utilizando hilos (Threads) y concurrencia en Java.

## Descripción de la Práctica

El programa simula un restaurante completo donde participan varios actores a la vez. La sincronización entre los clientes, los cocineros, los camareros y el gerente se realiza utilizando mecanismos de control de concurrencia de Java, específicamente Semáforos (`java.util.concurrent.Semaphore`) para garantizar el acceso seguro a los recursos compartidos, controlar la producción y consumo, y evitar interbloqueos (*deadlocks*).

### Características Principales:
*   **Multihilo:** Cada actor (`Cliente.java`, `Cocinero.java`, `Camarero.java`, `Gerente.java`) se ejecuta en su propio hilo.
*   **Control de Aforo y Cocina:** Se usa un semáforo contador para limitar que no haya más de un número máximo de comandas simultáneas preparándose en la cocina.
*   **Gestión de Pedidos (Productor-Consumidor):** Los clientes actúan como productores de pedidos, y los cocineros como consumidores. A su vez, los cocineros producen los platos terminados, que los camareros consumen para llevarlos a las mesas.
*   **Atención Individualizada:** Se utiliza un array de semáforos para notificar a clientes específicos, asegurando que un cliente espere exactamente a que su comida (y no otra) sea servida.
*   **Control de Cuellos de Botella:** La caja tiene un límite de capacidad. Si hay demasiadas mesas esperando para pagar (saturación de cobros), el sistema bloquea preventivamente que los clientes realicen nuevos pedidos mediante un semáforo que actúa de barrera y protege al camarero y gerente.

### Clases Principales:
*   **`Restaurante.java`**: La clase principal (orquestador) que inicializa y lanza los hilos. También almacena todas las variables de estado compartido, listas (comandas, cajas, platos) y declara todos los *Semáforos* utilizados.
*   **`Cliente.java`**: Representa a un cliente que llega, pide comida, espera a que se la sirvan, come, pide la cuenta y paga para irse.
*   **`Cocinero.java`**: Representa a un cocinero que toma las comandas, las prepara y  las deja en la zona de platos listos. Si no hay comandas se detiene.
*   **`Camarero.java`**: Representa a un trabajador que lleva la comida terminada o lleva las quejas/cobros al gerente. También previene al sistema de bloqueos si hay mucha gente en cola de pagar.
*   **`Gerente.java`**: Entidad que procesa única y exclusivamente el final del ciclo: recibe el pago final y da permiso al cliente para irse.

## Cómo Ejecutar

Compila los archivos Java:
```bash
javac *.java
```

Ejecuta la clase principal:
```bash
java Restaurante
```
