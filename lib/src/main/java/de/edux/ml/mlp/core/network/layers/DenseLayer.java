package de.edux.ml.mlp.core.network.layers;

import de.edux.ml.mlp.core.network.Layer;
import de.edux.ml.mlp.core.tensor.Matrix;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;

public class DenseLayer implements Layer {
  private final Random random = new Random();
  private AtomicReference<Matrix> weights;
  private AtomicReference<Matrix> bias;
  private Matrix lastInput;

  public DenseLayer(int inputSize, int outputSize) {
    weights = new AtomicReference<>(new Matrix(outputSize, inputSize));
    bias = new AtomicReference<>(new Matrix(outputSize, 1));
    initialize();
  }

  private void initialize() {
    double standartDeviation = Math.sqrt(2.0 / (weights.get().getRows() + weights.get().getCols()));

    for (int i = 0; i < weights.get().getRows(); i++) {
      for (int j = 0; j < weights.get().getCols(); j++) {
        weights.get().set(i, j, random.nextGaussian() * standartDeviation);
      }
    }
    for (int i = 0; i < bias.get().getRows(); i++) {
      for (int j = 0; j < bias.get().getCols(); j++) {
        bias.get().set(i, j, 0);
      }
    }
  }

  @Override
  public Matrix forwardLayerbased(Matrix input) {
    this.lastInput = input;
    return this.weights.get().multiplyParallel(input).add(this.bias.get());
  }

  @Override
  public synchronized void updateWeightsAndBias() {}

  @Override
  public Matrix backwardLayerBased(Matrix error, float learningRate) {
    Matrix output = weights.get().transposeParallel().multiplyParallel(error);
    // Calculate gradient of weights
    Matrix weightsGradient = error.multiplyParallel(lastInput.transposeParallel());
    // Calculate gradient of bias
    Matrix biasGradient = error.averageColumn();
    // Calculate learning rate per weight
    float rate = learningRate / (lastInput.getCols());

    // Update weights and bias
    weights.set(weights.get().subtract(weightsGradient.multiplyParallel(rate)));
    bias.set(bias.get().subtract(biasGradient.multiplyParallel(rate)));

    return output;
  }

  @Override
  public String toString() {
    return "Dense in: " + weights.get().getCols() + " x out: " + weights.get().getRows();
  }
}
