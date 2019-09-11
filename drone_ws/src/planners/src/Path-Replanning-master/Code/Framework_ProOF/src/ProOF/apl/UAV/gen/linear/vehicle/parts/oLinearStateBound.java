/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.UAV.gen.linear.vehicle.parts;

import ProOF.apl.UAV.gen.linear.LinearState;
import ProOF.apl.UAV.abst.vehicle.parts.oStateBound;
import ProOF.apl.UAV.gen.linear.LinearApproach;
import ProOF.apl.UAV.gen.linear.LinearModel;

/**
 *
 * @author marcio
 * @param <App>
 * @param <Model>
 */
public abstract class oLinearStateBound<App extends LinearApproach, Model extends LinearModel> extends oStateBound<App, Model, LinearState>{

    @Override
    public abstract LinearState[] build_states(App approach, Model model) throws Exception;
    
}
