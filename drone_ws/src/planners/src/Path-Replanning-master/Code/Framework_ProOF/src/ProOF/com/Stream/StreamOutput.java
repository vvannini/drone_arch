/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.com.Stream;

import java.util.Locale;


/**
 *
 * @author marcio
 */
public interface StreamOutput extends Stream{
    public void printf(String format, Object... arg);
    public void printf(Locale l, String format, Object... arg);
    public void println();
    public void println(String x);
}
