/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.pog.problem.MatrixDinamics;

import ProOF.com.Linker.LinkerParameters;
import ProOF.opt.abst.problem.Instance;
import ProOF.utilities.uIO;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 *
 * @author marcio
 */
public class MDInstance extends Instance{
    public final int M = 2;
    
    private File file;
    public int T;           //number of times
    public int I;           //number of inputs
    public int S;           //number of maped states
    
    public double dt;
    public double inputs[][];
    public double states[][];
    
    @Override
    public String name() {
        return "Instance-MD";
    }
    @Override
    public String description() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    @Override
    public void parameters(LinkerParameters win) throws Exception {
        file = win.File("Instances for MD",null,"txt");
    }
    @Override
    public void load() throws FileNotFoundException, Exception {
        Scanner sc = new Scanner(file);
        T = uIO.ReadInt(sc);
        I = uIO.ReadInt(sc);
        S = uIO.ReadInt(sc);
        double times[] = uIO.ReadVectorDouble(sc);
        double in[][] = uIO.ReadMatrixDouble(sc);
        double st[][] = uIO.ReadMatrixDouble(sc);
        sc.close();
        
        dt = 0;
        for(int t=1; t<times.length; t++){
            if(times[t]<times[t-1]){
                throw new Exception("time[t] < time[t-1]");
            }
            dt += (times[t]-times[t-1]);
        }
        dt = dt / (times.length-1);
        
        System.out.println("T = "+T);
        System.out.println("I = "+I);
        System.out.println("S = "+S);
        System.out.println("dt = "+dt);
        System.out.println("in = "+in.length);
        System.out.println("st = "+st.length);
        
        inputs = new double[I][T];
        for(int i=0; i<I; i++){
            for(int t=0; t<T; t++){
                inputs[i][t] = find(times[0]+t*dt, 0, T-1, times, in[i], dt);
            }
        }
        states = new double[S][T];
        for(int s=0; s<S; s++){
            for(int t=0; t<T; t++){
                states[s][t] = find(times[0]+t*dt, 0, T-1, times, st[s], dt);
            }
        }
        
        System.out.printf("%8s;%8s;%8s;%8s;%8s;%8s;%8s;%8s;%8s;\n", "time", "Longitude", "Latitude", "Altitude", "Roll", "Pitch", "Throttle", "Aileron", "Elevator");
        for(int t=0; t<T; t++){
            System.out.printf("%8g;", times[0]+t*dt);
            for(int s=0; s<S; s++){
                System.out.printf("%8g;", states[s][t]);
            }
            for(int i=0; i<I; i++){
                System.out.printf("%8g;", inputs[i][t]);
            }
            System.out.println();
        }
    }

    private static double find(double key, int a, int b, double[] times, double[] data, double dt) {
        int m = (a+b)/2;
        if(key<times[m]){
            if(a>=b){
                return data[a];
            }else{
                return find(key, a, m-1, times, data, dt);
            }
        }else if(key>times[m]){
            if(a>=b){
                return data[b];
            }else{
                return find(key, m+1, b, times, data, dt);
            }
        }else{
            return data[m];
        }
    }
}
