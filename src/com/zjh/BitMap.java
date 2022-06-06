package com.zjh;

import java.util.ArrayList;
import java.util.Scanner;

/**
 * @author 张俊鸿
 * @description: 位示图
 * @since 2022-06-05 17:13
 */
public class BitMap {
    private static int CYLINDER = 4;  //4个柱面
    private static int TRACK = 2000;  //2000个磁道
    private static int SECTION = 8;  //8个扇区
    private static int BLOCK_SIZE = 4; //块大小
    private static ArrayList<block> block_list = new ArrayList<>(); //记录已分配磁盘块
    int[][] map;//位示图

    public BitMap() {
        //初始化位示图  一行2000列 总共4*8行

    }

    /**
     * 磁盘块
     *
     * @author 张俊鸿
     * @date 2022/06/05
     */
    public class block {
        int index;   //物理块号
        String fileName; //存放文件名
        public block(int index, String fileName) {
            this.index = index;
            this.fileName = fileName;
        }
    }

    public static void main(String[] args) {
        BitMap bitMap = new BitMap();
        int i = 0;
        do {
            System.out.println("=========位示图==========");
            System.out.println("请输入操作序号：");
            System.out.println("1.打印位示图");
            System.out.println("2.打印存储信息");
            System.out.println("3.申请磁盘块");
            System.out.println("4.回收磁盘块");
            System.out.println("5.退出");
            System.out.println("========================");
            Scanner scanner = new Scanner(System.in);
            i = scanner.nextInt();
            switch (i) {
                case 1:

                    break;
                case 2:

                    break;
                case 3:

                    break;
                case 4:

                    break;
                case 5:

                    break;
            }
        } while (i != 5);
    }
}
