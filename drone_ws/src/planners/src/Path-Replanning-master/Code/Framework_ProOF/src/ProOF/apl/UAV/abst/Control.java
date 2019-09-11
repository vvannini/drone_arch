/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.UAV.abst;

/**
 *
 * @author marcio
 */
public abstract class Control {
    private static final int UNIT = 100;
    public final int unit(double v){
        return (int)(v*UNIT);
    }
}
