/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.UAV.gen.linear.mission.parts;

import ProOF.apl.UAV.gen.linear.LinearApproach;
import ProOF.apl.UAV.gen.linear.LinearModel;
import ProOF.com.language.Factory;

/**
 *
 * @author marcio
 */
public class fLinearObstacleAvoidance extends Factory<oLinearObstacleAvoidance>{
    public static final fLinearObstacleAvoidance obj = new fLinearObstacleAvoidance();
    @Override
    public String name() {
        return "Obstacle Avoidance";
    }
    @Override
    public oLinearObstacleAvoidance build(int index) {  //build the operators
        switch(index){
            case 0: return new Example();            
        }
        return null;
    }
    private class Example extends oLinearObstacleAvoidance<LinearApproach, LinearModel>{
        @Override
        public String name() {
            return "Example";
        }
        @Override
        public void addConstraints(LinearApproach approach, LinearModel model) throws Exception {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    }
}