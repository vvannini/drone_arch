/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.pog.problem.MatrixDinamicsCplex;

import ProOF.opt.abst.problem.meta.codification.Codification;

/**
 *
 * @author marcio
 */
public class MDCodificationCpx extends Codification<
    MDFactoryCpx, MDCodificationCpx
> {
    protected int R;
    protected final double A[][];
    protected final double B[][];
    protected final double C[][];
    
    public MDCodificationCpx(MDFactoryCpx prob) {
        this.A = new double[prob.inst.S*prob.inst.M][prob.inst.S*prob.inst.M];
        this.B = new double[prob.inst.S*prob.inst.M][prob.inst.I];
        this.C = new double[prob.inst.S*prob.inst.M][prob.inst.S*(prob.inst.M-1)];
        
    }
    @Override
    public void copy(MDFactoryCpx prob, MDCodificationCpx source) throws Exception {
        this.R = source.R;
        for(int i=0; i<A.length; i++){
            System.arraycopy(source.A[i], 0, this.A[i], 0, this.A[i].length);
        }
        for(int i=0; i<B.length; i++){
            System.arraycopy(source.B[i], 0, this.B[i], 0, this.B[i].length);
        }
        for(int i=0; i<C.length; i++){
            System.arraycopy(source.C[i], 0, this.C[i], 0, this.C[i].length);
        }
    }
    @Override
    public MDCodificationCpx build(MDFactoryCpx prob) throws Exception {
        return new MDCodificationCpx(prob);
    }
}
