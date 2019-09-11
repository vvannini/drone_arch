/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.UAV.abst;

import ProOF.CplexExtended.IncumbentBest;
import ProOF.CplexExtended.CplexExtended;
import ProOF.CplexExtended.iCplexExtract;
import ProOF.apl.UAV.Swing.Graphics2DReal;
import static ProOF.apl.UAV.abst.UAVApproach.*;
import ProOF.com.Linker.LinkerResults;
import ilog.concert.IloException;
import ilog.cplex.IloCplex;
import ilog.cplex.IloCplex.Status;
import java.awt.Font;


/**
 *
 * @author marcio
 * @param <App>
 */
public abstract class UAVModel<App extends UAVApproach> {
        
    public final String name;
    public final App approach;
    public final CplexExtended cplex;
    public final IncumbentBest inc;
    
    public UAVModel(String name, App app, CplexExtended cplex) throws IloException {
        this.name = name;
        this.approach = app;
        this.cplex = cplex;
        final UAVModel THIS = this;
        this.inc = new IncumbentBest() {
            @Override
            public void main() throws IloException {
                try {
                    time_best = System.nanoTime()/1e9-time_init;
                    approach.solutionCallback(this, THIS, Callback.PARTIAL_INCUMBENT);
                } catch (Throwable ex) {
                    
                }
            }
        };
        this.cplex.use(inc);
    }
    
    public abstract boolean addChanges() throws Exception;
    public abstract void extract(iCplexExtract ext, Callback type) throws Exception;
    public abstract void paint(Graphics2DReal gr, double size) throws Exception;
    public abstract void paint(Graphics2DReal gr, Font font, double size) throws Exception;
    public abstract void save() throws Exception;
            
    public boolean solve(double time, double epGap, int threads, boolean print_war, boolean print_out) throws Exception {
        //Parada por tempo maximo de 10 segundos
        cplex.setParam(IloCplex.DoubleParam.TiLim, time);

        //Parada por gap relativo
        cplex.setParam(IloCplex.DoubleParam.EpGap, epGap);
        
        //Modo de paralelismo, numero de threads
        cplex.setParam(IloCplex.IntParam.Threads, threads);
        
        if(!print_war) cplex.setWarning(null);
        if(!print_out) cplex.setOut(null);
        
        return solve();
    }
    
    private int iteration;
    private double time_init;
    private double time_best;
    private double time_tot;
    private double ti_lim;
    
    private double time_solve(){
        return System.nanoTime()/1e9-time_init;
    }
    
    private double remaining_time(){
        return ti_lim-time_solve();
    }
    
    private Status status;
    
    public boolean solve() throws Exception {
        status = cplex.getStatus();
        
        time_init = System.nanoTime()/1e9;
        ti_lim = cplex.getParam(IloCplex.DoubleParam.TiLim);
        
        System.out.printf("Frist solve '%s', current status = '%s'\n", name, status.toString());
        if(!cplex.solve()) {
            status = cplex.getStatus();
            System.out.printf("Cplex doesn't solve the model, status = '%s'\n", status.toString());
            time_tot = time_solve();
            return false;
        }else{
            if(cplex.getStatus()==Status.Optimal){
                status = Status.Optimal;
                approach.solutionCallback(cplex, this, Callback.PARTIAL_OPTIMAL);
            }else if(cplex.getStatus()==Status.Feasible){
                status = Status.Feasible;
                approach.solutionCallback(cplex, this, Callback.PARTIAL_FEASIBLE);
            }else{
                throw new Exception("Cplex invalid status '"+cplex.getStatus().toString()+"' to solve() = true");
            }
            System.out.printf("Status cplex after solve = '%s' and partial status = '%s'\n", cplex.getStatus().toString(), status.toString());

            iteration = 1;
            while(addChanges()){
                iteration++;
                double remaining_time = remaining_time();
                if(remaining_time>ti_lim*0.05){//I have enough time
                    cplex.setParam(IloCplex.DoubleParam.TiLim, remaining_time);
                    System.out.printf("Again [%d] solve '%s' for more %g time seconds, partial status ='%s'\n", iteration, name, remaining_time, status.toString());
                    if(cplex.solve()){
                        if(cplex.getStatus()==Status.Optimal){
                            if(status==Status.Optimal){ //continue optimal
                                status=Status.Optimal;
                                approach.solutionCallback(cplex, this, Callback.PARTIAL_OPTIMAL);
                            }else if(status==Status.Feasible){//consider feasible
                                status=Status.Feasible;
                                approach.solutionCallback(cplex, this, Callback.PARTIAL_FEASIBLE);
                            }else{
                                throw new Exception("This case can not exist, status = '"+status+"'");
                            }
                        }else if(cplex.getStatus()==Status.Feasible){
                            if(status==Status.Optimal || status==Status.Feasible){ //consider feasible
                                status=Status.Feasible;
                                approach.solutionCallback(cplex, this, Callback.PARTIAL_FEASIBLE);
                            }else{
                                throw new Exception("This case can not exist, status = '"+status+"'");
                            }
                        }else{
                            throw new Exception("Cplex invalid status '"+cplex.getStatus().toString()+"' to solve() = true");
                        }
                        System.out.printf("Status cplex after solve = '%s' and partial status = '%s'\n", cplex.getStatus().toString(), status.toString());
                    }else{
                        status = cplex.getStatus();
                        System.out.printf("Cplex doesn't solve the model, status = '%s'\n", status.toString());
                        break;
                    }
                }else{  //I don't have time to solve again after changes
                    status = Status.Unknown;
                    System.out.printf("Stop [%d] with unknown solution because it doesn't have time to solve '%s' again after changes\n", iteration, name);
                    break;
                }
            }
            time_tot = time_solve();
            if(status == Status.Optimal){
                approach.solutionCallback(cplex, this, Callback.OPTIMAL);
                return true;
            }else if(status == Status.Feasible){
                approach.solutionCallback(cplex, this, Callback.FEASIBLE);
                return true;
            }else{
                return false;
            }
        }
    }

    public final boolean isFeasible() throws IloException {
        if(status==IloCplex.Status.Feasible || status==IloCplex.Status.Optimal){
            //return cplex.getBestObjValue() < 1e9;
            return true;
        }else{
            return false;
        }
    }
    public double upper() throws IloException {
        return cplex.getObjValue();
    }
    public double lower() throws IloException {
        return cplex.getBestObjValue();
    }
    public void results(LinkerResults link) throws Exception {
        link.writeString(name+"-status", status.toString());
        if(isFeasible()){
            link.writeDbl(name+"-objective", cplex.getObjValue());
            link.writeDbl(name+"-lower", cplex.getBestObjValue());
            link.writeDbl(name+"-bin", cplex.getNbinVars());
            link.writeDbl(name+"-cols", cplex.getNcols());
            link.writeDbl(name+"-rows", cplex.getNrows());
            link.writeDbl(name+"-time-best", time_best);
            link.writeDbl(name+"-time-tot", time_tot);
            link.writeInt(name+"-uav-iteration", iteration);
        }
    }
}
