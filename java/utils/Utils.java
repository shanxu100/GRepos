package utils;

/**
 * @author Guan
 * @date Created on 2018/8/12
 */
public class Utils {

    /**
     * 一次遍历，找到数组中最大的差值
     * 规定：值小的 在前，值大的 在后.
     * 就像股票，低价买入在前，高价卖出在后
     *
     * @param prices
     * @param l
     * @param r
     * @return
     */
    public static int getDiffValue(int[] prices, int l, int r) {
        if (l >= r) {
            return 0;
        }
        int value = 0;
        int min = Integer.MAX_VALUE;
        for (int i = l; i <= r; i++) {
            if (prices[i] <= min) {
                min = prices[i];
            } else {
                value = value > (prices[i] - min) ? value : (prices[i] - min);
            }
        }
        return value;
    }

}
