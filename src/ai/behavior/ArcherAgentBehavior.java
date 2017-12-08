/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ai.behavior;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import core.actors.CollisionHandler;
import core.actors.GameActor;
import core.actors.enemies.ArcherSkeleton;
import core.actors.enemies.Enemy;
import core.objects.Arrow;
import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.Property;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.util.leap.Iterator;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Adson Esteves
 */
public class ArcherAgentBehavior extends AgentBehavior{
    
    public static final float ARCHER_DISTANCE_TO_ATK = 30f;
    public static float TIME_BEFORE_WALKING = 3f;
    private float attack_cooldown = 2f;
    private boolean arrowCreatedOnAtk = false;
    private final List<AID> swordGuardiansAdress = new ArrayList<>();
//    private float closestGuardian = -1f;
    private final DFAgentDescription guardianSearcher = new DFAgentDescription();
    private long lastPositionUpdateTime = 0;
    private long lastGuardianSearchTime = 0;
    
    public ArcherAgentBehavior(Enemy container, Agent a, long period) {
        super(container, a, period, ARCHER_DISTANCE_TO_ATK);
        super.container.setFacingRight(true);
        super.container.setCurrentState(GameActor.State.WALKING);
        super.container.getVelocity().set(super.container.getWalkingSpeed(), 0);
        ServiceDescription sd = new ServiceDescription();
        sd.setType("guard");
        this.guardianSearcher.addServices(sd);
    }
    
    @Override
    public void defineAction(){
        if(super.container.isIsBlinking()){
            super.updateHurted();
        }
        this.defineCommunicationWithGuardians();
        switch(super.container.getCurrentState()){
            case STANDING:
                this.defineActionStanding();
            break;
            case WALKING:
                this.defineActionWalking();
            break;
            case ATTACKING:
                this.defineActionAtk();
            break;
            case JUMPING:
                this.container.fallFromJump();
            break;
            case DYING:
                this.realizeAgentTakeDown();
            break;
        }
    }
    
    private void defineActionStanding(){
        super.container.getVelocity().set(0, 0);
        if(super.container.getStateTime() >= this.attack_cooldown && super.checkIfCanAtk()){
            return;
        }
        if(super.container.getStateTime() >= TIME_BEFORE_WALKING){
            super.container.getVelocity().x = super.container.getWalkingSpeed();
            super.container.setFacingRight(!super.container.isFacingRight());
            super.container.setCurrentState(GameActor.State.WALKING);
        }
    }
        
    private void defineActionWalking(){
        if(super.checkIfCanAtk()){
            return;
        }
        Vector2 initialPosition = ((ArcherSkeleton)super.container).getInitialPosition();
        float range = ((ArcherSkeleton)super.container).getWalkingRange();
        float xNew = super.container.getBody().x += super.container.getVelocity().x * super.container.getGameScreen().getLastDelta() * ((super.container.isFacingRight()) ? 1: -1);
        if((super.container.isFacingRight() && xNew > initialPosition.x + range) || (!super.container.isFacingRight() && xNew < initialPosition.x - range)){
            super.container.setStateTime(0);
            super.container.setCurrentState(GameActor.State.STANDING);
        }
    }
        
    private void defineActionAtk(){
        if(!arrowCreatedOnAtk && super.container.getStateTime() >= GameActor.STANDARD_ATK_FRAME_TIME * 3){
            ((ArcherSkeleton)super.container).getArrows().add(
                new Arrow((super.container.isFacingRight()) ? super.container.getBody().x + super.container.getBody().width : super.container.getBody().x, 
                super.container.getBody().y,
                super.container.isFacingRight()));
            this.arrowCreatedOnAtk = true;
        }
        super.updateAtk();
        if(super.container.getCurrentState() == GameActor.State.STANDING){
            this.arrowCreatedOnAtk = false;
        }
    }

    @Override
    protected boolean checkIfFoundPlayer() {
        if (super.container.isFacingRight() && super.container.getBody().x + super.container.getSpriteAdjustmentForCollision()[0] > this.player.getBody().x
        || !super.container.isFacingRight() && super.container.getBody().x - super.container.getSpriteAdjustmentForCollision()[0] < this.player.getBody().x){
            return false;
        }
        return super.checkIfFoundPlayer();
    }
    
    @Override
    public void checkCollisions(){
        super.groundBehavior();
        super.wallBehavior();
        if(((ArcherSkeleton)super.container).getArrows().size != 0){
            this.updateWeaponHit();
        }
    }
    
        
    @Override
    public void checkStatus(){
        if(super.container.getLifePoints() <= 0){
            super.container.getVelocity().set(0, 0);
            super.container.setCurrentState(GameActor.State.DYING);
        }
    }
    
    @Override
    protected void realizeAgentTakeDown(){
        this.prepareMessageToGuards("Patron died");
        super.myAgent.doDelete();
    }
    
    private void updateWeaponHit(){
        Array<Arrow> arrows = ((ArcherSkeleton)super.container).getArrows();
        Rectangle arrow;
        for (int i = 0; i < arrows.size; i++) {
            arrow = arrows.get(i).getCollisionBody();
            Rectangle weaponArea = CollisionHandler.rectanglePool.obtain();
            weaponArea.set(arrow.x, arrow.y + Arrow.DISTANCE_FROM_GROUND, (arrows.get(i).isFaceToRight()) ? arrow.width: -arrow.width, arrow.height);
            if(CollisionHandler.checkCollisionBetweenBodyAndObject(super.player, weaponArea)){
                super.player.receiveDamage(super.container.getBody(), 1);
                arrows.removeIndex(i);
                i--;
            }
            CollisionHandler.rectanglePool.free(weaponArea);
        }
    }
    
    private void defineCommunicationWithGuardians(){
        ACLMessage msg = myAgent.receive();
        if(msg != null){
            switch(msg.getContent()){
                case "Accept":
                    this.sendConfirmationMessage(msg);
                break;
                case "Guardian died":
                    this.swordGuardiansAdress.remove(msg.getSender());
                break;
            }
        }else{
            this.searchForGuardians();
        }
        
        if(!this.swordGuardiansAdress.isEmpty()){
            this.updatePositionForGuardians();
        }
    }
    
    private void searchForGuardians(){
        if(this.swordGuardiansAdress.isEmpty()){
            this.sendMessageToAvaliableGuardians();
        }
    }
    
    private void updatePositionForGuardians(){
        if(System.currentTimeMillis() - lastPositionUpdateTime < 2000){
            return;
        }
        this.prepareMessageToGuards("Update Guard Position," + this.container.getBody().x + "," + this.container.getBody().y);
        this.lastPositionUpdateTime = System.currentTimeMillis();
    }
    
    private void sendMessageToAvaliableGuardians(){
        if(System.currentTimeMillis() - this.lastGuardianSearchTime < 1000){
            return;
        }
        try {
            AID closestAddress = null;
            double dist, closestGuardian = -1;
            DFAgentDescription[] result = DFService.search(myAgent, this.guardianSearcher);
            for (int i = 0; i < result.length; i++) {
                dist = this.defineDistanceToGuardian(result[i]);
                if(dist != -1){
                    if(closestGuardian == -1 || (closestGuardian > dist)){
                        closestGuardian = dist;
                        closestAddress = result[i].getName();
                    }
                }
            }
            if(closestAddress != null){
                System.out.println(closestAddress);
                ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
                msg.addReceiver(closestAddress);
                msg.setContent("Requesting guard");
                super.myAgent.send(msg);
            }

        } catch (FIPAException ex) {
            Logger.getLogger(ArcherAgentBehavior.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.lastGuardianSearchTime = System.currentTimeMillis();
    }
    
    private double defineDistanceToGuardian(DFAgentDescription result){
        float dx = -1, dy = -1;
        Iterator iter = result.getAllServices();
        while(iter.hasNext()){
            ServiceDescription sd = (ServiceDescription) iter.next();
            Iterator proIter = sd.getAllProperties();
            while(proIter.hasNext()){
                Property p = (Property) proIter.next();
                if(p.getName().equals("x")){
                    dx = Math.abs(super.container.getBody().x - Float.parseFloat((String)p.getValue()));
                }
                if(p.getName().equals("y")){
                    dy = Math.abs(super.container.getBody().y - Float.parseFloat((String)p.getValue()));
                }
            }
        }
        return ((dx != -1 && dy != -1) ? (0.394 * (dx + dy) + 0.554 * Math.max(dx, dy)): -1);
    }
    
    private void sendConfirmationMessage(ACLMessage msg){
        this.swordGuardiansAdress.add(msg.getSender());
        ACLMessage reply = msg.createReply();
        reply.setPerformative(ACLMessage.CONFIRM);
        Rectangle body = this.container.getBody();
        reply.setContent("Guard confirmation," + body.x + "," + body.y);
        super.myAgent.send(reply);
    }
    
    private void prepareMessageToGuards(String msgContent){
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        for (AID guard : swordGuardiansAdress) {
            msg.addReceiver(guard);
        }
        msg.setContent(msgContent);
        super.myAgent.send(msg);
    }

    public List<AID> getSwordGuardians() {
        return swordGuardiansAdress;
    }
}
