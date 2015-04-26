package ctf.agent;

import ctf.common.AgentEnvironment;
import ctf.agent.Agent;
import java.util.ArrayList;
import java.util.Hashtable;

import ctf.common.AgentAction;
//this is the jobs you can have
enum Job {MAPPING, RANDOM_MOVES, FIND_MAP_SIZE, GUARDING, ATTACKING}

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
        step++;
        return path[step];

    }

    private class Board {

        private class Coordinate {
            int x; //+ from left, - from right
            int y; //+ from bottom, - from top
        }

        private class BoardTile {
            enum Flag = {}
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

        void normalize() {
        
        }

        void completed() {complete = true;}
        boolean isComplete() {return complete;}

    }

}
