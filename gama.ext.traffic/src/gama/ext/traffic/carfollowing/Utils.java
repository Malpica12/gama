/*******************************************************************************************************
 *
 * Utils.java, in gama.ext.traffic, is part of the source code of the
 * GAMA modeling and simulation platform (v.2.0.0).
 *
 * (c) 2007-2021 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package gama.ext.traffic.carfollowing;

import org.apache.commons.collections4.OrderedBidiMap;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Triple;

import gama.ext.traffic.RoadNodeSkill;
import gama.ext.traffic.RoadSkill;
import gama.metamodel.agent.IAgent;
import gama.runtime.IScope;

import static gama.ext.traffic.DrivingSkill.getCurrentRoad;
import static gama.ext.traffic.DrivingSkill.getCurrentTarget;
import static gama.ext.traffic.DrivingSkill.getDistanceToCurrentTarget;
import static gama.ext.traffic.DrivingSkill.getLinkedLaneLimit;
import static gama.ext.traffic.DrivingSkill.getMinSafetyDistance;
import static gama.ext.traffic.DrivingSkill.getNextRoad;
import static gama.ext.traffic.DrivingSkill.getNumLanesOccupied;
import static gama.ext.traffic.DrivingSkill.getProbaUseLinkedRoad;
import static gama.ext.traffic.DrivingSkill.getVehicleLength;
import static gama.ext.traffic.DrivingSkill.isViolatingOneway;
import static gama.ext.traffic.DrivingSkill.readyToCross;

import java.util.ArrayList;
import java.util.List;

/**
 * The Class Utils.
 */
public class Utils {
	
	/**
	 * Attempts to make lane changing probabilities timestep-agnostic.
	 *
	 * @param probaInOneSecond a probability with respect to one second
	 * @param timeStep         the duration of a simulation step
	 * @return the rescaled probability
	 */
	public static double rescaleProba(final double probaInOneSecond,
			final double timeStep) {
		return Math.min(probaInOneSecond * timeStep, 1.0);
	}
	
	/**
	 * Compute linked lane limit.
	 *
	 * @param vehicle the vehicle
	 * @param road the road
	 * @return the int
	 */
	public static int computeLinkedLaneLimit(final IAgent vehicle,
			final IAgent road) {
		double probaUseLinkedRoad = getProbaUseLinkedRoad(vehicle);
		int linkedLaneLimit = getLinkedLaneLimit(vehicle);

		IAgent linkedRoad = RoadSkill.getLinkedRoad(road);
		int numLinkedLanes = (linkedRoad != null) ? RoadSkill.getNumLanes(linkedRoad) : 0;

		if (probaUseLinkedRoad == 0.0) {
			linkedLaneLimit = 0;
		} else if (linkedLaneLimit == -1) {
			linkedLaneLimit = numLinkedLanes;
		} else {
			linkedLaneLimit = Math.min(linkedLaneLimit, numLinkedLanes);
		}
		return linkedLaneLimit;
	}
	
	/**
	 * Find leader.
	 *
	 * @param scope the scope
	 * @param vehicle the vehicle
	 * @param lowestLane the lowest lane
	 * @return the triple
	 */
	// TODO: this and findFollower don't need this many params
	public static Triple<IAgent, Double, Boolean> findLeader(final IScope scope,
										final IAgent vehicle,
										final int lowestLane) {
		IAgent road = getCurrentRoad(vehicle);
		IAgent target = getCurrentTarget(vehicle);
		double distToCurrentTarget = getDistanceToCurrentTarget(vehicle);	
		double vL = getVehicleLength(vehicle);
		double minSafetyDist = getMinSafetyDistance(vehicle);
		int numLanesOccupied = getNumLanesOccupied(vehicle);
		
		// The second condition can be false when moving to a bigger road.
		// In that case we will skip this and only find leaders on the new road.
		if (road != null &&
				lowestLane <= RoadSkill.getNumLanesTotal(road) - numLanesOccupied) {
			Triple<IAgent, Double, Boolean> triple = findNeighborOnCurrentRoad(scope,
					vehicle, lowestLane, true);
			if (triple != null) {
				 return triple;
			}
		}

		// No leading vehicle is found on the current road
		IAgent leader = null;
		double minGap = Double.MAX_VALUE;
		boolean sameDirection = false;
		IAgent nextRoad = getNextRoad(vehicle);
		// If vehicle is approaching an intersection, we need to slow down if
		// 1. The intersection is the final target, the vehicle hasn't decide the next road yet
		// 2. It is not possible to enter the next road (e.g. traffic lights)
		if (nextRoad == null || !readyToCross(scope, vehicle, target, nextRoad)) {
			// Return a virtual leading vehicle of length 0 to simulate deceleration at intersections
			// NOTE: the added minSafetyDist is necessary for the vehicle to ignore the safety dist when stopping at an endpoint
			return ImmutableTriple.of(target, distToCurrentTarget + minSafetyDist, false);
		} else {
			boolean willViolateOneway = target == RoadSkill.getTargetNode(nextRoad);
			IAgent nextTarget = !willViolateOneway ?
				RoadSkill.getTargetNode(nextRoad) : RoadSkill.getSourceNode(nextRoad);
			int numLanesNext = RoadSkill.getNumLanes(nextRoad);
			int linkedLaneLimit = computeLinkedLaneLimit(vehicle, nextRoad);
			int lowestLaneToCheck = Math.min(lowestLane, numLanesNext + linkedLaneLimit - numLanesOccupied);
			
			for (int i = 0; i < numLanesOccupied; i += 1) {
				int lane = lowestLaneToCheck + i;
				OrderedBidiMap<Double, IAgent> distMap = 
						RoadSkill.getVehicleOrderingMap(scope, nextRoad, lane).inverseBidiMap();
				boolean wrongDirection = lane < numLanesNext ? false : true;
				wrongDirection = willViolateOneway ? !wrongDirection : wrongDirection;

				if (distMap.isEmpty()) {
					continue;
				}
				
				double tmpLeaderDist = !wrongDirection ? distMap.firstKey() : distMap.lastKey();
				IAgent tmpLeader = distMap.get(tmpLeaderDist);
				if (tmpLeader == null || tmpLeader.dead()) {
					continue;
				}
				boolean tmpSameDirection = nextTarget == getCurrentTarget(tmpLeader);
				double extraGap = !wrongDirection ? 
						RoadSkill.getTotalLength(nextRoad) - tmpLeaderDist : tmpLeaderDist;
				double otherVL = getVehicleLength(tmpLeader);
				double gap = distToCurrentTarget + extraGap - 0.5 * vL - 0.5 * otherVL;
				
				if (gap < 0) {
					return ImmutableTriple.of(tmpLeader, gap, tmpSameDirection);
				}
				if (gap < minGap) {
					minGap = gap;
					leader = tmpLeader;
					sameDirection = tmpSameDirection;
				}
			}
		}	

		if (leader != null) {
			return ImmutableTriple.of(leader, minGap, sameDirection);
		} else {
			// the road ahead seems to be completely clear
			return ImmutableTriple.of(null, 1e6, false);
		}
	}
	
	
	/**
	 * Find follower.
	 *
	 * @param scope the scope
	 * @param vehicle the vehicle
	 * @param lowestLane the lowest lane
	 * @return the triple
	 */
	public static Triple<IAgent, Double, Boolean> findFollower(final IScope scope,
										final IAgent vehicle,
										final int lowestLane) {
		IAgent road = getCurrentRoad(vehicle);
		IAgent target = getCurrentTarget(vehicle);
		double distToCurrentTarget = getDistanceToCurrentTarget(vehicle);	
		if (road == null) {
		}
		double vL = getVehicleLength(vehicle);
		int numLanesOccupied = getNumLanesOccupied(vehicle);

		// The second condition can be false when moving to a bigger road.
		// In case there's new lane with higher index, consider that there's no back vehicle.
		if (road != null &&
				lowestLane <= RoadSkill.getNumLanesTotal(road) - numLanesOccupied) {
			Triple<IAgent, Double, Boolean> triple = findNeighborOnCurrentRoad(scope,
					vehicle, lowestLane, false);
			if (triple != null) {
				 return triple;
			}
		} else {
			return ImmutableTriple.of(null, 1e6, false);
		}

		// Find followers on previous roads
		IAgent follower = null;
		double minGap = Double.MAX_VALUE;
		boolean sameDirection = false;
		IAgent sourceNode = (target == RoadSkill.getTargetNode(road)) ?
				RoadSkill.getSourceNode(road) : target;
		for (IAgent prevRoad : RoadNodeSkill.getRoadsIn(sourceNode)) {
			int numLanes = RoadSkill.getNumLanes(prevRoad);
			int numLanesTotal = RoadSkill.getNumLanesTotal(prevRoad);
			for (int i = 0; i < numLanesOccupied; i += 1) {
				int lane = lowestLane + i;
				if (lane >= numLanesTotal - numLanesOccupied) {
					break;
				}
				
				boolean isLinkedLane = lane >= numLanes;
				if (isLinkedLane) {
					prevRoad = RoadSkill.getLinkedRoad(prevRoad);
				}
				OrderedBidiMap<Double, IAgent> distMap = 
						RoadSkill.getVehicleOrderingMap(scope, prevRoad, lane).inverseBidiMap();
				if (distMap.isEmpty()) {
					continue;
				}
				double distQuery = !isLinkedLane ? distMap.lastKey() : distMap.firstKey();

				IAgent tmpFollower = distMap.get(distQuery);
				if (getCurrentTarget(tmpFollower) != sourceNode) {
					// This vehicle is not following the current one, but it's going the other way
					continue;
				}
				
				boolean tmpSameDirection = sourceNode == getCurrentTarget(tmpFollower);
				double extraGap = !isLinkedLane ? distQuery : RoadSkill.getTotalLength(prevRoad) - distQuery;
				double gap = RoadSkill.getTotalLength(road) - distToCurrentTarget 
						+ extraGap - 0.5 * vL - 0.5 * getVehicleLength(tmpFollower);
				if (gap < 0) {
					return ImmutableTriple.of(tmpFollower, gap, tmpSameDirection);
				} else if (gap < minGap) {
					minGap = gap;
					follower = tmpFollower;
					sameDirection = tmpSameDirection;
				}
			}
		}
			
		if (follower != null && !follower.dead()) {
			return ImmutableTriple.of(follower, minGap, sameDirection);
		} else {
			return ImmutableTriple.of(null, 1e6, false);
		}
	}
	
	/**
	 * Find neighbor on current road.
	 *
	 * @param scope the scope
	 * @param vehicle the vehicle
	 * @param lowestLane the lowest lane
	 * @param isLeader the is leader
	 * @return the triple
	 */
	private static Triple<IAgent, Double, Boolean> findNeighborOnCurrentRoad(
			final IScope scope,
			final IAgent vehicle,
			final int lowestLane,
			final boolean isLeader) {
		IAgent road = getCurrentRoad(vehicle);
		IAgent target = getCurrentTarget(vehicle);
		double distToCurrentTarget = getDistanceToCurrentTarget(vehicle);	
		double vL = getVehicleLength(vehicle);
		boolean violatingOneway = isViolatingOneway(vehicle);
		int numRoadLanes = RoadSkill.getNumLanes(road);
		int numLanesOccupied = getNumLanesOccupied(vehicle);

		IAgent neighbor = null;
		double minGap = Double.MAX_VALUE;
		boolean sameDirection = false;
		for (int i = 0; i < numLanesOccupied; i += 1) {
			int lane = lowestLane + i;
			OrderedBidiMap<Double, IAgent> distMap = 
					RoadSkill.getVehicleOrderingMap(scope, road, lane).inverseBidiMap();
			boolean wrongDirection = lane < numRoadLanes ? false : true;
			wrongDirection = violatingOneway ? !wrongDirection : wrongDirection;
			double tmpDistQuery = !wrongDirection ? distToCurrentTarget : 
					RoadSkill.getTotalLength(road) - distToCurrentTarget;
			
			// Another vehicle already occupied the exact same longitudinal spot
			// in this lane, which will lead to a crash if switch
			if (distMap.containsKey(tmpDistQuery) && distMap.get(tmpDistQuery) != vehicle) {
				return ImmutableTriple.of(distMap.get(tmpDistQuery), -1.0, false);
			}
			
			Double k;
			if (isLeader) {
				k = !wrongDirection ? distMap.nextKey(tmpDistQuery)
						: distMap.previousKey(tmpDistQuery);
			} else {
				k = !wrongDirection ? distMap.previousKey(tmpDistQuery)
					: distMap.nextKey(tmpDistQuery);
			}
			// No neighbor on this lane
			if (k == null) {
				continue;
			} 
			double tmpNeighborDist = k;
			IAgent tmpNeighbor = distMap.get(tmpNeighborDist);
			if (tmpNeighbor == null || tmpNeighbor.dead()) {
				continue;
			}
			boolean tmpSameDirection = target == getCurrentTarget(tmpNeighbor);
			double otherVL = getVehicleLength(tmpNeighbor);
			double gap = Math.abs(tmpNeighborDist - tmpDistQuery) - 0.5 * vL - 0.5 * otherVL;

			if (gap < 0) {
				// Return immediately if crashing into another vehicle
				return ImmutableTriple.of(tmpNeighbor, gap, tmpSameDirection);
			} else if (gap < minGap) {
				minGap = gap;
				neighbor = tmpNeighbor;
				sameDirection = tmpSameDirection;
			}
		}

		if (neighbor != null) {
			return ImmutableTriple.of(neighbor, minGap, sameDirection);
		} else {
			return null;
		}
	}
}
