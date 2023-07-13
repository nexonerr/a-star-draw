package main.java;

import processing.core.PApplet;
import processing.core.PImage;

import java.util.ArrayList;

public class Render extends PApplet {

    public PImage originalImage;
    public Tile[][] matrix;
    public Pathfinder pathfinder;
    private boolean showFps = false;

    @Override
    public void settings() {
        //fullScreen();
        size(900,900);
    }

    @Override
    public void setup() {
        background(255);

        stroke(255);

        resetImage();
        resetPathfinder();

        frameRate(matrix.length);
        textSize(15f);
    }

    @Override
    public void keyReleased() {
        if (key == 'r') {
            resetPathfinder();
        } else if (key == 'i') {
            resetImage();
            resetPathfinder();
        } else if (key == 'f') {
            showFps = !showFps;
        } else if (keyCode == UP) {
            frameRate(frameRate + 5);
        } else if (keyCode == DOWN) {
            frameRate(frameRate - 5);
        }
    }

    @Override
    public void draw() {
        if (!pathfinder.finalised) {
            pathfinder.step();
            drawMatrix(pathfinder.currentExplored,pathfinder.startTile,pathfinder.endTile,false,pathfinder.failed);
        } else {
            drawMatrix(pathfinder.foundPath,pathfinder.startTile,pathfinder.endTile,true,false);
        }

        if (showFps) {
            fill(0,255,0);
            text(frameRate,10,30);
            text(frameCount,15,60);
            text(frameCount / frameRate,10,90);
        }
    }

    private void drawMatrix(ArrayList<Tile> traveled, Tile startTile, Tile endTile, boolean found, boolean failed){
        Tile tile;

        for (int i = 0; i < matrix.length; i += 1) {
            for (int j = 0; j < matrix[0].length; j += 1) {
                tile = matrix[j][i];

                if (j == startTile.x() && i == startTile.y()) {
                    fill(255,255,20);
                    stroke(255,255,20);
                } else if (j == endTile.x() && i == endTile.y()) {
                    fill(255,255,20);
                    stroke(255,255,20);
                } else if (tile.obstructed()) {
                    fill(0);
                    stroke(0);
                } else if (traveled.contains(tile) && found){
                    fill(20,255,20);
                    stroke(20,255,20);
                } else if (traveled.contains(tile) && failed){
                    fill(255,20,20);
                    stroke(255,20,20);
                } else if (traveled.contains(tile)) {
                    fill(255, 180, 20);
                    stroke(255, 180, 20);
                } else {
                    fill(255);
                    stroke(255);
                }

                int sizeX = width / matrix[0].length;
                int sizeY = height / matrix.length;

                rect(i * sizeX,j * sizeY,sizeX,sizeY);
            }
        }
    }

    private void resetPathfinder() {

        int startX = (int) (Math.random() * (matrix[0].length - 1));
        int endX = (int) (Math.random() * (matrix[0].length - 1));

        int startY = (int) (Math.random() * (matrix.length - 1));
        int endY = (int) (Math.random() * (matrix.length - 1));

        checkLoopStart:
        while(true) {
            for (int i = -1; i < 2; i++) {
                for (int j = -1; j < 2; j++) {
                    try {
                        if (i == 0 && j == 0) continue;
                        if (!matrix[startY + i][startX + j].obstructed()) {
                            break checkLoopStart;
                        }
                    } catch (ArrayIndexOutOfBoundsException ignored) {}
                }
            }
            startX = (int) (Math.random() * (matrix[0].length - 1));
            startY = (int) (Math.random() * (matrix.length - 1));
        }

        checkLoopEnd:
        while(true) {
            for (int i = -1; i < 2; i++) {
                for (int j = -1; j < 2; j++) {
                    try {
                        if (i == 0 && j == 0) continue;
                        if (!matrix[endY + i][endX + j].obstructed()) {
                            break checkLoopEnd;
                        }
                    } catch (ArrayIndexOutOfBoundsException ignored) {}
                }
            }
            endX = (int) (Math.random() * (matrix[0].length - 1));
            endY = (int) (Math.random() * (matrix.length - 1));
        }

        pathfinder = new Pathfinder(matrix);
        pathfinder.pathfind(startX,startY,endX,endY);
    }

    private void resetImage() {
        originalImage = loadImage("https://picsum.photos/900","png");
        //originalImage = loadImage("/src/main/resources/maze.jpg");
        originalImage.filter(THRESHOLD,0.5f);

        image(originalImage,0,0);

        matrix = processImage(originalImage,9);
    }

    private Tile[][] processImage(PImage originalImage, int processFactor){

        loadPixels();

        Tile[][] matrix = new Tile[originalImage.pixelHeight / processFactor][originalImage.pixelWidth / processFactor];

        for (int i = 0; i < originalImage.pixelHeight / processFactor; i ++) {
            for (int j = 0; j < originalImage.pixelWidth / processFactor; j ++) {

                if (originalImage.pixels.length == ((i * originalImage.pixelWidth * processFactor) + j * processFactor)) break;

                int pixel = originalImage.pixels[((i * originalImage.pixelWidth * processFactor) + j * processFactor)];

                matrix[i][j] = new Tile(pixel != -1,i,j);

            }
        }

        return matrix;

    }
}
