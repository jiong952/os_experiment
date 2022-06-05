package com.zjh.extra;

import java.util.*;

/**
 * @author 张俊鸿
 * @description: 磁盘调度算法 -- 最短优先寻道
 * 优先选择距当前磁头最近的访问请求进行服务
 * @since 2022-06-05 15:17
 */
public class SSTF {
    private static Scanner scanner = new Scanner(System.in);
    private static Set<Integer> diskSet = new HashSet<>(); //磁道序列
    private static int beginIndex; //起始位置
    private static int cur; //当前位置
    private static Queue<Integer> visitQueue = new LinkedList<>(); //访问序列
    private static int totalVisitNum = 0;  //总共经过的磁道数

    /**
     * 显示结果
     */
    public void show(){
        System.out.print("访问序列：");
        for (Integer integer : visitQueue) {
            System.out.print(integer+" ");
        }
        System.out.println();
        System.out.println("经过的磁道总数：" + totalVisitNum);
        System.out.println("平均寻道长度：" + totalVisitNum * 1.0 / visitQueue.size());
    }
    /**
     * 寻找访问队列
     */
    public void run(){
        while (diskSet.size() > 0){
            //从当前位置开始 寻找其他距离最近的磁道进行访问
            if(diskSet.size() == 0){
                return;
            }
            int next = 0;
            int length = Integer.MAX_VALUE;
            //遍历set中元素，找到最近的
            for (Integer disk : diskSet) {
                if(Math.abs(disk - cur) < length){
                    next = disk;
                    length = Math.abs(disk - cur);
                }
            }
            cur = next;
            diskSet.remove(cur);
            visitQueue.offer(cur);
            totalVisitNum += length;
        }
    }

    /**
     * 输入磁道序列及起始位置
     */
    public void input(){
        System.out.println("请输入磁盘请求序列长度：");
        int num = scanner.nextInt();
        System.out.println("请依次输入磁盘请求序列（空格分开）：");
        for(int i = 0;i < num; i++){
            diskSet.add(scanner.nextInt());
        }
        System.out.println("请输入读写头起始位置：");
        beginIndex = scanner.nextInt();
        cur = beginIndex;
    }
    public static void main(String[] args) {
        SSTF sstf = new SSTF();
        sstf.input();
        sstf.run();
        sstf.show();
    }
}
