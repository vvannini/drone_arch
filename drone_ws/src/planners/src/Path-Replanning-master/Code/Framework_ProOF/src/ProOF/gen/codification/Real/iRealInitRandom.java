/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.gen.codification.Real;

import ProOF.gen.operator.oInitialization;
import ProOF.opt.abst.problem.meta.Problem;

/**
 *
 * @author marcio
 */
public class iRealInitRandom extends oInitialization<Problem, cReal>{
    @Override
    public String name() {
        return "Random";
    }
    @Override
    public String description() {
        return "inicializa aleatoriamente cada posição i do vetor";
    }
    @Override
    public void initialize(Problem mem, cReal ind) throws Exception {
        for(int i=0; i<ind.X.length; i++){
            ind.X[i] = mem.rnd.nextDouble();
        }
    }
}
