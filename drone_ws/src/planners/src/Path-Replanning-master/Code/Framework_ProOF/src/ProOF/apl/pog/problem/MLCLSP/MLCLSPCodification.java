/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.pog.problem.MLCLSP;

import ProOF.apl.pog.problem.TSP.*;
import ProOF.opt.abst.problem.meta.codification.Codification;
import ProOF.com.Stream.StreamPrinter;
import ProOF.com.Linker.LinkerResults;

/**
 *
 * @author marcio
 */
public class MLCLSPCodification extends Codification<
    MLCLSPProblem, MLCLSPCodification
> {
    protected int Y[][];

    public MLCLSPCodification(MLCLSPProblem mem) {
        this.Y = new int[mem.inst.J][mem.inst.T];
    }
    @Override
    public void copy(MLCLSPProblem mem, MLCLSPCodification source) throws Exception {
        for(int j=0; j<source.Y.length; j++){
            System.arraycopy(source.Y[j], 0, this.Y[j], 0, this.Y[j].length);
        }
    }
    @Override
    public MLCLSPCodification build(MLCLSPProblem mem) throws Exception {
        return new MLCLSPCodification(mem);
    }
}
