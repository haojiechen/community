package com.nowcoder.community.util;

import org.apache.commons.lang3.CharUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * @author chen
 */
@Component
public class SensitiveFilter {

    private class TrieNode{
        private boolean isKeywordEnd = false;

        private Map<Character,TrieNode> subNodes = new HashMap<>();

        public boolean isKeywordEnd() {
            return isKeywordEnd;
        }

        public void setKeywordEnd(boolean keywordEnd) {
            isKeywordEnd = keywordEnd;
        }

        public void addSubNode(Character c,TrieNode node){
            subNodes.put(c,node);
        }
        public TrieNode getSubNode(Character c){
            return subNodes.get(c);
        }
    }

    private static final Logger logger = LoggerFactory.getLogger(SensitiveFilter.class);
    private static final String REPLACEMENT = "***";

    private TrieNode rootNode = new TrieNode();

    @PostConstruct
    public void init(){
        try (
                InputStream is = this.getClass().getClassLoader().getResourceAsStream("sensitive-words.txt");
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
                ){
            String keyword;
            while ((keyword = bufferedReader.readLine())!=null)
            {
                this.addKeyword(keyword);
            }

        } catch (Exception e) {
            logger.error("加载敏感词文件失败!",e.getMessage());
        }
    }
    private void addKeyword(String keyword){
        TrieNode tmp = rootNode;
        for (int i=0;i<keyword.length();i++){
            char c = keyword.charAt(i);
            TrieNode cur = tmp.getSubNode(c);
            if (cur == null){
                cur = new TrieNode();
                tmp.addSubNode(c,cur);
            }
            tmp = cur;
        }
        tmp.setKeywordEnd(true);
    }

    /**
     * 过滤敏感词
     * @param text 待过滤的敏感词
     * @return 过滤后的敏感词
     */
    public String filter(String text){
        if (text == null)
        {
            return null;
        }
        StringBuilder res = new StringBuilder();
        for (int i=0;i<text.length();i++){
            if (isSymbol(text.charAt(i))){
                continue;
            }
            int j = i;
            TrieNode cur = rootNode;
            boolean isKeywordEnd = false;
            while (j<text.length() && (cur = cur.getSubNode(text.charAt(j++)))!=null){
                if (isKeywordEnd = cur.isKeywordEnd()){
                    break;
                }
                while (j<text.length() && isSymbol(text.charAt(j))) {
                    j++;
                }
            }
            if (isKeywordEnd){
                res.append(REPLACEMENT);
                i=j;
            }else{
                res.append(text.charAt(i));
            }
        }
        return res.toString();
    }
    private boolean isSymbol(Character c){
        // 0x2E80 - 0x9FFF是东亚文字范围
        return CharUtils.isAsciiAlphanumeric(c) && c == ' ' && (c<0x2E80 || c>0x9FFF);
    }

}
