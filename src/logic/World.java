package logic;

import java.util.ArrayList;

public class World
{
	public static final int NR_OF_COUNTRIES = 42;
	
	private ArrayList<Country> nodes;
	
	public World() 
	{
		super();
		this.nodes = new ArrayList<Country>();
	}
	
	public void addCountry(Country country)
	{
		this.nodes.add(country);
	}
	
	public ArrayList<Country> getCountries()
	{
		return this.nodes;
	}
}
