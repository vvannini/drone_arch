/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.UAV.Swing;

import java.awt.Rectangle;

/**
 *
 * @author marcio
 */
public class Rectangle2DReal{
    public final Rectangle r;
    public final int UNIT;
    
    public Rectangle2DReal(int UNIT) {
        this.r = new Rectangle();
        this.UNIT = UNIT;
    }
    
    public final int unit(double v){
        return (int)(v*UNIT);
    }
}
