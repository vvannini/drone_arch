/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.UAV.PPCCS;

import ProOF.apl.UAV.gen.linear.uncertainty.pLinearStateUncertainty;
import ProOF.com.Linker.LinkerParameters;

/**
 * Classe que modela como a incerteza associada ao VANT se comporta em função do 
 * tempo.
 * @author marcio e jesimar
 */
public class PPCCSStateUncertainty extends pLinearStateUncertainty{
    
    private final PPCCSApproach approach;
    private double std_position;
    private double std_height;
//    private double std_control;    
    private double inc_factor;

    public PPCCSStateUncertainty(PPCCSApproach approach) {
        this.approach = approach;
    }
    
    @Override
    public String name() {
        return "PPCCS State Unc.";
    }

    @Override
    public void parameters(LinkerParameters link) throws Exception {
        super.parameters(link);
        std_position = link.Dbl("std-position-xy", 2.0, 0.0, 1e6, "uncertainty associated with position (x, y)");
        std_height = link.Dbl("std-height-z", 1.0, 0.0, 1e6, "uncertainty associated with position (z)");
//        std_control = link.Dbl("std-control", 0.5, 0.0, 1e6, "uncertainty associated with saturation controls");
        inc_factor = link.Dbl("inc-factor", 0.1, 0.0, 1e6, "increase factor of position (x, y) at time t");
    }
    
    @Override
    public double Sigma(int t, int row, int col) throws Exception {
        if(row==col){
            if(row<approach.N()){ //position
                if (row < 2){    //position (x, y)
                    return Math.pow(std_position*(1+(inc_factor*t)/approach.Waypoints()), 2);
                }else {         //position (z)
                    return Math.pow(std_height, 2);
                }
            }else{              //velocidade não existe incerteza
                return 0.0;
            }
        }
        return 0.0;
    }

    @Override
    public int N() throws Exception {
        return approach.N();
    }    
}
