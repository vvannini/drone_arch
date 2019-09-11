/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.pog.problem.Test;

import ProOF.apl.pog.problem.TSP.*;
import ProOF.com.Linker.LinkerApproaches;
import ProOF.com.Linker.LinkerParameters;
import ProOF.com.Linker.LinkerResults;
import ProOF.gen.best.BestSol;
import ProOF.opt.abst.problem.meta.Objective;
import ProOF.opt.abst.problem.meta.Problem;
import ProOF.opt.abst.problem.meta.codification.Codification;
import java.io.File;


/**
 *
 * @author marcio
 */
public class TestFactory extends Problem<BestSol>{
    @Override
    public String name() {
        return "MLCLSP";
    }
    @Override
    public String description() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    @Override
    public void parameters(LinkerParameters win) throws Exception {
        File file = win.File("Instances for MLCLSP",null,"inst");
    }
    @Override
    public Codification build_codif() throws Exception {
        return new TestCodification(this);
    }
    @Override
    public Objective build_obj() throws Exception {
        return new TestObjective();
    }

    @Override
    public void services(LinkerApproaches link) throws Exception {
        super.services(link);
        link.add(TestOperator.obj);
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
