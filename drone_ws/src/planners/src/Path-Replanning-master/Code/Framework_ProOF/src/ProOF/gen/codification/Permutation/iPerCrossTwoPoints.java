/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.gen.codification.Permutation;

import ProOF.gen.operator.oCrossover;
import ProOF.opt.abst.problem.meta.Problem;

/**
 *
 * @author marcio
 */
public class iPerCrossTwoPoints extends oCrossover<Problem, cPermutation>{
    @Override
    public String name() {
        return "TwoPoints";
    }
    @Override
    public String description() {
        return "inicializa aleatoriamente um ciclo hamiltoniano";
    }
    @Override
    public cPermutation crossover(Problem prob, cPermutation ind1, cPermutation ind2) throws Exception {
        cPermutation child = ind1.build(prob);
        int p[] = prob.rnd.cuts_points(child.cromo.length, 2);
        boolean selected[] = new boolean[child.cromo.length];
        for(int i=p[0]; i<p[1]; i++){
            child.cromo[i] = ind1.cromo[i];
            selected[child.cromo[i]] = true;
        }
        int i=p[1];
        int j=p[1];
        while(i!=p[0]){
            while(selected[ind2.cromo[j]]){
                j = (j+1) % child.cromo.length;
            }
            child.cromo[i] = ind2.cromo[j];
            selected[child.cromo[i]] = true;
            i = (i+1) % child.cromo.length;
        }
        return child;
    }
};
