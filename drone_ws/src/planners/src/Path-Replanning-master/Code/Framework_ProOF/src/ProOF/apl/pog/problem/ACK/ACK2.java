/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.pog.problem.ACK;

import ProOF.com.Linker.LinkerParameters;
import ProOF.gen.codification.FunctionSingle.RealSingle;

/**
 *
 * @author marcio
 */
public class ACK2 extends RealSingle{
    private int n;
    @Override
    public String name() {
        return "ACK[0]";
    }
    @Override
    public void parameters(LinkerParameters link) throws Exception {
        super.parameters(link);
        n = link.Int("N", 10, 1, 1000000000);
    }
    @Override
    public int size() throws Exception {
        return n;
    }

    @Override
    protected double F(double[] X) throws Exception {
        double sum1 = 0;
        double sum2 = 0;
        for(int i=0; i<n; i++){
            //Decoder
            double xi = decode(X[i], -30, +30);
            sum1 += xi * xi;
            sum2 += Math.cos(2*Math.PI*xi);
        }
        //Evaluate
        return  -20.0*Math.exp(-0.02*Math.sqrt(sum1/n)) - Math.exp(sum2/n) + 20 + Math.E;
    }
    
}
