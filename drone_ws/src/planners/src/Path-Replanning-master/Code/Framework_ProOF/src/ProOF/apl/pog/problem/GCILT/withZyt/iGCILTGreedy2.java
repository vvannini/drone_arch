/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.pog.problem.GCILT.withZyt;

import ProOF.apl.pog.problem.GCILT.GCILTInstance;
import ProOF.apl.pog.problem.GCILT.GCILTLot;
import java.util.ArrayList;
import java.util.Collections;

/**
 *
 * @author marcio
 */
public final class iGCILTGreedy2 extends aGCILTModel{
    private double[][][] Dipt;		    // Demanda pelo produto i na planta p, periodo t
    private double[][][][] Wipvt;	    // Quantidade de produto i transportado da planta p para v no periodo t
    private double[][][] Iipt;		    // Quantidade de produto i estocada na planta p no periodo t
    private int[] Zy;			    // Periodo em que o forno y parou de produzir
    private double[] Dl;		    // Demanda da cor l
    private double[] Yi;                    // Estimativa dinamica dos lotes que podem ser utilizados para produzir o produto i  

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
	Dl = new double[gcilt.inst.L];
	Wipvt = new double[gcilt.inst.N][gcilt.inst.P][gcilt.inst.P][gcilt.inst.T];
	Iipt = new double[gcilt.inst.N][gcilt.inst.P][gcilt.inst.T];
        Yi = new double[gcilt.inst.N];
    }
    
    @Override
    public double initialize(GCILTCodification codif) throws Exception {
        int penalty = 0;
	
        
        // Initializing data
	clear(Wipvt);
	clear(Iipt);
        clear(Yi);
        // Retrieve each color demand
	System.arraycopy(gcilt.inst.Dl, 0, Dl, 0, gcilt.inst.L);

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
                    
                    for(int k : gcilt.inst.Ky[y]){
                        for(int i : gcilt.inst.Olk[l][k]){
                            Yi[i] += lot.remTime;
                        }
                    }
                    
                    u = l;
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
	for (int t = 0; t < gcilt.inst.T; t++) {
	    for (int p = 0; p < gcilt.inst.P; p++) {
		for (int y : gcilt.inst.Fp[p]) {
		    // Only produce if this oven is still active
		    if (t <= Zy[y]) {
                        // Only produce if still has time in lot
			for(GCILTLot lot : codif.Syt[y][t]){
                            while(lot.remTime > 0.001){
                                for (int k : gcilt.inst.Ky[y]) {
                                    int i = bestLocal(1, lot.color, p, y, k, t, lot);
                                    update(1, lot.color, p, y, k, i, t, lot);
                                }
                            }
                        }
		    }
		}
	    }
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
	double qip[][] = new double[gcilt.inst.N][gcilt.inst.P];
	for (int i = 0; i < gcilt.inst.N; i++) {
	    for (int p = 0; p < gcilt.inst.P; p++) {
		qip[i][p] = 0.0;
		for (int t = 0; t < gcilt.inst.T; t++) {
		    if (Dipt[i][p][t] > 0.001) {
			qip[i][p] += Dipt[i][p][t];
			Dipt[i][p][t] = 0;
		    }
		}
	    }
	}

	// Calculate final cost
	double overtimeCost = 0;
        double holdingCost = 0;
        double transferCost = 0;
        
	for (int i = 0; i < gcilt.inst.N; i++) {
	    for (int p = 0; p < gcilt.inst.P; p++) {
		overtimeCost += gcilt.inst.Pip[i][p] * qip[i][p];

		for (int t = 0; t < gcilt.inst.T; t++) {
		    holdingCost += gcilt.inst.Hip[i][p] * Iipt[i][p][t];

		    for (int v = 0; v < gcilt.inst.P; v++) {
			transferCost += gcilt.inst.Ripv[i][p][v] * Wipvt[i][p][v][t];
		    }
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
                if(qip[i][p] > 0.001){
                   teste = true; 
                   break;
                }
            }
            if(teste){
                System.out.printf("       (%3d) | ", i);
                for(int p=0; p<gcilt.inst.P; p++){
                    System.out.printf("%12.2f ", qip[i][p]);
                }
                System.out.printf("\n");
            }
        }


        System.out.printf("--------------- Iipt: (p,i) x t ---------------------\n");
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
        }
        //System.out.println();
        
        double cost = setupCost + overtimeCost + holdingCost + transferCost;;
        System.out.printf("fitness  = %g\n",cost);
        System.out.printf("setup    = %g\n",setupCost);
        System.out.printf("overtime = %g\n",overtimeCost);
        System.out.printf("holding  = %g\n",holdingCost);
        System.out.printf("transfer = %g\n",transferCost);
        
	return cost;
    }

    

    private int bestLocal(double delta, int l, int p, int y, int k, int t, GCILTLot lot) {
	GCILTInstance inst = gcilt.inst;
	final int P = inst.P;
	final int T = inst.T;


        
	double bestCost = 1e12;
	int besti = -1;
	//double minStock = 1e12;
	// Initialize with the lowest inventory cost
	// Because if no demand is found to be supplied, this production will
	// be turned into extra production and carried over the entire time horizon
	for (int j: inst.Olk[l][k]) {
	    double stock = inst.Pip[j][p] * inst.Hip[j][p] * delta * inst.Pik[j][k] * inst.Ek[k] * (T-t);
	    if (stock < bestCost) {
		bestCost = stock;
		besti = j;
	    }
	}
       
        bestCost = 1e12;
	// Search a plant v, in period s, to supply demand of product i
	for (int i: inst.Olk[l][k]) {
	    for (int v = 0; v < P; v++) {
		for (int s = t; s < T; s++) {
		    double produced = delta * inst.Pik[i][k] * inst.Ek[k];
		    int period = s - t;

		    if (Dipt[i][v][s] > 0 && lot.remCap / (lot.remTime * inst.Ky[y].length) +0.001 >= inst.Pik[i][k]) {
			// Demand is greater than production
			// Calculate full production transfer
			double cost = 1e10;
                        final double param = 1;
                        if (Dipt[i][v][s] >= produced) {
			    cost = (inst.Ripv[i][p][v] + inst.Hip[i][v] * period + param )/ produced;
                            //cost = param * (period+1)  - produced;
			} // Production is greater than demand
			// Calculate needed transfer and remaining production is
			// carried over the entire time horizon
			else {
			    cost = (inst.Ripv[i][p][v] + inst.Hip[i][p] * period + param*1000) / Dipt[i][v][s] + 
			    	   (inst.Hip[i][p] * (T + 1 - t) + param*1000) / (produced - Dipt[i][v][s]);
                            //cost =  100 * param * (period+1) - produced;
			}
                        //cost = (cost * Yi[i])/Dipt[i][v][s];
                        cost = (Yi[i]+1)/(produced * Dipt[i][v][s]+1);
			if (cost < bestCost) {
			    bestCost = cost;
			    besti = i;
			}
		    }
		}
	    }
	}

	return besti;
    }

    private void update(double delta, int l, int p, int y, int k, int i, int t, GCILTLot lot) throws Exception {
	GCILTInstance inst = gcilt.inst;
	final int P = inst.P;
	final int T = inst.T;
	double produced = delta * inst.Pik[i][k] * inst.Ek[k];

	// Subtracting capacity consumed by this production
	// If a production is not supported, an error ocurred
	lot.remCap -= delta * inst.Pik[i][k];
	if (lot.remCap < -0.001) {
	    throw new Exception("lot.remCap < 0");
	}
	
	lot.remTime -= delta;


	// Preparing a list of demand slots to be supplied,
	// sorted by transfer and inventory cost
	ArrayList<Slot> slotList = new ArrayList<Slot>();
	for (int s = t; s < T; s++) {
	    for (int v = 0; v < P; v++) {
		if (Dipt[i][v][s] > 0.001) {
		    slotList.add(new Slot(i, t, p, s, v));
		}
	    }
	}

	Collections.sort(slotList);

	// Now, distribute production from the cheapest
	// to most expensive

	double send;
	int v, s;
	for (Slot slot : slotList) {
            v = slot.v;
	    s = slot.s;
	    if(Dipt[i][v][s]>0.001){
                send = Math.min(Dipt[i][v][s] , produced);

                // Calculate transfer cost
                if (p != v) {
                    Wipvt[i][p][v][t] += send;
                }

                // Calculate inventory cost
                //for (int u = t + 1; u < s; u++) {
                for (int u = t; u < s; u++) {
                    Iipt[i][v][u] += send;
                }

                // Deduct produced quantity
                Dipt[i][v][s] -= send;
                produced -= send;

                if (produced < 0.001) {
                    break;
                }
            }
	}

	// Remaining production is sent to last period
	// as inventory
	if (produced > 0) {
            //for (int u = t + 1; u < T; u++) {
	    for (int u = t; u < T; u++) {
		Iipt[i][p][u] += produced;
	    }
	}
        
        for(int j : gcilt.inst.Olk[l][k]){
            Yi[j] -= delta;
        }
        

	/*for (int j = 0; j < N; j++)
	 if (inst.getProductColor(j) == l)
	 Pi[j] -= 1;*/
    }

    private class Slot implements Comparable<Slot> {

	public int s, v;
	public double value;

	public Slot(int i, int t, int p, int s, int v) {
	    this.s = s;
	    this.v = v;
	    value = gcilt.inst.Ripv[i][p][v] + gcilt.inst.Hip[i][p] * (s - t);
	}

	@Override
	public int compareTo(Slot other) {
	    double d = this.value - other.value;
	    return (d > 0) ? (1) : ((d < 0) ? (-1) : (0));
	}
    }


}
