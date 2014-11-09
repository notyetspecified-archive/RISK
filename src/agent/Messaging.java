package agent;

import java.util.ArrayList;

import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class Messaging
{
	public static final String START = "start";
	public static final String JOIN = "join";
	public static final String ACCEPTED = "accepted";
	public static final String REJECTED = "rejected";
	
	public static ACLMessage receiveMessage(Agent receiver)
	{
		final MessageTemplate msgTemplate = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
		final ACLMessage msg = receiver.receive(msgTemplate);
		
		if (msg != null)
		{		
			System.out.println("<----Message received from "+msg.getSender()+" ,content= "+msg.getContent());
			return msg;
		}else{
			return null;
		}
	}
	
	public static ACLMessage blockingReceiveMessage(Agent receiver)
	{
		final MessageTemplate msgTemplate = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
		final ACLMessage msg = receiver.blockingReceive(msgTemplate);
				
		System.out.println("<----Message received from "+msg.getSender()+" ,content= "+msg.getContent());
		return msg;
	}
	
	public static void sendMessage(Agent sender, String receiver, String content)
	{
		final ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
		
		msg.setSender(sender.getAID());
		msg.addReceiver(new AID(receiver, AID.ISLOCALNAME));
		msg.setContent(content);

		sender.send(msg);
		System.out.println("----> Message sent to "+msg.getAllReceiver().next()+" ,content= "+msg.getContent());
	}
	
	public static void sendMessage(Agent sender, AID receiver, String content)
	{
		final ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
		
		msg.setSender(sender.getAID());
		msg.addReceiver(receiver);
		msg.setContent(content);

		sender.send(msg);
		System.out.println("----> Message sent to "+msg.getAllReceiver().next()+" ,content= "+msg.getContent());
	}

	public static void broadcast(Agent sender, ArrayList<AID> agentAIDs, String content)
	{
		final ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
		
		msg.setSender(sender.getAID());
		
		for(int i=0;i<agentAIDs.size();i++)
			msg.addReceiver(agentAIDs.get(i));
		
		msg.setContent(content);

		sender.send(msg);
		System.out.println("----> Message broadcasted ,content= "+msg.getContent());
	}

}
