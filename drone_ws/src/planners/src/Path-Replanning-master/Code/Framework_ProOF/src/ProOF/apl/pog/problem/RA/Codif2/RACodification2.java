/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.pog.problem.RA.Codif2;

import ProOF.opt.abst.problem.meta.codification.Codification;

/**
 *
 * @author marcio
 */
public class RACodification2 extends Codification<RAProblem2, RACodification2> {

    public final int Wit[][];

    public RACodification2(RAProblem2 prob) {
        Wit = new int[prob.inst.N][prob.inst.T];
    }

    @Override
    public void copy(RAProblem2 prob, RACodification2 source) throws Exception {
        for (int i = 0; i < prob.inst.N; i++) {
            System.arraycopy(source.Wit[i], 0, this.Wit[i], 0, prob.inst.T);
        }
    }

    @Override
    public RACodification2 build(RAProblem2 prob) throws Exception {
        return new RACodification2(prob);
    }

}
