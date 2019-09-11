/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.UAV.abst.vehicle.parts;

import ProOF.apl.UAV.abst.UAVApproach;
import ProOF.apl.UAV.abst.UAVModel;
import ProOF.apl.UAV.abst.State;
import ProOF.opt.abst.problem.meta.codification.Operator;

/**
 *
 * @author marcio
 * @param <App>
 * @param <Model>
 * @param <S>
 */
public abstract class oStateBound<App extends UAVApproach, Model extends UAVModel, S extends State> extends Operator {
    public abstract S[] build_states(App approach, Model model) throws Exception;
}
