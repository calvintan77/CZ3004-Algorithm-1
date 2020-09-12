package algorithms;

import map.*;
import robot.Robot;
import robot.Constant;
import robot.Constant.DIRECTION;
import robot.Constant.MOVEMENT;
import utils.*;

public class Exploration {
    private int timeLimit;
    private int coverageLimit;
    private Robot robot;
    private long startTime;
    private long endTime;
    private Map map;
    private int areaExplored;

    public Exploration(int timeLimit, int coverageLimit, Robot robot, Map map) {
        this.timeLimit = timeLimit;
        this.coverageLimit = coverageLimit;
        this.robot = robot;
        this.map = map;
    }

    public void run() {
        if (robot.getRealRun()) {
            System.out.println("Calibrating...");

            CommMgr.getCommMgr().recvMsg();
            
            robot.move(MOVEMENT.LEFT, false);
            CommMgr.getCommMgr().recvMsg();
            robot.move(MOVEMENT.CALIBRATE, false);
            CommMgr.getCommMgr().recvMsg();
            robot.move(MOVEMENT.LEFT, false);
            CommMgr.getCommMgr().recvMsg();
            robot.move(MOVEMENT.CALIBRATE, false);
            CommMgr.getCommMgr().recvMsg();
            robot.move(MOVEMENT.RIGHT, false);
            CommMgr.getCommMgr().recvMsg();
            robot.move(MOVEMENT.CALIBRATE, false);
            CommMgr.getCommMgr().recvMsg();
            robot.move(MOVEMENT.RIGHT, false);
            

            while (true) {
                System.out.println("Waiting for Start Command");
                String msg = CommMgr.getCommMgr().recvMsg();
                String[] msgArr = msg.split(";");
                if (msgArr[0].equals(CommMgr.EX_START)){
                    break;
                }
            }
        }

        System.out.println("Starting exploration...");
        startTime = System.currentTimeMillis();
        endTime = startTime + (timeLimit * 1000);


        if (robot.getRealRun()) {
            CommMgr.getCommMgr().sendMsg(null, CommMgr.BOT_START);
        }

        sense();

        areaExplored = getAreaExplored();
        System.out.println("Explored Area: " + areaExplored);

        explore(robot.getRow(), robot.getCol());
    }

    public void sense() {
        robot.setSensors();
        robot.sense(map);
        map.paintComponent(map.getGraphics());
    }


    //Right Wall Following Algorithm
    public void nextStep() {
        if (checkRight()) {
            move(MOVEMENT.RIGHT);
            System.out.println("Right empty. Rotate right");
            if (checkFront()) {
                move(MOVEMENT.FORWARD);
                System.out.println("Forward empty after right rotation. Move forward");
            }
        } else if (checkFront()) {
            move(MOVEMENT.FORWARD);
            System.out.println("Forward empty. Move forward");
        } else if (checkLeft()) {
            move(MOVEMENT.LEFT);
            System.out.println("Left empty. Rotate left");
            if (checkFront()) {
                move(MOVEMENT.FORWARD);
                System.out.println("Forward empty after left rotation. Move forward");
            }
        } else {
            move(MOVEMENT.RIGHT);
            move(MOVEMENT.RIGHT);
            System.out.println("Deadend. U-turn");
        }
    }

    public void explore(int startR, int startC) {
        while (System.currentTimeMillis() <= endTime && areaExplored <= coverageLimit) {
            nextStep();
            areaExplored = getAreaExplored();
            System.out.println("Area explored: " + areaExplored);

            if (robot.getRow() == startR && robot.getCol() == startC){
                break;
            }

        }
        returnHome();
    }    

    private boolean isExploredAndNotObstacle(int r, int c) {
        if (map.isValid(r, c)) {
            Tile t = map.getTile(r, c);
            return (t.getExplored() && (t.getState() == 0 || t.getState() == 2 || t.getState() == 3));
        }
        return false;
    }

    private boolean isExploredAndFree(int r, int c) {
        if (map.isValid(r, c)) {
            Tile t = map.getTile(r, c);
            return (t.getExplored() && (t.getState() == 0 || t.getState() == 2 || t.getState() == 3) && !t.getVirtual());
        }
        return false;
    }

    private void move(MOVEMENT m) {
        robot.move(m, false);
        map.paintComponent(map.getGraphics());
        if (m != MOVEMENT.CALIBRATE) {
            sense();
        } else {
            CommMgr commMgr = CommMgr.getCommMgr();
            commMgr.recvMsg();
        }

        /*
        if (robot.getRealRun() && !calibrationMode) {
            calibrationMode = true;

            if (canCalibrateOnTheSpot(bot.getRobotCurDir())) {
                lastCalibrate = 0;
                moveBot(MOVEMENT.CALIBRATE);
            } else {
                lastCalibrate++;
                if (lastCalibrate >= 5) {
                    DIRECTION targetDir = getCalibrationDirection();
                    if (targetDir != null) {
                        lastCalibrate = 0;
                        calibrateBot(targetDir);
                    }
                }
            }

            calibrationMode = false;
        }*/
    }

    private int getAreaExplored() {
        int result = 0;
        for (int r = 0; r < map.getRowSize(); r++) {
            for (int c = 0; c < map.getColSize(); c++) {
                if (map.getTile(r, c).getExplored()) {
                    result++;
                }
            }
        }
        return result;
    }

    private void returnHome() {
        /*
        if (!robot.getReachedGoal() && coverageLimit == 300 && timeLimit == 3600) {
            FastestPathAlgo goToGoal = new FastestPathAlgo(exploredMap, bot, realMap);
            goToGoal.runFastestPath(Constant.GOAL_ROW, Constant.GOAL_COL);
        }

        FastestPathAlgo returnToStart = new FastestPathAlgo(exploredMap, bot, realMap);
        returnToStart.runFastestPath(Constant.START_ROW, Constant.START_COL);

        System.out.println("Exploration complete!");
        areaExplored = calculateAreaExplored();
        System.out.printf("%.2f%% Coverage", (areaExplored / 300.0) * 100.0);
        System.out.println(", " + areaExplored + " Cells");
        System.out.println((System.currentTimeMillis() - startTime) / 1000 + " Seconds");

        if (bot.getRealRun()) {
            turnBotDirection(DIRECTION.WEST);
            moveBot(MOVEMENT.CALIBRATE);
            turnBotDirection(DIRECTION.SOUTH);
            moveBot(MOVEMENT.CALIBRATE);
            turnBotDirection(DIRECTION.WEST);
            moveBot(MOVEMENT.CALIBRATE);
        }
        turnBotDirection(DIRECTION.NORTH);
        */
    }

    private boolean checkRight() {
        switch (robot.getDir()) {
            case NORTH:
                return clearEast();
            case EAST:
                return clearSouth();
            case SOUTH:
                return clearWest();
            case WEST:
                return clearNorth();
        }
        return false;
    }

    private boolean checkFront() {
        switch (robot.getDir()) {
            case NORTH:
                return clearNorth();
            case EAST:
                return clearEast();
            case SOUTH:
                return clearSouth();
            case WEST:
                return clearWest();
        }
        return false;
    }

    private boolean checkLeft() {
        switch (robot.getDir()) {
            case NORTH:
                return clearWest();
            case EAST:
                return clearNorth();
            case SOUTH:
                return clearEast();
            case WEST:
                return clearSouth();
        }
        return false;
    }

    private boolean clearNorth() {
        int row = robot.getRow();
        int col = robot.getCol();
        return (isExploredAndNotObstacle(row + 1, col - 1) && isExploredAndFree(row + 1, col) && isExploredAndNotObstacle(row + 1, col + 1));
    }

    private boolean clearEast() {
        int row = robot.getRow();
        int col = robot.getCol();
        return (isExploredAndNotObstacle(row - 1, col + 1) && isExploredAndFree(row, col + 1) && isExploredAndNotObstacle(row + 1, col + 1));
    }

    private boolean clearSouth() {
        int row = robot.getRow();
        int col = robot.getCol();
        return (isExploredAndNotObstacle(row - 1, col - 1) && isExploredAndFree(row - 1, col) && isExploredAndNotObstacle(row - 1, col + 1));
    }

    private boolean clearWest() {
        int row = robot.getRow();
        int col = robot.getCol();
        return (isExploredAndNotObstacle(row - 1, col - 1) && isExploredAndFree(row, col - 1) && isExploredAndNotObstacle(row + 1, col - 1));
    }

}