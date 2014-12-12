package agent;

import java.util.ArrayList;

import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

/**
 * Classe de controlo de mensagens entre agentes
 *  
 * @author João Ladeiras
 * @author Rui Lima
 * 
 */

public class Messaging
{
	//Flag log
	private static final boolean log = false;
	
	//Constantes das mensagens de controlo
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
	public static final String ALLY = "ally";
	public static final String ACCEPT_ALLY = "accept_ally";
	public static final String REJECT_ALLY = "reject_ally";
	
	/**
	 * Recebe uma mensagem performativa
	 * 
	 * @param receiver Agente que recebe a mesangem
	 * @return Mensagem
	 */
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
	
	/**
	 * Recebe uma mensagem performativa.
	 * Suspende o agente até a mensagem estar disponivel
	 * 
	 * @param receiver Agente que recebe a mesangem
	 * @return Mensagem
	 */
	public static ACLMessage blockingReceiveMessage(Agent receiver)
	{
		final MessageTemplate msgTemplate = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
		final ACLMessage msg = receiver.blockingReceive(msgTemplate);
		
		if(log)
			System.out.println("<-- Message received from "+msg.getSender()+" ,content= "+msg.getContent());
		return msg;
	}
	
	/**
	 * Envia uma mensagem performativa
	 * 
	 * @param sender Agente remetente
	 * @param receiver Nome do agente destinatário
	 * @param content Conteúdo da mensagem
	 */
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
	
	/**
	 * Envia uma mensagem performativa
	 * 
	 * @param sender Agente remetente
	 * @param receiver AID do agente destinatário
	 * @param content Conteúdo da mensagem
	 */
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

	/**
	 * Envia uma mensagem para uma lista de agentes
	 * 
	 * @param sender Agente remetente
	 * @param agentAIDs Lista de AIDs dos agentes remetentes
	 * @param content Conteúdo da mensagem
	 */
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
