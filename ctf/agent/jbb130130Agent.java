package ctf.agent;

import ctf.common.AgentEnvironment;
import ctf.agent.Agent;
import java.util.*;
import java.util.Hashtable;

import ctf.common.AgentAction;
//this is the jobs you can have
enum Job {MAPPING, RANDOM_MOVES, FIND_MAP_SIZE, GUARDING, ATTACKING, TOWARDSGOAL, DEFENDWITHBOMBS}
enum Label {NORTH, SOUTH}

//Jeffrey Jennifer Agent
public class jbb130130Agent extends Agent {
    
    static Board board = new Board();
    int moveCounter = 0;
    //which one is which
    Label id;
    //this instaniazies it 
    boolean firstMove = true;
    ArrayList<Job> currentJobs = new ArrayList<Job>();
    LinkedList<Integer> history = new LinkedList<Integer>(); 
    int counter = 0;
    int xDisplacement = 0;
    int yDisplacement = 0;
    int[] path;
    int left,west = AgentAction.MOVE_WEST; //3
    int right,east = AgentAction.MOVE_EAST; //2
    int up, north = AgentAction.MOVE_NORTH; //0
    int down, south = AgentAction.MOVE_SOUTH; //1
    int bomb = AgentAction.PLANT_HYPERDEADLY_PROXIMITY_MINE; //379037
    int nothing = AgentAction.DO_NOTHING; //-1
    int step = -1;
    boolean bombLastMove = false; 
    int move;
    int ours = AgentEnvironment.OUR_TEAM;
    int enemy = AgentEnvironment.ENEMY_TEAM;
    boolean eastBase = false; //our base
    int lastPosX = -1;
    int lastPosY = -1;
    int secondToLastX = -1;
    int secondToLastY = -1;
    int thirdToLastX = -1;
    int thirdToLastY = -1;
    LinkedList<Integer> previousX = new LinkedList<Integer>();
    LinkedList<Integer> previousY = new LinkedList<Integer>();

    //top one gets size
    //both are constantly mapping (updates the map) 
    //
    public int getMove(AgentEnvironment inEnvironment) {
        moveCounter++;
        System.out.println(moveCounter);
        if (firstMove) {
            currentJobs.add(Job.MAPPING);
            if (inEnvironment.isBaseSouth(ours, false)) {
                id = Label.NORTH;
                yDisplacement = 10;
                currentJobs.add(Job.DEFENDWITHBOMBS);
                board = new Board();
            }
            else{
                id = Label.SOUTH;
                yDisplacement = 1;
                currentJobs.add(Job.TOWARDSGOAL);
            }
            if(inEnvironment.isBaseEast(enemy, false)){
                xDisplacement = 1;
            }
            else{
                xDisplacement = 10;
                eastBase = true;
            }
            firstMove = false;
            //System.out.println(inEnvironment.isObstacleNorthImmediate());
            //System.out.println(inEnvironment.isObstacleSouthImmediate());
            //System.out.println(id);
        }
        if(history.isEmpty()){
            history.addLast(-2);
        }

        boolean obstNorth = inEnvironment.isObstacleNorthImmediate() || board.isDeadEnd(xDisplacement, yDisplacement + 1);// && !(history.getLast() == 1);
        boolean obstSouth = inEnvironment.isObstacleSouthImmediate() || board.isDeadEnd(xDisplacement, yDisplacement - 1);// && !(history.getLast() == 0);
        boolean obstEast = inEnvironment.isObstacleEastImmediate() || board.isDeadEnd(xDisplacement + 1, yDisplacement);// && !(history.getLast() == 3);
        boolean obstWest = inEnvironment.isObstacleWestImmediate() || board.isDeadEnd(xDisplacement - 1, yDisplacement);// && !(history.getLast() == 2);
        //south east
        if(id == Label.SOUTH){
            if(inEnvironment.isBaseNorth(ours, false) && !inEnvironment.isBaseWest(ours, false) && !inEnvironment.isBaseEast(ours, false) && inEnvironment.isObstacleSouthImmediate()){
                yDisplacement = 1;
                if(inEnvironment.isBaseEast(enemy, false)){
                    xDisplacement = 1;
                }
                else{
                    xDisplacement = 10;
                    eastBase = true;
                }
            }
        }   
        else{
            if(inEnvironment.isBaseSouth(ours, false) && !inEnvironment.isBaseWest(ours, false) && !inEnvironment.isBaseEast(ours, false) && inEnvironment.isObstacleNorthImmediate()){
                yDisplacement = 10;
                if(inEnvironment.isBaseEast(enemy, false)){
                    xDisplacement = 1;
                }
                else{
                    xDisplacement = 10;
                    eastBase = true;
                }
            }
        }
        //Non Final Jobs
        if (currentJobs.contains(Job.MAPPING)) {
           //System.out.println(xDisplacement);
          // System.out.println(yDisplacement);
           board.updateMap(xDisplacement, yDisplacement, inEnvironment);
        }

        boolean[] obstacles = new boolean[] {obstNorth, obstSouth, obstEast, obstWest};
        int openings = 4;
        for (int i = 0; i < 4; i++){
            if (obstacles[i])
                openings--;
        }
        if (openings == 1) {
            System.out.println("DEADEND");
            board.setDeadEnd(xDisplacement,yDisplacement);
        }

        //Final Jobs  
        if(currentJobs.contains(Job.DEFENDWITHBOMBS)){
            move = defendWithBombs(inEnvironment, obstNorth, obstSouth, obstEast, obstWest);
        }
        if(currentJobs.contains(Job.TOWARDSGOAL)) {
            move = towardsGoal(inEnvironment, obstNorth, obstSouth, obstEast, obstWest); 
        }
        if(currentJobs.contains(Job.RANDOM_MOVES)){
            move = random(inEnvironment);
        }

        if(openings == 1){
            if(!obstNorth)
                move = north;
            else if(!obstSouth)
                move = south;
            else if(!obstWest)
                move = west;
            else if(!obstEast)
                move = east;
        }

        // if(inEnvironment.hasFlag() && history.size() > 0 && history.getLast() != -2){
        //     move = history.pollLast();
        //     switch (move){
        //         case 0:
        //             move = 1;
        //             break;
        //         case 2:
        //             move = 3;
        //             break;
        //         case 3:
        //             move = 2;
        //             break;
        //         case 1:
        //             move = 0;
        //             break;
        //         case 379037:
        //             break;
        //         case -1:
        //             break;
        //         default: 
        //             break;
        //         }    
        // }
        // thirdToLastX = secondToLastX;
        // thirdToLastY = secondToLastY;
        // secondToLastY = lastPosY;
        // secondToLastX = lastPosX;
        // lastPosX = xDisplacement;
        // lastPosY = yDisplacement;
        // System.out.println("" + lastPosX + lastPosY + secondToLastX + secondToLastY + thirdToLastX + thirdToLastY);
        // System.out.println("" + (xDisplacement == secondToLastX) + (yDisplacement == secondToLastY) + (lastPosX == thirdToLastX) + (lastPosY == thirdToLastY));
        // System.out.println("" + yDisplacement + secondToLastY);
        // if(xDisplacement == secondToLastX && yDisplacement == secondToLastY && lastPosX == thirdToLastX && lastPosY == thirdToLastY){
        //     System.out.println("Deadend x" + lastPosX + "deadend y" + lastPosY);
        //     board.setDeadEnd(lastPosX, lastPosY);
        // }
        previousX.addFirst(xDisplacement);
        previousY.addFirst(yDisplacement);
        Object[] preXArray = previousX.toArray();
        Object[] preYArray = previousY.toArray();

        if(preXArray.length > 6 && preYArray.length > 6 && preXArray[0] == preXArray[2] && preYArray[0] == preYArray[2] && preXArray[1] == preXArray[3] && preYArray[1] == preYArray[3]){
            board.setDeadEnd((Integer)preXArray[1], (Integer)preYArray[1]);
        }
        switch (move){
            case 0:
                yDisplacement++;
                break;
            case 2:
                xDisplacement++;
                break;
            case 3:
                xDisplacement--;
                break;
            case 1:
                yDisplacement--;
                break;
            case 379037:
                board.placeBomb(xDisplacement, yDisplacement);
                break;
            case -1:
                break;
            default: 
                break;
            }

        // if ((history.size() > 6) && (history.getLast() == history.get(history.size() - 3)) &&  (history.get(history.size()-2) == history.get(history.size() - 4)) && (history.getLast() != history.get(history.size()-2))){
        //     int moveSecondToLast = history.get(history.size() - 2);
        //     int moveLast = history.getLast();
        //     int deadEndSecondX = xDisplacement;
        //     int deadEndSecondY = yDisplacement;
        //     int deadEndLastX = xDisplacement;
        //     int deadEndLastY = yDisplacement;
        //     switch(moveSecondToLast){
        //         case 0: deadEndSecondY --;
        //         break;
        //         case 1: deadEndSecondY++;
        //         break;
        //         case 2: deadEndSecondX--;
        //         break;
        //         case 3: deadEndSecondX++;
        //         default: 
        //         break;
        //     }
        //     switch(moveLast){
        //         case 0: deadEndLastY --;
        //         break;
        //         case 1: deadEndLastY++;
        //         break;
        //         case 2: deadEndLastX--;
        //         break;
        //         case 3: deadEndLastX++;
        //         default: 
        //         break;
        //     }

        //     board.setDeadEnd(deadEndSecondX, deadEndSecondY);
        // }
        if(move == bomb)
            bombLastMove = true;
        else 
            bombLastMove = false;
        if(move != bomb){
            history.addLast(move);
        }
        System.out.println("id" + id + "m" + move + "x" + xDisplacement + "y" + yDisplacement);
        System.out.println(Arrays.toString(history.toArray()));
        if(move == oppositeMove(history.getLast()) && move != -1 && !inEnvironment.hasFlag()){
            board.setDeadEnd(xDisplacement, yDisplacement);
        }
        return move;
        
    //    step++;
    //    return path[step];

    }

    //if tbe map is unknown, just go towards the goal and add to the map
    public int towardsGoal(AgentEnvironment inEnvironment, boolean obstNorth, boolean obstSouth, boolean obstEast, boolean obstWest){
        // booleans describing direction of goal
        // goal is either enemy flag, or our base

        if(inEnvironment.isAgentNorth(enemy, true))
            obstNorth = true;
        if(inEnvironment.isAgentSouth(enemy, true))
            obstSouth = true;
        if(inEnvironment.isAgentEast(enemy, true))
            obstEast = true;
        if(inEnvironment.isAgentWest(enemy, true))
            obstWest = true;

        boolean goalNorth;
        boolean goalSouth;
        boolean goalEast;
        boolean goalWest;

        
        if( !inEnvironment.hasFlag() ) {
            // make goal the enemy flag
            goalNorth = inEnvironment.isFlagNorth( 
                enemy, false );
        
            goalSouth = inEnvironment.isFlagSouth( 
                enemy, false );
        
            goalEast = inEnvironment.isFlagEast( 
                enemy, false );
        
            goalWest = inEnvironment.isFlagWest( 
                enemy, false );
            }
        else {
            // we have enemy flag.
            // make goal our base
            goalNorth = inEnvironment.isBaseNorth( 
                ours, false );
        
            goalSouth = inEnvironment.isBaseSouth( 
                ours, false );
        
            goalEast = inEnvironment.isBaseEast( 
                ours, false );
        
            goalWest = inEnvironment.isBaseWest( 
                ours, false );
            }
        
        // if goal both north and east
        if( goalNorth && goalEast ) {
            
            if(inEnvironment.hasFlag() && !obstEast){
                return east;
            }
            else if(inEnvironment.hasFlag() && obstEast && !obstSouth){
                return south;
            }
            else if( !obstEast ) {   
                return AgentAction.MOVE_EAST;
                }
            else if( !obstNorth ) {
                return AgentAction.MOVE_NORTH;
                }
            else if(!obstWest){
                return AgentAction.MOVE_WEST;
                }
            else if(!obstSouth){
                return AgentAction.MOVE_SOUTH;
            }
        }
            
        // if goal both north and west  
        if( goalNorth && goalWest ) {
            if(inEnvironment.hasFlag() && !obstWest){
                return west;
            }
            else if(inEnvironment.hasFlag() && obstWest && !obstSouth){
                return south;
            }
            else if( !obstWest ) {   
                return AgentAction.MOVE_WEST;
                }
            else if( !obstNorth ) {  
                return AgentAction.MOVE_NORTH;
                } 
            else if(!obstEast){
                return AgentAction.MOVE_EAST;
                }
            else if(!obstSouth){
                return AgentAction.MOVE_SOUTH;
                }
        }
        
        // do same for southeast and southwest as for north versions    
        if( goalSouth && goalEast ) {
            if( !obstEast ) {
                return AgentAction.MOVE_EAST;
                }
            else if( !obstSouth ) {
                return AgentAction.MOVE_SOUTH;
                }
            else if(!obstWest){
                return AgentAction.MOVE_WEST;
                }
            else if(!obstNorth){
                return AgentAction.MOVE_NORTH;
                }
        }
                
        if( goalSouth && goalWest) {
            if( !obstWest ) {
                return AgentAction.MOVE_WEST;
                }
            else if( !obstSouth ) {
                return AgentAction.MOVE_SOUTH;
                }
            else if(!obstEast){
                return AgentAction.MOVE_EAST;
                }
            else if(!obstNorth){
                return AgentAction.MOVE_NORTH;
                }
        }

                    // if the goal is north only, and we're not blocked
        if( goalNorth) {
            if(inEnvironment.hasFlag() && !obstNorth){
                return north;
            }
            else if(!obstNorth){
                return AgentAction.MOVE_NORTH;
                }
            else if(!obstEast){
                return AgentAction.MOVE_EAST;
                }
            else if(!obstWest){
                return AgentAction.MOVE_WEST;
                }
            else if(!obstSouth){
                return AgentAction.MOVE_SOUTH;   
                }
        }


        // if the goal is east only, and we're not blocked
        if( goalEast) {
            if(inEnvironment.hasFlag() && !obstSouth){
                return south;        
            }
            else if( !obstEast ) {   
                return AgentAction.MOVE_EAST;
                }
            else if( !obstNorth ) {
                return AgentAction.MOVE_NORTH;
                }
            else if(!obstSouth){
                return AgentAction.MOVE_SOUTH;
                }
            else if(!obstWest){
                return AgentAction.MOVE_WEST;
                }
        }
        
        // if the goal is west only, and we're not blocked  
        if( goalWest) {
            if(inEnvironment.hasFlag() && !obstSouth){
                return south;
            }    
            else if( !obstWest ) {
                return AgentAction.MOVE_WEST;
                }
            else if(!obstNorth ) {
                return AgentAction.MOVE_NORTH;
                }
            else if(!obstSouth){
                return AgentAction.MOVE_SOUTH;
                }
            else if(!obstEast){
                return AgentAction.MOVE_EAST;
                }
        }   

        // if the goal is south only, and we're not blocked
        if( goalSouth) {
            // move south
            if(inEnvironment.hasFlag() && !obstSouth){
                return south;
            }
            else if(!obstSouth){
                return AgentAction.MOVE_SOUTH;
                }
            else if(!obstEast){
                return AgentAction.MOVE_EAST;
                }
            else if(!obstWest){
                return AgentAction.MOVE_WEST;
                }
            else if(!obstNorth){
                return AgentAction.MOVE_NORTH;   
            }
        }
        return AgentAction.DO_NOTHING;
    }

    public int random(AgentEnvironment inEnvironment){
        double rand = Math.random();
        
        if( rand < 0.25 ) {
            return AgentAction.MOVE_NORTH;
            }
        else if( rand < 0.5 ) {
            return AgentAction.MOVE_SOUTH;
            }
        else if( rand < 0.75 ) {
            return AgentAction.MOVE_EAST;
            }
        else {
            return AgentAction.MOVE_WEST;
            }   
    }

    public int defendWithBombs(AgentEnvironment inEnvironment, boolean obstNorth, boolean obstSouth, boolean obstEast, boolean obstWest){
        // if(id == Label.NORTH) {   
        //     if(!bombLastMove){
        //         return bomb;
        //     }
        //     else if (inEnvironment.isBaseSouth(ours,true)){
        //         if(inEnvironment.isBaseEast(enemy, false) && !obstEast){
        //             return east;
        //         }
        //         else if(!obstWest)
        //             return west;
        //     }
        //     else if(inEnvironment.isBaseNorth(ours, true)){
        //         if(inEnvironment.isBaseEast(enemy, false && !obstEast)){
        //             return east;
        //         }
        //         else if(!obstWest)
        //             return west;
        //     }
        //     else if(inEnvironment.isBaseEast(ours, true)){
        //         if(!obstSouth && id == Label.NORTH)
        //             return south;
        //         else if(!obstNorth && id == Label.SOUTH)
        //             return north;
        //     }
        //     else if(inEnvironment.isBaseWest(ours, true)){
        //         if(!obstSouth && id == Label.NORTH)
        //             return south;
        //         else if(!obstNorth && id == Label.SOUTH)
        //             return north;
        //     }
        //     else if(inEnvironment.isBaseSouth(ours, false)){
        //         if(!obstSouth)
        //                 return south;
        //         else if(inEnvironment.isBaseEast(ours, false) && !obstEast){
        //             return east;
        //         }
        //         else if(inEnvironment.isBaseWest(ours, false) && !obstWest){
        //             return west;
        //         }
        //     }
        //     else if(inEnvironment.isBaseNorth(ours, false)){
        //         if(!obstNorth)
        //                 return north;
        //         else if(inEnvironment.isBaseEast(ours, false) && !obstEast){
        //                 return east;
        //         }
        //         else if(inEnvironment.isBaseWest(ours, false) && !obstWest){
        //                 return west;
        //         }
        //     }
        // }
        // else if(id == Label.SOUTH) {
        

        if(bombLastMove){
            if (eastBase && !obstWest)
                return west;
            else if(!obstEast) 
                return east;
        }
        else if(inEnvironment.isBaseEast(ours, true)){
            if(!obstSouth && id == Label.NORTH)
                return bomb;
                //return south;
            else if(!obstNorth && id == Label.SOUTH)
                return bomb;
                //return north;
        }
        else if(inEnvironment.isBaseWest(ours, true)){
            if(!obstSouth && id == Label.NORTH)
                return bomb;
                //return south;
            else if(!obstNorth && id == Label.SOUTH)
                return bomb;
                //return north;
        }
        else if(!inEnvironment.isBaseNorth(ours,false) && !inEnvironment.isBaseSouth(ours, false) &&(inEnvironment.isBaseEast(ours, false) || inEnvironment.isBaseWest(ours, false))){
            return bomb;
        }
        else if(id == Label.SOUTH && obstNorth){
            if(eastBase && obstEast && !obstWest){
                return west;
            }
            else if(!eastBase && !obstEast)
                return east;

        }
        else if(id == Label.NORTH && obstSouth){
            if(eastBase && obstEast && !obstWest){
                return west;
            }
            else 
                return east;
        }
        else if (inEnvironment.isBaseSouth(ours,true)){
            if(inEnvironment.isBaseEast(enemy, false) && !obstEast){
                return east;
            }
            else if(!obstWest)
                return west;
        }
        else if(inEnvironment.isBaseNorth(ours, true)){
            if(inEnvironment.isBaseEast(enemy, false && !obstEast)){
                return east;
            }
            else if(!obstWest)
                return west;
        }
        else if(inEnvironment.isBaseSouth(ours, false)){
            if(!obstSouth && id == Label.NORTH){
                return south;
            }
            else if(!obstSouth && id == Label.SOUTH){
                return north;
            }
            else if(inEnvironment.isBaseEast(ours, false) && !obstEast && id == Label.SOUTH){
                return east;
            }
            else if(inEnvironment.isBaseEast(ours, false) && !obstEast && id == Label.NORTH){
                return south;
            }
            else if(inEnvironment.isBaseWest(ours, false) && !obstWest && id == Label.SOUTH){
                return west;
            }
            else if(inEnvironment.isBaseWest(ours, false) && !obstWest && id == Label.NORTH){
                return south;
            }
        }
        // else if(inEnvironment.isBaseNorth(ours, false)){
        //     if(!obstNorth && id == Label.SOUTH){
        //         return north;
        //     }
        //     else if(!obstNorth && id == Label.NORTH){
        //         return south;
        //     }
        //     else if(inEnvironment.isBaseEast(ours, false) && !obstEast && id == Label.NORTH){
        //         return east;
        //     }
        //     else if(inEnvironment.isBaseEast(ours, false) && !obstEast && id == Label.SOUTH){
        //         return south;
        //     }
        //     else if(inEnvironment.isBaseWest(ours, false) && !obstWest && id == Label.NORTH){
        //         return west;
        //     }
        //     else if(inEnvironment.isBaseWest(ours, false) && !obstWest && id == Label.SOUTH){
        //         return south;
        //     }
        // }
        return nothing;
    }

    public int oppositeMove(int move){
        if(move == 0)
            return 1;
        if(move == 1)
            return 0;
        if (move == 2)
            return 3;
        if (move == 3)
            return 2;
        else 
            return -1;
    }
    
    //private static class TreeNode<T> {
    //    private T data;
    //    private TreeNode<T> parent;
    //    private List<TreeNode<T>> children;

    //    TreeNode(T data,TreeNode<T> parent) {
    //        this.data = data;
    //        this.parent = parent;
    //        parent.children.add(this);
    //        children = new LinkedList<TreeNode<T>>();
    //    }
    //}

    //TreeNode<AgentAction> current = new TreeNode<AgentAction>(null,null);
    
    //int JDefend (AgentEnvironment env) {

       //return 0; 
    //}
}

class Board {

    private class Coordinate {
        int x;
        int y;
        Coordinate(int _x, int _y) {
            x = _x;
            y = _y;
        }
        @Override
        public boolean equals(Object o) {
            if (x == ((Coordinate)o).x && y == ((Coordinate)o).y)
                return true;
            else
                return false;
        }
        @Override
        public int hashCode() {
            return (int)Math.pow(13,7) + x + 1000*y;
        }
        public String toString() {
            return "" + x + " " + y;
        }
    }

    private class BoardTile {
        Hashtable<String,Boolean> flags;

        BoardTile(Hashtable<String, Boolean> options) {
            flags = new Hashtable<String, Boolean>();
            update(options);
        }

        BoardTile() {
            flags = new Hashtable<String,Boolean>();
        }

        void update(Hashtable<String,Boolean> options) {
            if (options.size() > 0)
                for (Enumeration<String> e = options.keys(); e.hasMoreElements();) {
                    Object temp = e.nextElement();
                    flags.put((String)temp,options.get((String)temp));
                }
        }   
        boolean deadEnd () {
            if (flags.containsKey("deadEnd"))
                if (flags.get("deadEnd"))
                    return true;
            return false;
        }

        boolean flagRow () {
            if (flags.containsKey("baseNorth") && flags.get("baseNorth"))
                return false;
            else if (flags.containsKey("baseNorth") && !flags.get("baseNorth"))
                return true;
            if (flags.containsKey("baseSouth") && flags.get("baseSouth"))
                return false;
            else if (flags.containsKey("baseSouth") && !flags.get("baseSouth"))
                return true;
            return false;
        }
    }

    Hashtable<Coordinate,BoardTile> board;
    int size = -1;
    Boolean complete = false;
    Coordinate test;

    Board() {
        board = new Hashtable<Coordinate,BoardTile>();
        addBoardTile(1,10);
        addBoardTile(10,10);
        addBoardTile(1,1);
        addBoardTile(10,1);
    }

    void addBoardTile(int x, int y, Hashtable<String, Boolean> hash) {
        board.put(new Coordinate(x,y),new BoardTile(hash));
    }

    void addBoardTile(int x, int y, BoardTile tile) {
        board.put(new Coordinate(x,y),tile);
    }

    void addBoardTile(int x, int y) {
        board.put(new Coordinate(x,y),new BoardTile());
    }

    void setDeadEnd(int x, int y) {
        Hashtable<String,Boolean> tmp = new Hashtable<String,Boolean>();
        tmp.put("deadEnd",true);
        board.get(new Coordinate(x,y)).update(tmp);
    }

    boolean isDeadEnd(int x, int y) {
        if (board.get(new Coordinate(x,y)) == null)
            return false;
        return board.get(new Coordinate(x,y)).deadEnd();
    }

    int[] pathToFlag(int x, int y) {
       return new int[] {}; 
    }

    void updateMap(int x, int y, AgentEnvironment env) {
        //System.out.println(" " + x +" " + y);
        int counter = 0;
        //for (Enumeration<Coordinate> e = board.keys(); e.hasMoreElements();)
        //    System.out.println((counter++) + ":" + e.nextElement());
        BoardTile current = board.get(new Coordinate(x,y));
        //Check update current tile
        Hashtable<String,Boolean> upd = new Hashtable<String,Boolean>();
        if (env.isBaseNorth(env.OUR_TEAM,false))
            upd.put("baseNorth",true);
        else
            upd.put("baseNorth", false);
        if (env.isBaseSouth(env.OUR_TEAM,false))
            upd.put("baseSouth",true);
        else
            upd.put("baseSouth",false);
        current.update(upd);
        //Add surrounding tiles
        upd.clear();
        //North
        if (board.get(new Coordinate(x,y+1)) == null)
            addBoardTile(x,y+1);
        if (env.isObstacleNorthImmediate())
            upd.put("blocked",true);
        board.get(new Coordinate(x,y+1)).update(upd);
        upd.clear();
        //South
        if (board.get(new Coordinate(x,y-1)) == null)
            addBoardTile(x,y-1);
        if (env.isObstacleSouthImmediate())
            upd.put("blocked",true);
        board.get(new Coordinate(x,y-1)).update(upd);
        upd.clear();
        //East
        if (board.get(new Coordinate(x+1,y)) == null)
            addBoardTile(x+1,y);
        if (env.isObstacleEastImmediate())
            upd.put("blocked",true);
        board.get(new Coordinate(x+1,y)).update(upd);
        upd.clear();
        //West
        if (board.get(new Coordinate(x-1,y)) == null)
            addBoardTile(x-1,y);
        if (env.isObstacleWestImmediate())
            upd.put("blocked",true);
        board.get(new Coordinate(x-1,y)).update(upd);
        upd.clear();
        if (size == -1)
            normalize();
    }

    void placeBomb(int x, int y) {
        
    }

    void match() {
    
    }

    void normalize() {
        boolean PFR = false;
        Coordinate pCoord = null;
        Coordinate nCoord = null;
        boolean NFR = false;
        for (Enumeration<Coordinate> e = board.keys(); e.hasMoreElements();) {
            Coordinate temp = e.nextElement();
            if (temp.y > 0 && board.get(temp).flagRow()) {
                PFR = true;
                pCoord = temp;
            }
            if (temp.y < 0 && board.get(temp).flagRow()) {
                NFR = true;
                nCoord = temp;
            }
        }
        if (PFR && NFR) {
            size = pCoord.y-1+(-(nCoord.y)+1)+1;
            System.out.println(size + "\n\n\n\n\n\n\n\n\n\n");
        }
    }

    void completed() {complete = true;}
    boolean isComplete() {return complete;}

}

