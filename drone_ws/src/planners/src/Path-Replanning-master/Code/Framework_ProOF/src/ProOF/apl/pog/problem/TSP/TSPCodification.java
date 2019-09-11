/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.pog.problem.TSP;

import ProOF.opt.abst.problem.meta.codification.Codification;

/**
 *
 * @author marcio
 */
public class TSPCodification extends Codification<
    TSPFactory, TSPCodification
> {
    protected int path[];

    public TSPCodification(TSPFactory prob) {
        this.path = new int[prob.inst.N];
    }
    @Override
    public void copy(TSPFactory prob, TSPCodification source) throws Exception {
        System.arraycopy(source.path, 0, this.path, 0, this.path.length);
    }
    @Override
    public TSPCodification build(TSPFactory prob) throws Exception {
        return new TSPCodification(prob);
    }
}
