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


public class AgentGame extends Agent
{	
	private static final long serialVersionUID = -1772942192462864835L;

	public static final int delay = 200;
	
	public static final int MAX_NR_OF_PLAYERS = 6;
	public static final int ADD_TROOPS_PHASE = 0;
	public static final int ATTACK_PHASE = 1;
	public static final int REINFORCE_PHASE = 2;

	public static Integer currentPlayer = 0;
	public static Integer currentPhase = 0;
	
	public static boolean playing = true;
	
	private Board board;
	
	protected void setup()
	{
		super.setup();

		Board.players = new ArrayList<Player>();
		board = new Board();
		Board.newDeck();
		
		//Create Pieces in Window
		Window.createPieces(board.getWorld().getCountries());

		//Add the behaviors
		addBehaviour(new Start(this));

		System.out.println(this.getLocalName()+ " has started.");
	}
	
	public class Start extends SimpleBehaviour
	{
		private static final long serialVersionUID = 9088209402507795289L;
		private boolean finished=false;

		public Start(final Agent myagent)
		{
			super(myagent);
		}


		public void action()
		{
			//Get agents information
			AMSAgentDescription [] agents = null;
			
			SearchConstraints c = new SearchConstraints();
            c.setMaxResults ( new Long(-1) );
            try {
				agents = AMSService.search( this.myAgent, new AMSAgentDescription (), c );
			} catch (FIPAException e) {
				e.printStackTrace();
			}
            
            ArrayList<AID> agentAIDs = new ArrayList<AID>();
            
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

		public boolean done() {
			return finished;
		}
	}

	public class Join extends SimpleBehaviour
	{
		private static final long serialVersionUID = 9088209402507795289L;
		private boolean finished=false;

		public Join(final Agent myagent)
		{
			super(myagent);
		}


		public void action()
		{	
			final ACLMessage msg;
			
			if((msg=Messaging.receiveMessage(this.myAgent)) != null)
			{
				//System.out.println("<----Message received from "+msg.getSender()+" ,content= "+msg.getContent());
				//TODO do something with msg
				//this.finished=true;
				
				if(msg.getContent().equals(Messaging.JOIN))
				{
					if(Board.players.size() <= MAX_NR_OF_PLAYERS)
					{
						Board.players.add(new Player(msg.getSender()));
						Messaging.sendMessage(this.myAgent, msg.getSender(), Messaging.ACCEPTED);
						System.out.println(msg.getSender().getName().split("@")[0]+" joined the game!");
					}
					else
					{
						Messaging.sendMessage(this.myAgent, msg.getSender(), Messaging.REJECTED);
						this.finished=true;
					}
				}
			}
			else
			{
				if(Board.players.size() == Platform.playerNr)
				{
					this.finished=true;
					addBehaviour(new Play(this.myAgent));
					initializeGame();
				}
			}
		}

		public boolean done() {
			return finished;
		}
	}
	
	public class Play extends SimpleBehaviour
	{
		private static final long serialVersionUID = 9088209402507795289L;
		private boolean finished=false;

		public Play(final Agent myagent)
		{
			super(myagent);
		}


		public void action()
		{
			//Get agents information
			AMSAgentDescription [] agents = null;
			
			SearchConstraints c = new SearchConstraints();
            c.setMaxResults ( new Long(-1) );
            try {
				agents = AMSService.search( this.myAgent, new AMSAgentDescription (), c );
			} catch (FIPAException e) {
				e.printStackTrace();
			}
            
            ArrayList<AID> agentAIDs = new ArrayList<AID>();
            
            for (int i=0; i<agents.length;i++)
            {
            	String name = agents[i].getName().getName().split("@")[0];
            	
            	if(name.startsWith("Player"))
            		agentAIDs.add(agents[i].getName());
            }
            
            try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            
            while(playing)
            {
            	if(currentPhase.equals(ADD_TROOPS_PHASE))
            	{
            		//Set Turn
            		Window.setTurn(Board.players.get(currentPlayer).toString());
            		//Set Phase
					Window.setPhase("Add Troops");
            		//Send update message to all
					Messaging.broadcast(this.myAgent, agentAIDs, Messaging.UPDATE+":"+board.toString());
					
					//Inform nextPlayer
					Messaging.sendMessage(this.myAgent, Board.players.get(currentPlayer).getAID(), Messaging.ADD_TROOPS);
					//Wait for response
					ACLMessage msg = Messaging.blockingReceiveMessage(this.myAgent);
					
					while(!msg.getContent().equals(Messaging.FINISH_ADD_TROOPS))
					{
						if(msg.getContent().split(":")[0].equals(Messaging.UPDATE_ADD_TROOPS) && msg.getSender().equals(Board.players.get(currentPlayer).getAID()))
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
					
            		currentPhase = ATTACK_PHASE;
            	}
            	else if(currentPhase.equals(ATTACK_PHASE))
            	{
            		//Set Phase
					Window.setPhase("Attack");
					//Send update message to all
					Messaging.broadcast(this.myAgent, agentAIDs, Messaging.UPDATE+":"+board.toString());
					
					//Inform nextPlayer
					Messaging.sendMessage(this.myAgent, Board.players.get(currentPlayer).getAID(), Messaging.ATTACK);
					//Wait for response
					ACLMessage msg = Messaging.blockingReceiveMessage(this.myAgent);
					
					while(!msg.getContent().equals(Messaging.FINISH_ATTACK))
					{
						if(msg.getContent().split(":")[0].equals(Messaging.UPDATE_ATTACK) && msg.getSender().equals(Board.players.get(currentPlayer).getAID()))
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
					
					currentPhase = REINFORCE_PHASE;
            	}
            	else if(currentPhase.equals(REINFORCE_PHASE))
            	{
            		//Set Phase
					Window.setPhase("Reinforce");
            		//Send update message to all
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
					
					currentPhase = ADD_TROOPS_PHASE;
					nextPlayer();
            	}
            	
            	playing = ((AgentGame) this.myAgent).isGameNotOver();
            }
			
			//TODO remove and continue
			this.finished = true;
		}

		public boolean done() {
			return finished;
		}
	}

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
	
	public void initializeGame()
	{
		Integer max = 0;
		Integer min = 0;
		Integer randomNum;
		Integer territoryPerPlayer = 42/Board.players.size();
		Integer playerCounter = 0;
		Integer counter = 0;
		Integer troopsCounter = 0;
		Boolean flag = false;
		
		Random rand = new Random();
		
		//Distribute countries by players
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
		
		//Distribute troops
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
		
		Window.updateBoard(board.getWorld().getCountries());
	}
	
	public static void nextPlayer()
	{
		if(currentPlayer+1 >= Board.players.size())
			currentPlayer = 0;
		else currentPlayer++;
	}

	protected void takeDown(){

	}
}
