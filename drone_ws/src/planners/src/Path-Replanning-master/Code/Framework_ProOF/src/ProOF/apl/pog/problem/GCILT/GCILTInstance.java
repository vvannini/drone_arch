/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.pog.problem.GCILT;

import ProOF.com.Linker.LinkerParameters;
import ProOF.opt.abst.problem.Instance;
import ProOF.utilities.uIO;
import java.io.File;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

/**
 *
 * @author Hossomi
 */
public class GCILTInstance extends Instance {

    public File file;
    public int Days;                // Total days in all periods
    public int T, P, Y, K, L, N;    // Instance dimensions
    public int[][] Ky,		    // Set of machines belonging to oven y
		   Fp,		    // Set of ovens belonging to plant p
		   Nl,		    // Set of products of color l
		   Aly0;	    // 1 if color l is initially set for oven y
    public int[] Qt;		    // Number of days in each period
    public int[] Li;		    // Color of product i
    
    public double[][][] Dipt,	    // Demand for product i in plant p, period t
			STluy,	    // Setup time from color l to u in oven y
			SCluy,	    // Setup cost from color l to u in oven y
			Ripv;	    // Transport cost of product i from plants p to v
    public double[][] Pik,	    // Number of products i produced per day in machine k
		      Hip,	    // Inventory cost for product i in plant p
		      Pip;	    // Penalization for unattended demand of product i in plant p
		      //Iip0;	    // Initial inventory of product i in plant p
    
    public double[] Ek,		    // Efficiency of machine k
		    Cy,		    // Capacity of oven y
		    Dl;		    // Demand of color l
    
    public double[][] Cly;	    // Capacidade media para produzir a cor l no forno y 
    
    public Set<Integer>[] Ly,	    // Set of colors that oven y can produce
			  Lk,	    // Set of colors that machine k can produce
			  Iy,	    // Set of products that oven y can produce
			  Ik;	    // Set of products that machine k can produce
    public Set<Integer>[][] Olk;    // Set of products of color l produced in machine k

    @Override
    public String name() {
        return "GCILT-Instance";
    }

    @Override
    public String description() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void parameters(LinkerParameters com) throws Exception {
        file = com.File("Instances for GCILT",null,"dat");
    }

    @Override
    public void load() throws Exception {
        Scanner sc = new Scanner(file);

        T = uIO.ReadIntOpl(sc);
        P = uIO.ReadIntOpl(sc);
        Y = uIO.ReadIntOpl(sc);
        K = uIO.ReadIntOpl(sc);
        L = uIO.ReadIntOpl(sc);
        N = uIO.ReadIntOpl(sc);

        for (int i = 0; i < 5; i++) {
            uIO.ReadVectorIntOpl(sc);
        }
        
        Ky = uIO.ReadMatrixIntOpl(sc);
        Fp = uIO.ReadMatrixIntOpl(sc);
        Nl = uIO.ReadMatrixIntOpl(sc);
        uIO.ReadMatrixIntOpl(sc);

        Qt = uIO.ReadVectorIntOpl(sc);
        Ek = uIO.ReadVectorDoubleOpl(sc);
        Pik = uIO.ReadMatrixDoubleOpl(sc);
        Cy = uIO.ReadVectorDoubleOpl(sc);
        Dipt = uIO.ReadCubeDoubleOpl(sc);

        STluy = uIO.ReadCubeDoubleOpl(sc);
        SCluy = uIO.ReadCubeDoubleOpl(sc);

        Hip = uIO.ReadMatrixDoubleOpl(sc);
        Pip = uIO.ReadMatrixDoubleOpl(sc);
        Ripv = uIO.ReadCubeDoubleOpl(sc);

        //Iip0 = uIO.ReadMatrixDoubleOpl(sc);
        uIO.ReadMatrixDoubleOpl(sc);
        Aly0 = uIO.ReadMatrixIntOpl(sc);

        sc.close();
	
        Days = 0;
        for(int t=0; t<T; t++){
            Days += Qt[t];
        }
        
	// Calculating Dl
        Li  = new int[N];
	Dl = new double[L];
        for(int l=0; l<L; l++){
            for(int i : Nl[l]){
                Li[i] = l;
                for (int p = 0; p < P; p++){
                    for (int t = 0; t < T; t++){
                        Dl[l] += Dipt[i][p][t];
                    }
                }
            }
        }
	
	// Creating Ik
	Ik = new HashSet[K];
	for (int k = 0; k < K; k++) {
	    Ik[k] = new HashSet<Integer>();
	    
	    for (int i = 0; i < N; i++) {
                if (Pik[i][k] > 0.001) {
                    Ik[k].add(i);
                }
            }
	}
	
	// Creating Lk
	Lk = new HashSet[K];
	for (int k = 0; k < K; k++) {
	    Lk[k] = new HashSet<Integer>();
	    
            for(int l=0; l<L; l++){
                int cont = 0;
                for(int i : Nl[l]){
                    if(Pik[i][k]>0.001){
                        cont++;
                    }
                }
                if(cont>0){
                    Lk[k].add(l);
                }
            }
	    /*for (int i: Ik[k]) {
                Lk[k].add(productColor(i));
            }*/
	}
	
	// Creating Iy
	Iy = new HashSet[Y];
	for (int y = 0; y < Y; y++) {
	    Iy[y] = new HashSet<Integer>();
	    
	    for (int k: Ky[y]) {
                Iy[y].addAll(Ik[k]);
            }
	}
	
	// Creating Ly
	Ly = new HashSet[Y];
	for (int y = 0; y < Y; y++) {
	    Ly[y] = new HashSet<Integer>();
	    
            for(int l=0; l<L; l++){
                boolean flag = true;
                for(int k : Ky[y]){
                    int cont = 0;
                    for(int i : Nl[l]){
                        if(Pik[i][k]>0.001){
                            cont++;
                        }
                    }
                    flag = flag && cont>0;
                }
                if(flag){
                    Ly[y].add(l);
                }
            }
            
	    /*for (int i: Iy[y]) {
                Ly[y].add(productColor(i));
            }*/
            
            /*System.out.print("[");
            for(int v : Ly[y]){
                System.out.print(" "+v);
            }
            System.out.println(" ]");*/
	}
        
	
	// Creating Olk
	Olk = new HashSet[L][K];
	for (int l = 0; l < L; l++) {
            for (int k = 0; k < K; k++) {
                Olk[l][k] = new HashSet<Integer>();
                for (int i: Ik[k]){
                    if (productColor(i) == l){
                        Olk[l][k].add(i);
                    }
                }
            }
        }
        double Di[] = new double[N];
        for(int i=0; i<N; i++){
            for(int p=0; p<P; p++){
                for(int t=0; t<T; t++){
                    Di[i] += Dipt[i][p][t];
                }
            }
        }
        
        Cly = new double[L][Y];
        for(int y=0; y<Y; y++){
            for(int l : Ly[y]){
                Cly[l][y] = 0;
                for(int k : Ky[y]){
                    double cont = 0;
                    double avg = 0;
                    for(int i : Nl[l]){
                        if(Pik[i][k]>0.001){
                            avg += Pik[i][k] * Ek[k] * Di[i];
                            cont += Di[i];
                        }
                    }
                    avg = avg / cont;
                    Cly[l][y] += avg;
                }
            }
        }
        System.out.println("------------------ Cyl -------------------");
        for(int y=0; y<Y; y++){
            System.out.printf("Cy = %5.1f\n", Cy[y]);
            System.out.printf("Y  = %d\n", y);
            for(int l : Ly[y]){
                System.out.printf("\tL   = %d\n", l);
                System.out.printf("\tCly = %5.1f\n", Cly[l][y]);
                System.out.printf("\t------------------ Pki -------------------\n");
                
                System.out.printf("\t\t----|");
                for(int i : Nl[l]){
                    System.out.printf("%5d ", i);
                }
                System.out.println();
                
                for(int k : Ky[y]){
                    System.out.printf("\t\tK %2d|", k);
                    for(int i : Nl[l]){
                        if(Pik[i][k]>0.001){
                            System.out.printf("%5.1f ", Pik[i][k]);
                        }else{
                            System.out.printf("   .  ");
                        }
                    }
                    System.out.println();
                }
            } 
        }
        System.out.println("------------------ Dl -------------------");
        for(int l=0; l<L; l++){
            System.out.printf("%8d ", l);
        }
        System.out.println();
        for(int l=0; l<L; l++){
            System.out.printf("%8.1f ", Dl[l]);
        }
        System.out.println();
        
    }

    public double Lluy(int l, int u, int y) {
        return STluy[l][u][y] - ((int) STluy[l][u][y]);
    }

    public int Mikt(int i, int k, int t) {
        return Pik[i][k] > 0.001 ? Qt[t] : 0;
    }

    public int fullSetup( int l, int u, int y ) {
	return (int) (STluy[l][u][y] + Lluy(l, u, y) + 0.001);
    }
    
    public int ovenStartColor(int y) {
        for (int l = 0; l < L; l++) {
            if (Aly0[l][y] == 1) {
                return l;
            }
        }

        return -1;
    }

    public int productColor(int i) throws Exception {
        for (int l = 0; l < L; l++) {
            for (int j : Nl[l]) {
                if (j == i) {
                    return l;
                }
            }
        }
        throw new Exception("product i="+i+" don't have color");
    }
    
    

    public double[] Dl(int y) {
        double R[] = new double[L];
        System.arraycopy(R, 0, Dl, 0, L);
        boolean flag[] = new boolean[L];
        for(int l : Ly[y]){
            flag[l] = true;
        }
        for(int l=0; l<L; l++){
            if(!flag[l]){
                Dl[l] = 0;
            }
        }
        return R;
    }
}
