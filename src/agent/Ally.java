package agent;

import jade.core.AID;

/**
 * Classe Aliado
 *  
 * @author João Ladeiras
 * @author Rui Lima
 * 
 */

public class Ally
{
	private AID ally;
	private String enemy;
	
	/**
	 * COnstrutor
	 * 
	 * @param ally AID do aliado
	 * @param enemy AID do inimigo em comum
	 */
	public Ally(AID ally, String enemy) {
		super();
		this.ally = ally;
		this.enemy = enemy;
	}

	public AID getAlly() {
		return ally;
	}

	public void setAlly(AID ally) {
		this.ally = ally;
	}

	public String getEnemy() {
		return enemy;
	}

	public void setEnemy(String enemy) {
		this.enemy = enemy;
	}

	@Override
	public String toString()
	{
		String ally = "";
		
		ally += "Ally: "+this.getAlly().toString();
		ally += ", Enemy: "+this.getEnemy();
		
		return ally;
	}
}
