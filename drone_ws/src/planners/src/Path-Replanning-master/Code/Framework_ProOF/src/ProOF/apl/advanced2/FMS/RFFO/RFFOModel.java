/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.advanced2.FMS.RFFO;

import ProOF.com.Linker.LinkerApproaches;
import ProOF.com.Linker.LinkerParameters;
import ProOF.com.Linker.LinkerResults;
import ProOF.com.Linker.LinkerValidations;
import ProOF.com.language.Approach;
import ProOF.CplexExtended.CplexExtended;
import ProOF.utilities.uDataDown;
import ProOF.utilities.uDataUp;
import ProOF.utilities.uSort;
import ilog.concert.IloException;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 *
 * @author marcio
 */
public abstract class RFFOModel extends Approach{
    public static final int STEP_RF = 0; 
    public static final int STEP_FO = 1; 
    public static final int STEP_FULL = 2;
    
    public CplexExtended cpx;
    private int iteration;
    
    private int Threads;
    private int HistoryTime;
    private int rfInc;
    private long RFNodesLim;
    private long FONodesLim;
    
    private double EpGap; 
    private double WorkMem;
    private int NodeSel;
    private int RootAlg;
    private int MIPEmphasis;
    
    private boolean print_war;
    private boolean print_out;
    
    protected String status;
    
    public RFFOModel() {
        status = "empty";
    }
    public void setStatus(String status) {
        this.status = status;
    }
    @Override
    public String description() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    @Override
    public void services(LinkerApproaches link) throws Exception {
        
    }
    @Override
    public void parameters(LinkerParameters link) throws Exception {
        
        Threads = link.Int("Threads", 1, 0, 32);
        HistoryTime = link.Int("HistoryTime", 0, 0, 10000);
        rfInc = link.Int("rf-Inc", 10, 1, Integer.MAX_VALUE);
        
        RFNodesLim = link.Long("NodeLim(RF)", 1000, 1, 1000000000000l);
        FONodesLim = link.Long("NodeLim(FO)", 2000, 1, 1000000000000l);
        
        EpGap = link.Dbl("EpGap", 1e-4, 1e-9, 1);
        WorkMem = link.Dbl("WorkMem(MB)", 128, 1e-6, 1e6);
        
        NodeSel = link.Itens("NodeSel", 2, new String[]{"Depth-first", "BestBound", "BestEst", "BestEstAlt"});
        RootAlg = link.Itens("RootAlg", 0, new String[]{"AutoAlg", "Primal", "Dual", "Network", "Barrier", "Sifting", "Concurrent"});
        MIPEmphasis = link.Itens("MIPEmphasis", 0, new String[]{"Balanced", "Feasibility", "Optimality", "BestBound", "HiddenFeas"});
        
        print_war = link.Bool("warning", true);
        print_out = link.Bool("output", true);
    }
    
    
    @Override
    public void load() throws Exception {
        
    }
    private double timeHistory;
    private LinkedList<Double> listUB = new LinkedList<Double>();
    
    @Override
    public void start() throws Exception {
        final long initial = System.nanoTime();
        iteration = 0;
        cpx = new CplexExtended();
        timeHistory = 0;
        listUB.add(Double.POSITIVE_INFINITY);
        
        if(!print_war) cpx.setWarning(null);
        if(!print_out) cpx.setOut(null);
        
        if(Threads==0){
            cpx.setParam(IloCplex.IntParam.Threads, Runtime.getRuntime().availableProcessors());
        }else{
            cpx.setParam(IloCplex.IntParam.Threads, Threads);
        }
        cpx.setParam(IloCplex.DoubleParam.EpGap, EpGap);

        cpx.setParam(IloCplex.IntParam.NodeSel, NodeSel);
        
        cpx.setParam(IloCplex.IntParam.RootAlg, RootAlg);
        cpx.setParam(IloCplex.IntParam.NodeAlg, RootAlg);
        
        cpx.setParam(IloCplex.IntParam.MIPEmphasis, MIPEmphasis);
        cpx.setParam(IloCplex.IntParam.NodeFileInd, 3);
        
        cpx.setParam(IloCplex.BooleanParam.MemoryEmphasis, true);
        cpx.setParam(IloCplex.DoubleParam.WorkMem, WorkMem);
        cpx.setParam(IloCplex.StringParam.WorkDir, "./");
        if(HistoryTime>0){
            cpx.use(new IloCplex.IncumbentCallback() {
                @Override
                protected void main() throws IloException {
                    if(status.equals("Fix And Opt")){
                        while((System.nanoTime() - initial)/1e9 > timeHistory + HistoryTime){
                            listUB.addLast(listUB.getLast());
                            timeHistory += HistoryTime;
                        }
                        if( (System.nanoTime() - initial)/1e9 <= timeHistory + HistoryTime){
                            listUB.addLast(Math.min(listUB.removeLast(), getObjValue()));
                        }
                    }
                }
            });
        }
        model();
    }
    @Override
    public boolean validation(LinkerValidations link) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    @Override
    public void results(LinkerResults link) throws Exception {
        link.writeInt("solver-iteration", iteration);
        link.writeInt("var-fixed", fixed());
        link.writeInt("var-free", converted()-fixed());
        link.writeInt("var-relax", size()-converted());
        link.writeString("RFFO-Status", cpx.getStatus().toString());
        if(HistoryTime>0){
            link.writeArray("HistoryRFFO", "UB", listUB.toArray(new Double[listUB.size()]));
        }
        if(cpx.getStatus() == IloCplex.Status.Optimal || cpx.getStatus() == IloCplex.Status.Feasible){
            link.writeDbl("RFFO-Obj Value", cpx.getObjValue());
            link.writeDbl("RFFO-Obj Lower", cpx.getBestObjValue());
            print();
        }
    }

    public abstract ArrayList<RelaxVar> relax_variables() throws Exception;
    public abstract ArrayList<RelaxVar> fix_variables() throws Exception;
    public abstract void extra_conversion() throws IloException ;
    protected abstract double getLB(int i);
    protected abstract double getUB(int i);
    public abstract int size();
    protected abstract double round(double value);
    
    public abstract void model() throws Exception;
    public abstract void print() throws Exception;

    
    public double cost() throws IloException {
        return cpx.getObjValue();
    }
    public double gap() throws IloException {
        if(cpx.getBestObjValue()>cpx.getObjValue()){
            return 0.0;
        }
        return (cpx.getObjValue()-cpx.getBestObjValue())*100.0/(Math.abs(cpx.getObjValue()) + 1e-10);
    }
    
    public void solve(final double time, ArrayList<RelaxVar> var) throws IloException, Exception {
        long t0 = System.currentTimeMillis();
        if(status.equals("Relax And Fix")){
            cpx.setParam(IloCplex.LongParam.NodeLim, RFNodesLim);
        }else if(status.equals("Fix And Opt")){
            cpx.setParam(IloCplex.LongParam.NodeLim, FONodesLim);
        }else{
            throw new Exception("unknown status = "+status);
        }
        do{
            cpx.setParam(IloCplex.DoubleParam.TiLim, Math.max(time-(System.currentTimeMillis()-t0)/1000.0, 10.0));
            if(cpx.solve()){
                iteration++;
                break;
            }else if(cpx.getStatus().equals(IloCplex.Status.Unknown)){
                //continue
                System.out.println("Unknown status, try again");
            }else if(cpx.getStatus().equals(IloCplex.Status.Infeasible)){
                //continue
                System.out.println("Infeasible status, free "+rfInc+" fixed variables and trying again");
                int count = rfInc;
                for(int i=var.size()-1; i>=0 && count>0; i--){
                    if(fixed.contains(var.get(i).var)){
                        var.get(i).var.setLB(getLB(i));
                        var.get(i).var.setUB(getUB(i));
                        fixed.remove(var.get(i).var);
                        count--;
                    }
                }
            }else{
                throw new Exception("solve fail, status = "+cpx.getStatus().toString());
            }
            if(time-(System.currentTimeMillis()-t0)/1000.0 < 1.0){
                throw new Exception("stop by time");
            }
        }while(true);
    }
    public void solve_full(double time) throws IloException, Exception {
        cpx.setParam(IloCplex.DoubleParam.TiLim, Math.max(1, time));
        if(cpx.solve()){
            iteration++;
        }else{
            throw new Exception("solve fail");
        }
    }
    public void print2(ArrayList<RelaxVar> var) throws IloException{
        System.out.printf("------------------------------[%d]------------------------------\n", iteration);
        System.out.printf("cost = %20g\n", cost());
        System.out.printf("sol  = [ \n");
        
        uDataUp R[] = new uDataUp[var.size()];
        for(int j=0; j<var.size(); j++){
            R[j] = new uDataUp(var.get(j), var.get(j).var.getName());
        }
        uSort.sort(R);
        int i=0;
        for(uDataUp<RelaxVar> v : R){
            if(fixed.contains(v.data.var)){
                System.out.printf("(%8s %8.5f %s) ", v.data.var, (cpx.getValue(v.data.var)), "$");
            }else if(converted.contains(v.data.var)){
                System.out.printf("(%8s %8.5f %s) ", v.data.var, (cpx.getValue(v.data.var)), "*");
            }else{
                System.out.printf("(%8s %8.5f %s) ", v.data.var, cpx.getValue(v.data.var), " ");
            }

            if(++i % 4 == 0){
                System.out.println();
            }
        }
        System.out.println("]");
        //print();
    }
    private final ArrayList<IloNumVar> converted = new ArrayList<IloNumVar>();
    public final int converted() {
        return converted.size();
    }
    public void conversion(ArrayList<RelaxVar> var, int N) throws IloException {
        for(int i=0; i<var.size() && N>0; i++){
            if(!converted.contains(var.get(i).var)){
                var.get(i).convert(cpx);
                converted.add(var.get(i).var);
                N--;
            }
        }
    }
    public ArrayList<RelaxVar> value_wise_nearRef(ArrayList<RelaxVar> var, double ref) throws IloException {
        ArrayList<Double> sol = solution(var);
        
        uDataDown<RelaxVar> R[] = new uDataDown[sol.size()];
        for(int i=0; i<R.length; i++){
            R[i] = new uDataDown<RelaxVar>(var.get(i), Math.abs(sol.get(i)-ref)); 
        }
        uSort.sort(R);
        ArrayList<RelaxVar> new_var = new ArrayList<RelaxVar>();
        for(uDataDown<RelaxVar> d : R){
            new_var.add(d.data);
        }
        return new_var;
    }
    public ArrayList<RelaxVar> value_wise_distRef(ArrayList<RelaxVar> var, double ref) throws IloException {
        ArrayList<Double> sol = solution(var);
        
        uDataUp<RelaxVar> R[] = new uDataUp[sol.size()];
        for(int i=0; i<R.length; i++){
            R[i] = new uDataUp<RelaxVar>(var.get(i), Math.abs(sol.get(i)-ref)); 
        }
        uSort.sort(R);
        ArrayList<RelaxVar> new_var = new ArrayList<RelaxVar>();
        for(uDataUp<RelaxVar> d : R){
            new_var.add(d.data);
        }
        return new_var;
    }
    
    public ArrayList<Double> solution(ArrayList<RelaxVar> var) throws IloException{
        ArrayList<Double> sol = new ArrayList<Double>();
        for(RelaxVar v : var){
            if(converted.contains(v.var)){
                sol.add(round(cpx.getValue(v.var)));
            }else{
                sol.add(cpx.getValue(v.var));
            }
        }
        return sol;
    }
    private final ArrayList<IloNumVar> fixed = new ArrayList<IloNumVar>();
    public final int fixed() {
        return fixed.size();
    }
    public void fix(ArrayList<RelaxVar> var, int N) throws IloException {
        ArrayList<Double> sol = solution(var);
        
        for(int i=0; i<var.size() && N>0; i++){
            if(!fixed.contains(var.get(i).var)){
                var.get(i).var.setLB(sol.get(i));
                var.get(i).var.setUB(sol.get(i));
                fixed.add(var.get(i).var);
                N--;
            }
        }
    }
    
    private ArrayList<IloNumVar> free = new ArrayList<IloNumVar>();
    public final int free() {
        return free.size();
    }
    public void moveWindow(ArrayList<RelaxVar> var, int wFreeFrom, int wFreeTo) throws IloException {
        ArrayList<Double> sol = solution(var);
        
        for(int i=0; i<var.size(); i++){    //fix all
            var.get(i).var.setLB(sol.get(i));
            var.get(i).var.setUB(sol.get(i));
        }
        
        free.clear();
        int i = wFreeFrom;
        int max = 0;
        while(wFreeFrom<wFreeTo && max < var.size()){
            if(!free.contains(var.get(i).var)){
                var.get(i).var.setLB(getLB(i));
                var.get(i).var.setUB(getUB(i));
                free.add(var.get(i).var);
                wFreeFrom++;
            }
            i = (i+1)%var.size();
            max++;
        }
    }
    public void print3(ArrayList<RelaxVar> var) throws IloException{
        System.out.printf("------------------------------[%d]------------------------------\n", iteration);
        System.out.printf("cost = %20g\n", cost());
        System.out.printf("sol  = [ \n");
        
        uDataUp R[] = new uDataUp[var.size()];
        for(int j=0; j<var.size(); j++){
            R[j] = new uDataUp(var.get(j), var.get(j).var.getName());
        }
        uSort.sort(R);
        int i=0;
        uDataUp<RelaxVar> before = null;
        for(uDataUp<RelaxVar> v : R){
            if(before==null || v.data != before.data){
                if(free.contains(v.data.var)){
                    System.out.printf("(%5s %8.5f %s) ", v.data.var, cpx.getValue(v.data.var), "*");
                }else{
                    System.out.printf("(%5s %8.5f %s) ", v.data.var, cpx.getValue(v.data.var), " ");
                }

                if(++i % 5 == 0){
                    System.out.println();
                }
                
            }
            before = v;
        }
        System.out.println("]");
    }
}
