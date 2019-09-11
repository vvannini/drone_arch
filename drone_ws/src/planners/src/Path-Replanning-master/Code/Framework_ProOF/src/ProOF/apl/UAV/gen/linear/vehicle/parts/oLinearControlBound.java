/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.UAV.gen.linear.vehicle.parts;

import ProOF.apl.UAV.gen.linear.LinearControl;
import ProOF.apl.UAV.abst.vehicle.parts.oControlBound;
import ProOF.apl.UAV.gen.linear.LinearApproach;
import ProOF.apl.UAV.gen.linear.LinearModel;

/**
 *
 * @author marcio
 * @param <App>
 * @param <Model>
 */
public abstract class oLinearControlBound<App extends LinearApproach, Model extends LinearModel> extends oControlBound<App, Model, LinearControl>{

    @Override
    public abstract LinearControl[] build_controls(App approach, Model model) throws Exception;
    
}
