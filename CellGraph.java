import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Stack;
import java.lang.Math;

/*
Mohamed Albgal
Sero Nazarian

This program will generate and output the solutions to an nxn maze.
The solutions are displayed as order visited using both a depth and breadth first search algorithms

 */

public class CellGraph {

	private List<LinkedList<Cell>> adjacencyList;
	//array representation of maze
	private Cell[] cellRecord;
	private final int SIZE;
	private final int TOTAL_CELLS;
	private Queue<Integer> randomNumbersCache;
	//string representation of output maze
	String stringRep;
	private final int CACHE_SIZE = 1000;
	private final int MAX_ELIGIBLE_NBORS = 4;
	private final int RANDOM_SEED = 0;
	private String ASCII_REP;

	//				main method, to change the size, change the first line's input
	public static void main(String[] args){

		CellGraph g;
		sopl("Please enter the dimensions of the graph:  ");

		Scanner scan = new Scanner(System.in);
		String in = scan.next();
		sopl("Do you want it blank?");
		String blank = scan.next();
		scan.close();
		try{
			Integer n = Integer.parseInt(in);
			if (blank.equalsIgnoreCase("yes") || blank.equalsIgnoreCase("y") && n instanceof Integer)
			{
				g = new CellGraph(n);
				g.buildMaze();
				g.printMaze(g.getCellRecord(), " ");
				return;
			}
			else if (blank.equalsIgnoreCase("no") || blank.equalsIgnoreCase("n") && n instanceof Integer)
			{
				g = new CellGraph(n);
				g.buildMaze();
				g.solve_bfs();
				sopl("HE");
				sopl("The BFS solutions:");
				sopl("");
				g.printMaze(g.getCellRecord(), "bfs");
				g.printMaze(g.getCellRecord(), "hash");
				sopl("");
				sopl("The DFS solutions:");
				g.solve_dfs();
				g.printMaze(g.getCellRecord(), "dfs");
				g.printMaze(g.getCellRecord(), "hash");
				sopl("");
				sopl("===============\n    COMPLETE!   \n===============");
				sopl("");
				sopl("");
			}
			else sopl("I don't understand that answer, please reload the application and try again");
		}
		catch(Exception e){ sopl("Sorry, \"" + in + "\" isn't a valid dimension");};
	}
	//								main

	public CellGraph(int size){
		this.SIZE = size;
		this.TOTAL_CELLS = size * size;
		fillAdjacencyWithEmptyLLs();
		fillCellRecord();
		stringRep = "";
		generateRandomsSupply();

	}

	//fill the array of cells with empty cells and default values
	private void fillCellRecord(){
		Cell[] cr = new Cell[TOTAL_CELLS];
		for (int i = 0; i < cr.length;i++){
			Cell cell = new Cell(i);
			cell.row = i / SIZE;
			cell.column = i % SIZE;
			cr[i] = cell;
		}
		//fill the neighbor of each cell, check same row and column
		for (int i = 0; i < TOTAL_CELLS; i++ ) {
			if (i + 1 < cr.length)
				if (cr[i].row == cr[i + 1].row)
					cr[i].addNeighbor(i + 1);
			if (i - 1 >= 0) {
				if (cr[i].row == cr[i -1].row)
					cr[i].addNeighbor(i - 1);
			}
			//make sure the next one down in the column is not out of bounds
			if (i + SIZE < cr.length) {
				if (cr[i].column == cr[i + SIZE].column)
					cr[i].addNeighbor(i + SIZE);
			}
			if (i - SIZE >= 0) {
				if (cr[i].column == cr[i - SIZE].column)
					cr[i].addNeighbor(i - SIZE);
			}
		}
		cellRecord = cr;
	}

	//adjacency list is an arraylist of Linkedlists, initialize
	private void fillAdjacencyWithEmptyLLs(){
		this.adjacencyList = new ArrayList<LinkedList<Cell>>();
		for (int i = TOTAL_CELLS; i > 0; i--){
			this.adjacencyList.add(new LinkedList<Cell>());
		}
	}

	//work around for getting a random choice of eligible closed neighbors, was having a bug where maze would simply print a zig zag each time.
	private Queue<Integer> generateRandomsSupply(){
		Random rand = new Random(RANDOM_SEED);
		Queue<Integer> randomNumbersCache = new LinkedList<Integer>();
		int i = 0;
		while (i++ < CACHE_SIZE)
			randomNumbersCache.add(rand.nextInt(MAX_ELIGIBLE_NBORS));
		this.randomNumbersCache = randomNumbersCache;
		return randomNumbersCache;
	}

	public int grabRandom(int limit){
		Integer randomIndex = this.randomNumbersCache.poll();
		if (randomIndex == null)
			generateRandomsSupply();
		while(randomIndex >= limit)
			randomIndex = this.randomNumbersCache.poll();
		return (randomIndex);
	}

	//check each cell's list of neighbors for those that are intact (all walls)

	public int findClosedNeighbors(int cell){
		ArrayList<Integer> cell_nbors = cellRecord[cell].neighbors;
		ArrayList<Integer> potentials = new ArrayList<Integer>();
		for (Integer elt: cell_nbors) {
			if(cellRecord[elt].isWhole())
				potentials.add(elt);
		}
		//randomly return anyone if more than one
		if (potentials.size() > 0)
			return (potentials.size() > 1) ?  potentials.get(grabRandom(potentials.size())) : potentials.get(0);
		return 0;
}

	private void connectCells(int cell1, int cell2) {
		//fill the adjacency list of each cell with the adjacency of the other
		adjacencyList.get(cell1).add(cellRecord[cell2]);
		adjacencyList.get(cell2).add(cellRecord[cell1]);
		Cell first = cellRecord[cell1];
		Cell second = cellRecord[cell2];

		//cells are connected by having the wall between them removed
		if (first.row == second.row){
			if (first.position > second.position){
				first.west = false;
				second.east = false;
			}
			else {
				first.east = false;
				second.west = false;
			}
		}
		else if (first.column == second.column){
			if(first.position > second.position){
				first.north = false;
				second.south = false;
			}
			else{
				first.south = false;
				second.north = false;
			}
		}

	}

	//given maze generation algorithm
	public void buildMaze() {
		Stack<Integer> cellStack = new Stack<Integer>();
		int currentCell = 0;
		int visited = 1;

		while( visited < TOTAL_CELLS){
			int neighbor = findClosedNeighbors(currentCell);
			if( neighbor > 0){
				connectCells(currentCell, neighbor);
				cellStack.push(currentCell);
				currentCell = neighbor;
				visited++;
			}
			else if (!cellStack.isEmpty())
				currentCell = cellStack.pop();
		}
		//the top left and bottom right cells will be the entrance and exit, break their walls
		cellRecord[0].north = false;
		cellRecord[TOTAL_CELLS - 1].south = false;

	}

	void solve_bfs(){
		Cell source = cellRecord[0];
		for (int i = 0; i< TOTAL_CELLS; i++){
			Cell elt = cellRecord[i];
			elt.isWhite = true;;
			elt.bfs_dist = -1;
			elt.bfs_parent = null;
		}
		source.isGray = true;
		source.isWhite = false;
		source.bfs_dist = 0;
		source.bfs_parent = null;
		source.visitedOrder_bfs = 0;
		Queue<Cell> q = new LinkedList<Cell>();
		q.add(source);
		while(!q.isEmpty()){
			Cell u = q.poll();
			LinkedList<Cell> adjU = adjacencyList.get(u.position);
			Iterator<Cell> itr = adjU.iterator();
			int vis = 0;
			while(itr.hasNext()){
				Cell v = itr.next();
				if (v.isWhite){
					v.isGray = true;
					v.isWhite = false;
					v.bfs_dist = u.bfs_dist + 1;
					v.visitedOrder_bfs = vis;
					v.bfs_parent = u;
					v.visitedOrder_bfs = u.visitedOrder_bfs + 1;
					if(v.position == 0)
						v.visited = true;
					q.add(v);
				}
			}
			u.isBlack = true;
			u.isGray = false;
		}
		//the hash path will always start at the entrance and exit cells
		cellRecord[0].hash =  cellRecord[TOTAL_CELLS - 1].hash = true;
		//mark the correct path with hash symbols
		markPath(cellRecord[0], cellRecord[TOTAL_CELLS -1], "bfs");

	}

	public void solve_dfs(){
		for (Cell elt: cellRecord){
			elt.isWhite = true;
			elt.dfs_parent = null;
		}
		int time = 0;
		for (Cell elt: cellRecord){
			if (elt.isWhite)
				time = DFS_visit(elt, time);
		}
		//the hash will always start at eth entrance and exit of cells
		cellRecord[0].hash = cellRecord[TOTAL_CELLS - 1].hash = true;
		//mark the correct path with hash symbols
		markPath(cellRecord[0], cellRecord[TOTAL_CELLS -1], "dfs");
	}

	public int DFS_visit(Cell u, int time){
		time++;
		u.dfs_dist = time;
		u.isGray = true;
		u.isWhite = false;
		LinkedList<Cell> adj = adjacencyList.get(u.position);
		Iterator<Cell> itr = adj.iterator();
		while(itr.hasNext()){
			Cell v = itr.next();
			if(v.isWhite){
				v.dfs_parent = u;
				time = DFS_visit(v, time);
			}
		}
		u.isBlack = true;
		u.isGray = false;
		time++;
		u.f = time;
		return time;

	}

	//cellData must either be: " ", dfs, hash, or is by defualt bfs. " " will print
	public void printMaze(Cell[] maze, String cellData){
		int mazeSize = (int)Math.sqrt(maze.length);
		String build = "";
		int count = 0;
		int index = 0;
		for (int i = 0; i < mazeSize; i++)
		{
			while(count++ < mazeSize){
				if (maze[index].row == i)
					build += maze[index++].getNorth();
			}
			build += "+\n";
			count = 0;
			index -= mazeSize;

			while(count++ < mazeSize){
				if (maze[index].row == i) {
					build += maze[index].getWest() + maze[index].getValue(cellData);
					index++;
				}
			}
			build += "|\n";
			count = 0;
		}

		index = maze.length - mazeSize;
		while(index < maze.length){
			build += maze[index++].getSouth();
		}
		build += "+\n";
		ASCII_REP = build;
		sopl(build);
	}

	public void markPath(Cell start, Cell end, String dfsOrBfs) {
		Cell parent = (dfsOrBfs.equalsIgnoreCase("dfs"))? end.dfs_parent:end.bfs_parent;
		if (parent == null || start.position == end.position)
			return;
		else
			parent.hash = true;
		markPath(start, parent, dfsOrBfs);
	}

	static void sopl(Object x){
		System.out.println(x);
	}

	public Cell[] getCellRecord(){
		return cellRecord;
	}

	public List<LinkedList<Cell>> getAdjList() {
		return adjacencyList;
	}

	public String getASCII(){
		return ASCII_REP;
	}
	//||_____________________________inner class: Cell______________________|
	public class Cell {
		boolean west;
		boolean east;
		boolean north;
		boolean south;
		int visitedOrder_bfs;
		int visitedOrder_dfs;
		ArrayList<Integer> neighbors;
		int position;
		boolean hash;
		int row;
		int column;
		boolean isWhite;
		boolean isGray;
		boolean isBlack;
		Cell bfs_parent;
		Cell dfs_parent;
		int bfs_dist;
		int dfs_dist;
		int f;
		boolean bfsPath;
		boolean dfsPath;
		boolean visited;

		Cell(int position){
			west = north = south = east = true;
			isWhite = true;
			bfsPath = false;
			isBlack = isGray = false;
			visitedOrder_bfs = visitedOrder_dfs =  0;
			bfs_parent = null;
			dfs_parent = null;
			this.position = position;
			hash = false;
			row = column = -1;
			neighbors = new ArrayList<Integer>();

		}

		boolean isWhole(){
			return (east && south && west && north);
		}
		String getNorth() {
			String n = (north) ? "+----" : "+    ";
			return n;
		}
		String getEast(){
			String e =  east ? "|" : " ";
			return e;
		}

		String getWest(){
			String w = west ? "|" : " ";
			return w;
		}

		String getSouth(){
			return south ? "+----" : "+    ";

		}
		//will return the value of the cell depending on 1 of 4 choices
		String getValue(String choice){
			//choice will either be hash, blank, or dfs or bfs
			String b = Integer.toString(visitedOrder_bfs);
			String d = Integer.toString(dfs_dist);
			if (b.length() < 2)
				b = "0" + b;
			if (d.length() < 2)
				d = "0" + d;
			String bfs = " " + b +" ";
			String dfs = " " + d +" ";
			String blank = "    ";
			if (choice == "hash") {
				return (hash) ? "  # ": "    ";
			}
			else if (choice == "bfs") {
				return bfs;
			}
			else if (choice == "dfs") {
				return hash ? dfs : "    ";
			}
			return blank;
		}

		void setRow(int size){
			row = position / size;
			column = position % size;
		}

		void addNeighbor(int nei) {
			if (neighbors.size() < 4) {
				neighbors.add(nei);
			}
		}
	}
	//||_________________________end of    inner class Cell_________________
}
