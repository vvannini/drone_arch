/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.CplexOpt;

import ProOF.com.Linker.LinkerApproaches;
import ProOF.com.language.Factory;
import ProOF.opt.abst.run.Exact;

/**
 *
 * @author marcio
 */
public class CplexModel extends Exact{
    private CplexFull cplexfull;
    private final Factory models;
    public CplexModel(Factory models) {
        this.models = models;
    }
    @Override
    public String name() {
        return "Cplex Model";
    }
    @Override
    public void services(LinkerApproaches link) throws Exception {
        cplexfull = link.get(models, cplexfull);
    }

    @Override
    public void execute() throws Exception {
        cplexfull.solve();
    }
}
