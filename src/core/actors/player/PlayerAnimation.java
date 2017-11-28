/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core.actors.player;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import core.AnimationManager;
import core.AssetsManager;
import core.actors.GameActor;

/**
 *
 * @author Alisson
 */
public class PlayerAnimation {
    public static final float STANDARD_ATK_FRAME_TIME = 0.15f;
    private final TextureRegion standImg;
    private final TextureRegion jumpImg;
    private final TextureRegion crouchImg;
    private final TextureRegion hurtedImg;
    private Animation<TextureRegion> walkAnimation;
    private Animation<TextureRegion> upstairsAnimation;
    private Animation<TextureRegion> downstairsAnimation;
    private Animation<TextureRegion> deathAnimation;
    private Animation<TextureRegion> standAtkAnimation;
    private Animation<TextureRegion> crouchAtkAnimation;
    private Animation<TextureRegion> jumpAtkAnimation;
    private Animation<TextureRegion> upStairsAtkAnimation;
    private Animation<TextureRegion> downStairsAtkAnimation;
    private float deathState = 0;

    public PlayerAnimation() {
        this.standImg = new TextureRegion(AssetsManager.assets.get("assets/img/playerSprites.png", Texture.class), 12, 8, 26, 49);
        this.crouchImg = new TextureRegion(AssetsManager.assets.get("assets/img/playerSprites.png", Texture.class), 62, 19, 26, 37);
        this.jumpImg = new TextureRegion(AssetsManager.assets.get("assets/img/playerSprites.png", Texture.class), 121, 9, 25, 47);
        this.hurtedImg = new TextureRegion(AssetsManager.assets.get("assets/img/playerSprites.png", Texture.class), 448, 91, 30, 47);
        this.prepareAnimations();
    }
    
    private void prepareAnimations(){
        this.walkAnimation = AnimationManager.generateAnimation(new TextureRegion(AssetsManager.assets.get("assets/img/playerSprites.png", Texture.class), 176, 6, 194, 50), 32, 50, Animation.PlayMode.LOOP);
        this.upstairsAnimation = AnimationManager.generateAnimation(new TextureRegion(AssetsManager.assets.get("assets/img/playerSprites.png", Texture.class), 405, 8, 194, 50), 32, 50, Animation.PlayMode.LOOP);
        this.downstairsAnimation = AnimationManager.generateAnimation(new TextureRegion(AssetsManager.assets.get("assets/img/playerSprites.png", Texture.class), 633, 8, 194, 50), 32, 50, Animation.PlayMode.LOOP);
        this.deathAnimation = AnimationManager.generateAnimation(AssetsManager.assets.get("assets/img/playerSprites.png", Texture.class), new int[]{537, 570, 624}, new int[]{103, 111, 125}, new int[]{26, 48, 67}, new int[]{34, 27, 13}, Animation.PlayMode.NORMAL);
        this.standAtkAnimation = AnimationManager.generateAnimation(AssetsManager.assets.get("assets/img/playerSprites.png", Texture.class),  new int[]{33, 86, 157}, new int[]{577, 563, 577}, new int[]{45, 55, 80}, new int[]{52, 62, 50}, Animation.PlayMode.NORMAL, STANDARD_ATK_FRAME_TIME);
        this.crouchAtkAnimation = AnimationManager.generateAnimation(AssetsManager.assets.get("assets/img/playerSprites.png", Texture.class),  new int[]{33, 86, 157}, new int[]{633, 633, 633}, new int[]{45, 55, 80}, new int[]{49, 49, 49}, Animation.PlayMode.NORMAL, STANDARD_ATK_FRAME_TIME);
        this.jumpAtkAnimation = AnimationManager.generateAnimation(AssetsManager.assets.get("assets/img/playerSprites.png", Texture.class),  new int[]{35, 86, 157}, new int[]{834, 824, 834}, new int[]{45, 55, 77}, new int[]{55, 59, 46}, Animation.PlayMode.NORMAL, STANDARD_ATK_FRAME_TIME / 1.5f);
        this.upStairsAtkAnimation = AnimationManager.generateAnimation(AssetsManager.assets.get("assets/img/playerSprites.png", Texture.class),  new int[]{33, 86, 157}, new int[]{707, 694, 708}, new int[]{47, 55, 77}, new int[]{51, 59, 46}, Animation.PlayMode.NORMAL, STANDARD_ATK_FRAME_TIME);
        this.downStairsAtkAnimation = AnimationManager.generateAnimation(AssetsManager.assets.get("assets/img/playerSprites.png", Texture.class),  new int[]{33, 86, 157}, new int[]{771, 759, 772}, new int[]{45, 55, 79}, new int[]{52, 59, 46}, Animation.PlayMode.NORMAL, STANDARD_ATK_FRAME_TIME);
    }
    
    public Animation<TextureRegion> getCorrectAtkAnimation(GameActor.Atk_State atkState, boolean upStairs){
        switch(atkState){
            case STAND_ATK:
                return this.standAtkAnimation;
            case JUMP_ATK:
                return this.jumpAtkAnimation;
            case STAIRS_ATK:
                return this.getStairsAtkAnimation(upStairs);
            case CROUCH_ATK:
                return this.crouchAtkAnimation;
        }
        return null;
    }
    
    public TextureRegion getStandImg() {
        return standImg;
    }

    public TextureRegion getHurtedImg() {
        return hurtedImg;
    }

    public TextureRegion getCrouchImg() {
        return crouchImg;
    }

    public TextureRegion getJumpImg() {
        return jumpImg;
    }

    public Animation<TextureRegion> getWalkAnimation() {
        return walkAnimation;
    }
    
    public Animation<TextureRegion> getDownstairsAnimation() {
        return downstairsAnimation;
    }

    public Animation<TextureRegion> getUpstairsAnimation() {
        return upstairsAnimation;
    }

    public Animation<TextureRegion> getDeathAnimation() {
        return deathAnimation;
    }

    public Animation<TextureRegion> getStandAtkAnimation() {
        return standAtkAnimation;
    }

    public Animation<TextureRegion> getCrouchAtkAnimation() {
        return crouchAtkAnimation;
    }

    public Animation<TextureRegion> getJumpAtkAnimation() {
        return jumpAtkAnimation;
    }

    public Animation<TextureRegion> getUpStairsAtkAnimation() {
        return upStairsAtkAnimation;
    }

    public Animation<TextureRegion> getDownStairsAtkAnimation() {
        return downStairsAtkAnimation;
    }
    
    public Animation<TextureRegion> getStairsAtkAnimation(boolean upstairs) {
        return (upstairs) ? upStairsAtkAnimation: downStairsAtkAnimation;
    }
}
