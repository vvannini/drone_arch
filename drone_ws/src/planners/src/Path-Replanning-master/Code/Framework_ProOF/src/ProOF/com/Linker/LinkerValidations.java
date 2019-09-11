/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.com.Linker;


/**
 *
 * @author marcio
 */
public interface LinkerValidations {   
    public void error(String name);
    public void warning(String name);
    public void error(int index);
    public void warning(int index);
}
