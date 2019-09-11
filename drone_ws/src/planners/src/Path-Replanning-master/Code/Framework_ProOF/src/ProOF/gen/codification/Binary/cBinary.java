/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.gen.codification.Binary;

import ProOF.opt.abst.problem.meta.Problem;
import ProOF.opt.abst.problem.meta.codification.Codification;

/**
 *
 * @author marcio
 */
public final class cBinary<Prob extends Problem> extends Codification<Prob, cBinary> {
    public final boolean cromo[];

    public cBinary(int size) {
        this.cromo = new boolean[size];
    }
    
    @Override
    public void copy(Prob mem, cBinary source) throws Exception {
        System.arraycopy(source.cromo, 0, this.cromo, 0, this.cromo.length);
    }
    @Override
    public cBinary build(Prob mem) throws Exception {
        return new cBinary(cromo.length);
    }
}
