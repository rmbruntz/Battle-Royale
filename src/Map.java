import java.util.ArrayList;
import java.util.Scanner;
import java.util.Stack;

public class Map {
	
	private double[][] grid;
	private double mapMax;
	private double mapMin;
	private int turn;
	
	public Stack<Player> killed;
	public Stack<Player> killers;
	public Stack<Integer> turns;
	
	private ArrayList<Player> players;

	
	
	public void printPlayers() {
		System.out.println("Name\tAcc\tClimb");
		for (Player p : players) {
			System.out.println(p.getName()+"\t"+p.getAcc()+"\t"+p.getClimb());
		}
	}
	
	public Map(int x, int y) {
		killed = new Stack<Player>();
		killers = new Stack<Player>();
		turns = new Stack<Integer>();
		int turn = 0;
		players = new ArrayList<Player>();
		grid = new double[x][y];
		mapMax = mapMin = grid[0][0] = (Math.random() - 0.5);
		int layer = 1;
		while (layer < x + y - 1) {
			int ind = 0;
			while (ind <= layer && ind < x) {
				if (layer - ind >= y) {
					ind++;
					continue;
				}
				double prec;
				if (ind == layer) {
					prec = grid[ind - 1][layer-ind];
				} else if (ind == 0){
					prec = grid[ind][layer-ind-1];
				} else {
					prec = (grid[ind][layer-ind-1] + grid[ind - 1][layer-ind])/2;
				}
				grid[ind][layer-ind] = prec + (Math.random()-0.5)*2;
				
				mapMax = Math.max(mapMax, grid[ind][layer-ind]);
				mapMin = Math.min(mapMin, grid[ind][layer-ind]);
				ind++;
			}
			layer++;
		}
	}
	
	public double getDelta() {
		return mapMax-mapMin;
	}
	
	public void kill(Player target, Player killer) {
		killed.push(target);
		players.remove(target);
		killers.push(killer);
		turns.push(turn);
	}
	
	public int getX() {
		return grid.length;
	}
	
	public int getY() {
		return grid[0].length;
	}
	
	public void addPlayer(Player p) {
		players.add(p);
	}
	
	public Player checkForPlayer(int x, int y) {
		for (Player p: players) {
			if (p.getX() == x && p.getY() == y) {
				return p;
			}
		}
		return null;
	}
	
	public double getHeight(int x, int y) {
		return grid[x][y];
	}
	
	
	public void playGame(int speedMode, int mapSpeed) {
		//printPlayers();
		Scanner scan = new Scanner(System.in);
		int curPlayer = 0;
		while (players.size() > 1) {
			while (curPlayer < players.size()) {
				if (speedMode == 2){
					System.out.println(players.get(curPlayer).getName()+"'s turn");
				}
					players.get(curPlayer).takeTurn();
				if (speedMode == 2) {
					System.out.println(toString());
					System.out.println("Press key for next turn");
					scan.nextLine();
				}
				if (players.size() == 1) {
					endGame();
					return;
				}
				curPlayer++;
			}
			if (speedMode == 1) {
				System.out.println(toString());
				System.out.println("Press key for next turn");
				scan.nextLine();
			}
			
			turn++;
			if(turn%mapSpeed == 0) {
				int offset = (Math.random() > 0.5? 0 : 1);
				if (Math.random() > 0.5) { // x
					if (getX() > 1) {
						double[][] newGrid = new double[grid.length-1][grid[0].length];
						for (int i = 0; i < newGrid.length; i++) {
							for (int j = 0; j < newGrid[0].length; j++) {
								newGrid[i][j] = grid[i+offset][j];
							}
						}
						System.out.println(getX());
						for (Player p: players) {
							if (p.getX() == getX()-1) {
								p.setX(getX()-2);
							}
						}
						grid = newGrid;
					}
				} else { // y
					if (getY() > 1) {
						double[][] newGrid = new double[grid.length][grid[0].length-1];
						for (int i = 0; i < newGrid.length; i++) {
							for (int j = 0; j < newGrid[0].length; j++) {
								newGrid[i][j] = grid[i][j+offset];
							}
						}
						System.out.println(getY());
						for (Player p: players) {
							if (p.getY() == getY()-1) {
								p.setY(getY()-2);
							}
						}
						grid = newGrid;
					}
					
				}
			}
			curPlayer = 0;
		}
		endGame();
	}
	
	public void endGame() {
		System.out.println("Rank\tName\tKilled By\tTurn Killed");
		System.out.println("1\t"+players.get(0).getName()+"\tNA\t\tNA");
		int rank = 1;
		while (!killed.isEmpty()) {
			rank++;
			System.out.println(rank+"\t"+killed.pop().getName()+"\t"+killers.pop().getName()+"\t\t"+turns.pop());
		}
	}
	
	
	
	public String toString() {
		double scale = 10 / (mapMax - mapMin);
		String out  = "";
		for (int i = 0; i < grid[0].length; i++) {
			for (int j = 0; j < grid.length; j++) {
				Player p = checkForPlayer(j, i);
				if (p == null) {
					out += String.format("%3d", (int)(scale*grid[j][i]));
				} else {
					out += "|";
					out += p.getName();
					out += "|";
					
				}
				out += "  ";
			}
			out += "\n";
		}
		out += "\n\n";
		return out;
	}
}
