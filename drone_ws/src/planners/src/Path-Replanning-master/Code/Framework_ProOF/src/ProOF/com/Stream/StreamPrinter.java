/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.com.Stream;


/**
 *
 * @author marcio
 */
public interface StreamPrinter extends Stream{
    public void printInt(String name, int value);
    public void printInt(String name, String format, int value);
    
    public void printLong(String name, long value);
    public void printLong(String name, String format, long value);
    
    public void printDbl(String name, double value);
    public void printDbl(String name, String format, double value);
    
    public void printString(String name, String value);
    public void printString(String name, String format, String value);
}
