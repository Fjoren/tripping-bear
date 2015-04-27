package ctf.agent;

import ctf.common.AgentEnvironment;
import ctf.agent.Agent;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Enumeration;
import java.io.*;

import ctf.common.AgentAction;
            
private class Board {

    private class Coordinate {
        int x;
        int y;
        Coordinate(int _x, int _y) {
            x = _x;
            y = _y;
        }
        public boolean equals(Object o) {
            if (o instanceof Coordinate && x == ((Coordinate)o).x && y == ((Coordinate)o).y)
                return true;
            else
                return false;
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
            for (Enumeration<String> e = options.keys(); e.hasMoreElements();) {
                Object temp = e.nextElement();
                flags.put((String)temp,options.get((String)temp));
            }
        }
    }

    Hashtable<Coordinate,BoardTile> board;
    int size = -1;
    Boolean complete = false;

    Board() {
        board = new Hashtable<Coordinate,BoardTile>();
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

    void updateMap(int x, int y, AgentEnvironment env) {
        BoardTile current = board.get(new Coordinate(x,y));
        //Shouldn't ever occur
        if (current == null) {
            addBoardTile(x,y,new BoardTile());
        }
        //Check update current tile
        Hashtable<String,Boolean> upd = new Hashtable<String,Boolean>();
        if (env.isBaseNorth(env.OUR_TEAM,false))
            upd.put("baseNorth",true);
        if (env.isBaseSouth(env.OUR_TEAM,true))
            upd.put("baseSouth",true);
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
    }

    void placeBomb(int x, int y) {
        
    }

    void match() {
    
    }

    void normalize() {
    
    }

    void completed() {complete = true;}
    boolean isComplete() {return complete;}

}

