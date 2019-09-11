/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.UAV.Blackmore;

import ProOF.apl.UAV.Swing.Graphics2DReal;
import ProOF.apl.UAV.gen.linear.mission.oLinearMission;
import java.awt.Color;

/**
 *
 * @author marcio
 */
public class BlackmoreMission extends oLinearMission<BlackmoreApproach, BlackmoreModel>{
    
    @Override
    public String name() {
        return "Blackmore";
    }
    
    @Override
    public void addObjective(BlackmoreApproach approach, BlackmoreModel model) throws Exception {
//        objective.addObjective(approach, model);
        //model.cplex //trabalhar assim.
    }
    
    @Override
    public void addConstraints(BlackmoreApproach approach, BlackmoreModel model) throws Exception {
        //---------------------- Mission path planning from start state to end point ---------------------------------
        for(int i=0; i<approach.N()*2; i++){
            int t = 0;
            model.states[t].x[i].setLB(approach.inst.start_state[i]);
            model.states[t].x[i].setUB(approach.inst.start_state[i]);
        }
        for(int i=0; i<approach.N(); i++){
            int t = approach.Waypoints();
            model.states[t].x[i].setLB(approach.inst.end_point[i]);
            model.states[t].x[i].setUB(approach.inst.end_point[i]);
        }
    }

    @Override
    public void paint(BlackmoreApproach approach, Graphics2DReal gr, double size) throws Exception {
        //start state
        gr.paintOvalR(approach.inst.start_state[0], approach.inst.start_state[1], 0.01*size, 0.01*size, Color.ORANGE, Color.BLACK);
        //end point
        gr.paintRectR(approach.inst.end_point[0], approach.inst.end_point[1], 0.01*size, 0.01*size, Color.ORANGE, Color.BLACK);
    }
    
}
