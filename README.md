# Práctica: Simulación de Restaurante con Semáforos en Java

Esta práctica consiste en una simulación concurrente del funcionamiento de un restaurante mediante el uso de hilos (Threads) y semáforos en Java. El objetivo es coordinar la ejecución de distintos actores (Clientes, Cocineros, Camareros y Gerente) evitando condiciones de carrera e interbloqueos (*deadlocks*).

### Descripción de la Práctica
El sistema modela el ciclo completo de atención de un restaurante:
- **Clientes**: Generan peticiones (comandas) y esperan a recibir y consumir su comida. Luego proceden a pedir la cuenta y abandonar el local una vez que han pagado.
- **Cocineros**: Atienden las comandas (respetando un máximo de comandas simultáneas en cocina) y preparan los platos.
- **Camareros**: Llevan los platos listos a la mesa de los clientes correspondientes y transportan las solicitudes de cobro a la caja. También avisan de saturación si hay muchos pedidos sin cobrar.
- **Gerente**: Encargado de procesar los cobros de las mesas y liberar a los clientes para que abandonen el restaurante.

Todo el control temporal y de concurrencia se ha conseguido mediante el uso de semáforos (`java.util.concurrent.Semaphore`), controlando exclusión mutua, barreras, productores-consumidores y notificaciones individuales.
