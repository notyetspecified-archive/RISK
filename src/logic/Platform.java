package logic;


import gui.Launcher;
import gui.Window;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import agent.AgentGame;
import agent.AgentPlayer;

/**
 * Nesta classe, são chamados os métodos principais do programa, como a 
 * interface e métodos de inicialização da plataforma Jade e respetivos 
 * containers e agentes.
 * 
 * @author João Ladeiras
 * @author Rui Lima
 * 
 */

public class Platform {

	/**
	 * Parametros de inicialização
	 */
	private static String hostname = "127.0.0.1"; 
	private static HashMap<String, ContainerController> containerList=new HashMap<String, ContainerController>();// container's name - container's ref
	private static List<AgentController> agentList;// agents's ref
	public static int playerNr;
	public static String[] playerType = {"None", "Normal", "Offensive", "Defensive"};
	private static ArrayList<String> playerTypes = new ArrayList<String>();
	public static boolean alliesMode;

	/**
	 * Função main
	 */
	public static void main(String[] args)
	{
		//Mostrar janela launcher
		Launcher launcher = new Launcher();
		launcher.setVisible(true);
		
		//Esperar pelo launcher
		while(launcher.isVisible()) {}
		
		//Atribuir modo de alianças
		if(launcher.getComboBoxAllies().getSelectedItem().equals("Yes"))
			alliesMode = true;
		else alliesMode = false;
		
		//Guardar tipos dos agentes localmente
		getPlayerTypes(launcher);
		playerNr = playerTypes.size();
		
		//Fechar janela launcher
		launcher.dispose();
		
		//Terminar se não existem suficientes jogadores
		if(playerNr <= 1)
			return;
		
		//Criação de containers e agentes
		emptyPlatform(containerList);
		agentList=createAgents(containerList);
		
		//Esperar pela inicialização do Jade
		try {
			System.out.print("Game starting in 3...");
			Thread.sleep(1000);
			System.out.print("2...");
			Thread.sleep(1000);
			System.out.println("1...");
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			System.out.println("Error on sleep");
		}

		//Iniciação dos agentes
		startAgents(agentList);
		
		//Atribuir delay entre movimentos
		AgentGame.setDelay(Integer.valueOf((String) launcher.getComboBoxDelay().getSelectedItem()));
		
		//Criar e mostrar janela do tabuleiro
		Window window = new Window();
		window.frmRisk.setVisible(true);
		
		//Input do utilizador na consola
		Scanner reader = new Scanner(System.in);
		int temp = AgentGame.delay;
		boolean flag = true;
		
		//Atrasar execução a pedido do utilizador ('p')
		while(true)
		{
			if(reader.nextLine().equals("p"))
			{
				if(flag)
				{
					AgentGame.setDelay(10000);
					flag = false;
				}
				else
				{
					AgentGame.setDelay(temp);
					flag = true;
				}
			}
			else if(reader.nextLine().equals("stop"))
				break;
		}
		reader.close();
	}

	/**
	 * Nesta função são passados os parametros escolhidos na janela launcher 
	 * para a plataforma
	 * 
	 * @param launcher janela de inicialização
	 */
	private static void getPlayerTypes(Launcher launcher)
	{
		if(!launcher.getComboBox1().getSelectedItem().equals(playerType[0]))
			playerTypes.add((String) launcher.getComboBox1().getSelectedItem());
		if(!launcher.getComboBox2().getSelectedItem().equals(playerType[0]))
			playerTypes.add((String) launcher.getComboBox2().getSelectedItem());
		if(!launcher.getComboBox3().getSelectedItem().equals(playerType[0]))
			playerTypes.add((String) launcher.getComboBox3().getSelectedItem());
		if(!launcher.getComboBox4().getSelectedItem().equals(playerType[0]))
			playerTypes.add((String) launcher.getComboBox4().getSelectedItem());
		if(!launcher.getComboBox5().getSelectedItem().equals(playerType[0]))
			playerTypes.add((String) launcher.getComboBox5().getSelectedItem());
		if(!launcher.getComboBox6().getSelectedItem().equals(playerType[0]))
			playerTypes.add((String) launcher.getComboBox6().getSelectedItem());
	}

	/**
	 * Nesta função são criados os elementos da plataforma Jade, excepto os 
	 * agentes
	 * 
	 * @param containerList
	 * @return runtime
	 */
	private static Runtime emptyPlatform(HashMap<String, ContainerController> containerList){

		Runtime rt = Runtime.instance();

		//Criar a plataforma
		Profile pMain = new ProfileImpl(hostname, 8888, null);
		System.out.println("Launching a main-container..."+pMain);
		AgentContainer mainContainerRef = rt.createMainContainer(pMain); //DF and AMS are include

		//Criar os containers
		containerList.putAll(createContainers(rt));

		//Criar os sniffers 
		createMonitoringAgents(mainContainerRef);

		System.out.println("Plaform ok");
		return rt;

	}

	/**
	 * Criação dos containers
	 * 
	 * @param rt
	 * @return
	 */
	private static HashMap<String,ContainerController> createContainers(Runtime rt) {
		String containerName;
		ProfileImpl pContainer;
		ContainerController containerRef;
		HashMap<String, ContainerController> containerList=new HashMap<String, ContainerController>();

		System.out.println("Launching containers ...");

		//create the container0	
		containerName="players";
		pContainer = new ProfileImpl(null, 8888, null);
		System.out.println("Launching container "+pContainer);
		containerRef = rt.createAgentContainer(pContainer); //ContainerController replace AgentContainer in the new versions of Jade.
		containerList.put(containerName, containerRef);

		//create the container1	
		containerName="game";
		pContainer = new ProfileImpl(null, 8888, null);
		System.out.println("Launching container "+pContainer);
		containerRef = rt.createAgentContainer(pContainer); //ContainerController replace AgentContainer in the new versions of Jade.
		containerList.put(containerName, containerRef);

		System.out.println("Launching containers done");
		return containerList;
	}

	/**
	 * Criação dos sniffers
	 * 
	 * @param mc
	 */
	private static void createMonitoringAgents(ContainerController mc) {

		System.out.println("Launching the rma agent on the main container ...");
		AgentController rma;

		try {
			rma = mc.createNewAgent("rma", "jade.tools.rma.rma", new Object[0]);
			rma.start();
		} catch (StaleProxyException e) {
			e.printStackTrace();
			System.out.println("Launching of rma agent failed");
		}

		System.out.println("Launching  Sniffers...");
		AgentController snif=null;
		
		try {
			Object[] m = new Object[1];
			String sniff_arg = ""; 
			
			for(int i=0;i<playerNr;i++)
			{
				if((i+1)!=playerNr)
					sniff_arg += "Player"+(i+1)+";";
				else
					sniff_arg += "Player"+(i+1);
			}
			
            m[0]= sniff_arg;
			snif= containerList.get("players").createNewAgent("playersSniffer", "jade.tools.sniffer.Sniffer",m);
			snif.start();

		} catch (StaleProxyException e) {
			e.printStackTrace();
			System.out.println("launching of sniffer agent failed");

		}
	}

	/**
	 * Criação dos agentes
	 * 
	 * @param containerList lista de containers
	 * @return lista de controladores dos agentes
	 */
	private static List<AgentController> createAgents(HashMap<String, ContainerController> containerList) {
		System.out.println("Launching agents...");
		ContainerController c;
		String agentName;
		ArrayList<AgentController> agentList = new ArrayList<AgentController>();

		//Agente Game
		c = containerList.get("game");
		agentName="Game";
		try {
			Object[] objtab=new Object[]{};
			AgentController	ag=c.createNewAgent(agentName,AgentGame.class.getName(),objtab);
			agentList.add(ag);
			System.out.println(agentName+" launched");
		} catch (StaleProxyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//Agentes Player
		for(int i=0;i<playerNr;i++)
		{
			//Players
			c = containerList.get("players");
			agentName="Player"+(i+1);
			try {
				List<String> data=new ArrayList<String>();
				//Adicionar o tipo e agente
				data.add(playerTypes.get(i));
				Object[] objtab=new Object[]{data};
				AgentController	ag=c.createNewAgent(agentName,AgentPlayer.class.getName(),objtab);
				agentList.add(ag);
				System.out.println(agentName+" launched");
			} catch (StaleProxyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		System.out.println("Agents launched...");
		return agentList;
	}

	/**
	 * Inicialização dos agentes
	 * 
	 * @param agentList lista de agentes
	 */
	private static void startAgents(List<AgentController> agentList){

		System.out.println("Starting agents...");


		for(final AgentController ac: agentList){
			try {
				ac.start();
			} catch (StaleProxyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		System.out.println("Agents started...");
	}

}







