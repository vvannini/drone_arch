/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.pog.problem.Test;

import ProOF.com.Linker.LinkerParameters;
import ProOF.com.language.Factory;
import ProOF.gen.operator.cPopulation;
import ProOF.gen.operator.hImprovement;
import ProOF.gen.operator.oInitialization;
import ProOF.gen.operator.oLocalMove;
import ProOF.opt.abst.problem.meta.Problem;
import ProOF.opt.abst.problem.meta.codification.Operator;


/**
 *
 * @author marcio
 */
public class TestOperator extends Factory<Operator>{
    public static final TestOperator obj = new TestOperator();
    
    @Override
    public String name() {
        return "MLCLSP Operators";
    }
    @Override
    public Operator build(int index) {
        switch(index){
            case 0: return new cMatrix();
            case 1: return new hFixAndOptimize();
        }
        return null;
    }
    private class cMatrix extends cPopulation{
        @Override
        public String name() {
            return "cMatrix";
        }

        @Override
        public void parameters(LinkerParameters win) throws Exception {
            win.Dbl("Initial probability", 0.5);
            win.Int("Size of Population", 100);
        }
        
    }
    private class hFixAndOptimize extends hImprovement{
        @Override
        public String name() {
            return "hFix&Optimize";
        }
    }
    
}
