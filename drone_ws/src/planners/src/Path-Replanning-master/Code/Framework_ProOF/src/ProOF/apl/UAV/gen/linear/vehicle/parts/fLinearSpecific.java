/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.UAV.gen.linear.vehicle.parts;

import ProOF.apl.UAV.gen.linear.LinearApproach;
import ProOF.apl.UAV.gen.linear.LinearModel;
import ProOF.com.language.Factory;

/**
 *
 * @author marcio
 */
public class fLinearSpecific extends Factory<oLinearSpecific>{
    public static final fLinearSpecific obj = new fLinearSpecific();
    @Override
    public String name() {
        return "Specific";
    }
    @Override
    public oLinearSpecific build(int index) {  //build the operators
        switch(index){
            case 0: return new Empty();
        }
        return null;
    }
    private class Empty extends oLinearSpecific<LinearApproach, LinearModel>{
        @Override
        public String name(){
            return "Empty";
        }
        @Override
        public void addConstraints(LinearApproach approach, LinearModel model) throws Exception {
            
        }
    }
    
}