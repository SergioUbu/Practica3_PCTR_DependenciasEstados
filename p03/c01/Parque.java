package p03.c01;

import java.util.Enumeration;
import java.util.Hashtable;

public class Parque implements IParque{

	private int contadorPersonasTotales;
	private Hashtable<String, Integer> contadoresPersonasPuerta;
	//Declaramos CONSTANTES con los valores de las personas que puede haber en el parque
	private final static int AFOROMAXIMO=20;
	private final static int AFOROMINIMO=0;
	
	//Variable con el valor del tiempo medio de estancia de las personas.
	private int tmedio;
		
	public Parque() {	// TODO
		contadorPersonasTotales = 0;
		contadoresPersonasPuerta = new Hashtable<String, Integer>();
		// TODO
	}

	@Override
	public void entrarAlParque(String puerta) {
		//Si tenemos el aforo máximo, no dejamos entrar a ninguna persona, durmiendo los hilos.
		comprobarAntesDeEntrar();
		// Si no hay entradas por esa puerta, inicializamos
		if (contadoresPersonasPuerta.get(puerta) == null){contadoresPersonasPuerta.put(puerta, 0);}
		
		//TODO calculamos el tiempo
		
		//if(this.contadoresPersonasPuerta.get(puerta)< AFOROMAXIMO)		
			// Aumentamos el contador total y el individual de personas por puerta
			contadorPersonasTotales++;		
			contadoresPersonasPuerta.put(puerta, contadoresPersonasPuerta.get(puerta)+1);
			
			// Imprimimos el estado del parque
			imprimirInfo(puerta, "Entrada");
			
			//Comprobación del invariante.
			checkInvariante();
			//Avisamos al resto de hilos para que despierten.
			notifyAll();
		
	}
	
	@Override
	public void salirDelParque(String puerta) {
		
	}
	
	private void imprimirInfo (String puerta, String movimiento){
		System.out.println(movimiento + " por puerta " + puerta);
		System.out.println("--> Personas en el parque " + contadorPersonasTotales); //+ " tiempo medio de estancia: "  + tmedio);
		
		// Iteramos por todas las puertas e imprimimos sus entradas
		for(String p: contadoresPersonasPuerta.keySet()){
			System.out.println("----> Por puerta " + p + " " + contadoresPersonasPuerta.get(p));
		}
		System.out.println(" ");
	}
	
	private int sumarContadoresPuerta() {
		int sumaContadoresPuerta = 0;
			Enumeration<Integer> iterPuertas = contadoresPersonasPuerta.elements();
			while (iterPuertas.hasMoreElements()) {
				sumaContadoresPuerta += iterPuertas.nextElement();
			}
		return sumaContadoresPuerta;
	}
	
	protected void checkInvariante() {
		assert sumarContadoresPuerta() == contadorPersonasTotales : "INV: La suma de contadores de las puertas debe ser igual al valor del contador del parte";
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
