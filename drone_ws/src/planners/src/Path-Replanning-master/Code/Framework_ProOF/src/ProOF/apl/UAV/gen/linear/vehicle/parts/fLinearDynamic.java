/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.UAV.gen.linear.vehicle.parts;

import ProOF.apl.UAV.gen.linear.LinearApproach;
import ProOF.apl.UAV.gen.linear.LinearModel;
import ProOF.com.Linker.LinkerApproaches;
import ProOF.com.Linker.LinkerParameters;
import ProOF.com.language.Factory;

/**
 *
 * @author marcio
 */
public class fLinearDynamic extends Factory<oLinearDynamic>{
    public static final fLinearDynamic obj = new fLinearDynamic();
    @Override
    public String name() {
        return "Dynamic";
    }
    @Override
    public oLinearDynamic build(int index) {  //build the operators
        switch(index){
            case 0: return new AirFree();
            case 1: return new AirResistence();
            case 2: return new BlackmorePure();
            case 3: return new BlackmoreBased();
        }
        return null;
    }
    
    public static class AirFree extends oLinearDynamic<LinearApproach, LinearModel>{
        private final DynamicDt waypoint = new DynamicDt();
        @Override
        public String name() {
            return "Air-Free";
        }
        @Override
        public void services(LinkerApproaches link) throws Exception {
            super.services(link); //To change body of generated methods, choose Tools | Templates.
            link.add(waypoint);
        }
        @Override
        public void addConstraints(LinearApproach approach, LinearModel model) throws Exception {
            super.letBeAirFree(approach.N(), waypoint.dt());
            super.addConstraints(approach, model); //To change body of generated methods, choose Tools | Templates.
        }
    }
    private class AirResistence extends oLinearDynamic<LinearApproach, LinearModel>{
        private final DynamicDt waypoint = new DynamicDt();
        private double mass;
        @Override
        public String name() {
            return "Air-Resistence";
        }
        @Override
        public void services(LinkerApproaches link) throws Exception {
            super.services(link); //To change body of generated methods, choose Tools | Templates.
            link.add(waypoint);
        }
        @Override
        public void parameters(LinkerParameters link) throws Exception {
            super.parameters(link); //To change body of generated methods, choose Tools | Templates.
            mass = link.Dbl("mass", 3.7, 0.001, 1e5);
        }
        @Override
        public void addConstraints(LinearApproach approach, LinearModel model) throws Exception {
            super.letBeAirResistence(approach.N(), mass, waypoint.maxControl(), waypoint.maxVelocity(), waypoint.dt());
            super.addConstraints(approach, model); //To change body of generated methods, choose Tools | Templates.
        }
    }
    private class BlackmorePure extends oLinearDynamic<LinearApproach, LinearModel>{
        private final FixedDt waypoint = new FixedDt(1.0);  //it is suposed dt=1.0 second
        @Override
        public String name() {
            return "Blackmore";
        }
        @Override
        public void services(LinkerApproaches link) throws Exception {
            super.services(link); //To change body of generated methods, choose Tools | Templates.
            link.add(waypoint);
        }
        
        @Override
        public void addConstraints(LinearApproach approach, LinearModel model) throws Exception {
            super.letBeBlackmorePure(approach.N());
            super.addConstraints(approach, model); //To change body of generated methods, choose Tools | Templates.
        }
    }
    private class BlackmoreBased extends oLinearDynamic<LinearApproach, LinearModel>{
        private final FixedDt waypoint = new FixedDt(1.0);  //it is suposed dt=1.0 second
        private double pr;
        private double vr;
        @Override
        public String name() {
            return "Blackmore-Based";
        }
        @Override
        public void services(LinkerApproaches link) throws Exception {
            super.services(link); //To change body of generated methods, choose Tools | Templates.
            link.add(waypoint);
        }
        @Override
        public void parameters(LinkerParameters link) throws Exception {
            super.parameters(link); //To change body of generated methods, choose Tools | Templates.
            pr = link.Dbl("pr", 0.7869, 0.0001, 0.9999);
            vr = link.Dbl("vr", 0.6065, 0.0001, 0.9999);
        }
        @Override
        public void addConstraints(LinearApproach approach, LinearModel model) throws Exception {
            super.letBeBlackmoreBased(approach.N(), pr, vr);
            super.addConstraints(approach, model); //To change body of generated methods, choose Tools | Templates.
        }
    }
    
    
    
    private static class DynamicDt extends pLinearWaypoints{
        private int waypoints;
        private double time_horizon;
        private double maxVelocity;
        private double maxControl;
        
        @Override
        public String name() {
            return "Dynamic dt";
        }
        @Override
        public void parameters(LinkerParameters link) throws Exception {
            super.parameters(link); //To change body of generated methods, choose Tools | Templates.
            waypoints = link.Int("Waypoints", 20, 2, 1000);
            time_horizon = link.Dbl("time horizon", 20.0, 0.001, 1e5);
            maxVelocity = link.Dbl("Max-velocity", 3.0, 1e-5, 1e6);
            maxControl = link.Dbl("Max-control", 1.0, 1e-5, 1e6);
        }
        @Override
        public double timeHorizon() {
            return time_horizon;
        }
        @Override
        public int Waypoints() {
            return waypoints;
        }
        public double dt(){
            return time_horizon/waypoints;
        }
        @Override
        public double maxVelocity() {
            return maxVelocity*dt();
        }
        @Override
        public double maxControl() {
            return maxControl*dt()*dt();
        }
    }
    private static class FixedDt extends pLinearWaypoints{
        private final double dt;
        private int waypoints;
        private double maxVelocity;
        private double maxControl;

        public FixedDt(double dt) {
            this.dt = dt;
        }
        @Override
        public String name() {
            return "Fixed dt";
        }
        @Override
        public void parameters(LinkerParameters link) throws Exception {
            super.parameters(link); //To change body of generated methods, choose Tools | Templates.
            waypoints = link.Int("Waypoints", 20, 2, 1000);
            maxVelocity = link.Dbl("Max-velocity", 3.0, 1e-5, 1e6);
            maxControl = link.Dbl("Max-control", 1.0, 1e-5, 1e6);
        }
        @Override
        public double timeHorizon() {
            return waypoints*dt;
        }
        @Override
        public int Waypoints() {
            return waypoints;
        }
        @Override
        public double dt(){
            return dt;
        }
        @Override
        public double maxVelocity() {
            return maxVelocity*dt;
        }
        @Override
        public double maxControl() {
            return maxControl*dt;
        }
    }
}
