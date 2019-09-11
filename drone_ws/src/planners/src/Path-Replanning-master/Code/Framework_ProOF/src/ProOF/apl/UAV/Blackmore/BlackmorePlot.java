/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.UAV.Blackmore;

import ProOF.apl.UAV.Swing.Graphics2DReal;
import ProOF.apl.UAV.gen.linear.LinearState;
import ProOF.apl.UAV.map.Plot;
import ProOF.com.Linker.LinkerResults;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.Locale;
import javax.swing.JComboBox;
//import static ProOF.apl.UAV.Blackmore.BlackmorePlot.ALLOC.*;
import ProOF.apl.UAV.map.Obstacle;
import ProOF.com.Linker.LinkerParameters;
import java.awt.Graphics2D;
import java.awt.Rectangle;

/**
 *
 * @author marcio
 */
public class BlackmorePlot extends Plot{
    private final BlackmoreApproach approach;
//    private final int width = 1100;
//    private final int height = 1030;
    
    private BlackmoreModel model = null;
//    private final LinkedList<BlackmoreModel> buffer_model = new LinkedList<BlackmoreModel>();       
     
//    public static enum ALLOC{ ALL, FRR, CRT }
//    private ALLOC type = ALL;
    
//    private JComboBox cb_model;
//    private JComboBox cb_alloc;   
    
    public BlackmorePlot(BlackmoreApproach approach) {
        this.approach = approach;
    }
    @Override
    public String name() {
        return "Blackmore Plot";
    }

    private double resolution;
    @Override
    public void parameters(LinkerParameters link) throws Exception {
        super.parameters(link); //To change body of generated methods, choose Tools | Templates.
        resolution = link.Dbl("resolution", 2.5, 0.1, 10.0, "factor of resolution to save the '.png' results");
    }
    
    @Override
    public void start() throws Exception {
        super.start(); //To change body of generated methods, choose Tools | Templates.
        setTitle(name()+" : "+(approach.N())+"D");
        Config(1100, 750);
        goTo(approach.inst.rectangle(), 0.15);
//        
//        goMove(0, -7.50/2-1.25);   //choise the center
//        goZoom(0.6);            //spefic the zoom
                
        
        if(isPlot()){
//            cb_alloc = new JComboBox(ALLOC.values());
//            cb_alloc.addActionListener(new ActionListener() {
//                @Override
//                public void actionPerformed(ActionEvent e) {
//                    type = ALLOC.values()[cb_alloc.getSelectedIndex()];
//                    //System.out.println("chooise Alloc = "+type.name());
//                    repaint();
//                    repaint_gif();
//                }
//            }); 
//            panel.add(cb_alloc);
//            
//            cb_model = new JComboBox();
//            cb_model.addActionListener(new ActionListener() {
//                @Override
//                public void actionPerformed(ActionEvent e) {
//                    int index = cb_model.getSelectedIndex();
//                    model = buffer_model.get(index);
//                    //System.out.println("chooise Model = "+model.name);
//                    repaint();
//                    repaint_gif();
//                }
//            }); 
//            panel.add(cb_model);
        }
    }

    
    @Override
    public void results(LinkerResults link) throws Exception {
        super.results(link); //To change body of generated methods, choose Tools | Templates.
//        savePNG();
//        saveGIF();
    }
//    public void savePNG() throws Exception{
//        ALLOC curr_type = type;
//        BlackmoreModel curr_model = model;
//        for(ALLOC alloc : ALLOC.values()){
//            type = alloc;
//            int count = 1;
//            for(BlackmoreModel m : buffer_model){
//                this.type = alloc;
//                this.model = m;
//                String prefix = String.format(Locale.ENGLISH, "%s%dD_J%2dT%2dD%7.5f%s%s", 
//                    alloc, approach.N(), approach.inst.J(), approach.Waypoints(), approach.Delta(), 
//                    approach.inst.map_mame(), fBlackmoreOperator.obj.choise().name());
//                
//                String suffix = String.format("%2d%s", count, m.name);
//
//                prefix = prefix.replaceAll(" ", "0");
//                suffix = suffix.replaceAll(" ", "0");
//
//                save(resolution, "./", prefix, suffix);    //2.5 the plot resolution = 2.5*width x 2.5*height
//                count++;
//            }
//        }
//        type = curr_type;
//        model = curr_model;
//        repaint();
//    }
    public void saveGIF() throws Exception{
//        String prefix = String.format(Locale.ENGLISH, "%s%dD_J%2dT%2dD%7.5f%s%s", 
//            "Gif", approach.N(), approach.inst.J(), approach.Waypoints(), approach.Delta(), 
//            approach.inst.map_mame(), fBlackmoreOperator.obj.choise().name());
//        prefix = prefix.replaceAll(" ", "0");
//                
//        save_gif("./", prefix, null);
    }

    @Override
    protected void paintStatic(Graphics2D g2, Rectangle rect) throws Throwable {
//        g2.setColor(Color.BLUE);
//        g2.draw(rect);
    }

    @Override
    protected void paintDynamic(Graphics2DReal gr, double x, double y, double w, double h) throws Throwable {
        //gr.draw(approach.inst.rectangle(), Color.RED);
        
//        gr.setColor(Color.RED);
//        gr.drawOvalR(x+w/2, y+h/2, w/2, h/2);
//        
        //obstacles
        for(Obstacle obs : approach.inst.obstacles){
            obs.paint(gr);
        }
//        if(approach.plot.type() == BlackmorePlot.ALLOC.ALL){
//            for(Obstacle obs : approach.inst.obstacles){
//                obs.paint_sense(gr, 0.05, 0.2);
//            }
//        }
        approach.paint(gr, 10.0);
        if(model!=null){
            model.paint(gr, 10.0);
//            if(type==BlackmorePlot.ALLOC.ALL){
//                for (LinearState state : model.states) {
//                    state.plot.drawTrueAllocation(gr, Color.GREEN, approach.inst.obstacles);
//                }
//            }
        }
    }
//    public ALLOC type() {
//        return type;
//    }
    
    public void addModel(BlackmoreModel model) {
//        if(!buffer_model.contains(model)){
//            buffer_model.addLast(model);
//            if(isPlot()){
//                cb_model.addItem(model.name);
//                cb_model.setSelectedIndex(buffer_model.size()-1);
//            }else{
//                this.model = model;
//                repaint_gif();
//            }
//        }else{
//            if(isPlot()){
//                cb_model.setSelectedIndex(buffer_model.indexOf(model));
//            }else{
//                this.model = model;
//                repaint_gif();
//            }
//        }
        this.model = model;//adiconado
        repaint();
    }
}
