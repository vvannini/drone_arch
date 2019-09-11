/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.UAV.map;

import ProOF.apl.UAV.abst.Provider;
import ProOF.com.Linker.LinkerParameters;

/**
 *
 * @author marcio
 */
public final class DimensionsTest extends Provider{
    
    public static final DimensionsTest obj = new DimensionsTest();
    
    private int N;

    private DimensionsTest() {}
    
    @Override
    public String name() {
        return "Linear Delta";
    }
    
    @Override
    public void parameters(LinkerParameters link) throws Exception {
        super.parameters(link);
        N = 2+link.Itens("Dimension", 1, "2D", "3D");
    }
    
    public int N() throws Exception {
        return N;
    }
}
