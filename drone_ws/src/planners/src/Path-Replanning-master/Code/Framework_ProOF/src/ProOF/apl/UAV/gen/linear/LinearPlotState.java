/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.UAV.gen.linear;

import ProOF.apl.UAV.PPCCS.data.ColorPlot;
import ProOF.apl.UAV.PPCCS.util.ConfigSimulation;
import ProOF.apl.UAV.Swing.Graphics2DReal;
import ProOF.apl.UAV.gen.linear.uncertainty.pLinearStateUncertainty;
import ProOF.apl.UAV.map.Obstacle;
import java.awt.Color;
import java.awt.Font;
/**
 *
 * @author marcio
 */
public class LinearPlotState {
    
    public final double x[];
    private final int t;
    private static final Font font = new Font(Font.SANS_SERIF, Font.PLAIN, 20);
    
    public LinearPlotState(double x[], int t) {
        this.x = x;
        this.t = t;
    }
    
    public void fillPoint(Graphics2DReal gr, Color color, double r){
        gr.setColor(color);
        gr.fillOvalR(x[0], x[1], r, r);
    }
    
    public void drawLabel(Graphics2DReal gr, Color color, double r){
        gr.setFont(font);
        if(x.length==6){            
            gr.drawStringR("%d", x[0]-r*5.0, x[1]+r/1.5, t);
            gr.drawStringR("%1.1f", x[0]+r*5.0, x[1]+r/1.5, x[2]);
            //gr.drawStringR("%d | %1.2f", x[0]+r*2.5, x[1]+r/1.5, t, x[2]);
        }else{
            gr.drawStringR("%d", x[0]+r*3, x[1]+r/1.5, t);
        }
    }
    
    public void drawLabel(Graphics2DReal gr, Color color, Font font, double r){
        gr.setFont(font);
        if(x.length==6){
            if (ConfigSimulation.IS_PAINT_TIME){
                gr.setColor(ColorPlot.PLOT_TIME);
                gr.drawStringR("%d", x[0]-r*5, x[1]+r/1.5, t);
            }
            if (ConfigSimulation.IS_PAINT_HEIGHT){
                gr.setColor(ColorPlot.PLOT_HEIGHT);
                gr.drawStringR("%1.1f", x[0]+r*5, x[1]+r/1.5, x[2]);
            }
        }else{
            if (ConfigSimulation.IS_PAINT_TIME){
                gr.setColor(ColorPlot.PLOT_TIME);            
                gr.drawStringR("%d", x[0]+r*3, x[1]+r/1.5, t);
            }
        }
    }
    
    public void drawCenter(Graphics2DReal gr, Color color, double r){
        gr.setColor(color);
        gr.drawOvalR(x[0], x[1], r, r);
    }
    
    public void drawLine(Graphics2DReal gr, Color color, LinearPlotState next){
        gr.setColor(color);
        gr.drawLineR(x[0], x[1], next.x[0], next.x[1]);
    }
    
    public void drawTrueAllocation(Graphics2DReal gr, Color color, Obstacle ...obs) throws Exception{
        double r = Obstacle.trueAllocation(x, obs);
        gr.setColor(color);
        gr.drawOvalR(x[0], x[1], r, r);
    }
    
    public void drawRiskAllocation(Graphics2DReal gr, pLinearStateUncertainty unc, 
            double delta, Color color) throws Exception{
        double r = unc.risk_allocation(t, delta);
        gr.setColor(color);
        gr.drawOvalR(x[0], x[1], r, r);
    }
    
    public void drawRiskAllocation(Graphics2DReal gr, pLinearStateUncertainty unc, 
            double delta, Color color, Obstacle ...obs) throws Exception{
        double r = unc.risk_allocation(t, delta, x, obs);
        gr.setColor(color);
        gr.drawOvalR(x[0], x[1], r, r);
    }
    
//    public double riskAllocation(pLinearStateUncertainty unc, double delta, Obstacle ...obs) throws Exception{
//        int j = Obstacle.indexNear(x, obs);
//        int i = obs[j].indexNear(x);
//        double alloc = unc.risk_allocation(t, delta, obs[j].hyperplans[i].a);
//        return alloc;
//    }
    
//    public double cplex_allocation(double risk_ji[][], Obstacle ...obs) throws Exception{
//        double alloc = Double.POSITIVE_INFINITY;
//        for(int j=0; j<obs.length; j++){
//            double max = Double.NEGATIVE_INFINITY;
//            for(int i=0; i<obs[j].Gj(); i++){
//                if(obs[j].distance(x)>0.0){
//                    max = Math.max(max, risk_ji[j][i]);
//                }
//            }
//            alloc = Math.min(alloc, max);
//        }
//        return alloc;
//    }

    public double[] copy_x() {
        double copy[] = new double[x.length];
        System.arraycopy(x, 0, copy, 0, x.length);
        return copy;
    }      
}
