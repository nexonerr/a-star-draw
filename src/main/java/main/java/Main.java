package main.java;

import processing.core.PApplet;

public class Main {

    public static final Render render = new Render();

    public static void main(String[] args) throws InterruptedException {
        PApplet.runSketch(new String[]{"Test"},render);
    }
}