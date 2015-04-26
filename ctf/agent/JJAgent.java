package ctf.agent;

import ctf.common.AgentEnvironment;
import ctf.agent.Agent;
import java.util.ArrayList;
import java.util.Hashtable;

import ctf.common.AgentAction;
//this is the jobs you can have
enum Job {MAPPING, RANDOM_MOVES, FIND_MAP_SIZE, GUARDING, ATTACKING, TOWARDSGOAL}

//Jeffrey Jennifer Agent
public class JJAgent extends Agent {
    
    static Board board;
    //which one is which
    int id;
    //this instaniazies it 
    boolean firstMove = true;
    ArrayList<Job> currentJobs = new ArrayList<Job>();
    ArrayList<AgentAction> history = new ArrayList<AgentAction>(); 
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
    //top one gets size
    //both are constantly mapping (updates the map) 
    //
    public int getMove(AgentEnvironment inEnvironment) {
        if (firstMove) {
            currentJobs.add(Job.MAPPING);
            if (inEnvironment.isBaseSouth(AgentEnvironment.OUR_TEAM, false))
                id = 0;
            if (inEnvironment.isBaseNorth(AgentEnvironment.OUR_TEAM, false))
                id = 1;
            if (id == 0) {
                board = new Board();
                currentJobs.add(Job.TOWARDSGOAL);
                //path = new int[] {left,down,down,down,down,right,bomb,bomb,left,bomb,down,bomb,down,bomb,down,bomb,down,left,bomb,left,bomb,left,bomb,left,bomb,down,bomb,right,bomb,right, bomb};
            }
            if (id == 1) {
                currentJobs.add(Job.TOWARDSGOAL);
                //path = new int[] {left,left,left,left,left,left,left,left,left,up,up,up,up,down,down,down,down,right,right,right,right,right,right,right,right,right,up,up,up,up};
            }
            firstMove = false;
        }

        //Non Final Jobs
        if (currentJobs.contains(Job.MAPPING)) {
            board.updateMap(inEnvironment);
        }

        //Final Jobs  
        if(currentJobs.contains(Job.TOWARDSGOAL)) {
            move = towardsGoal(inEnvironment);  
        }
        if(currentJobs.contains(Job.RANDOM_MOVES)){
            move = random(inEnvironment);
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

        if(move == bomb)
            bombLastMove = true;
        else
            bombLastMove = false;
            
        history.add((AgentAction)move);
            
        return move;
        
    //    step++;
    //    return path[step];

    }

    //if tbe map is unknown, just go towards the goal and add to the map
    public int towardsGoal(AgentEnvironment inEnvironment){
        // booleans describing direction of goal
        // goal is either enemy flag, or our base
        boolean goalNorth;
        boolean goalSouth;
        boolean goalEast;
        boolean goalWest;

        
        if( !inEnvironment.hasFlag() ) {
            // make goal the enemy flag
            goalNorth = inEnvironment.isFlagNorth( 
                inEnvironment.ENEMY_TEAM, false );
        
            goalSouth = inEnvironment.isFlagSouth( 
                inEnvironment.ENEMY_TEAM, false );
        
            goalEast = inEnvironment.isFlagEast( 
                inEnvironment.ENEMY_TEAM, false );
        
            goalWest = inEnvironment.isFlagWest( 
                inEnvironment.ENEMY_TEAM, false );
            }
        else {
            // we have enemy flag.
            // make goal our base
            goalNorth = inEnvironment.isBaseNorth( 
                inEnvironment.OUR_TEAM, false );
        
            goalSouth = inEnvironment.isBaseSouth( 
                inEnvironment.OUR_TEAM, false );
        
            goalEast = inEnvironment.isBaseEast( 
                inEnvironment.OUR_TEAM, false );
        
            goalWest = inEnvironment.isBaseWest( 
                inEnvironment.OUR_TEAM, false );
            }
        
        // now we have direction booleans for our goal  
        
        // check for immediate obstacles blocking our path      
            //needs to check if there is a bomb or agent there
        boolean obstNorth = inEnvironment.isObstacleNorthImmediate();
        boolean obstSouth = inEnvironment.isObstacleSouthImmediate();
        boolean obstEast = inEnvironment.isObstacleEastImmediate();
        boolean obstWest = inEnvironment.isObstacleWestImmediate();
            
        // if goal both north and east
        if( goalNorth && goalEast ) {
            // pick north or east for move with 50/50 chance
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
}

    
