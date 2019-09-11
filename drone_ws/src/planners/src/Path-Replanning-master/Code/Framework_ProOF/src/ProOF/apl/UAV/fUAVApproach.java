/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.UAV;

import ProOF.apl.UAV.abst.UAVApproach;
import ProOF.apl.UAV.Blackmore.BlackmoreApproach;
import ProOF.apl.UAV.PPCCS.PPCCSApproach;
import ProOF.com.language.Factory;

/**
 *
 * @author marcio
 */
public class fUAVApproach extends Factory<UAVApproach>{
    public static final fUAVApproach obj = new fUAVApproach();
    
    @Override
    public String name() {
        return "UAV-Approach";
    }
    
    @Override
    public UAVApproach build(int index) throws Exception{
        switch(index){
            case 0: return new BlackmoreApproach();
            case 1: return new PPCCSApproach();
        }
        return null;
    }
}
