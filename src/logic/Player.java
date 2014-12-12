package logic;

import java.awt.Color;

import jade.core.AID;

/**
 * Classe jogador
 *  
 * @author João Ladeiras
 * @author Rui Lima
 * 
 */

public class Player
{
	private static final float NUMBER_OF_TERRITORIES = (float) 42.0;
	private static int IDNr = 0;
	private int ID;
	private AID aid;
	private Color color;
	private static int colorNr = 0;
	private int totalCountries;
	private float worldPercentage;
	private int rank;

	/**
	 * Construtor
	 * 
	 * @param aid AID do agente jogador correspondente
	 */
	public Player(AID aid) {
		super();
		this.aid = aid;
		colorNr++;
		setColor(colorNr);
		setID(IDNr);
		IDNr++;
		//Numero de territorios total inicial
		totalCountries = 0;
		//Percentagem total do mundo conquistado inicial
		worldPercentage = (float) 0.0;
		//Rank inicial
		rank = 0;
	}

	private void setColor(int color)
	{
		switch (color) {
		case 1:
			this.color = Color.red;
			break;
		case 2:
			this.color = Color.blue;
			break;
		case 3:
			this.color = Color.green;
			break;
		case 4:
			this.color = Color.orange;
			break;
		case 5:
			this.color = Color.darkGray;
			break;
		case 6:
			this.color = Color.black;
			break;

		default:
			this.color = Color.yellow;
			break;
		}
	}

	public AID getAID() {
		return aid;
	}

	public void setAID(AID aid) {
		this.aid = aid;
	}

	public Color getColor() {
		return this.color;
	}

	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
	}

	public int getTotalCountries() {
		return totalCountries;
	}

	public void setTotalCountries(int totalCountries) {
		this.totalCountries = totalCountries;
	}
	
	/**
	 * Incremeta o numero de territorios em 1, e atualiza a percentagem do
	 * mundo conquistado
	 */
	public void incTotalCountries() {
		this.totalCountries++;
		this.worldPercentage = this.totalCountries/NUMBER_OF_TERRITORIES;
	}
	
	public void setWorldPercentage(float worldPercentage) {
		this.worldPercentage = worldPercentage;
	}

	public float getWorldPercentage() {
		return worldPercentage;
	}

	public int getRank() {
		return rank;
	}

	public void setRank(int rank) {
		this.rank = rank;
	}

	@Override
	public String toString()
	{
		if(this.color.equals(Color.red))
			return "Red";
		else if(this.color.equals(Color.green))
			return "Green";
		else if(this.color.equals(Color.orange))
			return "Orange";
		else if(this.color.equals(Color.darkGray))
			return "Gray";
		else if(this.color.equals(Color.blue))
			return "Blue";
		else if(this.color.equals(Color.black))
			return "Black";
		
		return "NA";
	}
}
