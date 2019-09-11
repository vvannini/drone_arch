/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.pog.problem.MatrixDinamicsCplex;

import ProOF.com.Linker.LinkerApproaches;
import ProOF.com.Linker.LinkerParameters;
import ProOF.com.Linker.LinkerResults;
import ProOF.com.Linker.LinkerValidations;
import ProOF.com.Stream.StreamPrinter;
import ProOF.com.language.Approach;

/**
 *
 * @author marcio
 */
public abstract class aMDHeuristic extends Approach{

    @Override
    public String description() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public void services(LinkerApproaches link) throws Exception {
        
    }

    @Override
    public void parameters(LinkerParameters link) throws Exception {
    }

    @Override
    public void load() throws Exception {
    }

    @Override
    public void start() throws Exception {
    }

    @Override
    public boolean validation(LinkerValidations link) throws Exception {
        return true;
    }

    @Override
    public void results(LinkerResults link) throws Exception {
    }
    
    public abstract double solve(double A[][], double B[][], double C[][], int R) throws Exception;
    public abstract void printer(StreamPrinter com) throws Exception;
}
