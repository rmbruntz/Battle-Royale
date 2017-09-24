import java.util.ArrayList;
import java.util.Random;

public class Player {

	private char name;
	private double climb;
	private double acc;
	private Map map;
	private double direction;
	private int x;
	private int y;
	private ArrayList<Integer> seenX;
	private ArrayList<Integer> seenY;
	private Random r;
	
	
	public Player(char name, double climb, double acc, Map map, int x, int y) {
		r = new Random();
		this.name = name;
		this.climb = climb;
		this.acc = acc;
		this.map = map;
		this.x = x;
		this.y = y;
		seenX = new ArrayList<Integer>();
		seenY = new ArrayList<Integer>();
		direction = Math.random()*2*Math.PI;
		//System.out.println(direction + " " + Math.cos(direction) + " " + Math.sin(direction));
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public void setX(int x) {
		this.x = x;
	}
	
	public void setY(int y) {
		this.y = y;
	}
	
	public double getAcc() {
		return acc;
	}
	
	public double getClimb() {
		return climb;
	}
	
	
	public void takeTurn() {
		direction = Math.random()*2*Math.PI;
		updateSeenSquares();
		for (int i = 0; i < seenX.size(); i++) {
			Player other = map.checkForPlayer(seenX.get(i), seenY.get(i));
			if (other != null && other != this) {
				System.out.println(name+" shot "+other.getName());
				shoot(other);
				return;
			}
		}
		

		
		int newX = x;
		int newY = y;
		
		if (direction > Math.PI*0.25 && direction < Math.PI*0.75) {
			newY++;
		} else if (direction > Math.PI*0.75 && direction < Math.PI*1.25) {
			newX--;
		} else if (direction > Math.PI*1.25 && direction < Math.PI*1.75) {
			newY--;
		} else {
			newX++;
		}
		
		if (newX >= 0 && newX < map.getX() && newY >= 0 && newY < map.getY()) {
			double delta = Math.abs(map.getHeight(newX, newY) - map.getHeight(x, y));
			double chance = 1 - (1-climb)*(delta/map.getDelta());
			if (Math.random() < chance) {
				x = newX;
				y = newY;
				updateSeenSquares();
				for (int i = 0; i < seenX.size(); i++) {
					Player other = map.checkForPlayer(seenX.get(i), seenY.get(i));
					if (other != null && other != this) {
						shoot(other);
						return;
					}
				}
			}
		}
		
		
	}
	
	public void shoot(Player other) {
		double hDiff = map.getHeight(x, y) - map.getHeight(other.getX(), other.getY());
		double dist = Math.hypot(x-other.getX(), y-other.getY());
		double chance = acc + hDiff/map.getDelta() - dist/Math.hypot(map.getX(), map.getY());
		if (chance > Math.random()) {
			System.out.println(this.name+" killed " +other.getName());
			map.kill(other, this);
		}
	}
	
	private void updateSeenSquares() {
		seenX.clear();
		seenY.clear();
		
		double xOrig = x;
		double yOrig = y;
		double origEl = map.getHeight(x, y);
		double bigIncline = 0;
		seenX.add(x);
		seenY.add(y);
		
		double xPos = x + 0.5f;
		double yPos = y + 0.5f;
		double xPer = Math.cos(direction);
		double yPer = Math.sin(direction);
		
		
		while (xPos > 0 && yPos > 0 && xPos < map.getX() && yPos < map.getY()) {
			//System.out.print(xPos + " " + yPos + "\t");
			//System.out.println(bigIncline);
			double nextX = (xPer > 0 ? ((Math.round(xPos) == xPos)? xPos + 1 : Math.ceil(xPos)) : ((Math.round(xPos) == xPos)? xPos - 1 : Math.floor(xPos)));
			double nextY = (yPer > 0 ? ((Math.round(yPos) == yPos)? yPos + 1 : Math.ceil(yPos)) : ((Math.round(yPos) == yPos)? yPos - 1 : Math.floor(yPos)));
			
			//System.out.print(nextX + " " + nextY + "\t");
			
			double xSteps = (nextX - xPos)/xPer;
			double ySteps = (nextY - yPos)/yPer;
			
			//System.out.print(xSteps + " " + ySteps + "\t");
			
			boolean doX = xSteps < ySteps;
			double steps = Math.min(xSteps, ySteps);
			
			xPos += steps * xPer;
			yPos += steps * yPer;
			
			int addX;
			int addY;
			
			if (doX) {
				xPos = Math.round(xPos) + (xPer > 0 ? 1 : -1)*.0001f;
				addY = (int)Math.round(Math.floor(yPos));
				addX = (int)Math.round(Math.floor(xPos));
				
			} else {
				yPos = Math.round(yPos) + (yPer > 0 ? 1 : -1)*.0001f;
				addX = (int)Math.round(Math.floor(xPos));
				addY = (int)Math.round(Math.floor(yPos));
			}
			
			if ((seenX.get(seenX.size()-1) != addX || seenY.get(seenY.size() -1) != addY) && addX >= 0 && addY >= 0 && addX < map.getX() && addY < map.getY()) {
				double dist = Math.hypot(addX-x, addY-y);
				double hDiff = map.getHeight(addX, addY) - origEl;
				if (hDiff > 0 || bigIncline > 0) {
					if ((hDiff/dist) < bigIncline) {
						break;
					} else {
						bigIncline = hDiff/dist;
					}
				}
				seenX.add(addX);
				seenY.add(addY);
			}
			
			//System.out.println(addX + " " + addY);
		}
		//System.out.println();
		
		/*
		for (int tx : seenX) {
			System.out.printf("%3d\t", tx);
		}
		
		System.out.println();
		for (int ty : seenY) {
			System.out.printf("%3d\t", ty);
		}
		*/
	}

	public char getName() {
		return name;
	}
	
	
}
