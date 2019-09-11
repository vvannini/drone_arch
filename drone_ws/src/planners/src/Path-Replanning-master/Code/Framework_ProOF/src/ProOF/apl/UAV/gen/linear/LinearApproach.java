/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.UAV.gen.linear;

import ProOF.apl.UAV.Swing.Graphics2DReal;
import ProOF.apl.UAV.abst.UAVApproach;
import ProOF.apl.UAV.gen.linear.mission.fLinearMission;
import ProOF.apl.UAV.gen.linear.mission.oLinearMission;
import ProOF.apl.UAV.gen.linear.vehicle.fLinearVehicle;
import ProOF.apl.UAV.gen.linear.vehicle.oLinearVehicle;
import ProOF.apl.UAV.gen.linear.vehicle.parts.pLinearWaypoints;
import ProOF.com.Linker.LinkerApproaches;

/**
 *
 * @author marcio
 * @param <Model>
 */
public abstract class LinearApproach<Model extends LinearModel> extends UAVApproach<Model>{
    protected oLinearVehicle vehicle;
    protected oLinearMission mission;
    protected pLinearWaypoints waypoint;   
    
    @Override
    public void services(LinkerApproaches link) throws Exception {
        super.services(link);
        vehicle = link.get(fLinearVehicle.obj, vehicle);
        mission = link.get(fLinearMission.obj, mission);
        waypoint = link.need(pLinearWaypoints.class, waypoint);
    }

    @Override
    public void paint(Graphics2DReal gr, double size) throws Exception {
        mission.paint(this, gr, size);
    }
    public abstract int N() throws Exception;
    
    public int Waypoints() {
        return waypoint.Waypoints();
    }
    public double maxControl() {
        return waypoint.maxControl();
    }
    public double maxVelocity() {
        return waypoint.maxVelocity();
    }
}
