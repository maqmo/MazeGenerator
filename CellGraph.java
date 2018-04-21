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
	private final DIMENSION;
	private final TOTAL_CELLS;

	public CellGraph(int size){
		this.DIMENSION = size;
		this.TOTAL_CELLS = size * size;
		adjacencyList = new ArrayList<LinkedList<Cell>>(TOTAL_CELLS);
		cellRecord = generateCells();
	}

	public void generateCells(){
		/*
		-this method will make an array of unconnected cells
		-each cell should be default (intact)
		-have each cell know its position in the list

		*/

		for (int i=0; i < cellRecord.length();i++){
			cellRecord[i] = new Cell(i);
		}
	}

	public int findClosedNeighbors(int cell){
		/*find neighbors:
		-check position +1, -1, + size, - size. if all are intact, add to the neighbors array
		-if more than one return Math.random() * neighbors.length() + 1
		*/
		ArrayList<Cell> neighbors = new ArrayList<Cell>();
		int north = cell + size;
		int south = cell - size;
		int east = cell + 1;
		int west = cell - 1;

		// modulo checks column, dividing checks row
		if (north < size && cell % size == north % size && cellRecord[north].isWhole())
			neighbors.add(cellRecord[north]);
		if (south >= 0 && cell % size == south % size && cellRecord[south].isWhole())
			neighbors.add(cellRecord[south]);
		if (east < size && cell / size == east / size && cellRecord[east].isWhole());
			neighbors.add(cellRecord[east]);
		if (west >= 0 && cell / size == west / size & && cellRecord[west].isWhole());
			neighbors.add(cellRecord[west]);
		if (neighbors.size() > 1)
			return (neighbors.get(Math.random() * neighbors.size() + 1).position);
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
		Cell cell1 = cellRecord[cell1];
		Cell cell2 = cellRecord[cell2];

		// check if same row:
		if (cell1 / size == cell2 / size){
			sameRow = true;
		}

		if (sameRow){
			if (cell1IsLater){
				cell1.west = false;
				cell2.east = false;
			}
			else {
				cell1.east = false;
				cell2.west = false;
			}
		}
		else {
			if(cell1IsLater){
				cell1.north = false;
				cell2.south = false;
			}
			else{
				cell1.south = false;
				cell2.north = false;
			}
		}

	}
	public void buildMaze(){
		Stack<Integer> cellStack = new Stack<Integer>();
		int currentCell = 0;
		int visited = 1;

		while( visited < TOTAL_CELLS){
			int neighbor = findClosedNeighbor(currentCell);
			if( neighbor > 0){
				connectCells(currentCell, neighbor);
				cellStack.push(currentCell);
				currentCell = neighbor;
				vistited++;
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
		boolean south;
		boolean north;
		boolean isEntrance;
		boolean isExit;
		int visitedOrder;
		int position

		Cell(int position){
			west = east = south = north = true;
			isEntrance = isExit = false;
			visitedOrder = 0;
			stamp = '';
			this.position =position;
		}

		boolean isWhole(){
			return (west||east||south||north)
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
	//||_________________________inner class Cell_________________|
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