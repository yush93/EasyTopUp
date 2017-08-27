package com.aayush.scanandtopup.segmentationModule;

import com.aayush.scanandtopup.primaryGUIModule.RechargeActivity;

import java.util.ArrayList;

public class CcLabeling {

    int neighbourIndex;
    ArrayList<Integer> temp;
    ArrayList<ArrayList<Integer>> cc = new ArrayList<>();

    public int[][][] CcLabels(boolean[] main, int width) {
        int direction[] =
                {-width - 1,
                        -width,
                        -width + 1,
                        -1,
                        +1,
                        width - 1,
                        width,
                        width + 1
                };
        int length = main.length;
        for (int i = 0; i < length; i++) {
            if (main[i]) {
                temp = new ArrayList<>();
                temp.add(i);
                for (int u = 0; u < temp.size(); u++) {
                    int index = temp.get(u);
                    for (int x = 0; x < 8; x++) {
                        neighbourIndex = index + direction[x];
                        if (main[neighbourIndex]) {
                            temp.add(neighbourIndex);
                            main[neighbourIndex] = false;
                        }
                    }
                    //change pixel at index to false
                    main[index] = false;
                }
                cc.add(temp);
            }
        }
        int[][][] ccxy = new int[cc.size()][][];
        int x;
        int y;
        for (int component = 0; component < cc.size(); component++) {
            ccxy[component] = new int[cc.get(component).size()][2];
            for (int pixelIndex = 0; pixelIndex < cc.get(component).size(); pixelIndex++) {
                y = Math.round(cc.get(component).get(pixelIndex) / width);
                x = cc.get(component).get(pixelIndex) - y * width;
                ccxy[component][pixelIndex][0] = x;
                ccxy[component][pixelIndex][1] = y;
            }
        }
        return ccxy;
    }
}
