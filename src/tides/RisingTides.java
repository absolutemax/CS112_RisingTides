package tides;

import java.util.*;

/**
 * This class contains methods that provide information about select terrains 
 * using 2D arrays. Uses floodfill to flood given maps and uses that 
 * information to understand the potential impacts. 
 * Instance Variables:
 *  - a double array for all the heights for each cell
 *  - a GridLocation array for the sources of water on empty terrain 
 * 
 * @author Original Creator Keith Scharz (NIFTY STANFORD) 
 * @author Vian Miranda (Rutgers University)
 */
public class RisingTides {

    // Instance variables
    private double[][] terrain;     // an array for all the heights for each cell
    private GridLocation[] sources; // an array for the sources of water on empty terrain 

    /**
     * DO NOT EDIT!
     * Constructor for RisingTides.
     * @param terrain passes in the selected terrain 
     */
    public RisingTides(Terrain terrain) {
        this.terrain = terrain.heights;
        this.sources = terrain.sources;
    }

    /**
     * Find the lowest and highest point of the terrain and output it.
     * 
     * @return double[][], with index 0 and index 1 being the lowest and 
     * highest points of the terrain, respectively
     */
    public double[] elevationExtrema() {
        double[] lowestAndHighest = {0,0};

        for(double[] r : terrain) {
            for(double c : r) {
                if (c < lowestAndHighest[0]) {
                    lowestAndHighest[0] = c;
                }
                if (c > lowestAndHighest[1]) {
                    lowestAndHighest[1] = c;
                }
            }
        }
    
        return lowestAndHighest;
    }

    /**
     * Implement the floodfill algorithm using the provided terrain and sources.
     * 
     * All water originates from the source GridLocation. If the height of the 
     * water is greater than that of the neighboring terrain, flood the cells. 
     * Repeat iteratively till the neighboring terrain is higher than the water 
     * height.
     * 
     * 
     * @param height of the water
     * @return boolean[][], where flooded cells are true, otherwise false
     */
    public boolean[][] floodedRegionsIn(double height) {
        boolean[][] resultingArray = new boolean[terrain.length][terrain[0].length];
        ArrayList<GridLocation> queue = new ArrayList<GridLocation>();

        for(GridLocation source : sources) {
            queue.add(source);
            resultingArray[source.row][source.col] = true;
        }
        
        while(queue.size() > 0) {
            GridLocation source = queue.remove(0);
            
            // top coordinate
            int topNeighborRow = source.row - 1;
            int topNeighborCol = source.col;
            if(source.row > 0 && resultingArray[topNeighborRow][topNeighborCol] == false && terrain[topNeighborRow][topNeighborCol] <= height) {
                queue.add(new GridLocation(topNeighborRow, topNeighborCol));
                resultingArray[topNeighborRow][topNeighborCol] = true;
            }
            // bottom coordinate
            int bottomNeighborRow = source.row + 1;
            int bottomNeighborCol = source.col;
            if(source.row < resultingArray.length - 1 && resultingArray[bottomNeighborRow][bottomNeighborCol] == false && terrain[bottomNeighborRow][bottomNeighborCol] <= height) {
                queue.add(new GridLocation(bottomNeighborRow, bottomNeighborCol));
                resultingArray[bottomNeighborRow][bottomNeighborCol] = true;
            }
            // left coordinate
            int leftNeighborRow = source.row;
            int leftNeighborCol = source.col - 1;
            if(source.col > 0 && resultingArray[leftNeighborRow][leftNeighborCol] == false && terrain[leftNeighborRow][leftNeighborCol] <= height) {
                queue.add(new GridLocation(leftNeighborRow, leftNeighborCol));
                resultingArray[leftNeighborRow][leftNeighborCol] = true;
            }
            // right coordinate
            int rightNeighborRow = source.row;
            int rightNeighborCol = source.col + 1;
            if(source.col < resultingArray[0].length - 1 && resultingArray[rightNeighborRow][rightNeighborCol] == false && terrain[rightNeighborRow][rightNeighborCol] <= height) {
                queue.add(new GridLocation(rightNeighborRow, rightNeighborCol));
                resultingArray[rightNeighborRow][rightNeighborCol] = true;
            }
        }

        return resultingArray;
    }

    /**
     * Checks if a given cell is flooded at a certain water height.
     * 
     * @param height of the water
     * @param cell location 
     * @return boolean, true if cell is flooded, otherwise false
     */
    public boolean isFlooded(double height, GridLocation cell) {    
        boolean[][] floodedArea = floodedRegionsIn(height);
        return floodedArea[cell.row][cell.col];
    }

    /**
     * Given the water height and a GridLocation find the difference between 
     * the chosen cells height and the water height.
     * 
     * If the return value is negative, the Driver will display "meters below"
     * If the return value is positive, the Driver will display "meters above"
     * The value displayed will be positive.
     * 
     * @param height of the water
     * @param cell location
     * @return double, representing how high/deep a cell is above/below water
     */
    public double heightAboveWater(double height, GridLocation cell) {
        return terrain[cell.row][cell.col] - height;
    }

    /**
     * Total land available (not underwater) given a certain water height.
     * 
     * @param height of the water
     * @return int, representing every cell above water
     */
    public int totalVisibleLand(double height) {
        boolean[][] floodedArea = floodedRegionsIn(height);
        int landAreaCount = 0;

        for(boolean[] r : floodedArea) {
            for(boolean c : r) {
                if(c == false) {
                    landAreaCount ++;
                }
            }
        }

        return landAreaCount;
    } 


    /**
     * Given 2 heights, find the difference in land available at each height. 
     * 
     * If the return value is negative, the Driver will display "Will gain"
     * If the return value is positive, the Driver will display "Will lose"
     * The value displayed will be positive.
     * 
     * @param height of the water
     * @param newHeight the future height of the water
     * @return int, representing the amount of land lost or gained
     */
    public int landLost(double height, double newHeight) {
        return totalVisibleLand(height) - totalVisibleLand(newHeight);
    }



    /**
     * Count the total number of islands on the flooded terrain.
     * 
     * Parts of the terrain are considered "islands" if they are completely 
     * surround by water in all 8-directions. Should there be a direction (ie. 
     * left corner) where a certain piece of land is connected to another 
     * landmass, this should be considered as one island. A better example 
     * would be if there were two landmasses connected by one cell. Although 
     * seemingly two islands, after further inspection it should be realized 
     * this is one single island. Only if this connection were to be removed 
     * (height of water increased) should these two landmasses be considered 
     * two separate islands.
     * 
     * @param height of the water
     * @return int, representing the total number of islands
     */
    public int numOfIslands(double height) {
        boolean[][] floodedArea = floodedRegionsIn(height);
        int count = 0;
        WeightedQuickUnionUF islands = new WeightedQuickUnionUF(floodedArea.length, floodedArea[0].length);

        for(int r = 0; r < floodedArea.length; r++) {
            for(int c = 0; c < floodedArea[0].length; c++) {
                while(floodedArea[r][c] == false) {
                    // top coordinate
                    if(r != 0 && floodedArea[r - 1][c] == false) {
                        GridLocation cell1 = new GridLocation(r, c);
                        GridLocation cell2 = new GridLocation(r - 1, c);
                        islands.union(cell1, cell2);
                    }
                    // bottom coordinate
                    if(r != floodedArea.length && floodedArea[r + 1][c] == false) {
                        GridLocation cell1 = new GridLocation(r, c);
                        GridLocation cell2 = new GridLocation(r + 1, c);
                        islands.union(cell1, cell2);
                    }
                    // left coordinate
                    if(c != 0 && floodedArea[r][c - 1] == false) {
                        GridLocation cell1 = new GridLocation(r, c);
                        GridLocation cell2 = new GridLocation(r, c - 1);
                        islands.union(cell1, cell2);
                    }
                    // right coordinate
                    if(c != floodedArea[0].length && floodedArea[r][c + 1] == false) {
                        GridLocation cell1 = new GridLocation(r, c);
                        GridLocation cell2 = new GridLocation(r, c + 1);
                        islands.union(cell1, cell2);
                    }

                    // top left coordinate
                    if(r != 0 && c != 0 && floodedArea[r - 1][c - 1] == false) {
                        GridLocation cell1 = new GridLocation(r, c);
                        GridLocation cell2 = new GridLocation(r - 1, c - 1);
                        islands.union(cell1, cell2);
                    }
                    // bottom left coordinate
                    if(r != floodedArea.length && c != 0 && floodedArea[r + 1][c - 1] == false) {
                        GridLocation cell1 = new GridLocation(r, c);
                        GridLocation cell2 = new GridLocation(r + 1, c - 1);
                        islands.union(cell1, cell2);
                    }
                    // top right coordinate
                    if(r != 0 && c != floodedArea[0].length && floodedArea[r - 1][c + 1] == false) {
                        GridLocation cell1 = new GridLocation(r, c);
                        GridLocation cell2 = new GridLocation(r - 1, c + 1);
                        islands.union(cell1, cell2);
                    }
                    // bottom right coordinate
                    if(r != floodedArea.length && c != floodedArea[0].length && floodedArea[r + 1][c + 1] == false) {
                        GridLocation cell1 = new GridLocation(r, c);
                        GridLocation cell2 = new GridLocation(r + 1, c + 1);
                        islands.union(cell1, cell2);
                    }
                }
            }
        }

        for(int r = 0; r < floodedArea.length; r++) {
            for(int c = 0; c < floodedArea[0].length; c++) {
                GridLocation cell = new GridLocation(r, c);
                if(islands.getSize(cell) == (r+1)*(c+1)) {
                    count ++;
                }
            }
        }
        return count;
    }
}
