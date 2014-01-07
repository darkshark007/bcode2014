package ModularStrategyBot.Strategies;

import ModularStrategyBot.Orders.I_Orders;
import ModularStrategyBot.Path.Path;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;

public interface I_RobotStrategy {

	public void run();
	
	public void goTo(MapLocation in) throws GameActionException;
	public void goTo(MapLocation in,I_Orders orders) throws GameActionException;

	public void followPath(Path in) throws GameActionException;
	
	public void takeStepTowards(MapLocation in) throws GameActionException;
	
	public boolean locIsOnMap(MapLocation in);
}
