/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.pog.problem.TSP;

import ProOF.com.Linker.LinkerParameters;
import ProOF.opt.abst.problem.Instance;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 *
 * @author marcio
 */
public class TSPInstance extends Instance{
    private File file;
    public int N;
    public double Cij[][];
    public double optimal;
    
    public double adj_i[][];
    
    @Override
    public String name() {
        return "Instance-TSP";
    }
    @Override
    public String description() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    @Override
    public void parameters(LinkerParameters win) throws Exception {
        file = win.File("Instances for TSP",null,"txt");
    }
    @Override
    public void load() throws FileNotFoundException {
        Scanner sc = new Scanner(file);
        sc.nextLine();
        this.optimal = Double.parseDouble(sc.nextLine());
        sc.nextLine();
        this.N = Integer.parseInt(sc.nextLine());
        sc.nextLine();
        this.Cij = new double[N][N];
        for(int i=0; i<N; i++){
            for(int j=0; j<N; j++){
                Cij[i][j] = sc.nextDouble();
            }
        }
        sc.close();
        
        adj_i = new double[N][N-1];
        for(int i=0; i<N; i++){
            int k=0;
            for(int j=0; j<N; j++){
                if(i!=j){
                    adj_i[i][k] = Cij[i][j];
                    k++;
                }
            }
            for(k=1; k<N-1; k++){
                double aux = adj_i[i][k];
                int j = k-1;
                while(j>=0 && adj_i[i][j]>aux){
                    adj_i[i][j+1] = adj_i[i][j];
                    j--;
                }
                adj_i[i][j+1] = aux;
            }
        }
    }    
}
