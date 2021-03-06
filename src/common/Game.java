package common;

import Board.Board;
import army.Army;
import army.ArmyColor;
import army.unit.Unit;
import Board.Tile;
import java.io.*;
import java.util.List;
import java.util.Random;

public class Game implements Serializable {
    private Army currentArmy;
    private Army enemyArmy;
    private Board board;

    public Game() {
        Board board = new Board();
        currentArmy = new Army(ArmyColor.BLUE);
        enemyArmy = new Army(ArmyColor.RED);
    }

    public void placeUnit(Unit unitOfChoice, Position destination){
        currentArmy.placeUnit(unitOfChoice, destination);
    }

    public void move(Unit unitOfChoice, Position destination){
        checkMove(unitOfChoice, destination);
    }

    public void checkMove(Unit unitOfChoice, Position destination){
        boolean isInBound = board.isInBounds(destination);
        boolean canMoveTo = unitOfChoice.canMoveTo(destination);
        boolean isAvailable = board.tileIsFree(destination);
        boolean isAccessable = board.isAccessable(destination);
        boolean friendlyAtPosition = currentArmy.hasUnitAtPosition(destination);
    }

    public void doMove(Unit unitOfChoice, Position destination){
            boolean available = board.tileIsFree(destination);
            if (available){
                currentArmy.placeUnit(unitOfChoice, destination);
                return;
            }
            battleEnemyAtPlace(unitOfChoice, destination);

    }

    public void battleEnemyAtPlace(Unit unitOfChoice, Position destination){
        boolean enemyUnitAtPlace = enemyArmy.hasUnitAtPosition(destination);
        if (enemyUnitAtPlace){
            unitOfChoice.battle(enemyArmy.getUnitAtPosition(destination));
        }
    }

    public void processturn(){
        swapTurns();
    }

    public void update(){
        board.clear();
        List<Unit> currentUnitsOnBoard = currentArmy.getPlacedUnits();
        List<Unit> enemyUnitsOnBoard = enemyArmy.getPlacedUnits();
        boolean player = currentArmy.getColor() == ArmyColor.BLUE;
        boolean otherPlayer = enemyArmy.getColor() == ArmyColor.RED;
        board.update(currentUnitsOnBoard, player);
        board.update(enemyUnitsOnBoard, otherPlayer);
    }

    public void swapTurns(){

        Army swapArmy = currentArmy;
        currentArmy = enemyArmy;
        enemyArmy = swapArmy;
    }

    public void saveArmy(){
        FileManager.write((Serializable) currentArmy, "LastArmy.txt");
    }

    public void loadArmyConfig() throws Exception {
        try {
            currentArmy = (Army) common.FileManager.read("ArmyConfig.txt");
        } catch (FileNotFoundException e) {
            throw new ExceptionInInitializerError("File not found");
        } catch (IOException e) {
            throw new ExceptionInInitializerError("Error parsing data from file");
        } catch (Exception e) {
            throw new ExceptionInInitializerError("Caught Exception, contact administrator for further information.");
        }
    }

    public boolean isPlaying() {
        return !currentArmy.isDefeated() && !enemyArmy.isDefeated();
    }

    public ArmyColor getWinner() {
        return this.currentArmy.isDefeated() ? this.enemyArmy.getColor() : this.currentArmy.getColor();
    }

    public Unit getUnitAtPositionOfArmy(Position position) {
        return currentArmy.getUnitAtPosition(position);
    }

    public boolean armyHasUnitAtPosition(Position position){
        return currentArmy.hasUnitAtPosition(position);
    }

    public String getSelectedUnitInformation(Unit unit) {
        return String.format("Unit selected: %s at position (%d,%d)\n", unit.getClass().getSimpleName(), unit.getX(), unit.getY());
    }

    public List<Unit> getUnitsToPlace(Position position){
        return currentArmy.getUnitsToPlace();
    }

    public List<Unit> getArmyUnitsToPlace(){
        return currentArmy.getUnitsToPlace();
    }

    public boolean currentArmyHasUnitsToPlace() {
        return currentArmy.hasUnitsToPlace();
    }

    public List<Unit> getCurrentArmyDeadUnits(){
        return currentArmy.getDeadUnits();
    }

    public List<Unit> getEnemyArmyDeadUnits(){
        return enemyArmy.getDeadUnits();
    }

    public ArmyColor getArmyColor(){
        return currentArmy.getColor();
    }

    public Tile[][] getGamefield(){
        return board.getGameField();
    }

    public void computerPlaceArmy() {
        Random rand = new Random();
        while (currentArmy.hasUnitsToPlace()) {
            List<Unit> unitsToPlace = currentArmy.getUnitsToPlace();
            Unit selectedUnit = unitsToPlace.get(rand.nextInt(unitsToPlace.size()));
            Position unitDestination = new Position(rand.nextInt(10), rand.nextInt(10));
            try {
                placeUnit(selectedUnit, unitDestination);
            } catch (Exception e) {
            }
        }
    }

    public Unit selectRandomUnit(){
        return currentArmy.selectRandomPlacedUnit();
    }
}
