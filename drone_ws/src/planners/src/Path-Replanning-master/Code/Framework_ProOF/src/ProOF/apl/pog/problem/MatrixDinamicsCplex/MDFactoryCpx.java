/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.pog.problem.MatrixDinamicsCplex;

import ProOF.apl.pog.problem.MatrixDinamics.*;
import ProOF.com.Linker.LinkerApproaches;
import ProOF.gen.best.BestSol;
import ProOF.opt.abst.problem.meta.Objective;
import ProOF.opt.abst.problem.meta.Problem;
import ProOF.opt.abst.problem.meta.codification.Codification;


/**
 *
 * @author marcio
 */
public class MDFactoryCpx extends Problem<BestSol>{
    public final MDInstance inst = new MDInstance();
    public aMDHeuristic solver;
    @Override
    public String name() {
        return "MD-Cpx";
    }
    @Override
    public String description() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    @Override
    public Codification build_codif() throws Exception {
        return new MDCodificationCpx(this);
    }
    @Override
    public Objective build_obj() throws Exception {
        return new MDObjectiveCpx();
    }

    @Override
    public void services(LinkerApproaches link) throws Exception {
        super.services(link);
        solver = link.get(fMDHeuristic.obj, solver);        
        link.add(inst);
        link.add(MDOperatorCpx.obj);
    }
    @Override
    public BestSol best() {
        return BestSol.object();
    }
    

    
    /*@Override
    protected ServiceFactory NewOperators() {
        return new ServiceFactory() {
            @Override
            public Service NewService(int index) {
                switch(index){
                    case 0: return new INIT();  
                }
                return null;
            }
        };
    }
    private class INIT extends oInitializer<pMetaProblem, TSPCodification>{
        @Override
        public String name() {
            return "RMD";
        }
        @Override
        public String description() {
            return "inicializa aleatoriamente um ciclo hamiltoniano";
        }
        @Override
        public void initialize(pMetaProblem mem, TSPCodification ind) throws Exception {
            for(int i=0; i<ind.path.length; i++){
                ind.path[i] = i;
            }
            for(int i=0; i<ind.path.length; i++){
                int a =  mem.rmd.nextInt(ind.path.length);
                int b =  mem.rmd.nextInt(ind.path.length);
                int aux = ind.path[a];
                ind.path[a] = ind.path[b];
                ind.path[b] = aux;
            }
        }
    }*/

    



    

    
}
