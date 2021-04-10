package p03.c01;

public interface IParque {
	
	/**
	 * Definir método de entrada al parque
	 * @param puerta de entrada
	 */
	public abstract void entrarAlParque(String puerta);
	/**
	 * Definir método de salida del parque
	 * @param puerta de salida del parque
	 */
	public abstract void salirDelParque(String puerta);

}
