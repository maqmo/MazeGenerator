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
		adjacencyList = new ArrayList<LinkedList<Cell>>(TOTAL_CELLS);
		cellRecord = fillCellRecord();
	}

	private Cell[] fillCellRecord(){

		//each cell should know its position in the list
		Cell[] cr = new Cell[TOTAL_CELLS];
		for (int i = 0; i < cr.length;i++){
			cr[i] = new Cell(i);
		}
		if (cr[TOTAL_CELLS - 1] == null)
			System.out.println("problem in generate cells method line 40");
		return cr;
	}

	public int findClosedNeighbors(int cell){
		/*find neighbors:
		-check position +1, -1, + size, - size. if all are intact, add to the neighbors array
		-if more than one return Math.random() * neighbors.length() + 1
		*/
		ArrayList<Cell> neighbors = new ArrayList<Cell>();
		int north = cell + SIZE;
		int south = cell - SIZE;
		int east = cell + 1;
		int west = cell - 1;

		// modulo checks column, dividing checks row
		if (north < SIZE && cell % SIZE == north % SIZE && cellRecord[north].isWhole())
			neighbors.add(cellRecord[north]);
		if (south >= 0 && cell % SIZE == south % SIZE && cellRecord[south].isWhole())
			neighbors.add(cellRecord[south]);
		if (east < SIZE && cell / SIZE == east / SIZE && cellRecord[east].isWhole());
			neighbors.add(cellRecord[east]);
		if (west >= 0 && cell / SIZE == west / SIZE && cellRecord[west].isWhole());
			neighbors.add(cellRecord[west]);
		if (neighbors.size() > 1){
			return (neighbors.get((int)(Math.floor((Math.random() * neighbors.size()) + 1))).position);
		}

		else if (neighbors.size() == 1)
			return neighbors.get(0).position;
		return 0;
	}

	public void connectCells(int cell1, int cell2){
		/*add cell2 as a neighbor of cell1

			get cell1's position
			access that index in the adj list
			since an empty linked list is there, add to its head cell2.

			when making a connection between cells
		take the cell's position, and in that index of the adj list, add a node (the cell its connected to)

		*/
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
	public void buildMaze(){
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

	}

//||_____________________________inner class: Cell______________________|
		private class Cell {
		boolean west;
		boolean east;
		boolean north;
		boolean south;
		int visitedOrder;
		int position;

		Cell(int position){
			west = north = south = east = true;
			visitedOrder = 0;
			this.position = position;
		}

		boolean isWhole(){
			return (west&&north);
		}

		void printCell(){
			//only print the 'u's' the rest will be done manually

			//wrong

			//must account for dividers and separators
		}
		void sop(Object x){
			System.out.print(x);
		}
	}
	//||_________________________inner class Cell_________________


	public static void main(String[] args){
		//print 0 row tops
		System.out.println("+  +--+--+--");
		//print 0 sides:
		System.out.println("| #|  |  |  |");
		//print 1 row tops:
		System.out.println("+  +--+--+--");
		//print 1 row sides:
		System.out.println("|  | #|  |  |");
		//print bottom:
		System.out.println("+--+--+--+  ");

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