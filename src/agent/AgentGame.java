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
import logic.Country;
import logic.Player;


public class AgentGame extends Agent
{	
	private static final long serialVersionUID = -1772942192462864835L;

	public static final int MAX_NR_OF_PLAYERS = 6;
	public static int WAIT = 0;
	
	private ArrayList<Player> players; 
	private Board board;
	
	protected void setup()
	{
		super.setup();

		players = new ArrayList<Player>();
		board = new Board();
		
		//Create Pieces in Window
		Window.createPieces(board.getWorld().getCountries());

		//Add the behaviors
		addBehaviour(new Start(this));

		System.out.println(this.getLocalName()+ " has started.");
	}

	protected void takeDown(){

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
					if(players.size() <= MAX_NR_OF_PLAYERS)
					{
						players.add(new Player(msg.getSender()));
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
				if(WAIT > 1000000)
				{
					this.finished=true;
					addBehaviour(new Play(this.myAgent));
				}
				else WAIT++;
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
			initializeGame();
			//TODO remove and continue
			this.finished = true;
		}

		public boolean done() {
			return finished;
		}
	}

	
	public void initializeGame()
	{
		Integer max = 0;
		Integer min = 0;
		Integer randomNum;
		Integer territoryPerPlayer = 42/players.size();
		Integer playerCounter = 0;
		Integer counter = 0;
		Integer troopsCounter = 0;
		Boolean flag = false;
		
		Random rand = new Random();
		
		//Distribute countries by players
		for(int i=0;i<board.getWorld().getCountries().size();i++)
		{
			if(playerCounter.equals(players.size()))
				playerCounter--;
			
			board.getWorld().getCountries().get(i).setOwner(players.get(playerCounter));
			counter++;
			
			if(counter.equals(territoryPerPlayer))
			{
				playerCounter++;
				counter = 0;
			}
		}
		
		//Distribute troops
		for(int j=0;j<players.size();j++)
		{
			counter = 0;
			min = 0;
			
			for(int i=0;i<board.getWorld().getCountries().size();i++)
			{
				if(board.getWorld().getCountries().get(i).getOwner().equals(players.get(j)))
					counter++;
					
					randomNum = rand.nextInt((max - min) + 1) + min;
				
			}
			
			max = 30-counter;
			troopsCounter = 0;
			flag = false;
			
			for(int i=0;i<board.getWorld().getCountries().size();i++)
			{
				if(board.getWorld().getCountries().get(i).getOwner().equals(players.get(j)))
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
}
