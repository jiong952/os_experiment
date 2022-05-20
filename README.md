# 广东工业大学操作系统实验

使用java语言实现

![image-20220520090643660](https://myblogimgbed.oss-cn-shenzhen.aliyuncs.com/img/202205200906890.png)

## 进程调度算法

#### 时间片轮转法



要求：时间片轮转算法，

```java
package com.zjh.processScheduling;

import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Scanner;

/**
 * @author 张俊鸿
 * @description: 时间片轮转算法
 * @since 2022-05-09 19:20
 */
public class RR {
    static Scanner scanner = new Scanner(System.in);

    /**
     * 进程控制块PCB
     */
    static class PCB implements Comparable<PCB>{
        String name; // 进程名
        int arriveTime; // 到达时间
        int serveTime; // 运行时间
        int hasRunTime = 0; // 已经运行时间
        int beginTime; // 开始时间
        int turnRoundTime; //周转时间
        @Override
        public int compareTo(PCB o) { // 按照 到达时间 进入就绪队列
            return  this.arriveTime - o.arriveTime;
        }

        public PCB(String name, int arriveTime, int serveTime) {
            this.name = name;
            this.arriveTime = arriveTime;
            this.serveTime = serveTime;
        }
    }
    static PCB[] pcbArrays; // 进程数组
    static int timeSlice; //时间片
    //使用优先队列将输入的进程自动按照到达时间排序,模拟按照时间到达
    static Queue<PCB> pcb_queue = new PriorityQueue<>();

    /**
     * 输入进程信息
     */
    public static void input(){
        System.out.print("请输入进程数：");
        int pcb_num = scanner.nextInt();
        pcbArrays = new PCB[pcb_num];
        System.out.print("请输入时间片大小：");
        timeSlice = scanner.nextInt();
        for (int i = 0; i < pcb_num; i++) {
            System.out.println("进程号"+ (i+1));
            System.out.print("请输入进程名：");
            String name = scanner.next();
            System.out.print("请输入到达时间：");
            int arriveTime = scanner.nextInt();
            System.out.print("请输入服务时间：");
            int serveTime = scanner.nextInt();
            PCB pcb = new PCB(name, arriveTime, serveTime);
            //放进数组
            pcbArrays[i] = pcb;
            //加入pcb队列
            pcb_queue.offer(pcb);
        }
    }

    /**
     * 检查是否有到达的进程，有就加入就绪队列中
     *
     * @param readyQueue 就绪队列
     * @param cpuTime    cpu当前的时间
     */
    public static void check(Queue<PCB> readyQueue, int cpuTime){
        while (true){
            //查看到达的进程，直到有进程没到达就退出
            PCB pcb_first = pcb_queue.peek();
            if(pcb_first == null){
                //全部加入就绪队列了
                break;
            }else if(pcb_first.arriveTime <= cpuTime){//判断是否到达
                //已就绪，弹出排队队列，加入就绪队列
                readyQueue.offer(pcb_queue.poll());
            }else {
                //没有就绪的进程
                break;
            }
        }
    }

    public static void printCurrent(PCB pcb,int cpuTime){
        System.out.println("当前时间:"+cpuTime+"\t运行的进程:"+pcb.name+"\t剩余运行时间:"+(pcb.serveTime-pcb.hasRunTime));
    }

    /**
     * 一个时间片内进程运行
     *
     * @param pcb     进程控制块
     * @param cpuTime cpu时间
     * @return boolean 是否提前退出
     */
    static boolean pcbRun(PCB pcb,int cpuTime){
        if(pcb.hasRunTime == 0){
            //首次响应时间
            pcb.beginTime = cpuTime;
        }
        printCurrent(pcb,cpuTime);
        pcb.hasRunTime++;
        if(pcb.hasRunTime == pcb.serveTime){
            //进程结束
            pcb.turnRoundTime = cpuTime + 1 - pcb.arriveTime;
            return true;
        }
        return false;
    }

    /**
     * cpu开始运行
     */
    public static void run(){
        int cpuTime = 0;
        //cpu开始工作时间是第一个到达的时间
        if(!pcb_queue.isEmpty()){
            cpuTime = pcb_queue.peek().arriveTime;
        }
        Queue<PCB> readyQueue = new LinkedList<>(); //就绪队列
        int timeSliceFlag = timeSlice;
        PCB runningPcb = null;
        while (true){
            System.out.println();
            //判断是否结束
            if(pcb_queue.isEmpty() && readyQueue.isEmpty() && runningPcb== null){
                System.out.println("======所有进程运行结束======");
                break;
            }else {
                //处理进程
                if(timeSliceFlag % timeSlice == 0){
                    //一个时间片结束
                    check(readyQueue,cpuTime);
                    if(runningPcb != null){
                        readyQueue.offer(runningPcb);
                        runningPcb = null;
                    }
                    //取出要运行的进程
                    runningPcb = readyQueue.poll();
                    if(runningPcb == null){
                        //当前没有可以运行的pcb
                        cpuTime++;
                        continue;
                    }
                }
                //时间片没到，判断是否有进程提前结束
                if(pcbRun(runningPcb, cpuTime)){
                    runningPcb = null;
                    timeSliceFlag = timeSlice;
                }else {
                    timeSliceFlag++;
                }
                cpuTime++;
            }
        }
    }
    // 周转时间
    private static void showTurnAroundTime() {
        double averageT = 0;
        double averageWTAT = 0;
        System.out.println("进程\t 周转时间\t 带权周转时间\t 响应时间\t");
        for (int i = 0; i < pcbArrays.length; i ++) {
            int turnAroundTime = pcbArrays[i].turnRoundTime;
            double weightTurnAroundTime = turnAroundTime*1.0/pcbArrays[i].serveTime;
            int beginTime = pcbArrays[i].beginTime;
            System.out.printf(i+"\t   "+turnAroundTime+"\t\t   ");
            System.out.printf("%.1f", weightTurnAroundTime);
            System.out.print("\t\t  "+beginTime+"\n");
            averageT += turnAroundTime;
            averageWTAT += weightTurnAroundTime;
        }
        averageT /= pcbArrays.length;
        averageWTAT /= pcbArrays.length;
        System.out.println("平均周转时间：" + averageT);
        System.out.printf("平均带权周转时间：%.2f\n", averageWTAT);
    }
    public static void main(String[] args) {
        input();
        System.out.println("======时间片轮转调度算法======");
        run();
        showTurnAroundTime();
    }
}

```



结果

```
"C:\Program Files\Java\jdk-13.0.2\bin\java.exe" "-javaagent:D:\Idea\IntelliJ IDEA 2021.1.1\lib\idea_rt.jar=7622:D:\Idea\IntelliJ IDEA 2021.1.1\bin" -Dfile.encoding=UTF-8 -classpath D:\java项目\os_experiment\out\production\os_experiment com.zjh.processScheduling.RR
请输入进程数：5
请输入时间片大小：2
进程号1
请输入进程名：A
请输入到达时间：0
请输入服务时间：3
进程号2
请输入进程名：B
请输入到达时间：1
请输入服务时间：6
进程号3
请输入进程名：C
请输入到达时间：2
请输入服务时间：4
进程号4
请输入进程名：D
请输入到达时间：3
请输入服务时间：9
进程号5
请输入进程名：E
请输入到达时间：4
请输入服务时间：1
======时间片轮转调度算法======

当前时间:0	运行的进程:A	剩余运行时间:3

当前时间:1	运行的进程:A	剩余运行时间:2

当前时间:2	运行的进程:B	剩余运行时间:6

当前时间:3	运行的进程:B	剩余运行时间:5

当前时间:4	运行的进程:C	剩余运行时间:4

当前时间:5	运行的进程:C	剩余运行时间:3

当前时间:6	运行的进程:A	剩余运行时间:1

当前时间:7	运行的进程:D	剩余运行时间:9

当前时间:8	运行的进程:D	剩余运行时间:8

当前时间:9	运行的进程:E	剩余运行时间:1

当前时间:10	运行的进程:B	剩余运行时间:4

当前时间:11	运行的进程:B	剩余运行时间:3

当前时间:12	运行的进程:C	剩余运行时间:2

当前时间:13	运行的进程:C	剩余运行时间:1

当前时间:14	运行的进程:D	剩余运行时间:7

当前时间:15	运行的进程:D	剩余运行时间:6

当前时间:16	运行的进程:B	剩余运行时间:2

当前时间:17	运行的进程:B	剩余运行时间:1

当前时间:18	运行的进程:D	剩余运行时间:5

当前时间:19	运行的进程:D	剩余运行时间:4

当前时间:20	运行的进程:D	剩余运行时间:3

当前时间:21	运行的进程:D	剩余运行时间:2

当前时间:22	运行的进程:D	剩余运行时间:1

======所有进程运行结束======
进程	 周转时间	 带权周转时间	 响应时间	
0	   7		   2.3		  0
1	   17		   2.8		  2
2	   12		   3.0		  4
3	   20		   2.2		  7
4	   6		   6.0		  9
平均周转时间：12.4
平均带权周转时间：3.28

Process finished with exit code 0

```



请输出进程的调度过程，以及系统平均带权周转时间。

input队列加就绪队列

run方法每次判断时间片是否结束，结束就手动开启一个新时间片，取出下一个runningPcb进行运行

#### 短作业优先算法



#### 高响应比调度算法

增加一个优先级属性 = 到达时间（等待时间）+ 需要服务时间 / 需要服务时间

一个输入input队列 使用优先队列对到达时间进行排序，这样input就自动排序好了

一个就绪列表 有到的就加入

run方法

遍历队列，拿出优先级最高的运行到结束





##### FF

```
"C:\Program Files\Java\jdk-13.0.2\bin\java.exe" "-javaagent:D:\Idea\IntelliJ IDEA 2021.1.1\lib\idea_rt.jar=6868:D:\Idea\IntelliJ IDEA 2021.1.1\bin" -Dfile.encoding=UTF-8 -classpath D:\java项目\os_experiment\out\production\os_experiment com.zjh.dynamicPartition.FirstFit
=========首次适应算法============
请输入初始内存总大小
640
请输入要分配内存还是要释放内存
1 分配内存 2 释放内存
1
请输入要分配的内存大小
130
分区编号	分区始址	分区大小	空闲状态	
0		0		130  	占用
1		130		510  	空闲
请输入要分配内存还是要释放内存
1 分配内存 2 释放内存
1
请输入要分配的内存大小
60
分区编号	分区始址	分区大小	空闲状态	
0		0		130  	占用
1		130		60  	占用
2		190		450  	空闲
请输入要分配内存还是要释放内存
1 分配内存 2 释放内存
1
请输入要分配的内存大小
100
分区编号	分区始址	分区大小	空闲状态	
0		0		130  	占用
1		130		60  	占用
2		190		100  	占用
3		290		350  	空闲
请输入要分配内存还是要释放内存
1 分配内存 2 释放内存
2
输入想要释放内存的分区号
1
成功回收60大小的空间
分区编号	分区始址	分区大小	空闲状态	
0		0		130  	占用
1		130		60  	空闲
2		190		100  	占用
3		290		350  	空闲
请输入要分配内存还是要释放内存
1 分配内存 2 释放内存
1
请输入要分配的内存大小
200
分区编号	分区始址	分区大小	空闲状态	
0		0		130  	占用
1		130		60  	空闲
2		190		100  	占用
3		290		200  	占用
4		490		150  	空闲
请输入要分配内存还是要释放内存
1 分配内存 2 释放内存
2
输入想要释放内存的分区号
2
成功回收100大小的空间
分区编号	分区始址	分区大小	空闲状态	
0		0		130  	占用
1		130		160  	空闲
2		290		200  	占用
3		490		150  	空闲
请输入要分配内存还是要释放内存
1 分配内存 2 释放内存
2
输入想要释放内存的分区号
0
成功回收290大小的空间
分区编号	分区始址	分区大小	空闲状态	
0		0		290  	空闲
1		290		200  	占用
2		490		150  	空闲
请输入要分配内存还是要释放内存
1 分配内存 2 释放内存
1
请输入要分配的内存大小
140
分区编号	分区始址	分区大小	空闲状态	
0		0		140  	占用
1		140		150  	空闲
2		290		200  	占用
3		490		150  	空闲
请输入要分配内存还是要释放内存
1 分配内存 2 释放内存
1
请输入要分配的内存大小
60
分区编号	分区始址	分区大小	空闲状态	
0		0		140  	占用
1		140		60  	占用
2		200		90  	空闲
3		290		200  	占用
4		490		150  	空闲
请输入要分配内存还是要释放内存
1 分配内存 2 释放内存
1
请输入要分配的内存大小
50
分区编号	分区始址	分区大小	空闲状态	
0		0		140  	占用
1		140		60  	占用
2		200		50  	占用
3		250		40  	空闲
4		290		200  	占用
5		490		150  	空闲
请输入要分配内存还是要释放内存
1 分配内存 2 释放内存
1
请输入要分配的内存大小
60
分区编号	分区始址	分区大小	空闲状态	
0		0		140  	占用
1		140		60  	占用
2		200		50  	占用
3		250		40  	空闲
4		290		200  	占用
5		490		60  	占用
6		550		90  	空闲
请输入要分配内存还是要释放内存
1 分配内存 2 释放内存

```

