/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.jsa.problem.PPCCS.instance;

import ProOF.apl.jsa.problem.PPCCS.structure.TypeOfCriticalSituation;
import ProOF.com.Linker.LinkerParameters;
import ProOF.opt.abst.problem.Instance;
import java.awt.Polygon;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 *
 * @author marcio
 */
public final class InstanceProblem extends Instance{
    
    public static InstanceProblem instance = new InstanceProblem();
    
    public final FileInstance file = new FileInstance();     
    
    public final PlotScenery plot = new PlotScenery(); 
    
    private InstanceProblem(){
        initialize();
    }
    
    public void initialize(){
        file.setInstance(this);
        plot.setInstance(this);
    }
    
    @Override
    public String name() {
        return "PPCCSInstance";
    }
    
    @Override    
    public String description() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public void parameters(LinkerParameters win) throws Exception {
        FILE_MAP = win.File("Map for jUAV",null,"sgl");
        FILE_CONFIG = win.File("Dinamic for jUAV",null,"sgl");
    }        
    
    @Override
    public void load() throws FileNotFoundException, IOException {
        file.load();
    }
    
    /*--------------Variaveis de configuração do UAV-------------*/            
    
    /*Estados iniciais do UAV [posX, posY, vel, angle]*/
    public final double X0[] = new double[]{0, 0, 0, 0};  
    
    /*Significa a variancia da posição do vant (incerteza da posição)*/
    public double P0 = 10;
    
    /*Velocidade maxima do UAV*/
    public double V_MAX = 3;    
    
    /*Velocidade minima do UAV*/
    public double V_MIN = 1;
    
    /*Velocidade angular maxima do UAV*/
    public double LEME_MAX = +0.05;
    
    /*Velocidade angular minima do UAV*/
    public double LEME_MIN = -0.05;
    
    /*Aceleração máxima do UAV*/
    public double A_MAX = 1;
    
    /*Aceleração minima do UAV*/
    public double A_MIN = 0.0;
    
    /*Horizonte de planejamento*/
    public int T = 10;
    
    /*Constante de resistencia do ar do UAV*/
    public double G = 0.05;   
    
    /*Massa do VANT - Tiriba*/
//    public double MASS = 3.7;
    
    /*Tipo de falha ocorrida no UAV*/
    public TypeOfCriticalSituation TYPE_OF_FAILURE;
    
    public double Rjti[][][][];
    public double aji[][][][];
    public double bji[][][];
    
    /*-----------------Configurações Gerais-----------------*/
    
    /*Risco máximo permitido na solução*/
    public double DELTA = 0.001;
    
    /*Discretização do tempo*/
    public double DT = 1;
    
    /*integrado com algum simulador*/
    public boolean ONLINE;
    
    /*-----------------Configurações do MAPA-----------------*/
    
    /*Conjunto de pontos2D que formam os poligonos utilizados no mapa do ambiente*/
    public Point2D poly[][][];
    
    /*Conjunto de poligonos utilizados no mapa do ambiente*/
    public Polygon polygon[][];
    
    /*Vetor de nomes das regiões*/
    public String[] NAME_REGIONS;        
    
    /*Quantidade de tipos de zonas*/
    public int L;
    
    /*Coordinates Cartesian(false). Coordinates Geographical(true)*/
    public boolean MODE_COORDENATE;
    
    /*Vetor de custos de cada uma dos tipos de regiões
    CUST_REGIONS[0] = CUST_REGION_N;
    CUST_REGIONS[1] = CUST_REGION_P;
    CUST_REGIONS[2] = CUST_REGION_B;*/
    public double[] CUST_REGIONS;
    
    /*Representa a quantidade de regioes de cada um dos tipos*/
    public int[] SIZE_REGIONS;
    
    /*Numero da execução. Utilizado quando se faz varias execuções(?)*/
    public int NUMBER_OF_EXECUTION = 1;
    
    /*-----------------Arquivos de Mapa e Configuração-----------------*/
    
    /*Arquivo contendo o mapa utilizado*/
    public File FILE_MAP;
    
    /*Arquivo contendo as configurações utilizadas*/
    public File FILE_CONFIG;
    
    /*-----------------Configurações da Janela-----------------*/
    
    /*Dimensão X da janela de plot*/
    public int WIDTH = 800;
    
    /*Dimensão Y da janela de plot*/
    public int HEIGTH = 700;
    
    public double bestFitness = Double.MAX_VALUE;
    
    public double actualFitness; 
    
    public int bestK;
            
    public int actualK;
    
    public int bestIdSolution;
    
    public int actualIdSolution;
    
    public String actualLandingLocal;
    
    public String bestLandingLocal;
}
