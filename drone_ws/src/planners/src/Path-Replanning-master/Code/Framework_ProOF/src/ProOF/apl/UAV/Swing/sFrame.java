/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.UAV.Swing;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.HeadlessException;
import java.awt.LayoutManager;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 *
 * @author marcio
 */
public class sFrame extends JFrame implements iDimensions{
    
    
    public sFrame() throws HeadlessException {
        this(new FlowLayout(FlowLayout.CENTER));
        main_config(null);
    }
    public sFrame(LayoutManager layout) throws HeadlessException {
        setLayout(layout);
        main_config(layout);
    }
    public sFrame(LayoutManager layout, String title) throws HeadlessException {
        super(title);
        main_config(layout);
    }
    private void main_config(LayoutManager layout){
        if(layout!=null) {
            setLayout(layout);
        }
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(final ComponentEvent e) {
                resized();
            }
        });
    }
    private void resized(){
        Config(getWidth(), getHeight());
        SwingUtilities.updateComponentTreeUI(this);
    }
    
    /*private boolean fristSise = true;
    @Override
    public void setSize(Dimension size) {
        if(fristSise){
            fristSise = false;
            Original(size.width, size.height);
            super.setSize(size);
        }else{
            super.setSize(size);
        }
    }*/
    @Override
    public void Config(int w, int h) {
        setSize(w, h);
        for(Component c : getContentPane().getComponents()){
            if(c instanceof iDimensions){
                ((iDimensions)c).Config(w-16, h-38);
            }
        }
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
    
}
