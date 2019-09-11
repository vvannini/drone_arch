/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.com.model;

import ProOF.com.Linker.LinkerParameterAbstract;
import ProOF.com.RgxRule;
import java.io.File;
import java.io.InputStream;

/**
 *
 * @author marcio
 */
public class SavePof extends LinkerParameterAbstract{
    private String output;
    private int size;
    public SavePof() {
        output = "";
        size = 0;
    }
    
    private void printf(String format, Object ...args){
        output += String.format(format, args);
    }
    public int size() {
        return size;
    }
    

    @Override
    public String toString() {
        return output;
    }
    
    @Override
    public int Int(String name, int init, int min, int max, String description) throws Exception {
        printf("\t\tInt [name = '%s', init = %d, min = %d, max = %d, description = '%s']\n",name, init, min, max, description);
        size++;
        return 0;
    }
    @Override
    public int Itens(String name, int init, String itens[], String description) throws Exception {
        if(itens.length<2){
            throw new Exception("itens.length<2");
        }
        String s = itens[0];
        for(int i=1; i<itens.length; i++){
            s += ";"+itens[i];
        }
        printf("\t\tItens [name = '%s', init = %d, itens = [%s], description = '%s']\n",name, init, s, description);
        size++;
        return 0;
    }
    @Override
    public long Long(String name, long init, long min, long max, String description) throws Exception {
        printf("\t\tLong [name = '%s', init = %d, min = %d, max = %d, description = '%s']\n",name, init, min, max, description);
        size++;
        return 0;
    }
    @Override
    public double Dbl(String name, double init, double min, double max, String description) throws Exception {
        printf("\t\tDbl [name = '%s', init = %g, min = %g, max = %g, description = '%s']\n",name, init, min, max, description);
        size++;
        return 0;
    }
    @Override
    public String String(String name, String init, String description) throws Exception {
        printf("\t\tString [name = '%s', init = %s, description = '%s']\n",name, init, description);
        size++;
        return null;
    }
    @Override
    public String Regex(String name, String init, String regex, String description) throws Exception {
        printf("\t\tRegex[name = '%s', init = '%s', regex = '%s', description = '%s']\n", name, init, regex, description);
        size++;
        return null;
    }

    @Override
    public InputStream InputStream(String name, String description) {
        printf("\t\tInputStream[name = '%s', description = '%s']\n", name, description);
        size++;
        return null;
    }
    
    @Override
    public File FileRgx(String name, String description, RgxRule rule) throws Exception {
        if(rule==null){
            throw new Exception("rule==null");
        }
        String s = null;
        if(rule.constraints!=null && rule.constraints.length>0){
            s = "{'"+rule.constraints[0];
            for(int i=1; i<rule.constraints.length; i++){
                s += "','" + rule.constraints[i];
            }
            s+="'}";
        }
        printf("\t\tFileRgx[name = '%s', description = '%s', regex = '%s', constrants = %s]\n", name, description, rule.regex, s);
        size++;
        return null;
    }

    @Override
    public File[] FilesRgx(String name, String description, RgxRule... rules) throws Exception {
        if(rules==null || rules.length<1){
            throw new Exception("rules==null || rules.length<1");
        }
        String s = "{";
        for(int j=0; j<rules.length; j++){
            String t = null;
            if(rules[j].constraints!=null && rules[j].constraints.length>0){
                t = "{'"+rules[j].constraints[0];
                for(int i=1; i<rules[j].constraints.length; i++){
                    t += "','" + rules[j].constraints[i];
                }
                t+="'}";
            }
            s+=(j==0?"":",")+String.format("regex = '%s', constrants = %s", rules[j].regex, t);
        }
        s+="}";
        printf("\t\tFilesRgx[name = '%s', description = '%s', rules = %s]\n", name, description, s);
        
        size++;
        return null;
    }    

}

