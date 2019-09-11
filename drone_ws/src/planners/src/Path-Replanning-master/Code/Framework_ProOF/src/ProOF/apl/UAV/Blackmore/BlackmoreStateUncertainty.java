/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.UAV.Blackmore;

import ProOF.apl.UAV.gen.linear.uncertainty.pLinearStateUncertainty;
import ProOF.com.Linker.LinkerParameters;

/**
 *
 * @author marcio
 */
public class BlackmoreStateUncertainty extends pLinearStateUncertainty{
    private final BlackmoreApproach approach;
    
    private double std_position;
    private double std_velocity;
    private double inc_factor;

    public BlackmoreStateUncertainty(BlackmoreApproach approach) {
        this.approach = approach;
    }
    
    @Override
    public String name() {
        return "Blackmore State Unc.";
    }

    @Override
    public void parameters(LinkerParameters link) throws Exception {
        super.parameters(link); //To change body of generated methods, choose Tools | Templates.
        std_position = link.Dbl("std-position", 0.05, 0.0, 1e6);
        std_velocity = link.Dbl("std-velocity", 0.00, 0.0, 1e6);
        inc_factor = link.Dbl("inc-factor", 1.0, 0.0, 1e6);
    }
    
    @Override
    public double Sigma(int t, int row, int col) throws Exception {
        if(row==col){
            if(row<approach.N()){ //position
                return Math.pow(std_position*(1+(inc_factor*t)/approach.Waypoints()) ,2);
            }else{              //velocity
                return Math.pow(std_velocity*(1+(inc_factor*t)/approach.Waypoints()) ,2);
            }
        }
        return 0.0;
    }

    @Override
    public int N() throws Exception {
        return approach.N();
    }
    
}
