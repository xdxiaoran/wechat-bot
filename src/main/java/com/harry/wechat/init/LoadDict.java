package com.harry.wechat.init;

import lombok.extern.slf4j.Slf4j;
import org.apdplat.word.dictionary.DictionaryFactory;
import org.apdplat.word.util.WordConfTools;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.File;

import static com.harry.wechat.util.WordUtil.analyse;

/**
 * @author Harry
 * @date 2020/10/23
 * Time: 00:58
 * Desc: LoadDict
 */
@Order(1)
@Component
@Slf4j
@ConditionalOnProperty(prefix = "card", name = "mode", havingValue = "false", matchIfMissing = true)
public class LoadDict implements ApplicationRunner {
    @Override
    public void run(ApplicationArguments args) throws Exception {
        // TODO: 2020/10/23 加载字典
        log.info("加载字典");

        String selfDic = "D:\\part_of_speech_dic.txt";
        String selfSynonym = "D:\\word_synonym.txt";

        File fileDic = new File(selfDic);
        if (fileDic.exists()) {
            log.info("加载自定义字典 dic : {}", selfDic);
            WordConfTools.set("part.of.speech.dic.path", "classpath:part_of_speech_dic.txt," + selfDic);
            // WordConfTools.set("part.of.speech.dic.path", selfDic);
        }
        File fileSyn = new File(selfSynonym);
        if (fileSyn.exists()) {
            log.info("加载自定义字典 synonym : {}", selfSynonym);
            WordConfTools.set("word.synonym.path", "classpath:word_synonym.txt," + selfSynonym);

        }

        DictionaryFactory.reload();
        analyse("init");
        log.info("加载完成");
    }
}
