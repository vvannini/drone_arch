/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.gen.codification.Integer;

import ProOF.opt.abst.problem.meta.Problem;
import ProOF.opt.abst.problem.meta.codification.Codification;

/**
 *
 * @author marcio
 */
public final class cInteger<Prob extends Problem> extends Codification<Prob, cInteger> {
    public final int cromo[];
    private final int min[];
    private final int max[];
    
    public cInteger(int size, int min[], int max[]) {
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
    public void copy(Prob mem, cInteger source) throws Exception {
        System.arraycopy(source.cromo, 0, this.cromo, 0, this.cromo.length);
    }
    @Override
    public cInteger build(Prob mem) throws Exception {
        return new cInteger(cromo.length, min, max);
    }
}
