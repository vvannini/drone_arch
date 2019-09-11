/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.pog.gen.callback;

import java.io.PrintStream;

/**
 *
 * @author marcio
 */
public interface cbCSV<T> {
    public void title(PrintStream out, T data) throws Exception;
    public void values(PrintStream out, T data) throws Exception;
}
