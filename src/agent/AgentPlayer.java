package agent;

import jade.core.Agent;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import logic.Board;

public class AgentPlayer extends Agent
{
	private static final long serialVersionUID = 9088209402507795289L;
	
	//protected boolean onGame;
	private Board board;
	
	protected void setup()
	{
		super.setup();
		
		//onGame = false;
		board = new Board();
		
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
			final ACLMessage msg = Messaging.receiveMessage(this.myAgent);
			
			if(msg != null)
			{
				if (msg.getContent().equals(Messaging.REJECTED))
				{
					this.finished = true;
				}
				else
				{
					//TODO start playing
					this.finished = true;
				}
			}
		}

		public boolean done() {
			return finished;
		}
	}
}
