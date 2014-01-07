package ModularStrategyBot.Strategies;

import ModularStrategyBot.Orders.I_Orders;
import ModularStrategyBot.Path.Path;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.Direction;
import battlecode.common.RobotController;
import battlecode.common.Team;

public abstract class Strategy implements I_RobotStrategy {
	
	static RobotController rc;
	
	
	// This is my fixed direction.  If i cannot go straight, i will go in this direction instead until i can go straight.  This direction needs to be fixed to avoid bouncing behavior.
	// 0 == Left
	// 1 == Right
	int direc = 0;
	// This variable holds the location i was prior to my last step.  I will not go back to that spot in order to avoid bouning.
	MapLocation lastSpace;
	
	public Strategy(RobotController in) {
		rc = in;
		lastSpace = rc.senseHQLocation();
	}

	public abstract void run();

	
	/**
	 * Take a single step towards the specified location.  This method does NOT take into account whether the robot is able to move, and if it is called and the robot is unable to move an exception will occur.
	 * @param in	The location to move towards.
	 * @throws GameActionException	If the robot is unable to move (for example, if it has already moved) then this exception will be thrown.
	 */
	public void takeStepTowards(MapLocation in) throws GameActionException {
		// Check Straight
		
		rc.setIndicatorString(2, "Direc: "+direc);
		MapLocation myLoc = rc.getLocation();
		System.out.println("Taking Step towards:  ("+in.x+","+in.y+")  from  ("+myLoc.x+","+myLoc.y+")");
		
		Direction dirS = myLoc.directionTo(in);
		
		// See if i can move straight
		if ( rc.canMove(dirS) ) {
			if ( !myLoc.add(dirS).equals(lastSpace) ) {
				rc.move(dirS);
				lastSpace = myLoc;
				return;				
			}
		}
		
		// Either there is a robot in the way, or there is a mine.
		// See if i can move 45 degrees left of straight
		Direction dirL = dirS.rotateLeft();
		if ( rc.canMove(dirL) ) {
			if ( !myLoc.add(dirL).equals(lastSpace) ) {
				rc.move(dirL);
				lastSpace = myLoc;
				return;				
			}
		}
		
		// Either there is a robot in the way, or there is a mine.
		// See if i can move 45 degrees right of straight
		Direction dirR = dirS.rotateRight();
		if ( rc.canMove(dirR) ) {
			if ( !myLoc.add(dirR).equals(lastSpace) ) {
				rc.move(dirR);
				lastSpace = myLoc;
				return;				
			}
		}
		
		// Since i cant move in any of the three forward directions, i will try to move at a steeper angle.
		// I use a fixed direction for this in order to avoid bouncing behavior.  That is, if i am moving 90/135 degrees left of straight, 
		//   i will continue to move in that direction until that direction becomes inviable.
		Direction dirLL = dirL.rotateLeft();
		Direction dirLLL = dirLL.rotateLeft();
		Direction dirRR = dirR.rotateRight();
		Direction dirRRR = dirRR.rotateRight();
		
		// Check 90 degrees in my fixed direction
		if ( direc == 0 ) dirS = dirLL;
		else  dirS = dirRR;
		if ( rc.canMove(dirS) ) {
			if ( !myLoc.add(dirS).equals(lastSpace) ) {
				rc.move(dirS);
				lastSpace = myLoc;
				return;				
			}
		}

		// Check 135 degrees in my fixed direction
		if ( direc == 0 ) dirS = dirLLL;
		else  dirS = dirRRR;
		if ( rc.canMove(dirS) ) {
			if ( !myLoc.add(dirS).equals(lastSpace) ) {
				rc.move(dirS);
				lastSpace = myLoc;
				return;				
			}
		}

		// If that direction did not work, swap my direction and try that way.
		direc = 1-direc; // Because direc is initialized at 0, this will always bounce it between 1 and 0
		
		
		// Check 90 degrees in my new fixed direction
		if ( direc == 0 ) dirS = dirLL;
		else  dirS = dirRR;
		if ( rc.canMove(dirS) ) {
			if ( !myLoc.add(dirS).equals(lastSpace) ) {
				rc.move(dirS);
				lastSpace = myLoc;
				return;				
			}
		}

		// Check 135 degrees in my fixed direction
		if ( direc == 0 ) dirS = dirLLL;
		else  dirS = dirRRR;
		if ( rc.canMove(dirS) ) {
			if ( !myLoc.add(dirS).equals(lastSpace) ) {
				rc.move(dirS);
				lastSpace = myLoc;
				return;				
			}
		}
	}
	

	/**
	 * Moves the robot to the specified location.  Ensure that the destination is reachable before calling this method, as control of the robot is taken by this method until it reaches its destination
	 * @param in	The location to move to.
	 */
	public void goTo(MapLocation in) throws GameActionException { goTo(in,null); }
	/**
	 * Moves the robot to the specified location.  Additionally, the specified set of orders will be executed each turn until the destination is reached.  Ensure that the destination is reachable and is not occupied before calling this method, as control of the robot is taken by this method until it reaches its destination.
	 * @param in	The location to move to.
	 * @param orders	The set of orders which will be executed each turn until the robot reaches its destination.
	 */
	public void goTo(MapLocation in,I_Orders orders) throws GameActionException {
		while ( !rc.getLocation().equals(in) ) {
			if ( orders != null ) orders.executeOrders(rc);
			if ( rc.isActive() ) takeStepTowards(in);
			rc.yield();
		}
	}

	
	public void followPath(Path in) throws GameActionException {
		int path = 0;
		if ( rc.getLocation().equals(in.getLink(0)) ) path = 1;
		
		MapLocation nextLink;
		Direction dir;
		while ( true ) {
			if ( rc.isActive() ) {
				nextLink = in.getLink(path++);
				if ( nextLink == null ) return;

				dir = rc.getLocation().directionTo(nextLink);

				
				while ( !rc.canMove(dir) ) rc.yield();
				rc.move(dir);
			}
		}

		
	}
	

	/**
	 * Checks to see whether the specified location is on the map.  Checks only the map boundaries, does NOT take into account whether the location is a wall.
	 * @param in	The MapLocation to check
	 * @return
	 */
	public boolean locIsOnMap(MapLocation in) {
		if ( in.x < 0 ) return false;
		if ( in.y < 0 ) return false;
		if ( in.x >= rc.getMapWidth() ) return false;
		if ( in.y >= rc.getMapHeight() ) return false;
		return true;
	}
	
}
