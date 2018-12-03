package utils;

import java.util.Scanner;

/**
 * @author Guan
 * @date Created on 2018/4/5
 */
public class GCDLCM {
    /**
     * 最大公约数
     *
     * @param a
     * @param b
     * @return
     */
    public static int get_gcd(int a, int b) {
        int max, min;
        max = (a > b) ? a : b;
        min = (a < b) ? a : b;

        if (max % min != 0) {
            return get_gcd(min, max % min);
        } else {
            return min;
        }

    }

    /**
     * 最小公倍数
     *
     * @param a
     * @param b
     * @return
     */
    public static int get_lcm(int a, int b) {
        return a * b / get_gcd(a, b);
    }

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        int n1 = input.nextInt();
        int n2 = input.nextInt();
        System.out.println("(" + n1 + "," + n2 + ")" + "=" + get_gcd(n1, n2));
        System.out.println("[" + n1 + "," + n2 + "]" + "=" + get_lcm(n1, n2));

    }

}
