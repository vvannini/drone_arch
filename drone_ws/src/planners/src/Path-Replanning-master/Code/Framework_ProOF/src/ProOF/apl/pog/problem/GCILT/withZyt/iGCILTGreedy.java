/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.pog.problem.GCILT.withZyt;

import ProOF.apl.pog.problem.GCILT.GCILTLot;
import java.util.ArrayList;
import java.util.Collections;

/**
 *
 * @author marcio
 */
public final class iGCILTGreedy extends aGCILTModel{
    private double[][][] Dipt;		    // Demanda pelo produto i na planta p, periodo t
    //private double[][][][] Wipvt;	    // Quantidade de produto i transportado da planta p para v no periodo t
    //private double[][][] Iipt;		    // Quantidade de produto i estocada na planta p no periodo t
    private double Qip[][];
    private int[] Zy;			    // Periodo em que o forno y parou de produzir
    //private double[] Dl;		    // Demanda da cor l
    //private double[] Yi;                    // Estimativa dinamica dos lotes que podem ser utilizados para produzir o produto i  
    private double Xt[];                    // Slots disponiveis para produção no periodo t
    private double Ord1_it[][];           // 
    
    private double Xikt[][][];
    @Override
    public String name() {
        return "Greedy";
    }
    @Override
    public String description() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    private boolean print = false;
    public void set(boolean print){
        this.print = print;
    }
    @Override
    public void start() throws Exception {
        Dipt = new double[gcilt.inst.N][gcilt.inst.P][gcilt.inst.T];
	Zy = new int[gcilt.inst.Y];
	//Dl = new double[gcilt.inst.L];
	//Wipvt = new double[gcilt.inst.N][gcilt.inst.P][gcilt.inst.P][gcilt.inst.T];
	//Iipt = new double[gcilt.inst.N][gcilt.inst.P][gcilt.inst.T];
        Qip  = new double[gcilt.inst.N][gcilt.inst.P];
        //Yi = new double[gcilt.inst.N];
        Xikt = new double[gcilt.inst.N][gcilt.inst.K][gcilt.inst.T];
        Xt = new double[gcilt.inst.T];
        
        Ord1_it =  new double[gcilt.inst.N][gcilt.inst.T];
    }
    
    @Override
    public double initialize(GCILTCodification codif) throws Exception {
        int penalty = 0;
	
        
        // Initializing data
	//clear(Wipvt);
	//clear(Iipt);
        //clear(Yi);
        clear(Qip);
        clear(Xt);
        clear(Xikt);
        clear(Ord1_it);
        
        
        // Retrieve each color demand
	//System.arraycopy(gcilt.inst.Dl, 0, Dl, 0, gcilt.inst.L);

	for (int i = 0; i < gcilt.inst.N; i++) {
	    for (int p = 0; p < gcilt.inst.P; p++) {
		System.arraycopy(gcilt.inst.Dipt[i][p], 0, Dipt[i][p], 0, gcilt.inst.T);
	    }
	}
	for (int y = 0; y < gcilt.inst.Y; y++) {
	    Zy[y] = gcilt.inst.T;
	}
        
        
        for (int y = 0; y < gcilt.inst.Y; y++) {
            int u = gcilt.inst.ovenStartColor(y);
            int setupTime = 0;
            for(int t = 0; t < gcilt.inst.T; t++){
                for(GCILTLot lot : codif.Syt[y][t]){
                    if(!gcilt.inst.Ly[y].contains(lot.color)){
                        penalty += gcilt.inst.Qt[t];
                    }
                    
                    int l = lot.color;
                    
                    if(u != l){
                        if(setupTime>0){
                            penalty += setupTime;
                            setupTime = 0;
                        }
                        setupTime = gcilt.inst.fullSetup( u, l, y );
                    }
                    
                    if (lot.days <= setupTime) {
                        setupTime -= lot.days;
                        lot.remTime = 0;
                        lot.remCap = 0;
                    }else {
                        lot.remTime = lot.days - setupTime;
                        lot.remCap = lot.remTime * gcilt.inst.Cy[y];
                        setupTime = 0;
                    }
                    
                    /*for(int k : gcilt.inst.Ky[y]){
                        for(int i : gcilt.inst.Olk[l][k]){
                            Yi[i] += lot.remTime;
                        }
                    }*/

                    Xt[t] += lot.remTime;
                    
                    u = l;
                }
            }
        }
        for (int t =  0; t < gcilt.inst.T; t++) {
            for (int y = 0; y < gcilt.inst.Y; y++) {
                for(GCILTLot lot : codif.Syt[y][t]){
                    int l = lot.color;
                    for(int i : gcilt.inst.Nl[l]){
                        for (int s = 0; s <= t; s++) {
                            for (int k = 0; k < gcilt.inst.K; k++) {
                                Ord1_it[i][t] += gcilt.inst.Pik[i][k] * gcilt.inst.Ek[k] * lot.remTime;
                            }
                        }
                    }
                }
            }
        }
        
        
        for (int y = 0; y < gcilt.inst.Y; y++) {
            System.out.printf(" y%d -> ", y);
            for(int t = 0; t < gcilt.inst.T; t++){
                for(GCILTLot lot : codif.Syt[y][t]){
                    System.out.printf("[ %2d %2d ]", lot.color, lot.days);
                }
                System.out.printf(" | ");
            }
            System.out.println();
        }
	
	return penalty;
    }
    
    @Override
    public double execute(GCILTCodification codif) throws Exception {
        for (int t = gcilt.inst.T-1; t >=0 ; t--) {
            for (int p = 0; p < gcilt.inst.P; p++) {
		for (int y : gcilt.inst.Fp[p]) {
                    for(GCILTLot lot : codif.Syt[y][t]){
                        if(lot.remTime>0.001){
                            for(int i : gcilt.inst.Nl[lot.color]){
                                if(t>0){
                                    Dipt[i][p][t-1] += Dipt[i][p][t]; 
                                }else{
                                    Qip[i][p] += Dipt[i][p][t];
                                }
                                Dipt[i][p][t] = 0;
                            }
                        }
                    }
                }
            }
        }
        
        for (int t = 0; t < gcilt.inst.T; t++) {
            boolean flag = false;
            
            do{
                System.out.println("aqui");
                ArrayList<Ord1> ord1 = new ArrayList<Ord1>();
                for (int p = 0; p < gcilt.inst.P; p++) {
                    for(int i = 0; i < gcilt.inst.N; i++){
                        if (Dipt[i][p][t] > 0.001) {
                            ord1.add(new Ord1(p, i, t));
                        }
                    }
                }
                Collections.sort(ord1);
                
                for (Ord1 o : ord1) {
                    flag = production(t, o.p, o.i, codif);
                    if(!flag){
                        break;
                    }
                }
            }while(flag);
        }

	// Calculate each lot remaining time and capacity
	// Also calculate setupCost;
	int lastColor;
	double setupCost = 0.0;

	for (int y = 0; y < gcilt.inst.Y; y++) {
	    lastColor = gcilt.inst.ovenStartColor(y);

	    for (int t = 0; t < gcilt.inst.T; t++) {
		if (t <= Zy[y]) {
		    for (GCILTLot lot : codif.Syt[y][t]) {
			setupCost += gcilt.inst.SCluy[lastColor][lot.color][y];
			lastColor = lot.color;
		    }
		}
	    }
	}

	// Non-supplied demand
	for (int i = 0; i < gcilt.inst.N; i++) {
	    for (int p = 0; p < gcilt.inst.P; p++) {
		for (int t = 0; t < gcilt.inst.T; t++) {
		    if (Dipt[i][p][t] > 0.001) {
			Qip[i][p] += Dipt[i][p][t];
			Dipt[i][p][t] = 0;
		    }
		}
	    }
	}

	// Calculate final cost
	double overtimeCost = 0;
        double holdingCost = 0;
        double transferCost = 0;
        
	/*for (int i = 0; i < gcilt.inst.N; i++) {
	    for (int p = 0; p < gcilt.inst.P; p++) {
		overtimeCost += gcilt.inst.Pip[i][p] * Qip[i][p];

		for (int t = 0; t < gcilt.inst.T; t++) {
		    holdingCost += gcilt.inst.Hip[i][p] * Iipt[i][p][t];

		    for (int v = 0; v < gcilt.inst.P; v++) {
			transferCost += gcilt.inst.Ripv[i][p][v] * Wipvt[i][p][v][t];
		    }
		}
	    }
	} */

        System.out.printf("--------------- Xikt: (i,k) x t ---------------------\n");
        for(int i=0; i<gcilt.inst.N; i++){
            for(int k=0; k<gcilt.inst.K; k++){
                boolean teste = false;
                for(int t=0; t<gcilt.inst.T; t++){
                    if(Xikt[i][k][t] > 0.001){
                       teste = true; 
                       break;
                    }
                }
                if(teste){
                    System.out.printf("    (%2d,%3d) | ", i, k);
                    for(int t=0; t<gcilt.inst.T; t++){
                        System.out.printf("%12.2f ", Xikt[i][k][t]);
                    }
                    System.out.printf("\n");
                }
            }
        }
        
            
        System.out.printf("--------------- I0ip: (i) x p ---------------------\n");
        System.out.printf("             | ");
        for(int p=0; p<gcilt.inst.P; p++){
            System.out.printf("%12s ", p);
        }
        System.out.printf("\n");
        for(int i=0; i<gcilt.inst.N; i++){
            boolean teste = false;
            for(int p=0; p<gcilt.inst.P; p++){
                if(Qip[i][p] > 0.001){
                   teste = true; 
                   break;
                }
            }
            if(teste){
                System.out.printf("       (%3d) | ", i);
                for(int p=0; p<gcilt.inst.P; p++){
                    System.out.printf("%12.2f ", Qip[i][p]);
                }
                System.out.printf("\n");
            }
        }


        /*System.out.printf("--------------- Iipt: (p,i) x t ---------------------\n");
        for(int p=0; p<gcilt.inst.P; p++){
            for(int i=0; i<gcilt.inst.N; i++){
                boolean teste = false;
                for(int t=0; t<gcilt.inst.T; t++){
                    if(Iipt[i][p][t] > 0.001){
                       teste = true; 
                       break;
                    }
                }
                if(teste){
                    System.out.printf("    (%2d,%3d) | ", p, i);
                    for(int t=0; t<gcilt.inst.T; t++){
                        System.out.printf("%12.2f ", Iipt[i][p][t]);
                    }
                    System.out.printf("\n");
                }
            }
        }
        System.out.printf("--------------- Wipvt: (i,p,v) x t ---------------------\n");
        for(int i=0; i<gcilt.inst.N; i++){
            for(int p=0; p<gcilt.inst.P; p++){
                for(int v=0; v<gcilt.inst.P; v++){
                    boolean teste = false;
                    for(int t=0; t<gcilt.inst.T; t++){
                        if(Wipvt[i][p][v][t] > 0.001){
                           teste = true; 
                           break;
                        }
                    }
                    if(teste){
                        System.out.printf(" (%3d,%2d,%2d) | ", i, p, v);
                        for(int t=0; t<gcilt.inst.T; t++){
                            System.out.printf("%12.2f ", Wipvt[i][p][v][t]);
                        }
                        System.out.printf("\n");
                    }
                }
            }
        }*/
        //System.out.println();
        
        double cost = setupCost + overtimeCost + holdingCost + transferCost;
        System.out.printf("fitness  = %g\n",cost);
        System.out.printf("setup    = %g\n",setupCost);
        System.out.printf("overtime = %g\n",overtimeCost);
        System.out.printf("holding  = %g\n",holdingCost);
        System.out.printf("transfer = %g\n",transferCost);
        
	return cost;
    }

    private boolean production(int t, int p, int i, GCILTCodification codif) throws Exception {
        Best best = best(t, p, i, codif);
        if(best.val>1e12){
            return false;
        }
        int s = best.s;
        int v = best.v;
        int y = best.y;
        int k = best.k;
        
        double delta = Math.min(1, best.lot.remTime);
        Xt[t] -= delta;
        best.lot.remTime -= delta;
        
        Xikt[i][k][s] += delta;
        Dipt[i][p][t] -= delta * gcilt.inst.Pik[i][k] * gcilt.inst.Ek[k];

        for(int j=0; j<gcilt.inst.N; j++){
            for (int u = s; u < gcilt.inst.T; u++) {
                Ord1_it[j][u] -= delta * gcilt.inst.Pik[i][k] * gcilt.inst.Ek[k];
            }
        }
        /*
        for (int t =  0; t < gcilt.inst.T; t++) {
            for (int y = 0; y < gcilt.inst.Y; y++) {
                for(GCILTLot lot : codif.lotSchedule[y][t]){
                    int l = lot.color;
                    for(int i : gcilt.inst.Nl[l]){
                        for (int s = 0; s <= t; s++) {
                            for (int k = 0; k < gcilt.inst.K; k++) {
                                Ord1_it[i][t] += gcilt.inst.Pik[i][k] * gcilt.inst.Ek[k] * lot.remTime;
                            }
                        }
                    }
                }
            }
        }
        */
        
        best.lot.remCap -= delta * gcilt.inst.Pik[i][k];
	if (best.lot.remCap < -0.001) {
	    throw new Exception("lot.remCap < 0");
	}
	return true;
    }
    
    private Best best(int t, int p, int i, GCILTCodification codif){
        Best best = new Best();
        for(int s=0; s<t; s++){
            for(int v=0; v<gcilt.inst.P; v++){
                for (int y : gcilt.inst.Fp[v]) {
                    for(GCILTLot lot : codif.Syt[y][s]){
                        for (int k : gcilt.inst.Ky[y]) {
                            if(lot.remTime>0.001){
                                double val;
                                if(gcilt.inst.Pik[i][k]>0.001){
                                    val = (1 + gcilt.inst.Hip[i][p]*(t-s) + gcilt.inst.Ripv[i][v][p]) / (gcilt.inst.Pik[i][k] * gcilt.inst.Ek[k] );
                                }else{
                                    val = gcilt.inst.Pip[i][p];
                                }
                                best.better(s, v, y, k, lot, val);
                            }
                        }
                    }
                }
            }
        }
        return best;
    }

    private class Best{
        private int s;
        private int v;
        private int y;
        private int k;
        private GCILTLot lot;
        private double val;

        public Best() {
            this.val = Double.MAX_VALUE;
        }
        public void better(int s, int v, int y, int k, GCILTLot lot, double val){
            if(val < this.val){
                this.s = s;
                this.v = v;
                this.y = y;
                this.k = k;
                this.lot = lot;
                this.val = val;
            }
        }
    }
    
    private class Ord1 implements Comparable<Ord1> {

	public final int p, i, t;
        
        public Ord1(int p, int i, int t) {
            this.p = p;
            this.i = i;
            this.t = t;
        }
	@Override
	public int compareTo(Ord1 other) {
	    double d = Dipt[i][p][t]/Ord1_it[i][t] - Dipt[other.i][other.p][other.t]/Ord1_it[other.i][other.t];
	    return d > 0 ? 1 : d < 0 ? -1 : 0;
	}
    }
}
