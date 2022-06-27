package Utilities;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.datavec.image.loader.NativeImageLoader;
import org.deeplearning4j.nn.modelimport.keras.KerasModelImport;
import org.deeplearning4j.nn.modelimport.keras.exceptions.InvalidKerasConfigurationException;
import org.deeplearning4j.nn.modelimport.keras.exceptions.UnsupportedKerasConfigurationException;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.common.io.ClassPathResource;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.api.preprocessor.ImagePreProcessingScaler;
import org.nd4j.linalg.factory.Nd4j;

import javax.imageio.ImageIO;

/**
 *
 * @author Ricardo Silva Peres <ricardo.peres@uninova.pt>
 */
public class InspectionModel {

    private MultiLayerNetwork model;

    // TODO: Initialize the model in the constructor using loadModel method
    public InspectionModel(String modelPath) {
        this.model = loadModel(modelPath);
    }

    // TODO: Load the sequential model using KerasModelImport
    // Relevant classes: MultiLayerNetwork, KerasModelImport
    public MultiLayerNetwork loadModel(String filepath) {
        String simpleMlp = null;
        MultiLayerNetwork model;

        try {
            simpleMlp = new ClassPathResource(filepath).getFile().getPath();
            model = KerasModelImport.importKerasSequentialModelAndWeights(simpleMlp);

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (UnsupportedKerasConfigurationException e) {
            throw new RuntimeException(e);
        } catch (InvalidKerasConfigurationException e) {
            throw new RuntimeException(e);
        }
        return model;
    }

    public static BufferedImage resize(String inputImagePath)
            throws IOException {
        // lazy fabio moment :)
        int scaledWidth = 224;
        int scaledHeight = 224;

        // reads input image
        File inputFile = new File(inputImagePath);
        BufferedImage inputImage = ImageIO.read(inputFile);

        // creates output image
        BufferedImage outputImage = new BufferedImage(scaledWidth,
                scaledHeight, inputImage.getType());

        // scales the input image to the output image
        Graphics2D g2d = outputImage.createGraphics();
        g2d.drawImage(inputImage, 0, 0, scaledWidth, scaledHeight, null);
        g2d.dispose();

        // returns the image
        return outputImage;
    }




    // TODO: Load the image and return the corresponding array. The input shape should match the one from training
    // Relevant classes: NativeImageLoader, INDArray
    public INDArray loadImage(String filepath, int height, int width, int channels) {
        INDArray image = null;
        // Load the image file
        BufferedImage myImage = null;
        try {
            // myImage = ImageIO.read(new FileInputStream("product1.jpg")); //ou fileopen things
            // myImage = ImageIO.read(new FileInputStream(new ClassPathResource(filepath).getFile().getPath())); //ou fileopen things
            myImage = (resize(new ClassPathResource(filepath).getFile().getPath()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        //Use the NativeImageLoader to convert to a numerical matrix
        //NativeImageLoader loader = new NativeImageLoader(height, width, channels);
        NativeImageLoader loader = new NativeImageLoader(myImage.getHeight(), myImage.getWidth(), 3);

        //Load the image into an INDArray
        try {
            image = loader.asMatrix(myImage);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Conversion from NCHW to NHWC
        // https://github.com/eclipse/deeplearning4j/issues/8975
        image = image.permute(0, 2, 3, 1);

        return image;
    }

    // TODO: Classify the image by feeding the array from the loadImage method to the model
    // Relevant classes: INDArray, Nd4j
    public int predict(INDArray imageInput) {
        int pred = -1;
        INDArray output = model.output(imageInput);

        //System.out.println(output.getColumn(1).getFloat());
        return (int)output.getColumn(1).getFloat();
    }

}
