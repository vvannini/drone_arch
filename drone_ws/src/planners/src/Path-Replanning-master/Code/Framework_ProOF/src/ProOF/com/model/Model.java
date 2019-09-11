/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.com.model;

import ProOF.com.Linker.LinkerApproaches;
import ProOF.com.language.Factory;
import ProOF.com.language.Approach;
import ProOF.com.language.ApproachEnd;
import ProOF.com.language.ApproachFactory;
import ProOF.com.language.Run;
import java.awt.Graphics2D;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.LinkedList;

/**
 *
 * @author marcio
 */
public class Model {
    private WinServ winServ = new WinServ();
    private String tab = "";
    
    private LinkedList<Fact> all_f = new LinkedList<Fact>();
    private LinkedList<Factory> all_f2 = new LinkedList<Factory>();
    
    private LinkedList<Fact> fifo_f = new LinkedList<Fact>();
    private LinkedList<Set> fifo_s = new LinkedList<Set>();
    private LinkedList<Approach> all_s = new LinkedList<Approach>();
    protected LinkedList<dVertex> vertexes = new LinkedList<dVertex>();
    
    private LinkedList<Set> all = new LinkedList<Set>();
    
    public static boolean PRINT = false;
    
    private int ID = 0;
    
    private class Fact{
        private final Factory fact;
        private final Class type;

        public Fact(Factory fact, Class type) {
            this.fact = fact;
            this.type = type;
        }
        @Override
        public boolean equals(Object obj) {
            Fact f = ((Fact)obj);
            if(type==null){
                return fact.equals(f.fact) && f.type==null;
            }else{
                return fact.equals(f.fact) && type.equals(f.type);
            }
        }
    }
    
    public void savePof(String name) throws FileNotFoundException{
        PrintStream output = new PrintStream(name);
        try{
            for(dVertex v : vertexes){
                v.salvePof(output);
            }
        }catch(Exception ex){
            ex.printStackTrace(output);
            ex.printStackTrace();
        }
        output.close(); 
    }
    public void saveSgl(String name) throws FileNotFoundException{
        PrintStream output = new PrintStream(name);
        try{
            output.printf("<nodes number>\n");
            output.printf("%d\n", vertexes.size());
            for(dVertex v : vertexes){
                v.salveSgl(output);
            }
        }catch(Exception ex){
            ex.printStackTrace(output);
        }
        output.close(); 
    }
    
    private void add(Factory fact, Class type){
        add2( new Fact(fact, null));
    }
    private void add2(Fact F){
        all_f.add(F);
        fifo_f.add(F);
    }
    
    public void create(Factory<Run> obj) {
        //dVertex vertex = new dVertex(null);
        //vertex.add(obj);
        //vertexes.addLast(vertex);
        
        add(obj, null); 
        
        while(fifo_f.size()>0){
            Fact fact = fifo_f.removeFirst();
            split(fact);
            
            if(fifo_s.size()==0){
                continue;
            }
            if(PRINT)System.out.printf(tab+"Begin Implementations\n");
            if(PRINT)tab = "\t";
            while(fifo_s.size()>0){
                Set set = fifo_s.removeFirst();
                //Service s = set.serv;
                
                for(Set a:all){
                    if(a.serv.name().equals(set.serv.name())){
                        if(!a.serv.getClass().toString().equals(set.serv.getClass().toString())){
                            System.out.printf("war:"+tab+"Conflict of names [%s]\n", a.serv.name());
                            System.out.printf("war:"+tab+"\t[%s]\n", a.serv.getClass());
                            System.out.printf("war:"+tab+"\t[%s]\n", set.serv.getClass());
                        }
                    }
                }
                all.addLast(set);
                
                winServ.activate = set;
                winServ.vertex = new dVertex(set);
                vertexes.addLast(winServ.vertex);
                
                if(PRINT)System.out.printf(tab+"[%d]:%s -> end-node = %s\n", set.serv.getID(), set.serv, set.serv instanceof ApproachEnd);
                if(set.serv instanceof ApproachEnd){
                    winServ.activate = null;
                    winServ.vertex = null;
                    continue;
                }
                if(PRINT)System.out.printf(tab+"Begin Servicer\n");
                if(PRINT)tab = "\t\t";
                try {
                    set.serv.services(winServ);
                } catch (Exception ex) {
                    if(PRINT)System.out.printf(tab+"%s\n", ex.getMessage());
                }
                winServ.activate = null;
                winServ.vertex = null;
                if(PRINT)tab = "\t";
                if(PRINT)System.out.printf(tab+"End\n");
            }
            if(PRINT)tab = "";
            if(PRINT)System.out.printf(tab+"End\n");
        }   
        
        Calc();
    }
    
    private <T> void  split(Fact F){
        ApproachFactory node = new ApproachFactory(F.fact); 
        node.setID(ID++);
        //vertexes.addLast(new dVertex(new Set(node, factory)));
        
        
        /*if(all_f2.contains(F.fact)){    //se ja foi processado esta factory
            for(NodeService serv : ){
                node
            }
        }else{
            
            int index = 0;
            NodeService serv = F.fact.NewService(index);
            while(serv!=null){
                //??????
                node.add(serv);
                index++;
                serv = F.fact.NewService(index);
            }
        }*/
        //F.fact.split();
            
        
        Set set = new Set(node, F.fact, F.type); 
        winServ.activate = set;
        winServ.vertex = new dVertex(set);
        vertexes.addLast(winServ.vertex);
        
        if(PRINT)System.out.printf(tab+"Begin Split: %s : %s\n", F.type==null?"null":F.type.getSimpleName(), F.fact);
        if(PRINT)tab = "\t";
        try {
            set.serv.services(winServ);
        } catch (Exception ex) {
            System.out.printf(tab+"%s\n", ex);
        }
        if(PRINT)tab = "";
        if(PRINT)System.out.printf(tab+"End\n");
        winServ.activate = null;
        winServ.vertex = null;
    }
    
    private <T extends Approach> T try_errors(T service){
        if(service!=null){
            try{
                service.toString();
            }catch(Exception ex){
                System.out.printf(tab+"Error method name not defined.\n");
                System.out.printf(tab+"\tIn         : %s\n", service.getClass());
                System.out.printf(tab+"\tException  : %s\n", ex);
                service = null;
            }
        }
        return service;
    }
    private Factory try_errors(Factory factory){
        if(factory!=null){
            try{
                factory.toString();
            }catch(Exception ex){
                System.out.printf(tab+"Error method name not defined.\n");
                System.out.printf(tab+"\tIn         : %s\n", factory.getClass());
                System.out.printf(tab+"\tException  : %s\n", ex);
                factory = null;
            }
        }
        return factory;
    }

    public void Calc(){
        Calc2();
        for(dVertex v : vertexes){
            v.update(vertexes);
        }
        if(PRINT){
            System.out.println("------------------ removing nodes not used -----------------------");
            System.out.println("     not implemented yet");
        }
        //mark death nodes
        for(dVertex v : vertexes){
            v.mark_death();
        }
    }
    
    public void Calc1(){
        double sum = 0;
        for(dVertex v : vertexes){
            sum += v.estimate();
        }
        
        int R = 400;
        
        //int i = 0;
        double i = 0;
        for(dVertex v : vertexes){
            int x = (int) (R*Math.cos((i-v.estimate()/2)*Math.PI*2/sum));
            int y = (int) (R*Math.sin((i-v.estimate()/2)*Math.PI*2/sum));
            
            v.setXY(x-80, y-20);
            i += v.estimate();
            //i++;
        }
    }
    
    
    
    public void Calc2(){
        int area = 0;
        for(dVertex v : vertexes){
            area += (v.getHeigth()+20) * (v.getWidth()+40);
        }
        int dy = 0, dx = 0;
        
        int sq = (int) Math.sqrt(area);
        dx = (sq * 128)/200;
        dy = (sq * 72)/200;
        
        int x = (int)(-dx);
        int y = (int)(-dy);
        for(dVertex v : vertexes){
            v.setXY(x-80, y-20);
            //i++;
            if(y+v.estimate()+20>dy){
                x += v.getWidth()+40;
                y = (int)(-dy);
            }else{
                y += v.estimate()+20;
            }
        }
    }
    
    
    public void paint(Graphics2D g2) {
        g2.drawRect(-10, -10, 20, 20);
        for(dVertex v : vertexes){
            v.paint_vertex(g2);
        }
        for(dVertex v : vertexes){
            v.paint_lines(g2);
        }
    }
    
    
    
    private class WinServ implements LinkerApproaches { 
        protected Set activate = null;
        protected dVertex vertex = null;
        @Override
        public <T extends Approach> T add(T service) throws Exception {
            service = try_errors(service);
            //System.out.printf(tab+"-->%s\n", service);
            if(service!=null){
                if(activate.fact==null || activate.type==null || activate.type.isInstance(service)){
                    service.add_dad(activate.serv.getID());
                    if(all_s.contains(service)){
                        if(PRINT)System.out.printf(tab+"GetService (%d)%s\n", service.getID(), service);
                    }else{
                        service.setID(ID++);
                        all_s.addLast(service);
                        if(PRINT)System.out.printf(tab+"AddService (%d)%s\n", service.getID(), service);
                        fifo_s.addLast(new Set(service, null, null));
                    }
                    vertex.add(service, true);
                }else{
                    vertex.add(service, false);
                }
            }
            return service;
        }
        private void add(Factory factory, Class type, char signal) throws Exception {
            factory = try_errors(factory);
            //System.out.printf(tab+"-->%s\n", service);
            if(factory!=null){
                Fact F = new Fact(factory, type);
                if(all_f.contains(F)){
                    if(PRINT)System.out.printf(tab+"GetFactory [%c] %s : %s\n",
                            signal, type==null?"null":type.getSimpleName(),factory);
                    vertex.get(signal, type, factory);
                }else{
                    if(PRINT)System.out.printf(tab+"AddFactory[%c] %s : %s\n",
                            signal, type==null?"null":type.getSimpleName(),factory);
                    add2(F);
                    vertex.get(signal, type, factory);
                }
            }
        }
        @Override
        public void add(Factory factory) throws Exception {
            add(factory, null, '.');
        }
        @Override 
        public Approach get(Factory factory, Class type) throws Exception {
            add(factory, type, '1');
            return null;
        }
        @Override 
        public <T extends Approach> T get(Factory factory, Class type, T a) throws Exception {
            add(factory, type, '1');
            return null;
        }
        @Override 
        public Approach get(Factory factory) throws Exception {
            add(factory, null, '1');
            return null;
        }
        @Override 
        public <T extends Approach> T get(Factory factory, T a) throws Exception {
            add(factory, null, '1');
            return null;
        }
        @Override 
        public Approach[] gets(Factory factory) throws Exception {
            add(factory, null, '+');
            return null;
        }

        @Override
        public Approach need(Class type) throws Exception {
            if(PRINT)System.out.printf(tab+"Need[1] %s\n", type);
            vertex.need('1',type);
            return null;
        }
        @Override
        public <T extends Approach> T need(Class type, T a) throws Exception {
            if(PRINT)System.out.printf(tab+"Need[1] %s\n", type);
            vertex.need('1',type);
            return null;
        }
        @Override
        public Approach[] needs(Class type) throws Exception {
            if(PRINT)System.out.printf(tab+"Need[+] %s\n", type);
            vertex.need('+',type);
            return null;
        }
        @Override
        public <T extends Approach> T[] needs(Class type, T[] a) throws Exception {
            if(PRINT)System.out.printf(tab+"Need[+] %s\n", type);
            if(a==null){
                throw new Exception("call not valid -> needs("+type+".class, null) \n\t usage needs(Node.class, new Node[1])");
            }
            vertex.need('+',type);
            return null;
        }
        @Override
        public Approach wish(Class type) throws Exception {
            if(PRINT)System.out.printf(tab+"Wish[1] %s\n", type);
            vertex.need('-',type);
            return null;
        }
        @Override
        public <T extends Approach> T wish(Class type, T a) throws Exception {
            if(PRINT)System.out.printf(tab+"Wish[1] %s\n", type);
            vertex.need('-',type);
            return null;
        }
        @Override
        public Approach[] wishes(Class type) throws Exception {
            if(PRINT)System.out.printf(tab+"Wishes[*] %s\n", type);
            vertex.need('*',type);
            return null;
        }
        @Override
        public <T extends Approach> T[] wishes(Class type, T[] a) throws Exception {
            if(PRINT)System.out.printf(tab+"Wishes[*] %s\n", type);
            if(a==null){
                throw new Exception("call not valid -> wishes("+type+".class, null) \n\t usage wishes(Node.class, new Node[1])");
            }
            vertex.need('*',type);
            return null;
        }
        /*@Override
        public ComPrinter needPrinter(String name) {
            if(PRINT)System.out.printf(tab+"MakePrinter %s\n", name);
            vertex.need("printer", "[]");
            throw new UnsupportedOperationException("Not supported yet.");
        }*/
    }
}
