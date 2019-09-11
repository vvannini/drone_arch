/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.com.model;

import java.awt.Graphics2D;
import java.util.LinkedList;

/**
 *
 * @author marcio
 */
public class dNeed {
    protected final char signal;
    protected final Class type;
    
    protected final LinkedList<dVertex> vertexes = new LinkedList<dVertex>();

    public dNeed(char signal, Class type) {
        this.signal = signal;
        this.type = type;
    }
    public void paint(Graphics2D g2, int x, int y) {
        g2.drawString(String.format("[%c] %s", signal, type.getSimpleName()), x, y);
    }
    public void addVertex(dVertex vertex) {
        this.vertexes.addLast(vertex);
    }
}
