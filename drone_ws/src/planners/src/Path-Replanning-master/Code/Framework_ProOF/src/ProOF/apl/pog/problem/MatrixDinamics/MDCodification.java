/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.pog.problem.MatrixDinamics;

import ProOF.opt.abst.problem.meta.codification.Codification;

/**
 *
 * @author marcio
 */
public class MDCodification extends Codification<
    MDFactory, MDCodification
> {
    protected int R;
    protected final double A[][];
    protected final double B[][];

    public MDCodification(MDFactory prob) {
        this.A = new double[prob.inst.S*prob.inst.M][prob.inst.S*prob.inst.M];
        this.B = new double[prob.inst.S*prob.inst.M][prob.inst.I];
    }
    @Override
    public void copy(MDFactory prob, MDCodification source) throws Exception {
        this.R = source.R;
        for(int i=0; i<A.length; i++){
            System.arraycopy(source.A[i], 0, this.A[i], 0, this.A[i].length);
        }
        for(int i=0; i<B.length; i++){
            System.arraycopy(source.B[i], 0, this.B[i], 0, this.B[i].length);
        }
    }
    @Override
    public MDCodification build(MDFactory prob) throws Exception {
        return new MDCodification(prob);
    }
}
