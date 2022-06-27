package pt.unl.fct.srcim.lab3;

public class Main {

  public  static void main(String[] args){
        InspectionModel_v1 im = new InspectionModel_v1("model_loss=0.1029804398616155accuracy=0.975 (1).h5");
        System.out.println(im.predict(im.loadImage("product4.jpg",1,1,1)));


    }
}

