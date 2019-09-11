/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.com.Stream;

import java.awt.Color;


/**
 *
 * @author marcio
 */
public interface StreamPlot2D extends Stream{
    public void point(int id, double x, double y) throws Exception;
    public void point(String name, double x, double y) throws Exception;
    
    public void point(int id, double x, double y, Color color) throws Exception;
    public void point(int id, double x, double y, int rgb) throws Exception;
    public void point(String name, double x, double y, Color color) throws Exception;
    public void point(String name, double x, double y, int rgb) throws Exception;
    
    public void point(int id, double x, double y, Color color, String description) throws Exception;
    public void point(int id, double x, double y, int rgb, String description) throws Exception;
    public void point(String name, double x, double y, Color color, String description) throws Exception;
    public void point(String name, double x, double y, int rgb, String description) throws Exception;
    
    public void clear(int id) throws Exception;
    public void clear(String name) throws Exception;

    public void background(Color color);
}