/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.pog.problem.MatrixDinamicsCplex;

import ProOF.com.language.Factory;

/**
 *
 * @author marcio
 */
public class fMDHeuristic extends Factory<aMDHeuristic>{
    public static final fMDHeuristic obj = new fMDHeuristic();
    @Override
    public String name() {
        return "MD-Heuristic";
    }

    @Override
    public aMDHeuristic build(int index) {
        switch(index){
            case 0: return new iMDModel();
        }
        return null;
    }

}