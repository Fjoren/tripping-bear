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
public class JJAgent extends Agent {
    
    static Board board = new Board();
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
    //top one gets size
    //both are constantly mapping (updates the map) 
    //
    public int getMove(AgentEnvironment inEnvironment) {
        if (firstMove) {
            currentJobs.add(Job.MAPPING);
            if (inEnvironment.isBaseSouth(ours, false)) {
                id = Label.NORTH;
                yDisplacement = -2;
                currentJobs.add(Job.DEFENDWITHBOMBS);
            }
            else{
                id = Label.SOUTH;
                yDisplacement = 2;
                currentJobs.add(Job.TOWARDSGOAL);
            }
            if(inEnvironment.isBaseEast(enemy, false)){
                xDisplacement = 2;
            }
            else{
                xDisplacement = -2;
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

        boolean obstNorth = inEnvironment.isObstacleNorthImmediate() || board.isDeadEnd(yDisplacement + 1, xDisplacement);// && !(history.getLast() == 1);
        boolean obstSouth = inEnvironment.isObstacleSouthImmediate() || board.isDeadEnd(yDisplacement - 1, xDisplacement);// && !(history.getLast() == 0);
        boolean obstEast = inEnvironment.isObstacleEastImmediate() || board.isDeadEnd(yDisplacement, xDisplacement + 1);// && !(history.getLast() == 3);
        boolean obstWest = inEnvironment.isObstacleWestImmediate() || board.isDeadEnd(yDisplacement, xDisplacement - 1);// && !(history.getLast() == 2);
        //south east
        if(id == Label.SOUTH){
            if(inEnvironment.isBaseNorth(ours, false) && !inEnvironment.isBaseWest(ours, false) && !inEnvironment.isBaseEast(ours, false) && inEnvironment.isObstacleSouthImmediate()){
                yDisplacement = 2;
                if(inEnvironment.isBaseEast(enemy, false)){
                    xDisplacement = 2;
                }
                else{
                    xDisplacement = -2;
                    eastBase = true;
                }
            }
        }   
        else{
            if(inEnvironment.isBaseSouth(ours, false) && !inEnvironment.isBaseWest(ours, false) && !inEnvironment.isBaseEast(ours, false) && inEnvironment.isObstacleNorthImmediate()){
                yDisplacement = -2;
                if(inEnvironment.isBaseEast(enemy, false)){
                    xDisplacement = 2;
                }
                else{
                    xDisplacement = -2;
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

        if(move == bomb)
            bombLastMove = true;
        else 
            bombLastMove = false;
        if(!inEnvironment.hasFlag() && move != bomb){
            history.addLast(move);
        }
        System.out.println("id" + id + "m" + move + "x" + xDisplacement + "y" + yDisplacement);
        System.out.println(Arrays.toString(history.toArray()));
        if(move == oppositeMove(history.getLast()) && move != -1){
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
            
            if( !obstEast ) {   
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
            else{
                return bomb;    
            }
        }
            
        // if goal both north and west  
        if( goalNorth && goalWest ) {
            if( !obstWest ) {   
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
            else{
                return bomb;    
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
            else{
                return bomb;    
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
            else{
                return bomb;    
            }
        }

                    // if the goal is north only, and we're not blocked
        if( goalNorth) {
            if(!obstNorth){
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
            else{
                return bomb;    
            }
        }


        // if the goal is east only, and we're not blocked
        if( goalEast) {
            if( !obstEast ) {   
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
            else{
                return bomb;    
            }
        }
        
        // if the goal is west only, and we're not blocked  
        if( goalWest) {
            if( !obstWest ) {
                return AgentAction.MOVE_WEST;
                }
            else if(!obstSouth ) {
                return AgentAction.MOVE_SOUTH;
                }
            else if(!obstNorth){
                return AgentAction.MOVE_NORTH;
                }
            else if(!obstEast){
                return AgentAction.MOVE_EAST;
                }
            else{
                return bomb;    
            }
        }   

        // if the goal is south only, and we're not blocked
        if( goalSouth) {
            // move south
            if(!obstSouth){
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
            else{
                return bomb;    
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
        if(id == Label.SOUTH && obstNorth){
            if(eastBase && obstEast && !obstWest){
                return west;
            }
            else 
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
        else if(inEnvironment.isBaseEast(ours, true)){
            if(!obstSouth && id == Label.NORTH)
                return nothing;
                //return south;
            else if(!obstNorth && id == Label.SOUTH)
                return nothing;
                //return north;
        }
        else if(inEnvironment.isBaseWest(ours, true)){
            if(!obstSouth && id == Label.NORTH)
                return nothing;
                //return south;
            else if(!obstNorth && id == Label.SOUTH)
                return nothing;
                //return north;
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
        else if(inEnvironment.isBaseNorth(ours, false)){
            if(!obstNorth && id == Label.SOUTH){
                return north;
            }
            else if(!obstNorth && id == Label.NORTH){
                return south;
            }
            else if(inEnvironment.isBaseEast(ours, false) && !obstEast && id == Label.NORTH){
                return east;
            }
            else if(inEnvironment.isBaseEast(ours, false) && !obstEast && id == Label.SOUTH){
                return south;
            }
            else if(inEnvironment.isBaseWest(ours, false) && !obstWest && id == Label.NORTH){
                return west;
            }
            else if(inEnvironment.isBaseWest(ours, false) && !obstWest && id == Label.SOUTH){
                return south;
            }
        }
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

}

    
