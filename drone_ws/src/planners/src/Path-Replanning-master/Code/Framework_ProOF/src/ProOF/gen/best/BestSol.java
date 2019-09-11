/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.gen.best;

import ProOF.com.Communication;
import ProOF.com.Linker.LinkerApproaches;
import ProOF.com.Linker.LinkerParameters;
import ProOF.com.Linker.LinkerResults;
import ProOF.com.Stream.StreamPrinter;
import ProOF.com.runner.ExceptionForceFinish;
import ProOF.gen.stopping.Stop;
import ProOF.opt.abst.problem.meta.Best;
import ProOF.opt.abst.problem.meta.Problem;
import ProOF.opt.abst.problem.meta.Solution;
import ProOF.utilities.uTime;
import ProOF.utilities.uTimeMilli;

/**
 *
 * @author marcio
 */
public class BestSol extends Best{
    private static BestSol obj = null;
    public static BestSol object(){
        if(obj==null){
            obj = new BestSol();
        }
        return obj;
    }

    private class Sol{
        private Solution sol;
        private long eval;
        private double sol_time;
        private double refresh;

        public Sol(Solution sol, long eval, double sol_time) {
            this.sol = sol;
            this.eval = eval;
            this.sol_time = sol_time;
            this.refresh = time.time();
        }
    }
    

    private Sol best;
    private long cout;
    private final uTime time = new uTimeMilli();
    
    private final nEvaluations cont_eval = nEvaluations.object();
    
    public Stop stop;
    private StreamPrinter printer;
    
    private double refresh_time;
    
    private double time_force = -1;
    private boolean print_buffer = false;
    
    
    @Override
    public String name() {
        return "BestSol";
    }
    @Override
    public String description() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Solution ind() throws Exception {
        return best.sol;
    }
    @Override
    public long id(){
        return best.eval;
    }
    @Override
    public void services(LinkerApproaches link) throws Exception {
        link.add(cont_eval);
        stop = link.need(Stop.class, stop);
    }
    @Override
    public void parameters(LinkerParameters link) throws Exception {
        refresh_time = link.Dbl("refresh time", 0.1, 0, 60, "tempo dado entre atualizacoes dos prints");
    }
    @Override
    public void load() throws Exception {
        printer = Communication.mkPrinter("bests");
    }
    @Override
    public void start() throws Exception {
        time.start();
        cout = 0;
        best = new Sol(null, 0, 0);
    }
    @Override
    public void results(Problem prob, LinkerResults link) throws Exception {
        link.writeLong("eval tot", cont_eval.value());
        link.writeLong("eval best", best.eval);
        link.writeDbl("time tot", time_force>0 ? time_force : time_now());
        link.writeDbl("time best", time_best());
        //link.writeDbl("time after", time_after());
        if(best.sol!=null){
            best.sol.obj().results(prob, link, best.sol.codif());
            best.sol.codif().resulter(prob, link);
        }
    }
    
    @Override
    public void better(Problem prob, Solution sol) throws Exception {
        //best = best==null ? sol : stop.evaluate() ? best : best.minimum(sol);
        cont_eval.update();
        
        if(stop==null || !stop.end()){
            if( best.sol==null || sol.LT(best.sol) ){
                best.sol = sol.clone(prob);
                best.eval = cont_eval.value();
                best.sol_time = time_now();
                if(cout<2 || best.sol_time>best.refresh+refresh_time){
                    print_best(prob);
                    print_buffer = false;
                }else{
                    print_buffer = true;
                }
            }
        }else if(force_finish){
            time_force = time_now();
            throw new ExceptionForceFinish();
        }
    }
    private void print_best(Problem prob) throws Exception{
        best.refresh = time_now();
        printer.printLong("eval", best.eval);
        printer.printDbl("time", time_best());
        best.sol.printer(prob, printer);
        printer.flush();
        cout++;
    }
    @Override
    public void flush(Problem prob) throws Exception {
        if(print_buffer){
            print_best(prob);
            print_buffer = false;
        }
    }
    @Override
    public double time_now() {
        return time.time();
    }
    @Override
    public double time_best() {
        return best.sol_time;
    }
    @Override
    public double time_after() {
        return time_now()-time_best();
    }
}
