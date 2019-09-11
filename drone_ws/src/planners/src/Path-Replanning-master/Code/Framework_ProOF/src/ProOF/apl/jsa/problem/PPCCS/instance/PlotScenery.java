/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.jsa.problem.PPCCS.instance;

import ProOF.apl.jsa.problem.PPCCS.core.PPCCSCodification;
import ProOF.apl.jsa.problem.PPCCS.structure.PPCCSState;
import ProOF.apl.jsa.problem.PPCCS.util.ColorPlot;
import ProOF.apl.jsa.problem.PPCCS.util.ConfigSimulation;
import ProOF.apl.jsa.problem.PPCCS.util.Util;
import ProOF.apl.jsa.util.UtilStatistic;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 *
 * @author jesimar
 */
public final class PlotScenery {
    
    private final boolean DEBUG = ConfigSimulation.DEBUG;    
    private final boolean INVERT_Y = ConfigSimulation.INVERT_Y;
    private final boolean IS_PAINT_NORM = ConfigSimulation.IS_PAINT_NORM;
    private final boolean IS_PAINT_INFORMATION = ConfigSimulation.IS_PAINT_INFORMATION;
    private final boolean IS_PAINT_LEGEND = ConfigSimulation.IS_PAINT_LEGEND;
    private final boolean IS_PAINT_CENTER_REGIONS = ConfigSimulation.IS_PAINT_CENTER_REGIONS;
    private final boolean IS_PAINT_LINES_BACKGROUND = ConfigSimulation.IS_PAINT_LINES_BACKGROUND;
    private final String FORMAT_CARTESIAN = ConfigSimulation.FORMAT_CARTESIAN;
    private final String FORMAT_GEOGRAPHIC = ConfigSimulation.FORMAT_GEOGRAPHIC;  
    private final boolean PAINT_MULT_ROUTE = ConfigSimulation.PAINT_MULT_ROUTE;

    /*Janela de plotagem de dados*/
    private JFrame frame;
    
    /*Painel de plotagem da trajetoria de voo*/
    private JPanel draw;

    private final ArrayList<double[][]> listMU = new ArrayList<double[][]>();
    private final ArrayList<int[][][]> listZ = new ArrayList<int[][][]>();

    /*Dimensões do UAV que será plotado na tela*/
    private final int SIZE_UAV = 8;
    
    /*Fator de ajuste de deslocamento lateral e da escala da plotagem usada*/
    private final int OFF =120;

    /*Fator de zoon da janela de plot*/
    public double zoon;

    /*Valor mínimo do posicionamento do mapa em X*/
    public double minX;

    /*Valor mínimo do posicionamento do mapa em Y*/
    public double minY;

    /*Valor maximo do posicionamento do mapa em X*/
    public double maxX;

    /*Valor maximo do posicionamento do mapa em Y*/
    public double maxY;
    
    /*Distância entre uma linha de marcação e outra*/
    private double step;

    /*Tempo para plotagem da rota:
    Caso 1: (timeSpecific == -1) -> plota toda a rota 
    Caso 2: (timeSpecific != -1) -> plota apenas o tempo especificado*/
    private int timeSpecific = -1;
    
    private InstanceProblem inst;
    
    public PlotScenery() {
        
    }
    
    public void setInstance(InstanceProblem instance){
        inst = instance;
    }

    public JFrame getFrame() {
        return frame;
    }

    public void plot(final double[][] vMt) {
        plot("Plot 2D", vMt);
    }

    public void plot(final String title, final double[][] vMt) {
        plot(title, vMt, null, -1);
    }

    public void plot(final String title, final double[][] vMt, final int[][][] Zjti,
            final int t) {
        if (vMt != null) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    if (!PAINT_MULT_ROUTE){
                        listMU.clear();
                    }
//                    if (inst.actualLandingLocal.equals("b")){
//                            listMU.add(vMt);             
//                    }
//                    if (inst.actualFitness < 2000 && inst.actualLandingLocal.equals("b")){
//                        listMU.add(vMt);             
//                    }
                    listMU.add(vMt);                    
                }
            });
        }

        if (Zjti != null) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    timeSpecific = t;
                    listZ.clear();
                    listZ.add(Zjti);
                }
            });
        } else {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    timeSpecific = t;
                    listZ.clear();
                }
            });
        }
        if (ConfigSimulation.PLOT) {
            if (frame == null) {
                frame = new JFrame();
                draw = new JPanel() {
                    @Override
                    protected void paintComponent(Graphics g) {
                        super.paintComponent(g);
                        if (inst.MODE_COORDENATE) {
                            paintGeographic(g);
                        } else {
                            paintCartesian(g);
                        }
                    }
                };
                draw.setSize(inst.WIDTH, inst.HEIGTH);
                frame.add(draw);
                frame.setSize(inst.WIDTH + 20, inst.HEIGTH + 50);
                frame.setVisible(true);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            }
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    draw.repaint();
                }
            });
            if (ConfigSimulation.IS_SEE_ANIMATION) {
                try {
                    Thread.sleep(ConfigSimulation.TIME_SEE_ANIMATION);
                } catch (InterruptedException ex) {
                    Logger.getLogger(PlotScenery.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    frame.setTitle(title);
                }
            });
        }
    }
    
    public void plot(PPCCSCodification codif) {
        double Xt[][] = new double[inst.T + 1][4];
        PPCCSState state = new PPCCSState(inst.X0);
        Xt[0][0] = state.getPositionX();
        Xt[0][1] = state.getSpeed();
        Xt[0][2] = state.getPositionY();
        for (int t = 0; t < inst.T; t++) {
            state = state.nextState(inst.TYPE_OF_FAILURE, codif.Ut[t], inst.DT, 
                    inst.G);
            Xt[t + 1][0] = state.getPositionX();
            Xt[t + 1][1] = state.getSpeed();
            Xt[t + 1][2] = state.getPositionY();
        }
        plot(Xt);
    }

    public void clear() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                if (!PAINT_MULT_ROUTE){
                    listMU.clear();
                }
            }
        });
    }

    public void paintCartesian(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        initialization();

        paintEnviroment(g2);
        if (IS_PAINT_LINES_BACKGROUND) {
            paintLinesBackgroundAndNumbers(g2);
        }
        if (INVERT_Y) {
            g2.scale(1.0, -1.0);
            g2.translate(0, -(int) (zoon * (maxY - minY) + 2 * OFF));
        }

        paintRegionsMap(g2);
        paintUAV(g2);
        if (PAINT_MULT_ROUTE){
            paintMultiRouteUAV(g2);
        }else {
            paintRouteUAV(g2);
        }        

        if (INVERT_Y) {
            g2.translate(0.0, +(zoon * (maxY - minY) + 2 * OFF));
            g2.scale(1.0, -1.0);
        }
        if (IS_PAINT_LEGEND) {
            paintLegendRegions(g2);
        }
        if (IS_PAINT_INFORMATION) {
            paintInformation(g2);
        }
    }

    private void initialization() {
        zoon = Math.min((inst.WIDTH - OFF - 5) / (maxX - minX),
                (inst.HEIGTH - OFF - 5) / (maxY - minY));

        step = Math.max(maxX - minX, maxY - minY) * 1000 * 2 / (11 - 1);
        if (step > 1000) {
            step = (int) (step / 1000) * 1000;
        } else if (step > 100) {
            step = (int) (step / 1000) * 100;
        } else if (step > 10) {
            step = (int) (step / 10) * 10;
        } else if (step > 1) {
            step = (int) (step);
        } else {
            step = 1;
        }
    }

    private void paintEnviroment(Graphics2D g2) {
        g2.setColor(ColorPlot.BACKGROUND_EXTERNAL);
        g2.fillRect(0, 0, inst.WIDTH - 1, inst.HEIGTH - 1);
        g2.setColor(ColorPlot.LINE_CONTOUR);
        g2.drawRect(0, 0, inst.WIDTH - 1, inst.HEIGTH - 1);

        g2.setColor(ColorPlot.BACKGROUND_MAP);
        g2.fillRect(OFF, OFF, (int) (zoon * (maxX - minX)),
                (int) (zoon * (maxY - minY)));
        g2.setColor(ColorPlot.LINE_CONTOUR);
        g2.drawRect(OFF, OFF, (int) (zoon * (maxX - minX)),
                (int) (zoon * (maxY - minY)));
    }

    private void paintLinesBackgroundAndNumbers(Graphics2D g2) {
        int val = (int) (((int) ((minX * 3600) / step)) * step);
        val += minX < 0 ? -step : step;
        while (val < minX * 1000) {
            val += step;
        }
        while (val < maxX * 1000) {
            double degrees = val / 1000;
            int x = (int) (zoon * (val / 1000.0 - minX) + OFF);
            g2.setColor(ColorPlot.NUMBER);
            g2.drawString(String.format(FORMAT_CARTESIAN, degrees), x - 20, OFF - 10);
            g2.setColor(ColorPlot.LINE_BACKGROUND);
            g2.drawLine(x, OFF, x, OFF + (int) (zoon * (maxY - minY)));
            val += step;
        }
        val = (int) (((int) ((minY * 1000) / step)) * step);
        val += minY < 0 ? -step : step;
        while (val < minY * 1000) {
            val += step;
        }
        while (val < maxY * 1000) {
            double degrees = -val / 1000.0;
            int y = (int) (zoon * (val / 1000.0 - minY) + OFF);
            g2.setColor(ColorPlot.NUMBER);
            g2.drawString(String.format(FORMAT_CARTESIAN, degrees), OFF - 40, y + 4);
            g2.setColor(ColorPlot.LINE_BACKGROUND);
            g2.drawLine(OFF, y, OFF + (int) (zoon * (maxX - minX)), y);
            val += step;
        }
    }

    private void paintRegionsMap(Graphics2D g2) {
        for (int l = inst.L - 1; l >= 0; l--) {
            for (int j = 0; j < inst.bji[l].length; j++) {
                if (DEBUG) {
                    System.out.println("---------- poly[" + l + "," + j + "]------------");
                    for (int i = 0; i < inst.poly[l][j].length; i++) {
                        int k = (i + 1) % inst.poly[l][j].length;
                        System.out.printf("(%4g,%4g) -> (%4g,%4g)\n",
                                inst.poly[l][j][i].getX(), inst.poly[l][j][i].getY(),
                                inst.poly[l][j][k].getX(), inst.poly[l][j][k].getY());
                    }
                    System.out.println("---------- lines[" + j + "]------------");
                }
                paintRegion(g2, l, j);
                paintLineContourRegion(g2, l, j);
                if (IS_PAINT_CENTER_REGIONS) {
                    paintCenterRegions(g2, l, j);
                }
            }
        }
    }

    private void paintRegion(Graphics2D g2, int l, int j) {
        int npoints = inst.poly[l][j].length;
        int xpoints[] = new int[npoints];
        int ypoints[] = new int[npoints];
        for (int i = 0; i < npoints; i++) {
            double x1, y1;
            if (Math.abs(inst.aji[l][j][i][2]) > 0.01) {
                x1 = inst.poly[l][j][i].getX();
                y1 = (inst.bji[l][j][i] - inst.aji[l][j][i][0] * x1) / inst.aji[l][j][i][2];
            } else {
                y1 = inst.poly[l][j][i].getY();
                x1 = (inst.bji[l][j][i] - inst.aji[l][j][i][2] * y1) / inst.aji[l][j][i][0];
            }
            xpoints[i] = (int) (zoon * (x1 - minX) + OFF);
            ypoints[i] = (int) (zoon * (y1 - minY) + OFF);
        }
        if (l == 0) {
            g2.setColor(ColorPlot.ZONE_N);
        } else if (l == 1) {
            g2.setColor(ColorPlot.ZONE_P);
        } else if (l == 2) {
            g2.setColor(ColorPlot.ZONE_B);
        }
        //g2.setColor(new Color(255, (l - 1) * 255 / (v.L - 1), (l - 1) * 255 / (v.L - 1), 150));
        g2.fillPolygon(new Polygon(xpoints, ypoints, npoints));
    }

    private void paintLineContourRegion(Graphics2D g2, int l, int j) {
        for (int i = 0; i < inst.poly[l][j].length; i++) {
            double x1, y1;
            if (Math.abs(inst.aji[l][j][i][2]) > 0.01) {
                x1 = inst.poly[l][j][i].getX();
                y1 = (inst.bji[l][j][i] - inst.aji[l][j][i][0] * x1) / inst.aji[l][j][i][2];
            } else {
                y1 = inst.poly[l][j][i].getY();
                x1 = (inst.bji[l][j][i] - inst.aji[l][j][i][2] * y1) / inst.aji[l][j][i][0];
            }
            int k = (i + 1) % inst.poly[l][j].length;
            double x2, y2;
            if (Math.abs(inst.aji[l][j][k][2]) > 0.01) {
                x2 = inst.poly[l][j][k].getX();
                y2 = (inst.bji[l][j][k] - inst.aji[l][j][k][0] * x2) / inst.aji[l][j][k][2];
            } else {
                y2 = inst.poly[l][j][k].getY();
                x2 = (inst.bji[l][j][k] - inst.aji[l][j][k][2] * y2) / inst.aji[l][j][k][0];
            }
            g2.setColor(ColorPlot.LINE_CONTOUR);
            g2.drawLine((int) (zoon * (x1 - minX) + OFF),
                    (int) (zoon * (y1 - minY) + OFF),
                    (int) (zoon * (x2 - minX) + OFF),
                    (int) (zoon * (y2 - minY) + OFF));

            if (IS_PAINT_NORM) {
                paintNorm(g2, l, j, i, (x1 + x2) / 2, (y1 + y2) / 2);
            }
        }
    }

    private void paintNorm(Graphics2D g2, int l, int j, int i, double xm, double ym) {
        double S = step / (Math.sqrt(inst.aji[l][j][i][0] * inst.aji[l][j][i][0]
                + inst.aji[l][j][i][2] * inst.aji[l][j][i][2]) * 5000);
        g2.setColor(ColorPlot.NORM);
        g2.drawLine((int) (zoon * (xm - minX)) + OFF,
                (int) (zoon * (ym - minY)) + OFF,
                (int) (zoon * (xm - minX + S * inst.aji[l][j][i][0])) + OFF,
                (int) (zoon * (ym - minY + S * inst.aji[l][j][i][2])) + OFF);
        g2.fillOval((int) (zoon * (xm - minX)) + OFF - 5,
                (int) (zoon * (ym - minY)) + OFF - 5, 10, 10);
        g2.fillOval((int) (zoon * (xm - minX + S * inst.aji[l][j][i][0])) + OFF - 5,
                (int) (zoon * (ym - minY + S * inst.aji[l][j][i][2])) + OFF - 5, 10, 10);
    }

    private void paintCenterRegions(Graphics2D g2, int l, int j) {
        double xm = 0;
        double ym = 0;
        for (Point2D item : inst.poly[l][j]) {
            xm += item.getX();
            ym += item.getY();
        }
        xm = xm / inst.poly[l][j].length;
        ym = ym / inst.poly[l][j].length;
        int raio = 3;
        g2.setColor(ColorPlot.CENTER_REGION);
        g2.fillOval((int) (zoon * (xm - minX) + OFF) - raio,
                (int) (zoon * (ym - minY) + OFF) - raio, 2 * raio, 2 * raio);
    }

    private void paintInformation(Graphics2D g2) {
        g2.setColor(ColorPlot.INFORMATION_BACKGROUND);
        g2.fillRect(10, 10, inst.WIDTH - 20, 85);
        g2.setColor(ColorPlot.LINE_CONTOUR);
        g2.drawRect(10, 10, inst.WIDTH - 20, 85);
        g2.setColor(ColorPlot.INFORMATION);
        g2.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        g2.drawString(String.format("Informations:"), 20, 28);
        g2.drawString(String.format("Best:: Fitness = %1.1f", inst.bestFitness), 30, 50);
        g2.drawString(String.format("Time (K) = %d", inst.bestK), 260, 50);
        g2.drawString(String.format("Time (T) = %d", inst.T), 400, 50);        
        g2.drawString(String.format("LandLocal (Z) = %s", inst.bestLandingLocal), 540, 50);
        
        g2.drawString(String.format("Atual: Fitness = %1.1f", inst.actualFitness), 30, 70);
        g2.drawString(String.format("Time (K) = %d", inst.actualK), 260, 70);
        g2.drawString(String.format("Time (T) = %d", inst.T), 400, 70);
        g2.drawString(String.format("LandLocal (Z) = %s", inst.actualLandingLocal), 540, 70);                

        
        
//        g2.drawString(String.format("Best:: Fitness = %1.1f", statistic.bestFitness), 30, 50);
//        g2.drawString(String.format("Time (K) = %d", statistic.bestK), 260, 50);
//        g2.drawString(String.format("Time (T) = %d", inst.T), 400, 50);        
//        g2.drawString(String.format("LandLocal (Z) = %s", statistic.bestLandingLocal), 540, 50);
//        
//        g2.drawString(String.format("Atual: Fitness = %1.1f", statistic.actualFitness), 30, 70);
//        g2.drawString(String.format("Time (K) = %d", statistic.actualK), 260, 70);
//        g2.drawString(String.format("Time (T) = %d", inst.T), 400, 70);
//        g2.drawString(String.format("LandLocal (Z) = %s", statistic.actualLandingLocal), 540, 70);                

        
        int posLegX = 600;
        int posLegY = 72;
        g2.drawString(String.format("Legend"), posLegX, posLegY + 17);
        g2.setColor(ColorPlot.ZONE_N);
        g2.fillRect(posLegX + 70, posLegY, 20, 20);
        g2.setColor(ColorPlot.ZONE_P);
        g2.fillRect(posLegX + 100, posLegY, 20, 20);
        g2.setColor(ColorPlot.ZONE_B);
        g2.fillRect(posLegX + 130, posLegY, 20, 20);
        g2.setColor(ColorPlot.ZONE_R);
        g2.fillRect(posLegX + 160, posLegY, 20, 20);
        g2.setColor(ColorPlot.INFORMATION);
        g2.drawString(String.format("n"), posLegX + 74, posLegY + 14);
        g2.drawString(String.format("p"), posLegX + 104, posLegY + 14);
        g2.drawString(String.format("b"), posLegX + 134, posLegY + 14);
        g2.drawString(String.format("r"), posLegX + 164, posLegY + 14);
    }

    private void paintLegendRegions(Graphics2D g2) {
        for (int l = 0; l < inst.L; l++) {
            for (int j = 0; j < inst.bji[l].length; j++) {
                double xm = 0;
                double ym = 0;
                for (Point2D item : inst.poly[l][j]) {
                    xm += item.getX();
                    ym += INVERT_Y ? -item.getY() : item.getY();
                }
                xm = xm / inst.poly[l][j].length;
                ym = ym / inst.poly[l][j].length;
                g2.setColor(ColorPlot.LEGEND);
                g2.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 16));
                g2.drawString(String.format("%s", inst.NAME_REGIONS[l]),
                        (int) (zoon * (xm - minX) + OFF) - 12,
                        (int) (zoon * (ym - minY) + OFF) + 5);
                g2.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 11));
                g2.drawString(String.format("%d", (j + 1)),
                        (int) (zoon * (xm - minX) + OFF) - 2,
                        (int) (zoon * (ym - minY) + OFF) + 8);
            }
        }
    }

    private void paintUAV(Graphics2D g2) {
        g2.setColor(ColorPlot.UAV);
        g2.fillOval(-SIZE_UAV + (int) (zoon * (inst.X0[0] - minX) + OFF),
                -SIZE_UAV + (int) (zoon * (inst.X0[1] - minY) + OFF),
                2 * SIZE_UAV, 2 * SIZE_UAV);
    }

    private void paintRouteUAV(Graphics2D g2) {        
        for (double[][] vMUt : listMU) {
            if (timeSpecific == -1) {
                paintCenterImprecisionUAV(g2, vMUt);
                paintBoarderImprecisionUAV(g2, vMUt);
            } else {
                paintBoarderImprecisionUAV2(g2, vMUt);
            }
        }
    }
    
    private void paintMultiRouteUAV(Graphics2D g2) {                
        for (double[][] vMUt : listMU) {
            if (timeSpecific == -1) {
                paintPointMultiIndividual(g2, vMUt);
//                paintLineMultiIndividual(g2, vMUt);                
//                paintLineMultiIndividualAGMP(g2, vMUt, Color.RED);                
            }
        }
    }
    
    private void paintCenterImprecisionUAV(Graphics2D g2, double vMUt[][]) {
        g2.setColor(ColorPlot.CENTER_IMPRECISION_UAV);
        int RAIO = 2;
        double x2 = 0;
        double y2 = 0;
        for (int t = 0; t < inst.actualK; t++) { 
            double x1 = vMUt[t][0];
            double y1 = vMUt[t][2];
            x2 = vMUt[t + 1][0];
            y2 = vMUt[t + 1][2];
            g2.drawLine((int) (zoon * (x1 - minX) + OFF),
                    (int) (zoon * (y1 - minY) + OFF),
                    (int) (zoon * (x2 - minX) + OFF),
                    (int) (zoon * (y2 - minY) + OFF));
            g2.fillOval((int) (zoon * (x1 - minX) + OFF - RAIO),
                    (int) (zoon * (y1 - minY) + OFF - RAIO), 2 * RAIO, 2 * RAIO);
        }
        g2.fillOval((int) (zoon * (x2 - minX) + OFF - RAIO),
                    (int) (zoon * (y2 - minY) + OFF - RAIO), 2 * RAIO, 2 * RAIO);
    }
    
    

    int cont = 0;
    private void paintLineMultiIndividual(Graphics2D g2, double vMUt[][]) {
//        if (cont % 3 == 0){
//            g2.setColor(Color.RED);
//        }else if (cont % 3 == 1){
//            g2.setColor(Color.GREEN);
//        }else if (cont % 3 == 2){
//            g2.setColor(Color.BLUE);
//        }
//        cont++;        
        g2.setColor(new Color(0, 150, 0));
//        g2.setColor(new Color((int)(Math.random()*255), (int)(Math.random()*255), (int)(Math.random()*255)));
        int RAIO = 3;
        double x2 = 0;
        double y2 = 0;
        for (int t = 0; vMUt[t][1] > inst.V_MIN && t < inst.T; t++) { 
            double x1 = vMUt[t][0];
            double y1 = vMUt[t][2];
            x2 = vMUt[t + 1][0];
            y2 = vMUt[t + 1][2];
            g2.drawLine((int) (zoon * (x1 - minX) + OFF),
                    (int) (zoon * (y1 - minY) + OFF),
                    (int) (zoon * (x2 - minX) + OFF),
                    (int) (zoon * (y2 - minY) + OFF));
        }
        g2.fillOval((int) (zoon * (x2 - minX) + OFF - RAIO),
                    (int) (zoon * (y2 - minY) + OFF - RAIO), 2 * RAIO, 2 * RAIO);
    }
    
    private void paintLineMultiIndividualAGMP(Graphics2D g2, double vMUt[][], Color color) {        
//        g2.setColor(color);
        int RAIO = 3;
        double x2 = 0;
        double y2 = 0;
        for (int t = 0; vMUt[t][1] > inst.V_MIN && t < inst.T; t++) { 
            x2 = vMUt[t + 1][0];
            y2 = vMUt[t + 1][2];            
        }
        String l = Util.landingLocal(inst, x2, y2);
        if (l.equals("b")){
            g2.setColor(Color.BLUE);
        }else if (l.equals("p")){
            g2.setColor(Color.RED);
        }else if (l.equals("n")){
            g2.setColor(Color.BLACK);
        }else if (l.equals("r")){
            g2.setColor(Color.YELLOW);
        }
//        x2 = 0;
//        y2 = 0;
//        for (int t = 0; vMUt[t][1] > inst.V_MIN && t < inst.T; t++) { 
//            double x1 = vMUt[t][0];
//            double y1 = vMUt[t][2];
//            x2 = vMUt[t + 1][0];
//            y2 = vMUt[t + 1][2];
//            if (!l.equals("r") && !l.equals("n") && !l.equals("p")){
//                g2.drawLine((int) (zoon * (x1 - minX) + OFF),
//                        (int) (zoon * (y1 - minY) + OFF),
//                        (int) (zoon * (x2 - minX) + OFF),
//                        (int) (zoon * (y2 - minY) + OFF));
//            }
//        }
        g2.fillOval((int) (zoon * (x2 - minX) + OFF - RAIO),
                    (int) (zoon * (y2 - minY) + OFF - RAIO), 2 * RAIO, 2 * RAIO);
    }
    
    private void paintLineMultiIndividualAGMP2(Graphics2D g2, double vMUt[][], Color color) {
        g2.setColor(color);
        int RAIO = 3;
        double x2 = 0;
        double y2 = 0;
        for (int t = 0; vMUt[t][1] > inst.V_MIN && t < inst.T; t++) { 
            double x1 = vMUt[t][0];
            double y1 = vMUt[t][2];
            x2 = vMUt[t + 1][0];
            y2 = vMUt[t + 1][2];
            g2.drawLine((int) (zoon * (x1 - minX) + OFF),
                    (int) (zoon * (y1 - minY) + OFF),
                    (int) (zoon * (x2 - minX) + OFF),
                    (int) (zoon * (y2 - minY) + OFF));
        }
        g2.fillOval((int) (zoon * (x2 - minX) + OFF - RAIO),
                    (int) (zoon * (y2 - minY) + OFF - RAIO), 2 * RAIO, 2 * RAIO);
    }
    
    private void paintPointMultiIndividual(Graphics2D g2, double vMUt[][]) {
//        if (cont % 3 == 0){
//            g2.setColor(Color.RED);
//        }else if (cont % 3 == 1){
//            g2.setColor(Color.GREEN);
//        }else if (cont % 3 == 2){
//            g2.setColor(Color.BLUE);
//        }
//        cont++;
        g2.setColor(new Color(0, 150, 0)); //ColorPlot.CENTER_IMPRECISION_UAV);
//        g2.setColor(new Color((int)(Math.random()*255), (int)(Math.random()*255), (int)(Math.random()*255)));
        float RAIO = 2.5f;        
        int t;
        for (t = 0; vMUt[t][1] > inst.V_MIN && t < inst.T; t++) { 
            
        }
        double x2 = vMUt[t][0];
        double y2 = vMUt[t][2];
        g2.fillOval((int) (zoon * (x2 - minX) + OFF - RAIO),
                    (int) (zoon * (y2 - minY) + OFF - RAIO), (int)(2 * RAIO), (int)(2 * RAIO));
    }

    private void paintBoarderImprecisionUAV(Graphics2D g2, double vMUt[][]) {
        double rx = inst.P0;
        double ry = inst.P0;
        double erfInv = UtilStatistic.erf_inv(1 - 2 * inst.DELTA);
        double cx = Math.sqrt(2 * rx) * erfInv;
        double cy = Math.sqrt(2 * ry) * erfInv;
        double x1 = 0;
        double y1 = 0;
        g2.setColor(ColorPlot.BOARDER_IMPRECISION_UAV);
        for (int t = 0; t < inst.actualK; t++) {
            x1 = vMUt[t][0];
            y1 = vMUt[t][2];            
            g2.drawOval((int) (zoon * ((x1 - cx) - minX) + OFF),
                    (int) (zoon * ((y1 - cy) - minY) + OFF),
                    (int) (zoon * 2 * cx),
                    (int) (zoon * 2 * cy));
        }
        if (vMUt[inst.bestK][1] <= inst.V_MIN) {
            g2.setColor(ColorPlot.BOARDER_IMPRECISION_UAV_FINAL);
            g2.drawOval((int) (zoon * ((x1 - cx) - minX) + OFF),
                    (int) (zoon * ((y1 - cy) - minY) + OFF),
                    (int) (zoon * 2 * cx), (int) (zoon * 2 * cy));
        }        
    }

    private void paintBoarderImprecisionUAV2(Graphics2D g2, double vMUt[][]) {
        int t = timeSpecific;
        double rx = inst.P0;
        double ry = inst.P0;
        double erfInv = UtilStatistic.erf_inv(1 - 2 * inst.DELTA);
        double cx = Math.sqrt(2 * rx) * erfInv;
        double cy = Math.sqrt(2 * ry) * erfInv;
        double x1 = vMUt[t][0];
        double y1 = vMUt[t][2];
        g2.setColor(ColorPlot.BOARDER_IMPRECISION_UAV);
        g2.drawOval((int) (zoon * ((x1 - cx) - minX) + OFF),
                (int) (zoon * ((y1 - cy) - minY) + OFF),
                (int) (zoon * 2 * cx), (int) (zoon * 2 * cy));
        if (DEBUG) {
            System.out.println("Cx = " + cx);
            System.out.println("Cy = " + cy);
        }
    }

    public void paintGeographic(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(Color.LIGHT_GRAY);
        g2.fillRect(0, 0, inst.WIDTH - 1, inst.HEIGTH - 1);
        g2.setColor(Color.BLACK);
        g2.drawRect(0, 0, inst.WIDTH - 1, inst.HEIGTH - 1);

        zoon = Math.min((inst.WIDTH - OFF - 5) / (maxX - minX),
                (inst.HEIGTH - OFF - 5) / (maxY - minY));

        step = Math.max(maxX - minX, maxY - minY) * 3600 / 6;
        if (step > 3600) {
            step = (int) (step / 3600) * 3600;
        } else if (step > 600) {
            step = (int) (step / 600) * 600;
        } else if (step > 60) {
            step = (int) (step / 60) * 60;
        } else if (step > 10) {
            step = (int) (step / 10) * 10;
        }

        g2.setColor(Color.WHITE);
        g2.fillRect(OFF, OFF, (int) (zoon * (maxX - minX)),
                (int) (zoon * (maxY - minY)));
        g2.setColor(Color.BLACK);
        g2.drawRect(OFF, OFF, (int) (zoon * (maxX - minX)),
                (int) (zoon * (maxY - minY)));

        g2.setColor(Color.BLACK);
        g2.setFont(new Font(Font.MONOSPACED, Font.BOLD, 16));

        g2.drawString(String.format("step = %1.0f\"", step), 10, 15);

        int val = (int) (((int) ((minX * 3600) / step)) * step);
        val += minX < 0 ? -step : step;
        while (val < minX * 3600) {
            val += step;
        }
        while (val < maxX * 3600) {
            int degrees = Math.abs(val) / 3600;
            int min = (Math.abs(val) - degrees * 3600) / 60;
            int sec = (Math.abs(val) - degrees * 3600 - min * 60);
            int x = (int) (zoon * (val / 3600.0 - minX) + OFF);
            g2.setColor(Color.BLACK);
            g2.drawString(String.format(FORMAT_GEOGRAPHIC, degrees, min, sec, val > 0 ? 'L' : 'O'), x - 40, OFF - 10);

            g2.setColor(Color.LIGHT_GRAY);
            g2.drawLine(x, OFF, x, OFF + (int) (zoon * (maxY - minY)));

            val += step;
        }
        val = (int) (((int) ((minY * 3600) / step)) * step);
        val += minY < 0 ? -step : step;
        while (val < minY * 3600) {
            val += step;
        }
        while (val < maxY * 3600) {
            int degrees = Math.abs(val) / 3600;
            int min = (Math.abs(val) - degrees * 3600) / 60;
            int sec = (Math.abs(val) - degrees * 3600 - min * 60);
            int y = (int) (zoon * (val / 3600.0 - minY) + OFF);
            g2.setColor(Color.BLACK);
            g2.drawString(String.format(FORMAT_GEOGRAPHIC, degrees, min, sec, val > 0 ? 'S' : 'N'), 10, y + 4);

            g2.setColor(Color.LIGHT_GRAY);
            g2.drawLine(OFF, y, OFF + (int) (zoon * (maxX - minX)), y);

            val += step;
        }

        for (int l = 0; l < inst.L; l++) {
            for (int j = 0; j < inst.bji[l].length; j++) {
                if (DEBUG) {
                    System.out.println("---------- poly[" + j + "]------------");
                }
                for (int i = 0; i < inst.poly[l][j].length; i++) {
                    int k = (i + 1) % inst.poly[l][j].length;
                    if (DEBUG) {
                        System.out.printf("(%4g,%4g) -> (%4g,%4g)\n",
                                inst.poly[l][j][i].getX(), inst.poly[l][j][i].getY(),
                                inst.poly[l][j][k].getX(), inst.poly[l][j][k].getY());
                    }
                }
                if (DEBUG) {
                    System.out.println("---------- lines[" + j + "]------------");
                }
                for (int i = 0; i < inst.poly[l][j].length; i++) {
                    double x1, y1;
                    if (Math.abs(inst.aji[l][j][i][2]) > 0.01) {
                        x1 = inst.poly[l][j][i].getX();
                        y1 = (inst.bji[l][j][i] - inst.aji[l][j][i][0] * x1) / inst.aji[l][j][i][2];
                    } else {
                        y1 = inst.poly[l][j][i].getY();
                        x1 = (inst.bji[l][j][i] - inst.aji[l][j][i][2] * y1) / inst.aji[l][j][i][0];
                    }

                    int k = (i + 1) % inst.poly[j].length;

                    double x2, y2;
                    if (Math.abs(inst.aji[l][j][i][2]) > 0.01) {
                        x2 = inst.poly[l][j][k].getX();
                        y2 = (inst.bji[l][j][k] - inst.aji[l][j][k][0] * x2) / inst.aji[l][j][k][2];
                    } else {
                        y2 = inst.poly[l][j][k].getY();
                        x2 = (inst.bji[l][j][k] - inst.aji[l][j][k][2] * y2) / inst.aji[l][j][k][0];
                    }

                    double S = step / (Math.sqrt(inst.aji[l][j][i][0] * inst.aji[l][j][i][0]
                            + inst.aji[l][j][i][2] * inst.aji[l][j][i][2]) * 18000);
                    if (DEBUG) {
                        System.out.printf("(%4d,%4d) -> (%4d,%4d)\n", (int) (x1 * 100), (int) (y1 * 100), (int) (x2 * 100), (int) (y2 * 100));
                    }
                    g2.setColor(Color.BLACK);
                    g2.drawLine((int) (zoon * (x1 - minX) + OFF),
                            (int) (zoon * (y1 - minY) + OFF),
                            (int) (zoon * (x2 - minX) + OFF),
                            (int) (zoon * (y2 - minY) + OFF));

                    g2.setColor(Color.BLUE);
                    g2.drawLine((int) (zoon * ((x1 + x2) / 2 - minX)) + OFF,
                            (int) (zoon * ((y1 + y2) / 2 - minY)) + OFF,
                            (int) (zoon * ((x1 + x2) / 2 - minX + S * inst.aji[l][j][i][0])) + OFF,
                            (int) (zoon * ((y1 + y2) / 2 - minY + S * inst.aji[l][j][i][2])) + OFF);
                    g2.fillOval((int) (zoon * ((x1 + x2) / 2 - minX)) + OFF - 5,
                            (int) (zoon * ((y1 + y2) / 2 - minY)) + OFF - 5, 10, 10);
                    g2.fillOval((int) (zoon * ((x1 + x2) / 2 - minX + S * inst.aji[l][j][i][0])) + OFF - 5,
                            (int) (zoon * ((y1 + y2) / 2 - minY + S * inst.aji[l][j][i][2])) + OFF - 5, 10, 10);
                }
                double xm = 0;
                double ym = 0;
                for (Point2D item : inst.poly[l][j]) {
                    xm += item.getX();
                    ym += item.getY();
                }
                xm = xm / inst.poly[l][j].length;
                ym = ym / inst.poly[l][j].length;
                g2.setColor(Color.BLACK);
                g2.drawString(String.format("[%d %d]", l, j),
                        (int) (zoon * (xm - minX) + OFF) - 20,
                        (int) (zoon * (ym - minY) + OFF) + 5);
            }
        }

        g2.setStroke(new BasicStroke(1));
        g2.setColor(Color.BLACK);
        g2.fillOval((int) (-10 + (int) (zoon * (inst.X0[0] - minX) + OFF)),
                (int) (-10 + (int) (zoon * (inst.X0[1] - minY) + OFF)), 10, 10);
        g2.drawRect(-800, -1100, 1400, 1100);

        for (double[][] vMUt : listMU) {
            g2.setColor(Color.red);
            if (timeSpecific == -1) {
                for (int t = 0; t < inst.T; t++) {
                    double x1 = vMUt[t][0];
                    double y1 = vMUt[t][2];
                    double x2 = vMUt[t + 1][0];
                    double y2 = vMUt[t + 1][2];
                    g2.drawLine((int) (zoon * (x1 - minX) + OFF),
                            (int) (zoon * (y1 - minY) + OFF),
                            (int) (zoon * (x2 - minX) + OFF),
                            (int) (zoon * (y2 - minY) + OFF));
                    g2.fillOval((int) (zoon * (x1 - minX) + OFF - 5),
                            (int) (zoon * (y1 - minY) + OFF - 5), 10, 10);
                    g2.fillOval((int) (zoon * (x2 - minX) + OFF - 5),
                            (int) (zoon * (y2 - minY) + OFF - 5), 10, 10);
                }
                double erfInv = UtilStatistic.erf_inv(1 - 2 * inst.DELTA);
                for (int t = 0; t < inst.T + 1; t++) {
                    double x1 = vMUt[t][0];
                    double y1 = vMUt[t][2];

                    double rx = inst.P0;
                    double ry = inst.P0;
                    
                    double cx = Math.sqrt(2 * rx) * erfInv;
                    double cy = Math.sqrt(2 * ry) * erfInv;

                    if (DEBUG) {
                        System.out.println("Cx = " + cx);
                        System.out.println("Cy = " + cy);
                    }
                    g2.drawOval((int) (zoon * ((x1 - cx) - minX) + OFF),
                            (int) (zoon * ((y1 - cy) - minY) + OFF),
                            (int) (zoon * 2 * cx),
                            (int) (zoon * 2 * cy));
                }
            } else {
                int t = timeSpecific;
                double x1 = vMUt[t][0];
                double y1 = vMUt[t][2];

                double rx = inst.P0;
                double ry = inst.P0;
                double erfInv = UtilStatistic.erf_inv(1 - 2 * inst.DELTA);
                double cx = Math.sqrt(2 * rx) * erfInv;
                double cy = Math.sqrt(2 * ry) * erfInv;

                if (DEBUG) {
                    System.out.println("Cx = " + cx);
                    System.out.println("Cy = " + cy);
                }
                g2.drawOval((int) (zoon * ((x1 - cx) - minX) + OFF),
                        (int) (zoon * ((y1 - cy) - minY) + OFF),
                        (int) (zoon * ((2 * cx)) + OFF),
                        (int) (zoon * ((2 * cy)) + OFF));
            }
        }
    }

    public void adjustRange1() {
        minX = inst.X0[0];
        minY = inst.X0[1];
        maxX = inst.X0[0];
        maxY = inst.X0[1];
    }

    public void adjustRange2() {
        double offset = 0.2;
        double temp = Math.max(maxX - minX, maxY - minY);
        minX -= temp * offset;
        minY -= temp * offset;
        maxX += temp * offset;
        maxY += temp * offset;
    }

    public void adjustRange3(double x, double y) {
        minX = Math.min(minX, x);
        minY = Math.min(minY, y);
        maxX = Math.max(maxX, x);
        maxY = Math.max(maxY, y);
    }
     
}
