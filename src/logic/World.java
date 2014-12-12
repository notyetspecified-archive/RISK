package logic;

import java.util.ArrayList;

/**
 * Classe mundo
 *  
 * @author João Ladeiras
 * @author Rui Lima
 * 
 */

public class World
{
	//Numero total de territorios no mundo
	public static final int NR_OF_COUNTRIES = 42;
	
	//Grafo do mundo
	private ArrayList<Country> nodes;
	
	/**
	 * Construtor
	 */
	public World() 
	{
		super();
		this.nodes = new ArrayList<Country>();
	}
	
	/**
	 * Adiciona novo territorio ao mundo
	 * 
	 * @param country Territorio a ser adicionado
	 */
	public void addCountry(Country country)
	{
		this.nodes.add(country);
	}
	
	public ArrayList<Country> getCountries()
	{
		return this.nodes;
	}
}
