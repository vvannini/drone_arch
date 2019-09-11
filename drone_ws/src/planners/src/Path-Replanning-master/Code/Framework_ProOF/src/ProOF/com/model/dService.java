/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.com.model;

import ProOF.com.language.Approach;
import java.awt.Graphics2D;
import java.util.LinkedList;

/**
 *
 * @author marcio
 */
public class dService {
    protected final boolean enabled;
    protected final Approach serv;
    protected final LinkedList<dVertex> vertexes = new LinkedList<dVertex>();
    
    public dService(Approach serv, boolean enabled) {
        this.serv = serv;
        this.enabled = enabled;
    }

    public void paint(Graphics2D g2, int x, int y) {
        g2.drawString(String.format("%s", serv), x, y);
    }

    public void addVertex(dVertex vertex) {
        this.vertexes.addLast(vertex);
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }
}
