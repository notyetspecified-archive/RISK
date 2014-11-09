package logic;


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


public class Platform {

	private static String hostname = "127.0.0.1"; 
	private static HashMap<String, ContainerController> containerList=new HashMap<String, ContainerController>();// container's name - container's ref
	private static List<AgentController> agentList;// agents's ref
	private static Runtime rt;
	private static int playerNr;

	public static void main(String[] args)
	{		
		Scanner reader = new Scanner(System.in);
		System.out.print("Number of players: ");
		//get user input for a
		playerNr=reader.nextInt();
		reader.close();
		
		rt=emptyPlatform(containerList);
		agentList=createAgents(containerList);
		
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

		startAgents(agentList);
		
		Window window = new Window();
		window.frmRisk.setVisible(true);
		
//		EventQueue.invokeLater(new Runnable() {
//			public void run() {
//				try {
//					window.frmRisk.setVisible(true);
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//		});
		
		//window.test();
		//Tests
//		Board board = new Board();
//		System.out.println(board.nrOfTerritoriesInContinent(logic.Board.SOUTH_AMERICA));
	}

	private static Runtime emptyPlatform(HashMap<String, ContainerController> containerList){

		Runtime rt = Runtime.instance();

		// 1) create a platform (main container+DF+AMS)
		Profile pMain = new ProfileImpl(hostname, 8888, null);
		System.out.println("Launching a main-container..."+pMain);
		AgentContainer mainContainerRef = rt.createMainContainer(pMain); //DF and AMS are include

		// 2) create the containers
		containerList.putAll(createContainers(rt));

		// 3) create monitoring agents : rma agent, used to debug and monitor the platform; sniffer agent, to monitor communications; 
		createMonitoringAgents(mainContainerRef);

		System.out.println("Plaform ok");
		return rt;

	}

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
		
		try {
			Object[] m = new Object[1];
			String sniff_arg = ""; 
			sniff_arg += "Game";
            m[0]= sniff_arg;
            
			snif= containerList.get("game").createNewAgent("gameSniffer", "jade.tools.sniffer.Sniffer",m);
			snif.start();

		} catch (StaleProxyException e) {
			e.printStackTrace();
			System.out.println("launching of sniffer agent failed");

		}
	}


	private static List<AgentController> createAgents(HashMap<String, ContainerController> containerList) {
		System.out.println("Launching agents...");
		ContainerController c;
		String agentName;
		ArrayList<AgentController> agentList = new ArrayList<AgentController>();

		//Game
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

		for(int i=0;i<playerNr;i++)
		{
			//Players
			c = containerList.get("players");
			agentName="Player"+(i+1);
			try {					
				Object[] objtab=new Object[]{};
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







