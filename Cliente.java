public class Cliente implements Runnable {
    private int id;

    public Cliente(int id) {
        this.id = id;
    }

    @Override
    public void run() {
        try {
            // Llegada aleatoria (1-20s)
            int tiempoLlegada = 1000 + Restaurante.random.nextInt(19000);
            Thread.sleep(tiempoLlegada);
            System.out.println("Cliente " + id + " llega al restaurante.");

            // Pedir: primero comprobar si hay bloqueo de pedidos (demasiada gente esperando
            // caja)
            Restaurante.bloqueoPedidos.acquire();
            Restaurante.bloqueoPedidos.release(); // Solo comprobamos, no lo retenemos

            // Intentar entrar comandas en cocina
            System.out.println("Cliente " + id + " quiere pedir. Esperando espacio en cocina...");
            Restaurante.capacidadCocina.acquire();

            // Añadir comanda
            Restaurante.mutexComandas.acquire();
            Restaurante.colaComandas.add(id);
            Restaurante.mutexComandas.release();

            System.out.println("Cliente " + id + " ha hecho su pedido.");
            Restaurante.comandasPendientes.release(); // Avisar a los cocineros

            // Esperar comida servida
            System.out.println("Cliente " + id + " esperando su comida.");
            Restaurante.comidaServida[id].acquire();

            // Comer (10-15s)
            System.out.println("Cliente " + id + " recibe su comida y empieza a comer...");
            int tiempoComida = 10000 + Restaurante.random.nextInt(5000);
            Thread.sleep(tiempoComida);
            System.out.println("Cliente " + id + " ha terminado de comer. Pidiendo la cuenta.");

            // Pedir cuenta
            Restaurante.mutexCuentas.acquire();
            Restaurante.colaCuentasPosibles.add(id);
            Restaurante.mutexCuentas.release();
            Restaurante.tareasCamarero.release(); // Avisar camareros

            // Esperar a haber pagado
            Restaurante.pagoRealizado[id].acquire();
            System.out.println("Cliente " + id + " ha pagado y abandona el restaurante.");

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
