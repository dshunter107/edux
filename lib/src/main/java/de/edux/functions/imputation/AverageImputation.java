package de.edux.functions.imputation;

import java.util.Arrays;
import java.util.List;

public class AverageImputation implements IImputationStrategy {
  @Override
  public String[] performImputation(String[] datasetColumn) {
    checkIfColumnContainsCategoricalValues(datasetColumn);

    String[] updatedDatasetColumn = new String[datasetColumn.length];
    double average = calculateAverage(datasetColumn);

    for (int index = 0; index < datasetColumn.length; index++) {
      if (datasetColumn[index].isBlank()) {
        updatedDatasetColumn[index] = String.valueOf(average);

      } else {
        updatedDatasetColumn[index] = datasetColumn[index];
      }
    }

    return updatedDatasetColumn;
  }

  private void checkIfColumnContainsCategoricalValues(String[] datasetColumn) {
    for (String value : datasetColumn) {
      if (!isNumeric(value)) {
        throw new RuntimeException(
            "AVERAGE imputation strategy can not be used on categorical features. "
                + "Use MODE imputation strategy or perform a list wise deletion on the features.");
      }
    }
  }

  private boolean isNumeric(String value) {
    return value.matches("-?\\d+(\\.\\d+)?") || value.isBlank();
  }

  private double calculateAverage(String[] datasetColumn) {
    List<String> filteredDatasetColumn =
        Arrays.stream(datasetColumn).filter((value) -> !value.isBlank()).toList();
    int validValueCount = filteredDatasetColumn.size();
    double sumOfValidValues =
        filteredDatasetColumn.stream().map(Double::parseDouble).reduce(0.0, Double::sum);
    return sumOfValidValues / validValueCount;
  }
}
