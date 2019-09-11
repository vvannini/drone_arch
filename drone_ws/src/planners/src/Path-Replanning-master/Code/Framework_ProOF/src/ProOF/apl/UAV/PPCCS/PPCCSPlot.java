/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.UAV.PPCCS;

import ProOF.apl.UAV.PPCCS.util.ConfigSimulation;
import ProOF.apl.UAV.Swing.Graphics2DReal;
import ProOF.apl.UAV.map.Plot;
import ProOF.com.Linker.LinkerResults;
import ProOF.apl.UAV.map.Obstacle;
import ProOF.com.Linker.LinkerParameters;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.Locale;

/**
 *
 * @author marcio e jesimar
 */
public class PPCCSPlot extends Plot{
    
    private final PPCCSApproach approach;    
    private PPCCSModel model = null;
    private double resolution;
    private final double scaleMap = 1000.0;//Mapas Marcio = 10.0  Mapas Jesimar = 1000.0
   
    public PPCCSPlot(PPCCSApproach approach) {
        this.approach = approach;
    }
    
    @Override
    public String name() {
        return "PPCCS Plot";
    }
    
    @Override
    public void parameters(LinkerParameters link) throws Exception {
        super.parameters(link);
        resolution = link.Dbl("resolution", 2.5, 0.1, 10.0, 
                "factor of resolution to save the '.png' results");
    }
    
    @Override
    public void start() throws Exception {
        super.start(); 
        setTitle(name()+" : "+(approach.N())+"D");
        Config(1100, 750);
        goTo(approach.inst.rectangle(), 0.15);               
    }
    
    @Override
    public void results(LinkerResults link) throws Exception {
        super.results(link);
        if (ConfigSimulation.IS_SAVE_IMAGE_SOLUTION_FINAL){
            savePNG();
        }
    }
    
    public void savePNG() throws Exception{
        String prefix = String.format(Locale.ENGLISH, "%dD_T%2dD%7.5f%s%s", 
                approach.N(), approach.Waypoints(), approach.Delta(), 
            approach.inst.map_mame(), fPPCCSOperator.obj.choise().name());

        String suffix = String.format("%s", model.name);

        prefix = prefix.replaceAll(" ", "0");
        suffix = suffix.replaceAll(" ", "0");

        save(resolution, "./", prefix, suffix);    //2.5 the plot resolution = 2.5*width x 2.5*height
    }

    @Override
    protected void paintStatic(Graphics2D g2, Rectangle rect) throws Throwable {
        
    }

    @Override
    protected void paintDynamic(Graphics2DReal gr, double x, double y, double w, 
            double h) throws Throwable {

//        for(int i=approach.inst.obstacles.length-1; i>=0; i--){
//            approach.inst.obstacles[i].paint(gr);
//        }
        
        for(Obstacle obs : approach.inst.obstacles){
            obs.paint(gr);
        }
                
        if (ConfigSimulation.IS_PAINT_NORM){
            for(Obstacle obs : approach.inst.obstacles){
                obs.paint_sense(gr, 5, 30);
            }
        }
        
        approach.paint(gr, scaleMap);
        if(model!=null){
            model.paint(gr, scaleMap);
        }
    }
    
    public void addModel(PPCCSModel model) {
        this.model = model;
        repaint();
    }
}
