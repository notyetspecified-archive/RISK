package logic;

import java.util.ArrayList;

public class Country
{
	private ArrayList<Country> outnodes;
	private String name;
	private String continent;
	private Player owner;
	private int troops;
	private Coordinate coord;
	private boolean visited;

	public Country(String name, String continent, Coordinate coord)
	{
		super();
		troops = 1;
		this.outnodes = new ArrayList<Country>();
		this.name = name;
		this.continent = continent;
		this.coord = coord;
		this.visited = false;
	}
	
	public void addOutNode(Country outNode)
	{
		this.outnodes.add(outNode);
	}

	public ArrayList<Country> getOutnodes() {
		return outnodes;
	}

	public void setOutnodes(ArrayList<Country> outnodes) {
		this.outnodes = outnodes;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getContinent() {
		return continent;
	}

	public void setContinent(String continent) {
		this.continent = continent;
	}

	public Player getOwner() {
		return owner;
	}

	public void setOwner(Player owner){
		this.owner = owner;
	}

	public int getTroops() {
		return troops;
	}

	public void setTroops(int troops) {
		this.troops = troops;
	}
	
	public void addTroops(int troops) {
		this.troops += troops;
	}

	public Coordinate getCoord() {
		return coord;
	}

	public void setCoord(Coordinate coord) {
		this.coord = coord;
	}

	public boolean isVisited() {
		return visited;
	}

	public void setVisited(boolean visited) {
		this.visited = visited;
	}
}