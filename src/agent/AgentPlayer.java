package agent;

import java.util.ArrayList;

import jade.core.Agent;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import logic.Board;
import logic.Country;
import logic.Card;

public class AgentPlayer extends Agent
{
	private static final boolean log = true;
	
	private static final long serialVersionUID = 9088209402507795289L;
	private static final int MIN_BONUS_TROOPS = 3;
	private static final int TRADABLE_NUMBER = 3;
	
	//protected boolean onGame;
	private Board board;
	private ArrayList<Card> cards;
	private int turnedSetCounter = 0;
	
	protected void setup()
	{
		super.setup();
		
		//onGame = false;
		board = new Board();
		cards = new ArrayList<Card>();
		
		addBehaviour(new Start(this));
		//addBehaviour(new JoinGame(this));
		//addBehaviour(new Playing(this));

		System.out.println(this.getLocalName()+ " has started.");
	}

	protected void takeDown(){}
	
	/*
	 * Waits until it receives the message for starting
	 */
	public class Start extends SimpleBehaviour{
		
		private static final long serialVersionUID = 9088209402507795289L;
		private boolean finished=false;

		public Start(final Agent myagent) 
		{
			super(myagent);
		}

		public void action()
		{	
			final ACLMessage msg = Messaging.blockingReceiveMessage(this.myAgent);
			
			if(msg.getContent().equals(Messaging.START) && 
					msg.getSender().getName().split("@")[0].equals("Game"))
			{
				addBehaviour(new JoinGame(this.myAgent));
				this.finished=true;
			}
		}

		public boolean done() {
			return finished;
		}
	}

	public class JoinGame extends SimpleBehaviour{
		
		private static final long serialVersionUID = 9088209402507795289L;
		private boolean finished=false;

		public JoinGame(final Agent myagent) 
		{
			super(myagent);
		}

		public void action()
		{	
			Messaging.sendMessage(this.myAgent, "Game", Messaging.JOIN);
			addBehaviour(new Playing(this.myAgent));
			this.finished=true;
		}

		public boolean done() {
			return finished;
		}

	}
	
	public class Playing extends SimpleBehaviour{
		
		private static final long serialVersionUID = 9088209402507795289L;
		private boolean finished=false;

		public Playing(final Agent myagent) 
		{
			super(myagent);
		}

		public void action()
		{	
			final ACLMessage msg = Messaging.blockingReceiveMessage(this.myAgent);
			
			if(msg != null)
			{
				if (msg.getContent().equals(Messaging.REJECTED))
				{
					this.finished = true;
				}
				else if(msg.getContent().split(":")[0].equals(Messaging.UPDATE))
				{
					((AgentPlayer) this.myAgent).getBoard().
					updateBoardFromString(msg.getContent().split(":")[1]);
				}
				else if(msg.getContent().equals(Messaging.ADD_TROOPS))
				{
					if(gameNotOver())
					{
						distributeBonusTroops();
						Messaging.sendMessage(this.myAgent, "Game", Messaging.UPDATE_ADD_TROOPS+":"+((AgentPlayer) this.myAgent).getBoard().toString());
					}
					Messaging.sendMessage(this.myAgent, "Game", Messaging.FINISH_ADD_TROOPS);
				}
				else if(msg.getContent().equals(Messaging.ATTACK))
				{
					boolean pickCard = false;
					
					if(gameNotOver())
					{
						//random attack
						boolean flag = false;
						
						while(existPossibleMoves())
						{
							flag = false;
							//TODO evaluation needed
							
							for(int i=0;i<((AgentPlayer) this.myAgent).getBoard().getWorld().getCountries().size();i++)
							{
								if(((AgentPlayer) this.myAgent).getBoard().getWorld().getCountries().get(i).getOwner().getAID().equals(this.myAgent.getAID()))
								{
									if(((AgentPlayer) this.myAgent).getBoard().getWorld().getCountries().get(i).getTroops() > 1)
									{
										for(int j=0;j<((AgentPlayer) this.myAgent).getBoard().getWorld().getCountries().get(i).getOutnodes().size();j++)
										{
											if(!((AgentPlayer) this.myAgent).getBoard().getWorld().getCountries().get(i).getOutnodes().get(j).getOwner().getAID().equals(this.myAgent.getAID()))
											{
												//Attack
												((AgentPlayer) this.myAgent).getBoard().attack(((AgentPlayer) this.myAgent).getBoard().getWorld().getCountries().get(i), ((AgentPlayer) this.myAgent).getBoard().getWorld().getCountries().get(i).getOutnodes().get(j));
												
												//Win card in the end of turn?
												if(((AgentPlayer) this.myAgent).getBoard().getWorld().getCountries().get(i).getOutnodes().get(j).getTroops() == 0)
													pickCard = true;
												
												moveTroops(((AgentPlayer) this.myAgent).getBoard().getWorld().getCountries().get(i), ((AgentPlayer) this.myAgent).getBoard().getWorld().getCountries().get(i).getOutnodes().get(j));
												Messaging.sendMessage(this.myAgent, "Game", Messaging.UPDATE_ATTACK+":"+((AgentPlayer) this.myAgent).getBoard().toString());
												flag = true;
											}
											
											if(flag) break;
										}
									}
								}
								
								if(flag) break;
							}
						}
					}
					Messaging.sendMessage(this.myAgent, "Game", Messaging.FINISH_ATTACK);
					
					//Pick Card
					if(pickCard)
						cards.add(Board.pickCard());
					
					//this.finished = true;
				}
				else if(msg.getContent().equals(Messaging.REINFORCE))
				{
					if(gameNotOver())
					{
						reinforce();
						Messaging.sendMessage(this.myAgent, "Game", Messaging.UPDATE_REINFORCE+":"+((AgentPlayer) this.myAgent).getBoard().toString());
					}
					Messaging.sendMessage(this.myAgent, "Game", Messaging.FINISH_REINFORCE);
				}
			}
		}

		private boolean gameNotOver()
		{
			if(getOwnedCountries() == 0)
				return false;
			
			return true;
		}

		private void distributeBonusTroops()
		{
			int bonus = getBonusTroops();
			ArrayList<Country> countries = ((AgentPlayer) this.myAgent).getOwnedCountriesList();
			
			if(log) System.out.println("Bonus troops = "+bonus);
			
			while(bonus > 0)
			{	
				for(int i=0;i<countries.size();i++)
				{
					if(!surroundingContriesAreOwned(countries.get(i)))
					{
						countries.get(i).addTroops(1);
						bonus--;
					}
				}
			}
		}

		private boolean existPossibleMoves()
		{
			for(int i=0;i<((AgentPlayer) this.myAgent).getBoard().getWorld().getCountries().size();i++)
			{
				if(((AgentPlayer) this.myAgent).getBoard().getWorld().getCountries().get(i).getOwner().getAID().equals(this.myAgent.getAID()))
				{
					if(((AgentPlayer) this.myAgent).getBoard().getWorld().getCountries().get(i).getTroops() > 1)
					{
						for(int j=0;j<((AgentPlayer) this.myAgent).getBoard().getWorld().getCountries().get(i).getOutnodes().size();j++)
						{
							if(!((AgentPlayer) this.myAgent).getBoard().getWorld().getCountries().get(i).getOutnodes().get(j).getOwner().getAID().equals(this.myAgent.getAID()))
							{
								return true;
							}
						}
					}
				}
			}
			
			return false;
		}
		
		private void reinforce()
		{
			boolean flag = false;
			
			ArrayList<Country> countries = ((AgentPlayer) this.myAgent).getOwnedCountriesList();
			
			for(int i=0;i<countries.size();i++)
			{
				if(countries.get(i).getTroops() > 1)
				{
					if(surroundingContriesAreOwned(countries.get(i)))
					{
						for(int j=0;j<countries.size();j++)
						{
							if(!surroundingContriesAreOwned(countries.get(j)))
							{
								countries.get(j).addTroops(countries.get(i).getTroops()-1);
								countries.get(i).setTroops(1);
								
								flag = true;
							}
							if(flag) break;
						}
					}
				}
				if(flag) break;
			}
		}
		
		private boolean surroundingContriesAreOwned(Country country)
		{
			for(int i=0;i<country.getOutnodes().size();i++)
			{
				if(!country.getOutnodes().get(i).getOwner().equals(country.getOwner()))
					return false;
			}
			
			return true;
		}

		//TODO update this to match personality
		private void moveTroops(Country attacker, Country defendant)
		{
			//If win move all
			if(defendant.getTroops() == 0)
			{
				defendant.setTroops(attacker.getTroops()-1);
				attacker.setTroops(1);
			}
		}

		public boolean done() {
			return finished;
		}
	}
	
	private Integer getOwnedCountries()
	{
		Integer counter = 0;
		for(int i=0;i<board.getWorld().getCountries().size();i++)
		{
			if(board.getWorld().getCountries().get(i).getOwner().getAID().equals(this.getAID()))
				counter++;
		}
		
		return counter;
	}
	
	private ArrayList<Country> getOwnedCountriesList()
	{
		ArrayList<Country> list = new ArrayList<Country>();
		
		for(int i=0;i<board.getWorld().getCountries().size();i++)
		{
			if(board.getWorld().getCountries().get(i).getOwner().getAID().equals(this.getAID()))
				list.add(board.getWorld().getCountries().get(i));
		}
		
		return list;
	}
	
	private int getBonusTroops()
	{
		int southAmerica = 0;
		int northAmerica = 0;
		int europe = 0;
		int africa = 0;
		int asia = 0;
		int australia = 0;
		
		Integer total = 0;
		
		for(int i=0;i<board.getWorld().getCountries().size();i++)
		{
			if(board.getWorld().getCountries().get(i).getOwner().getAID().equals(this.getAID()))
			{
				if(board.getWorld().getCountries().get(i).getContinent().equals(Board.SOUTH_AMERICA))
					southAmerica++;
				if(board.getWorld().getCountries().get(i).getContinent().equals(Board.NORTH_AMERICA))
					northAmerica++;
				if(board.getWorld().getCountries().get(i).getContinent().equals(Board.EUROPE))
					europe++;
				if(board.getWorld().getCountries().get(i).getContinent().equals(Board.AFRICA))
					africa++;
				if(board.getWorld().getCountries().get(i).getContinent().equals(Board.ASIA))
					asia++;
				if(board.getWorld().getCountries().get(i).getContinent().equals(Board.AUSTRALIA))
					australia++;
			}
		}
		
		if(southAmerica == board.nrOfTerritoriesInContinent(Board.SOUTH_AMERICA))
			total += Board.SOUTH_AMERICA_BONUS;
		if(northAmerica == board.nrOfTerritoriesInContinent(Board.NORTH_AMERICA))
			total += Board.NORTH_AMERICA_BONUS;
		if(europe == board.nrOfTerritoriesInContinent(Board.EUROPE))
			total += Board.EUROPE_BONUS;
		if(africa == board.nrOfTerritoriesInContinent(Board.AFRICA))
			total += Board.AFRICA_BONUS;
		if(asia == board.nrOfTerritoriesInContinent(Board.ASIA))
			total += Board.ASIA_BONUS;
		if(australia == board.nrOfTerritoriesInContinent(Board.AUSTRALIA))
			total += Board.AUSTRALIA_BONUS;
		
		if(getOwnedCountries() <= MIN_BONUS_TROOPS)
			total += MIN_BONUS_TROOPS;
		else total += (int) (getOwnedCountries()/3);
		
		//Trade cards
		total += tradeCards();
		
		return total;
	}
	
	private int tradeCards()
	{
		int infantryNr = 0;
		int artilleryNr = 0;
		int cavalryNr = 0;
		int bonus = 0;
		
		boolean trade = false;
		
		if(log) System.out.print("Deck: ");
		
		for(int i=0;i<cards.size();i++)
		{
			if(!cards.get(i).isUsed())
			{
				if(cards.get(i).getSymbol() == Card.ARTILLERY)
					artilleryNr++;
				else if(cards.get(i).getSymbol() == Card.INFANTRY)
					infantryNr++;
				else if(cards.get(i).getSymbol() == Card.CAVALRY)
					cavalryNr++;
				
				if(log) System.out.print(cards.get(i).getSymbol()+" ,");
			}
		}
		
		if(log) System.out.println();
		
		if(infantryNr >= TRADABLE_NUMBER || artilleryNr >= TRADABLE_NUMBER || cavalryNr >= TRADABLE_NUMBER)
		{
			trade = true;
			int counter = 0;
			
			if(infantryNr >= TRADABLE_NUMBER)
			{
				for(int i=0;i<cards.size();i++)
				{
					if(!cards.get(i).isUsed() && cards.get(i).getSymbol() == Card.INFANTRY)
					{
						cards.get(i).setUsed(true);
						counter++;
					}
					
					if(counter == TRADABLE_NUMBER)
						break;
				}
			}
			else if(artilleryNr >= TRADABLE_NUMBER)
			{
				for(int i=0;i<cards.size();i++)
				{
					if(!cards.get(i).isUsed() && cards.get(i).getSymbol() == Card.ARTILLERY)
					{
						cards.get(i).setUsed(true);
						counter++;
					}
					
					if(counter == TRADABLE_NUMBER)
						break;
				}
			}
			else if(cavalryNr >= TRADABLE_NUMBER)
			{
				for(int i=0;i<cards.size();i++)
				{
					if(!cards.get(i).isUsed() && cards.get(i).getSymbol() == Card.CAVALRY)
					{
						cards.get(i).setUsed(true);
						counter++;
					}
					
					if(counter == TRADABLE_NUMBER)
						break;
				}
			}
		}
		else
		{
			boolean infantryFlag = false;
			boolean artilleryFlag = false;
			boolean cavalryFlag = false;
			
			if(infantryNr > 0 && artilleryNr > 0 && cavalryNr > 0)
			{
				trade = true;
				for(int i=0;i<cards.size();i++)
				{
					if(!cards.get(i).isUsed())
					{
						if(!infantryFlag && cards.get(i).getSymbol() == Card.INFANTRY)
						{
							infantryFlag = true;
							cards.get(i).setUsed(true);
						}
						else if(!artilleryFlag && cards.get(i).getSymbol() == Card.ARTILLERY)
						{
							artilleryFlag = true;
							cards.get(i).setUsed(true);
						}
						else if(!cavalryFlag && cards.get(i).getSymbol() == Card.CAVALRY)
						{
							cavalryFlag = true;
							cards.get(i).setUsed(true);
						}
					}
					
					if(infantryFlag && artilleryFlag && cavalryFlag)
						break;
				}
			}
		}
		
		if(trade)
		{
			turnedSetCounter++;
			
			switch (turnedSetCounter)
			{
				case 1:
					bonus += 4;
					break;
				case 2:
					bonus += 6;
					break;
				case 3:
					bonus += 8;
					break;
				case 4:
					bonus += 10;
					break;
				case 5:
					bonus += 12;
					break;
				case 6:
					bonus += 15;
					break;
				default:
					bonus += (15 + ((turnedSetCounter - 6)*5));
					break;
			}
			
			if(log) System.out.println("Traded cards with a bonus of "+bonus+" troops.");
		}
		
		return bonus;
	}

	private Board getBoard()
	{
		return this.board;
	}
}
