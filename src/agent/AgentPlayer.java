package agent;

import java.util.ArrayList;
import java.util.List;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import logic.Board;
import logic.Country;
import logic.Card;
import logic.Platform;
import logic.Player;

/**
 * Classe Agente Jogador
 *  
 * @author João Ladeiras
 * @author Rui Lima
 * 
 */

public class AgentPlayer extends Agent
{
	//Debug log flag
	private static final boolean log = true;
	
	//Constantes
	private static final long serialVersionUID = 9088209402507795289L;
	private static final int MIN_BONUS_TROOPS = 3;
	private static final int TRADABLE_NUMBER = 3;
	private static final int CONQUER_WORLD = 0;
	private static final int CONQUER_CONTINENT = 1;
	
	//Elementos do jogador
	private Board board;
	private ArrayList<Card> cards;
	private int turnedSetCounter = 0;
	private String type;
	protected List<String> data;
	private ArrayList<Ally> allies;
	private ArrayList<Ally> blackList;
	private boolean initAlly;
	
	@SuppressWarnings("unchecked")
	protected void setup()
	{
		super.setup();
		
		//Inicialização dos elementos
		final Object[] args = getArguments();
		data = (List<String>) args[0];
		type = data.get(0);
		//onGame = false;
		board = new Board();
		cards = new ArrayList<Card>();
		initAlly = false;
		allies = new ArrayList<Ally>();
		blackList = new ArrayList<Ally>();
		
		//Adicinar behaviour de inicio
		addBehaviour(new Start(this));

		System.out.println(this.getLocalName()+ " has started.");
	}

	protected void takeDown(){}
	
	/**
	 * Espera até receber a mensagem de inicio de jogo
	 */
	public class Start extends SimpleBehaviour{
		
		private static final long serialVersionUID = 9088209402507795289L;
		
		//Flag
		private boolean finished=false;

		/**
		 * Construtor
		 * 
		 * @param myagent Agente atual
		 */
		public Start(final Agent myagent) 
		{
			super(myagent);
		}

		/**
		 * Ação do behaviour
		 */
		public void action()
		{
			//Receber mensagem
			final ACLMessage msg = Messaging.blockingReceiveMessage(this.myAgent);
			
			//Mensagem de inicio
			if(msg.getContent().equals(Messaging.START) && 
					msg.getSender().getName().split("@")[0].equals("Game"))
			{
				//Adicionar behaviour para adicionar ao jogo
				addBehaviour(new JoinGame(this.myAgent));
				this.finished=true;
			}
		}

		/**
		 * Finalização
		 */
		public boolean done() {
			return finished;
		}
	}

	/**
	 * Behaviour de adição ao jogo
	 *  
	 * @author João Ladeiras
	 * @author Rui Lima
	 *
	 */
	public class JoinGame extends SimpleBehaviour{
		
		private static final long serialVersionUID = 9088209402507795289L;
		
		//Flag
		private boolean finished=false;

		/**
		 * Construtor
		 * 
		 * @param myagent Agente atual
		 */
		public JoinGame(final Agent myagent) 
		{
			super(myagent);
		}

		/**
		 * Ação do behaviour
		 */
		public void action()
		{	
			//Envio de mensagem de adição
			Messaging.sendMessage(this.myAgent, "Game", Messaging.JOIN);
			//Iniciar behaviour de jogada
			addBehaviour(new Playing(this.myAgent));
			this.finished=true;
		}

		/**
		 * Finalização
		 */
		public boolean done() {
			return finished;
		}

	}
	
	/**
	 * Behaviour de jogada
	 * 
	 * @author João Ladeiras
	 * @author Rui Lima
	 */
	public class Playing extends SimpleBehaviour{
		
		private static final long serialVersionUID = 9088209402507795289L;
		
		//Flag
		private boolean finished=false;

		/**
		 * Construtor
		 * @param myagent Agente atual
		 */
		public Playing(final Agent myagent) 
		{
			super(myagent);
		}

		/**
		 * Ação do behaviour
		 */
		public void action()
		{
			//Receber mensagem
			final ACLMessage msg = Messaging.blockingReceiveMessage(this.myAgent);
			
			if(msg != null)
			{
				if(!initAlly)
				{
					if(Platform.alliesMode)
						addBehaviour(new AllyBehaviour(this.myAgent));
					initAlly = true;
				}
				
				if (msg.getContent().equals(Messaging.REJECTED))
				{
					//Terminar se rejeitado
					this.finished = true;
				}
				else if(msg.getContent().split(":")[0].equals(Messaging.UPDATE))
				{
					//Update do tabuleiro
					if(!allies.isEmpty())
						brokeAlliance(msg.getContent().split(":")[1]);
					
					((AgentPlayer) this.myAgent).getBoard().
					updateBoardFromString(msg.getContent().split(":")[1]);
				}
				else if(msg.getContent().equals(Messaging.ADD_TROOPS))
				{
					//Adicionar tropas
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
					
					//Atacar
					if(gameNotOver())
					{
						pickCard = attack();
					}
					Messaging.sendMessage(this.myAgent, "Game", Messaging.FINISH_ATTACK);
					
					//Retirar uma carta
					if(pickCard)
						cards.add(Board.pickCard());
				}
				else if(msg.getContent().equals(Messaging.REINFORCE))
				{
					//Reforço
					if(gameNotOver())
					{
						reinforce();
						Messaging.sendMessage(this.myAgent, "Game", Messaging.UPDATE_REINFORCE+":"+((AgentPlayer) this.myAgent).getBoard().toString());
					}
					Messaging.sendMessage(this.myAgent, "Game", Messaging.FINISH_REINFORCE);
				}
				else if(msg.getContent().split("::")[0].equals(Messaging.ALLY))
				{
					//Adicionar aliado
					if(gameNotOver())
					{
						if(!isAlly(msg.getSender()) && !isEnemy(msg.getSender()))
						{
							Messaging.sendMessage(this.myAgent, msg.getSender(), Messaging.ACCEPT_ALLY+"::"+msg.getContent().split("::")[1]);
							
							AID allyAID = msg.getSender();
							String enemyAID = msg.getContent().split("::")[1];
							Ally ally = new Ally(allyAID, enemyAID);
							
							((AgentPlayer) this.myAgent).allies.add(ally);
							
							String agentBeingAsked = ((AgentPlayer) this.myAgent).getAID().toString().split("name ")[1].split("@")[0];
							String agentAsking = msg.getSender().toString().split("name ")[1].split("@")[0];
							
							System.out.println(agentAsking+" and "+agentBeingAsked+" are now allies!!");
						}
						else
						{
							Messaging.sendMessage(this.myAgent, msg.getSender(), Messaging.REJECT_ALLY+"::"+msg.getContent().split("::")[1]);
						}
					}
					else ((AgentPlayer) this.myAgent).allies.clear();
				}
				else if(msg.getContent().split("::")[0].equals(Messaging.ACCEPT_ALLY))
				{
					//Aceitar aliado
					if(gameNotOver())
					{
						if(!isAlly(msg.getSender()) && !isEnemy(msg.getSender()))
						{
							AID allyAID = msg.getSender();
							String enemyAID = msg.getContent().split("::")[1];
							Ally ally = new Ally(allyAID, enemyAID);
							
							((AgentPlayer) this.myAgent).allies.add(ally);
						}
					}
					else ((AgentPlayer) this.myAgent).allies.clear();
				}
				else if(msg.getContent().split("::")[0].equals(Messaging.REJECT_ALLY))
				{
					//Rejeitar aliado
					if(gameNotOver())
					{
						AID allyAID = msg.getSender();
						String enemyAID = msg.getContent().split("::")[1];
						Ally ally = new Ally(allyAID, enemyAID);
						
						((AgentPlayer) this.myAgent).blackList.add(ally);
					}
					else ((AgentPlayer) this.myAgent).allies.clear();
				}
			}
		}

		/**
		 * Verifica se uma agente é aliado
		 * 
		 * @param sender Agente jogador
		 * @return Aliado
		 */
		private boolean isAlly(AID sender)
		{
			for(int i=0;i<((AgentPlayer) this.myAgent).allies.size();i++)
				if(((AgentPlayer) this.myAgent).allies.get(i).getAlly().equals(sender))
					return true;
			
			return false;
		}
		
		/**
		 * Verifica se um agente é inimigo
		 * 
		 * @param sender Agente jogador
		 * @return Inimigo
		 */
		private boolean isEnemy(AID sender)
		{
			for(int i=0;i<((AgentPlayer) this.myAgent).allies.size();i++)
				if(((AgentPlayer) this.myAgent).allies.get(i).getEnemy().equals(sender.toString()))
					return true;
			
			return false;
		}

		/**
		 * Verifica se algum aliado quebrou a aliança
		 * 
		 * @param str String do update do tabuleiro
		 */
		private void brokeAlliance(String str)
		{
			String countries[] = str.split(";");
			
			for(int i=0;i<countries.length;i++)
			{
				String settings[] = countries[i].split(",");
				Player newOwner = Board.players.get(Integer.valueOf(settings[0])); 
				
				if(((AgentPlayer) this.myAgent).getBoard().getWorld().getCountries().get(i).getOwner().getAID().equals(((AgentPlayer) this.myAgent).getAID()))
					if(((AgentPlayer) this.myAgent).allies.contains(newOwner))
						((AgentPlayer) this.myAgent).allies.remove(newOwner.getAID());
			}
		}

		/**
		 * Função que executa o ataque.
		 * 
		 * @return Retirar carta
		 */
		private boolean attack()
		{
			boolean flag = false;
			boolean pickCard = false;
			boolean forceAttack = false;
			int attackPhase = CONQUER_CONTINENT;
			
			//Tentar conquistar os continentes primeiro
			while(existPossibleMoves())
			{
				ArrayList<Country> countries = ((AgentPlayer) this.myAgent).getOwnedCountriesList();
				int counter = 0;
				
				for(int i=0;i<countries.size();i++)
					if(surroundingContriesAreFriendly(countries.get(i)))
						counter++;
				
				if(counter == countries.size())
					removeAllyPreventingToMove();
				
				flag = false;
				int possibleMoves = possibleMoves();
				int movesCounter = 0;
				
				ArrayList<Float> countriesPerContinent = getPercentageCountriesPerContinent();
				ArrayList<String> continentsToAttack = new ArrayList<String>();
				
				if(!forceAttack)
				{
					for(int i=0;i<countriesPerContinent.size();i++)
					{
						if(countriesPerContinent.get(i) > 0.5 && countriesPerContinent.get(i) < 1.0)
							continentsToAttack.add(getContinentName(i));
					}
				}
				
				if(!continentsToAttack.isEmpty())
					attackPhase = CONQUER_CONTINENT;
				else attackPhase = CONQUER_WORLD;
				
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
									movesCounter++;
									
									if((continentsToAttack.contains(((AgentPlayer) this.myAgent).getBoard().getWorld().getCountries().get(i).getOutnodes().get(j).getContinent()) && attackPhase == CONQUER_CONTINENT) ||
											attackPhase == CONQUER_WORLD)
									{
										if(shouldAttack(((AgentPlayer) this.myAgent).getBoard().getWorld().getCountries().get(i), ((AgentPlayer) this.myAgent).getBoard().getWorld().getCountries().get(i).getOutnodes().get(j), attackPhase))
										{
											//Atacar
											((AgentPlayer) this.myAgent).getBoard().attack(((AgentPlayer) this.myAgent).getBoard().getWorld().getCountries().get(i), ((AgentPlayer) this.myAgent).getBoard().getWorld().getCountries().get(i).getOutnodes().get(j));
											
											//Ganha carta no fim do turno?
											if(((AgentPlayer) this.myAgent).getBoard().getWorld().getCountries().get(i).getOutnodes().get(j).getTroops() == 0)
												pickCard = true;
											
											moveTroops(((AgentPlayer) this.myAgent).getBoard().getWorld().getCountries().get(i), ((AgentPlayer) this.myAgent).getBoard().getWorld().getCountries().get(i).getOutnodes().get(j));
											Messaging.sendMessage(this.myAgent, "Game", Messaging.UPDATE_ATTACK+":"+((AgentPlayer) this.myAgent).getBoard().toString());
											flag = true;
										}
									}
								}
								if(flag) break;
							}
						}
						if(flag) break;
					}
				}
				
				forceAttack = false;
				
				if(possibleMoves == movesCounter)
					break;
				
				//Forçar ataque se já não existem ataques possiveis dentro dos continentes
				if(movesCounter < possibleMoves && attackPhase == CONQUER_CONTINENT)
				{
					forceAttack = true;
				}
				else if(movesCounter == 0 && attackPhase == CONQUER_WORLD)
					break;
			}

			return pickCard;
		}
		
		/**
		 * Elimina um aliado que não permite movimentar
		 */
		private void removeAllyPreventingToMove()
		{
			ArrayList<Country> countries = ((AgentPlayer) this.myAgent).getOwnedCountriesList();
			
			for(int i=0;i<countries.size();i++)
			{
				for(int j=0;j<countries.get(i).getOutnodes().size();j++)
					if(isAlly(countries.get(i).getOutnodes().get(j).getOwner().getAID()))
					{
						removeAlly(countries.get(i).getOutnodes().get(j).getOwner().getAID());
					}
			}
		}

		/**
		 * Verifica se deve ou não atacar um dado território
		 * 
		 * @param country1 País de onde envia tropas
		 * @param country2 País para o qual envia tropas
		 * @param attackPhase Fase do ataque (Continente ou Mundo)
		 * @return Atacar
		 */
		private boolean shouldAttack(Country country1, Country country2, int attackPhase)
		{
			//Eliminar aliança
			if(isAlly(country2.getOwner().getAID()))
			{
				boolean flag = false;
				
				for(int i=0;i<((AgentPlayer) this.myAgent).getBoard().getWorld().getCountries().size();i++)
				{
					if(((AgentPlayer) this.myAgent).getBoard().getWorld().getCountries().get(i).getOwner().getAID().toString().equals(getAllyEnemy(country2.getOwner().getAID())))
						flag = true;
				}
				
				if(!flag)
					removeAlly(country2.getOwner().getAID());
			}
			
			if(isAlly(country2.getOwner().getAID()))
				return false;
			
			int difference = country1.getTroops() - country2.getTroops();
			
			if(((AgentPlayer) this.myAgent).getType().equals("Offensive"))
			{
				if(attackPhase == CONQUER_WORLD && difference > 0)
				{
					return true;
				}
				else if(attackPhase == CONQUER_CONTINENT && difference > -1)
				{
					return true;
				}
			}
			else if(((AgentPlayer) this.myAgent).getType().equals("Normal"))
			{
				if(attackPhase == CONQUER_WORLD && difference > 3)
				{
					return true;
				}
				else if(attackPhase == CONQUER_CONTINENT && difference > 1)
				{
					return true;
				}
			}
			else if(((AgentPlayer) this.myAgent).getType().equals("Defensive"))
			{
				if(attackPhase == CONQUER_WORLD && difference > 5)
				{
					return true;
				}
				else if(attackPhase == CONQUER_CONTINENT && difference > 2)
				{
					return true;
				}
			}
			
			return false;	
		}

		/**
		 * Remove um aliado da lista de aliados
		 * 
		 * @param allyAID AID do agente aliado
		 */
		private void removeAlly(AID allyAID)
		{
			String formerAlly = null;
			
			for(int i=0;i<((AgentPlayer) this.myAgent).allies.size();i++)
			{
				if(((AgentPlayer) this.myAgent).allies.get(i).getAlly().equals(allyAID))
				{
					formerAlly = ((AgentPlayer) this.myAgent).allies.get(i).getAlly().toString();
					String thisAgent = ((AgentPlayer) this.myAgent).getAID().toString().split("name ")[1].split("@")[0];
					formerAlly = ((AgentPlayer) this.myAgent).getAID().toString().split("name ")[1].split("@")[0];
					System.out.println(thisAgent+" and "+formerAlly+" are no longer allies!!");
					
					((AgentPlayer) this.myAgent).allies.remove(i);
					return;
				}
			}
		}

		/**
		 * Retorna o inimigo de uma dada aliança com um agente
		 * 
		 * @param allyAID Agente aliado
		 * @return Inimigo
		 */
		private String getAllyEnemy(AID allyAID)
		{
			for(int i=0;i<((AgentPlayer) this.myAgent).allies.size();i++)
				if(((AgentPlayer) this.myAgent).allies.get(i).getAlly().equals(allyAID))
					return ((AgentPlayer) this.myAgent).allies.get(i).getEnemy();
			
			return null;
		}

		/**
		 * Percentagem conquistada de cada continente
		 * 
		 * @return Lista de percentagens de cada continente
		 */
		private ArrayList<Float> getPercentageCountriesPerContinent()
		{
			ArrayList<Country> ownedCountries = getOwnedCountriesList();
			ArrayList<Float> countriesPerContinent = new ArrayList<Float>();
			
			//Initializar contadores
			for(int i=0;i<6;i++)
				countriesPerContinent.add((float) 0);
			
			//Contar
			for(int i=0;i<ownedCountries.size();i++)
			{
				if(ownedCountries.get(i).getContinent().equals(Board.ASIA))
					countriesPerContinent.set(0, countriesPerContinent.get(0)+1);
				else if(ownedCountries.get(i).getContinent().equals(Board.EUROPE))
					countriesPerContinent.set(1, countriesPerContinent.get(1)+1);
				else if(ownedCountries.get(i).getContinent().equals(Board.NORTH_AMERICA))
					countriesPerContinent.set(2, countriesPerContinent.get(2)+1);
				else if(ownedCountries.get(i).getContinent().equals(Board.AFRICA))
					countriesPerContinent.set(3, countriesPerContinent.get(3)+1);
				else if(ownedCountries.get(i).getContinent().equals(Board.SOUTH_AMERICA))
					countriesPerContinent.set(4, countriesPerContinent.get(4)+1);
				else if(ownedCountries.get(i).getContinent().equals(Board.AUSTRALIA))
					countriesPerContinent.set(5, countriesPerContinent.get(5)+1);
			}
			
			//Percentagens
			countriesPerContinent.set(0, countriesPerContinent.get(0) / ((AgentPlayer) this.myAgent).nrOfTerritoriesInContinent(Board.ASIA));
			countriesPerContinent.set(1, countriesPerContinent.get(1) / ((AgentPlayer) this.myAgent).nrOfTerritoriesInContinent(Board.EUROPE));
			countriesPerContinent.set(2, countriesPerContinent.get(2) / ((AgentPlayer) this.myAgent).nrOfTerritoriesInContinent(Board.NORTH_AMERICA));
			countriesPerContinent.set(3, countriesPerContinent.get(3) / ((AgentPlayer) this.myAgent).nrOfTerritoriesInContinent(Board.AFRICA));
			countriesPerContinent.set(4, countriesPerContinent.get(4) / ((AgentPlayer) this.myAgent).nrOfTerritoriesInContinent(Board.SOUTH_AMERICA));
			countriesPerContinent.set(5, countriesPerContinent.get(5) / ((AgentPlayer) this.myAgent).nrOfTerritoriesInContinent(Board.AUSTRALIA));
			
			return countriesPerContinent;
		}
		
		/**
		 * Retorna o nome de um continente pela posição
		 * 
		 * @param nr posição
		 * @return Nome do continente
		 */
		private String getContinentName(int nr)
		{
			switch (nr) {
			case 0:
				return Board.ASIA;
			case 1:
				return Board.EUROPE;
			case 2:
				return Board.NORTH_AMERICA;
			case 3:
				return Board.AFRICA;
			case 4:
				return Board.SOUTH_AMERICA;
			case 5:
				return Board.AUSTRALIA;
			}
			
			return null;
		}

		/**
		 * Verifica se o jogo já terminou
		 * 
		 * @return Terminou
		 */
		private boolean gameNotOver()
		{
			if(getOwnedCountries() == 0)
				return false;
			
			return true;
		}

		/**
		 * Distribui as tropas de bonus pelos territorios que fazem fronteira
		 * com pelo menos um territorio hostil.
		 */
		private void distributeBonusTroops()
		{
			int bonus = getBonusTroops();
			ArrayList<Country> countries = ((AgentPlayer) this.myAgent).getOwnedCountriesList();
			
			if(log) System.out.println("Bonus troops = "+bonus);
			
			while(bonus > 0)
			{
				int counter = 0;
				
				for(int i=0;i<countries.size();i++)
				{
					if(!surroundingContriesAreFriendly(countries.get(i)))
					{
						countries.get(i).addTroops(1);
						bonus--;
					}
					else counter++;
				}
				
				if(counter == countries.size())
					removeAllyPreventingToMove();
			}
		}

		/**
		 * Verifica se ainda existem movimentos possiveis
		 * 
		 * @return Existem movimentos
		 */
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
		
		/**
		 * Retorna o numero de jogadas ainda possiveis
		 * 
		 * @return numero de jogadas possoiveis
		 */
		private int possibleMoves()
		{
			int counter = 0;
			
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
								counter++;
							}
						}
					}
				}
			}
			
			return counter;
		}
		
		/**
		 * Move tropas para um territorio que faz fronteira com pelo menos um 
		 * territorio hostil 
		 */
		private void reinforce()
		{
			boolean flag = false;
			int counter = 0;
			
			ArrayList<Country> countries = ((AgentPlayer) this.myAgent).getOwnedCountriesList();
			
			
			for(int i=0;i<countries.size();i++)
				if(surroundingContriesAreFriendly(countries.get(i)))
					counter++;
			
			if(counter == countries.size())
				removeAllyPreventingToMove();
			
			for(int i=0;i<countries.size();i++)
			{
				if(countries.get(i).getTroops() > 1)
				{
					if(surroundingContriesAreFriendly(countries.get(i)))
					{
						for(int j=0;j<countries.size();j++)
						{
							if(!surroundingContriesAreFriendly(countries.get(j)))
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
		
		/**
		 * Verifica se os territórios envolventes são do prórpio agente ou de 
		 * um aliado
		 * 
		 * @param country
		 * @return Friendly
		 */
		private boolean surroundingContriesAreFriendly(Country country)
		{
			for(int i=0;i<country.getOutnodes().size();i++)
			{
				if(!country.getOutnodes().get(i).getOwner().equals(country.getOwner()) &&
						!isAlly(country.getOutnodes().get(i).getOwner().getAID()))
					return false;
			}
			
			return true;
		}

		/**
		 * Move as tropas de um território para outro, após uma batalha
		 * 
		 * @param attacker Território de onde foram enviadas tropas
		 * @param defendant Território para onde foram enviadas tropas
		 */
		private void moveTroops(Country attacker, Country defendant)
		{
			//Se ganhou
			if(defendant.getTroops() == 0)
			{
				if(surroundingContriesAreFriendly(defendant))
				{
					defendant.setTroops(attacker.getTroops()-1);
					attacker.setTroops(1);
				}
				else
				{
					int availableTroops = attacker.getTroops();
					int surroundingDefendant = getTotalEnemyTroopsSurronding(defendant);
					int surroundingAttacker = getTotalEnemyTroopsSurronding(attacker);
					int surrounfingTotal = surroundingDefendant + surroundingAttacker;
					int defendantTroops = (surroundingDefendant*availableTroops)/surrounfingTotal;
					int attackerTroops = (surroundingAttacker*availableTroops)/surrounfingTotal;
					
					if(defendantTroops + attackerTroops < availableTroops)
						defendantTroops += availableTroops - (defendantTroops + attackerTroops);
					
					if(defendantTroops < 1)
					{
						if(attackerTroops > 1)
						{
							defendantTroops++;
							attackerTroops--;
						}
					}
					else if(attackerTroops < 1)
					{
						if(defendantTroops > 1)
						{
							defendantTroops--;
							attackerTroops++;
						}
					}
					
					defendant.setTroops(defendantTroops);
					attacker.setTroops(attackerTroops);
				}
			}
		}

		/**
		 * Calcúla o numero total de tropas inimigas que estão nos territórios
		 * que envolvem um determinado território
		 * 
		 * @param defendant Território a ser verificado
		 * @return numero total de tropas inimigas
		 */
		private int getTotalEnemyTroopsSurronding(Country defendant)
		{
			int counter = 0;
			
			for(int i=0; i<defendant.getOutnodes().size();i++)
			{
				if(!defendant.getOutnodes().get(i).getOwner().getAID().equals(((AgentPlayer) this.myAgent).getAID())
					&& !isAlly(defendant.getOwner().getAID()))
					counter += defendant.getOutnodes().get(i).getTroops();
			}
			
			return counter;
		}

		/**
		 * Finalização
		 */
		public boolean done() {
			return finished;
		}
	}
	
	/**
	 * Behaviour de alianças
	 * 
	 * @author João Ladeiras
	 * @author Rui Lima
	 *
	 */
	public class AllyBehaviour extends SimpleBehaviour{
		
		private static final long serialVersionUID = 9088209402507795289L;
		
		//Flag
		private boolean finished=false;

		/**
		 * Construtor
		 * @param myagent Agente atual
		 */
		public AllyBehaviour(final Agent myagent) 
		{
			super(myagent);
		}

		/**
		 * Ação do behaviour
		 */
		public void action()
		{	
			Ally ally = null;
			
			if((ally = chooseAlly()) != null)
			{
				if(!blackList.contains(ally))
					Messaging.sendMessage(this.myAgent, ally.getAlly(), Messaging.ALLY+"::"+ally.getEnemy());
			}
		}

		/**
		 * Finalização
		 */
		public boolean done() {
			return finished;
		}
		
		/**
		 * Faz a escolha do melhor aliado
		 * 
		 * @return Objeto do aliado com respetivo inimigo em comum
		 */
		private Ally chooseAlly()
		{
			ArrayList<Player> players = new ArrayList<Player>();
			ArrayList<Player> sortedPlayers = new ArrayList<Player>();
			
			//Jogadores
			for(int i=0; i<((AgentPlayer) this.myAgent).getBoard().getWorld().getCountries().size(); i++)
			{
				((AgentPlayer) this.myAgent).getBoard().getWorld().getCountries().get(i).getOwner().setTotalCountries(0);
				((AgentPlayer) this.myAgent).getBoard().getWorld().getCountries().get(i).getOwner().setWorldPercentage((float) 0.0);;
				
				if(!players.contains(((AgentPlayer) this.myAgent).getBoard().getWorld().getCountries().get(i).getOwner()) &&
						!((AgentPlayer) this.myAgent).getBoard().getWorld().getCountries().get(i).getOwner().getAID().equals(((AgentPlayer) this.myAgent).getAID()))
					players.add(((AgentPlayer) this.myAgent).getBoard().getWorld().getCountries().get(i).getOwner());
			}
			
			for(int i=0; i<((AgentPlayer) this.myAgent).getBoard().getWorld().getCountries().size(); i++)
			{
				for(int j=0; j<players.size(); j++)
				{
					if(players.get(j).getAID().equals(((AgentPlayer) this.myAgent).getBoard().getWorld().getCountries().get(i).getOwner().getAID()))
					{
						players.get(j).incTotalCountries();
						break;
					}
				}
			}
			
			while(players.size() != sortedPlayers.size())
			{
				float temp = (float) 0.0;
				int tempIndex = -1;
				
				for(int i=0; i<players.size(); i++)
				{	
					if(players.get(i).getWorldPercentage() >= temp && !sortedPlayers.contains(players.get(i)))
					{
						temp = players.get(i).getWorldPercentage();
						tempIndex = i;
					}
				}
				
				sortedPlayers.add(players.get(tempIndex));
			}
			
			/*
			 * Escolha do segundo agente mais forte como aliado e o mais
			 * forte como inimigo
			 */
			if(sortedPlayers.size() > 1)
			{
				if(sortedPlayers.get(0).getWorldPercentage() > 0.7)
				{
					if(!isEnemy(sortedPlayers.get(1).getAID()) && !isAlly(sortedPlayers.get(0).getAID()))
						return new Ally(sortedPlayers.get(1).getAID(), sortedPlayers.get(0).getAID().toString());
				}
			}
			
			return null;
		}
		
		/**
		 * Verifica se um dado agente é aliado
		 * 
		 * @param sender AID do agente
		 * @return Aliado
		 */
		private boolean isAlly(AID sender)
		{
			for(int i=0;i<((AgentPlayer) this.myAgent).allies.size();i++)
				if(((AgentPlayer) this.myAgent).allies.get(i).getAlly().equals(sender))
					return true;
			
			return false;
		}

		/**
		 * Verifica se um dado agente é inimigo
		 * 
		 * @param sender AID do agente
		 * @return Inimigo
		 */
		private boolean isEnemy(AID sender)
		{
			for(int i=0;i<((AgentPlayer) this.myAgent).allies.size();i++)
				if(((AgentPlayer) this.myAgent).allies.get(i).getEnemy().equals(sender.toString()))
					return true;
			
			return false;
		}
	}
	
	/**
	 * Calcula o numero de territórios conquistados
	 * 
	 * @return numero de territorios conquistados
	 */
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
	
	/**
	 * Retorna uma lista com os territorios conquistados
	 * 
	 * @return
	 */
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
	
	/**
	 * Calcula o numero de tropas bonus, adicionando bonus de numero de paises,
	 * de troca de cartas e de fim de turno.
	 * 
	 * @return Numero total de tropas bonus
	 */
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
		
		//Troca cartas
		total += tradeCards();
		
		return total;
	}
	
	/**
	 * Troca cartas se for possivel
	 * 
	 * @return bonus da troca de cartas
	 */
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

	/**
	 * Tabuleiro do agente
	 * 
	 * @return tabuleiro
	 */
	private Board getBoard()
	{
		return this.board;
	}
	
	/**
	 * Tipo de agente jogador
	 * 
	 * @return tipo
	 */
	private String getType()
	{
		return this.type;
	}
	
	/**
	 * Calcula o numero de territorios que o agente conquistou num dado
	 * continente
	 * 
	 * @param Nome do continente
	 * @return numero de territorios
	 */
	private int nrOfTerritoriesInContinent(String continent)
	{
		int counter = 0;
		
		for(int i=0;i<this.getBoard().getWorld().getCountries().size();i++)
			if(this.getBoard().getWorld().getCountries().get(i).getContinent().equals(continent))
				counter++;
		
		return counter;
	}
}
