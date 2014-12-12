package agent;

import gui.Window;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.SimpleBehaviour;
import jade.domain.AMSService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.AMSAgentDescription;
import jade.domain.FIPAAgentManagement.SearchConstraints;
import jade.lang.acl.ACLMessage;

import java.util.ArrayList;
import java.util.Random;

import logic.Board;
import logic.Platform;
import logic.Player;

/**
 * Classe Agente Jogo
 *  
 * @author João Ladeiras
 * @author Rui Lima
 * 
 */

public class AgentGame extends Agent
{	
	//Parametros de inicialização
	private static final long serialVersionUID = -1772942192462864835L;
	
	public static int delay = 1000;
	
	//Constantes
	public static final int MAX_NR_OF_PLAYERS = 6;
	public static final int ADD_TROOPS_PHASE = 0;
	public static final int ATTACK_PHASE = 1;
	public static final int REINFORCE_PHASE = 2;

	//Indicadores
	public static Integer currentPlayer = 0;
	public static Integer currentPhase = 0;
	
	//Flags
	public static boolean playing = true;
	
	//Tabuleiro
	private Board board;
	
	/**
	 * Inicialização dos parametros e behaviours do agente
	 */
	protected void setup()
	{
		super.setup();

		//Inicialização dos jogadores
		Board.players = new ArrayList<Player>();
		//Inicialização do tabuleiro
		board = new Board();
		//Inicialização do baralho de cartas
		Board.newDeck();
		
		//Criar as peças na janela
		Window.createPieces(board.getWorld().getCountries());

		//Adicionar behaviors
		addBehaviour(new Start(this));

		System.out.println(this.getLocalName()+ " has started.");
	}
	
	/**
	 * Behaviour de inicio do agente
	 * 
	 * @author João Ladeiras
	 * @author Rui Lima
	 *
	 */
	public class Start extends SimpleBehaviour
	{
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
			//Informação do agente
			AMSAgentDescription [] agents = null;
			
			SearchConstraints c = new SearchConstraints();
            c.setMaxResults ( new Long(-1) );
            try {
				agents = AMSService.search( this.myAgent, new AMSAgentDescription (), c );
			} catch (FIPAException e) {
				e.printStackTrace();
			}
            
            //AIDs dos jogadores
            ArrayList<AID> agentAIDs = new ArrayList<AID>();
            
            //Nomes dos jogadores
            for (int i=0; i<agents.length;i++)
            {
            	String name = agents[i].getName().getName().split("@")[0];
            	
            	if(name.startsWith("Player"))
            		agentAIDs.add(agents[i].getName());
            }
            
            //Broadcast start
			Messaging.broadcast(this.myAgent, agentAIDs, Messaging.START);
			addBehaviour(new Join(this.myAgent));
			this.finished=true;
		}

		/**
		 * Terminação
		 */
		public boolean done() {
			return finished;
		}
	}

	/**
	 * Behaviour de pedido de entrada no jogo
	 * 
	 * @author João Ladeiras
	 * @author Rui Lima
	 */
	public class Join extends SimpleBehaviour
	{
		private static final long serialVersionUID = 9088209402507795289L;
		
		//Flag
		private boolean finished=false;

		/**
		 * Construtor
		 * 
		 * @param myagent
		 */
		public Join(final Agent myagent)
		{
			super(myagent);
		}

		/**
		 * Ação do behaviour
		 */
		public void action()
		{	
			//Inicialização da mensagem
			final ACLMessage msg;
			
			//Receber mensagem
			if((msg=Messaging.receiveMessage(this.myAgent)) != null)
			{
				//Mensagem JOIN
				if(msg.getContent().equals(Messaging.JOIN))
				{
					//Se não ultrapassa o numero maximo de jogadores permitido
					if(Board.players.size() <= MAX_NR_OF_PLAYERS)
					{
						//Adicionar novo jogador
						Board.players.add(new Player(msg.getSender()));
						//Responder positivamente ao jogador
						Messaging.sendMessage(this.myAgent, msg.getSender(), Messaging.ACCEPTED);
						System.out.println(msg.getSender().getName().split("@")[0]+" joined the game!");
					}
					else
					{
						//Responder negativamente ao jogador
						Messaging.sendMessage(this.myAgent, msg.getSender(), Messaging.REJECTED);
						this.finished=true;
					}
				}
			}
			else
			{
				//Terminar se maximo numero de jogadores e iniciar behaviour Play
				if(Board.players.size() == Platform.playerNr)
				{
					this.finished=true;
					addBehaviour(new Play(this.myAgent));
					initializeGame();
				}
			}
		}

		/**
		 * Terminação
		 */
		public boolean done() {
			return finished;
		}
	}
	
	/**
	 * Behaviour de jogodas
	 * 
	 * @author João Ladeiras
	 * @author Rui Lima
	 */
	public class Play extends SimpleBehaviour
	{
		private static final long serialVersionUID = 9088209402507795289L;
		
		//Flag
		private boolean finished=false;

		/**
		 * Construtor
		 * 
		 * @param myagent Agente atual
		 */
		public Play(final Agent myagent)
		{
			super(myagent);
		}

		/**
		 * Ação do behaviour
		 */
		public void action()
		{
			//Informação dos agentes
			AMSAgentDescription [] agents = null;
			
			SearchConstraints c = new SearchConstraints();
            c.setMaxResults ( new Long(-1) );
            try {
				agents = AMSService.search( this.myAgent, new AMSAgentDescription (), c );
			} catch (FIPAException e) {
				e.printStackTrace();
			}
            
            //AIDs dos jogadores
            ArrayList<AID> agentAIDs = new ArrayList<AID>();
            
            //Nomes dos jogadores
            for (int i=0; i<agents.length;i++)
            {
            	String name = agents[i].getName().getName().split("@")[0];
            	
            	if(name.startsWith("Player"))
            		agentAIDs.add(agents[i].getName());
            }
            
            //Delay para inicialização da GUI do Jade
            try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
            
            //Estado de jogo
            while(playing)
            {
            	//Fase de adição de tropas
            	if(currentPhase.equals(ADD_TROOPS_PHASE))
            	{
            		//Set Turno
            		Window.setTurn(Board.players.get(currentPlayer).toString());
            		//Set Fase
					Window.setPhase("Add Troops");
            		//Broadcast com update do tabuleiro
					Messaging.broadcast(this.myAgent, agentAIDs, Messaging.UPDATE+":"+board.toString());
					
					//Informar o proximo jogador
					Messaging.sendMessage(this.myAgent, Board.players.get(currentPlayer).getAID(), Messaging.ADD_TROOPS);
					//Esperar pela resposta
					ACLMessage msg = Messaging.blockingReceiveMessage(this.myAgent);
					
					//Esperar pela mensagem de terminação da fase
					while(!msg.getContent().equals(Messaging.FINISH_ADD_TROOPS))
					{
						//Atualização do tabuleiro
						if(msg.getContent().split(":")[0].equals(Messaging.UPDATE_ADD_TROOPS) && msg.getSender().equals(Board.players.get(currentPlayer).getAID()))
						{
							board.updateBoardFromString(msg.getContent().split(":")[1]);
							Window.updateBoard(board.getWorld().getCountries());
							try {
								Thread.sleep(delay);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
						//Esperar pela resposta
						msg = Messaging.blockingReceiveMessage(this.myAgent);
					}
					
					//Fase seguinte
            		currentPhase = ATTACK_PHASE;
            	}
            	else if(currentPhase.equals(ATTACK_PHASE))
            	{
            		//Set Fase
					Window.setPhase("Attack");
					//Broadcast do update
					Messaging.broadcast(this.myAgent, agentAIDs, Messaging.UPDATE+":"+board.toString());
					
					//Informar o proximo jogador
					Messaging.sendMessage(this.myAgent, Board.players.get(currentPlayer).getAID(), Messaging.ATTACK);
					//Esperar pela resposta
					ACLMessage msg = Messaging.blockingReceiveMessage(this.myAgent);
					
					//Esperar pela mensagem de terminação
					while(!msg.getContent().equals(Messaging.FINISH_ATTACK))
					{
						//Atualizar o tabuleiro
						if(msg.getContent().split(":")[0].equals(Messaging.UPDATE_ATTACK) && msg.getSender().equals(Board.players.get(currentPlayer).getAID()))
						{
							board.updateBoardFromString(msg.getContent().split(":")[1]);
							Window.updateBoard(board.getWorld().getCountries());
							try {
								Thread.sleep(delay);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
						
						//Esperar pela resposta
						msg = Messaging.blockingReceiveMessage(this.myAgent);
					}
					
					//Proxima fase
					currentPhase = REINFORCE_PHASE;
            	}
            	else if(currentPhase.equals(REINFORCE_PHASE))
            	{
            		//Set Fase
					Window.setPhase("Reinforce");
            		//Broadcast da atualização do tabuleira
					Messaging.broadcast(this.myAgent, agentAIDs, Messaging.UPDATE+":"+board.toString());
					
					//Inform nextPlayer
					Messaging.sendMessage(this.myAgent, Board.players.get(currentPlayer).getAID(), Messaging.REINFORCE);
					//Wait for response
					ACLMessage msg = Messaging.blockingReceiveMessage(this.myAgent);
					
					while(!msg.getContent().equals(Messaging.FINISH_REINFORCE))
					{
						if(msg.getContent().split(":")[0].equals(Messaging.UPDATE_REINFORCE) && msg.getSender().equals(Board.players.get(currentPlayer).getAID()))
						{
							board.updateBoardFromString(msg.getContent().split(":")[1]);
							Window.updateBoard(board.getWorld().getCountries());
							try {
								Thread.sleep(delay);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						
						msg = Messaging.blockingReceiveMessage(this.myAgent);
					}
					
					//Proxima fase
					currentPhase = ADD_TROOPS_PHASE;
					nextPlayer();
            	}
            	
            	//Estado do jogo
            	playing = ((AgentGame) this.myAgent).isGameNotOver();
            }
			
			this.finished = true;
		}

		/**
		 * Terminação
		 */
		public boolean done() {
			return finished;
		}
	}

	/**
	 * Verificar se o jogo acabou
	 * 
	 * @return jogo não acabou
	 */
	public boolean isGameNotOver()
	{
		Player player = board.getWorld().getCountries().get(0).getOwner();
		
		for(int i=1;i<board.getWorld().getCountries().size();i++)
		{
			if(!player.equals(board.getWorld().getCountries().get(i).getOwner()))
			{
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Inicialização dos elementos do jogo
	 */
	public void initializeGame()
	{
		//Inicializadores
		Integer max = 0;
		Integer min = 0;
		Integer randomNum;
		Integer territoryPerPlayer = 42/Board.players.size();
		Integer playerCounter = 0;
		Integer counter = 0;
		Integer troopsCounter = 0;
		Boolean flag = false;
		
		//Novo random
		Random rand = new Random();
		
		//Distrbuir países pelos jogadores
		for(int i=0;i<board.getWorld().getCountries().size();i++)
		{
			if(playerCounter.equals(Board.players.size()))
				playerCounter--;
			
			board.getWorld().getCountries().get(i).setOwner(Board.players.get(playerCounter));
			counter++;
			
			if(counter.equals(territoryPerPlayer))
			{
				playerCounter++;
				counter = 0;
			}
		}
		
		//Distribuir tropas pelos jogadores
		for(int j=0;j<Board.players.size();j++)
		{
			counter = 0;
			min = 0;
			
			for(int i=0;i<board.getWorld().getCountries().size();i++)
			{
				if(board.getWorld().getCountries().get(i).getOwner().equals(Board.players.get(j)))
					counter++;
					
					randomNum = rand.nextInt((max - min) + 1) + min;
				
			}
			
			max = 30-counter;
			troopsCounter = 0;
			flag = false;
			
			for(int i=0;i<board.getWorld().getCountries().size();i++)
			{
				if(board.getWorld().getCountries().get(i).getOwner().equals(Board.players.get(j)))
				{
					randomNum = rand.nextInt((max - min) + 1) + min;
					
					if(troopsCounter+randomNum > max)
					{
						randomNum = max - troopsCounter;
						flag = true;
					}
					
					troopsCounter += randomNum;
					
					board.getWorld().getCountries().get(i).setTroops(board.getWorld().getCountries().get(i).getTroops()+randomNum);
				}
				
				if(flag) break;
			}
		}
		
		//Refrescamento da janela de jogo
		Window.updateBoard(board.getWorld().getCountries());
	}
	
	/**
	 * Seleccionar próximo jogador na fila
	 */
	public static void nextPlayer()
	{
		if(currentPlayer+1 >= Board.players.size())
			currentPlayer = 0;
		else currentPlayer++;
	}
	
	/**
	 * Atribuir atraso
	 * @param d tempo em milisegundos entre movimentos
	 */
	public static void setDelay(int d)
	{
		delay = d;
	}

	/**
	 * Terminar agente
	 */
	protected void takeDown(){

	}
}
