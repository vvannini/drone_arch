
package ProOF.apl.pog.method;

import ProOF.apl.pog.problem.PPDCP.Old.PPDCPInstanceOld;
import ProOF.com.Linker.LinkerApproaches;
import ProOF.com.Linker.LinkerParameters;
import ProOF.com.Linker.LinkerResults;
import ProOF.com.Linker.LinkerValidations;
import ProOF.com.language.Run;
import ProOF.CplexExtended.CplexExtended;
import ilog.concert.IloException;
import ilog.concert.IloIntVar;
import ilog.concert.IloNumExpr;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;

/**
 *
 * @author Hossomi
 */
public class PPDCPFullTrocas extends Run {
    
    
    private double execTime;
    private double epGap;
    private int threads;
    
    private CplexExtended cplex;
    
    
    private IloNumVar Vt[][];  
    /** Vx, Vy*/
    private IloNumVar Ut[][];  
    /** px, vx, py, vy*/
    private IloNumVar MUt[][];
    private IloNumVar Djt[][];
    private IloIntVar Zjti[][][];
    private IloNumVar Ajtik[][][][];
    
    private double time;
    
    private PPDCPInstanceOld inst = new PPDCPInstanceOld();
    
    @Override
    public String name() {
        return "PPDCP-Full-T";
    }

    @Override
    public String description() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    @Override
    public void services(LinkerApproaches com) throws Exception {
        com.add(inst);
    }
    @Override
    public void parameters(LinkerParameters com) throws Exception {
        execTime    = com.Dbl("Time", 3600.0, 1.0, 180000.0);
        epGap       = com.Dbl("Gap Rel", 0.0001, 0.0, 100.0);
        threads     = com.Int("Threads", 1, 0, 16);
        
        execTime  = 11111;
    }
    @Override
    public boolean validation(LinkerValidations com) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    @Override
    public void results(LinkerResults com) throws Exception {
        com.writeDbl("Best Time", time);
        com.writeString("Status", cplex.getStatus().toString());
        com.writeDbl("Obj Value", cplex.getObjValue());
        com.writeDbl("Lower Bound", cplex.getBestObjValue());
        com.writeDbl("Obj Beta", cplex.getValue(ObjBeta));
        com.writeDbl("Obj Delta", cplex.getValue(ObjDelta));
        //------------------------(11)---------------------------
        double vMt[][] = cplex.getValues(MUt);
        double vUt[][] = cplex.getValues(Ut); 
        //------------------------[beta]---------------------------
        double beta = inst.norm2(vMt[inst.T-1], inst.Xgoal);
        
        //------------------------[beta]---------------------------
        double delta = 0;
        for(int j=0; j<inst.J; j++){
            for(int t=0; t<inst.T; t++){
                delta += Delta(inst, j, t, vMt);
            }
        }
        delta = Math.max(0, delta-inst.DELTA);
        
        //------------------------[goal]---------------------------
        double goal = 0;
        for(int t=0; t<inst.T; t++){
            goal += vUt[t][0]*vUt[t][0]+vUt[t][1]*vUt[t][1];
        }
        
        double fitness = beta*inst.P1 + delta*inst.P2  + goal;
        com.writeDbl("fitness", fitness);
        com.writeDbl("beta", beta*inst.P1);
        com.writeDbl("delta", delta*inst.P2);
        double M[][] = inst.trans(vMt);
        com.writeArray("PPDCP", "fMt[0]", M[0]);
        com.writeArray("PPDCP", "fMt[1]", M[1]);
        com.writeArray("PPDCP", "fMt[2]", M[2]);
        com.writeArray("PPDCP", "fMt[3]", M[3]);
        double U[][] = inst.trans(vUt);
        com.writeArray("PPDCP", "fUt[0]", U[0]);
        com.writeArray("PPDCP", "fUt[1]", U[1]);
        
        inst.plot(vMt);
        inst.save();
    }
    
    public static double Delta(PPDCPInstanceOld inst, int j, int t, double Mt[][]){
        double min = Integer.MAX_VALUE;
        for(int i=0; i<inst.bji[j].length; i++){
            double exp = inst.chi(j, t, i);
            for(int k=0; k<4; k++){
                exp += inst.aji[j][i][k] * Mt[t][k];
            }
            exp = exp / inst.psi(j, t, i);
            min = Math.min(min, exp);
        }
        return Math.max(0, min);
    }
    

    @Override
    public void load() throws Exception {
        
    }
     private IloNumVar ObjBeta;
    private IloNumVar ObjDelta;
    @Override
    public void start() throws Exception {
        cplex = new CplexExtended();
        
        
        
        Vt  = cplex.numVarArray(inst.T, 2, 0, Integer.MAX_VALUE, "V");
        
        Ut  = cplex.numVarArray(inst.T, 2, -inst.VMAX, +inst.VMAX, "U");
        MUt = cplex.numVarArray(inst.T, 4, Integer.MIN_VALUE, Integer.MAX_VALUE, "MU");
        Djt = cplex.numVarArray(inst.J, inst.T, 0, Integer.MAX_VALUE, "MU");
        ObjBeta = cplex.numVar(0, Integer.MAX_VALUE, "Beta");
        ObjDelta = cplex.numVar(0, Integer.MAX_VALUE, "Delta");
        
        Zjti = new IloIntVar[inst.J][inst.T][];
        for(int j=0; j<inst.J; j++){
            for(int t=0; t<inst.T; t++){
                Zjti[j][t] = cplex.boolVarArray(inst.bji[j].length, "Z"+(j+1)+""+(t+1));
            }
        }
        
        Ajtik = new IloNumVar[inst.J][inst.T][][];
        for(int j=0; j<inst.J; j++){
            for(int t=0; t<inst.T; t++){
                Ajtik[j][t] = cplex.numVarArray(inst.bji[j].length, inst.bji[j].length, 0, Integer.MAX_VALUE, "A"+(j+1)+""+(t+1));
            }
        }
        
        //-------------------------------- (Objective) --------------------------------
        IloNumExpr obj = cplex.prod(ObjBeta, inst.P1);
        obj = cplex.sum(obj, cplex.prod(ObjDelta, inst.P2));
        for(int t=0; t<inst.T; t++){
            /*if(obj==null){
                obj = cplex.sum(Vt[t][0], Vt[t][1]);
            }else{
                obj = cplex.sum(obj, cplex.sum(Vt[t][0], Vt[t][1]));
            }*/
            if(obj==null){
                obj = cplex.sum(cplex.prod(Vt[t][0],Vt[t][0]), cplex.prod(Vt[t][1],Vt[t][1]));
            }else{
                obj = cplex.sum(obj, cplex.prod(Vt[t][0],Vt[t][0]), cplex.prod(Vt[t][1],Vt[t][1]));
            }
        }
        cplex.addMinimize(obj);
        
        //-------------------------------- (|V|) --------------------------------
        for(int t=0; t<inst.T; t++){
            for(int j=0; j<2; j++){
                cplex.addGe(Vt[t][j], cplex.prod(+1, Ut[t][j]));
                cplex.addGe(Vt[t][j], cplex.prod(-1, Ut[t][j]));
            }
        }
        
        //-------------------------------- (11) --------------------------------
        for(int t=0; t<inst.T; t++){
            IloNumExpr exp[] = new IloNumExpr[4];
            for(int i=0; i<t; i++){
                double R[][] = inst.prod(inst.pow(inst.A,t-i), inst.B);
                for(int j=0; j<2; j++){
                    for(int k=0; k<4; k++){
                        if(exp[k]==null){
                            exp[k] = cplex.prod(R[k][j], Ut[i][j]);
                        }else{
                            exp[k] = cplex.sum(exp[k], cplex.prod(R[k][j], Ut[i][j]));
                        }
                    }
                }
            }
            double AX0[][] = inst.prod(inst.pow(inst.A,t+1), inst.X0);
            for(int k=0; k<4; k++){
                if(exp[k]==null){
                    //exp[k] = AX0[k][0];
                }else{
                    exp[k] = cplex.sum(exp[k], AX0[k][0]);
                }
            }
            for(int k=0; k<4; k++){
                if(exp[k]==null){
                    cplex.addEq(MUt[t][k], AX0[k][0], "S11."+(t+1)+","+(k+1)+"");
                }else{
                    cplex.addEq(MUt[t][k], exp[k], "S11."+(t+1)+","+(k+1)+"");
                }
            }
        }
        
        //-------------------------------- (34) --------------------------------
        for(int k=0; k<4; k++){
            //if(k==0 || k==2){
                //cplex.addEq(MUt[inst.T-1][k], inst.Xgoal[k], "S34."+(k+1)+"");     //Position and velocity
            cplex.addGe(cplex.sum(ObjBeta, cplex.prod(MUt[inst.T-1][k],+1)), +inst.Xgoal[k], "S34a."+(k+1)+"");     //Position and velocity
            cplex.addGe(cplex.sum(ObjBeta, cplex.prod(MUt[inst.T-1][k],-1)), -inst.Xgoal[k], "S34b."+(k+1)+"");     //Position and velocity
            //}
        }
        
        
        //-------------------------------- (36) --------------------------------
        for(int j=0; j<inst.J; j++){
            for(int t=0; t<inst.T; t++){
                for(int i=0; i<inst.bji[j].length; i++){
                    
                    IloNumExpr exp = cplex.prod(inst.Rjti[j][t][i]*-2*inst.AA, Djt[j][t]);
                    for(int k=0; k<4; k++){
                        exp = cplex.sum(exp, cplex.prod(-inst.aji[j][i][k], MUt[t][k]));
                    }
                    exp = cplex.sum(exp, cplex.prod(inst.M, Zjti[j][t][i]));
                    
                    double val = inst.Rjti[j][t][i]*(-inst.AA-inst.BB) - inst.bji[j][i] + inst.M;
                    cplex.addLe(exp, val, "S36."+(j+1)+","+(t+1)+","+(i+1)+"");
                    
                    /*IloNumExpr exp = cplex.prod(-M, Zjti[j][t][i]);
                    for(int k=0; k<4; k++){
                        exp = cplex.sum(exp, cplex.prod(aji[j][i][k], MUt[t][k]));
                    }
                    
                    double val = bji[j][i] - M;
                    cplex.addGe(exp, val, "S36."+(j+1)+","+(t+1)+","+(i+1)+"");*/
                    
                }
            }
        }
        
        //-------------------------------- (36) --------------------------------
        for(int j=0; j<inst.J; j++){
            for(int t=0; t<inst.T; t++){
                for(int i=0; i<inst.bji[j].length; i++){
                    for(int k=0; k<inst.bji[j].length; k++){
                        if(i!=k){
                            if(t==0){
                                //cplex.addGe(cplex.sum(Ajtik[j][t][i][k], cplex.prod(Zjti[j][t][i], -1), cplex.prod(Zjti[j][t-1][k], -1)), 1);
                            }else{
                                cplex.addGe(cplex.sum(Ajtik[j][t][i][k], cplex.prod(Zjti[j][t][k], -1), cplex.prod(Zjti[j][t-1][i], -1)), -1);
                                //cplex.addGe(cplex.sum(Ajtik[j][t][i][k], cplex.prod(Bjti[j][t][i], -1), cplex.prod(Bjti[j][t][k], -1)), -1);//
                            }
                        }
                    }
                }
            }
        }
        
        //-------------------------------- (A) --------------------------------
        for(int j=0; j<inst.J; j++){
            for(int t=0; t<inst.T; t++){
                IloNumExpr exp = null;
                for(int i=0; i<inst.bji[j].length; i++){
                    if(exp==null){
                        exp = Zjti[j][t][i];
                    }else{
                        exp = cplex.sum(exp, Zjti[j][t][i]);
                    }
                }
                for(int i=0; i<inst.bji[j].length; i++){
                    for(int k=0; k<inst.bji[j].length; k++){
                        exp = cplex.sum(exp, cplex.prod(Ajtik[j][t][i][k], -1));
                    }
                    
                }
                cplex.addGe(exp, 1, "SA."+(j+1)+","+(t+1));
            }
        }
        
        //-------------------------------- (37) --------------------------------
        IloNumExpr exp = cplex.prod(ObjDelta,-1);;
        for(int j=0; j<inst.J; j++){
            for(int t=0; t<inst.T; t++){
                if(exp==null){
                    exp = Djt[j][t];
                }else{
                    exp = cplex.sum(exp, Djt[j][t]);
                }
            }
        }
        cplex.addLe(exp, inst.DELTA, "Delta");
        
        
       
        
        
    }

    
    
    @Override
    public void execute() throws Exception {
        final long initial = System.nanoTime();
        cplex.setWarning(null);
        
        cplex.use(new IloCplex.IncumbentCallback() {
            double best = Integer.MAX_VALUE;
            @Override
            protected void main() throws IloException {
                time = (System.nanoTime() - initial)/1e9;
                
                if(PPDCPInstanceOld.PLOT){
                    double incb = getIncumbentObjValue();
                    if(incb<best){
                        //cplex.output().println("Solution incb     = " + incb);
                        //cplex.output().println("Solution best     = " + getBestObjValue());

                        best = incb;
                        double vMUt[][] = new double[inst.T][4];
                        for(int t=0; t<inst.T; t++){
                            for(int j=0; j<4; j++){
                                vMUt[t][j] = getValue(MUt[t][j]);
                            }
                        }
                        inst.plot(vMUt); 
                    }
                }
            }
        });
        
        //Escrita do modelo em arquivo
        //cplex.exportModel("PPDCPFullTrocas.lp");

        //execTime = 120;
        //epGap = 0.5;
        //Parada por tempo maximo de 10 segundos
        cplex.setParam(IloCplex.DoubleParam.TiLim, execTime);

        //Parada por gap relativo
        cplex.setParam(IloCplex.DoubleParam.EpGap, epGap);
        
        //Modo de paralelismo, numero de threads
        cplex.setParam(IloCplex.IntParam.Threads, threads);
        //Parada por gap absoluto de 100
        //cplex.setParam(IloCplex.DoubleParam.EpAGap, 100);

        //Parada por gap relativo
        //cplex.setParam(IloCplex.DoubleParam.EpGap, 0.000001);
        
        
        if (cplex.solve()) {
            cplex.output().println("Solution status     = " + cplex.getStatus());
            
            cplex.output().println("Solution value      = " + cplex.getObjValue());
            double vMUt[][] = cplex.getValues(MUt); 
            double vDjt[][] = cplex.getValues(Djt); 
            double vZjti[][][] = cplex.getValues(Zjti); 
            double vVt[][] = cplex.getValues(Vt); 
            double vUt[][] = cplex.getValues(Ut); 
            
            System.out.println("-------------------- [Ut] -> [Vt] ->[MUt] ----------------------");
            for(int t=0; t<inst.T; t++){
                System.out.printf("[%8g %8g] -> [%8g %8g] -> [%8g %8g %8g %8g]\n", vUt[t][0],vUt[t][1],vVt[t][0],vVt[t][1], vMUt[t][0],vMUt[t][1], vMUt[t][2], vMUt[t][3]);
            }
            
            System.out.println("-------------------- Dtj ----------------------");
            for(int t=0; t<inst.T; t++){
                for(int j=0; j<inst.J; j++){
                    System.out.printf("%8g ", vDjt[j][t]);
                }
                System.out.println();
            }
            
            System.out.println("-------------------- Ztji ----------------------");
            for(int t=0; t<inst.T; t++){
                for(int j=0; j<inst.J; j++){
                    System.out.printf("[ ");
                    for(int i=0; i<inst.bji[j].length; i++){
                        System.out.printf("%8g ", vZjti[j][t][i]);
                    }
                    System.out.printf("] ");
                }
                System.out.println();
            }
            inst.plot(vMUt);
        }
    }
    
    
    
    
    
    public static void main(String[] args) throws IloException, Exception{
        look_up();
    }
    public static void norm1() throws Exception{
        System.out.println("Path Planning - Norm1");
        CplexExtended cplex = new CplexExtended();
        IloNumVar x = cplex.numVar(0, 10, "x");
        IloNumVar y = cplex.numVar(0, 10, "y");
        
        
        x.setLB(3);
        x.setUB(3);
        
        y.setLB(4);
        y.setUB(4);
        
        
        IloNumExpr norm1 = cplex.sum(x, y);
         
        cplex.addMinimize(norm1);
        
        
        cplex.solve();
        
        System.out.println("x       = "+cplex.getValue(x));
        System.out.println("y       = "+cplex.getValue(y));
        System.out.println("norm1   = "+cplex.getValue(norm1));
    }
    public static void norm2() throws Exception{
        System.out.println("Path Planning - Norm2");
        CplexExtended cplex = new CplexExtended();
        IloNumVar x = cplex.numVar(0, 10, "x");
        IloNumVar y = cplex.numVar(0, 10, "y");
        IloNumVar z = cplex.numVar(-10, 10, "z");
        IloNumVar a = cplex.numVar(-10, 10, "a");
        
        x.setLB(3);
        x.setUB(3);
        
        y.setLB(4);
        y.setUB(4);
        
        
        // z = sqrt(x*x + y*y)
        // max |x|+|y|
        
        // x/z = sin(a)
        // y/z = cos(a)
        // z = x/sin(a)
        
        cplex.addMinimize(z);
        
        IloNumExpr expr = cplex.sum(cplex.prod(x, x), cplex.prod(y, y));
        cplex.addEq(expr, a);
        cplex.addEq(a, z);
        
        cplex.solve();
        
        System.out.println("x       = "+cplex.getValue(x));
        System.out.println("y       = "+cplex.getValue(y));
        System.out.println("z       = "+cplex.getValue(z));
    }
    public static void look_up() throws Exception{
        System.out.println("Path Planning - Norm2");
        CplexExtended cplex = new CplexExtended();
        IloNumVar x = cplex.numVar(0, 10, "x");
        IloNumVar y = cplex.numVar(0, 10, "y");
        
        x.setLB(3);
        x.setUB(3);
        
        y.setLB(4);
        y.setUB(4);
        
        
        // n = sqrt(x*x + y*y)
        // max |x|+|y|
        // |x|<
        
        IloNumExpr norm2 = cplex.sum(cplex.prod(x, x), cplex.prod(y, y));
         
        cplex.addMinimize(norm2);
        
        cplex.solve();
        
        System.out.println("x       = "+cplex.getValue(x));
        System.out.println("y       = "+cplex.getValue(y));
        System.out.println("norm2   = "+cplex.getValue(norm2));
        
        final int N = 100;
        double Ii[] = new double[N];
        double Oi[] = new double[N];
        for(int i = 0; i<N; i++){
            Ii[i] = ((double)i)/(N-1);
            Oi[i] = erf_inv(Ii[i]);
            
            System.out.printf("erf-inv(%5.3f) = %5.6f\n", Ii[i], Oi[i]);
        }
        
    }
    
    private static double p(double x, double y){
        return Math.pow(x, y);
    }
    private static double erf_inv(double z){
        final double PI = Math.PI;
        return 0.5 * Math.sqrt(PI) * (z + PI*p(z,3)/12 + 7*p(PI,2)*p(z,5)/480 + 127*p(PI,3)*p(z,7)/40320 + 4369*p(PI,4)*p(z,9)/5806080 + 34807*p(PI,5)*p(z,11)/182476800);
    }
    
}
