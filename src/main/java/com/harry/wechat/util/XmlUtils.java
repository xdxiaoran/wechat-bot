package com.harry.wechat.util;


import org.dom4j.*;
// import com.thoughtworks.xstream.XStream;

/**
 * @author Harry
 * @date 2020/12/5
 * Time: 23:31
 * Desc: XmlUtils
 */
public class XmlUtils {
    private final static String XML_DECLARATION = "<?xml version=\"1.0\" encoding=\"utf-8\"?>";

    private XmlUtils() {
    }

    /**
     * 序列化XML
     *
     * @param obj
     * @return
     */
    /*public static <T> String toXML(Object obj) {
        XStream stream = getXStream();
        stream.processAnnotations(obj.getClass());
        return new StringBuffer(XML_DECLARATION).append(stream.toXML(obj)).toString();
    }*/

    /**
     * 反序列化XML
     *
     * @param xmlStr
     * @param clazz
     * @return
     */
   /* public static <T> T fromXML(String xmlStr, Class<T> clazz) {
        XStream stream = getXStream();
        stream.processAnnotations(clazz);
        Object obj = stream.fromXML(xmlStr);
        try {
            return clazz.cast(obj);
        } catch (ClassCastException e) {
            return null;
        }
    }*/

    /**
     * 获取指定节点的值
     *
     * @param xpath
     * @return
     */
    public static String getNodeValue(String xpath, String dataStr) {
        try {
            // 将字符串转为xml
            Document document = DocumentHelper.parseText(dataStr);
            // 查找节点
            Element element = (Element) document.selectSingleNode(xpath);
            if (element != null) {
                return element.getStringValue();
            }
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getAttribute(String xpath, String dataStr) {
        try {
            // 将字符串转为xml
            Document document = DocumentHelper.parseText(dataStr);
            // 查找节点
            Element rootElement = document.getRootElement();
            Attribute attribute = rootElement.attribute(xpath);
            if (attribute != null) {
                return attribute.getStringValue();
            }
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getAttributes(String dataStr, String a1, String a2) {
        try {
            String res;
            // 将字符串转为xml
            Document document = DocumentHelper.parseText(dataStr);
            // 查找节点
            Element rootElement = document.getRootElement();
            Attribute attribute = rootElement.attribute(a1);
            res = attribute.getStringValue();
            res += "--";

            Attribute attribute1 = rootElement.attribute(a2);
            res += attribute1.getStringValue();
            return res;

        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 获取Xstream实例
     *
     * @return
     */
   /* public static XStream getXStream() {
        return new XStream();
    }*/

}
