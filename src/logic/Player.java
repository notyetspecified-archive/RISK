package logic;

import java.awt.Color;

import jade.core.AID;

public class Player
{
	private AID aid;
	private Color color;
	private static int colorNr = 0;

	public Player(AID aid) {
		super();
		this.aid = aid;
		colorNr++;
		setColor(colorNr);
	}

	private void setColor(int color)
	{
		switch (color) {
		case 1:
			this.color = Color.red;
			break;
		case 2:
			this.color = Color.blue;
			break;
		case 3:
			this.color = Color.green;
			break;
		case 4:
			this.color = Color.orange;
			break;
		case 5:
			this.color = Color.darkGray;
			break;
		case 6:
			this.color = Color.black;
			break;

		default:
			this.color = Color.yellow;
			break;
		}
	}

	public AID getID() {
		return aid;
	}

	public void setID(AID aid) {
		this.aid = aid;
	}

	public Color getColor() {
		return this.color;
	}
}
