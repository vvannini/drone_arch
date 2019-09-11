/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.pog.problem.RA.Codif1;
import ProOF.apl.pog.problem.RA.model.MethodResult;
import ProOF.apl.pog.problem.RA.model.RAInstance;
import ProOF.apl.pog.problem.RA.sequencia.aRASequencia;
import ProOF.apl.pog.problem.RA.sequencia.fRASequencia;
import ProOF.apl.pog.problem.RA.solvers.aRASolver;
import ProOF.apl.pog.problem.RA.solvers.fRASolver;
import ProOF.apl.pog.problem.RA.solvers.iRAModel;
import ProOF.com.Linker.*;
import ProOF.gen.best.BestSol;
import ProOF.opt.abst.problem.meta.Best;
import ProOF.opt.abst.problem.meta.Objective;
import ProOF.opt.abst.problem.meta.Problem;
import ProOF.opt.abst.problem.meta.codification.Codification;

/**
 *
 * @author marcio
 */
public class RAProblem1 extends Problem {

    public boolean repetidos[];
    
    public aRASolver solver;
    public aRASequencia seq;
    public RAInstance inst;
    
    private iRAModel modelSolver;
    private MethodResult result;

    public RAProblem1() {
        this.modelSolver = new iRAModel();
        this.result = new MethodResult();
    }
    
    @Override
    public void start() throws Exception {
        repetidos = new boolean[inst.N];
    }

    @Override
    public void parameters(LinkerParameters win) throws Exception {
    }

    @Override
    public void services(LinkerApproaches win) throws Exception {
        super.services(win);
        solver = win.get(fRASolver.obj, solver);        
        this.inst = win.add(RAInstance.obj);
        this.seq = win.get(fRASequencia.obj, seq);
        win.add(fRAOperators1.obj);
        win.add(modelSolver);
        win.add(result);
    }

    @Override
    public String name() {
        return "PSSLSAN-1";
    }

    @Override
    public String description() {
        return "Racao animal";
    }

    @Override
    public Codification build_codif() throws Exception {
        return new RACodification1(this);
    }

    @Override
    public Objective build_obj() throws Exception {
        return new RAObjective1();
    }

    @Override
    public Best best() {
        return BestSol.object();
    }

    @Override
    public void finish() throws Exception {
        super.finish();
        RACodification1 best = (RACodification1) best().ind().codif();
        double obj;
        double tempoSetup = seq.tempoSetup(best.Yt);
        int Wit[][] = new int[inst.N][inst.T];
        for(int t = 0; t < inst.T; t++){
            for(int  i : best.Yt[t]){
                Wit[i][t]=1; 
            }
        }
        obj = solver.solve(Wit, seq.STt);
        System.out.println("Resultado Greedy:" + obj);
        obj = modelSolver.solve(Wit, seq.STt);
        modelSolver.export(result);
        result.setYt(best.Yt);
        System.out.println("Resultado model:" + obj);
    }
    
    @Override
    public void results(LinkerResults com) throws Exception {
        super.results(com);      
        
    }
    
    
}
