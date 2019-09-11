/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.pog.gen.operator;

import ProOF.opt.abst.problem.meta.Solution;
import ProOF.opt.abst.problem.meta.codification.Codification;


/**
 *
 * @author marcio
 */
public class iCrossRandom extends aCrossover{
    private static iCrossRandom obj = null;
    public static iCrossRandom object(){
        if(obj==null){
            obj = new iCrossRandom();
        }
        return obj;
    }
    
    @Override
    public String name() {
        return "Random";
    }
    @Override
    public String description() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    @Override
    public Solution crossover(Solution ind1, Solution ind2) throws Exception {
        int index = prob.rnd.nextInt(cross.length);
        Codification sol = cross[index].crossover(prob, ind1.codif(), ind2.codif());
        return prob.build_sol(sol);
    }
}
