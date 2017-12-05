/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core.objects;

/**
 *
 * @author Adson Esteves
 */
public class Arrow {
    public float positionX;
    public float positionY;
    boolean faceToRight;

    public Arrow(float positionX, float positionY, boolean faceToRight) {
        this.positionX = positionX;
        this.positionY = positionY;
        this.faceToRight = faceToRight;
    }

    public boolean isFaceToRight() {
        return faceToRight;
    }

    public void setFaceToRight(boolean faceToRight) {
        this.faceToRight = faceToRight;
    }   
    
}
