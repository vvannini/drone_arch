/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.com.Stream;

/**
 *
 * @author marcio
 */
public interface Stream {
    public String type()throws Exception;
    public String name()throws Exception;
    public void open()throws Exception;
    public void flush();
    public void close()throws Exception;
}
