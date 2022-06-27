package Resource;

import Utilities.Constants;
import jade.core.Agent;
import jade.domain.FIPAAgentManagement.FailureException;
import jade.domain.FIPAAgentManagement.NotUnderstoodException;
import jade.domain.FIPAAgentManagement.RefuseException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.AchieveREResponder;

import java.util.concurrent.TimeUnit;

public class REQUEST_responder extends AchieveREResponder {

    private boolean passou = true;

    public REQUEST_responder(Agent a, MessageTemplate mt){

        super(a,mt);
    }


    @Override
    protected ACLMessage handleRequest(ACLMessage request) throws NotUnderstoodException, RefuseException {

        ACLMessage msg = request.createReply();
        msg.setPerformative(ACLMessage.AGREE);
        String[] parts = request.getContent().split(Constants.TOKEN);

        System.out.println("I am " + myAgent.getLocalName()+" sender is " +request.getSender().getLocalName());

        switch (parts[0]){
            case ("free"):
                System.out.println("RA_Request : step free,the "+ ((ResourceAgent)myAgent).current_product +" is about to leave, i can accept new ones");
                ((ResourceAgent) myAgent).swap = "None";
                //fica livre se o que libertou é igual ao que tinha
                if(((ResourceAgent)myAgent).current_product.equalsIgnoreCase(request.getSender().getLocalName())) {
                    ((ResourceAgent) myAgent).current_product = "None";
                }
                break;
            case ("swap"):
                System.out.println("RA_Request : swap step: my "+ ((ResourceAgent)myAgent).current_product +
                        " wants to swap with " + parts[1]);
                ((ResourceAgent)myAgent).swap = parts[1];
                break;
            default:
                System.out.println("RA_Request : step 6 - Execute Skill");
                //comment this and in the TA if you dont want sim
                this.passou = ((ResourceAgent)myAgent).myLib.executeSkill(parts[0]);
                break;
        }
        System.out.println("My product now is " + ((ResourceAgent) myAgent).current_product);

        return msg;
    }

    @Override
    protected ACLMessage prepareResultNotification(ACLMessage request, ACLMessage response) throws FailureException {
        System.out.println("RA_Request : step 7 - Inform que a Skill foi executada->  " + request.getContent());
        ACLMessage msg = request.createReply();
        msg.setPerformative(ACLMessage.INFORM);
        System.out.println("______________________________________"+String.valueOf(this.passou));
        msg.setContent(String.valueOf(this.passou));
        return msg;
    }
}
