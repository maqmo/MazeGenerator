import java.util.LinkedList;
import java.util.List;
import java.util.ArrayList;
import java.util.Stack;
import java.lang.Math;

/*
begin at the starting room and end at the terminating room, end as soon as it is found
for each search algorithm, you must print out the order in which rooms were visited and indicate the shortest solution path
from starting to finishing room

output:
the program should output the maze, then the dfs solution, then the bfs solution


*/

public class CellGraph {

	private List<LinkedList<Cell>> adjacencyList;
	private Cell[] cellRecord;
	private final int SIZE;
	private final int TOTAL_CELLS;

	public CellGraph(int size){
		this.SIZE = size;
		this.TOTAL_CELLS = size * size;
		fillAdjacencyWithEmptyLLs();
		fillCellRecord();
	}

	private void fillCellRecord(){

		//each cell should know its position in the list
		Cell[] cr = new Cell[TOTAL_CELLS];
		for (int i = 0; i < cr.length;i++){
			Cell cell = new Cell(i);
			cell.row = i / SIZE;
			cell.column = i % SIZE;
			cr[i] = cell;
		}
		if (cr[TOTAL_CELLS - 1] == null)
			System.out.println("problem in generate cells method line 40");
		cellRecord = cr;
	}

	private void fillAdjacencyWithEmptyLLs(){
		adjacencyList = new ArrayList<LinkedList<Cell>>();
		for (int i = SIZE; i > 0; i--){
			adjacencyList.add(new LinkedList<Cell>());
		}
		sopl(adjacencyList.get(0).peek());
	}

	private int findClosedNeighbors(int cell){
		/*find neighbors:
		-check position +1, -1, + size, - size. if all are intact, add to the neighbors array
		-if more than one return Math.random() * neighbors.length() + 1
		*/
		ArrayList<Cell> neighbors = new ArrayList<Cell>();
		int north = cell + SIZE;
		int south = cell - SIZE;
		int east = cell + 1;
		int west = (cell > 0) ? (cell - 1):cell;
		//sopl(north + " " + east + " " + south + " " + west);

		// modulo checks column, dividing checks row
		if (north < SIZE && cell % SIZE == north % SIZE && cellRecord[north].isWhole())
			neighbors.add(cellRecord[north]);
		if (south >= 0 && cell % SIZE == south % SIZE && cellRecord[south].isWhole())
			neighbors.add(cellRecord[south]);
		if (east < SIZE && cell / SIZE == east / SIZE && cellRecord[east].isWhole())
			neighbors.add(cellRecord[east]);
		if (west >= 0 && cell / SIZE == west / SIZE && cellRecord[west].isWhole())
				neighbors.add(cellRecord[west]);
		if (neighbors.size() > 1){
			return (neighbors.get((int)(Math.floor((Math.random() * neighbors.size())))).position);
		}

		else if (neighbors.size() == 1)
			return neighbors.get(0).position;
		return 0;
	}

	private void connectCells(int cell1, int cell2){
		/*add cell2 as a neighbor of cell1

			get cell1's position
			access that index in the adj list
			since an empty linked list is there, add to its head cell2.

			when making a connection between cells
		take the cell's position, and in that index of the adj list, add a node (the cell its connected to)

		*/
		sopl("adjaceny list size is: " + 0);
		adjacencyList.get(cell1).add(cellRecord[cell2]);
		if (cell1 == cell2)
			return;
		boolean sameRow = false;
		boolean cell1IsLater = ((cell1 - cell2) > 0);
		Cell first = cellRecord[cell1];
		Cell second = cellRecord[cell2];

		// check if same row:
		if (cell1 / SIZE == cell2 / SIZE){
			sameRow = true;
		}

		if (sameRow){
			if (cell1IsLater){
				first.west = false;
				second.east = false;
			}
			else {
				first.east = false;
				second.west = false;
			}
		}
		else {
			if(cell1IsLater){
				first.north = false;
				second.south = false;
			}
			else{
				first.south = false;
				second.north = false;
			}
		}

	}

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
			else
				currentCell = cellStack.pop();
		}
	}

	public void solve_BFS(){
		return;
	}

	public Cell[] getCellRecord(){
		return cellRecord;
	}

	public void printMaze(Cell[] maze){
		int mazeSize = (int)Math.sqrt(maze.length);
		String build = "";
		int count = 0;
		int index = 0;
		sopl("here");
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
					build += maze[index].getWest() + maze[index].getValue() ;
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
		sopl(build);
	}

	// public void printMaze(Cell[] maze){
	// 	int mazeSize = (int)Math.sqrt(maze.length);
	// 	String[] output = new String[2*mazeSize + 1];
	// 	output[0] = buildString("north", maze, "") + "+";
	// 	output[1] = buildString("west", maze, output[0]) + "|";
	// 	for (int i = maze.length - mazeSize - 1; i < maze.length; i++)
	// 		output[3] = maze[i].getSouth();
	// 	output[3] += "+";
	// }

	private void sop(Object x){
		System.out.print(x);
	}

	private void sopl(Object x){
		System.out.println(x);
	}

//||_____________________________inner class: Cell______________________|
		public class Cell {
		boolean west;
		boolean east;
		boolean north;
		boolean south;
		int visitedOrder;
		int position;
		boolean hash;
		int row;
		int column;

		Cell(int position){
			west = north = south = east = true;
			visitedOrder = 0;
			this.position = position;
			hash = false;
			row = column = -1;
		}

		Cell(int pos, boolean n, boolean e, boolean s, boolean w){
			position = pos;
			north = n;
			east = e;
			south = s;
			west = w;
			hash = false;
			row = column = -1;
		}

		boolean isWhole(){
			return (west&&north);
		}
		String getNorth() {
			String n = (north) ? "+---" : "+   ";
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
			String s = south ? "+---" : "+   ";
			return s;
		}
		String getValue(){
			String v = hash ? " # " : (" " + Integer.toString(position) + " ");
			return v;
		}

		void setRow(int size){
			row = position / size;
			column = position % size;
		}
	}
	//||_________________________inner class Cell_________________

	public Cell[] getSampleMaze(){
		Cell[] m = {
			new Cell(0, false, true, false, true),
			new Cell(1, true, false, true, false),
			new Cell(2, false, false, true, true),
			new Cell(3, true, false, false, false)
		};
		for (Cell elt: m){
			elt.setRow(2);
		}
		return m;
	}
	public Cell[] getSampleMaze1(){
		Cell[] m = { new Cell(0, true, true, true, true),
		new Cell(1, true, true, true, true),
		new Cell(2, true, true, true, true),
		new Cell(3, true, true, true, true)};
		for (Cell elt: m){
			elt.setRow(2);
		}
		return m;
	}



	public static void main(String[] args){
		CellGraph g = new CellGraph(9);
		g.buildMaze();
		g.printMaze(g.getCellRecord());

	}
}


		/*
		algorithm to make a maze:

create a CellStack (LIFO) to hold a list of cell locations
set TotalCells= number of cells in grid
choose the starting cell and call it CurrentCell

set VisitedCells = 1

while VisitedCells < TotalCells {
	find all neighbors of CurrentCell with all walls intact
	if one or more found choose one at random
	{
		knock down the wall between it and CurrentCell
		push CurrentCell location on the CellStack
		make the new cell CurrentCell
		add 1 to VisitedCell
	}
	else
		pop the most recent cell entry off the CellStack make it CurrentCell
}

		*/