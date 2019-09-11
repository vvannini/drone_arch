/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.sample2.problem.cplex;

import ProOF.apl.sample1.problem.TSP.TSPInstance;
import ProOF.com.Linker.LinkerApproaches;
import ProOF.CplexOpt.CplexFull;
import ilog.concert.IloException;
import ilog.concert.IloIntVar;
import ilog.concert.IloNumExpr;
import ilog.concert.IloNumVar;

/**
 *
 * @author marcio
 */
public class TSPFull extends CplexFull{

    private TSPInstance inst = new TSPInstance();
    private IloIntVar Xij[][];
    private IloNumVar Ui[];
    
    
    public TSPFull() throws IloException {
    }

    @Override
    public void services(LinkerApproaches link) throws Exception {
        super.services(link);
        inst = link.add(inst);
    }
    
    @Override
    public String name() {
        return "TSP";
    }
    @Override
    public void model() throws Exception {
        Xij = boolVarArray(inst.N, inst.N, "Xij");
        Ui  = numVarArray(inst.N-1, 0, inst.N*2, "Ui");
        
        for(int i=0; i<inst.N; i++){
            Xij[i][i].setMin(0);
            Xij[i][i].setMax(0);
        }
        
        IloNumExpr sum = null;
        for(int i=0; i<inst.N; i++){
            for(int j=0; j<inst.N; j++){
                sum = sum_prod(sum, inst.Cij[i][j], Xij[i][j]);
            }
        }
        cpx.addMinimize(sum);
        
        for(int j=0; j<inst.N; j++){
            sum = null;
            for(int i=0; i<inst.N; i++){
                sum = sum_prod(sum, 1, Xij[i][j]);
            }
            cpx.addEq(sum, 1, "Col["+(j+1)+"]");
        }
        for(int i=0; i<inst.N; i++){
            sum = null;
            for(int j=0; j<inst.N; j++){
                sum = sum_prod(sum, 1, Xij[i][j]);
            }
            cpx.addEq(sum, 1, "Row["+(i+1)+"]");
        }        
        for(int i=0; i<inst.N-1; i++){
            for(int j=0; j<inst.N-1; j++){
                if(i!=j){
                    IloNumExpr aux[] = new IloNumExpr[3];
                    aux[0] = prod(+1, Ui[i]);
                    aux[1] = prod(-1, Ui[j]);
                    aux[2] = prod(inst.N, Xij[i][j]);
                    cpx.addLe(cpx.sum(aux), inst.N-1);
                }
            }
        }
        
        //cpx.exportModel("../../../TSP.lp");
    }

    
}
