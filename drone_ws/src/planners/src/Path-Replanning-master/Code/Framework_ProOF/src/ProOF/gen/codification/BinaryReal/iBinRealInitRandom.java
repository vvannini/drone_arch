/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.gen.codification.BinaryReal;

import ProOF.gen.operator.oInitialization;
import ProOF.opt.abst.problem.meta.Problem;

/**
 *
 * @author marcio
 */
public class iBinRealInitRandom extends oInitialization<Problem, cBinaryReal>{
    @Override
    public String name() {
        return "Random";
    }
    @Override
    public String description() {
        return "inicializa aleatoriamente cada posição i do vetor";
    }
    @Override
    public void initialize(Problem mem, cBinaryReal ind) throws Exception {
        for(int i=0; i<ind.X.length; i++){
            for(int j=0; j<ind.X[i].length; i++){
                ind.X[i][j] = mem.rnd.nextBoolean();
            }
        }
    }
}
