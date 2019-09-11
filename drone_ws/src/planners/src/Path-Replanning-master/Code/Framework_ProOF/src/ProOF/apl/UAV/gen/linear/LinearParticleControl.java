/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.UAV.gen.linear;

import ProOF.apl.UAV.Swing.Graphics2DReal;
import ProOF.apl.UAV.gen.linear.uncertainty.pLinearStateUncertainty;
import ProOF.apl.UAV.gen.linear.vehicle.parts.oLinearDynamic;
import java.awt.Color;
import jsc.distributions.Normal;

/**
 *
 * @author marcio
 */
public class LinearParticleControl {

    private final pLinearStateUncertainty unc;
    private final oLinearDynamic dynamic;

    private final LinearPlotState states[];

    
    public LinearParticleControl(pLinearStateUncertainty unc, oLinearDynamic dynamic, LinearState[] states, LinearControl[] controls) throws Exception {
        this.unc = unc;
        this.dynamic = dynamic;

        this.states = new LinearPlotState[states.length];
        
        calculate1(states, controls);
        //calculateReal(states, controls);
    }
    /**
     * it is suppose that the Sigma(t,row,col) is correct
     * @param t
     * @param x
     * @throws Exception 
     */
    private void calculate1(LinearState[] states, LinearControl[] controls) throws Exception{
        double x[] = states[0].x();
        disturbance(0, x);
        this.states[0] = new LinearPlotState(x, 0);

        for (int t = 0; t < controls.length; t++) {
            x = states[t + 1].x();
            disturbance(t + 1, x);
            this.states[t + 1] = new LinearPlotState(x, t + 1);
            //x = dynamic.next(x, controls[t].u());
        }
    }
    /**
     * it just nothing supposed
     * @param t
     * @param x
     * @throws Exception 
     */
    private void calculateReal(LinearState[] states, LinearControl[] controls) throws Exception{
        double x[] = states[0].x();
        disturbance(0, x);
        this.states[0] = new LinearPlotState(x, 0);
        
        for (int t = 0; t < controls.length; t++) {
            x = dynamic.next(x, controls[t].u());
            disturbance(t + 1, x);
            
            this.states[t + 1] = new LinearPlotState(x, t + 1);
        }
    }
    
    
    private void disturbance(int t, double x[]) throws Exception {
        double w[] = new double[x.length];
        for (int i = 0; i < x.length; i++) {
            w[i] = 0;
            for (int j = 0; j < x.length; j++) {
                double sigma = unc.Sigma(t, i, j);
                if (sigma > 1e-6) {
                    Normal n = new Normal(0, Math.sqrt(sigma));
                    w[i] += n.random();
                }
            }
        }
        for (int i = 0; i < x.length; i++) {
            x[i] += w[i];
        }
    }

    public void paint(Graphics2DReal gr) throws Exception {
        for (LinearPlotState state : states) {
            state.fillPoint(gr, Color.PINK, 0.01);
        }
    }

}
