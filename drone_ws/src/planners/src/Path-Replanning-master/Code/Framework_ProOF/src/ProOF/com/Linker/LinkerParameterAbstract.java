/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.com.Linker;

import ProOF.com.RgxRule;
import java.io.File;
import java.io.InputStream;

/**
 *
 * @author marcio
 */
public abstract class LinkerParameterAbstract implements LinkerParameters{

    @Override
    public final int Int(String name, int init) throws Exception {
        return Int(name, init, Integer.MIN_VALUE, Integer.MAX_VALUE, null);
    }
    @Override
    public final int Int(String name, int init, int min, int max) throws Exception {
        return Int(name, init, min, max, null);
    }

    @Override
    public final boolean Bool(String name, boolean init) throws Exception {
        return Int(name, init?1:0, 0, 1, null) != 0;
    }
    @Override
    public boolean Bool(java.lang.String name, boolean init, String description) throws Exception {
        return Int(name, init?1:0, 0, 1, description) != 0;
    }
    
    @Override
    public int Itens(String name, int init, String ...itens) throws Exception{
        return Itens(name, init, itens, null);
    }

    @Override
    public final long Long(String name, long init) throws Exception {
        return Long(name, init, Long.MIN_VALUE, Long.MAX_VALUE, null);
    }
    @Override
    public final long Long(String name, long init, long min, long max) throws Exception {
        return Long(name, init, min, max, null);
    }

    
    @Override
    public final double Dbl(String name, double init) throws Exception {
        return Dbl(name, init, Double.MIN_VALUE, Double.MAX_VALUE, null);
    }
    @Override
    public final double Dbl(String name, double init, double min, double max) throws Exception {
        return Dbl(name, init, min, max, null);
    }

    @Override
    public final String String(String name, String init) throws Exception {
        return String(name, init, null);
    }

    @Override
    public final String Regex(String name, String init, String regex) throws Exception {
        return Regex(name, init, regex, null);
    }
    
    @Override
    public final InputStream InputStream(String name) throws Exception {
        return InputStream(name, null);
    }
    
    @Override
    public final File File(String name, String description, String ...extensions) throws Exception{
        //(.*\\.txt)|(.*\\.bin)
        return FileRgx(name, description, RgxRule.One(extensions));
    }
  
    @Override
    public final File[] Files(String name, String description, String... extensions) throws Exception {
        return FilesRgx(name, description, RgxRule.All(extensions));
    }

    @Override
    public final File Directory(String name) throws Exception{
    //    return Directory(name, null, null);
        return FileRgx(name, null, new RgxRule(RgxRule.DIRECTORY));
    }
    @Override
    public final File Directory(String name, String description) throws Exception{
        //return Directory(name, description, null);
        return FileRgx(name, description, new RgxRule(RgxRule.DIRECTORY));
    }
    @Override
    public final File Directory(String name, String description, String... constraints) throws Exception {
        return FileRgx(name, description, RgxRule.Directory(constraints));
    }
    
    @Override
    public final File FileRgx(String name, String description, String regex, String... constraints) throws Exception {
        return FileRgx(name, description, new RgxRule(regex, constraints));
    }
    @Override
    public final File FileRgx(String name, RgxRule rule) throws Exception {
        return FileRgx(name, null, rule);
    }
    
    @Override
    public final File[] FilesRgx(String name, RgxRule... rules) throws Exception {
        return FilesRgx(name, null, rules);
    }
}
