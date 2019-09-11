/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.UAV.abst.vehicle;

import ProOF.apl.UAV.abst.Control;
import ProOF.apl.UAV.abst.State;
import ProOF.apl.UAV.abst.UAVApproach;
import ProOF.apl.UAV.abst.UAVModel;
import ProOF.opt.abst.problem.meta.codification.Operator;

/**
 *
 * @author marcio
 * @param <App>
 * @param <Model>
 * @param <S>
 * @param <C>
 */
public abstract class oVehicle <App extends UAVApproach, Model extends UAVModel, S extends State, C extends Control> extends Operator {
    public abstract S[] build_states(App approach, Model model) throws Exception;
    public abstract C[] build_controls(App approach, Model model) throws Exception;
    public abstract void addConstraints(App approach, Model model) throws Exception;
}
