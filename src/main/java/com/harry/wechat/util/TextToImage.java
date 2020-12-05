package com.harry.wechat.util;

import org.springframework.util.ResourceUtils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

/**
 * @author Harry
 * @date 2020/11/26
 * Time: 02:24
 * Desc: TextToImage
 */
public class TextToImage {

    public static void main(String[] args) throws Exception {
        String message = "“两国交兵，不斩来使”在后世流传下来的交战规则主要只有“两国交兵，不斩来使”。春秋时期诸侯派出的外交使节是不可侵犯的。公元前596年楚国派出申舟出使齐国，楚庄王特意嘱咐不要从宋国经过。宋国执政华元听说了，觉得这是对宋国的莫大侮辱，就设伏击杀死楚国使者。楚庄王为此“投袂而起”，出动大军包围宋国国都整整9个月。宋国派出使者到晋国告急，晋国上一年刚被楚军打败，不敢冒与楚国全面冲突的危险，只是派解扬为使者劝宋国坚守，不要投降。解扬经过郑国，被郑国抓起来交给楚国。楚庄王亲自接见解扬，企图买通他，要他向宋军喊话，说晋军不再提供救援，断绝宋军的希望，解扬不同意。经楚庄王几次威逼利诱，解扬才答应下来。可是当解扬来到了望城中的楼车上，就大声疾呼，说晋国援军不日就到，请宋国无论如何要坚持下去。楚庄王大怒，解扬说：“我答应你的条件只是为了实现使命，现在使命实现了，请立刻处死我。”楚庄王无话可说，反而释放他回晋国。长期围困而无战果，楚庄王打算退兵，可申舟的父亲拦在车前，说：“我儿子不惜生命以完成国王的使命，难道国王要食言了吗？”楚庄王无言以对。申舟父亲建议在宋国建造住房、耕种土地，表示要长期占领宋国，宋国就会表示屈服。宋国见楚军不肯撤退，就派华元为使者来谈判。华元半夜里潜入楚军大营，劫持了楚军统帅子反，说：“我的国君要我为使者来谈判，现在城内确实已是‘易子而食，析骸以爨’，但是如果订立城下之盟则情愿举国牺牲。贵军退到三十里外，我国唯命是听。”子反就在睡床上保证做到。第二天报告了楚庄王，楚军真的退30里外，和宋国停战，双方保证不再互相欺瞒，华元作为这项和约的人质到楚国居住。\n" +
                "后世将这一交战规则称之为“两国交兵，不斩来使”。历史上最著名的战时两国使节以礼相见的故事是“彭城相会”。450年南朝刘宋与北魏发生战争，刘宋发起北伐，先胜后败，战略据点彭城被包围。江夏王刘义恭率领军队死守彭城（今徐州），北魏太武帝想一举打过长江，派出李孝伯为使节进彭城劝降。刘义恭派了张畅为代表与李孝伯谈判。两人都是当时的“名士”，互相代表各自的君主赠送礼品，尽管处在极其残酷的战争环境，但他们在谈判中却仍然是文质彬彬、礼貌周全。这次谈判本身并没有什么实质性的结果，可双方的礼节及言辞，一直被后世誉为战场佳话。";

        String msg1 = "======艾欧尼亚========\n" +
                "\n" +
                "100号 段位: 白银I\n" +
                "\n" +
                "英雄列表:\n" +
                "\n" +
                "正义天使,远古巫灵,九尾妖狐,法外狂徒,无极剑圣,仙灵女巫,符文法师,皎月女神,暗黑元首,亡灵战神,虚空之女,战争女神,众星之子,虚空之眼,迅捷斥候,麦林炮手,狂战士,雪原双子,永猎双子,赏金猎人,寒冰射手,暴走萝莉,蛮族之王,武器大师,堕落天使,时光守护者,唤潮鲛姬,炼金术士,痛苦之拥,瘟疫之源,死亡颂唱者,殇之木乃伊,琴瑟仙女,虚空行者,风暴之怒,天启者,瓦洛兰之盾,邪恶小法师,德邦总管,皮城女警,万花通灵,蒸汽机器人,熔岩巨兽,不祥之刃,永恒梦魇,扭曲树精,蜘蛛女皇,发条魔灵,复仇焰魂,暗夜猎手,圣锤之毅,酒桶,不屈之枪,探险家,铁铠冥魂,离群之刺,德玛西亚之力,曙光女神,远古恐惧,虚空先知,光辉女郎\n" +
                "\n" +
                "\n" +
                "\n" +
                " 回复编号直接下单";
        String[] strArr = msg1.split("\n");
        int line_height = 30; // 每行或者每个文字的高度
        int image_height = strArr.length * line_height; // 每张图片的高度
        int every_line = image_height / line_height; // 每张图片有多少行文字

        createImage(strArr, new Font("宋体", Font.PLAIN, 22), 500, image_height, every_line, line_height);

        // createImage(msg1, new Font("微软雅黑", Font.PLAIN, 32), new File("tmp/a.png"), 500, 6000);


    }

    public static void createImage(String str, Font font, File outFile,
                                   Integer width, Integer height) throws Exception {
        // 创建图片
        BufferedImage image = new BufferedImage(width, height,
                BufferedImage.TYPE_INT_BGR);
        Graphics g = image.getGraphics();
        g.setClip(0, 0, width, height);
        g.setColor(Color.white);
        g.fillRect(0, 0, width, height);// 先用黑色填充整张图片,也就是背景
        g.setColor(Color.black);// 在换成黑色
        g.setFont(font);// 设置画笔字体
        /** 用于获得垂直居中y */
        Rectangle clip = g.getClipBounds();
        FontMetrics fm = g.getFontMetrics(font);
        int ascent = fm.getAscent();
        int descent = fm.getDescent();
        int y = (clip.height - (ascent + descent)) / 2 + ascent;
        for (int i = 0; i < 6; i++) {// 256 340 0 680
            g.drawString(str, i * 680, y);// 画出字符串
        }
        g.dispose();
        ImageIO.write(image, "png", outFile);// 输出png图片
    }



    /**
     * 根据str,font的样式等生成图片
     * https://blog.csdn.net/sinat_28505133/article/details/54669111
     *
     * @param strArr
     * @param font
     * @param width
     * @param image_height
     * @throws Exception
     */
    public static void createImage(String[] strArr, Font font,
                                   int width, int image_height, int every_line, int line_height) throws Exception {

        FontMetrics fm = sun.font.FontDesignMetrics.getMetrics(font);
        int stringWidth = fm.charWidth('字');// 标点符号也算一个字
        int line_string_num = width % stringWidth == 0 ? (width / stringWidth) : (width / stringWidth) + 1;
        System.out.println("每行=" + line_string_num);

        List<String> listStr = new ArrayList<String>();
        List<String> newList = new ArrayList<String>();
        for (int h = 0; h < strArr.length; h++) {
            listStr.add(strArr[h]);
        }
        for (int j = 0; j < listStr.size(); j++) {
            if (listStr.get(j).length() > line_string_num) {
                newList.add(listStr.get(j).substring(0, line_string_num));
                listStr.add(j + 1, listStr.get(j).substring(line_string_num));
                listStr.set(j, listStr.get(j).substring(0, line_string_num));
            } else {
                newList.add(listStr.get(j));
            }
        }

        int a = newList.size();
        int b = every_line;
        int imgNum = a % b == 0 ? (a / b) : (a / b) + 1;

        for (int m = 0; m < imgNum; m++) {
            // String filePath = ResourceUtils.getURL("classpath:").getPath() + "/tmp/" + m + ".jpg";
            String filePath = "tmp/" + m + ".jpg";
            File outFile = new File(filePath);
            if (!outFile.getParentFile().exists()) {
                outFile.getParentFile().mkdir();
            }
            outFile.createNewFile();
            System.out.println(outFile.getAbsolutePath());
            // 创建图片
            BufferedImage image = new BufferedImage(width, image_height,
                    BufferedImage.TYPE_INT_BGR);
            Graphics g = image.getGraphics();
            g.setClip(0, 0, width, image_height);
            g.setColor(Color.white); // 背景色白色
            g.fillRect(0, 0, width, image_height);
            g.setColor(Color.black);//  字体颜色黑色
            g.setFont(font);// 设置画笔字体
            // 每张多少行，当到最后一张时判断是否填充满
            for (int i = 0; i < every_line; i++) {
                int index = i + m * every_line;
                if (newList.size() - 1 >= index) {
                    System.out.println("每行实际=" + newList.get(index).length());
                    g.drawString(newList.get(index), 0, line_height * (i + 1));
                }
            }
            g.dispose();
            ImageIO.write(image, "jpg", outFile);// 输出png图片
        }
    }
}
