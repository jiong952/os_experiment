package com.zjh.pageReplacement;

import java.util.*;

/**
 * @author 张俊鸿
 * @description: 最近最久未使用
 * @since 2022-06-01 23:10
 */
public class LRU {
    private static int blockNum = 4; //内存块数
    private static int pageSize = 10;  //页面存放指令的数目
    private static int instrNum = 320;  //作业的指令数目
    private static int pageNum ;  //页面数目 = instructionNum / PageSize
    private static int missingPageNum; //缺页数
    private static double missingPageRate; //缺页率 = MissingPageNum / instructionNum
    private static Queue<Integer> instrQue; //访问指令序列 值指令地址
    private static Map<Integer,Integer> blockMap; //存放内存块 key是内存块号 value是页面号


    public static void main(String[] args) {
        OPT opt = new OPT();
        opt.init();
        System.out.println("指令序列"+instrQue);
        System.out.println("页面数"+pageNum);
        opt.run();
        missingPageRate = (missingPageNum*1.0)/instrNum ;
        System.out.println("缺页数"+missingPageNum);
        System.out.println("缺页率"+(missingPageRate));
    }

    /**
     * 初始化
     */
    public void init(){
        pageNum = instrNum / pageSize;
        blockMap = new HashMap<>();
        instrQue = new OPT().getInstrQue();
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

    public void run(){
        OPT opt = new OPT();
        for (int i = 0; i < instrNum; i++) {
            //取出指令
            int address = instrQue.poll();
            //虚拟页号
            int pageCur = address / pageSize;
            //页内地址
            int inIndex = address % pageSize;
            System.out.println(address+"虚拟地址: 第" + pageCur +"页 第"+ inIndex+"条指令");
            if(!blockMap.containsValue(pageCur)){
                System.out.println("发生缺页");
                //缺页
                missingPageNum++;
                if(blockMap.size() < blockNum){
                    //内存块没满
                    //直接加入一页
                    blockMap.put(blockMap.size(),pageCur);
                }else {
                    //内存块满了，使用OPT算法进行淘汰
                    //借用一个list 遍历指令序列 筛选出要淘汰的
                    ArrayList<Integer> list = new ArrayList<>();
                    list.addAll(blockMap.values());
                    //遍历淘汰
                    Iterator<Integer> iterator2 = instrQue.iterator();
                    while (iterator2.hasNext()){
                        Integer nextAddress = iterator2.next();
                        int pageReplace = nextAddress / pageSize;
                        for (int i1 = 0; i1 < list.size(); i1++) {
                            if(list.get(i1).equals(pageReplace)){
                                list.remove(i1);
                            }
                        }
                        if(list.size() == 1){
                            break;
                        }
                    }
                    int page = list.get(0);
                    int index = getKey(blockMap, page);
                    blockMap.remove(index);
                    System.out.println("从第"+index+"块移除页号" + page);
                    blockMap.put(index,pageCur);
                }
            }
            int block = opt.getKey(blockMap, pageCur);
            System.out.println(address+"物理地址: 第" + block +"块 第"+ inIndex+"条指令");
            System.out.println("============");
        }
    }

    /**
     * 通过值得到键
     *
     * @param blockMap 块地图
     * @param value    价值
     * @return {@link Integer}
     */
    public Integer getKey(Map<Integer,Integer> blockMap,Integer value){
        Iterator<Map.Entry<Integer, Integer>> iterator = blockMap.entrySet().iterator();
        while (iterator.hasNext()){
            Map.Entry<Integer, Integer> next = iterator.next();
            if(next.getValue().equals(value)){
                return next.getKey();
            }
        }
        return null;
    }
}
