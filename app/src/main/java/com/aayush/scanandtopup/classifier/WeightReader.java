package com.aayush.scanandtopup.classifier;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by aviisekh on 7/29/16.
 */
public class WeightReader {
    double [][] weightsArr;  //Contains weights and bias array

    public double [][] getWeights(String jsonContent, String weights) {

        try {
            JSONObject jsonRootObject = new JSONObject(jsonContent);
            JSONArray jsonWeightArray = jsonRootObject.optJSONArray(weights);

            int rows = jsonWeightArray.length();
            int columns = jsonWeightArray.getJSONArray(0).length();

            weightsArr = new double[rows][columns];
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < columns; j++) {
                    weightsArr[i][j] = jsonWeightArray.getJSONArray(i).getDouble(j);

                }
            }

        } catch (JSONException e1) {
            e1.printStackTrace();

        }

        return weightsArr;


    }

}




