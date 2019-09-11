/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.UAV.PPCCS;

import ProOF.CplexExtended.CplexExtended;
import ProOF.apl.UAV.oFull;
import ProOF.com.language.Factory;
import ProOF.opt.abst.problem.meta.codification.Operator;

/**
 *
 * @author marcio e jesimar
 */
public class fPPCCSOperator extends Factory<Operator>{
    
    public static final fPPCCSOperator obj = new fPPCCSOperator();
    
    @Override
    public String name() {
        return "PPCCS Operators";
    }
    
    /**
     * Constroi os operadores.
     * @param index
     * @return 
     */
    @Override
    public Operator build(int index) {
        switch(index){
            case 0: return new Full();
        }
        return null;
    }
    
    private class Full extends oFull<PPCCSApproach, PPCCSModel>{
        
        @Override
        public String name() {
            return "Full";
        }
        
        @Override
        public final PPCCSModel build_model(PPCCSApproach approach, CplexExtended cplex) throws Exception {            
            return new PPCCSModel(approach, id_name(), cplex);
        }
    }        
}
