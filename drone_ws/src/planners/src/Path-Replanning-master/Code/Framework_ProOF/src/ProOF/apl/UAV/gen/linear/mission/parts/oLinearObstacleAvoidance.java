/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.UAV.gen.linear.mission.parts;

import ProOF.apl.UAV.abst.mission.pats.oObstacleAvoidance;
import ProOF.apl.UAV.gen.linear.LinearApproach;
import ProOF.apl.UAV.gen.linear.LinearModel;

/**
 *
 * @author marcio
 * @param <App>
 * @param <Model>
 */
public abstract class oLinearObstacleAvoidance <App extends LinearApproach, Model extends LinearModel> extends oObstacleAvoidance<App, Model>{
    @Override
    public abstract void addConstraints(App approach, Model model) throws Exception;

    //@Override
    //public abstract boolean addChanges(App approach, Model model) throws Exception;
    
}
