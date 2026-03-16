public class Cocinero implements Runnable {
    private int id;

    public Cocinero(int id) {
        this.id = id;
    }

    @Override
    public void run() {
        while (true) {
            try {
                // Esperar a que haya comandas pendientes
                Restaurante.comandasPendientes.acquire();

                // Sacar de la cola
                Restaurante.mutexComandas.acquire();
                int idCliente = Restaurante.colaComandas.poll();
                Restaurante.mutexComandas.release();

                System.out.println("Cocinero " + id + " está preparando la comanda de Cliente " + idCliente + "...");

                // Cocinar (5-15s)
                int tiempoCocina = 5000 + Restaurante.random.nextInt(10000);
                Thread.sleep(tiempoCocina);

                // Dejar plato en listos
                System.out.println("Cocinero " + id + " ha terminado la comida del Cliente " + idCliente + ".");
                Restaurante.mutexPlatos.acquire();
                Restaurante.colaPlatosListos.add(idCliente);
                Restaurante.mutexPlatos.release();

                // Avisar a camareros de un nuevo plato y liberar la cocina para más pedidos
                Restaurante.tareasCamarero.release();
                Restaurante.capacidadCocina.release();

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
