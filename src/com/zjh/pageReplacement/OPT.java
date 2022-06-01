package com.zjh.pageReplacement;

import java.util.*;

/**
 * @author 张俊鸿
 * @description: 最佳置换算法
 * @since 2022-06-01 16:15
 */
public class OPT {
    private static int blockNum = 4; //内存块数
    private static int pageSize = 10;  //页面存放指令的数目
    private static int instrNum = 320;  //作业的指令数目
    private static int pageNum ;  //页面数目 = instructionNum / PageSize
    private static int missingPageNum; //缺页数
    private static double missingPageRate; //缺页率 = MissingPageNum / instructionNum
    private Queue<Integer> instrQue; //访问指令序列 值指令地址
    private Map<Integer,Integer> blockMap; //存放内存块 key是内存块号 value是页面号
    private Scanner scanner;


    public static void main(String[] args) {
        //初始化
        pageNum = instrNum / pageSize;
        for (int i = 0; i < 100; i++) {
            Queue<Integer> instrQue = new OPT().getInstrQue();
            System.out.println("======结束======");
            System.out.println(instrQue);
        }
    }
    /**
     * 按照要求生成指令流序列
     * ① 在[0，319]的指令地址之间随机选取一起点m；
     * ② 顺序执行一条指令，即执行序号为m+1的指令；
     * ③ 在前地址[0，m-1]中随机选取一条指令并执行，该指令的序号为m1；
     * ④ 顺序执行一条指令，其序号为m1+1的指令；
     * ⑤ 在后地址[m1+2，319]中随机选取一条指令并执行,该指令的序号为m2；
     * ⑥ 顺序执行一条指令，其序号为m2+1的指令；
     *        重复上述步骤①～⑥，直到执行320次指令。
     *
     * @return {@link Queue}<{@link Integer}>
     */
    public Queue<Integer> getInstrQue(){
        Random random = new Random();
        Queue<Integer> queue = new LinkedList<>();
        Set<Integer> set = new TreeSet<>();
        //初始化全部指令
        for (int i = 0; i < instrNum; i++) {
            set.add(i);
        }
        while (queue.size() != instrNum){
            int m,m1;
            while (true){
                m = random.nextInt(instrNum-1); // m 0-319
                //1.判断m+1是否在set中，在则重新生成m，不在则放m+1入set
                if(m == 0) break;
                if(!set.contains(m+1)){
                    boolean index = true;
                    //判断set是否有剩余
                    for (int i = 0; i <= (instrNum-1); i++) {
                        if(set.contains(i)) index = false;
                    }
                    if(index) break;
                    continue;
                }else {
                    set.remove(m+1);
                    queue.offer(m+1);
//                    System.out.println("[m]:"+m);
//                    System.out.println("m+1:"+ (m+1));
//                    System.out.println("set:"+set);
                    break;
                }
            }
            //2.[0，m-1] 随机一个m1 判断是否在set中，在则重新生成m1
            while (true){
                if(m-1 > 0){
                    m1 = random.nextInt(m);
                }else {
                    m1 = 0;
                }
                if(!set.contains(m1)){
                    boolean index = true;
                    //遍历判断是否[0，m-1]已经分配完
                    for (int i = 0; i < m; i++) {
                        if(set.contains(i)) index = false;
                    }
                    if(index) {
//                        System.out.println("退出m1");
                        break;
                    }
                    continue;
                }else {
                    //3.不在则放m1 m1+1
                    set.remove(m1);
                    queue.offer(m1);
//                    System.out.println("m1:"+m1);
                    if(set.contains(m1+1)){
                        set.remove(m1+1);
                        queue.offer(m1+1);
//                        System.out.println(m1+1);
                    }
//                    System.out.println("set:"+set);
                    break;
                }
            }
            //4.[m1+2，319) 随机一个m2 判断是否在set中，在则重新生成m2
            while (true){
                int m2 = random.nextInt(instrNum - m1 -2) + m1 + 2 ; //[0,319 - m1 -2] [m1 + 2,319]
                if(!set.contains(m2)){
                    boolean index = true;
                    //遍历判断是否[m1+2，319]已经分配完
                    for (int i = m1+2; i <= (instrNum-1); i++) {
                        if(set.contains(i)) index = false;
                    }
                    if(index) {
//                        System.out.println("那完了");
                        break;
                    }
                    continue;
                }else {
                    //5.不在则放m2 m2+1
                    set.remove(m2);
                    queue.offer(m2);
//                    System.out.println("m2:"+m2);
                    if(m2 == (instrNum-1)) {
//                        System.out.println("拿到");
                        break;
                    }
                    if(set.contains(m2+1)){
                        set.remove(m2+1);
                        queue.offer(m2+1);
//                        System.out.println(m2+1);
                    }
//                    System.out.println("set:"+set);
                    break;
                }
            }
//            System.out.println("===================");
        }
        return queue;
    }

}
