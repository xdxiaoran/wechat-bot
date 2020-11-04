package com.harry.wechat.util;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.harry.wechat.dto.BaseResponse;
import com.harry.wechat.dto.vo.GetAccountDto;
import com.harry.wechat.entity.Account;
import com.harry.wechat.service.AccountService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apdplat.word.WordSegmenter;
import org.apdplat.word.segmentation.Word;
import org.apdplat.word.tagging.PartOfSpeechTagging;
import org.apdplat.word.tagging.SynonymTagging;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.harry.wechat.util.Constance.*;

/**
 * @author Harry
 * @date 2020/10/9
 * Time: 15:03
 * Desc: WordUtil
 */
public class WordUtil {

    public static List<Word> analyse(String text) {
        List<Word> words = WordSegmenter.segWithStopWords(text);

        PartOfSpeechTagging.process(words);
        // System.out.println("标注词性：" + words);

        SynonymTagging.process(words);
        return words;
    }

    private static List<String> KEYS = Lists.newArrayList("f", "d", "m", "y");

    public static String collect(String wxid, String message, AccountService accountService, boolean isSvip) {
        List<Word> words = analyse(message);
        Map<String, List<String>> results = Maps.newHashMap();
        words.stream().filter(w -> KEYS.indexOf(w.getPartOfSpeech().getPos()) != -1).forEach(word -> {
            if (results.containsKey(word.getPartOfSpeech().getDes())) {
                results.get(word.getPartOfSpeech().getDes()).add(getText(word));
            } else {
                results.put(word.getPartOfSpeech().getDes(), Lists.newArrayList(getText(word)));
            }
        });

        if (results.isEmpty()) {
            // this.api().sendText(message.getFromUserName(), "机器人已完成升级，欢迎大家体验！\n 请输入大区、段位、影响来搜索账号");
        } else {

            if (words.stream().filter(w -> Objects.equals(w.getPartOfSpeech().getPos(), "m")).anyMatch(w -> w.getText().matches("^[-\\+]?[\\d]*$"))) {
                // 忽略数字错误识别
                return null;
            }

            String msg = MapUtils.mapToValueString(results);
            String account = getAccounts(words, wxid, accountService, isSvip);
            return "======" + msg + "========\n" + account + "\n 回复编号直接下单";
        }
        return null;
    }


    public static String getText(Word word) {
        return CollectionUtils.isEmpty(word.getSynonym()) || word.getSynonym().size() > 1 ? word.getText() : word.getSynonym().get(0).getText();
    }


    @SuppressWarnings("unchecked")
    public static String getAccounts(List<Word> words, String wxid, AccountService accountService, boolean isSvip) {
        List<String> servers = Lists.newArrayList();
        List<String> modes = Lists.newArrayList();

        Set<Integer> levelIndex = Sets.newHashSet();

        List<String> heros = Lists.newArrayList();

        List<String> skins = Lists.newArrayList();

        words.forEach(word -> {
            if (word.getPartOfSpeech().getPos().equals("f")) {
                servers.add(getText(word));
                return;
            }
            if (word.getPartOfSpeech().getPos().equals("d")) {
                levelIndex.add(INDESX.getOrDefault(getText(word), 3));
                return;
            }
            if (word.getPartOfSpeech().getPos().equals("y")) {
                heros.add(getText(word));
                return;
            }
        });

        if (CollectionUtils.isNotEmpty(USER_STATUS.get(wxid))) {
            USER_STATUS.get(wxid).forEach(word -> {
                if (CollectionUtils.isEmpty(servers) && word.getPartOfSpeech().getPos().equals("f")) {
                    words.add(word);
                    servers.add(getText(word));
                    return;
                }
                if (CollectionUtils.isEmpty(levelIndex) && word.getPartOfSpeech().getPos().equals("d")) {
                    words.add(word);
                    levelIndex.add(INDESX.getOrDefault(getText(word), 3));
                    return;
                }
                if (CollectionUtils.isEmpty(heros) && word.getPartOfSpeech().getPos().equals("y")) {
                    words.add(word);
                    heros.add(getText(word));
                    return;
                }
            });
        }

        if (CollectionUtils.isEmpty(modes)) {
            modes.add("单双排位");
        }

        GetAccountDto dto = GetAccountDto.builder()
                .servers(servers)
                .modes(modes)
                .levelIndex(levelIndex)
                .heros(heros)
                .wxid(wxid)
                .build();
        BaseResponse response = accountService.getAccounts(dto);

        List<Account> accounts = (List<Account>) response.getData();


        // List<Word> words1 = USER_STATUS.get(wxid);
        USER_STATUS.put(wxid, words);
        /*if (CollectionUtils.isNotEmpty(words1)) {
            words1.addAll(words);
            USER_STATUS.put(wxid, words1);

        } else {
            USER_STATUS.put(wxid, words);
        }*/

        if (CollectionUtils.isEmpty(accounts)) {

            // 未找到合适的账号，推荐相邻段位的账号
            // 黑铁推荐 青铜、白银
            // 其它段位推荐上下一个段位的
            if (levelIndex.size() == 0) {
                // 当前服务器没有符合的账号
                // 清空输入条件
                USER_STATUS.put(wxid, Collections.EMPTY_LIST);
                return "未找到符合条件的账号";
            } else {
                List<Integer> holder = Lists.newArrayList();
                levelIndex.forEach(level -> {
                    if (level == 0) {
                        holder.add(1);
                        holder.add(2);
                    } else {
                        holder.add(level - 1);
                        holder.add(level + 1);
                    }
                });

                dto.getLevelIndex().addAll(holder);

                BaseResponse response1 = accountService.getAccounts(dto);

                List<Account> accounts1 = (List<Account>) response1.getData();
                // List<Word> words1 = USER_STATUS.get(wxid);
                if (CollectionUtils.isEmpty(accounts1)) {
                    USER_STATUS.put(wxid, Collections.EMPTY_LIST);
                    return "未找到符合条件的账号";
                }
                accounts = accounts1;
            }
        }

        StringBuilder msg = new StringBuilder();

        accounts.forEach(account -> {
            msg.append("【" + account.getId() + "】 ");
            msg.append(account.getServer() + "-");
            msg.append("单双" + account.getRankLevelSingle());
            if (StringUtils.isNotBlank(account.getRankLevelFlexible())) {
                msg.append("-灵活" + account.getRankLevelFlexible());
            }
            if (isSvip) {
                msg.append("-等级 " + account.getLevel());
                msg.append(" -英雄数量 " + account.getHeroNum());
            }
            msg.append("- " + account.getPrice() + " rh\n");
        });

        return msg.toString();
    }


    public static String transferAmount1(String content) {

        Pattern pattern1 = Pattern.compile("\\[CDATA\\[收到转账.*.元。如需收钱，请点此升级至最新版本]]");
        Pattern pattern2 = Pattern.compile("[+-]?\\d+(.\\d{2})?");

        Matcher matcher = pattern1.matcher(content);
        if (matcher.find()) {
            String group = matcher.group(0);
            Matcher matcher2 = pattern2.matcher(group);
            if (matcher2.find()) {
                return matcher2.group(0);
            }
        }
        return "0";
    }

    public static String transferAmount(String content) {

        Pattern pattern = Pattern.compile("(?<=\\<des\\>\\<\\!\\[CDATA\\[收到转账).*?(?=元。如需收钱，请点此升级至最新版本\\]\\])");

        Matcher matcher = pattern.matcher(content);
        if (matcher.find()) {
            return matcher.group(0);
        } else {
            return transferAmount1(content);
        }
    }

    public static String transferId(String content) {

        // (?<=\<des\>\<\!\[CDATA\[收到转账).*?(?=元。如需收钱，请点此升级至最新版本\]\])
        Pattern pattern = Pattern.compile("(?<=\\<transferid\\>\\<\\!\\[CDATA\\[).*?(?=\\]\\]\\>\\<\\/transferid\\>)");

        Matcher matcher = pattern.matcher(content);
        if (matcher.find()) {
            return matcher.group(0);
        }
        return "0";
    }

}
