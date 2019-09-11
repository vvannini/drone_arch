
package ProOF.apl.pog.method;

import ProOF.apl.pog.problem.GCILT.GCILTInstance;
import ProOF.apl.pog.problem.GCILT.GCILTLot;
import ProOF.com.Linker.LinkerParameters;
import ProOF.com.Linker.LinkerResults;
import ProOF.com.Linker.LinkerApproaches;
import ProOF.com.Linker.LinkerValidations;
import ProOF.com.language.Run;
import ProOF.CplexExtended.CplexExtended;
import ProOF.utilities.uUtil;
import ilog.concert.IloException;
import ilog.concert.IloIntVar;
import ilog.concert.IloNumExpr;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;
import ilog.cplex.IloCplex.UnknownObjectException;

/**
 *
 * @author Hossomi
 */
public class GCILTFixOpt_v1 extends Run {

    private GCILTInstance inst = new GCILTInstance();
    private double execTime;
    private double epGap;
    private double alpha;
    private int threads;
    private CplexExtended cplex;
    
    private IloIntVar Xikt[][][];
    private IloIntVar Yuikt[][][][];
    private IloNumVar Iipt[][][];   
    private IloNumVar Wipvt[][][][]; 
    private IloNumVar I0ip[][];     
    private IloNumVar Blyt[][][];   
    private IloNumVar Vlyt[][][];   
    
    
    private IloIntVar Alyt[][][];   
    private IloIntVar Tluyt[][][][];
    private IloIntVar Zyt[][];    
    
    
    
    private IloNumExpr ObjHold;
    private IloNumExpr ObjSwaping;
    private IloNumExpr ObjTransfer;
    private IloNumExpr ObjOverHold;
    private IloNumExpr ObjValue;
    
    private double time;
    
    
    private int[][][] v_Alyt;
    private int[][][][] v_Tluyt;
    private int[][] v_Zyt;
    
    @Override
    public String name() {
        return "GCILT Fix&Opt v1";
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
        execTime    = com.Dbl("Time", 60.0, 1.0, 180000.0);
        epGap       = com.Dbl("Gap Rel", 0.0001, 0.0, 100.0);
        alpha       = com.Dbl("alpha", 0.1, 0.0, 1.0);
        threads     = com.Int("Threads", 0, 0, 16);
    }

    @Override
    public boolean validation(LinkerValidations com) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void load() throws Exception {
    }

    @Override
    public void results(LinkerResults com) throws Exception {
        com.writeDbl("Best Time", time);
        com.writeString("Status", cplex.getStatus().toString());
        com.writeDbl("Obj Value", cplex.getObjValue());
        com.writeDbl("Lower Bound", cplex.getBestObjValue());
        com.writeDbl("Swap Cost", cplex.getValue(ObjSwaping));
        com.writeDbl("Holding Cost", cplex.getValue(ObjHold));
        com.writeDbl("Transfer Cost", cplex.getValue(ObjTransfer));
        com.writeDbl("OverHold Cost", cplex.getValue(ObjOverHold));
    }
    
    @Override
    public void start() throws Exception {
        cplex = new CplexExtended();
        
        Xikt = cplex.intVarArray(inst.N, inst.K, inst.T, 0, 31, "X");
        Yuikt = cplex.boolVarArray(inst.L, inst.N, inst.K, inst.T, "Y");
        Iipt = cplex.numVarArray(inst.N, inst.P, inst.T, 0, Double.MAX_VALUE, "I");
        Wipvt = cplex.numVarArray(inst.N, inst.P, inst.P, inst.T, 0, Double.MAX_VALUE, "W"); 
        I0ip = cplex.numVarArray(inst.N, inst.P, 0, Double.MAX_VALUE, "Io");
        
        Blyt = cplex.numVarArray(inst.L, inst.Y, inst.T, 0, 31, "B");
        
        Alyt = cplex.boolVarArray(inst.L, inst.Y, inst.T, "A");
        Tluyt = cplex.boolVarArray(inst.L, inst.L, inst.Y, inst.T, "T");
        Vlyt = cplex.numVarArray(inst.L, inst.Y, inst.T, 0, Integer.MAX_VALUE, "V");
        Zyt = cplex.boolVarArray(inst.Y, inst.T, "Z");
        
        //--------------------------Definindo função objetivo ------------------
        IloNumExpr Obj_luyt[][][][] = new IloNumExpr[inst.L][inst.L][inst.Y][inst.T];
        for(int l=0; l<inst.L; l++){
            for(int u=0; u<inst.L; u++){
                for(int y=0; y<inst.Y; y++){
                    for(int t=0; t<inst.T; t++){
                        Obj_luyt[l][u][y][t] = cplex.prod(Tluyt[l][u][y][t], inst.SCluy[l][u][y]);
                    }
                }
            }
        }
        
        IloNumExpr Obj_ipvt[][][][] = new IloNumExpr[inst.N][inst.P][inst.P][inst.T];
        for(int i=0; i<inst.N; i++){
            for(int p=0; p<inst.P; p++){
                for(int v=0; v<inst.P; v++){
                    for(int t=0; t<inst.T; t++){
                        Obj_ipvt[i][p][v][t] = cplex.prod(Wipvt[i][p][v][t], inst.Ripv[i][p][v]);
                    }
                }
            }
        }
        
        IloNumExpr Obj_ipt[][][] = new IloNumExpr[inst.N][inst.P][inst.T];
        for(int i=0; i<inst.N; i++){
            for(int p=0; p<inst.P; p++){
                for(int t=0; t<inst.T; t++){
                    Obj_ipt[i][p][t] = cplex.prod(Iipt[i][p][t], inst.Hip[i][p]);
                }
            }
        }
        
        IloNumExpr Obj_ip[][] = new IloNumExpr[inst.N][inst.P];
        for(int i=0; i<inst.N; i++){
            for(int p=0; p<inst.P; p++){
                Obj_ip[i][p] = cplex.prod(I0ip[i][p], inst.Pip[i][p]);
            }
        }

        
        ObjHold      = Sum(cplex, Obj_ipt);
        ObjSwaping   = Sum(cplex, Obj_luyt);
        ObjTransfer  = Sum(cplex, Obj_ipvt);
        ObjOverHold  = Sum(cplex, Obj_ip);
        
        ObjValue = cplex.sum(ObjHold, ObjSwaping, ObjTransfer, ObjOverHold);
        
        cplex.addMinimize(ObjValue);
        
        //----------------------------------------------------------------------
        //Wipvt = 0 se p=v
        for(int i=0; i<inst.N; i++){
            for(int p=0; p<inst.P; p++){
                for(int t=0; t<inst.T; t++){
                    Wipvt[i][p][p][t].setLB(0);
                    Wipvt[i][p][p][t].setUB(0);
                }
            }
        }
        //----------------------------------------------------------------------
        //Tluyt = 0 se l=u
        for(int l=0; l<inst.L; l++){
            for(int y=0; y<inst.Y; y++){
                for(int t=0; t<inst.T; t++){
                    Tluyt[l][l][y][t].setLB(0);
                    Tluyt[l][l][y][t].setUB(0);
                    for(int i : inst.Nl[l]){
                        for(int k : inst.Ky[y]){
                            Yuikt[l][i][k][t].setLB(0);
                            Yuikt[l][i][k][t].setUB(0);
                        }
                    }
                }
            }
        }

        //--------------------------------- sub(2) -----------------------------
        for(int l=0; l<inst.L; l++){
            for(int i : inst.Nl[l]){
                for(int p=0; p<inst.P; p++){
                    for(int t=0; t<inst.T; t++){
                        IloNumExpr hold_out = cplex.prod(Iipt[i][p][t], +1);

                        IloNumExpr transfer_out = null;
                        for(int v=0; v<inst.P; v++){
                            IloNumExpr temp = cplex.prod(Wipvt[i][p][v][t], +1);
                            if(transfer_out==null){
                                transfer_out = temp;
                            }else{
                                transfer_out = cplex.sum(transfer_out, temp);
                            }
                        }
                        IloNumExpr transfer_in = null;
                        for(int v=0; v<inst.P; v++){
                            IloNumExpr temp = cplex.prod(Wipvt[i][v][p][t], -1);
                            if(transfer_in==null){
                                transfer_in = temp;
                            }else{
                                transfer_in = cplex.sum(transfer_in, temp);
                            }
                        }
                        IloNumExpr prod = null;
                        for(int y: inst.Fp[p]){
                            for(int k: inst.Ky[y]){
                                IloNumExpr temp = cplex.prod(Xikt[i][k][t], -inst.Ek[k]*inst.Pik[i][k]);
                                if(prod==null){
                                    prod = temp;
                                }else{
                                    prod = cplex.sum(prod, temp);
                                }
                                for(int u=0; u<inst.L; u++){
                                    prod = cplex.sum(prod, cplex.prod(Yuikt[u][i][k][t], -inst.Lluy(u, l, y)*inst.Ek[k]*inst.Pik[i][k]));
                                }
                            }
                        }
                        if(prod==null){
                            cplex.output().println("prod==null");
                        }

                        if(t==0){
                            IloNumExpr overhold_in = cplex.prod(I0ip[i][p], -1);

                            if(transfer_in==null && transfer_out==null){
                                cplex.addEq(
                                        cplex.sum(hold_out, overhold_in, prod),
                                        /*inst.Iip0[i][p]*/-inst.Dipt[i][p][t],
                                        "Estoque("+i+","+p+","+t+")");
                            }else{
                                cplex.addEq(
                                        cplex.sum(hold_out, transfer_out, overhold_in, transfer_in, prod),
                                        /*inst.Iip0[i][p]*/-inst.Dipt[i][p][t],
                                        "Estoque("+i+","+p+","+t+")");
                            }
                        }else{
                            IloNumExpr hold_in = cplex.prod(Iipt[i][p][t-1], -1);

                            if(transfer_in==null && transfer_out==null){
                                cplex.addEq(
                                    cplex.sum(hold_out, hold_in, prod),
                                    -inst.Dipt[i][p][t],
                                    "Estoque("+i+","+p+","+t+")");
                            }else{
                                cplex.addEq(
                                    cplex.sum(hold_out, transfer_out, hold_in, transfer_in, prod),
                                    -inst.Dipt[i][p][t],
                                    "Estoque("+i+","+p+","+t+")");
                            }

                        }
                    }
                }
            }
        }
        
        for(int u=0; u<inst.L; u++){
            for(int l=0; l<inst.L; l++){
                for(int y=0; y<inst.Y; y++){
                    for(int k : inst.Ky[y]){
                        for(int t=0; t<inst.T; t++){
                            IloNumExpr sum = null;
                            for(int i : inst.Nl[l]){
                                IloNumExpr temp = cplex.prod(Yuikt[u][i][k][t], 1);
                                if(sum==null){
                                    sum = temp;
                                }else{
                                    sum = cplex.sum(sum, temp);
                                }
                            }
                            if(inst.Lluy(u, l, y)>0.001){
                                cplex.addEq(sum,Tluyt[u][l][y][t],
                                    "Subject03("+u+","+l+","+y+","+k+","+t+")");
                            }else{
                                cplex.addEq(sum,0,
                                    "Subject03("+u+","+l+","+y+","+k+","+t+")");
                            }
                        }
                    }
                }
            }
        }
        
        
        for(int l=0; l<inst.L; l++){
            for(int y=0; y<inst.Y; y++){
                for(int k : inst.Ky[y]){
                    for(int t=0; t<inst.T; t++){
                        IloNumExpr sum = null;
                        for(int i : inst.Nl[l]){
                            IloNumExpr temp = cplex.prod(Xikt[i][k][t], 1);
                            if(sum==null){
                                sum = temp;
                            }else{
                                sum = cplex.sum(sum, temp);
                            }
                        }
                        for(int u=0; u<inst.L; u++){
                            IloNumExpr temp = cplex.prod(Tluyt[u][l][y][t], inst.STluy[u][l][y] + inst.Lluy(u,l,y));
                            sum = cplex.sum(sum, temp);
                        }
                        cplex.addEq(sum, Blyt[l][y][t],
                            "Subject04("+l+","+y+","+t+")");
                    }
                }
            }
        }
        
        for(int y=0; y<inst.Y; y++){
            for(int t=1; t<inst.T; t++){
                cplex.addLe(Zyt[y][t], Zyt[y][t-1],
                    "Subject05("+y+","+t+")");
            }
        }
        
        
        for(int y=0; y<inst.Y; y++){
            for(int t=0; t<inst.T; t++){
                IloNumExpr sum = null;
                for(int l=0; l<inst.L; l++){
                    IloNumExpr temp = cplex.prod(Blyt[l][y][t], +1);
                    if(sum==null){
                        sum = temp;
                    }else{
                        sum = cplex.sum(sum, temp);
                    }
                }
                if(t==0){
                    cplex.addLe(sum, inst.Qt[t],
                        "Subject06a("+y+","+t+")");
                }else{
                    cplex.addLe(sum, cplex.prod(inst.Qt[t], Zyt[y][t-1]),
                        "Subject06b("+y+","+t+")");
                }
                    
            }
        }
        for(int y=0; y<inst.Y; y++){
            for(int t=0; t<inst.T; t++){
                IloNumExpr sum = null;
                for(int l=0; l<inst.L; l++){
                    IloNumExpr temp = cplex.prod(Blyt[l][y][t], +1);
                    if(sum==null){
                        sum = temp;
                    }else{
                        sum = cplex.sum(sum, temp);
                    }
                }
                cplex.addGe(sum, cplex.prod(inst.Qt[t], Zyt[y][t]),
                    "Subject07("+y+","+t+")");
            }
        }
        
        for(int l=0; l<inst.L; l++){
            for(int y=0; y<inst.Y; y++){
                for(int t=0; t<inst.T; t++){
                    IloNumExpr sum = null;
                    for(int k : inst.Ky[y]){
                        for(int i : inst.Nl[l]){
                            IloNumExpr temp = cplex.prod(Xikt[i][k][t], inst.Pik[i][k]);
                            if(sum==null){
                                sum = temp;
                            }else{
                                sum = cplex.sum(sum, temp);
                            }
                            for(int u=0; u<inst.L; u++){
                                temp = cplex.prod(Yuikt[u][i][k][t], inst.Lluy(u, l, y)*inst.Pik[i][k]);
                                sum = cplex.sum(sum, temp);
                            }
                        }
                    }
                    for(int u=0; u<inst.L; u++){
                        IloNumExpr temp = cplex.prod(Tluyt[u][l][y][t], inst.STluy[u][l][y] * inst.Cy[y]);
                        sum = cplex.sum(sum, temp);
                    }
                    cplex.addLe(sum, cplex.prod(Blyt[l][y][t], inst.Cy[y]),
                        "Subject08("+l+","+y+","+t+")");
                }
            }
        }
        
        for(int l=0; l<inst.L; l++){
            for(int y=0; y<inst.Y; y++){
                for(int t=0; t<inst.T; t++){
                    for(int k : inst.Ky[y]){
                        for(int i : inst.Nl[l]){
                            IloNumExpr max = cplex.prod(Alyt[l][y][t], inst.Mikt(i,k,t));
                            for(int u=0; u<inst.L; u++){
                                //IloNumExpr temp = cplex.prod(Tluyt[l][u][y][t], inst.Mikt[i][k][t]);
                                IloNumExpr temp = cplex.prod(Tluyt[u][l][y][t], inst.Mikt(i,k,t));
                                max = cplex.sum(max, temp);
                            }
                            
                            IloNumExpr prod = Xikt[i][k][t];
                            for(int u=0; u<inst.L; u++){
                                //prod = cplex.sum(prod, Yuikt[u][i][k][t]);
                                prod = cplex.sum(prod, cplex.prod(inst.Lluy(u,l,y), Yuikt[u][i][k][t]));
                            }
                            
                            cplex.addLe(prod, max,
                                "Subject09("+y+","+t+")");
                        }
                    }
                }
            }
        }
        
        for(int y=0; y<inst.Y; y++){
            for(int t=0; t<inst.T; t++){
                IloNumExpr one = null;
                for(int l=0; l<inst.L; l++){
                    if(one==null){
                        one = Alyt[l][y][t];
                    }else{
                        one = cplex.sum(one, Alyt[l][y][t]);
                    }
                }
                
                cplex.addEq(one, 1,
                    "Subject10("+y+","+t+")");
            }
        }
        
        //Subject 07(B)
        for(int l=0; l<inst.L; l++){
            for(int y=0; y<inst.Y; y++){
                Alyt[l][y][0].setLB(inst.Aly0[l][y]);
                Alyt[l][y][0].setUB(inst.Aly0[l][y]);
            }
        }
        
        for(int l=0; l<inst.L; l++){
            for(int y=0; y<inst.Y; y++){
                for(int t=0; t<inst.T; t++){
                    IloNumExpr in = cplex.prod(Alyt[l][y][t], +1);
                    for(int u=0; u<inst.L; u++){
                        IloNumExpr temp = cplex.prod(Tluyt[u][l][y][t], +1);
                        if(in==null){
                            in = temp;
                        }else{
                            in = cplex.sum(in, temp);
                        }
                    }

                    IloNumExpr out = t+1==inst.T ? null : cplex.prod(Alyt[l][y][t+1], -1);
                    for(int u=0; u<inst.L; u++){
                        IloNumExpr temp = cplex.prod(Tluyt[l][u][y][t], -1);
                        if(out==null){
                            out = temp;
                        }else{
                            out = cplex.sum(out, temp);
                        }
                    }
                    
                    if(t+1==inst.T){
                        if(out==null){
                            cplex.addLe(in, 1,
                                "Subject11b("+y+","+t+")");
                        }else{
                            cplex.addLe(cplex.sum(in,out), 1,
                                "Subject11c("+y+","+t+")");
                        }
                    }else{
                        cplex.addEq(cplex.sum(in,out), 0,
                            "Subject11a("+y+","+t+")");
                    }
                }
            }
        }
                    
        
        for(int l=0; l<inst.L; l++){
            for(int u=0; u<inst.L; u++){
                if(l!=u){
                    for(int y=0; y<inst.Y; y++){
                        for(int t=0; t<inst.T; t++){
                            cplex.addGe(cplex.sum(
                                    cplex.prod(Vlyt[u][y][t], +1),
                                    cplex.prod(Vlyt[l][y][t], -1),
                                    cplex.prod(Tluyt[l][u][y][t], -(inst.L)),
                                    cplex.prod(Alyt[l][y][t], +(inst.L))
                                ), 1-(inst.L),
                                "Subject12("+y+","+t+")");
                        }
                    }
                }
            }
        }
    }

    
    private void getSol()  throws Exception{
        v_Alyt  = getValues(cplex,Alyt);
        v_Tluyt = getValues(cplex,Tluyt);
        v_Zyt   = getValues(cplex,Zyt);
    }
    private void freeAll() throws Exception{
        for(int t = 0; t<inst.T; t++){
            free(t);
        }
    }
    private void free(int t) throws Exception {
        //Free variables
        for(int y=0; y<inst.Y; y++){
            Zyt[y][t].setMin(0);
            Zyt[y][t].setMax(1);
        }
        if(t>0){
            for(int y=0; y<inst.Y; y++){
                for(int l=0; l<inst.L; l++){
                    Alyt[l][y][t].setMin(0);
                    Alyt[l][y][t].setMax(1);
                }
            }
        }
        for(int y=0; y<inst.Y; y++){
            for(int l=0; l<inst.L; l++){
                for(int u=0; u<inst.L; u++){
                    if(l!=u){
                        Tluyt[l][u][y][t].setMin(0);
                        Tluyt[l][u][y][t].setMax(1);
                    }
                }
            }
        }
    }
    private void fix(int t) throws Exception {
        //Free variables
        for(int y=0; y<inst.Y; y++){
            Zyt[y][t].setMin(v_Zyt[y][t]);
            Zyt[y][t].setMax(v_Zyt[y][t]);
        }
        if(t>0){
            for(int y=0; y<inst.Y; y++){
                for(int l=0; l<inst.L; l++){
                    Alyt[l][y][t].setMin(v_Alyt[l][y][t]);
                    Alyt[l][y][t].setMax(v_Alyt[l][y][t]);
                }
            }
        }
        for(int y=0; y<inst.Y; y++){
            for(int l=0; l<inst.L; l++){
                for(int u=0; u<inst.L; u++){
                    if(l!=u){
                        Tluyt[l][u][y][t].setMin(v_Tluyt[l][u][y][t]);
                        Tluyt[l][u][y][t].setMax(v_Tluyt[l][u][y][t]);
                    }
                }
            }
        }
    }
    
    /*
     * 
    for(int y=0; y<inst.Y; y++){
            int a = inst.ovenStartColor(y);
            int l = a;
            for(int t=0; t<inst.T; t++){
                v_Zyt[y][t] = t>0 ? 0 : 1;
                
                
                boolean flag[] = new boolean[inst.L];
                
                //a = l;
                v_Alyt[l][y][t] = 1;
                flag[l] = true;

                
                //System.out.print("sequ["+t+"] = [ "+l);
                if(v_Zyt[y][t]>0){
                    for(int u : inst.Ly[y]){
                        if(!flag[u] && u!=l){
                            v_Tluyt[l][u][y][t] = 1;
                            l = u;
                            flag[l] = true;
                            //System.out.print(" "+l);
                        }
                    }
                }
                    
                //System.out.println(" ]");
                //v_Tluyt[l][a][y][t] = 1;
            }
        }

     */
    public void initial_solution() throws Exception{
        
        v_Alyt  = new int[inst.L][inst.Y][inst.T];
        v_Tluyt = new int[inst.L][inst.L][inst.Y][inst.T];
        v_Zyt   = new int[inst.Y][inst.T];
        
        
        int LASTy[] = new int[inst.Y];
        for (int y = 0; y < inst.Y; y++) {
            LASTy[y] = inst.ovenStartColor(y);
        }

        double Dl[] = new double[inst.L];
        double Rl[] = new double[inst.L];
        System.arraycopy(inst.Dl, 0, Dl, 0, inst.L);

        boolean STOPy[] = new boolean[inst.Y];
        for (int t = 0; t < inst.T; t++) {
            for (int y = 0; y < inst.Y; y++) {
                int l = LASTy[y];
                boolean flag[] = new boolean[inst.L];
                
                v_Alyt[l][y][t] = 1;
                
                flag[l] = true;
                
                System.out.printf("(%d;%d) l = %d | ", t,y,l);
                
                
                int remDays = inst.Qt[t];
                while(remDays>0 && !STOPy[y]){
                    l = LASTy[y];
                    double sum = 0;
                    for(int w = 0; w<inst.L; w++){
                        if(inst.Ly[y].contains(w)){
                            Rl[w] = Math.min(Dl[w], inst.STluy[l][w][y]*inst.Cly[w][y]);
                        }else{
                            Rl[w] = Dl[w];
                        }
                        Dl[w] -= Rl[w];
                        sum += Dl[w];
                    }
                    if(sum<inst.Cy[y]){
                        STOPy[y] = true;
                        remDays = 0;
                        System.out.printf(" stop");
                    }else{
                        v_Zyt[y][t] = 1;
                        
                        System.out.printf("[ ");
                        for(int w=0; w<inst.L; w++){
                            System.out.printf("%1.0f ", Dl[w]);
                        }
                        System.out.printf("]");
                        int u = uUtil.indexMax(Dl); //inst.Ly[y].size()<=1 ? l : 
                        
                        
                        
                        if (inst.fullSetup(l, u, y) < remDays ) {//&& u!=l&& Dl[u]>1
                            
                            double q = uUtil.minDbl(
                                        (remDays-inst.STluy[l][u][y])*inst.Cly[u][y],
                                        //(inst.Qt[t]/inst.Ly[y].size()-inst.STluy[l][u][y])*inst.Cly[u][y],
                                        (Dl[u]+Rl[u])
                                    );
                            q = uUtil.maxDbl(q, 0);
                            if(Dl[u]+Rl[u]>q){
                                Dl[u] -= q;
                            }else{
                                Rl[u] = 0;
                                Dl[u] = 0;
                            }
                            if(flag[u]){
                                remDays -= (int)(1 + q/inst.Cly[u][y]);
                                System.out.printf(" *{%d;%d;%1.0f} ", u, (int)(1 + q/inst.Cly[u][y]), q);
                            }else{
                                v_Tluyt[l][u][y][t] = 1;
                                flag[u] = true;
                                LASTy[y] = u;
                                remDays -= inst.fullSetup(l, u, y) + (int)(1+q/inst.Cly[u][y]);
                                
                                System.out.printf(" +{%d;%d;%1.0f} ", u, inst.fullSetup(l, u, y) + (int)(1+q/inst.Cly[u][y]), q);
                            }
                        }else{
                            double q = remDays*inst.Cly[l][y];
                            if(Dl[l]+Rl[l]>q){
                                Dl[l] -= q;
                            }else{
                                Rl[l] = 0;
                                Dl[l] = 0;
                            }
                            System.out.printf(" ${%d;%d;%1.0f} ", l, remDays, q);
                            remDays = 0;
                            
                        }
                    }
                    for(int w = 0; w<inst.L; w++){
                        Dl[w] += Rl[w];
                    }
                }
                System.out.printf("[ ");
                for(int w=0; w<inst.L; w++){
                    System.out.printf("%1.0f ", Dl[w]);
                }
                System.out.printf("]\n");
            }
        }
        
        for(int t=0; t<inst.T; t++){
            fix(t);
        }
        cplex.setParam(IloCplex.DoubleParam.TiLim, 10);
        if(cplex.solve()){
            print();
            
            
            
            freeAll();
        }else{
            throw new Exception("initial solution is not valid");
        }
    }
    
    @Override
    public void execute() throws Exception {
        final long initial = System.nanoTime();
        
        
        cplex.use(new IloCplex.IncumbentCallback() {
            @Override
            protected void main() throws IloException {
                time = (System.nanoTime() - initial)/1e9;
                //System.out.printf("IncumbentCallback time = %g\n", time);
            }
        });
        
        //Escrita do modelo em arquivo
        //cplex.exportModel("LTGCIP.lp");

        //Parada por tempo maximo de 10 segundos
        execTime = 1200;
        

        //Parada por gap relativo
        cplex.setParam(IloCplex.DoubleParam.EpGap, epGap);
        
        //Modo de paralelismo, numero de threads
        cplex.setParam(IloCplex.IntParam.Threads, threads);
        //Parada por gap absoluto de 100
        //cplex.setParam(IloCplex.DoubleParam.EpAGap, 100);

        //Parada por gap relativo
        //cplex.setParam(IloCplex.DoubleParam.EpGap, 0.000001);
        
        cplex.setParam(IloCplex.IntParam.MIPEmphasis, IloCplex.MIPEmphasis.Feasibility);
        
        initial_solution();
        cplex.setParam(IloCplex.DoubleParam.TiLim, /*execTime*alpha*/20);
        int count = 0;
        do{
            System.out.println("-------------------------cont["+(count++)+"]--------------------------");
            cplex.solve();
        }while(!(cplex.getStatus() == IloCplex.Status.Feasible || cplex.getStatus() == IloCplex.Status.Optimal) && (System.nanoTime() - initial)/1e9<execTime);
        
        
        int period = 0;
        
        cplex.setParam(IloCplex.IntParam.MIPEmphasis, IloCplex.MIPEmphasis.Balanced);
        int windows = 1;//;inst.T;
        
        double best = cplex.getObjValue();
        count = 0;
        double ref = 0;
        double gap = 0;
        if((System.nanoTime() - initial)/1e9<execTime){
            getSol();
            gap = (cplex.getObjValue()-cplex.getBestObjValue())/(cplex.getObjValue());
            ref = 0;//gap/6;
        }else{
            ref = 0.1;
        }
        
        double tt[] = new double[inst.T];
        for(int t=0; t<inst.T; t++){
            tt[t] = Math.max(3,execTime*alpha/inst.T);
        }
        int loop = 0;
        while((System.nanoTime() - initial)/1e9<execTime){
            /*boolean flag = true;
            for(int y=0; y<inst.Y; y++){
                flag = flag && v_Zyt[y][period]<1;
            }
            if(flag){
                period = 0;
            }*/

            
            cplex.setParam(IloCplex.DoubleParam.TiLim, tt[period]);
            if(windows>=inst.T){
                tt[period] = Math.max(5,execTime - (System.nanoTime() - initial)/1e9);
                cplex.setParam(IloCplex.DoubleParam.TiLim, tt[period]);
            }
            
            //Zyt Alyt Tluyt Yuikt Xikt
            System.out.println("-----------------------period["+period+"]--------------------------");
            System.out.println("count(%)  = "+(count)*1.0/inst.T);
            System.out.println("best time = "+time);
            System.out.println("this time = "+(System.nanoTime() - initial)/1e9);
            System.out.print("tt        = [");
            for(int t=0; t<inst.T; t++){
                System.out.printf(" %1.0f",tt[t]);
            }
            System.out.println(" ]");
            System.out.println("ref       = "+ref);
            System.out.println("last gap  = "+gap);
            
            for(int t=0; t<inst.T; t++){
                if(period<=t && t<period+windows || ((period+windows) > inst.T && ((period+windows) % inst.T)>t)){
                    System.out.print("*");
                    free(t);
                }else{
                    System.out.print("#");
                    fix(t);
                }
            }
            System.out.println();
            if(cplex.solve()){
                System.out.printf("solve t = %d -> true\n", period);
                getSol();
                if(cplex.getObjValue()*1.05<best){
                    best = cplex.getObjValue();
                    count = 0;
                    period-=(windows+1);
                    if(period<0){
                        period = inst.T+period;
                    }
                }else{
                    count++;
                    if(count+2>inst.T && windows<inst.T){
                        count = 0;
                        windows++;
                    }
                }
                
                gap = (cplex.getObjValue()-cplex.getBestObjValue())/cplex.getObjValue();
                
                if(gap<1){ // loop>inst.T && 
                    tt[period] *= (1+gap-ref);
                    tt[period] = Math.max(tt[period],execTime*alpha/inst.T); 
                    tt[period] = Math.min(tt[period],execTime/inst.T); 
                    tt[period] = Math.min(tt[period],execTime - (System.nanoTime() - initial)/1e9);
                    tt[period] = Math.max(tt[period],3);
                } 

                ref = ref * (1-alpha) + alpha*gap;
            }else{
                System.out.printf("solve t = %d -> false\n", period);
            }
            
                
            /*period--;
            if(period<0){
                period = inst.T-1;
            }*/
            period = (period+1)%inst.T;
            loop++;
        }
        cplex.output().println("Solution status     = " + cplex.getStatus());
        if(cplex.getStatus() == IloCplex.Status.Feasible || cplex.getStatus() == IloCplex.Status.Optimal){
            print();
        }
    }
    private void print() throws IloException, Exception{
        cplex.output().println("Solution value      = " + cplex.getObjValue());
        cplex.output().println("Solution Swaping    = " + cplex.getValue(ObjSwaping));
        cplex.output().println("Solution Transfer   = " + cplex.getValue(ObjTransfer));
        cplex.output().println("Solution Hold       = " + cplex.getValue(ObjHold));
        cplex.output().println("Solution OverHold   = " + cplex.getValue(ObjOverHold));


        getSol();

        double[][][] v_Blyt     = getValues(cplex,Blyt);

        int[][][] v_Xikt     = getValues(cplex,Xikt);
        int[][][][] v_Yuikt    = getValues(cplex,Yuikt);

        double[][][][] v_Wipvt  = getValues(cplex,Wipvt);
        double[][][] v_Iipt     = getValues(cplex,Iipt);
        double[][] v_I0ip       = getValues(cplex,I0ip);

        //double[][][] v_Vlyt     = getValues(cplex,Vlyt);


        cplex.output().printf("--------------- Zyt: (y) x t ---------------------\n");
        cplex.output().printf("             | ");
        for(int t=0; t<inst.T; t++){
            cplex.output().printf("%12s ", t);
        }
        cplex.output().printf("\n");
        for(int y=0; y<inst.Y; y++){
            cplex.output().printf("       (%3d) | ", y);
            for(int t=0; t<inst.T; t++){
                cplex.output().printf("%12d ", v_Zyt[y][t]);
            }
            cplex.output().printf("\n");
        }
        cplex.output().printf("--------------- Blyt: (y,l) x t ---------------------\n");
        for(int y=0; y<inst.Y; y++){
            for(int l=0; l<inst.L; l++){
                boolean teste = false;
                for(int t=0; t<inst.T; t++){
                    if(v_Blyt[l][y][t] > 0.001){
                       teste = true; 
                       break;
                    }
                }
                if(teste){
                    cplex.output().printf("    (%2d,%2d) | ", y, l);
                    for(int t=0; t<inst.T; t++){
                        cplex.output().printf("%5.2f ", v_Blyt[l][y][t]);
                    }
                    cplex.output().printf("\n");
                }
            }
        }

        cplex.output().printf("--------------- Prod: (k,i) x T ---------------------\n");
        for(int y=0; y<inst.Y; y++){
            for(int k : inst.Ky[y]){
                for(int l=0; l<inst.L; l++){
                    for(int i : inst.Nl[l]){
                        boolean teste = false;
                        for(int t=0; t<inst.T; t++){
                            double flag = 0;
                            for(int u=0; u<inst.L; u++){
                                if(v_Yuikt[u][i][k][t]>0.5){
                                    flag = inst.Lluy(u,l,y);
                                }
                            }
                            if(flag>0.001 || v_Xikt[i][k][t] > 0.001){
                               teste = true; 
                               break;
                            }
                        }
                        if(teste){
                            cplex.output().printf("    (%2d,%3d) | ", k, i);
                            for(int t=0; t<inst.T; t++){
                                double flag = 0;
                                for(int u=0; u<inst.L; u++){
                                    if(v_Yuikt[u][i][k][t]>0.5){
                                        flag = inst.Lluy(u,l,y);
                                    }
                                }
                                cplex.output().printf("[%5.0f + %3.0f]", v_Xikt[i][k][t]*inst.Pik[i][k]*inst.Ek[k], flag*inst.Pik[i][k]*inst.Ek[k]);
                            }
                            cplex.output().printf("\n");
                        }
                    }
                }
            }
        }

        cplex.output().printf("--------------- Alyt: (y,l) x t ---------------------\n");
        for(int y=0; y<inst.Y; y++){
            for(int l=0; l<inst.L; l++){
                boolean teste = false;
                for(int t=0; t<inst.T; t++){
                    if(v_Alyt[l][y][t] > 0.001){
                       teste = true; 
                       break;
                    }
                }
                if(teste){
                    cplex.output().printf("    (%2d,%2d) | ", y, l);
                    for(int t=0; t<inst.T; t++){
                        cplex.output().printf("%5d ", v_Alyt[l][y][t]);
                    }
                    cplex.output().printf("\n");
                }
            }
        }
        cplex.output().printf("--------------- Tluyt: (y,l,u) x t ---------------------\n");
        for(int y=0; y<inst.Y; y++){
            for(int l=0; l<inst.L; l++){
                for(int u=0; u<inst.L; u++){
                    boolean teste = false;
                    for(int t=0; t<inst.T; t++){
                        if(v_Tluyt[l][u][y][t] > 0.001){
                           teste = true; 
                           break;
                        }
                    }
                    if(teste){
                        cplex.output().printf(" (%2d,%2d,%2d) | ", y, l, u);
                        for(int t=0; t<inst.T; t++){
                            cplex.output().printf("%5d ", v_Tluyt[l][u][y][t]);
                        }
                        cplex.output().printf("\n");
                    }
                }
            }
        }



        cplex.output().printf("--------------- Xikt: (k,i) x T ---------------------\n");
        for(int k=0; k<inst.K; k++){
            for(int i=0; i<inst.N; i++){
                boolean teste = false;
                for(int t=0; t<inst.T; t++){
                    if(v_Xikt[i][k][t] > 0.001){
                       teste = true; 
                       break;
                    }
                }
                if(teste){
                    cplex.output().printf("    (%2d,%3d) | ", k, i);
                    for(int t=0; t<inst.T; t++){
                        cplex.output().printf("%5d ", v_Xikt[i][k][t]);
                    }
                    cplex.output().printf("\n");
                }
            }
        }

        cplex.output().printf("--------------- Yuikt: (u,i,k) x T ---------------------\n");
        for(int y=0; y<inst.Y; y++){
            for(int k : inst.Ky[y]){
                for(int l=0; l<inst.L; l++){
                    for(int i : inst.Nl[l]){
                        for(int u=0; u<inst.L; u++){
                            boolean teste = false;
                            for(int t=0; t<inst.T; t++){
                                if(v_Yuikt[u][i][k][t] > 0.001){
                                   teste = true; 
                                   break;
                                }
                            }
                            if(teste){
                                cplex.output().printf(" (%2d,%3d,%2d) | ", u, i, k);
                                for(int t=0; t<inst.T; t++){
                                    cplex.output().printf("%5d ", v_Yuikt[u][i][k][t]);
                                }
                                cplex.output().printf("\n");
                            }

                        }

                    }
                }
            }
        }

        cplex.output().printf("--------------- I0ip: (i) x p ---------------------\n");
        cplex.output().printf("             | ");
        for(int p=0; p<inst.P; p++){
            cplex.output().printf("%12s ", p);
        }
        cplex.output().printf("\n");
        for(int i=0; i<inst.N; i++){
            boolean teste = false;
            for(int p=0; p<inst.P; p++){
                if(v_I0ip[i][p] > 0.001){
                   teste = true; 
                   break;
                }
            }
            if(teste){
                cplex.output().printf("       (%3d) | ", i);
                for(int p=0; p<inst.P; p++){
                    cplex.output().printf("%12.2f ", v_I0ip[i][p]);
                }
                cplex.output().printf("\n");
            }
        }


        cplex.output().printf("--------------- Iipt: (p,i) x t ---------------------\n");
        for(int p=0; p<inst.P; p++){
            for(int i=0; i<inst.N; i++){
                boolean teste = false;
                for(int t=0; t<inst.T; t++){
                    if(v_Iipt[i][p][t] > 0.001){
                       teste = true; 
                       break;
                    }
                }
                if(teste){
                    cplex.output().printf("    (%2d,%3d) | ", p, i);
                    for(int t=0; t<inst.T; t++){
                        cplex.output().printf("%12.2f ", v_Iipt[i][p][t]);
                    }
                    cplex.output().printf("\n");
                }
            }
        }
        cplex.output().printf("--------------- Wipvt: (i,p,v) x t ---------------------\n");
        for(int i=0; i<inst.N; i++){
            for(int p=0; p<inst.P; p++){
                for(int v=0; v<inst.P; v++){
                    boolean teste = false;
                    for(int t=0; t<inst.T; t++){
                        if(v_Wipvt[i][p][v][t] > 0.001){
                           teste = true; 
                           break;
                        }
                    }
                    if(teste){
                        cplex.output().printf(" (%3d,%2d,%2d) | ", i, p, v);
                        for(int t=0; t<inst.T; t++){
                            cplex.output().printf("%12.2f ", v_Wipvt[i][p][v][t]);
                        }
                        cplex.output().printf("\n");
                    }
                }
            }
        }
    }
    public static int[] getValues(IloCplex cplex, IloIntVar V[]) throws UnknownObjectException, IloException{
        int x[] = new int[V.length];
        for(int i=0; i<V.length; i++){
            x[i] = (int)(cplex.getValue(V[i]) + 0.5);
        }
        return x;
    }
    public static int[][] getValues(IloCplex cplex, IloIntVar V[][]) throws UnknownObjectException, IloException{
        int x[][] = new int[V.length][];
        for(int i=0; i<V.length; i++){
            x[i] = getValues(cplex, V[i]);
        }
        return x;
    }
    public static int[][][] getValues(IloCplex cplex, IloIntVar V[][][]) throws UnknownObjectException, IloException{
        int x[][][] = new int[V.length][][];
        for(int i=0; i<V.length; i++){
            x[i] = getValues(cplex, V[i]);
        }
        return x;
    }
    public static int[][][][] getValues(IloCplex cplex, IloIntVar V[][][][]) throws UnknownObjectException, IloException{
        int x[][][][] = new int[V.length][][][];
        for(int i=0; i<V.length; i++){
            x[i] = getValues(cplex, V[i]);
        }
        return x;
    }
    
    public static double[] getValues(IloCplex cplex, IloNumVar V[]) throws UnknownObjectException, IloException{
        return cplex.getValues(V);
    }
    public static double[][] getValues(IloCplex cplex, IloNumVar V[][]) throws UnknownObjectException, IloException{
        double X[][] = new double[V.length][];
        for(int i=0; i<V.length; i++){
            X[i] = getValues(cplex, V[i]);
        }
        return X;
    }
    public static double[][][] getValues(IloCplex cplex, IloNumVar V[][][]) throws UnknownObjectException, IloException{
        double X[][][] = new double[V.length][][];
        for(int i=0; i<V.length; i++){
            X[i] = getValues(cplex, V[i]);
        }
        return X;
    }
    public static double[][][][] getValues(IloCplex cplex, IloNumVar V[][][][]) throws UnknownObjectException, IloException{
        double X[][][][] = new double[V.length][][][];
        for(int i=0; i<V.length; i++){
            X[i] = getValues(cplex, V[i]);
        }
        return X;
    }
    public static IloNumExpr Sum(IloCplex cplex, IloNumExpr M[]) throws IloException{
        return cplex.sum(M);
    }
    public static IloNumExpr Sum(IloCplex cplex, IloNumExpr M[][]) throws IloException{
        IloNumExpr aux[] = new IloNumExpr[M.length];
        for(int i=0; i<M.length; i++){
            aux[i] = Sum(cplex, M[i]);
        }
        return cplex.sum(aux);
    }
    public static IloNumExpr Sum(IloCplex cplex, IloNumExpr M[][][]) throws IloException{
        IloNumExpr aux[] = new IloNumExpr[M.length];
        for(int i=0; i<M.length; i++){
            aux[i] = Sum(cplex, M[i]);
        }
        return cplex.sum(aux);
    }
    public static IloNumExpr Sum(IloCplex cplex, IloNumExpr M[][][][]) throws IloException{
        IloNumExpr aux[] = new IloNumExpr[M.length];
        for(int i=0; i<M.length; i++){
            aux[i] = Sum(cplex, M[i]);
        }
        return cplex.sum(aux);
    }
    
}
