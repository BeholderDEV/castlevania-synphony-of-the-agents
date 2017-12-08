/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ai;

import ai.behavior.ArcherAgentBehavior;
import ai.behavior.BatAgentBehavior;
import ai.behavior.SwordAgentBehavior;
import core.actors.enemies.ArcherSkeleton;
import core.actors.enemies.Bat;
import core.actors.enemies.SwordSkeleton;
import jade.core.Agent;

/**
 *
 * @author Augustop
 */
public class AgentCore extends Agent{
    
    @Override
    protected void setup() {
        System.out.println("Iniating agent: " + getAID().getName());
        Object [] args = getArguments();
        if(args != null && args.length > 0){
            if(getAID().getName().startsWith("Sword_Skeleton")){
                addBehaviour(new SwordAgentBehavior((SwordSkeleton) args[0], this, AgentCreator.BEHAVIOR_DELAY));
            }
            if(getAID().getName().startsWith("Archer_Skeleton")){
                addBehaviour(new ArcherAgentBehavior((ArcherSkeleton) args[0], this, AgentCreator.BEHAVIOR_DELAY));
            }
            if(getAID().getName().startsWith("Bat")){
                addBehaviour(new BatAgentBehavior((Bat) args[0], this, AgentCreator.BEHAVIOR_DELAY));
            }
        }
    }
        
    @Override
    protected void takeDown() {
        System.out.println("Ending agent " + getAID().getName());
    }
    
}
