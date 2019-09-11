/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.gen.codification.Permutation;

import ProOF.opt.abst.problem.meta.Problem;
import ProOF.opt.abst.problem.meta.codification.Codification;

/**
 *
 * @author marcio
 */
public final class cPermutation<Prob extends Problem> extends Codification<Prob, cPermutation> {
    public final int cromo[];
    private final int min[];
    private final int max[];
    
    public cPermutation(int size, int min[], int max[]) {
        this.cromo = new int[size];
        this.min = min;
        this.max = max;
    }
    
    public int min(int i){
        return min[i];
    }
    public int max(int i){
        return max[i];
    }
    
    @Override
    public void copy(Prob mem, cPermutation source) throws Exception {
        System.arraycopy(source.cromo, 0, this.cromo, 0, this.cromo.length);
    }
    @Override
    public cPermutation build(Prob mem) throws Exception {
        return new cPermutation(cromo.length, min, max);
    }
}
