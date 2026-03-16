public class Gerente implements Runnable {
    @Override
    public void run() {
        while (true) {
            try {
                // Esperar a que haya una cuenta que cobrar
                Restaurante.esperandoPagar.acquire();

                // Sacar de la cola
                Restaurante.mutexPagos.acquire();
                Integer idCliente = Restaurante.colaCaja.poll();

                if (idCliente != null) {
                    System.out.println("Gerente procesando el pago del Cliente " + idCliente + "...");

                    // Tiempo de cobrar (1-3s)
                    int tiempoCobro = 1000 + Restaurante.random.nextInt(2000);
                    Thread.sleep(tiempoCobro);

                    System.out.println("Gerente ha cobrado al Cliente " + idCliente + ".");

                    // Actualizar contadores y liberar bloqueo si es seguro
                    Restaurante.clientesEsperandoCaja--;

                    // Si clientes esperando baja por debajo de 3, podemos habilitar nuevos pedidos
                    if (Restaurante.clientesEsperandoCaja < 3 && Restaurante.bloqueoPedidos.availablePermits() == 0) {
                        System.out.println("¡ALERTA! Cola de pagos despejada. Se aceptan nuevos pedidos.");
                        Restaurante.bloqueoPedidos.release();
                    }

                    Restaurante.mutexPagos.release();

                    // Avisar al cliente
                    Restaurante.pagoRealizado[idCliente].release();

                    // Contar totales y si ya están todos cobrados, terminar al hilo Gerente
                    Restaurante.numClientesFinalizados++;
                    if (Restaurante.numClientesFinalizados == Restaurante.NUM_CLIENTES) {
                        break;
                    }
                } else {
                    Restaurante.mutexPagos.release();
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
