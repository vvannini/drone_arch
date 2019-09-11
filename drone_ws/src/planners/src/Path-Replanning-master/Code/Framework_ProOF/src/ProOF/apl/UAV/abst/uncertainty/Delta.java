/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.UAV.abst.uncertainty;

import ProOF.apl.UAV.abst.Provider;
import ProOF.com.Linker.LinkerParameters;

/**
 *
 * @author marcio
 */
public final class Delta extends Provider{
    
    public static final Delta obj = new Delta();
    
    private double delta;

    private Delta() {}
    
    @Override
    public String name() {
        return "Linear Delta";
    }
    
    @Override
    public void parameters(LinkerParameters link) throws Exception {
        super.parameters(link);
        delta = link.Dbl("Delta", 0.001, 1e-6, 0.5, "probability to fail the mission");
    }
    
    public double Delta() throws Exception {
        return delta;
    }
}
