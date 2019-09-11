/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.UAV.gen.linear.mission;

import ProOF.apl.UAV.Swing.Graphics2DReal;
import ProOF.apl.UAV.gen.linear.LinearApproach;
import ProOF.apl.UAV.gen.linear.LinearModel;
import ProOF.apl.UAV.Blackmore.BlackmoreMission;
import ProOF.apl.UAV.PPCCS.PPCCSMission;
import ProOF.apl.UAV.gen.linear.mission.parts.fLinearObjective;
import ProOF.apl.UAV.gen.linear.mission.parts.fLinearObstacleAvoidance;
import ProOF.apl.UAV.gen.linear.mission.parts.oLinearObjective;
import ProOF.apl.UAV.gen.linear.mission.parts.oLinearObstacleAvoidance;
import ProOF.apl.UAV.gen.linear.mission.parts.oLinearStayInRegion;
import ProOF.com.Linker.LinkerApproaches;
import ProOF.com.language.Factory;

/**
 *
 * @author marcio
 */
public class fLinearMission extends Factory<oLinearMission>{
    public static final fLinearMission obj = new fLinearMission();
    @Override
    public String name() {
        return "Mission";
    }
    @Override
    public oLinearMission build(int index) {  //build the operators
        switch(index){
            case 0: return new PartsMission();
            case 1: return new BlackmoreMission();
            case 2: return new PPCCSMission();
        }
        return null;
    }
    private class PartsMission extends oLinearMission<LinearApproach, LinearModel>{
        private oLinearObjective objective;
        private oLinearObstacleAvoidance obstacle;
        private oLinearStayInRegion stayIn;
        
        @Override
        public String name() {
            return "Parts";
        }
        @Override
        public void services(LinkerApproaches link) throws Exception {
            super.services(link); 
            objective = link.get(fLinearObjective.obj, objective);
            obstacle = link.get(fLinearObstacleAvoidance.obj, obstacle);
            //stayIn = link.get(fLinearStayInRegion.obj, stayIn);
            throw new UnsupportedOperationException("Not supported yet.");
        }
        @Override
        public void addObjective(LinearApproach approach, LinearModel model) throws Exception {
            objective.addObjective(approach, model);
        }
        @Override
        public void addConstraints(LinearApproach approach, LinearModel model) throws Exception {
            obstacle.addConstraints(approach, model);
            stayIn.addConstraints(approach, model);
        }

        @Override
        public void paint(LinearApproach approach, Graphics2DReal gr, double size) throws Exception {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }
}
