/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.UAV.abst.uncertainty;

import ProOF.apl.UAV.abst.Provider;
import jsc.distributions.Normal;

/**
 *
 * @author marcio
 */
public abstract class pStateUncertainty extends Provider{
    private static final double max_x = 0.9999999999999998;
//    private static final double max_f = 5.805018683193452;
//    private static final double correction = 5e15;
    
    
    public static final double inv_erf(double x){
        if(x>+max_x){
            return Double.POSITIVE_INFINITY;
        }else if(x<-max_x){
            return Double.NEGATIVE_INFINITY;
        }else{
            try{
                return Normal.inverseStandardCdf((1+x)/2.0)/Math.sqrt(2);
            }catch(Throwable ex){
                System.err.println("inv_erf("+x+") --> inv_cdf("+((1+x)/2.0)+")invalid");
                ex.printStackTrace();
                return Double.NaN;
            }
        }
    }
    
    public static void main(String args[]){
        double x = 0;
        for(int i=0; i<100; i++){
            System.out.printf("inv-erf(%25.23f) = %25.23f\n", x, inv_erf(x));
            //x += (1-x)/2;
            x += (-1-x)/2;
        }
    }
}
