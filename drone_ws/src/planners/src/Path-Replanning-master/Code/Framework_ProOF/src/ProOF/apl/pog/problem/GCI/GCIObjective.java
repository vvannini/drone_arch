/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.pog.problem.GCI;

import ProOF.opt.abst.problem.meta.objective.BoundDbl;
import ProOF.opt.abst.problem.meta.objective.SingleObjective;
import ilog.concert.IloException;

/**
 *
 * @author marcio
 */
public class GCIObjective extends SingleObjective<GCIFactory, GCICodification, GCIObjective> {

    private static final BoundDbl gci_bounds = new BoundDbl(0, 1e15, 1e-6);
    
    public GCIObjective() throws Exception {
        super(gci_bounds);
    }
    
    @Override
    public void evaluate(GCIFactory prob, GCICodification codif) throws Exception {
        double fitness = 0;
        fitness += CustoDeTroca(prob, codif);
        fitness += CustoExtra(prob, codif);
        fitness += CustoSolver(prob, codif);
        set(fitness);
    }
    @Override
    public GCIObjective build(GCIFactory mem) throws Exception {
        return new GCIObjective();
    }
    public double CustoDeTroca(GCIFactory prob, GCICodification codif){
        double CustoTroca = 0;
        for (int t = 1; t < codif.te; t++) {
            for(int k=0; k<prob.inst.K; k++){
                int i = codif.cromo[k][t-1];
                int j = codif.cromo[k][t];
                CustoTroca += prob.inst.Cijk[i][j][k];
            }
        }
        return CustoTroca;
    }
    public double CustoExtra(GCIFactory prob, GCICodification codif){
        double CustoExtra = 0;
        for (int t = 1; t < codif.te; t++) {
            for(int k=0; k<prob.inst.K; k++){
                int i = codif.cromo[k][t-1];
                int j = codif.cromo[k][t];
                CustoExtra += prob.inst.PEijtk[i][j][t][k];
            }
        }
        return CustoExtra;
    }
    public double CustoSolver(GCIFactory prob, GCICodification codif) throws Exception{
        double CustoSolver = 1e10;
        try {
            prob.model.init(codif);
            CustoSolver = prob.model.solve(codif);
        } catch (IloException ex) {
            ex.printStackTrace();
        }
        return CustoSolver;
    }
}
