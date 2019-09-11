/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.UAV.Blackmore;

import ProOF.CplexExtended.CplexExtended;
import ProOF.apl.UAV.oFull;
import ProOF.com.Linker.LinkerApproaches;
import ProOF.com.language.Factory;
import ProOF.opt.abst.problem.meta.codification.Operator;

/**
 *
 * @author marcio
 */
public class fBlackmoreOperator extends Factory<Operator>{
    
    public static final fBlackmoreOperator obj = new fBlackmoreOperator();
    
    @Override
    public String name() {
        return "Blackmore Operators";
    }
    
    @Override
    public Operator build(int index) {//build the operators
        switch(index){
            case 0: return new Full();
        }
        return null;
    }
    
    private class Full extends oFull<BlackmoreApproach, BlackmoreModel>{
        
        @Override
        public String name() {
            return "Full";
        }
        
        @Override
        public final BlackmoreModel build_model(BlackmoreApproach approach, CplexExtended cplex) throws Exception {            
            return new BlackmoreModel(approach, id_name(), cplex);
        }
    }        
}
