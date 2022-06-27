package Transport;

import Utilities.Constants;
import jade.core.Agent;
import jade.domain.FIPAAgentManagement.FailureException;
import jade.domain.FIPAAgentManagement.NotUnderstoodException;
import jade.domain.FIPAAgentManagement.RefuseException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.ContractNetResponder;

public class CFP_responderTA extends ContractNetResponder {

    String[] associatedSkills;

    public CFP_responderTA(Agent a, MessageTemplate mt, String[] associatedSkillsTA) {
        super(a,mt);
        this.associatedSkills = associatedSkillsTA;

    }


        @Override
    protected ACLMessage handleCfp(ACLMessage cfp) throws RefuseException, FailureException, NotUnderstoodException {
            System.out.println(myAgent.getLocalName() + ":  Processing CFP Message");
            ACLMessage msg = cfp.createReply();
            msg.setPerformative(ACLMessage.PROPOSE);
            msg.setContent(Double.toString(Math.random()));
            msg.setOntology(Constants.ONTOLOGY_NEGOTIATE_RESOURCE);
            return msg;
    }

    @Override
    protected ACLMessage handleAcceptProposal(ACLMessage cfp, ACLMessage propose, ACLMessage accept) throws FailureException {
        System.out.println(myAgent.getLocalName() + ":  Preparing result of CFP : Transport");
        System.out.println("MOVE");
        ACLMessage msg = cfp.createReply();
        msg.setPerformative(ACLMessage.INFORM);
        msg.setContent(String.valueOf(associatedSkills));
        return msg;
    }
}
