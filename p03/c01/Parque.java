package p03.c01;

import java.util.Enumeration;
import java.util.Hashtable;

public class Parque implements IParque{

	private int contadorPersonasTotales;
	private Hashtable<String, Integer> contadoresPersonasPuertaEntrada;
	private Hashtable<String, Integer> contadoresPersonasPuertaSalida;
	//Declaramos CONSTANTES con los valores de las personas que puede haber en el parque
	private final static int AFOROMAXIMO=40;
	private final static int AFOROMINIMO=0;
	
	//Variable con el valor del tiempo medio de estancia de las personas.
	private int tmedio;
		
	public Parque() {	// TODO
		contadorPersonasTotales = 0;
		contadoresPersonasPuertaEntrada = new Hashtable<String, Integer>();
		contadoresPersonasPuertaSalida=new Hashtable<String, Integer>();
		// TODO
	}


	@Override
	public void entrarAlParque(String puerta) {
		//Si tenemos el aforo máximo, no dejamos entrar a ninguna persona, durmiendo los hilos.
		comprobarAntesDeEntrar();
		// Si no hay entradas por esa puerta, inicializamos
		if (contadoresPersonasPuertaEntrada.get(puerta) == null){contadoresPersonasPuertaEntrada.put(puerta, 0);}
		
		//TODO calculamos el tiempo
		if(this.contadoresPersonasPuertaEntrada.get(puerta)< AFOROMAXIMO)		
		
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
	
	@Override
	public void salirDelParque(String puerta) {
		//Si tenemos el aforo mínimo, no puede  salir ninguna persona.
				comprobarAntesDeSalir();
				// Si no hay salidas por esa puerta, inicializamos
				if (contadoresPersonasPuertaSalida.get(puerta) == null){contadoresPersonasPuertaSalida.put(puerta, 0);}
				
				//TODO calculamos el tiempo
				//if(this.contadoresPersonasPuertaSalida.get(puerta)>AFOROMINIMO)		
				
				//Decrementamos el contador total y aumentamos el individual de personas por puerta
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
		System.out.println(movimiento + " por puerta " + puerta);
		System.out.println("--> Personas en el parque " + contadorPersonasTotales); //+ " tiempo medio de estancia: "  + tmedio);
		
		// Iteramos por todas las puertas e imprimimos sus entradas
		for(String p: contadoresPersonasPuertaEntrada.keySet()){
			System.out.println("----> Por puerta " + p + " " + contadoresPersonasPuertaEntrada.get(p));
		}
		System.out.println(" ");
		// Iteramos por todas las puertas e imprimimos sus salidas
				for(String p: contadoresPersonasPuertaSalida.keySet()){
					System.out.println("----> Por puerta " + p + " " + contadoresPersonasPuertaSalida.get(p));
				}
	}
	
	private int sumarContadoresPuerta() {
		int sumaContadoresPuerta = 0;
			Enumeration<Integer> iterPuertas = contadoresPersonasPuertaEntrada.elements();
			while (iterPuertas.hasMoreElements()) {
				sumaContadoresPuerta += iterPuertas.nextElement();
			}
			Enumeration<Integer> iterPuertasSalida = contadoresPersonasPuertaSalida.elements();
			while (iterPuertasSalida.hasMoreElements()) {
				sumaContadoresPuerta -= iterPuertasSalida.nextElement();
			}
		return sumaContadoresPuerta;
	}
	
	protected void checkInvariante() {
		assert sumarContadoresPuerta() == contadorPersonasTotales : "INV: La suma de contadores de las puertas debe ser igual al valor del contador del parque";
		// TODO 
		// TODO
	}

	protected synchronized void comprobarAntesDeEntrar(){	
		while(this.contadorPersonasTotales==Parque.AFOROMAXIMO) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
		}
	}

	protected synchronized void comprobarAntesDeSalir(){	
		while(this.contadorPersonasTotales==Parque.AFOROMINIMO) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
		}
	}

}
