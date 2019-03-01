
package net.iaround.tools;

import android.text.Selection;

import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * 关于字符串的公用方法
 */
public class StringUtil {
    public final static int FACE_TAG_NUM = 4;


    /**
     * 获取字符串长度，英文字符长度为1，中文字符长度为2，表情长度为4
     */
    /*获取字符串长度，英文字符长度为1，中文字符长度为2,表情按照表情实际所占的长度计算*  2016-3-16*/
    public static int getLengthCN1(String value) {
        int valueLength = 0;
        String chinese = "[\u0391-\uFFE5]";// 汉字
        Object[] objects = replaceFace(value);
        int faceNum = (Integer) objects[0];
        String replaceAfterValue = String.valueOf(objects[1]);
        /* 获取字段值的长度，如果含中文字符，则每个中文字符长度为2，否则为1 */
        for (int i = 0; i < replaceAfterValue.length(); i++) {
            /* 获取一个字符 */
            String temp = replaceAfterValue.substring(i, i + 1);
            /* 判断是否为中文字符 */
            if (temp.matches(chinese)) {
                /* 中文字符长度为1 */
                valueLength += 2;
            } else {
                /* 其他字符长度为1 */
                valueLength += 1;
            }
        }
//		return valueLength ;

        return valueLength + (faceNum * FACE_TAG_NUM);// 1个表情算4个字符
    }

    public static Object[] replaceFace(String str) {
        String regex = FaceManager.regex;
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);
        StringBuffer sb = new StringBuffer();
        int cishu = 0;
        while (matcher.find()) {
            matcher.appendReplacement(sb, "");
            cishu++;
        }
        matcher.appendTail(sb);

        return new Object[]
                {Integer.valueOf(cishu), sb.toString()};
    }


    /**
     * ASCII表中可见字符从!开始，偏移位值为33(Decimal)
     */
    static final char DBC_CHAR_START = 33; // 半角!

    /**
     * ASCII表中可见字符到~结束，偏移位值为126(Decimal)
     */
    static final char DBC_CHAR_END = 126; // 半角~

    /**
     * 全角对应于ASCII表的可见字符从！开始，偏移值为65281
     */
    static final char SBC_CHAR_START = 65281; // 全角！

    /**
     * 全角对应于ASCII表的可见字符到～结束，偏移值为65374
     */
    static final char SBC_CHAR_END = 65374; // 全角～

    /**
     * ASCII表中除空格外的可见字符与对应的全角字符的相对偏移
     */
    static final int CONVERT_STEP = 65248; // 全角半角转换间隔

    /**
     * 全角空格的值，它没有遵从与ASCII的相对偏移，必须单独处理
     */
    static final char SBC_SPACE = 12288; // 全角空格 12288
    static final char SBC_SPACE_1 = 41377; // 全角空格 12288
    /**
     * 半角空格的值，在ASCII中为32(Decimal)
     */
    static final char DBC_SPACE = ' '; // 半角空格

    /**
     * <PRE>
     * 半角字符->全角字符转换
     * 只处理空格，!到&tilde;之间的字符，忽略其他
     * </PRE>
     */
    public static String bj2qj(String src) {
        if (src == null) {
            return src;
        }
        StringBuilder buf = new StringBuilder(src.length());
        char[] ca = src.toCharArray();
        for (int i = 0; i < ca.length; i++) {
            if (ca[i] == DBC_SPACE) { // 如果是半角空格，直接用全角空格替代
                buf.append(SBC_SPACE);
            } else if ((ca[i] >= DBC_CHAR_START) && (ca[i] <= DBC_CHAR_END)) { // 字符是!到~之间的可见字符
                buf.append((char) (ca[i] + CONVERT_STEP));
            } else { // 不对空格以及ascii表中其他可见字符之外的字符做任何处理
                buf.append(ca[i]);
            }
        }
        return buf.toString();
    }

    /**
     * <PRE>
     * 全角字符->半角字符转换
     * 只处理全角的空格，全角！到全角～之间的字符，忽略其他 只处理全角空格转半角空格
     * </PRE>
     */
    public static String qj2bj(String src) {
        if (src == null) {
            return src;
        }


        StringBuilder buf = new StringBuilder(src.length());
        char[] ca = src.toCharArray();
        for (int i = 0; i < ca.length; i++) {
            // if (ca[i] >= SBC_CHAR_START && ca[i] <= SBC_CHAR_END) { //
            // 如果位于全角！到全角～区间内
            // buf.append((char) (ca[i] - CONVERT_STEP));
            // } else
            if (ca[i] == SBC_SPACE || ca[i] == SBC_SPACE_1) { // 如果是全角空格
                CommonFunction.log("shifengxiong", " qj2bj" + ca[i]);
                buf.append(DBC_SPACE);
            } else { // 不处理全角空格，全角！到全角～区间外的字符
                buf.append(ca[i]);
            }
        }
        return buf.toString().trim();
    }

    /**
     * <PRE>
     * 对字符串进行截取
     * 获得可以显示完整表情的字符串
     * </PRE>
     *
     * @param input      需要处理的目标字符串
     * @param max_length 限制的最大长度
     */
    public static String getEntireFaceString(String input, int max_length) {
        String output = "";
        if (input.length() > max_length) {

            String wholeStr = input.substring(0, max_length);
            String tailStr = input.substring(max_length - 6, max_length);//表情最小长度为6

            CommonFunction.log("fan", "wholeStr---" + wholeStr);
            CommonFunction.log("fan", "tailStr---" + tailStr);

            if (tailStr.contains("[") && !tailStr.contains("]")) {

                int index = wholeStr.lastIndexOf("[");
                output = wholeStr.substring(0, index);

            } else if (tailStr.contains("[") && tailStr.contains("]")) {

                if (tailStr.lastIndexOf("]") < tailStr.lastIndexOf("[")) {
                    int index = wholeStr.lastIndexOf("]");
                    output = wholeStr.substring(0, index + 1);
                } else {
                    output = wholeStr;
                }
            } else {
                output = wholeStr;
            }

        } else {
            output = input;
        }
        return output;
    }

    public static class StringFromTo {
        public int start;
        public int end;
        public String subStr;

        public StringFromTo(int start, int end) {
            this.start = start;
            this.end = end;
            subStr = "";
        }

    }

    /**
     * 对字符串进行截取
     * Java截取方式一个中文长度为2
     *
     * @param str
     * @param subSLength
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String subStr(String str, int subSLength)
            throws UnsupportedEncodingException {
        if (str == null)
            return "";
        else {
            int tempSubLength = subSLength;//截取字节数
            String subStr = str.substring(0, str.length() < subSLength ? str.length() : subSLength);//截取的子串
            int subStrByetsL = subStr.getBytes("GBK").length;//截取子串的字节长度
            //int subStrByetsL = subStr.getBytes().length;//截取子串的字节长度
            // 说明截取的字符串中包含有汉字
            while (subStrByetsL > tempSubLength) {
                int subSLengthTemp = --subSLength;
                subStr = str.substring(0, subSLengthTemp > str.length() ? str.length() : subSLengthTemp);
                subStrByetsL = subStr.getBytes("GBK").length;
                //subStrByetsL = subStr.getBytes().length;
            }
            return subStr;
        }
    }

}
