package p03.c01;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Parque implements IParque{
	
	//Personas totales en el parque
	private int contadorPersonasTotales; 
	//Personas que han entrado y salido por cada puerta
	private Hashtable<String, Integer> contadoresPersonasPuertaEntrada;
	private Hashtable<String, Integer> contadoresPersonasPuertaSalida;
	//Variables para control aforo máximo y mínimo.
	private final static int AFOROMAXIMO=40;
	private final static int AFOROMINIMO=0;
	
	//Variables para contadores de estancia
	private long timingInicio;
	private long timingAlEntrar;
	private long timingAlSalir;
	private double timingDeMedia;
	
	/**
	 * Constructor de la clase Parque
	 */
	public Parque() {
		//Inicializamos el contador de personas en el parque a 0.
		contadorPersonasTotales = 0;
		//Reservamos memoria para los contadores de persona/puerta
		contadoresPersonasPuertaEntrada = new Hashtable<String, Integer>();
		contadoresPersonasPuertaSalida=new Hashtable<String, Integer>();
		//Registramos el inicio del tiempo en el que creamos el parque.
		timingInicio = System.currentTimeMillis();
		//Al no haber ninguna entrada de personas, definimos el tiempo de media a 0.
		timingDeMedia = 0;
	}

	@Override
	public synchronized void entrarAlParque(String puerta){		
		//Si tenemos el aforo máximo, no dejamos entrar a ninguna persona, durmiendo los hilos.
		comprobarAntesDeEntrar();		
		
		// Si no hay entradas por esa puerta, inicializamos
		if (contadoresPersonasPuertaEntrada.get(puerta) == null){ 
			contadoresPersonasPuertaEntrada.put(puerta, 0);
		}	
		

		//Calculamos el tiempo de entrada 
		timingAlEntrar = System.currentTimeMillis();									

		
		// Aumentamos el contador total y el individual de personas por puerta
		contadorPersonasTotales++;		
		contadoresPersonasPuertaEntrada.put(puerta, contadoresPersonasPuertaEntrada.get(puerta)+1);		
		
		// Imprimimos el estado del parque
		imprimirInfo(puerta, "Entrada");		
		
		//Comprobación del invariante.
		checkInvariante();
		//Avisamos al resto de hilos para que despierten.
		notifyAll();		
		
		
	}
	
	public synchronized void salirDelParque(String puerta){
		
		//Comprobamos si hay persona en el interior del parque.
		comprobarAntesDeSalir();		
		
		// Si no hay salidas por esa puerta, inicializamos
		if (contadoresPersonasPuertaSalida.get(puerta) == null){ 
			contadoresPersonasPuertaSalida.put(puerta, 0);
		}
		
		//Asignamos el tiempo actual al timingAlSalir
		timingAlSalir = System.currentTimeMillis();				
		
		// Decrementamos el contador total y el individual
		contadorPersonasTotales--;		
		contadoresPersonasPuertaSalida.put(puerta, contadoresPersonasPuertaSalida.get(puerta)+1);		
		
		// Imprimimos el estado del parque
		imprimirInfo(puerta, "Salida");
		//Comprobación del invariante.
		checkInvariante();
		//Avisamos al resto de hilos para que despierten.
		notifyAll();		
	}
		
	private void imprimirInfo (String puerta, String movimiento){	
		//Imprimimos el movimiento y las personas que hay en el parque
		System.out.println(movimiento + " por puerta " + puerta);
		String texto="--> Personas en el parque " + contadorPersonasTotales;
		if(movimiento.equals("Salida")) {
			texto+= "\nTiempo de estancia media: "  + obtenertimingDeMedia();
			// Iteramos por todas las puertas de entrada e imprimimos sus entradas
			for(String p: contadoresPersonasPuertaSalida.keySet()){
				texto+=("\n----> Por puerta " + p + " " + contadoresPersonasPuertaSalida.get(p));
			}
		}
		else {
			// Iteramos por todas las puertas de entrada e imprimimos sus entradas
			for(String p: contadoresPersonasPuertaEntrada.keySet()){
				texto+=("\n----> Por puerta " + p + " " + contadoresPersonasPuertaEntrada.get(p));
			}
		}
		System.out.println(texto);
		
		
		
		System.out.println(" ");
	}
	
	private int sumarContadoresPuerta() {
		//Acumulamos los contadores de cada puerta
		int sumaContadoresPuerta = 0;
		Enumeration<Integer> iterPuertasEntrada = contadoresPersonasPuertaEntrada.elements();
		Enumeration<Integer> iterPuertasSalida = contadoresPersonasPuertaSalida.elements();
		while (iterPuertasEntrada.hasMoreElements()) { 
			sumaContadoresPuerta += iterPuertasEntrada.nextElement(); 
		}
		while (iterPuertasSalida.hasMoreElements()) { 
			sumaContadoresPuerta -= iterPuertasSalida.nextElement(); 
		}
		return sumaContadoresPuerta;
	}
	
	protected void checkInvariante() {		
		//Comprobamos que la suma de los contadores de las puertas sea igual a la suma de presonas totales
		assert sumarContadoresPuerta() == contadorPersonasTotales : "INV: La suma de contadores de las puertas debe ser igual al valor del contador del parque";
		assert this.contadorPersonasTotales <= AFOROMAXIMO : "INV: El contadorPersonasTotales nunca puede ser mayor que el aforo máximo";
		assert this.contadorPersonasTotales >= AFOROMINIMO : "INV: El contadorPersonasTotales nunca puede ser menor que el aforo mínimo";
	}

	protected synchronized void comprobarAntesDeEntrar(){	
		//Cuando se alcanza el limite del parque, el hilo se pone a la espera
		while(contadorPersonasTotales == AFOROMAXIMO ){ 
	           try {
				wait(); 
			} catch (InterruptedException e) {
				Logger.getGlobal().log(Level.INFO, "Entrada interrumpida");
				Logger.getGlobal().log(Level.INFO, e.toString());
			}
	    }
	}

	protected synchronized void comprobarAntesDeSalir(){
		//Cuando no hay nadie en el parque, el hilo se pone a la espera
		while (contadorPersonasTotales == AFOROMINIMO) 	
			try { wait(); } 
			catch(InterruptedException e) {
				Logger.getGlobal().log(Level.INFO, "Entrada interrumpida");
				Logger.getGlobal().log(Level.INFO, e.toString());
			}	
	}
	
	private double obtenertimingDeMedia() {
		//Procedemos a calcular el tiempo medio que está en el parque.
		
		timingDeMedia=((timingDeMedia + (timingAlSalir - timingAlEntrar))/2.0); 
		timingAlEntrar=timingAlSalir;
		return timingDeMedia;
	}

}
