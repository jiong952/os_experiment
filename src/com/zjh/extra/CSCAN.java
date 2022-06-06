package com.zjh.extra;

import java.util.*;

/**
 * @author 张俊鸿
 * @description: 磁盘调度算法 -- 循环扫描算法
 * @since 2022-06-06 17:21
 */
public class CSCAN {
    private static Scanner scanner = new Scanner(System.in);
    private static Set<Integer> diskSet = new HashSet<>(); //磁道序列
    private static int beginIndex; //起始位置
    private static int cur; //当前位置
    private static Queue<Integer> visitQueue = new LinkedList<>(); //访问序列
    private static int totalVisitNum = 0;  //总共经过的磁道数
    private static int direction; //访问方向 1往磁道号增加方向 0往磁道数减少方向
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
        boolean flag = false;
        while (diskSet.size() > 0){
            //从当前位置开始 寻找其他距离最近的磁道进行访问
            if(diskSet.size() == 0){
                return;
            }
            int next = -1;
            int length = Integer.MAX_VALUE;
            if(flag){
                //该方向剩余没有了 循环
                //找距离最远的
                int max_length = 0;
                int max_index = 0;
                for (Integer disk : diskSet) {
                    if(Math.abs(disk - cur) > max_length){
                        max_index = disk;
                        max_length = Math.abs(disk - cur);
                    }
                }
                cur = max_index;
                diskSet.remove(cur);
                visitQueue.offer(cur);
                totalVisitNum += max_length;
                flag  = false;
                continue;
            }
            //遍历set中元素，找到该方向最近的
            if(direction == 1 && !flag){
                for (Integer disk : diskSet) {
                    if(disk > cur){ //增加方向
                        if(Math.abs(disk - cur) < length){
                            next = disk;
                            length = Math.abs(disk - cur);
                        }
                    }
                }
                if(next == -1){
                    flag = true;
                    continue;
                }
            }
            if(direction == 0 && !flag){
                for (Integer disk : diskSet) {
                    if(disk < cur){ //减少方向
                        if(Math.abs(disk - cur) < length){
                            next = disk;
                            length = Math.abs(disk - cur);
                        }
                    }
                }
                if(next == -1){
                    flag = true;
                    continue;
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
        System.out.println("请输入访问方向（1往磁道号增加方向 0往磁道数减少方向）：");
        direction = scanner.nextInt();
    }

    public static void main(String[] args) {
        CSCAN cscan = new CSCAN();
        cscan.input();
        cscan.run();
        cscan.show();
    }
}
