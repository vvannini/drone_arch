/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.pog.method.BranchAndBound;

import ProOF.com.language.Factory;

/**
 *
 * @author marcio
 */
public final class fOptimalProblem extends Factory<aOptimalProblem>{
    public static final fOptimalProblem obj = new fOptimalProblem(); 
    @Override
    public String name() {
        return "fOptimalProblem";
    }
    @Override
    public aOptimalProblem build(int index) {
        switch(index){
            case 0: return new iTSP();
            case 1: return new iCCQSP();
        }
        return null;
    }
}
