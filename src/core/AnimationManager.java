/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

/**
 *
 * @author Augustop
 */
public class AnimationManager {
    
    public static Animation<TextureRegion> generateAnimation(TextureRegion spritesSheet, int spriteWidth, int spriteHeight, PlayMode loopMode){
        Array<TextureRegion> spriteArray = new Array<TextureRegion>();
        TextureRegion[][] splitedSprites = spritesSheet.split(spriteWidth, spriteHeight);
        for (int i = 0; i < splitedSprites.length; i++) {
            for (int j = 0; j < splitedSprites[0].length; j++) {
                spriteArray.add(splitedSprites[i][j]);
            }
        }
        if(spriteArray.size == 0){
            System.out.println("Error in generating animation"); // Possible exception
            return null;
        }
        return new Animation<TextureRegion>(1f / spriteArray.size, spriteArray, loopMode);
    }
    
}