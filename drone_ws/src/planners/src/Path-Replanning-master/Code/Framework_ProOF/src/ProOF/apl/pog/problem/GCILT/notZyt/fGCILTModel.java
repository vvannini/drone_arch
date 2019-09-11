package ProOF.apl.pog.problem.GCILT.notZyt;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import ProOF.com.language.Factory;

/**
 *
 * @author marcio
 */
public final class fGCILTModel extends Factory<aGCILTModel>{
    public static final fGCILTModel obj = new fGCILTModel();

    @Override
    public String name() {
        return "Model";
    }
    @Override
    public aGCILTModel build(int index) {
        switch(index){
            case 0: return new iGCILTCplexInt();
            case 1: return new iGCILTCplexInt();
        }
        return null;
    }
}
