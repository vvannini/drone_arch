/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.pog.problem.RA.Codif2.TSP;

import ProOF.com.language.Factory;


/**
 *
 * @author marcio
 */
public class fRATSP extends Factory<aRATSP>{
    public static final fRATSP obj = new fRATSP();
    @Override
    public String name() {
        return "fRATSP";
    }

    @Override
    public aRATSP build(int index) {
        switch(index){
            case 0: return new iRATSPModel();
            case 1: return new iRATSPGreedy();
            
        }
        return null;
    }
}
