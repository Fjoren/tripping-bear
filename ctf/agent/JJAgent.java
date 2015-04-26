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
    int left = AgentAction.MOVE_WEST;
    int right = AgentAction.MOVE_EAST;
    int up = AgentAction.MOVE_NORTH;
    int down = AgentAction.MOVE_SOUTH;
    int bomb = AgentAction.PLANT_HYPERDEADLY_PROXIMITY_MINE;
    int step = -1;
    
    //top one gets size
    //both are constantly mapping (updates the map) 
    //
    public int getMove(AgentEnvironment inEnvironment) {
        if (firstMove) {
            if (inEnvironment.isBaseSouth(AgentEnvironment.OUR_TEAM, false))
                id = 0;
            if (inEnvironment.isBaseNorth(AgentEnvironment.OUR_TEAM, false))
                id = 1;
            if (id == 0) {
                board = new Board();
                currentJobs.add(Job.MAPPING);
                currentJobs.add(Job.FIND_MAP_SIZE);
                path = new int[] {left,down,down,down,down,right,bomb,bomb,left,bomb,down,bomb,down,bomb,down,bomb,down,left,bomb,left,bomb,left,bomb,left,bomb,down,bomb,right,bomb,right, bomb};
            }
            if (id == 1) {
                currentJobs.add(Job.RANDOM_MOVES);
                currentJobs.add(Job.MAPPING);
                path = new int[] {left,left,left,left,left,left,left,left,left,up,up,up,up,down,down,down,down,right,right,right,right,right,right,right,right,right,up,up,up,up};
            }
            firstMove = false;
        }

        //Non Final Jobs
        if (currentJobs.contains(Job.MAPPING)) {
            
        }

        //Final Jobs
        if (id == 0) {
            
        }   
        if(currentJobs.contains(Job.TOWARDSGOAL)) {
            towardsGoal()  
        }
        step++;
        return path[step];

    }

    private class Board {

        private class Coordinate {
            int x; //+ from left, - from right
            int y; //+ from bottom, - from top
        }
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
        
        board.addInformation(obstNorth, obstSouth, obstWest, obstEast)
            
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
            else()
        }
    }
        

    //tile on board, stores any information you want it to have
    private class BoardTile {
    
        int hasBomb;
        boolean blocked;
        int hasAgent;
        boolean unknown;
        boolean mightHaveAgent;
        boolean mightHaveBomb;

        private class BoardTile {
            private enum Flag = {empty}
            Hashtable<Flag,boolean>
        }
    
        Hashtable<Coordinate,BoardTile> board;
        int size = -1;
        boolean complete = false;

        Board() {
            board = new Hashtable<Coordinate,BoardTile>();
        }

        void match() {
        
        }

        boolean hasBomb(int x, int y) {
        
        }

        void normalize() {
        
        }

        void completed() {complete = true;}
        boolean isComplete() {return complete;}

    }

}
