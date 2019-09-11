
package ProOF.apl.pog.method;

import ProOF.apl.pog.problem.GCILT.GCILTInstance;
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
import java.util.LinkedList;

/**
 *
 * @author Hossomi
 */
public class GCILTFixOpt_v2 extends Run {

    private GCILTInstance inst = new GCILTInstance();
    private double execTime;
    private double epGap;
    private double alpha;
    private double increase;                // taxa de aumento da janela
    private double overlap;		    // Sobreposição
    private double ganho;		    // Ganho minimo para nao aumentar a janela
    private int threads;
    private int size;                       // initial size windows
    private CplexExtended cplex;
    
    private IloIntVar Xikt[][][];
    private IloIntVar Yuikt[][][][];
    private IloNumVar Iipt[][][];   
    private IloNumVar Wipvt[][][][]; 
    private IloNumVar I0ip[][];     
    private IloNumVar Blyt[][][];   
    private IloIntVar Alyt[][][];   
    private IloIntVar Tluyt[][][][];
    private IloNumVar Vlyt[][][];   
    private IloIntVar Zyt[][];    
    
    
    
    private IloNumExpr ObjHold;
    private IloNumExpr ObjSwaping;
    private IloNumExpr ObjTransfer;
    private IloNumExpr ObjOverHold;
    private IloNumExpr ObjValue;
    
    private double best_time;
    
    private int[][][] v_Alyt;
    private int[][][][] v_Tluyt;
    private int[][] v_Zyt;
    
    @Override
    public String name() {
        return "GCILT Fix&Opt";
    }

    @Override
    public String description() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void services(LinkerApproaches link) throws Exception {
        link.add(inst);
    }

    @Override
    public void parameters(LinkerParameters link) throws Exception {
        execTime    = link.Dbl("Time", 3600.0, 1.0, 180000.0);
        epGap       = link.Dbl("Gap Rel", 0.05, 0.0, 100.0);
        alpha       = link.Dbl("alpha", 0.1, 0.0, 1.0);
        ganho       = link.Dbl("ganho", 0.05, 0.0, 1.0);
        overlap     = link.Dbl("overlap", 0.2, 0.0, 1.0);
        size        = link.Int("size", 4, 2, 150);
        threads     = link.Int("Threads", 1, 0, 16);
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
        com.writeDbl("Best Time", best_time);
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
                            System.out.println("prod==null");
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
	for (int y = 0; y < inst.Y; y++) {
	    for(int t = 0; t<inst.T; t++) {
            free(y, t);
	    }
	}
    }
    private void free(int y, int t) throws Exception {
	Zyt[y][t].setMin(0);
	Zyt[y][t].setMax(1);
	
	if (t > 0) {
	    for(int l=0; l<inst.L; l++) {
		Alyt[l][y][t].setMin(0);
		Alyt[l][y][t].setMax(1);
	    }
	}
	
	for(int l=0; l<inst.L; l++) {
	    for(int u=0; u<inst.L; u++) {
		if(l!=u){
		    Tluyt[l][u][y][t].setMin(0);
		    Tluyt[l][u][y][t].setMax(1);
		}
	    }
	}
    }
    
    private void fix(int y, int t) throws Exception {
	Zyt[y][t].setMin(v_Zyt[y][t]);
	Zyt[y][t].setMax(v_Zyt[y][t]);

	if (t > 0) {
	    for(int l=0; l<inst.L; l++){
		Alyt[l][y][t].setMin(v_Alyt[l][y][t]);
		Alyt[l][y][t].setMax(v_Alyt[l][y][t]);
	    }
	}

	for(int l=0; l<inst.L; l++){
	    for(int u=0; u<inst.L; u++){
		if(l!=u){
		    Tluyt[l][u][y][t].setMin(v_Tluyt[l][u][y][t]);
		    Tluyt[l][u][y][t].setMax(v_Tluyt[l][u][y][t]);
		}
	    }
	}
    }

    public void initial_solution() throws Exception{
        v_Alyt  = new int[inst.L][inst.Y][inst.T];
        v_Tluyt = new int[inst.L][inst.L][inst.Y][inst.T];
        v_Zyt   = new int[inst.Y][inst.T];
        
        int LASTy[] = new int[inst.Y];
        for (int y = 0; y < inst.Y; y++) {
            LASTy[y] = inst.ovenStartColor(y);
        }
        
        LinkedList<Integer> Syt[][] = new LinkedList[inst.Y][inst.T];
        for (int t = 0; t < inst.T; t++) {
            for (int y = 0; y < inst.Y; y++) {
                Syt[y][t] = new LinkedList<Integer>();
            }
        }
        
        double Dl[] = new double[inst.L];
        double Rl[] = new double[inst.L];
        System.arraycopy(inst.Dl, 0, Dl, 0, inst.L);

        boolean STOPy[] = new boolean[inst.Y];
        for (int t = 0; t < inst.T; t++) {
            for (int y = 0; y < inst.Y; y++) {
				//clear na celula
				//cel.clear();
				
                int l = LASTy[y];
                boolean flag[] = new boolean[inst.L];
                
                v_Alyt[l][y][t] = 1;
                
                flag[l] = true;
                Syt[y][t].addLast(l);
                
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
                        Syt[y][t].addLast(-1);
                    }else{
                        if(t>0){
                            v_Zyt[y][t-1] = 1;
                        }
                        
                        System.out.printf("[ ");
                        for(int w=0; w<inst.L; w++){
                            System.out.printf("%1.0f ", Dl[w]);
                        }
                        System.out.printf("]");
                        int u = uUtil.indexMax(Dl); //inst.Ly[y].size()<=1 ? l : 
                        
                        
                        
                        if (inst.fullSetup(l, u, y) < remDays ) {//&& u!=l&& Dl[u]>1
                            
                            double q = uUtil.minDbl(
                                        flag[u] ?  remDays*inst.Cly[u][y] : (remDays-inst.STluy[l][u][y])*inst.Cly[u][y],
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
                                Syt[y][t].addLast(u);
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
        for (int y = 0; y < inst.Y; y++) {
            System.out.printf("y = %d | ", y);
            for(int t=0; t<inst.T; t++){
                for(int l : Syt[y][t]){
                    System.out.printf("%d ", l);
                }
                System.out.printf("| ");
            }
            System.out.println();
        }
        
        for(int t=0; t<inst.T; t++){
            for (int y = 0; y < inst.Y; y++) {
                fix(y,t);
            }
        }
        cplex.setParam(IloCplex.DoubleParam.TiLim, 120);
        cplex.setParam(IloCplex.DoubleParam.EpGap, 0.80);
        if(cplex.solve()){
            print();
            
        }else{
            throw new Exception("initial solution is not valid");
        }
    }
    
    @Override
    public void execute() throws Exception {
        
        //final double overlap = 0.2;		    // Sobreposição
        //final double ganho = 0.05;		    // Ganho minimo para nao aumentar a janela
        
        //epGap       = 0.05;
        //execTime    = 3600.0;			    // Tempo de execução
        //alpha       = 0.1;			    // Redução de tempo de janela
        //threads     = 1;			    // Tempo da janela = execTime * <tamanho da janela> * alpha / (Y*T)
        
        final long   initial = System.nanoTime();
	//final int    wInitial = (int)(1/overlap + 0.9999);//3*inst.Y;
        
        
        cplex.use(new IloCplex.IncumbentCallback() {
            @Override
            protected void main() throws IloException {
                best_time = (System.nanoTime() - initial)/1e9;
            }
        });
        
        //Escrita do modelo em arquivo
        //cplex.exportModel("LTGCIP.lp");

	
	
        
        
        //Modo de paralelismo, numero de threads
        cplex.setParam(IloCplex.IntParam.Threads, threads);
        //Parada por gap absoluto de 100
        //cplex.setParam(IloCplex.DoubleParam.EpAGap, 100);

        //Parada por gap relativo
        //cplex.setParam(IloCplex.DoubleParam.EpGap, 0.000001);
        
        cplex.setParam(IloCplex.IntParam.MIPEmphasis, IloCplex.MIPEmphasis.Feasibility);
        
        initial_solution();

	int wSize = size;
	int wStart = 0;
        double wTime;
	double remTime = execTime - (System.nanoTime() - initial)/1e9;
	boolean repeat;
        
        double best = cplex.getObjValue();
        int count = 0;
        
        cplex.setParam(IloCplex.IntParam.MIPEmphasis, IloCplex.MIPEmphasis.Balanced);
        //Parada por gap relativo
        cplex.setParam(IloCplex.DoubleParam.EpGap, epGap);

        cplex.setWarning(null);
        cplex.setOut(null);
        
	freeAll();
        while(remTime > 0){
	    // --------- HORIZONTAL LOOP ----------
            System.out.println("Starting horizontal loop");
	    wStart = 0;
            repeat = (remTime > 0);
	    while (repeat) {
		
		if(wSize >= inst.T*inst.Y){
		    wTime = Math.max(5,remTime);
		}
		else {
		    //wTime = Math.max( remTime*wSize/(inst.Y*inst.T), 5);
                    wTime = Math.max( remTime*Math.max(wSize*(1-overlap),1)*alpha/(2*inst.Y*inst.T), 5);
		    //wTime = Math.min( wTime, remTime + 5);
		}
		
		System.out.println("----------------------- Start: ["+wStart+"] --------------------------");
		System.out.println("Obj value = "+best);
		System.out.println("Count     = "+count+"/"+(int)((inst.T * inst.Y * 2)/wSize));
		System.out.println("Best time = "+best_time);
                System.out.println("This time = "+(System.nanoTime() - initial)/1e9);
		System.out.println("Rem time  = "+remTime);
		System.out.println("Win time  = "+wTime);

		System.out.println("Var status:");
		for (int i = 0; i < inst.T * inst.Y; i++) {
		    if (i >= wStart && i < wStart + wSize) {
			free(i%inst.Y, i/inst.Y);
			System.out.print("o");
		    }
		    else {
			fix(i%inst.Y, i/inst.Y);
			System.out.print("-");
		    }
		    if ((i+1)%inst.Y == 0) {
			System.out.println();
		    }
		}
		System.out.println();
		
		cplex.setParam(IloCplex.DoubleParam.TiLim, wTime);

		if(cplex.solve()){
		    System.out.println("Solved "+wStart);
		    getSol();
		    if(cplex.getObjValue()*(1+ganho)<best){
			best = cplex.getObjValue();
			count = 0;
		    }
		    else {
			count++;
			//if(count > (int)((inst.T * inst.Y * 2)/wSize) && wSize < inst.T * inst.Y){
			if(count > (int)((inst.T * inst.Y * 2 * (1-increase))/Math.max(wSize*(1-overlap),1)) && wSize < inst.T * inst.Y){
                            count = 0;
			    wSize += Math.max(1,size/2);//inst.Y;
                            wSize = Math.min(wSize, inst.T*inst.Y);
			}
		    }
                    double gap = 100 * (cplex.getObjValue()-cplex.getBestObjValue())/(cplex.getBestObjValue()+1e-10);
                    System.out.println("Gap(%)    = "+gap);
		}
		else {
		    System.out.println("Could not solve "+wStart);
		}
		
		remTime = execTime - (System.nanoTime() - initial)/1e9;
		if ((remTime > 0) && (wStart < inst.T*inst.Y - wSize)) {
		    repeat = true;
		}
		else {
		    repeat = false;
		}
		
		wStart += Math.max((1.0 - overlap) * 1.0*wSize, 1);
	    }
	    
	    // --------- VERTICAL LOOP ----------
	    System.out.println("Starting vertical loop");
            wStart = 0;
            repeat = (remTime > 0);
	    while (repeat) {
		
		if(wSize >= inst.T*inst.Y){
		    wTime = Math.max(5,remTime);
		}
		else {
		    wTime = Math.max( remTime*Math.max(wSize*(1-overlap),1)*alpha/(2*inst.Y*inst.T), 5);
		    //wTime = Math.min( wTime, remTime + 5);
		}
		
		System.out.println("----------------------- Start: ["+wStart+"] --------------------------");
		System.out.println("Obj value = "+best);
		System.out.println("Count     = "+count+"/"+(int)((inst.T * inst.Y * 2)/wSize));
                System.out.println("Best time = "+best_time);
		System.out.println("This time = "+(System.nanoTime() - initial)/1e9);
		System.out.println("Rem time  = "+remTime);
		System.out.println("Win time  = "+wTime);

		System.out.println("Var status:");
		for (int i = 0; i < inst.T * inst.Y; i++) {
		    if (i >= wStart && i < wStart + wSize) {
			free(i/inst.T, i%inst.T);
			System.out.print("o");
		    }
		    else {
			fix(i/inst.T, i%inst.T);
			System.out.print("-");
		    }
		    if ((i+1)%inst.T == 0) {
			System.out.println();
		    }
		}
		System.out.println();
		
		cplex.setParam(IloCplex.DoubleParam.TiLim, wTime);

		if(cplex.solve()){
		    System.out.println("Solved "+wStart);
		    getSol();
		    if(cplex.getObjValue()*(1+ganho)<best){
			best = cplex.getObjValue();
			count = 0;
		    }
		    else {
			count++;
			if(count > (int)((inst.T * inst.Y * 2 * (1-increase))/Math.max(wSize*(1-overlap),1)) && wSize < inst.T * inst.Y){
			    count = 0;
			    wSize += Math.max(1,size/2);//inst.Y;
                            wSize = Math.min(wSize, inst.T*inst.Y);
			}
		    }
                    double gap = 100 * (cplex.getObjValue()-cplex.getBestObjValue())/(cplex.getBestObjValue()+1e-10);
                    System.out.println("Gap(%)    = "+gap);
		}
		else {
		    System.out.println("Could not solve "+wStart);
		}
		
		remTime = execTime - (System.nanoTime() - initial)/1e9;
		if ((remTime > 0) && (wStart < inst.T*inst.Y - wSize)) {
		    repeat = true;
		}
		else {
		    repeat = false;
		}
		
		wStart += Math.max((1.0 - overlap) * 1.0*wSize, 1);
	    }
            
	    remTime = execTime - (System.nanoTime() - initial)/1e9;
        }
        System.out.println("Solution status     = " + cplex.getStatus());
        if(cplex.getStatus() == IloCplex.Status.Feasible || cplex.getStatus() == IloCplex.Status.Optimal){
            print();
        }
    }
    
    private void print() throws IloException, Exception{
        System.out.println("Solution value      = " + cplex.getObjValue());
        System.out.println("Solution Swaping    = " + cplex.getValue(ObjSwaping));
        System.out.println("Solution Transfer   = " + cplex.getValue(ObjTransfer));
        System.out.println("Solution Hold       = " + cplex.getValue(ObjHold));
        System.out.println("Solution OverHold   = " + cplex.getValue(ObjOverHold));


        getSol();

        double[][][] v_Blyt     = getValues(cplex,Blyt);

        int[][][] v_Xikt     = getValues(cplex,Xikt);
        int[][][][] v_Yuikt    = getValues(cplex,Yuikt);

        double[][][][] v_Wipvt  = getValues(cplex,Wipvt);
        double[][][] v_Iipt     = getValues(cplex,Iipt);
        double[][] v_I0ip       = getValues(cplex,I0ip);

        //double[][][] v_Vlyt     = getValues(cplex,Vlyt);


        System.out.printf("--------------- Zyt: (y) x t ---------------------\n");
        System.out.printf("             | ");
        for(int t=0; t<inst.T; t++){
            System.out.printf("%12s ", t);
        }
        System.out.printf("\n");
        for(int y=0; y<inst.Y; y++){
            System.out.printf("       (%3d) | ", y);
            for(int t=0; t<inst.T; t++){
                System.out.printf("%12d ", v_Zyt[y][t]);
            }
            System.out.printf("\n");
        }
        System.out.printf("--------------- Blyt: (y,l) x t ---------------------\n");
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
                    System.out.printf("    (%2d,%2d) | ", y, l);
                    for(int t=0; t<inst.T; t++){
                        System.out.printf("%5.2f ", v_Blyt[l][y][t]);
                    }
                    System.out.printf("\n");
                }
            }
        }

        System.out.printf("--------------- Prod: (k,i) x T ---------------------\n");
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
                            System.out.printf("    (%2d,%3d) | ", k, i);
                            for(int t=0; t<inst.T; t++){
                                double flag = 0;
                                for(int u=0; u<inst.L; u++){
                                    if(v_Yuikt[u][i][k][t]>0.5){
                                        flag = inst.Lluy(u,l,y);
                                    }
                                }
                                System.out.printf("[%5.0f + %3.0f]", v_Xikt[i][k][t]*inst.Pik[i][k]*inst.Ek[k], flag*inst.Pik[i][k]*inst.Ek[k]);
                            }
                            System.out.printf("\n");
                        }
                    }
                }
            }
        }

        System.out.printf("--------------- Alyt: (y,l) x t ---------------------\n");
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
                    System.out.printf("    (%2d,%2d) | ", y, l);
                    for(int t=0; t<inst.T; t++){
                        System.out.printf("%5d ", v_Alyt[l][y][t]);
                    }
                    System.out.printf("\n");
                }
            }
        }
        System.out.printf("--------------- Tluyt: (y,l,u) x t ---------------------\n");
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
                        System.out.printf(" (%2d,%2d,%2d) | ", y, l, u);
                        for(int t=0; t<inst.T; t++){
                            System.out.printf("%5d ", v_Tluyt[l][u][y][t]);
                        }
                        System.out.printf("\n");
                    }
                }
            }
        }



        System.out.printf("--------------- Xikt: (k,i) x T ---------------------\n");
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
                    System.out.printf("    (%2d,%3d) | ", k, i);
                    for(int t=0; t<inst.T; t++){
                        System.out.printf("%5d ", v_Xikt[i][k][t]);
                    }
                    System.out.printf("\n");
                }
            }
        }

        System.out.printf("--------------- Yuikt: (u,i,k) x T ---------------------\n");
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
                                System.out.printf(" (%2d,%3d,%2d) | ", u, i, k);
                                for(int t=0; t<inst.T; t++){
                                    System.out.printf("%5d ", v_Yuikt[u][i][k][t]);
                                }
                                System.out.printf("\n");
                            }
                        }
                    }
                }
            }
        }

        System.out.printf("--------------- I0ip: (i) x p ---------------------\n");
        System.out.printf("             | ");
        for(int p=0; p<inst.P; p++){
            System.out.printf("%12s ", p);
        }
        System.out.printf("\n");
        for(int i=0; i<inst.N; i++){
            boolean teste = false;
            for(int p=0; p<inst.P; p++){
                if(v_I0ip[i][p] > 0.001){
                   teste = true; 
                   break;
                }
            }
            if(teste){
                System.out.printf("       (%3d) | ", i);
                for(int p=0; p<inst.P; p++){
                    System.out.printf("%12.2f ", v_I0ip[i][p]);
                }
                System.out.printf("\n");
            }
        }


        System.out.printf("--------------- Iipt: (p,i) x t ---------------------\n");
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
                    System.out.printf("    (%2d,%3d) | ", p, i);
                    for(int t=0; t<inst.T; t++){
                        System.out.printf("%12.2f ", v_Iipt[i][p][t]);
                    }
                    System.out.printf("\n");
                }
            }
        }
        System.out.printf("--------------- Wipvt: (i,p,v) x t ---------------------\n");
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
                        System.out.printf(" (%3d,%2d,%2d) | ", i, p, v);
                        for(int t=0; t<inst.T; t++){
                            System.out.printf("%12.2f ", v_Wipvt[i][p][v][t]);
                        }
                        System.out.printf("\n");
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
