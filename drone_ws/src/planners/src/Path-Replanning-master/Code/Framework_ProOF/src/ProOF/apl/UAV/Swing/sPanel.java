/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.UAV.Swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.LayoutManager;
import java.util.LinkedList;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 *
 * @author marcio
 */
public class sPanel extends JPanel implements iDimensions{

    private static final int WGAP = 5;
    private static final int HGAP = 5;
    
    public final iUpdate up;
    protected boolean dinamicHeight = false;
    
    private final Color cDEFAULT;// = Color.LIGHT_GRAY;

    public sPanel(Color default_color) {
        this(new FlowLayout(FlowLayout.CENTER, WGAP, HGAP), default_color);
    }
    public sPanel(iUpdate up, Color default_color) {
        this(new FlowLayout(FlowLayout.CENTER, WGAP, HGAP), up, default_color);
    }
    public sPanel(final LayoutManager layout, Color default_color) {
        cDEFAULT = default_color;
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                setLayout(layout);
                setBackground(cDEFAULT);
            }
        });
        this.up = new iUpdate() {
            @Override
            public void update() {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        Update();
                    }
                });
            }
        };
    }
    public sPanel(final LayoutManager layout, iUpdate up, Color default_color) {
        cDEFAULT = default_color;
        this.up = up;
        
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                setLayout(layout);
                setBackground(cDEFAULT);
            }
        });
    }

    public void changeToDefaultBackground() {
        chageBackground(cDEFAULT);
    }
    public void chageBackground(final Color bg){
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                setBackground(bg); //To change body of generated methods, choose Tools | Templates.
            }
        });
    }
    
    
    
    @Override
    public void setEnabled(boolean enabled) {
        if(enabled){
            this.changeToDefaultBackground();
        }else{
            this.chageBackground(Color.LIGHT_GRAY);
        }
        for(Component c : getComponents()){
            if(c instanceof JLabel){
                
            }else{
                c.setEnabled(enabled);
            }
        }
    }
    public void setEnabledLater(final boolean enabled) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                setEnabled(enabled);
            }
        }); 
    }
    
    private boolean fristSize = true;
    private void fristSize(){
        if(fristSize){
            fristSize = false;
            Dimension preferredSize = getPreferredSize();
            Original(preferredSize.width, preferredSize.height);
        }
    }
    private int ow, oh;
    @Override
    public void Original(int w, int h) {
        this.ow = w;
        this.oh = h;
    }
    @Override
    public int oWidth() {
        fristSize();
        return ow;
    }
    @Override
    public int oHeight() {
        fristSize();
        return oh;
    }
    @Override
    public Dimension oDimension() {
        return new Dimension(ow, oh);
    }
    
    private final void Update(){
        Config(getPreferredSize().width, getPreferredSize().height);
        SwingUtilities.updateComponentTreeUI(this);
    }
    
    
    @Override
    public void Config(int w, int h) {
        if(dinamicHeight){
            for(Component c : getComponents()){
                if(c instanceof iDimensions){
                    ((iDimensions)c).Config(w-12, h-12);
                }
            }
            setPreferredSize(new Dimension(w, height(w)));
        }else{
            setPreferredSize(new Dimension(w, h));
            for(Component c : getComponents()){
                if(c instanceof iDimensions){
                    ((iDimensions)c).Config(w-12, h-12);
                }
            }
        }
    }
    
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(getBackground());
        g.fillRect(0, 0, getPreferredSize().width-1, getPreferredSize().height-1);
        g.setColor(Color.BLACK);
        g.drawRect(0, 0, getPreferredSize().width-1, getPreferredSize().height-1);
    }
    
    public static int minimum(int ...values){
        int min = Integer.MAX_VALUE;
        for(int v : values){
            min = Math.min(min, v);
        }
        return min;
    }
    public static int maximum(int ...values){
        int max = Integer.MIN_VALUE;
        for(int v : values){
            max = Math.max(max, v);
        }
        return max;
    }
    public static int minimum_W(Component ...comp){
        return minimum(comp).getPreferredSize().width;
    }
    public static int maximum_W(Component ...comp){
        return maximum(comp).getPreferredSize().width;
    }
    public static int minimum_H(Component ...comp){
        int min = Integer.MAX_VALUE;
        for(Component v : comp){
            if(min>v.getPreferredSize().height){
                min = v.getPreferredSize().height;
            }
        }
        return min;
    }
    public static int maximum_H(Component ...comp){
        int max = Integer.MIN_VALUE;
        for(Component v : comp){
            if(max<v.getPreferredSize().height){
                max = v.getPreferredSize().height;
            }
        }
        return max;
    }
    public static Component minimum(Component ...comp){
        int min = Integer.MAX_VALUE;
        Component c = null;
        for(Component v : comp){
            if(min>v.getPreferredSize().width){
                min = v.getPreferredSize().width;
                c = v;
            }
        }
        return c;
    }
    public static Component maximum(Component ...comp){
        int max = Integer.MIN_VALUE;
        Component c = null;
        for(Component v : comp){
            if(max<v.getPreferredSize().width){
                max = v.getPreferredSize().width;
                c = v;
            }
        }
        return c;
    }
    
    public void setDinamicHeight(boolean flag){
        dinamicHeight = flag;
    }
    
    public static int max_width(Component vet[], Component ...more){
        int max = 0;
        for(Component c : vet){
            max = Math.max(max, c.getPreferredSize().width);
        }
        for(Component c : more){
            max = Math.max(max, c.getPreferredSize().width);
        }
        return max;
    }
    
    protected void normalize(boolean changeOriginal){
        normalizeWH(changeOriginal, getComponents());
    }
    protected void normalize(boolean changeOriginal, Component ...comps){
        normalizeWH(changeOriginal, comps);
    }
    
    protected void normalizeWH(boolean changeOriginal, Component ...comps){
        int w = minW();
        int h = minH();
        for(Component c : comps){
            c.setPreferredSize(new Dimension(w, h));
            if(changeOriginal && c instanceof iDimensions){
                ((iDimensions)c).Original(w, h);
            }
        }
    }
    protected void normalizeH(boolean changeOriginal){
        int h = minH();
        for(Component c : getComponents()){
            int w = c.getPreferredSize().width;
            c.setPreferredSize(new Dimension(w, h));
            if(changeOriginal && c instanceof iDimensions){
                ((iDimensions)c).Original(w, h);
            }
        }
    }
    protected void normalizeW(boolean changeOriginal){
        int w = minW();
        for(Component c : getComponents()){
            int h = c.getPreferredSize().height;
            c.setPreferredSize(new Dimension(w, h));
            if(changeOriginal && c instanceof iDimensions){
                ((iDimensions)c).Original(w, h);
            }
        }
    }
    public void justified(){
        justified(WGAP);
    }
    public void justified(final int wgap){
        int w = 0;
        int col = 0;
        LinkedList<Component> list = new LinkedList<Component>();
        for(Component c : getComponents()){
            if(c.getPreferredSize().width+2*wgap>this.getPreferredSize().width){
                col=0;
                w = 0;
                continue;
            }
            w += c.getPreferredSize().width;
            col++;
            int width = w + (col+1)*wgap;
            if(width>this.getPreferredSize().width){
                int dif = this.getPreferredSize().width  - (w-c.getPreferredSize().width + (col)*wgap);
                int cont = list.size();
                for(Component comp : list){
                    int dw = dif/cont;
                    comp.setPreferredSize(new Dimension(comp.getPreferredSize().width+dw, comp.getPreferredSize().height));
                    dif -= dw;
                    cont--;
                }
                list.clear();
                w = c.getPreferredSize().width;
                col = 1;
            }
            list.addLast(c);
        }
    }
    
    public int minW(){
        int w = 0;
        for(Component c : getComponents()){
            if(c instanceof iDimensions){
                w = Math.max(w, ((iDimensions)c).oWidth());
            }else{
                w = Math.max(w, c.getPreferredSize().width);
            }
        }
        return w;
    }
    public int minW(final int wgap){
        return minW()+2*wgap;
    }
    public int maxW(){
        int w = 0;
        for(Component c : getComponents()){
            if(c instanceof iDimensions){
                w += ((iDimensions)c).oWidth();
            }else{
                w += c.getPreferredSize().width;
            }
        }
        return w;
    }
    public int maxW(final int wgap){
        return maxW() + (getComponents().length+1)*wgap;
    }
    public int minH(){
        int h = 0;
        for(Component c : getComponents()){
            if(c instanceof iDimensions){
                h = Math.max(h, ((iDimensions)c).oHeight());
            }else{
                h = Math.max(h, c.getPreferredSize().height);
            }
        }
        return h;
    }
    public int minH(final int hgap){
        return minH()+hgap*2;
    }
    public int maxH(){
        int h = 0;
        for(Component c : getComponents()){
            if(c instanceof iDimensions){
                h += ((iDimensions)c).oHeight();
            }else{
                h += c.getPreferredSize().height;
            }
        }
        return h;
    }
    public int maxH(final int hgap){
        return maxH() + (getComponents().length+1)*hgap;
    }
    
    public int height(final int width){
        return height(width, WGAP, HGAP);
    }
    public int height(final int width, final int wgap, final int hgap){
        int h = hgap;
        int w = wgap;
        int max_h = 0;
        for(Component c : getComponents()){
            if(w+c.getPreferredSize().width+wgap <= width){
                max_h = Math.max(max_h, c.getPreferredSize().height);
                w += c.getPreferredSize().width+wgap;
            }else{
                h += max_h+hgap;
                w = wgap+c.getPreferredSize().width+wgap;
                max_h = c.getPreferredSize().height;
            }
        }
        h += max_h+hgap;
        return Math.max(h, minH());
    }
    public boolean find(Component key, Component ...exeptions){
        for(Component e : exeptions){
            if(key.equals(e)){
                return true;
            }
        }
        return false;
    }
    
    public int width_exeptions(final int width, Component ...exeptions){
        return width_exeptions(width, WGAP, exeptions);
    }
    public int width_exeptions(final int width, final int wgap, Component ...exeptions){
        int w = width-wgap;
        for(Component c : getComponents()){
            if(!find(c, exeptions)){
                w -= wgap + c.getPreferredSize().width;
            }
        }
        return w;
    }
    
    public int width(final int height){
        return width(height, WGAP, HGAP);
    }
    public int width(final int height, final int wgap, final int hgap){
        int maxW = maxW(wgap);
        int minW = minW(wgap);
        //System.out.println("---------------------------");
        //System.out.printf("w = %4d ; h(w) = %4d | height = %4d\n", minW, height(minW), height);
        
        for(int w = minW; w <= maxW; w++){
            if(height(w, wgap, hgap)<=height){
                //System.out.printf("w = %4d ; h(w) = %4d | height = %4d\n", w, height(w), height);
                return Math.max(w, minW);
            }
        }
        return maxW;
    }
    
    

    /*public int width(final int height, final int wgap, final int hgap){
        int w = wgap;
        int h = hgap;
        int max_w = 0;
        for(Component c : getComponents()){
            if(h+c.getPreferredSize().height+hgap <= height){
                max_w = Math.max(max_w, c.getPreferredSize().width);
                h += c.getPreferredSize().height+hgap;
            }else{
                w += max_w+wgap;
                h = hgap+c.getPreferredSize().height+hgap;
                max_w = 0;
            }
        }
        if(max_w>0){
            w += max_w+wgap;
        }
        return Math.max(w, minW());
    }*/
    public void minimazeWidth(final int height){
        setPreferredSize(new Dimension(width(height), height));
    }
    
    public void minimazeArea(){
        minimazeArea(10000, 10000, WGAP, HGAP);
    }
    public void minimazeArea(final int width, final int height){
        minimazeArea(width, height, WGAP, HGAP);
    }
    /*public void minimazeArea(final int wgap, final int hgap){
        int bArea = 100000000;
        int bw = minW(wgap);
        int bh = minH(hgap);

        int maxW = maxW(wgap);
        for(int w = bw; w < maxW; w++){
            int h = height(w, wgap, hgap);
            if(w*h<bArea){
                bw = w;
                bh = h;
                bArea = w*h;
            }
        }
        setPreferredSize(new Dimension(bw, bh));
    }*/
    public void minimazeArea(final int width, final int height, final int wgap, final int hgap){
        int bArea = width*height;
        int bw = minW(wgap);
        int bh = minH(hgap);

        int maxW = Math.min(maxW(wgap), width);
        for(int w = bw; w < maxW; w++){
            int h = height(w, wgap, hgap);
            if(h<=height && w*h<bArea){
                bw = w;
                bh = h;
                bArea = w*h;
            }
        }
        setPreferredSize(new Dimension(bw, bh));
    }
}
