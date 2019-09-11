/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ProOF.apl.jsa.problem.PPCCS.util;

import com.gif4j.GifEncoder;
import com.gif4j.GifFrame;
import com.gif4j.GifImage;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 *
 * @author jesimar
 */
public final class CreateGif {    

    public CreateGif() throws IOException{
        
    }
    
    public void createGif(BufferedImage[] images, File fileToSave) 
            throws IOException { 
        createGif(images, fileToSave, 30);
    }
    
    public void createGif(BufferedImage[] images, File fileToSave,
            int delay) throws IOException { 
        createGif(images, fileToSave, delay, 1000);
    }
    
    public void createGif(BufferedImage[] images, File fileToSave,
            int delay, int loopNumber) throws IOException { 
        createGif(images, fileToSave, delay, loopNumber, "");
    }
    
    public void createGif(BufferedImage[] images, File fileToSave,
            int delay, int loopNumber, String comment) throws IOException {       
        GifImage gifImage = new GifImage();        
        gifImage.setDefaultDelay(delay);
        gifImage.addComment(comment);
        for (BufferedImage image : images) {
            gifImage.addGifFrame(new GifFrame(image));
        }
        gifImage.setLoopNumber(loopNumber);
        GifEncoder.encode(gifImage, fileToSave);
    }
    
}
