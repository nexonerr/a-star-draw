package main.java;

import java.util.ArrayList;

public class Pathfinder {
    public Tile[][] matrix;
    public ArrayList<Tile> currentExplored;

    private ArrayList<TileGoal> toExplore = new ArrayList<>();
    private ArrayList<TileGoal> explored = new ArrayList<>();
    private ArrayList<Tile> exploredTile = new ArrayList<>();

    public Tile startTile;
    public Tile endTile;

    private TileGoal currentTileGoal;
    private TileGoal pathTileGoal;

    public ArrayList<Tile> foundPath = new ArrayList<>();
    public boolean finalised = false;
    public boolean failed = false;

    public Pathfinder(Tile[][] matrix){
        this.matrix = matrix;
    }

    public static Tile[][] createRandomMatrix(int width, int height, float obstructChance){
        Tile[][] matrixToReturn = new Tile[width][height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                matrixToReturn[i][j] = new Tile(Math.random() < obstructChance,i,j);
            }
        }
        return matrixToReturn;
    }

    public static void printTileMatrix(Tile[][] matrix){
        for (Tile[] row : matrix) {
            System.out.print('\n');
            for (Tile tile : row) {
                if (tile.obstructed()) {
                    System.out.print("\033[31m[B] ");
                } else {
                    System.out.print("\033[37m[0] ");
                }
            }
        }
    }

    public static void printTileMatrix(Tile[][] matrix, ArrayList<Tile> explored, Tile startTile, Tile endTile){
        for (Tile[] row : matrix) {
            System.out.print('\n');
            for (Tile tile : row) {

                if (explored == null){
                    System.out.println("Null");
                    return;
                }

                if (tile.x() == startTile.x() && tile.y() == startTile.y()) {
                    System.out.print("\033[93m[S] ");
                } else if (tile.x() == endTile.x() && tile.y() == endTile.y()) {
                    System.out.print("\033[93m[E] ");
                } else if (tile.obstructed()) {
                    System.out.print("\033[31m[B] ");
                } else if (explored.contains(tile)){
                    System.out.print("\033[32m[X] ");
                } else {
                    System.out.print("\033[37m[0] ");
                }
            }
        }
        System.out.print('\n');
    }

    /*public ArrayList<Tile> pathfind(int startX, int startY, int endX, int endY) {

        ArrayList<TileGoal> toExplore = new ArrayList<>();
        ArrayList<TileGoal> explored = new ArrayList<>();
        ArrayList<Tile> exploredTile = new ArrayList<>();
        currentExplored = exploredTile;

        final Tile startTile = matrix[startY][startX];
        final Tile endTile = matrix[endY][endX];

        TileGoal currentTileGoal = new TileGoal(null,startTile,calculateManhattanDistance(startTile,endTile),0);
        TileGoal pathTileGoal;

        while(true) {

            explored.add(currentTileGoal);
            exploredTile.add(currentTileGoal.tile());
            toExplore.addAll(getNearbyTiles(currentTileGoal, startTile, endTile, exploredTile, toExplore));

            printTileMatrix(matrix,exploredTile,startTile,endTile);

            for (int j = 0; j < toExplore.size(); j++) {
                if (exploredTile.contains(toExplore.get(j).tile())) toExplore.remove(j);
            }

            if (toExplore.size() == 0){
                printTileMatrix(matrix,exploredTile,startTile,endTile);
                return null;
            }

            TileGoal minTile = toExplore.get(0);

            for (TileGoal tileGoal : toExplore) {
                if (tileGoal.cost() < minTile.cost()) {
                    minTile = tileGoal;
                }
                if (tileGoal.tile() == endTile) {
                    minTile = tileGoal;
                    break;
                }
            }
            if (minTile.tile() == endTile){
                pathTileGoal = minTile;
                break;
            }
            currentTileGoal = minTile;
        }

        ArrayList<Tile> finalPath = new ArrayList<>();
        TileGoal currentFinalTileGoal = pathTileGoal;

        while(true){
            finalPath.add(currentFinalTileGoal.tile());
            currentFinalTileGoal = currentFinalTileGoal.precursor();
            if (currentFinalTileGoal.tile() == startTile) break;
        }
        System.out.println("Success");
        printTileMatrix(matrix,exploredTile,startTile,endTile);
        printTileMatrix(matrix,finalPath,startTile,endTile);
        return finalPath;
    }*/

    public void pathfind(int startX, int startY, int endX, int endY) {

        currentExplored = exploredTile;

        startTile = matrix[startY][startX];
        endTile = matrix[endY][endX];

        currentTileGoal = new TileGoal(null,startTile,calculateManhattanDistance(startTile,endTile),0);
    }

    public void step(){
        explored.add(currentTileGoal);
        exploredTile.add(currentTileGoal.tile());
        toExplore.addAll(getNearbyTiles(currentTileGoal, startTile, endTile, exploredTile, toExplore));
        currentExplored = exploredTile;

        for (int j = 0; j < toExplore.size(); j++) {
            if (exploredTile.contains(toExplore.get(j).tile())) toExplore.remove(j);
        }

        if (toExplore.size() == 0){
            failed = true;
            return;
        }

        TileGoal minTile = toExplore.get(0);

        for (TileGoal tileGoal : toExplore) {
            if (tileGoal.cost() < minTile.cost()) {
                minTile = tileGoal;
            }
            if (tileGoal.tile() == endTile) {
                minTile = tileGoal;
                break;
            }
        }
        if (minTile.tile() == endTile){
            pathTileGoal = minTile;
            finalise();
        }
        currentTileGoal = minTile;
    }

    public void finalise(){
        ArrayList<Tile> finalPath = new ArrayList<>();
        TileGoal currentFinalTileGoal = pathTileGoal;

        while(true){
            finalPath.add(currentFinalTileGoal.tile());
            currentFinalTileGoal = currentFinalTileGoal.precursor();
            if (currentFinalTileGoal.tile() == startTile) break;
        }
        System.out.println("Success");
        printTileMatrix(matrix,exploredTile,startTile,endTile);
        printTileMatrix(matrix,finalPath,startTile,endTile);

        finalised = true;
        foundPath = finalPath;
    }

    private ArrayList<TileGoal> getNearbyTiles(TileGoal toGet, Tile startTile, Tile endTile,
                                               ArrayList<Tile> explored, ArrayList<TileGoal> toExplore){
        ArrayList<TileGoal> returnArr = new ArrayList<>();

        int x = toGet.tile().x();
        int y = toGet.tile().y();

        for (int i = -1; i < 2; i++) {
            for (int j = -1; j < 2; j++) {

                int xToCheck = clamp(x + i,0, matrix.length -1);
                int yToCheck = clamp(y + j,0,matrix[x].length -1);

                TileGoal toCheck = new TileGoal(toGet, matrix[xToCheck][yToCheck],
                        calculateManhattanDistance(matrix[xToCheck][yToCheck], endTile) + toGet.startDistance() + 1,
                        toGet.startDistance() + 1);

                if (!explored.contains(toCheck.tile()) && !returnArr.contains(toCheck) &&
                        (!toCheck.tile().obstructed() || toCheck.tile() == endTile)) {
                    returnArr.add(toCheck);
                }
            }
        }
        return returnArr;
    }

    private static int calculateManhattanDistance(Tile start, Tile end){
        return Math.abs(start.x() - end.x()) + Math.abs(start.y() - end.y());
    }

    private int clamp(int val, int min, int max) {
        return Math.max(min, Math.min(max, val));
    }

}
