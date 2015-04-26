package ctf.agent;

import ctf.common.AgentEnvironment;
import ctf.agent.Agent;
import java.util.ArrayList;

import ctf.common.AgentAction;
//this is the jobs you can have
enum Job {MAPPING, RANDOM_MOVES, FIND_MAP_SIZE, GUARDING, ATTACKING, FIND_FLAG}

//Jeffrey Jennifer Agent
public class JJAgent extends Agent {
    
    static Board board;
    //which one is which
    //0 is north 
    //1 is south
    int id;
    //this instaniazies it 
    boolean firstMove = true;
    ArrayList<Job> currentJobs = new ArrayList<Job>();
    ArrayList<AgentAction> history = new ArrayList<AgentAction>(); 
    int counter = 0;
    int xDisplacement = 0;
    int yDisplacement = 0;
    
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
            }
            if (id == 1) {
                currentJobs.add(Job.RANDOM_MOVES);
                currentJobs.add(Job.MAPPING);
            }
            firstMove = false;
        }

        //Non Final Jobs
        if (currentJobs.contains(Job.MAPPING)) {
            
        }
        if(currentJobs.includes(Job.FIND_FLAG)){
            if(id == 0){

            }
        }

        //Final Jobs
        

    }

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
        boolean obstNorth = inEnvironment.isObstacleNorthImmediate();
        boolean obstSouth = inEnvironment.isObstacleSouthImmediate();
        boolean obstEast = inEnvironment.isObstacleEastImmediate();
        boolean obstWest = inEnvironment.isObstacleWestImmediate();
        
        
        // if the goal is north only, and we're not blocked
        if( goalNorth && ! goalEast && ! goalWest && !obstNorth ) {
            // move north
            return AgentAction.MOVE_NORTH;
            }
            
        // if goal both north and east
        if( goalNorth && goalEast ) {
            // pick north or east for move with 50/50 chance
            if( Math.random() < 0.5 && !obstNorth ) {
                return AgentAction.MOVE_NORTH;
                }
            if( !obstEast ) {   
                return AgentAction.MOVE_EAST;
                }
            if( !obstNorth ) {  
                return AgentAction.MOVE_NORTH;
                }
            }   
            
        // if goal both north and west  
        if( goalNorth && goalWest ) {
            // pick north or west for move with 50/50 chance
            if( Math.random() < 0.5 && !obstNorth ) {
                return AgentAction.MOVE_NORTH;
                }
            if( !obstWest ) {   
                return AgentAction.MOVE_WEST;
                }
            if( !obstNorth ) {  
                return AgentAction.MOVE_NORTH;
                }   
            }
        
        // if the goal is south only, and we're not blocked
        if( goalSouth && ! goalEast && ! goalWest && !obstSouth ) {
            // move south
            return AgentAction.MOVE_SOUTH;
            }
        
        // do same for southeast and southwest as for north versions    
        if( goalSouth && goalEast ) {
            if( Math.random() < 0.5 && !obstSouth ) {
                return AgentAction.MOVE_SOUTH;
                }
            if( !obstEast ) {
                return AgentAction.MOVE_EAST;
                }
            if( !obstSouth ) {
                return AgentAction.MOVE_SOUTH;
                }
            }
                
        if( goalSouth && goalWest && !obstSouth ) {
            if( Math.random() < 0.5 ) {
                return AgentAction.MOVE_SOUTH;
                }
            if( !obstWest ) {
                return AgentAction.MOVE_WEST;
                }
            if( !obstSouth ) {
                return AgentAction.MOVE_SOUTH;
                }
            }
        
        // if the goal is east only, and we're not blocked
        if( goalEast && !obstEast ) {
            return AgentAction.MOVE_EAST;
            }
        
        // if the goal is west only, and we're not blocked  
        if( goalWest && !obstWest ) {
            return AgentAction.MOVE_WEST;
            }   
        
        // otherwise, make any unblocked move
        if( !obstNorth ) {
            return AgentAction.MOVE_NORTH;
            }
        else if( !obstSouth ) {
            return AgentAction.MOVE_SOUTH;
            }
        else if( !obstEast ) {
            return AgentAction.MOVE_EAST;
            }
        else if( !obstWest ) {
            return AgentAction.MOVE_WEST;
            }   
        else {
            // completely blocked!
            return AgentAction.DO_NOTHING;
            }   
        }
    }

    //tile on board, stores any information you want it to have
    private class BoardTile {
    
        int hasBomb;
        boolean blocked;
        int hasAgent;
        boolean unknown;
    
    }

    private class Board {
    
        ArrayList<ArrayList<BoardTile>> board = new ArrayList<ArrayList<BoardTile>>();
        boolean complete = false;

        void makeBigger() {
            
        }
        void completed() {complete = true;}
        boolean isComplete() {return complete;}

    }

}
