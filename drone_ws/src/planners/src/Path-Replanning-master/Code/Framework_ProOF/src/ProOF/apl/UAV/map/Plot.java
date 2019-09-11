/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.UAV.map;

import ProOF.apl.UAV.Swing.Graphics2DReal;
import ProOF.apl.UAV.Swing.sFrame;
import ProOF.apl.UAV.Swing.sPanel;
import ProOF.apl.UAV.Swing.sPanelDraw;
import ProOF.com.Linker.LinkerApproaches;
import ProOF.com.Linker.LinkerParameters;
import ProOF.com.Linker.LinkerResults;
import ProOF.com.Linker.LinkerValidations;
import ProOF.com.language.Approach;
import com.gif4j.GifEncoder;
import com.gif4j.GifFrame;
import com.gif4j.GifImage;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;

/**
 *
 * @author marcio
 */
public abstract class Plot extends Approach{
    
    public final FlowLayout layout_null = new FlowLayout(FlowLayout.CENTER, 0, 0);
    private sFrame frame;
    protected sPanel panel;
    private Draw draw;
    
    private boolean plot;
    private final int grid = 1;
  
    
    
    @Override
    public String description() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    @Override
    public void services(LinkerApproaches link) throws Exception {
        
    }
    @Override
    public void parameters(LinkerParameters link) throws Exception {
        plot = link.Bool("Enable plot", true);
    }
    @Override
    public void load() throws Exception {
        
    }
    
    public boolean isPlot() {
        return plot;
    }
    
    @Override
    public void start() throws Exception {
        draw = new Draw(){
            @Override
            public void Config(int w, int h) {
                super.Config(w, h-56); //To change body of generated methods, choose Tools | Templates.
            }
        };
        
        if(plot){
            this.frame = new sFrame();
            frame.setLayout(layout_null);
            frame.setTitle(name());
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            
            panel = new sPanel(Color.ORANGE){
                @Override
                public void Config(int w, int h) {
                    super.Config(w, 50); //To change body of generated methods, choose Tools | Templates.
                }
            };
            frame.add(panel);
            
            
            frame.add(draw);
            frame.addComponentListener(new ComponentAdapter() {
                @Override
                public void componentResized(final ComponentEvent e) {
                    Config(frame.getWidth(), frame.getHeight());
                    SwingUtilities.updateComponentTreeUI(frame);
                }
            });
            LookAndFeelInfo[] LookAndFeelds = UIManager.getInstalledLookAndFeels();
            setLookAndFeel(LookAndFeelds[1].getClassName());
            frame.Config(800, 600);
            frame.setVisible(plot);
        }
    }
    public void setTitle(String name){
        if(plot){
            frame.setTitle(name);
        }
    }
    public void Config(int w, int h) {
        if(plot){
            frame.Config(w, h);
        }else{
            draw.Config(w-16, h-38);
        }
    }
    public void repaint() {
        if(plot){
            draw.repaint();
        }
    }
    public void goTo(double x, double y, double w, double h){
        draw.goTo(x+w/2, y+h/2);
    }
    public void goTo(Rectangle rect, double offset){
        draw.goTo(rect.x - rect.width*offset, rect.y-rect.height*offset, rect.width*(1+2*offset), rect.height*(1+2*offset));
    }
    public void goZoom(double zoom){
        draw.goZoom(zoom);
    }
    public void goTo(double Cx, double Cy) {
        draw.goTo(Cx, Cy);
    }
    public void goMove(double dx, double dy) {
        draw.goMove(dx, dy);
    }
    public void restart_system() {
        draw.restart_system();
    }
    
    public void save(double resolution, String dir, String prefix, String suffix){
        BufferedImage img = new BufferedImage(
                (int)(draw.width()*resolution), (int)(draw.height()*resolution), 
                BufferedImage.TYPE_3BYTE_BGR
        );
        Graphics g = img.getGraphics();
        Graphics2D g2 = (Graphics2D) g;
        g2.scale(resolution, resolution);
        try {
            draw.paintComponent(g);
            ImageIO.write(img, "png", new File(dir+(prefix!=null?prefix+"-":"")+job.getName()+(suffix!=null?"-"+suffix:"")+".png"));
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
    }
    
    private final LinkedList<BufferedImage> buffer_gif = new LinkedList<BufferedImage>();
    public void repaint_gif() {
        repaint_gif(1.0);
    }
    public void repaint_gif(double resolution) {
        BufferedImage img = new BufferedImage(
                (int)(draw.width()*resolution), (int)(draw.height()*resolution), 
                BufferedImage.TYPE_3BYTE_BGR
        );
        Graphics g = img.getGraphics();
        Graphics2D g2 = (Graphics2D) g;
        g2.scale(resolution, resolution);
        try {
            draw.paintComponent(g2);
            g2.scale(1/resolution, 1/resolution);
        } catch (Throwable ex) {
            ex.printStackTrace();
            g2.scale(1/resolution, 1/resolution);
            g2.setColor(Color.red);
            g2.drawString(String.format("Exception [%s]", ex.getMessage()), draw.offsetW+20, draw.offsetH-10);
        }
        g2.setColor(Color.BLACK);
        g2.drawString(String.format("%d", buffer_gif.size()), draw.offsetW/2-10, draw.offsetH/2+10);
        
        buffer_gif.add(img);
    }
    public void save_gif(String dir, String prefix, String suffix) throws IOException, Exception{
        save_gif(new File(dir), prefix, suffix);
    }
    public void save_gif(File dir, String prefix, String suffix) throws IOException, Exception{
        if(dir.isDirectory()){
            if(!buffer_gif.isEmpty()){
                //gif.createGif(imgBuffer, new File("saida.gif"), 50, 1);
                GifImage gif = new GifImage();         
                gif.setDefaultDelay(50);
                //gif.addComment("comment");
                for (BufferedImage image : buffer_gif) {
                    gif.addGifFrame(new GifFrame(image));
                }
                gif.setLoopNumber(1);
                GifEncoder.encode(gif, new File(dir,(prefix!=null?prefix+"-":"")+job.getName()+(suffix!=null?"-"+suffix:"")+".gif"));
            }else{
                throw new Exception("Buffer gif is empty");
            }
        }else{
            throw new Exception("The argument is not a directory");
        }
    }
    
    private void setLookAndFeel(String lookAndFeelds) throws UnsupportedLookAndFeelException, ClassNotFoundException, InstantiationException, IllegalAccessException{
        UIManager.setLookAndFeel(lookAndFeelds);
        SwingUtilities.updateComponentTreeUI(frame);
    }
    @Override
    public boolean validation(LinkerValidations link) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    @Override
    public void results(LinkerResults link) throws Exception {
        
    }
    
    protected abstract void paintStatic(Graphics2D g2, Rectangle rect) throws Throwable;
    protected abstract void paintDynamic(Graphics2DReal gr, double x, double y, double w, double h) throws Throwable;
    
    protected class Draw extends sPanelDraw{ 
        public Draw() {
            super(Color.WHITE);
        }
        protected double xi(){
            return inverse(Cx() - (width()-2*offsetW) / (2*zoom()));
        }
        protected double xf(){
            return inverse(Cx() + (width()) / (2*zoom()));
        }
        protected double yi(){
            return inverse(Cy() - (height()-2*offsetH)/ (2*zoom()));
        }
        protected double yf(){
            return inverse(Cy() + (height()) / (2*zoom()));
        }
        
        @Override
        protected void paintStart(Graphics2DReal gr) throws Throwable{
            super.paintStart(gr); //To change body of generated methods, choose Tools | Templates.
            if(grid==1){
                paintRuler(gr, true); 
            }
        }
        @Override
        protected void paintMiddle(Graphics2DReal gr) throws Throwable{
            super.paintMiddle(gr); //To change body of generated methods, choose Tools | Templates.
            paintDynamic(gr, xi(), yi(), xf()-xi(), yf()-yi());
        }
        @Override
        protected void paintEnd(Graphics2DReal gr) throws Throwable{
            super.paintEnd(gr); //To change body of generated methods, choose Tools | Templates.
            if(grid==2){
                paintRuler(gr, true);
            }else{
                paintRuler(gr, false);
            }
            paintStatic(gr.g2, new Rectangle(draw.offsetW, draw.offsetH, draw.width()-draw.offsetW-2, draw.height()-draw.offsetH-2));
        }
    }
}
