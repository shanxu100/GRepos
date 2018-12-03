package utils;

/**
 * @author Guan
 * @date Created on 2018/8/10
 */
public class InsertSort {

    public static void InsertSort(int[] data) {
        int i, j;
        int target;
        for (i = 1; i < data.length; i++) {
            j = i - 1;
            target = data[i];
            while (j >= 0 && data[j] > target) {
                data[j + 1] = data[j];
                j--;
            }
            //
            data[j + 1] = target;
        }
    }
}
