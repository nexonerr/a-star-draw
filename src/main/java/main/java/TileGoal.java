package main.java;

public record TileGoal(TileGoal precursor, Tile tile, int cost, int startDistance) {
}
