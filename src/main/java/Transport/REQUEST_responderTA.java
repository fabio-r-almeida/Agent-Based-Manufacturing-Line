package Transport;

import Utilities.Constants;
import jade.core.Agent;
import jade.domain.FIPAAgentManagement.FailureException;
import jade.domain.FIPAAgentManagement.NotUnderstoodException;
import jade.domain.FIPAAgentManagement.RefuseException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.AchieveREResponder;

import java.util.concurrent.TimeUnit;

public class REQUEST_responderTA extends AchieveREResponder {

    public REQUEST_responderTA(Agent a, MessageTemplate mt){

        super (a,mt);
    }

    @Override
    protected ACLMessage handleRequest(ACLMessage request)throws RefuseException, NotUnderstoodException {
        System.out.println("TA_Request : Step 6 - handle request move");
        ACLMessage msg = request.createReply();
        msg.setPerformative(ACLMessage.AGREE);
        String[] parts = request.getContent().split(Constants.TOKEN);

        //comment and this and in the RA if you dont want sim
        ((TransportAgent)myAgent).myLib.executeMove(parts[0], parts[1], request.getConversationId());
        /*try {
            TimeUnit.MILLISECONDS.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
        return msg;
    }

    @Override
    protected ACLMessage prepareResultNotification(ACLMessage request, ACLMessage response) throws FailureException{
        System.out.println("TA_Request : Step 7 - Inform move had been done-> "  + request.getContent());
        ACLMessage msg = request.createReply();
        msg.setPerformative(ACLMessage.INFORM);
        String[] parts = request.getContent().split(Constants.TOKEN);
        msg.setContent(parts[1]);

        return msg;
    }
}
