/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.pog.problem.PPDCP;


import ProOF.apl.factorys.*;
import ProOF.com.language.Factory;
import ProOF.gen.codification.Real.cReal;
import ProOF.gen.codification.Real.iRealCrossAritm;
import ProOF.gen.codification.Real.iRealCrossAvg;
import ProOF.gen.codification.Real.iRealCrossBLX;
import ProOF.gen.codification.Real.iRealCrossGeo;
import ProOF.gen.codification.Real.iRealCrossHeur;
import ProOF.gen.codification.Real.iRealCrossLinear;
import ProOF.gen.codification.Real.iRealCrossOX;
import ProOF.gen.codification.Real.iRealCrossOXFast;
import ProOF.gen.codification.Real.iRealCrossOnePoint;
import ProOF.gen.codification.Real.iRealCrossTwoPoints;
import ProOF.gen.codification.Real.iRealInitRandom;
import ProOF.gen.codification.Real.iRealMovInvert;
import ProOF.gen.codification.Real.iRealMovLimit;
import ProOF.gen.codification.Real.iRealMovReplace;
import ProOF.gen.codification.Real.iRealMutInvert;
import ProOF.gen.codification.Real.iRealMutLimit;
import ProOF.gen.codification.Real.iRealMutReplace;
import ProOF.gen.operator.oHcGA;
import ProOF.opt.abst.problem.meta.Solution;
import ProOF.opt.abst.problem.meta.codification.Operator;


/**
 *
 * @author marcio
 */
public class PPDCPOperator extends Factory<Operator>{
    public static final fRealOperator obj = new fRealOperator();

    @Override
    public String name() {
        return "PPDCP Operators";
    }
    
    @Override
    public Operator build(int index) {
        switch(index){
            case  0: return new iRealCrossAvg();
            case  1: return new iRealCrossGeo();
            case  2: return new iRealCrossOnePoint();
            case  3: return new iRealCrossTwoPoints();
            case  4: return new iRealCrossOX();
            case  5: return new iRealCrossOXFast();
            case  6: return new iRealCrossBLX();
            case  7: return new iRealCrossHeur();
            case  8: return new iRealCrossLinear();
            case  9: return new iRealCrossAritm();
                 
            case 10: return new iRealMutReplace();
            case 11: return new iRealMutLimit();
            case 12: return new iRealMutInvert();
                
            case 13: return new iRealMovReplace();
            case 14: return new iRealMovLimit();
            case 15: return new iRealMovInvert();
                
            case 16: return new iRealInitRandom();
        }
        return null;
    }
    
    private class CompactRepresentation extends oHcGA<PPDCPFactory, Solution<PPDCPFactory, PPDCPObjective, cReal, Solution>> {
        private double X[];
        @Override
        public String name() {
            return "Compact-Op";
        }
        @Override
        public void initialize(PPDCPFactory prob) throws Exception {
            X = new double[prob.inst.T*2];
            
        }
        @Override
        public void create(PPDCPFactory prob, Solution<PPDCPFactory, PPDCPObjective, cReal, Solution> ind) throws Exception {
            for(int i=0; i<ind.codif().X.length; i++){
                ind.codif().X[i] = ind.codif().X[i] + 2*prob.rnd.nextGaussian();
            }
        }

        @Override
        public void update(PPDCPFactory prob, Solution<PPDCPFactory, PPDCPObjective, cReal, Solution> ind, int popSize) throws Exception {
            
        }
        
    }
}
