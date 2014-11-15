package logic;

import jade.core.AID;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

public class Board
{
	private static final boolean log = false;
	
	public static final String ASIA = "Asia";
	public static final String EUROPE = "Europe";
	public static final String NORTH_AMERICA = "North America";
	public static final String AFRICA = "Africa";
	public static final String SOUTH_AMERICA = "South America";
	public static final String AUSTRALIA = "Australia";
	
	public static final int ASIA_BONUS = 7;
	public static final int EUROPE_BONUS = 5;
	public static final int NORTH_AMERICA_BONUS = 5;
	public static final int AFRICA_BONUS = 3;
	public static final int SOUTH_AMERICA_BONUS = 2;
	public static final int AUSTRALIA_BONUS = 3;
	
	public static final int DECK_SIZE = 72;
	public static final int NUMBER_OF_SYMBOLS = 3;
	
	public static ArrayList<Player> players;
	public static ArrayList<Card> deck;
	public static boolean deckInUse = false;
	private World world;
	
//	public static void main(String[] args)
//	{
//		Board board = new Board();
//		board.getWorld().getCountries().get(0).setTroops(5);
//		board.getWorld().getCountries().get(1).setTroops(5);
//		
//		board.attack(0,1);
//		
//		System.out.println(board.getWorld().getCountries().get(0).getTroops());
//		System.out.println(board.getWorld().getCountries().get(1).getTroops());
//	}

	public Board()
	{
		newBoard();
	}

	public static void newDeck()
	{
		deck = new ArrayList<Card>();
		
		for(int i=0;i<DECK_SIZE;i++)
		{
			if(i<DECK_SIZE/NUMBER_OF_SYMBOLS)
				deck.add(new Card(Card.INFANTRY));
			else if(i<(DECK_SIZE/NUMBER_OF_SYMBOLS)*2)
				deck.add(new Card(Card.CAVALRY));
			else deck.add(new Card(Card.ARTILLERY));
		}
		
		shuffleDeck();
	}

	private static void shuffleDeck()
	{
		long seed = System.nanoTime();
		Collections.shuffle(deck, new Random(seed));
	}

	private void newBoard()
	{
		//Create countries
		/// South America
		Country argentina = new Country("Argentina", SOUTH_AMERICA, new Coordinate(235,565));
		Country peru = new Country("Peru", SOUTH_AMERICA, new Coordinate(198,460));
		Country brasil = new Country("Brasil", SOUTH_AMERICA, new Coordinate(285,455));
		Country venezuela = new Country("Venezuela", SOUTH_AMERICA, new Coordinate(208,370));

		/// North America
		Country centralAmerica = new Country("Central America", NORTH_AMERICA, new Coordinate(140,320));
		Country westernUS = new Country("Western US", NORTH_AMERICA, new Coordinate(127,238));
		Country easternUS = new Country("Eastern US", NORTH_AMERICA, new Coordinate(211,232));
		Country quebec = new Country("Quebec", NORTH_AMERICA, new Coordinate(261,180));
		Country greenland = new Country("Greenland", NORTH_AMERICA, new Coordinate(327,75));
		Country ontario = new Country("Ontario", NORTH_AMERICA, new Coordinate(193,163));
		Country alberta = new Country("Alberta", NORTH_AMERICA, new Coordinate(126,148));
		Country northwestTerritory = new Country("Northwest Territory", NORTH_AMERICA, new Coordinate(133,92));
		Country alasca = new Country("Alasca", NORTH_AMERICA, new Coordinate(38,97));
		
		///Europe
		Country iceland = new Country("Iceland", EUROPE, new Coordinate(410,124));
		Country greatBritain = new Country("Great Britain", EUROPE, new Coordinate(380,210));
		Country scandinavia = new Country("Scandinavia", EUROPE, new Coordinate(488,117));
		Country westernEurope = new Country("Western Europe", EUROPE, new Coordinate(402,303));
		Country northernEurope = new Country("Northern Europe", EUROPE, new Coordinate(474,218));
		Country ukraine = new Country("Ukraine", EUROPE, new Coordinate(567,187));
		Country southernEurope = new Country("Southern Europe", EUROPE, new Coordinate(489,277));
		
		///Africa
		Country northAfrica = new Country("North Africa", AFRICA, new Coordinate(435,398));
		Country egypt = new Country("Egypt", AFRICA, new Coordinate(522,382));
		Country congo = new Country("Congo", AFRICA, new Coordinate(526,498));
		Country eastAfrica = new Country("East Africa", AFRICA, new Coordinate(576,453));
		Country southAfrica = new Country("South Africa", AFRICA, new Coordinate(521,574));
		Country madagascar = new Country("Madagascar", AFRICA, new Coordinate(626,580));
		
		///Asia
		Country middleEast = new Country("Middle East", ASIA, new Coordinate(602,352));
		Country india = new Country("India", ASIA, new Coordinate(715,353));
		Country afghanistan = new Country("Afghanistan", ASIA, new Coordinate(658,243));
		Country ural = new Country("Ural", ASIA, new Coordinate(675,144));
		Country siam = new Country("Siam", ASIA, new Coordinate(805,379));
		Country china = new Country("China", ASIA, new Coordinate(788,291));
		Country siberia = new Country("Siberia", ASIA, new Coordinate(728,120));
		Country mongolia = new Country("Mongolia", ASIA, new Coordinate(803,221));
		Country irkutsk = new Country("Irkutsk", ASIA, new Coordinate(775,165));
		Country yakutsk = new Country("Yakutsk", ASIA, new Coordinate(799,82));
		Country japan = new Country("Japan", ASIA, new Coordinate(912,193));
		Country kamchatka = new Country("Kamchatka", ASIA, new Coordinate(872,87));
		
		///Australia
		Country indonesia = new Country("Indonesia", AUSTRALIA, new Coordinate(804,492));
		Country newGuinea = new Country("New Guinea", AUSTRALIA, new Coordinate(902,445));
		Country westernAustralia = new Country("Western Australia", AUSTRALIA, new Coordinate(844,572));
		Country easternAustralia = new Country("Eastern Australia", AUSTRALIA, new Coordinate(940,585));
		
		//Create connections
		///South America
		argentina.addOutNode(peru);
		argentina.addOutNode(brasil);
		
		peru.addOutNode(argentina);
		peru.addOutNode(brasil);
		peru.addOutNode(venezuela);
		
		brasil.addOutNode(argentina);
		brasil.addOutNode(peru);
		brasil.addOutNode(venezuela);
		brasil.addOutNode(northAfrica);
		
		venezuela.addOutNode(peru);
		venezuela.addOutNode(brasil);
		venezuela.addOutNode(centralAmerica);
		
		///North America
		centralAmerica.addOutNode(venezuela);
		centralAmerica.addOutNode(westernUS);
		centralAmerica.addOutNode(easternUS);
		
		westernUS.addOutNode(centralAmerica);
		westernUS.addOutNode(easternUS);
		westernUS.addOutNode(ontario);
		westernUS.addOutNode(alberta);
		
		easternUS.addOutNode(centralAmerica);
		easternUS.addOutNode(westernUS);
		easternUS.addOutNode(ontario);
		easternUS.addOutNode(quebec);
		
		quebec.addOutNode(easternUS);
		quebec.addOutNode(ontario);
		quebec.addOutNode(greenland);
		
		greenland.addOutNode(quebec);
		greenland.addOutNode(ontario);
		greenland.addOutNode(northwestTerritory);
		greenland.addOutNode(iceland);
		
		ontario.addOutNode(quebec);
		ontario.addOutNode(easternUS);
		ontario.addOutNode(westernUS);
		ontario.addOutNode(alberta);
		ontario.addOutNode(northwestTerritory);
		ontario.addOutNode(greenland);
		
		alberta.addOutNode(westernUS);
		alberta.addOutNode(ontario);
		alberta.addOutNode(northwestTerritory);
		alberta.addOutNode(alasca);
		
		northwestTerritory.addOutNode(greenland);
		northwestTerritory.addOutNode(ontario);
		northwestTerritory.addOutNode(alberta);
		northwestTerritory.addOutNode(alasca);
		
		alasca.addOutNode(northwestTerritory);
		alasca.addOutNode(alberta);
		alasca.addOutNode(kamchatka);
		
		///Europe
		iceland.addOutNode(greenland);
		iceland.addOutNode(greatBritain);
		iceland.addOutNode(scandinavia);
		
		greatBritain.addOutNode(iceland);
		greatBritain.addOutNode(scandinavia);
		greatBritain.addOutNode(northernEurope);
		greatBritain.addOutNode(westernEurope);
		
		scandinavia.addOutNode(iceland);
		scandinavia.addOutNode(greatBritain);
		scandinavia.addOutNode(northernEurope);
		scandinavia.addOutNode(ukraine);
		
		westernEurope.addOutNode(greatBritain);
		westernEurope.addOutNode(northernEurope);
		westernEurope.addOutNode(southernEurope);
		westernEurope.addOutNode(northAfrica);
		
		northernEurope.addOutNode(southernEurope);
		northernEurope.addOutNode(westernEurope);
		northernEurope.addOutNode(greatBritain);
		northernEurope.addOutNode(scandinavia);
		northernEurope.addOutNode(ukraine);
		
		ukraine.addOutNode(ural);
		ukraine.addOutNode(afghanistan);
		ukraine.addOutNode(middleEast);
		ukraine.addOutNode(southernEurope);
		ukraine.addOutNode(northernEurope);
		ukraine.addOutNode(scandinavia);
		
		southernEurope.addOutNode(westernEurope);
		southernEurope.addOutNode(northernEurope);
		southernEurope.addOutNode(ukraine);
		southernEurope.addOutNode(middleEast);
		southernEurope.addOutNode(egypt);
		southernEurope.addOutNode(northAfrica);
		
		///Africa
		northAfrica.addOutNode(brasil);
		northAfrica.addOutNode(westernEurope);
		northAfrica.addOutNode(southernEurope);
		northAfrica.addOutNode(egypt);
		northAfrica.addOutNode(eastAfrica);
		northAfrica.addOutNode(congo);
		
		egypt.addOutNode(eastAfrica);
		egypt.addOutNode(northAfrica);
		egypt.addOutNode(southernEurope);
		egypt.addOutNode(middleEast);
		
		congo.addOutNode(northAfrica);
		congo.addOutNode(eastAfrica);
		congo.addOutNode(southAfrica);
		
		eastAfrica.addOutNode(madagascar);
		eastAfrica.addOutNode(southAfrica);
		eastAfrica.addOutNode(congo);
		eastAfrica.addOutNode(northAfrica);
		eastAfrica.addOutNode(egypt);
		eastAfrica.addOutNode(middleEast);
		
		southAfrica.addOutNode(congo);
		southAfrica.addOutNode(eastAfrica);
		southAfrica.addOutNode(madagascar);
		
		madagascar.addOutNode(southAfrica);
		madagascar.addOutNode(eastAfrica);
		
		///Asia
		middleEast.addOutNode(eastAfrica);
		middleEast.addOutNode(egypt);
		middleEast.addOutNode(southernEurope);
		middleEast.addOutNode(ukraine);
		middleEast.addOutNode(afghanistan);
		middleEast.addOutNode(india);
		
		india.addOutNode(middleEast);
		india.addOutNode(afghanistan);
		india.addOutNode(china);
		india.addOutNode(siam);
		
		afghanistan.addOutNode(middleEast);
		afghanistan.addOutNode(ukraine);
		afghanistan.addOutNode(ural);
		afghanistan.addOutNode(china);
		afghanistan.addOutNode(india);
		
		ural.addOutNode(ukraine);
		ural.addOutNode(afghanistan);
		ural.addOutNode(china);
		ural.addOutNode(siberia);
		
		siam.addOutNode(india);
		siam.addOutNode(china);
		siam.addOutNode(indonesia);
		
		china.addOutNode(siam);
		china.addOutNode(india);
		china.addOutNode(afghanistan);
		china.addOutNode(ural);
		china.addOutNode(siberia);
		china.addOutNode(mongolia);
		
		siberia.addOutNode(ural);
		siberia.addOutNode(china);
		siberia.addOutNode(mongolia);
		siberia.addOutNode(irkutsk);
		siberia.addOutNode(yakutsk);
		
		mongolia.addOutNode(china);
		mongolia.addOutNode(siberia);
		mongolia.addOutNode(irkutsk);
		mongolia.addOutNode(kamchatka);
		mongolia.addOutNode(japan);
		
		irkutsk.addOutNode(siberia);
		irkutsk.addOutNode(yakutsk);
		irkutsk.addOutNode(kamchatka);
		irkutsk.addOutNode(mongolia);
		
		yakutsk.addOutNode(siberia);
		yakutsk.addOutNode(irkutsk);
		yakutsk.addOutNode(kamchatka);
		
		japan.addOutNode(mongolia);
		japan.addOutNode(kamchatka);
		
		kamchatka.addOutNode(alasca);
		kamchatka.addOutNode(yakutsk);
		kamchatka.addOutNode(irkutsk);
		kamchatka.addOutNode(mongolia);
		kamchatka.addOutNode(japan);
		
		///Australia
		indonesia.addOutNode(siam);
		indonesia.addOutNode(newGuinea);
		indonesia.addOutNode(westernAustralia);
		
		newGuinea.addOutNode(indonesia);
		newGuinea.addOutNode(westernAustralia);
		newGuinea.addOutNode(easternAustralia);
		
		westernAustralia.addOutNode(indonesia);
		westernAustralia.addOutNode(newGuinea);
		westernAustralia.addOutNode(easternAustralia);
		
		easternAustralia.addOutNode(westernAustralia);
		easternAustralia.addOutNode(newGuinea);
		
		//Create World
		this.world = new World();
		this.world.addCountry(argentina);
		this.world.addCountry(peru);
		this.world.addCountry(brasil);
		this.world.addCountry(venezuela);
		this.world.addCountry(centralAmerica);
		this.world.addCountry(quebec);
		this.world.addCountry(easternUS);
		this.world.addCountry(westernUS);
		this.world.addCountry(greenland);
		this.world.addCountry(ontario);
		this.world.addCountry(alberta);
		this.world.addCountry(northwestTerritory);
		this.world.addCountry(alasca);
		
		this.world.addCountry(iceland);
		this.world.addCountry(greatBritain);
		this.world.addCountry(scandinavia);
		this.world.addCountry(westernEurope);
		this.world.addCountry(northernEurope);
		this.world.addCountry(ukraine);
		this.world.addCountry(southernEurope);
		
		this.world.addCountry(northAfrica);
		this.world.addCountry(egypt);
		this.world.addCountry(congo);
		this.world.addCountry(eastAfrica);
		this.world.addCountry(southAfrica);
		this.world.addCountry(madagascar);
		
		this.world.addCountry(middleEast);
		this.world.addCountry(india);
		this.world.addCountry(afghanistan);
		this.world.addCountry(ural);
		this.world.addCountry(siam);
		this.world.addCountry(china);
		this.world.addCountry(siberia);
		this.world.addCountry(mongolia);
		this.world.addCountry(irkutsk);
		this.world.addCountry(yakutsk);
		this.world.addCountry(japan);
		this.world.addCountry(kamchatka);
		
		this.world.addCountry(indonesia);
		this.world.addCountry(newGuinea);
		this.world.addCountry(westernAustralia);
		this.world.addCountry(easternAustralia);
	}
	
	public World getWorld() {
		return world;
	}

	public int nrOfTerritoriesInContinent(String continent)
	{
		int counter = 0;
		
		for(int i=0;i<this.world.getCountries().size();i++)
			if(this.world.getCountries().get(i).getContinent().equals(continent))
				counter++;
		
		return counter;
	}

	public boolean isAdjacent(Integer attacker, Integer defendant)
	{
		if(this.world.getCountries().get(attacker).getOutnodes().
				contains(this.world.getCountries().get(defendant)))
			return true;
		
		if(log) System.out.println("Territories are not adjacent!");
		return false;
	}
	
	public static boolean isAdjacent(Country attacker, Country defendant)
	{
		if(attacker.getOutnodes().contains(defendant))
			return true;
		
		if(log) System.out.println("Territories are not adjacent!");
		return false;
	}
	
	public boolean attack(Country cAttacker, Country cDefendant)
	{
		Integer attacker = getCountryIndex(cAttacker);
		Integer defendant = getCountryIndex(cDefendant);
		
		if(isAdjacent(attacker, defendant))
		{
			if(this.world.getCountries().get(attacker).getTroops() > 1)
			{
				while(this.world.getCountries().get(attacker).getTroops() > 1 &&
						this.world.getCountries().get(defendant).getTroops() > 0)
				{
					rollDice(attacker, defendant);
					
					if(this.world.getCountries().get(defendant).getTroops() == 0)
					{
						this.world.getCountries().get(defendant).setOwner(cAttacker.getOwner());
						return true;
					}
				}
			}
			else if(log) System.out.println("Not enough troops to attack!");
		}
		
		return false;
	}
	
	public boolean attack(Integer attacker, Integer defendant)
	{
		if(isAdjacent(attacker, defendant))
		{
			if(this.world.getCountries().get(attacker).getTroops() > 1)
			{
				while(this.world.getCountries().get(attacker).getTroops() > 1 &&
						this.world.getCountries().get(defendant).getTroops() > 0)
				rollDice(attacker, defendant);
			}
			else if(log) System.out.println("Not enough troops to attack!");
		}
		
		return false;
	}
	
	public void rollDice(int attacker, int defendant)
	{
		Integer attackerTroops = this.world.getCountries().get(attacker).getTroops();
		Integer defendantTroops = this.world.getCountries().get(defendant).getTroops();
		Integer nAttackerDice;
		Integer ndefendantDice;
		Integer max = 6;
		Integer min = 1;
		Random rand = new Random();
		Integer randomNumber;
		
		if(attackerTroops > 3)
			nAttackerDice = 3;
		else nAttackerDice = attackerTroops-1;
		
		if(defendantTroops >= 2)
			ndefendantDice = 2;
		else ndefendantDice = 1;
		
		ArrayList<Integer> attackerDice = new ArrayList<Integer>(nAttackerDice);
		ArrayList<Integer> defendantDice = new ArrayList<Integer>(ndefendantDice);
		
		//Roll attacker
		if(log) System.out.print("\nAttacker rolled ");
		for(int i=0;i<nAttackerDice;i++)
		{
			randomNumber = rand.nextInt((max - min) + 1) + min;
			attackerDice.add(randomNumber);
			if(log) System.out.print(randomNumber+", ");
		}
		if(log) System.out.println();
		
		//Roll defendant
		if(log) System.out.print("Defendant rolled ");
		for(int i=0;i<ndefendantDice;i++)
		{
			randomNumber = rand.nextInt((max - min) + 1) + min;
			defendantDice.add(randomNumber);
			if(log) System.out.print(randomNumber+", ");
		}
		if(log) System.out.println();
		
		while(!defendantDice.isEmpty() && !attackerDice.isEmpty())
		{
			if(attackerDice.get(findMax(attackerDice)) <= 
					defendantDice.get(findMax(defendantDice)))
			{
				attackerTroops--;
			}
			else defendantTroops--;
			
			attackerDice.remove(findMax(attackerDice));
			defendantDice.remove(findMax(defendantDice));
		}
		
		this.world.getCountries().get(attacker).setTroops(attackerTroops);
		this.world.getCountries().get(defendant).setTroops(defendantTroops);
	}
	
	public int findMax(ArrayList<Integer> list)
	{
		Integer max = 0;
		int maxIndex = 0;
		
		for(int i=0;i<list.size();i++)
		{
			if(list.get(i) > max)
			{
				max = list.get(i);
				maxIndex = i;
			}
		}
		
		return maxIndex;
	}

	@Override
	public String toString()
	{
		String str = "";
		
		for(int i=0;i<this.world.getCountries().size();i++)
		{
			str += this.world.getCountries().get(i).getOwner().getID() + ",";
			str += this.world.getCountries().get(i).getTroops();		
					
			if(i<this.world.getCountries().size()-1)
				str += ";";
		}
		
		return str;
	}
	
	public void updateBoardFromString(String str)
	{
		String countries[] = str.split(";");
		
		for(int i=0;i<countries.length;i++)
		{
			String settings[] = countries[i].split(",");
			this.world.getCountries().get(i).setOwner(Board.players.get(Integer.valueOf(settings[0])));
			this.world.getCountries().get(i).setTroops(Integer.valueOf(settings[1]));
		}
	}

	public Integer getPlayerIndex(AID aid)
	{	
		for(int i=0;i<Board.players.size();i++)
		{
			if(aid.equals(Board.players.get(i)))
				return i;
		}
		
		return -1;
	}
	
	public Integer getCountryIndex(Country country)
	{
		for(int i=0;i<this.getWorld().getCountries().size();i++)
		{
			if(country.equals(this.getWorld().getCountries().get(i)))
				return i;
		}
		
		return -1;
	}
	
	public Country getCountryByName(String name)
	{
		for(int i=0;i<this.getWorld().getCountries().size();i++)
		{
			if(this.getWorld().getCountries().get(i).getName().equals(name))
				return this.getWorld().getCountries().get(i);
		}
		
		return null;
	}
	
	//Breadth First Algorithm
	public void bfs(Country root)
	{
		Queue<Country> queue = new LinkedList<Country>();
		
		root.setVisited(true);
		
		queue.add(root);
		
		while(!queue.isEmpty())
        {
			Country r = queue.remove();
			
			for(Country n: r.getOutnodes())
			{
				if(n.getOwner().equals(r.getOwner()))
				{
					if(!n.isVisited())
					{
						queue.add(n);
						n.setVisited(true);
					}
				}
			}
        }
	}

	public boolean areConnected(Country from, Country to)
	{
		setAllNotVisited();
		bfs(from);
		return to.isVisited();
	}

	private void setAllNotVisited()
	{
		for(Country c: world.getCountries())
		{
			c.setVisited(false);
		}
	}

	public static Card pickCard()
	{
		while(deckInUse){}
		
		deckInUse = true;
		
		for(int i=0;i<deck.size();i++)
		{
			if(!deck.get(i).isPicked())
			{
				deck.get(i).setPicked(true);
				deckInUse = false;
				return deck.get(i);
			}
		}
		
		deckInUse = false;
		
		return null;
	}
}
