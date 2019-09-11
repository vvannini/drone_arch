/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.gen.codification.Real;

import ProOF.gen.operator.oCrossover;
import ProOF.opt.abst.problem.meta.Problem;

/**
 *
 * @author marcio
 */
public class iRealCrossAvg extends oCrossover<Problem, cReal>{
    @Override
    public String name() {
        return "Average";
    }
    @Override
    public String description() {
        return "para cada posição i do vetor pega-se a média dos valores dos pais nesta mesma posição";
    }
    @Override
    public cReal crossover(Problem mem, cReal ind1, cReal ind2) throws Exception {
        cReal child = ind1.build(mem);
        for(int i=0; i<child.X.length; i++){
            child.X[i] = (ind1.X[i]+ind2.X[i])/2.0;
        }
        return child;
    }
};
