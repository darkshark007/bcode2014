package ModularStrategyBot;

import java.util.Random;

import ModularStrategyBot.Strategies.*;
import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.GameConstants;
import battlecode.common.GameObject;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotType;

/**
 * The Modular 
 * @author Stephen Bush
 */
public class RobotPlayer {
	
	static I_RobotStrategy strategy;
	
	public static void run(RobotController rc) throws GameActionException {
		
		strategy = new CirleAroundTheCenterStrategy(rc); 
		
		while (true) {
			if ( rc.isActive() ) {
				strategy.run();
			}
			rc.yield();
		}
	}
}
