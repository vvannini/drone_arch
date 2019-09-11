/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.UAV.Swing;

import java.awt.Dimension;

/**
 *
 * @author marcio
 */
public interface iDimensions {
    public void Config(int w, int h);
    public void Original(int w, int h);
    public int oWidth();
    public int oHeight();
    public Dimension oDimension();
}
