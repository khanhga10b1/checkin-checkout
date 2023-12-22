package bv.utils;

import java.awt.*;

public class CompUtils {

    private CompUtils() {
    }
    
    
    public  static int getPreWidth(Component component){
        return component.getPreferredSize().width;
        
    }
    
     public  static int getPreHeight(Component component){
        return component.getPreferredSize().height;
        
    }
     
     public static int getDWidth(Component component){
        return component.getSize().width;
     }
    
}