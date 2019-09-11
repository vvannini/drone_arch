/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.pog.problem.FuncA;
import ProOF.apl.factorys.fRealOperator;
import ProOF.com.Linker.LinkerApproaches;
import ProOF.gen.best.BestSol;
import ProOF.gen.codification.Real.cReal;
import ProOF.opt.abst.problem.meta.Objective;
import ProOF.opt.abst.problem.meta.Problem;
import ProOF.opt.abst.problem.meta.codification.Codification;
import ProOF.opt.abst.problem.meta.objective.SingleObjective;

/**
 *
 * @author marcio
 */
public class FuncAFactory extends Problem<BestSol>{
    @Override
    public String name() {
        return "Fuction-v1";
    }
    @Override
    public Codification build_codif() throws Exception {
        return new cReal(1);
    }
    @Override
    public Objective build_obj() throws Exception {
        return new SingleObjective() {
            @Override
            public void evaluate(Problem mem, Codification cod) throws Exception {
                //Casting
                cReal codif = (cReal) cod;
                //Decoder
                double x = codif.X(0, -1, +2);
                //Evaluate
                double fitness = -(x * Math.sin(10*Math.PI*x)+1);
                set(fitness);
            }
            @Override
            public SingleObjective build(Problem mem) throws Exception {
                return this.getClass().newInstance();
            }
        };
    }
    @Override
    public BestSol best(){
        return BestSol.object();
    }
    @Override
    public void services(LinkerApproaches link) throws Exception {
        super.services(link);
        link.add(fRealOperator.obj);
    }
}