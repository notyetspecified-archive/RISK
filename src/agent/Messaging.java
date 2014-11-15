package agent;

import java.util.ArrayList;

import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class Messaging
{
	private static final boolean log = false;
	
	public static final String START = "start";
	public static final String JOIN = "join";
	public static final String ACCEPTED = "accepted";
	public static final String REJECTED = "rejected";
	public static final String UPDATE = "update";
	public static final String ADD_TROOPS = "add_troops";
	public static final String UPDATE_ADD_TROOPS = "update_add_troops";
	public static final String FINISH_ADD_TROOPS = "finish_add_troops";
	public static final String ATTACK = "attack";
	public static final String UPDATE_ATTACK = "update_attack";
	public static final String FINISH_ATTACK = "finish_attack";
	public static final String REINFORCE = "reinforce";
	public static final String FINISH_REINFORCE = "finish_reinforce";
	public static final String UPDATE_REINFORCE = "update_reinforce";
	
	public static ACLMessage receiveMessage(Agent receiver)
	{
		final MessageTemplate msgTemplate = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
		final ACLMessage msg = receiver.receive(msgTemplate);
		
		if (msg != null)
		{
			if(log)
				System.out.println("<-- Message received from "+msg.getSender()+" ,content= "+msg.getContent());
			return msg;
		}else{
			return null;
		}
	}
	
	public static ACLMessage blockingReceiveMessage(Agent receiver)
	{
		final MessageTemplate msgTemplate = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
		final ACLMessage msg = receiver.blockingReceive(msgTemplate);
		
		if(log)
			System.out.println("<-- Message received from "+msg.getSender()+" ,content= "+msg.getContent());
		return msg;
	}
	
	public static void sendMessage(Agent sender, String receiver, String content)
	{
		final ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
		
		msg.setSender(sender.getAID());
		msg.addReceiver(new AID(receiver, AID.ISLOCALNAME));
		msg.setContent(content);

		sender.send(msg);
		
		if(log)
			System.out.println("--> Message sent to "+msg.getAllReceiver().next()+" ,content= "+msg.getContent());
	}
	
	public static void sendMessage(Agent sender, AID receiver, String content)
	{
		final ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
		
		msg.setSender(sender.getAID());
		msg.addReceiver(receiver);
		msg.setContent(content);

		sender.send(msg);
		
		if(log)
			System.out.println("--> Message sent to "+msg.getAllReceiver().next()+" ,content= "+msg.getContent());
	}

	public static void broadcast(Agent sender, ArrayList<AID> agentAIDs, String content)
	{
		final ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
		
		msg.setSender(sender.getAID());
		
		for(int i=0;i<agentAIDs.size();i++)
			msg.addReceiver(agentAIDs.get(i));
		
		msg.setContent(content);

		sender.send(msg);
		
		if(log)
			System.out.println("--> Message broadcasted ,content= "+msg.getContent());
	}

}
