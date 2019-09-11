package ProOF.CplexOpt;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import ProOF.CplexExtended.CplexExtended;
import ProOF.CplexExtended.IncumbentBest;
import ProOF.com.Communication;
import ProOF.com.Linker.LinkerApproaches;
import ProOF.com.Linker.LinkerParameters;
import ProOF.com.Linker.LinkerResults;
import ProOF.com.Linker.LinkerValidations;
import ProOF.com.Stream.StreamPrinter;
import ProOF.com.Stream.StreamProgress;
import ProOF.com.language.Approach;
import ProOF.utilities.uTime;
import ProOF.utilities.uTimeNano;
import ilog.concert.IloException;
import ilog.concert.IloIntVar;
import ilog.concert.IloNumExpr;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;
import ilog.cplex.IloCplex.Status;
import java.util.LinkedList;

/**
 *
 * @author marcio
 */
public abstract class CplexFull extends Approach{

    public final CplexExtended cpx;
    private StreamProgress progress;
    private StreamPrinter output;
    
    private int Threads;
    private double TiLim;
    private int HistoryTime;
    private long NodesLim;
    private double EpGap; 
    private double WorkMem;
    private int NodeSel;
    private int RootAlg;
    private int MIPEmphasis;
    
    private boolean print_war;
    private boolean print_out;
    
    public abstract void model() throws Exception;
    
    public CplexFull() throws IloException {
        cpx = new CplexExtended();
    }
    @Override
    public String description() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    @Override
    public void services(LinkerApproaches link) throws Exception {
        
    }
    @Override
    public void parameters(LinkerParameters link) throws Exception {
        Threads = link.Int("Threads", 1, 0, 32);
        TiLim = link.Dbl("TiLim", 3600, 1e-3, 1e5);
        HistoryTime = link.Int("HistoryTime", 0, 0, 10000);
        NodesLim = link.Long("ItLim", 1000000000l, 1, 1000000000000l);
        EpGap = link.Dbl("EpGap", 1e-4, 1e-9, 1);
        WorkMem = link.Dbl("WorkMem(MB)", 128, 1e-6, 1e6);
        
        NodeSel = link.Itens("NodeSel", 2, new String[]{"Depth-first", "BestBound", "BestEst", "BestEstAlt"});
        RootAlg = link.Itens("RootAlg", 0, new String[]{"AutoAlg", "Primal", "Dual", "Network", "Barrier", "Sifting", "Concurrent"});
        MIPEmphasis = link.Itens("MIPEmphasis", 0, new String[]{"Balanced", "Feasibility", "Optimality", "BestBound", "HiddenFeas"});
        
        print_war = link.Bool("warning", false);
        print_out = link.Bool("output", true);
    }
    
    private double timeHistory;
    private LinkedList<Double> listUB = new LinkedList<Double>();
    private LinkedList<Double> listLB = new LinkedList<Double>();
    
    private double time_best;
    
    private final uTime elapsed_time = new uTimeNano();

    @Override
    public void load() throws Exception {
        progress = Communication.mkProgress("cplex progress");
        output = Communication.mkPrinter("bests");
    }
    @Override
    public void start() throws Exception {
        elapsed_time.start();
        
        timeHistory = 0;
        listUB.add(Double.POSITIVE_INFINITY);
        listLB.add(Double.NEGATIVE_INFINITY);
        
        if(!print_war) cpx.setWarning(null);
        if(!print_out) cpx.setOut(null);
        
        if(Threads==0){
            cpx.setParam(IloCplex.IntParam.Threads, Runtime.getRuntime().availableProcessors());
        }else{
            cpx.setParam(IloCplex.IntParam.Threads, Threads);
        }
        
        cpx.setParam(IloCplex.DoubleParam.TiLim, TiLim);
        cpx.setParam(IloCplex.LongParam.NodeLim, NodesLim);
        cpx.setParam(IloCplex.DoubleParam.EpGap, EpGap);

        cpx.setParam(IloCplex.IntParam.NodeSel, NodeSel);
        
        cpx.setParam(IloCplex.IntParam.RootAlg, RootAlg);
        cpx.setParam(IloCplex.IntParam.NodeAlg, RootAlg);
        cpx.setParam(IloCplex.IntParam.MIPEmphasis, MIPEmphasis);
        //cpx.setParam(IloCplex.IntParam.NodeFileInd, 3);
        
        cpx.setParam(IloCplex.BooleanParam.MemoryEmphasis, true);
        cpx.setParam(IloCplex.DoubleParam.WorkMem, WorkMem);
        cpx.setParam(IloCplex.StringParam.WorkDir, "./");
        
        
        
        cpx.use(new IncumbentBest() {
            @Override
            public void main() throws IloException {
                time_best = elapsed_time.time();
                long nodes = getNnodes64();
                progress.progress(Math.max(time_best/TiLim, nodes*1.0/NodesLim));  
                
                double gap = (getObjValue()-getBestObjValue())/ (1e-10 + Math.abs(getObjValue())); 
                
                output.printDbl("gap(%)", gap*100);
                output.printLong("nodes", nodes);
                output.printDbl("time", time_best);
                output.printDbl("objective", getObjValue());
                output.printDbl("lower", getBestObjValue());
                //output.printDbl("incubent", getIncumbentObjValue());
                output.flush();
                
                if(HistoryTime>0){
                    if( elapsed_time.time() <= timeHistory + HistoryTime){
                        listUB.addLast(Math.min(listUB.removeLast(), getObjValue()));
                    }else{
                        timeHistory += HistoryTime;
                    }
                }
            }
        });
        
        cpx.use(new IloCplex.MIPInfoCallback() {
            @Override
            protected void main() throws IloException {
                double cur_time = elapsed_time.time();
                progress.progress(Math.max(cur_time/TiLim, getNnodes64()*1.0/NodesLim));
                if(HistoryTime>0){
                    if( elapsed_time.time() > timeHistory + HistoryTime){
                        
                        listUB.addLast(listUB.getLast());
                        listLB.addLast(getBestObjValue());
                        timeHistory += HistoryTime;
                    }else if(listLB.getLast() < getBestObjValue()){
                        listLB.addLast(Math.max(listLB.removeLast(), getBestObjValue()));
                    }
                }
            }
        }); 
    }
    @Override
    public boolean validation(LinkerValidations link) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    
    public void solve() throws Exception{
        model();
        if(cpx.solve()){
            print();
        }
    }
    public void print() throws Exception{
        
    }
    
    @Override
    public void results(LinkerResults link) throws Exception {
        link.writeString("status", cpx.getStatus().toString());
        if(cpx.getStatus() == Status.Optimal || cpx.getStatus() == Status.Feasible){
            link.writeDbl("objective", cpx.getObjValue());
            link.writeDbl("lower", cpx.getBestObjValue());
            link.writeInt("bin-vars", cpx.getNbinVars());
            link.writeInt("n-cols", cpx.getNcols());
            link.writeInt("n-rows", cpx.getNrows());
            link.writeLong("n-iterations", cpx.getNiterations64());
            link.writeInt("node-best", cpx.getIncumbentNode());
            link.writeInt("node-total", cpx.getNnodes());
            link.writeDbl("time-best", time_best);
            link.writeDbl("time-tot", elapsed_time.time());
            
            if(HistoryTime>0){
                link.writeArray("HistoryCpx", "UB", listUB.toArray(new Double[listUB.size()]));
                link.writeArray("HistoryCpx", "LB", listLB.toArray(new Double[listLB.size()]));
            }
        }
    }
    
    
    public IloNumExpr prod(double v, IloNumExpr e) throws IloException {
        return cpx.prod(e, v);
    }
    public IloNumExpr sum(IloNumExpr e1, IloNumExpr e2) throws IloException {
        return cpx.sum(e1, e2);
    }
    public IloNumExpr sum_prod(IloNumExpr sum, double v, IloNumExpr e) throws IloException{
        if(sum==null){
            sum = cpx.prod(v, e);
        }else{
            sum = cpx.sum(sum, cpx.prod(v, e));
        }
        return sum;
    }
    
    
    public IloNumVar[] numVarArray(int d1, double min, double max, String name) throws IloException{
        return cpx.numVarArray(d1, min, max, name);
    }
    public IloNumVar[][] numVarArray(int d1, int d2, double min, double max, String name) throws IloException{
        return cpx.numVarArray(d1, d2, min, max, name);
    }
    public IloNumVar[][][] numVarArray(int d1, int d2, int d3, double min, double max, String name) throws IloException{
        return cpx.numVarArray(d1, d2, d3, min, max, name);
    }
    public IloNumVar[][][][] numVarArray(int d1, int d2, int d3, int d4, double min, double max, String name) throws IloException{
        return cpx.numVarArray(d1, d2, d3, d4, min, max, name);
    }
    public IloNumVar[][][][][] numVarArray(int d1, int d2, int d3, int d4, int d5, double min, double max, String name) throws IloException{
        return cpx.numVarArray(d1, d2, d3, d4, d5, min, max, name);
    }
    public IloIntVar[] intVarArray(int d1, int min, int max, String name) throws IloException{
        return cpx.intVarArray(d1, min, max, name);
    }
    public IloIntVar[][] intVarArray(int d1, int d2, int min, int max, String name) throws IloException{
        return cpx.intVarArray(d1, d2, min, max, name);
    }
    public IloIntVar[][][] intVarArray(int d1, int d2, int d3, int min, int max, String name) throws IloException{
        return cpx.intVarArray(d1, d2, d3, min, max, name);
    }
    public IloIntVar[][][][] intVarArray(int d1, int d2, int d3, int d4, int min, int max, String name) throws IloException{
        return cpx.intVarArray(d1, d2, d3, d4, min, max, name);
    }
    public IloIntVar[] boolVarArray(int d1, String name) throws IloException{
        return cpx.boolVarArray(d1, name);
    }
    public IloIntVar[][] boolVarArray(int d1, int d2, String name) throws IloException{
        return cpx.boolVarArray(d1, d2, name);
    }
    public IloIntVar[][][] boolVarArray(int d1, int d2, int d3, String name) throws IloException{
        return cpx.boolVarArray(d1, d2, d3, name);
    }
    public IloIntVar[][][][] boolVarArray(int d1, int d2, int d3, int d4, String name) throws IloException{
        return cpx.boolVarArray(d1, d2, d3, d4, name);
    }
    public IloIntVar[][][][][] boolVarArray(int d1, int d2, int d3, int d4, int d5, String name) throws IloException{
        return cpx.boolVarArray(d1, d2, d3, d4, d5, name);
    }

    public IloNumVar[][] numVarArray(int d1, int d2, double min, double max) throws IloException{
        return cpx.numVarArray(d1, d2, min, max);
    }
    public IloNumVar[][][] numVarArray(int d1, int d2, int d3, double min, double max) throws IloException{
        return cpx.numVarArray(d1, d2, d3, min, max);
    }
    public IloNumVar[][][][] numVarArray(int d1, int d2, int d3, int d4,double min, double max) throws IloException{
        return cpx.numVarArray(d1, d2, d3, d4, min, max);
    }
    public IloNumVar[][][][][] numVarArray(int d1, int d2, int d3, int d4, int d5, double min, double max) throws IloException{
        return cpx.numVarArray(d1, d2, d3, d4, d5, min, max);
    }
        public double[][] getValues(IloNumVar V[][]) throws IloCplex.UnknownObjectException, IloException{
        return cpx.getValues(V);
    }
    public double[][][] getValues(IloNumVar V[][][]) throws IloCplex.UnknownObjectException, IloException{
        return cpx.getValues(V);
    }
    public double[][][][] getValues(IloNumVar V[][][][]) throws IloCplex.UnknownObjectException, IloException{
        return cpx.getValues(V);
    }
    public IloNumExpr Sum(IloNumExpr M[]) throws IloException{
        return cpx.Sum(M);
    }
    public IloNumExpr Sum(IloNumExpr M[][]) throws IloException{
        return cpx.Sum(M);
    }
    public IloNumExpr Sum(IloNumExpr M[][][]) throws IloException{
        return cpx.Sum(M);
    }
    public IloNumExpr Sum(IloNumExpr M[][][][]) throws IloException{
        return cpx.Sum(M);
    }

    public void addSubject(Object ...obj) throws IloException{
        cpx.addSubject(obj);
    }
}
