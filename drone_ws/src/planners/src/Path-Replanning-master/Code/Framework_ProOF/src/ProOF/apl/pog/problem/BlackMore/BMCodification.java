/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.pog.problem.BlackMore;

import ProOF.opt.abst.problem.meta.codification.Codification;

/**
 *
 * @author marcio
 */
public class BMCodification extends Codification<BMProblem, BMCodification> {
    public final BMState Xt[];
    public final BMControl Ut[];
    public BMCodification(BMProblem prob) {
        Xt = new BMState[prob.inst.T+1];
        Ut = new BMControl[prob.inst.T];
    }
    @Override
    public void copy(BMProblem prob, BMCodification source) throws Exception {
        System.arraycopy(source.Xt, 0, Xt, 0, Xt.length);
        System.arraycopy(source.Ut, 0, Ut, 0, Ut.length);
    }
    @Override
    public BMCodification build(BMProblem prob) throws Exception {
        return new BMCodification(prob);
    }
}
