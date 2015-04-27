package ctf.agent;

import ctf.common.AgentEnvironment;
import ctf.agent.Agent;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Enumeration;
import java.lang.Object;

import ctf.common.AgentAction;
            
public class Board {

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
    }

    Hashtable<Coordinate,BoardTile> board;
    int size = -1;
    Boolean complete = false;
    Coordinate test;

    Board() {
        board = new Hashtable<Coordinate,BoardTile>();
        addBoardTile(-2,-2);
        addBoardTile(-2,2);
        addBoardTile(2,-2);
        addBoardTile(2,2);
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
        if (env.isBaseSouth(env.OUR_TEAM,false))
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

