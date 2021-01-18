package com.harry.wechat.util;

import com.google.common.collect.Sets;
import org.apdplat.word.segmentation.Word;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * @author Harry
 * @date 2020/10/23
 * Time: 23:16
 * Desc: Constance
 */
public class Constance {

    /**
     * 结账
     * 红包
     * 下了
     * 打完
     * 下线
     * 下号
     * 打完了
     * 结束
     * 结束了
     * 结束啦
     * 用完了
     * 老板不打了
     * 号下了
     * 多少钱
     * 好了
     * 完
     * 结账
     * 我下了
     * 退
     * 回收
     * 还号
     * jieshu
     * JIESHU
     * DAWAN
     * dawan
     * dwan
     *
     * @param message
     */
    public static Set<String> END_KEYWORD = Sets.newHashSet("结账", "红包", "下了", "打完", "下线", "下号", "打完了", "结束", "结束了", "结束啦", "用完了", "老板不打了", "号下了", "多少钱", "好了", "完", "结账", "我下了", "退", "回收", "还号", "jieshu", "JIESHU", "DAWAN", "dawan", "dwan");
    // private static Stream<String> END_KEYWORD = Lists.newArrayList("结账", "红包", "下了", "打完", "下线", "下号", "打完了", "结束", "结束了", "结束啦", "用完了", "老板不打了", "号下了", "多少钱", "好了", "完", "结账", "我下了", "退", "回收", "还号", "jieshu", "JIESHU", "DAWAN", "dawan", "dwan").stream();

    public static Set<String> QUERY_KEYWORD =  Sets.newHashSet("还能用", "可以用", "可以使用","是否可用");

    public static Set<String> END_ALL_KEYWORD =   Sets.newHashSet("全部下号");

    /**
     * 可过期的缓存
     */
    public static ExpiryMap<String, List<Word>> USER_STATUS = new ExpiryMap<>();
    public static ExpiryMap<String, String> USER_STATUS_WORD = new ExpiryMap<>();


    public static HashMap<String, Integer> INDESX = new HashMap<String, Integer>() {
        {
            put("黑铁", 0);
            put("青铜", 1);
            put("黄铜", 1);
            put("白银", 2);
            put("黄金", 3);
            put("铂金", 4);
            put("白金", 4);
            put("钻石", 5);
            put("大师", 6);
            put("王者", 7);
        }
    };


    public static Set<String> BLACK_NAME =  Sets.newHashSet("偷登号", "不租", "不能租", "太强", "外挂", "黑机器", "黑ip", "有问题", "嫌疑犯");

    public static Boolean silentMode = false;
    public static Boolean isCardMode = false;
}
