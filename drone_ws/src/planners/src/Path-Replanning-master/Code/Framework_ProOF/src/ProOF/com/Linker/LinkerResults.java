/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.com.Linker;

import ProOF.com.Stream.Stream;
import java.io.File;
import java.util.Collection;

/**
 *
 * @author marcio
 */
public interface LinkerResults extends Stream{
    public void writeInt(String name, int value) throws Exception;
    public void writeLong(String name, long value) throws Exception;
    public void writeDbl(String name, double value) throws Exception;
    public void writeString(String name, String value) throws Exception;
    public void writeFile(String name, File file) throws Exception;
    public void writeArray(String file, String tag, Collection c);
    public void writeArray(String file, String tag, Object[] array);
    public void writeArray(String file, String tag, int[] array);
    public void writeArray(String file, String tag, long[] array);
    public void writeArray(String file, String tag, double[] array);
}
