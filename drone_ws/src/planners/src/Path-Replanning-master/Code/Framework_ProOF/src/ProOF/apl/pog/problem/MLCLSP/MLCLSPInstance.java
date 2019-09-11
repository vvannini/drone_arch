/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.pog.problem.MLCLSP;

import ProOF.apl.pog.problem.TSP.*;
import ProOF.opt.abst.problem.Instance;
import ProOF.com.Linker.LinkerParameters;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 *
 * @author marcio
 */
public class MLCLSPInstance extends Instance{
    private File file;
    protected int J;
    protected int T;
    
    @Override
    public String name() {
        return "Instance-MLCLSP";
    }
    @Override
    public String description() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public void parameters(LinkerParameters win) throws Exception {
        file = win.File("Instances for MLCLSP",null,"txt");
    }
    @Override
    public void load() throws FileNotFoundException {
        Scanner sc = new Scanner(file);
        
        sc.close();
    }    
}
