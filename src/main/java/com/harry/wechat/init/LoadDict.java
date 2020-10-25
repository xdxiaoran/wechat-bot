package com.harry.wechat.init;

import lombok.extern.slf4j.Slf4j;
import org.apdplat.word.dictionary.DictionaryFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

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
        DictionaryFactory.reload();
        analyse("init");
        log.info("加载完成");
    }
}
