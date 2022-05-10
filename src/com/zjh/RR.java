package com.zjh;

import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Scanner;

/**
 * @author 张俊鸿
 * @description:
 * @since 2022-05-09 23:35
 */
public class RR {
    static Scanner cin = new Scanner(System.in);

    /** 进程控制块 */
    static class PCB implements Comparable<PCB>{
        int id; // 进程id
        int arriveTime; // 到达时间
        int runTime; // 运行时间
        int hasRanTime = 0; // 已经运行时间， 初始值为0

        int turnAroundTime; // 周转时间
        int waitTime; // 等待时间

        int responseTime;// 响应时间 == 首次运行时间-到达时间
        PCB(int id, int arriveTime, int runTime){
            this.id = id;
            this.arriveTime = arriveTime;
            this.runTime = runTime;
        }

        @Override
        public int compareTo(PCB o) { // 按照 到达时间 进入就绪队列
            return  this.arriveTime - o.arriveTime;
        }
    }

    static PCB[] pcbs; // 进程数组
    static int timeSlice; // 时间片
    /** 进程到达队列 */
    static Queue<PCB> queue = new PriorityQueue<>();

    /** 初始化 PCB 信息 */
    static void initPCB(){
        System.out.print("输入进程数： ");
        int num = cin.nextInt();
        pcbs = new PCB[num+1];
        System.out.println("输入到达时间， 运行时间");
        for(int i = 1; i <= num; i++) {
            System.out.print("进程" + i + ":");
            pcbs[i] = new PCB(i, cin.nextInt(), cin.nextInt());
            queue.offer(pcbs[i]);
        }
        System.out.print("输入时间片大小：");
        timeSlice = cin.nextInt();
    }

    /** 判断当前已经到达的进程， 并使之进入就绪队列 */
    static boolean judge(Queue<PCB> readyQueue, int currentTime){
        boolean flag = false; // 为 true 表示 有 到达的进程
        while (true){
            PCB pcb = queue.peek(); // 最先到达的进程
            if (pcb == null){ // 所有进程都已经进入了就绪队列
                break;
            }else if(pcb.arriveTime <= currentTime){ // 当前有进程到达
                PCB runPCB = queue.poll();
                readyQueue.offer(runPCB); // 进入就绪队列等待运行
                flag = true;
            }else { // 当前没有进程到达
                break;
            }
        }
        return flag;
    }

    /** 进程进入处理机运行, 如果进程运行完成返回 true*/
    static boolean processRun(PCB pcb, int currentTime){
        if(pcb.hasRanTime == 0){ // 进程首次运行时间
            pcb.responseTime = currentTime;
        }
        pcb.hasRanTime++; // 进入 处理机运行
        System.out.printf("  %d\n", pcb.id);
        if(pcb.hasRanTime == pcb.runTime){ // 进程已经结束
            pcb.turnAroundTime = currentTime+1 - pcb.arriveTime;  // 周转时间
            pcb.waitTime = pcb.turnAroundTime - pcb.runTime; // 等待时间
            pcb.responseTime -= pcb.arriveTime;
            return true;
        }else {
            return false;
        }
    }

    /** 处理机运行 */
    static void run() {
        int currentTime = 0; // 当前时间
        if(!queue.isEmpty()){
            currentTime = queue.peek().arriveTime;
        }
        /** 定义就绪队列 */
        Queue<PCB> readyQueue = new LinkedList<>();
        int timeSliceFlag = timeSlice; // 时间片标记 （假设 刚开始 是一个时间片的开始）
        PCB runPcb = null;
        System.out.println("now   正在运行的进程");
        while (true) {
            System.out.printf("%d\t ", currentTime);
            if(queue.isEmpty() && readyQueue.isEmpty() && runPcb== null){
                System.out.println("当前所有进程运行结束");
                break;
            }else{ // 进程进入 处理机运行
                /** 判断是否到一个时间片 */
                if(timeSliceFlag % timeSlice == 0){ // 一个时间片到
                    /** 判断当前已经到达的进程,并使之进入就绪队列 */
                    judge(readyQueue, currentTime);
                    if(runPcb != null){ // 处理机上还有进程
                        // 此时要下处理机， 进入就绪队列
                        readyQueue.offer(runPcb);
                        runPcb = null; // 此时处理机空闲
                    }
                    runPcb = readyQueue.poll(); // 出就绪队列，
                    if(runPcb == null){ // 就绪队列为空, 意味着此时处理机空闲，而且没有到达的进程
                        currentTime++; // 处理机等待
                        System.out.printf("  处理机空闲,\n");
                        continue; // 进入下一轮
                    }
                }
                /** 在 处理机中 运行 进程-->runPCB*/
                if(processRun(runPcb, currentTime) == true){ // 运行后 进程已经结束
                    runPcb = null;
                    timeSliceFlag = timeSlice; // 重新开始一个新的时间片
                }else { // 进程没有结束
                    timeSliceFlag++;
                }

                /** 时间片+1 */
                currentTime++;

            }
        }
    }

    public static void main(String[] args) {
        initPCB();
        System.out.println("-----处理机开始运行-----");
        run();
        System.out.println("-----处理机运行结束-----");
        showTurnAroundTime();
    }

    // 周转时间
    private static void showTurnAroundTime() {
        double averageT = 0;
        double averageWTAT = 0;
        double averageWT = 0;
        System.out.println("进程\t 周转时间\t 带权周转时间\t 等待时间\t 响应时间\t");
        for (int i = 1; i < pcbs.length; i ++) {
            int turnAroundTime = pcbs[i].turnAroundTime;
            double weightTurnAroundTime = turnAroundTime*1.0/pcbs[i].runTime;
            int waitTime = pcbs[i].waitTime;
            int responseTime = pcbs[i].responseTime;
            System.out.printf("%d\t     %d\t\t\t  %.2f\t\t\t %d\t\t\t %d\n" ,i , turnAroundTime,  weightTurnAroundTime, waitTime, responseTime);
            averageT += turnAroundTime;
            averageWTAT += weightTurnAroundTime;
            averageWT += waitTime;
        }
        averageT /= pcbs.length-1;
        averageWTAT /= pcbs.length-1;
        averageWT /= pcbs.length-1;
        System.out.println("平均周转时间：" + averageT);
        System.out.printf("平均带权周转时间：%.2f\n", averageWTAT);
        System.out.println("平均等待时间：" + averageWT);
    }
}


