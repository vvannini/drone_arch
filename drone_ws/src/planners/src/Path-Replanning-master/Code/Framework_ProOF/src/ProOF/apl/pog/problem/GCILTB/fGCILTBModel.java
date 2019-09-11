package ProOF.apl.pog.problem.GCILTB;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import ProOF.apl.pog.problem.GCILT.*;
import ProOF.com.language.Factory;

/**
 *
 * @author marcio
 */
public final class fGCILTBModel extends Factory<aGCILTBModel>{
    public static final fGCILTBModel obj = new fGCILTBModel();

    @Override
    public String name() {
        return "Model";
    }
    @Override
    public aGCILTBModel build(int index) {
        switch(index){
            case 0: return new iGCILTBCplexFull();
        }
        return null;
    }
}
