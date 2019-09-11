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
public interface LinkerParameters {
    public int Int(String name, int init) throws Exception;
    public int Int(String name, int init, int min, int max) throws Exception;
    public int Int(String name, int init, int min, int max, String description) throws Exception;
    
    public boolean Bool(String name, boolean init) throws Exception;
    public boolean Bool(String name, boolean init, String description) throws Exception;
    
    public int Itens(String name, int init, String ...itens) throws Exception;
    public int Itens(String name, int init, String itens[], String description) throws Exception;
    
    public long Long(String name, long init) throws Exception;
    public long Long(String name, long init, long min, long max) throws Exception;
    public long Long(String name, long init, long min, long max, String description) throws Exception;
    
    public double Dbl(String name, double init) throws Exception;
    public double Dbl(String name, double init, double min, double max) throws Exception;
    public double Dbl(String name, double init, double min, double max, String description) throws Exception;
    
    public String String(String name, String init) throws Exception;
    public String String(String name, String init, String description) throws Exception;
    
    public String Regex(String name, String init, String regex) throws Exception;
    public String Regex(String name, String init, String regex, String description) throws Exception;

    public InputStream InputStream(String name) throws Exception;
    public InputStream InputStream(String name, String description) throws Exception;

    
    public File     File(String name, String description, String ...extensions) throws Exception;
    
    public File[]   Files(String name, String description, String ...extensions) throws Exception;
    
    public File     Directory(String name) throws Exception;
    public File     Directory(String name, String description) throws Exception;
    public File     Directory(String name, String description, String ...constraints) throws Exception;
    
    
    /**
     * @param name          the name of component in graphical interface
     * @param description   the description of de component in graphical interface
     * @param regex         the regular expressions for accept the file
     * @param constraints   constraints to the content of file or directory
     * @return              a file or directory that matches the regular expression and satisfies all the constraints to the content
     * @throws Exception 
     * 
     * <p> A typical invocation sequence is thus
     * 
     * <blockquote><pre>
     * accepts any file or directory
     *      regex    = .*
     *      contains = null
     * 
     * accepts only directory
     *      regex    = .*[/]
     *      contains = null
     * 
     * accepts only file
     *      regex    = .*[^/]
     *      contains = null
     * 
     * accepts only file '.txt'
     *      regex    = .*\\.txt
     *      contains = null
     *
     * accepts only file '.txt' or '.bin'
     *      regex    = (.*\\.txt)|(.*\\.bin)
     *      contains = null
     * 
     * accepts any directory that contains a directory bin/ and src/
     *      regex    = .*[/]
     *      contains = .*[/]bin[/]
     *                 .*[/]src[/]
     * 
     * accepts any directory that contains a directory inst/ or dat/
     *      regex    = .*[/]
     *      contains = .*[/]inst[/]|.*[/]dat[/]
     *
     * accepts any directory that contains a directory bin/ and a file ".txt"
     *      regex    = .*[/]
     *      contains = .*[/]bin[/]
     *                 .*[/].*\\.txt
     * 
     * accepts any file that contains the patterns:
     *      1)  (.|\n)*             #anything until the end
     * 
     *      2)  NAME\n              #title NAME
     *          (\\w*)\n            #a name
     *          ID\n                #title ID
     *          (\\d*)\n            #a positive integer number
     *          (.|\\n)*            #anything until the end
     * 
     *      regex    = .*[/]
     *      contains = (.|\n)*
     *                 NAME\n(\\w*)\nID\n(\\d*)\n(.|\\n)*
     * </pre></blockquote>
     * 
     */
    public File     FileRgx(String name, String description, String regex, String ...constraints) throws Exception;
    public File     FileRgx(String name, RgxRule rule) throws Exception;
    public File     FileRgx(String name, String description, RgxRule rule) throws Exception;
    
    
    public File[]   FilesRgx(String name, RgxRule ...rules) throws Exception;
    public File[]   FilesRgx(String name, String description, RgxRule ...rules) throws Exception;
}
