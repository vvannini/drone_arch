/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.pog.problem.PID2;

import ProOF.apl.pog.problem.PID.*;
import ProOF.com.Linker.LinkerResults;
import ProOF.com.Stream.StreamPrinter;
import ProOF.gen.codification.Real.cReal;
import ProOF.opt.abst.problem.Val;
import ProOF.opt.abst.problem.meta.objective.SingleObjective;

/**
 *
 * @author marcio
 */
public class PIDObjective2 extends SingleObjective<PIDProblem2, cReal, PIDObjective2> {
    private double kc;
    private double ti;
    private double td;
    
    public PIDObjective2() throws Exception {
        super();
    }
    
    @Override
    public void evaluate(PIDProblem2 pid, cReal codif) throws Exception {
        kc = Kc(codif);
        ti = Ti(codif);
        td = Td(codif);
        double fitness = EVAL(kc, ti, td);
        set(fitness);
    }
    private double Kc(cReal codif){
        return codif.X(0, 0.18306 , 1.03598);
    }
    private double Ti(cReal codif){
        return codif.X(0, 0.20943 , 1.45024);
    }
    private double Td(cReal codif){
        return codif.X(0, 0.05238 , 0.36256);
    }

    @Override
    public void printer(PIDProblem2 prob, StreamPrinter com, cReal codif) throws Exception {
        com.printDbl("ITAE", abs_value());
        com.printDbl("Kc", kc);
        com.printDbl("Ti", ti);
        com.printDbl("Td", td);
    }

    @Override
    public void results(PIDProblem2 prob, LinkerResults com, cReal codif) throws Exception {
        com.writeDbl("ITAE", abs_value());
        com.writeDbl("Kc", kc);
        com.writeDbl("Ti", ti);
        com.writeDbl("Td", td);
    }
    
    
    
    
    @Override
    public PIDObjective2 build(PIDProblem2 mem) throws Exception {
        return new PIDObjective2();
    }

    @Override
    public void copy(PIDProblem2 prob, PIDObjective2 source) throws Exception {
        super.copy(prob, source);
        this.kc = source.kc;
        this.ti = source.ti;
        this.td = source.td;
        
    }
    
    
    
    
    //--------------------------------- begin : PID ---------------------------------
    public static final int PERIOD_EVAL = 40;
    public static final int PZEROS = 4;
    public static final double T = (1.0/PZEROS);
    public static final double A = 1.0;           //process variation

    public static int pid_clock = 1;

    //--------------------------------- end : PID -----------------------------------

    public static void main(String args[]){
        //--------------- test program ---------------
        int i;
        for(i=0; i<10; i++){
            double val = EVAL(0.749f, 0.963f, 0.434f);
            System.out.printf("%f\n", val);
        }
    }


    //--------------------------------- begin : PID ---------------------------------
    public static double EVAL(double Kc, double Ti, double Td){
        double eval = 0;
        double Yk;
        int t;

        //ITAE = sum(t, t*error)
        for(t=1; t<=PERIOD_EVAL; t++){
            Yk = PID(1, Kc, Ti, Td, t);

            //error = abs (Rk - Yk)
            double error = 1 - Yk;
            error = error<0 ? -error : error;

            eval += error*t*T;
        }

        return eval;
    }

    private static double Ek = 0;
    private static double Yk = 0;
    private static double Mk = 0;

    private static double Ek_1 = 0;
    private static double Yk_1 = 0;
    private static double Mk_1 = 0;
    private static double Uk[] = new double[PZEROS+1];

    public static double PID(double Rk, double Kc, double Ti, double Td, int start){
        
        if(start==1){
            Ek = 0;
            Yk = 0;
            Mk = 0;
            Ek_1 = 0;
            Yk_1 = 0;
            Mk_1 = 0;
            int i;
            for(i=0; i<PZEROS+1; i++){
                Uk[i] = 0;
            }
        }


        double Tf = Td / 10.0f;
        double alpha = Kc * ( 2 * Ti + T ) / (2 * Ti);
        double beta = Kc * ( T - 2 * Ti) / (2 * Ti);

        double gama = (2*Tf-T)/(2*Tf+T);
        double omega = (2*Td + T)/(2*Tf + T);
        double lambda = (T-2*Td)/(2*Tf+T);

        //(4) -> Ek | e(k) = 1-m(k);
        Ek = Rk - Mk;
        if(start<2){
            Ek = 0;
            Yk = 0;
            Mk = 0;
            Uk[0] = 0;
        }else if(start==2){
            //(1) -> Uk | u(k) = kc*(2*ti+T)/(2*ti)*e(k);
            Uk[0] = alpha * Ek;
            //(2) -> Yk | y(k) = 0;
            Yk = 0;
            //(3) -> Mk | m(k) = (2*td+T)/(2*td/10 +T)*y(k);
            Mk = 0;//(2*Td+T)/(2*Td/10+T)*Yk;
        }else{
            //(1) -> Uk | u(k) = u(k-1)+kc*(2*ti+T)/(2*ti)*e(k)+kc*(T-2*ti)/(2*ti)*e(k-1);
            Uk[0] = Uk[1] + alpha*Ek + beta * Ek_1;

            if(start<=PZEROS){
                //(2) -> Yk | y(k) = exp(-T)*y(k-1);
                Yk = Math.exp(-T*A) * Yk_1;
            }else{
                //(2) -> Yk | y(k) = exp(-T)*y(k-1)+(1-exp(-T))*u(k-p_zeros);
                Yk = Math.exp(-T*A) * Yk_1 + (1-Math.exp(-T*A)) * Uk[PZEROS] / A;
            }

            //(3) -> Mk | m(k) = (2*td/10-T)/(2*td/10 +T)*m(k-1)+(2*td+T)/(2*td/10 +T)*y(k)+(T-2*td)/(2*td/10 + T)* y(k-1);
            Mk = gama * Mk_1 + omega * Yk + lambda * Yk_1;
        }

        Ek_1 = Ek;
        Yk_1 = Yk;
        Mk_1 = Mk;
        int i;
        for(i=PZEROS; i>0; i--){
            Uk[i] = Uk[i-1];
        }

        pid_clock++;

        return Yk;
    }
}
