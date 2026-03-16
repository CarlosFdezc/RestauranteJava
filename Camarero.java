public class Camarero implements Runnable {
    private int id;

    public Camarero(int id) {
        this.id = id;
    }

    @Override
    public void run() {
        while (true) {
            try {
                // Esperar a tener algo que hacer (plato listo o cuenta que cobrar)
                Restaurante.tareasCamarero.acquire();

                // Intentar sacar un plato primero
                boolean platoServido = false;
                if (Restaurante.mutexPlatos.tryAcquire()) {
                    Integer idCliente = Restaurante.colaPlatosListos.poll();
                    Restaurante.mutexPlatos.release();

                    if (idCliente != null) {
                        System.out
                                .println("Camarero " + id + " está sirviendo la comida al Cliente " + idCliente + ".");
                        Restaurante.comidaServida[idCliente].release(); // Avisar al cliente
                        platoServido = true;
                    }
                }

                // Si no había plato, es que había una cuenta que llevar a caja
                if (!platoServido) {
                    Restaurante.mutexCuentas.acquire();
                    Integer idCliente = Restaurante.colaCuentasPosibles.poll();
                    Restaurante.mutexCuentas.release();

                    if (idCliente != null) {
                        System.out.println("Camarero " + id + " lleva la cuenta del Cliente " + idCliente + " a caja.");

                        Restaurante.mutexPagos.acquire();
                        Restaurante.colaCaja.add(idCliente);
                        Restaurante.clientesEsperandoCaja++;

                        // Si hay 3 o más esperando caja, bloqueamos la entrada de nuevos pedidos
                        if (Restaurante.clientesEsperandoCaja >= 3
                                && Restaurante.bloqueoPedidos.availablePermits() > 0) {
                            System.out.println("¡ALERTA! Cola de pagos saturada. No se aceptan más pedidos por ahora.");
                            Restaurante.bloqueoPedidos.acquire();
                        }
                        Restaurante.mutexPagos.release();

                        Restaurante.esperandoPagar.release(); // Avisar al gerente
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
