package Transport;

import Resource.CFP_responder;
import Resource.REQUEST_responder;
import Resource.ResourceAgent;
import Utilities.ConsoleFrame;
import Utilities.Constants;
import Utilities.DFInteraction;
import jade.core.Agent;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import Libraries.ITransport;
import jade.domain.FIPAAgentManagement.FailureException;
import jade.domain.FIPAAgentManagement.NotUnderstoodException;
import jade.domain.FIPAAgentManagement.RefuseException;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.ContractNetResponder;

/**
 *
 * @author Ricardo Silva Peres <ricardo.peres@uninova.pt>
 */
public class TransportAgent extends Agent {

    String id;
    ITransport myLib;
    String description;
    String[] associatedSkills;

    @Override
    protected void setup() {
        Object[] args = this.getArguments();
        this.id = (String) args[0];
        this.description = (String) args[1];

        //Load hw lib
        try {
            String className = "Libraries." + (String) args[2];
            Class cls = Class.forName(className);
            Object instance;
            instance = cls.newInstance();
            myLib = (ITransport) instance;
            System.out.println(instance);
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
            Logger.getLogger(TransportAgent.class.getName()).log(Level.SEVERE, null, ex);
        }


        myLib.init(this);
        this.associatedSkills = myLib.getSkills();

        System.out.println("Transport Deployed: " + this.id + " Executes: " + Arrays.toString(associatedSkills));

        // TO DO: Register in DF
        DFInteraction dfinteraction = new DFInteraction();
        try {
            dfinteraction.RegisterInDF(this, this.associatedSkills, Constants.DFSERVICE_TRANSPORT);
        } catch (FIPAException e) {
            e.printStackTrace();
        }
        // TO DO: Add responder behaviour/s
        this.addBehaviour(new CFP_responderTA(this, MessageTemplate.MatchPerformative(ACLMessage.CFP), associatedSkills));
        this.addBehaviour(new REQUEST_responderTA(this, MessageTemplate.MatchPerformative(ACLMessage.REQUEST)));
    }


    @Override
    protected void takeDown() {
        super.takeDown();
    }
}
