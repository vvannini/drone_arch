/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.pog.problem.TSP;

import ProOF.com.Linker.LinkerApproaches;
import ProOF.com.Linker.LinkerResults;
import ProOF.gen.best.BestSol;
import ProOF.opt.abst.problem.meta.Objective;
import ProOF.opt.abst.problem.meta.Problem;
import ProOF.opt.abst.problem.meta.codification.Codification;


/**
 *
 * @author marcio
 */
public class TSPFactory extends Problem<BestSol>{
    public final TSPInstance inst = new TSPInstance();
    
    @Override
    public String name() {
        return "TSP";
    }
    @Override
    public String description() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    @Override
    public Codification build_codif() throws Exception {
        return new TSPCodification(this);
    }
    @Override
    public Objective build_obj() throws Exception {
        return new TSPObjective();
    }

    @Override
    public void services(LinkerApproaches link) throws Exception {
        super.services(link);
        link.add(inst);
        link.add(TSPOpInit.obj);
        link.add(TSPOpMut.obj);
        link.add(TSPOpCross.obj);
        link.add(TSPOpMov.obj);
    }
    @Override
    public BestSol best() {
        return BestSol.object();
    }

    @Override
    public void start() throws Exception {
        add_gap("gap", inst.optimal);
    }
    
    @Override
    public void results(LinkerResults com) throws Exception {
        super.results(com);
        com.writeArray("testeA", "int", new int[]{1,2,3,4,5});
        com.writeArray("testeA", "double", new double[]{1.1,2.1,3.1,4.1,5.1,6.1,7.1});
        com.writeArray("testeA", "int|int", new String[]{"1|2","2|3","3|4","4|5","5|6"});
        com.writeArray("testeB", "str", new String[]{"oi","hi", "hello"});
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
