/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.com.model;

import ProOF.com.language.Factory;
import java.awt.Graphics2D;
import java.util.LinkedList;

/**
 *
 * @author marcio
 */
public class dFactory {
    protected final char signal;
    protected final Class type;
    protected final Factory factory;
    protected final LinkedList<dVertex> vertexes = new LinkedList<dVertex>();
    
    public dFactory(char signal, Class type, Factory factory) {
        this.signal = signal;
        this.type = type;
        this.factory = factory;
    }
    public void paint(Graphics2D g2, int x, int y) {
        g2.drawString(String.format("[%c] %s|%s", signal, factory, type==null?"null":type.getSimpleName()), x, y);
    }
    
    public void addVertex(dVertex vertex) {
        this.vertexes.addLast(vertex);
    }
    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }
}
