import java.util.concurrent.Semaphore;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

public class Restaurante {
    // Límites y actores del restaurante
    public static final int NUM_CLIENTES = 10;
    private static final int NUM_COCINEROS = 2;
    private static final int NUM_CAMAREROS = 3;

    // Semáforos para el control de la cocina y restricciones
    public static Semaphore capacidadCocina = new Semaphore(5, true); // Capacidad máxima de la cocina
    public static Semaphore comandasPendientes = new Semaphore(0, true); // Avisa a los cocineros de nuevas comandas
    public static Semaphore tareasCamarero = new Semaphore(0, true); // Avisa a los camareros de tareas (platos o
                                                                     // cuentas)
    public static Semaphore esperandoPagar = new Semaphore(0, true); // Avisa al gerente de que hay clientes para cobrar
    public static Semaphore bloqueoPedidos = new Semaphore(1, true); // Bloquea nuevos pedidos si hay demasiada cola en
                                                                     // caja

    // Semáforos para coordinar a los clientes de forma individual
    public static Semaphore[] comidaServida = new Semaphore[NUM_CLIENTES];
    public static Semaphore[] pagoRealizado = new Semaphore[NUM_CLIENTES];

    // Semáforos Mutex (Exclusión Mutua) para proteger el acceso a las colas
    // compartidas
    public static Semaphore mutexComandas = new Semaphore(1, true);
    public static Semaphore mutexPlatos = new Semaphore(1, true);
    public static Semaphore mutexCuentas = new Semaphore(1, true);
    public static Semaphore mutexPagos = new Semaphore(1, true);

    // Colas de trabajo
    public static Queue<Integer> colaComandas = new LinkedList<>();
    public static Queue<Integer> colaPlatosListos = new LinkedList<>();
    public static Queue<Integer> colaCuentasPosibles = new LinkedList<>();
    public static Queue<Integer> colaCaja = new LinkedList<>();

    // Variables de control
    public static int clientesEsperandoCaja = 0;
    public static int numClientesFinalizados = 0;

    // Generador de números aleatorios para los tiempos de llegada, preparación y
    // omida
    public static Random random = new Random();

    public static void main(String[] args) {
        System.out.println("Restaurante abierto.");

        // Inicializamos los semáforos individuales para cada cliente
        for (int i = 0; i < NUM_CLIENTES; i++) {
            comidaServida[i] = new Semaphore(0);
            pagoRealizado[i] = new Semaphore(0);
        }

        // Iniciar el hilo del Gerente
        Thread gerenteThread = new Thread(new Gerente());
        gerenteThread.start();

        // Iniciar los hilos de los Cocineros
        Thread[] cocineros = new Thread[NUM_COCINEROS];
        for (int i = 0; i < NUM_COCINEROS; i++) {
            cocineros[i] = new Thread(new Cocinero(i + 1));
            cocineros[i].start();
        }

        // Iniciar los hilos de los Camareros
        Thread[] camareros = new Thread[NUM_CAMAREROS];
        for (int i = 0; i < NUM_CAMAREROS; i++) {
            camareros[i] = new Thread(new Camarero(i + 1));
            camareros[i].start();
        }

        // Llegada progresiva de los Clientes

        Thread[] clientes = new Thread[NUM_CLIENTES];
        for (int i = 0; i < NUM_CLIENTES; i++) {
            clientes[i] = new Thread(new Cliente(i));
            clientes[i].start();
        }

        // Esperar a que todos los clientes terminen y se vayan
        for (int i = 0; i < NUM_CLIENTES; i++) {
            try {
                clientes[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("Restaurante cerrado. Todos los clientes atendidos.");
        System.exit(0);
    }
}
