/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.UAV.gen.linear.vehicle.parts;

import ProOF.apl.UAV.abst.vehicle.parts.pWaypoints;

/**
 *
 * @author marcio
 */
public abstract class pLinearWaypoints extends pWaypoints{
    public abstract double timeHorizon();
    public abstract int Waypoints();
    public abstract double dt();
    public abstract double maxControl();
    public abstract double maxVelocity();
}
