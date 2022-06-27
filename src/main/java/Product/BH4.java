package Product;

import Utilities.Constants;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.SequentialBehaviour;
import jade.lang.acl.ACLMessage;
import jade.proto.AchieveREInitiator;


import java.util.ArrayList;

public class BH4 extends AchieveREInitiator {
    ACLMessage msg_do_skill;

    public BH4(Agent a, ACLMessage msg, ACLMessage do_skill) {
        super(a,msg);
        msg_do_skill = do_skill;
    }

    @Override
    protected void handleAgree(ACLMessage agree) {
        System.out.println("BH4 : step 8 - Handle Agree");

    }

    @Override
    protected void handleInform(ACLMessage inform) {
        System.out.println("BH4 : step 9 - handle Inform");


        ((ProductAgent) myAgent).location = inform.getContent();
        myAgent.addBehaviour(new BH3(myAgent, msg_do_skill));

    }
}