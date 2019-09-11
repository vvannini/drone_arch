/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.pog.gen.codification.real;
import ProOF.apl.factorys.fRealOperator;
import ProOF.apl.pog.gen.callback.cbCSV;
import ProOF.com.Linker.LinkerApproaches;
import ProOF.gen.codification.Real.cReal;
import ProOF.opt.abst.problem.meta.Best;
import ProOF.opt.abst.problem.meta.MultiProblem;
import ProOF.opt.abst.problem.meta.Problem;
import ProOF.opt.abst.problem.meta.Solution;
import ProOF.opt.abst.problem.meta.codification.Codification;
import ProOF.opt.abst.problem.meta.objective.MultiObjective;
import java.io.PrintStream;

/**
 *
 * @author marcio
 */
public final class pRealMultiObjProblem extends MultiProblem{
    
    public final aRealMultiObj function;
    
    private nCSV call_back;
    
    public pRealMultiObjProblem(aRealMultiObj function) {
        this.function = function;
    }
    @Override
    public String name() {
        return function.name();
    }
    @Override
    public void services(LinkerApproaches link) throws Exception {
        super.services(link);
        link.add(fRealOperator.obj);
        call_back = link.need(nCSV.class, call_back);
    }

    @Override
    public void load() throws Exception {
        call_back.add(new cbCSV() {
            @Override
            public void title(PrintStream out, Object data) throws Exception {
                //System.out.println("title data = "+data.toString());
                if(data instanceof Solution){
                    Solution sol = (Solution) data;
                    if(sol.obj() instanceof MultiObjective){
                        MultiObjective obj = (MultiObjective) sol.obj();
                        for(int f=0; f<obj.goals(); f++){
                            out.printf("f%d;", f+1);
                        }
                    }
                    if(sol.codif() instanceof cReal){
                        cReal codif = (cReal) sol.codif();
                        for(int i=0; i<codif.X.length; i++){
                            out.printf("x%s;", i+1);
                        }
                    }
                }
            }
            @Override
            public void values(PrintStream out, Object data) throws Exception {
                //System.out.println("values data = "+data.toString());
                if(data instanceof Solution){
                    Solution sol = (Solution) data;
                    if(sol.obj() instanceof MultiObjective){
                        MultiObjective obj = (MultiObjective) sol.obj();
                        for(int i=0; i<obj.goals(); i++){
                            out.printf("%g;", obj.abs_value(i));
                        }
                    }
                    if(sol.codif() instanceof cReal){
                        cReal codif = (cReal) sol.codif();
                        for(double x : codif.X){
                            out.printf("%g;", x);
                        }
                    }
                }
            }
        });
    }
    
    @Override
    public Codification build_codif() throws Exception {
        return new cReal(function.size());
    }
    @Override
    public int goals() {
        return function.goals();
    }
    @Override
    public MultiObjective build_obj() throws Exception {
        return new MultiObjective(function.goals(), function.bounds()) {
            @Override
            public void evaluate(Problem mem, Codification cod) throws Exception {
                cReal codif = (cReal) cod;
                for(int i=0; i<function.goals(); i++){
                    set(i, function.F(i,codif.X));
                }
            }
            @Override
            public MultiObjective build(Problem mem) throws Exception {
                return this.getClass().newInstance();
            }
        };
    }

    @Override
    public String description() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Best best(){
        return null;//pBestSol.object();
    }
}
