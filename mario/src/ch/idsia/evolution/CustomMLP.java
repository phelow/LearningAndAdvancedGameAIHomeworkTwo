package ch.idsia.evolution;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by IntelliJ IDEA.
 * User: julian
 * Date: Apr 28, 2009
 * Time: 2:15:10 PM
 */

public class CustomMLP implements FA<double[], double[]>, Evolvable
{
	
    private double[][] firstConnectionLayer;
    private double[][] recessiveGenesLayer;
    private double[][] lastConnectionLayer;
    private int recessiveGenesSlot;
    private double[] hiddenNeurons;
    private double[] outputs;
    private double[] inputs;
    //private double[] targetOutputs;
    public double mutationMagnitude = 0.1;
    public double criticalMutationMagnitude = .5f;


    public static double mean = 0.0f;        // initialization mean
    public static double deviation = 0.1f;   // initialization deviation
    
    public static final Random random = new Random();
    public double learningRate = 0.1;
    public int numHidden;

    public CustomMLP(int numberOfInputs, int numberOfHidden, int numberOfOutputs)
    {
    	recessiveGenesSlot = random.nextInt(6);
    	numHidden = numberOfHidden;
        firstConnectionLayer = new double[numberOfInputs][numberOfHidden];
        recessiveGenesLayer = new double[numberOfInputs][numberOfHidden];
        lastConnectionLayer = new double[numberOfHidden][numberOfOutputs];
        hiddenNeurons = new double[numberOfHidden];
        outputs = new double[numberOfOutputs];
        //targetOutputs = new double[numberOfOutputs];
        inputs = new double[numberOfInputs];
        initializeLayer(firstConnectionLayer);
        
        initializeLayer(lastConnectionLayer);
    }

    public CustomMLP(double[][] firstConnectionLayer, double[][] recessiveGenesLayer, double[][] fifthConnectionLayer, int numberOfHidden,
               int numberOfOutputs)
    {
    	recessiveGenesSlot = random.nextInt(6);
        this.firstConnectionLayer = firstConnectionLayer;
        this.recessiveGenesLayer = recessiveGenesLayer;
        this.lastConnectionLayer = fifthConnectionLayer;
        inputs = new double[firstConnectionLayer.length];
        hiddenNeurons = new double[numberOfHidden];
        outputs = new double[numberOfOutputs];
    }

    protected void initializeLayer(double[][] layer)
    {
        for (int i = 0; i < layer.length; i++)
        {
            for (int j = 0; j < layer[i].length; j++)
            {
                layer[i][j] = (random.nextGaussian() * deviation + mean);
            }
        }
    }

    public CustomMLP getNewInstance()
    {
        return new CustomMLP(firstConnectionLayer.length, numHidden, outputs.length);
    }

    public CustomMLP copy()
    {
    	CustomMLP copy = new CustomMLP(copy(firstConnectionLayer), copy(recessiveGenesLayer),copy(lastConnectionLayer),
                hiddenNeurons.length, outputs.length);
        copy.setMutationMagnitude(mutationMagnitude);
        return copy;
    }

    private double[][] copy(double[][] original)
    {
        double[][] copy = new double[original.length][original[0].length];
        for (int i = 0; i < original.length; i++)
        {
            System.arraycopy(original[i], 0, copy[i], 0, original[i].length);
        }
        return copy;
    }

    public void mutate()
    {
    	int mutationType = random.nextInt(100);
        mutate(firstConnectionLayer,mutationType);
        mutate(recessiveGenesLayer,mutationType);
        mutate(lastConnectionLayer,mutationType);
    }

    private void mutate(double[] array, int mutationType)
    {
    	if(mutationType == 37){
    		for (int i = 0; i < array.length; i++)
	        {
	            array[i] += random.nextGaussian() * (criticalMutationMagnitude);
	        }
    	}
    	else if(mutationType == 54 ){
    		for (int i = 0; i < array.length; i++)
	        {
    			if(random.nextInt(25) == 16){
    				array[i] += random.nextGaussian() * (criticalMutationMagnitude);
    			}
    			else{
    	            array[i] += random.nextGaussian() * mutationMagnitude;   				
    				
    			}
	        }
    	}
    	else{
	        for (int i = 0; i < array.length; i++)
	        {
	            array[i] += random.nextGaussian() * mutationMagnitude;
	        }
    	}
    }

    private void mutate(double[][] array, int mutationType)
    {
        for (double[] anArray : array)
        {
            mutate(anArray,mutationType);
        }
    }

    public void psoRecombine(CustomMLP last, CustomMLP pBest, CustomMLP gBest)
    {
        // Those numbers are supposed to be constants. Ask Maurice Clerc.
        final double ki = 0.729844;
        final double phi = 2.05;
        final double psi = .1f;

        double phi1 = phi * random.nextDouble();
        double phi2 = phi * random.nextDouble();
        double phi3 = psi * random.nextDouble();
        double t = phi3;
        
        
        //System.out.println("phi1: "+phi1+" phi2: "+phi2);
        //System.out.println(" LAST:" + last);
        //System.out.println(" PBEST:" + pBest);
        //System.out.println(" GBEST:" + gBest);
        //System.out.println(" THIS:" + toString());
        if(recessiveGenesSlot == 1&& pBest.recessiveGenesSlot == 1){
    		phi3 = t;
    	}
    	else{
    		phi3 = 0;
    	}
        for (int i = 0; i < inputs.length; i++)
        {
            for (int j = 0; j < hiddenNeurons.length; j++)
            {
            	
                firstConnectionLayer[i][j] = (double) (firstConnectionLayer[i][j] + ki * (firstConnectionLayer[i][j] - ((double[][]) (last.firstConnectionLayer))[i][j]
                        + ( phi3) * (((double[][])(pBest.recessiveGenesLayer))[i][j] - recessiveGenesLayer[i][j])
                		+ (phi1- phi3/2.0) * (((double[][]) (pBest.firstConnectionLayer))[i][j] - firstConnectionLayer[i][j])
                        + (phi2 - phi3/2.0) * (((double[][]) (gBest.firstConnectionLayer))[i][j] - firstConnectionLayer[i][j])));
            }
        }
        if(recessiveGenesSlot == 2&& pBest.recessiveGenesSlot == 2){
    		phi3 = t;
    	}
    	else{
    		phi3 = 0;
    	}
        for (int i = 0; i < hiddenNeurons.length; i++)
        {
            for (int j = 0; j < outputs.length; j++)
            {
            	recessiveGenesLayer[i][j] = (double) (recessiveGenesLayer[i][j] + ki * (recessiveGenesLayer[i][j] - ((double[][]) (last.recessiveGenesLayer))[i][j]
                        + phi1 * (((double[][]) (pBest.recessiveGenesLayer))[i][j] - recessiveGenesLayer[i][j])
                        + phi2 * (((double[][]) (gBest.recessiveGenesLayer))[i][j] - recessiveGenesLayer[i][j])));
            }
        }
        if(recessiveGenesSlot == 3 && pBest.recessiveGenesSlot == 3){
    		phi3 = t;
    	}
    	else{
    		phi3 = 0;
    	}
        for (int i = 0; i < hiddenNeurons.length; i++)
        {
            for (int j = 0; j < outputs.length; j++)
            {
                lastConnectionLayer[i][j] = (double) (lastConnectionLayer[i][j] + ki * (lastConnectionLayer[i][j] - ((double[][]) (last.lastConnectionLayer))[i][j]
                		+ (phi3) * (((double[][])(pBest.recessiveGenesLayer))[i][j] - recessiveGenesLayer[i][j])
                		+ (phi1- phi3/2.0) * (((double[][]) (pBest.lastConnectionLayer))[i][j] - lastConnectionLayer[i][j])
                        + (phi2 - phi3/2.0) * (((double[][]) (gBest.lastConnectionLayer))[i][j] - lastConnectionLayer[i][j])));
            }
        }

    }

    private void clear(double[] array)
    {
        for (int i = 0; i < array.length; i++)
        {
            array[i] = 0;
        }
    }

    public void reset()
    {
    }

    public double[] approximate(double[] doubles)
    {
        return propagate(doubles);
    }

    public double[] propagate(double[] inputIn)
    {
        if (inputs != inputIn)
        {
            System.arraycopy(inputIn, 0, this.inputs, 0, inputIn.length);
        }
        if (inputIn.length < inputs.length)
            System.out.println("MLP: NOTE: only " + inputIn.length + " inputs out of " + inputs.length + " are used in the network");
        propagateOneStep(inputs, hiddenNeurons, firstConnectionLayer);
        tanh(hiddenNeurons);
        propagateOneStep(hiddenNeurons, outputs, lastConnectionLayer);
        tanh(outputs);

        return outputs;

    }

    private void propagateOneStep(double[] fromLayer, double[] toLayer, double[][] connections)
    {
        clear(toLayer);
        for (int from = 0; from < fromLayer.length; from++)
        {
            for (int to = 0; to < toLayer.length; to++)
            {
                toLayer[to] += fromLayer[from] * connections[from][to];
                //System.out.println("From : " + from + " to: " + to + " :: " +toLayer[to] + "+=" +  fromLayer[from] + "*"+  connections[from][to]);
            }
        }
    }

    public double backPropagate(double[] targetOutputs)
    {
        // Calculate output error
        double[] outputError = new double[outputs.length];

        for (int i = 0; i < outputs.length; i++)
        {
            //System.out.println("Node : " + i);
            outputError[i] = dtanh(outputs[i]) * (targetOutputs[i] - outputs[i]);
            //System.out.println("Err: " + (targetOutputs[i] - outputs[i]) +  "=" + targetOutputs[i] +  "-" + outputs[i]);
            //System.out.println("dnet: " +  outputError[i] +  "=" + (dtanh(outputs[i])) +  "*" + (targetOutputs[i] - outputs[i]));

            if (Double.isNaN(outputError[i]))
            {
                System.out.println("Problem at output " + i);
                System.out.println(outputs[i] + " " + targetOutputs[i]);
                System.exit(0);
            }
        }

        // Calculate hidden layer error
        double[] hiddenError = new double[hiddenNeurons.length];

        for (int hidden = 0; hidden < hiddenNeurons.length; hidden++)
        {
            double contributionToOutputError = 0;
            // System.out.println("Hidden: " + hidden);
            for (int toOutput = 0; toOutput < outputs.length; toOutput++)
            {
                // System.out.println("Hidden " + hidden + ", toOutput" + toOutput);
                contributionToOutputError += lastConnectionLayer[hidden][toOutput] * outputError[toOutput];
                // System.out.println("Err tempSum: " + contributionToOutputError +  "=" +secondConnectionLayer[hidden][toOutput]  +  "*" +outputError[toOutput] );
            }
            hiddenError[hidden] = dtanh(hiddenNeurons[hidden]) * contributionToOutputError;
            //System.out.println("dnet: " + hiddenError[hidden] +  "=" +  dtanh(hiddenNeurons[hidden])+  "*" + contributionToOutputError);
        }

        ////////////////////////////////////////////////////////////////////////////
        //WEIGHT UPDATE
        ///////////////////////////////////////////////////////////////////////////
        // Update first weight layer
        for (int input = 0; input < inputs.length; input++)
        {
            for (int hidden = 0; hidden < hiddenNeurons.length; hidden++)
            {

                double saveAway = firstConnectionLayer[input][hidden];
                firstConnectionLayer[input][hidden] += learningRate * hiddenError[hidden] * inputs[input];

                if (Double.isNaN(firstConnectionLayer[input][hidden]))
                {
                    System.out.println("Late weight error! hiddenError " + hiddenError[hidden]
                            + " input " + inputs[input] + " was " + saveAway);
                }
            }
        }

        // Update second weight layer
        for (int hidden = 0; hidden < hiddenNeurons.length; hidden++)
        {

            for (int output = 0; output < outputs.length; output++)
            {

                double saveAway = lastConnectionLayer[hidden][output];
                lastConnectionLayer[hidden][output] += learningRate * outputError[output] * hiddenNeurons[hidden];

                if (Double.isNaN(lastConnectionLayer[hidden][output]))
                {
                    System.out.println("target: " + targetOutputs[output] + " outputs: " + outputs[output] + " error:" + outputError[output] + "\n" +
                            "hidden: " + hiddenNeurons[hidden] + "\nnew conn weight: " + lastConnectionLayer[hidden][output] + " was: " + saveAway + "\n");
                }
            }
        }

        double summedOutputError = 0.0;
        for (int k = 0; k < outputs.length; k++)
        {
            summedOutputError += Math.abs(targetOutputs[k] - outputs[k]);
        }
        summedOutputError /= outputs.length;

        // Return something sensible
        return summedOutputError;
    }


    private double sig(double val)
    {
        return 1.0d / (1.0d + Math.exp(-val));
    }

    private void tanh(double[] array)
    {
        for (int i = 0; i < array.length; i++)
        {
            array[i] = Math.tanh(array[i]);
            // for the sigmoid
            // array[i] = array[i];
            //array[i] = sig(array[i]);//array[i];//
        }
    }

    private double dtanh(double num)
    {
        //return 1;
        return (1 - (num * num));
        // for the sigmoid
        //final double val = sig(num);
        //return (val*(1-val));
    }

    private double sum()
    {
        double sum = 0;
        for (double[] aFirstConnectionLayer : firstConnectionLayer)
        {
            for (double anAFirstConnectionLayer : aFirstConnectionLayer)
            {
                sum += anAFirstConnectionLayer;
            }
        }
        for (double[] aSecondConnectionLayer : lastConnectionLayer)
        {
            for (double anASecondConnectionLayer : aSecondConnectionLayer)
            {
                sum += anASecondConnectionLayer;
            }
        }
        return sum;
    }

    public double getMutationMagnitude()
    {
        return mutationMagnitude;
    }

    public void setMutationMagnitude(double mutationMagnitude)
    {
        this.mutationMagnitude = mutationMagnitude;
    }

    public static void setInitParameters(double mean, double deviation)
    {
        System.out.println("PARAMETERS SET: " + mean + "  deviation: " + deviation);

        MLP.mean = mean;
        MLP.deviation = deviation;
    }

    public void println()
    {
        System.out.print("\n\n----------------------------------------------------" +
                "-----------------------------------\n");
        for (double[] aFirstConnectionLayer : firstConnectionLayer)
        {
            System.out.print("|");
            for (double anAFirstConnectionLayer : aFirstConnectionLayer)
            {
                System.out.print(" " + anAFirstConnectionLayer);
            }
            System.out.print(" |\n");
        }
        System.out.print("----------------------------------------------------" +
                "-----------------------------------\n");
        for (double[] aSecondConnectionLayer : lastConnectionLayer)
        {
            System.out.print("|");
            for (double anASecondConnectionLayer : aSecondConnectionLayer)
            {
                System.out.print(" " + anASecondConnectionLayer);
            }
            System.out.print(" |\n");
        }
        System.out.print("----------------------------------------------------" +
                "-----------------------------------\n");
    }

    public String toString()
    {
        int numberOfConnections = (firstConnectionLayer.length * firstConnectionLayer[0].length) +
                (lastConnectionLayer.length * lastConnectionLayer[0].length);
        return "Straight mlp, mean connection weight " + (sum() / numberOfConnections);
    }

    public void setLearningRate(double learningRate)
    {
        this.learningRate = learningRate;
    }

    public double[] getOutputs()
    {
        double[] outputsCopy = new double[outputs.length];
        System.arraycopy(outputs, 0, outputsCopy, 0, outputs.length);
        return outputsCopy;
    }

    public double[] getWeightsArray()
    {
        double[] weights = new double[inputs.length * hiddenNeurons.length + hiddenNeurons.length * outputs.length];

        int k = 0;
        for (int i = 0; i < inputs.length; i++)
        {
            for (int j = 0; j < hiddenNeurons.length; j++)
            {
                weights[k] = firstConnectionLayer[i][j];
                k++;
            }
        }
        for (int i = 0; i < hiddenNeurons.length; i++)
        {
            for (int j = 0; j < outputs.length; j++)
            {
                weights[k] = lastConnectionLayer[i][j];
                k++;
            }
        }
        return weights;
    }

    public void setWeightsArray(double[] weights)
    {
        int k = 0;

        for (int i = 0; i < inputs.length; i++)
        {
            for (int j = 0; j < hiddenNeurons.length; j++)
            {
                firstConnectionLayer[i][j] = weights[k];
                k++;
            }
        }
        for (int i = 0; i < hiddenNeurons.length; i++)
        {
            for (int j = 0; j < outputs.length; j++)
            {
            	lastConnectionLayer[i][j] = weights[k];
                k++;
            }
        }
    }

    public int getNumberOfInputs()
    {
        return inputs.length;
    }

    public void randomise()
    {
        randomise(firstConnectionLayer);
        randomise(lastConnectionLayer);
    }

    protected void randomise(double[][] layer)
    {
        for (int i = 0; i < layer.length; i++)
        {
            for (int j = 0; j < layer[i].length; j++)
            {
                layer[i][j] = (Math.random() * 4.0) - 2.0;
            }
        }
    }

    public double[] getArray()
    {
        return getWeightsArray();
    }

    public void setArray(double[] array)
    {
        setWeightsArray(array);
    }

}
