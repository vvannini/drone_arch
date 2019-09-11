/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.UAV.gen.linear.mission;

import ProOF.apl.UAV.Swing.Graphics2DReal;
import ProOF.apl.UAV.gen.linear.LinearApproach;
import ProOF.apl.UAV.gen.linear.LinearModel;
import ProOF.apl.UAV.abst.mission.oMission;

/**
 *
 * @author marcio
 */
public abstract class oLinearMission  <App extends LinearApproach, Model extends LinearModel> extends oMission<App, Model>{

    @Override
    public abstract void addObjective(App approach, Model model) throws Exception;

    @Override
    public abstract void addConstraints(App approach, Model model) throws Exception;

    //@Override
    //public abstract boolean addChanges(App approach, Model model) throws Exception;
    
    public abstract void paint(App approach, Graphics2DReal gr, double size) throws Exception;
}
