/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.UAV.gen.linear.mission.parts;

import ProOF.apl.UAV.abst.mission.pats.oStayInRegion;
import ProOF.apl.UAV.gen.linear.LinearApproach;
import ProOF.apl.UAV.gen.linear.LinearModel;

/**
 *
 * @author marcio
 * @param <App>
 * @param <Model>
 */
public abstract class oLinearStayInRegion <App extends LinearApproach, Model extends LinearModel> extends oStayInRegion<App, Model>{
    @Override
    public abstract void addConstraints(App approach, Model model) throws Exception;
}
