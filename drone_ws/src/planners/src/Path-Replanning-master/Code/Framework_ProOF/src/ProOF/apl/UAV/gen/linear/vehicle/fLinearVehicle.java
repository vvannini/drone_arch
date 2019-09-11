/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.UAV.gen.linear.vehicle;

import ProOF.apl.UAV.gen.linear.LinearControl;
import ProOF.apl.UAV.gen.linear.LinearState;
import ProOF.apl.UAV.gen.linear.LinearApproach;
import ProOF.apl.UAV.gen.linear.LinearModel;
import ProOF.apl.UAV.gen.linear.vehicle.parts.fLinearControlBound;
import ProOF.apl.UAV.gen.linear.vehicle.parts.fLinearDynamic;
import ProOF.apl.UAV.gen.linear.vehicle.parts.fLinearSpecific;
import ProOF.apl.UAV.gen.linear.vehicle.parts.fLinearStateBound;
import ProOF.apl.UAV.gen.linear.vehicle.parts.oLinearControlBound;
import ProOF.apl.UAV.gen.linear.vehicle.parts.oLinearDynamic;
import ProOF.apl.UAV.gen.linear.vehicle.parts.oLinearSpecific;
import ProOF.apl.UAV.gen.linear.vehicle.parts.oLinearStateBound;
import ProOF.com.Linker.LinkerApproaches;
import ProOF.com.language.Factory;

/**
 *
 * @author marcio
 */
public class fLinearVehicle extends Factory<oLinearVehicle>{
    public static final fLinearVehicle obj = new fLinearVehicle();
    @Override
    public String name() {
        return "Vehicle";
    }
    @Override
    public oLinearVehicle build(int index) {  //build the operators
        switch(index){
            case 0: return new PartsVehicle();
            case 1: return new FullVehicle();
        }
        return null;
    }
    private class PartsVehicle extends oLinearVehicle<LinearApproach, LinearModel>{
        private oLinearStateBound state;
        private oLinearControlBound control;
        private oLinearDynamic dynamic;
        private oLinearSpecific specific;
        
        @Override
        public String name() {
            return "Parts";
        }
        @Override
        public void services(LinkerApproaches link) throws Exception {
            super.services(link); //To change body of generated methods, choose Tools | Templates.
            state = link.get(fLinearStateBound.obj, state);
            control = link.get(fLinearControlBound.obj, control);
            dynamic = link.get(fLinearDynamic.obj, dynamic);
            specific = link.get(fLinearSpecific.obj, specific);
        }
        @Override
        public LinearState[] build_states(LinearApproach approach, LinearModel model) throws Exception {
            return state.build_states(approach, model);
        }
        @Override
        public LinearControl[] build_controls(LinearApproach approach, LinearModel model) throws Exception {
            return control.build_controls(approach, model);
        }
        @Override
        public void addConstraints(LinearApproach approach, LinearModel model) throws Exception {
            dynamic.addConstraints(approach, model);
            specific.addConstraints(approach, model);
        }
    }
    private class FullVehicle extends oLinearVehicle<LinearApproach, LinearModel>{
        @Override
        public String name() {
            return "Full";
        }
        
        @Override
        public LinearState[] build_states(LinearApproach approach, LinearModel model) throws Exception {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public LinearControl[] build_controls(LinearApproach approach, LinearModel model) throws Exception {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void addConstraints(LinearApproach approach, LinearModel model) throws Exception {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    }
}
