/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.com.model;

import ProOF.com.language.Factory;
import ProOF.com.language.Approach;
import ProOF.com.language.ApproachEnd;
import ProOF.com.language.ApproachFactory;
import ProOF.com.language.ApproachParameter;
import ProOF.com.language.ApproachSingle;
import ProOF.com.language.Run;
import java.awt.Color;
import java.awt.Graphics2D;
import java.io.PrintStream;
import java.util.LinkedList;

/**
 *
 * @author marcio
 */
public class dVertex {
    public static int paint_lines = 0;
    private static final boolean TYPE = true; 
    
    private final Set set;
    private final int dh = 20;
    
    private int x, y;
    private int width;
    private int heigth;
    
    private double xo, yo;
    private Color background;
    
    private boolean death = false;
    private int enableds = 0;
    private LinkedList<dService> services = new LinkedList<dService>();
    private LinkedList<dFactory> factorys = new LinkedList<dFactory>();
    private LinkedList<dNeed> needs = new LinkedList<dNeed>();
    
    public dVertex(Set set) {
        this.set = set;
        calc();
    }

    @Override
    public String toString() {
        return set.serv.toString();
    }
    
    @Override
    public boolean equals(Object obj) {
        return ((dVertex)obj).set.serv.equals(this.set.serv);
    }

    public void setXY(int x, int y) {
        this.x = 10*(x / 10);
        this.y = 10*(y / 10);
    }

    public int getWidth() {
        return width;
    }
    public int getHeigth() {
        return heigth;
    }
    
    private void calc(){
        width = TYPE ? 220 : 160;
        heigth = dh*((TYPE?2:1)+services.size() + factorys.size() + needs.size());
    }
    
    public void add(Approach serv, boolean flag){
        services.addLast(new dService(serv, flag));
        enableds += flag ? 1 : 0;
        calc();
    }
    public void add(Class type, Factory fact){
        factorys.addLast(new dFactory('.', type, fact));
        calc();
    }
    public void get(char signal, Class type, Factory fact){
        factorys.addLast(new dFactory(signal, type, fact));
        calc();
    }
    public void need(char signal, Class type){
        needs.addLast(new dNeed(signal, type));
        calc();
    }
    public double estimate(){
        return heigth;//Math.sqrt(width*width + heigth*heigth);
    }
    public void mark_death() {
        if(!death){
            for(dNeed n : needs){
                if(n.vertexes.size()==0){
                    death = true;
                    return;
                }
            }
            
        }
    }
    public void salvePof(PrintStream output) throws Exception{
        String type = set.serv instanceof Run ? "run" :
                      set.serv instanceof ApproachFactory ? "factory":
                      set.serv instanceof ApproachSingle ? "single":
                      set.serv instanceof ApproachParameter ? "param":
                      set.serv instanceof ApproachEnd ? "end":
                      set.serv instanceof Approach ? "serv":
                      "null";
        
        output.printf("node [ id = %d, death = %s, type = %s, ref = '%s', name = '%s' ]{\n", set.serv.getID(), death, type, set.serv.getClass().getSuperclass().getSimpleName(), set.serv.name());
        if(set.serv.get_dads().size()>0){
            output.println("\t"+"dads{");
            output.printf("\t\t");
            String t="";
            for(Integer d : set.serv.get_dads()){
                output.printf(t+"%d",d);
                t = ",";
            }
            output.println("\n\t"+"}");
        }
        if(services.size()>0){
            output.println("\t"+"services{");
            output.printf("\t\t");
            String t="";
            for(dService s : services){
                if(s.vertexes.size()!=1){
                    throw new Exception("s.vertexes.size()!=1\n\ts.vertexes = "+s.vertexes+" serv = "+s.serv.name());
                }
                if(s.enabled){
                    output.printf(t+"%d",s.vertexes.getFirst().set.serv.getID());
                }else{
                    output.printf(t+"-");
                }
                t = ",";
            }
            output.println("\n\t"+"}");
        }
        if(factorys.size()>0){
            output.println("\t"+"factorys{");
            for(dFactory f : factorys){
                if(f.vertexes.size()!=1){
                    throw new Exception("s.vertexes.size()!=1\n\ts.vertexes = "+f.vertexes+ " fact = "+f.factory.name());
                }else{
                    output.printf("\t\t"+"[%c|%s|%s]{", f.signal, f.type==null?"null":f.type.getSimpleName(), f.factory.name());
                    String t="";
                    for(dVertex v : f.vertexes){
                        output.printf(t+"%d",v.set.serv.getID());
                        t = ",";
                    }
                    output.println("}");
                }
            }
            output.println("\t"+"}");
        }
        if(needs.size()>0){
            output.println("\t"+"needs{");
            for(dNeed n : needs){
                if(n.vertexes.size()==0){
                    output.printf("\t\t"+"[%c|%s]{death}\n", n.signal, n.type.getSimpleName());
                    //throw new Exception("No need found '"+n.type.getSimpleName()+"'");
                }else{
                    output.printf("\t\t"+"[%c|%s]{", n.signal, n.type.getSimpleName()); //n.type.getCanonicalName();
                    String t="";
                    for(dVertex v : n.vertexes){
                        output.printf(t+"%d",v.set.serv.getID());
                        t = ",";
                    }
                    output.println("}");
                }
            }
            output.println("\t"+"}");
        }
        
        SavePof win = new SavePof();
        set.serv.parameters(win);
        if(win.size()>0){
            output.println("\t"+"parameters{");
            output.print(win);
            output.println("\t"+"}");
        }
        output.println("}");
    }
    public void salveSgl(PrintStream output) throws Exception{
        int type =  set.serv instanceof Run ? 1 :
                    set.serv instanceof ApproachFactory ? 2:
                    set.serv instanceof ApproachSingle ? 3:
                    set.serv instanceof ApproachParameter ? 4:
                    set.serv instanceof ApproachEnd ? 5:
                    set.serv instanceof Approach ? 0:
                    6;
        output.printf("<node id>\n");
        output.printf("%s\n", set.serv.getID());
        output.printf("<node death>\n");
        output.printf("%s\n", death);
        output.printf("<node type>\n");
        output.printf("%d\n", type);
        output.printf("<node ref>\n");
        output.printf("%s\n", set.serv.getClass().getSuperclass().getSimpleName());
        output.printf("<node name>\n");
        output.printf("%s\n", set.serv.name());
        output.printf("<node class name>\n");
        output.printf("%s\n", set.serv.class_name());
        output.printf("<services number>\n");
        output.printf("%d\n", services.size());
        if(services.size()>0){
            output.printf("<services ..(id)>\n");
            for(dService s : services){
                if(s.vertexes.size()!=1){
                    throw new Exception("s.vertexes.size()!=1");
                }
                if(s.enabled){
                    output.printf("%d\n", s.vertexes.getFirst().set.serv.getID());
                }else{
                    output.printf("-1\n");
                }
            }
        }
        output.printf("<factorys number>\n");
        output.printf("%d\n", factorys.size());
        if(factorys.size()>0){
            output.printf("<factorys ..(signal:type:name:id)>\n");
            for(dFactory f : factorys){
                if(f.vertexes.size()!=1){
                    throw new Exception("f.vertexes.size()!=1");
                }else{
                    String t = "";
                    output.printf("%c:%s:%s:", f.signal, f.type==null?"null":f.type.getSimpleName(), f.factory.name());
                    for(dVertex v : f.vertexes){
                        output.printf(t+"%d",v.set.serv.getID());
                        t = ",";
                    }
                    output.println();
                }
            }
        }
        output.printf("<needs number>\n");
        output.printf("%d\n", needs.size());
        if(needs.size()>0){
            output.printf("<needs ..(signal:type:{..id})>\n");
            for(dNeed n : needs){
                if(n.vertexes.size()==0){
                    output.printf("%c:%s:death\n", n.signal, n.type.getSimpleName());
                }else{
                    String t = "";
                    output.printf("%c:%s:", n.signal, n.type.getSimpleName());
                    for(dVertex v : n.vertexes){
                        output.printf(t+"%d",v.set.serv.getID());
                        t = ",";
                    }
                    output.println();
                }
            }
        }
        
        SaveSgl win = new SaveSgl();
        set.serv.parameters(win);
        output.printf("<parameters number>\n");
        output.printf("%d\n", win.size());
        if(win.size()>0){
            output.print(win);
        }
    }
    
    public void paint_vertex(Graphics2D g2) {
        /*if(set.serv instanceof Executable){
            g2.setColor(Color.ORANGE);
        }else{
            g2.setColor(background);
        }*/
        g2.setColor(background);
        g2.fillRect(x-3, y-3, width+6, heigth+6); 
        g2.setColor(Color.BLACK);
        g2.drawRect(x-3, y-3, width+6, heigth+6); 
        
        int y0 = 0;
        g2.setColor(Color.GRAY);
        g2.fillRect(x, y+y0, width, dh*(TYPE?2:1));
        g2.setColor(Color.BLACK);
        g2.drawRect(x, y+y0, width, dh*(TYPE?2:1));
        
        y0 += dh*(TYPE?2:1);
        g2.setColor(Color.GREEN);
        g2.fillRect(x, y+y0, width, dh*enableds);
        g2.setColor(Color.BLACK);
        g2.drawRect(x, y+y0, width, dh*enableds);
        
        y0 += dh*enableds;
        g2.setColor(Color.YELLOW);
        g2.fillRect(x, y+y0, width, dh*factorys.size());
        g2.setColor(Color.BLACK);
        g2.drawRect(x, y+y0, width, dh*factorys.size());
        
        y0 += dh*factorys.size();
        g2.setColor(Color.PINK);
        g2.fillRect(x, y+y0, width, dh*needs.size());
        g2.setColor(Color.BLACK);
        g2.drawRect(x, y+y0, width, dh*needs.size());
        
        int i=0;
        g2.drawString(String.format("[%d]%s", set.serv.getID(),set.serv),x+5, y+dh/2+5 + dh*i);
        i++;
        if(TYPE){
            g2.drawString(String.format("%s", set.serv.getClass().getSimpleName()), x+5, y+dh/2+5 + dh*i);
            i++;
        }
        for(dService s : services){
            if(s.enabled){
                s.paint(g2, x+5, y+dh/2+5 + dh*i);
                i++;
            }
        }
        
        for(dFactory f : factorys){
            f.paint(g2, x+5, y+dh/2+5 + dh*i);
            i++;
        }
        
        for(dNeed n : needs){
            n.paint(g2, x+5, y+dh/2+5 + dh*i);
            i++;
        }
    }
    public void paint_lines(Graphics2D g2) {
        int off = 7;
        int i = (TYPE?2:1);
        
        g2.setColor(Color.BLACK);
        for(dService s : services){
            if(s.enabled){
                if((paint_lines/4)%2==0){
                    for(dVertex v : s.vertexes){
                        g2.drawLine(x+width+off-5, y+dh/2 + dh*i, v.x-off+5, v.y+dh/2);
                        g2.fillOval(x+width+off-5 - 4, y+dh/2 + dh*i - 4, 9, 9);
                        g2.fillRect(v.x-off+5-4, v.y+dh/2-4, 9, 9);
                    }
                }
                i++;
            }
        }
        g2.setColor(Color.BLUE);
        for(dFactory f : factorys){
            if((paint_lines/2)%2==0){
                for(dVertex v : f.vertexes){
                    g2.drawLine(x+width+off-5, y+dh/2 + dh*i, v.x-off+5, v.y+dh/2);
                    g2.fillOval(x+width+off-5 - 4, y+dh/2 + dh*i - 4, 9, 9);
                    g2.fillRect(v.x-off+5-4, v.y+dh/2-4, 9, 9);
                }
            }
            i++;
        }
        g2.setColor(Color.RED);
        for(dNeed n : needs){
            if((paint_lines/1)%2==0){
                for(dVertex v : n.vertexes){
                    g2.drawLine(x+width+off-5, y+dh/2 + dh*i, v.x-off+5, v.y+dh/2);
                    g2.fillOval(x+width+off-5 - 4, y+dh/2 + dh*i - 4, 9, 9);
                    g2.fillRect(v.x-off+5-4, v.y+dh/2-4, 9, 9);
                }
            }
            i++;
        }
        g2.setColor(Color.BLACK);
        
        
    }
    
    public void mark(double X, double Y, int Grid) {
        this.xo = (X - X % Grid)-x;
        this.yo = (Y - Y % Grid)-y;
    }
    public void mov(double X, double Y, int Grid) {
        this.x = (int)( (X - X % Grid) - xo );
        this.y = (int)( (Y - Y % Grid) - yo );
    }
    public void set(double X, double Y) {
        this.x = (int) X;
        this.y = (int) Y;
    }
    public void setBackground(Color background) {
        this.background = set.serv instanceof Run ? Color.blue : background;
    }
    public void setSelected(Color selected) {
        this.background = selected;
    }
    public void setMoved(Color selected) {
        this.background = selected;
    }
    public boolean contain(double X, double Y) {
        return (X>=x && Y>=y && X<= x+width && Y<=y+heigth);
    }

    public void update(LinkedList<dVertex> vertexes) {
        for(dVertex v : vertexes){
            for(dService s : this.services ){
                if(v.set.serv.equals(s.serv)){
                    s.addVertex(v);
                }
            }
            if(v.set.fact!=null){
                for(dFactory f : this.factorys ){
                    if(v.set.equal(f)){
                        f.addVertex(v);
                    }
                }
            }
            for(dNeed n : this.needs ){
                if(n.type.isInstance(v.set.serv)){
                    n.addVertex(v);
                }
            }
        }
    }
}
