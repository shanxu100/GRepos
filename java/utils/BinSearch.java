package utils;

/**
 * @author Guan
 * @date Created on 2018/3/23
 */
public class BinSearch {
    public static int n = 8;
    public static int[] data = new int[n];

    public static void main(String[] args) {
        for (int i = 0; i < data.length; i++) {
            data[i] = i * i;
        }
//        data[9] = 10;
//        data[4] = 3;
//        data[5] = 3;
        for (int i : data) {
            System.out.print(i + " ");
        }

        System.out.println();
//        System.out.println(BinarySearchLast(data, 3));
//        System.out.println(BinarySearchFirst(data, 3));
        System.out.println(BinarySearchHand(data, 15));

    }

    /**
     * 当有多个元素值与目标元素相等时，返回<P>最后一个</P>元素的下标
     *
     * @param data
     * @param target
     * @return
     */
    public static int BinarySearchLast(int[] data, int target) {
        int left = 0;
        int right = data.length - 1;

        while (left < right) {
            int mid = (left + right + 1) / 2;
            if (data[mid] <= target) {
                left = mid;
            } else {
                right = mid - 1;
            }
        }
        //此时，left==right
        if (data[right] == target) {
            return right;
        }
        return -1;
    }


    /**
     * 当有多个元素值与目标元素相等时，返回 <P>第一个</P>元素的下标
     *
     * @param data
     * @param target
     * @return
     */
    public static int BinarySearchFirst(int[] data, int target) {
        int left = 0;
        int right = data.length - 1;

        while (left < right) {
            int mid = (left + right) / 2;
            if (data[mid] < target) {
                left = mid + 1;
            } else {
                right = mid;
            }
        }
        //此时，left==right
        if (data[left] == target) {
            return left;
        }
        return -1;
    }


    /**
     * 只有一个元素和目标相等，返回这个元素元素的下标
     * 否则返回比target大的最小元素，（或者比target小的最大元素）
     *
     * @param data
     * @param target
     * @return
     */
    public static int BinarySearchHand(int[] data, int target) {
        int left = 0;
        int right = data.length - 1;
        while (left <= right) {
            int mid = (left + right) / 2;
            if (data[mid] < target) {
                left = mid + 1;
            } else if (data[mid] > target) {
                right = mid - 1;
            } else {
                return mid;
            }
        }

        //并没有一个元素和target相等。此时left=right+1
        //说明target在data[right]和data[left]中间，
        //即：data[right] < target < data[left]
        //所以根据业务，返回 right 或者 left 即可
        return -1;
    }

}
