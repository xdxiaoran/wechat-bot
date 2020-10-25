package com.harry.wechat.config;

/**
 * @author Harry
 * @date 2020/10/23
 * Time: 00:04
 * Desc: FunType
 */
public enum FunType {

    /**
     * 创建微信
     * 无返回值，调用获取当前启动微信数目和登录信息查看
     * {"funid":11}
     */
    INIT(11),
    /**
     * 退出登录
     * <p>
     * {"funid":12,"WeChatID":1}
     */
    LOGOUT(12),
    /**
     * 发送文本消息
     * {"funid":20,"WeChatID":1,"wxid":"filehelper","content":"你好[耶]"}
     * <p>
     * wxidlist	str	可选，群艾特的wxid数组，文本内容需带@名字，可用查询信息获取名字
     * <p>
     * {"funid":20,"WeChatID":1,"wxid":"filehelper","content":"@cxx 你好[耶]","wxidlist":["wxid_cxkcxkcxkcxkSB"]}
     */
    SENDTEXT(20),
    /**
     * 发送xml数据（小程序/链接）
     * type	int	5为链接，21为小程序
     * content	str	xml数据/附加数据，注意转义！！！只需要把content转义
     * {"funid":21,"WeChatID":1,"wxid":"filehelper","content":"","type":21}
     */
    SENDXML(21),
    /**
     * {"funid":22,"WeChatID":1,"wxid":"filehelper","content":"<?xml version=\"1.0\"?><msg bigheadimgurl=\"http://wx.qlogo.cn/mmhead/ver_1/JGSU7YZiaAdhyALl6UftpJBLHfbwIHfUhmmcR9LW3OZvctLLFNic43aK4aPU8nN3MNeK6qyU7NImeVhiaiamDYOsalLYyyOrcA3S7NvLGtmHzZE/0\" smallheadimgurl=\"http://wx.qlogo.cn/mmhead/ver_1/JGSU7YZiaAdhyALl6UftpJBLHfbwIHfUhmmcR9LW3OZvctLLFNic43aK4aPU8nN3MNeK6qyU7NImeVhiaiamDYOsalLYyyOrcA3S7NvLGtmHzZE/132\" username=\"填写WXID\" nickname=\"蔡徐坤\" fullpy=\"aoliao\" shortpy=\"\" alias=\"19991203\" imagestatus=\"2\" scene=\"17\" province=\"福建\" city=\"中国\" sign=\"\" sex=\"1\" certflag=\"0\" certinfo=\"\" brandIconUrl=\"\" brandHomeUrl=\"\" brandSubscriptConfigUrl= \"\" brandFlags=\"0\" regionCode=\"CN_Fujian_Sanming\" />
     * "}
     */
    CARD(22),
    /**
     * 发送图片/发送文件
     * {"funid":24,"WeChatID":1,"wxid":"filehelper","content":"写路径，如C:\\Users\\1\\Desktop\\1.png"}
     * {"funid":24,"WeChatID":1,"wxid":"filehelper","content":"写路径，如C:\\Users\\1\\Desktop\\1.mp4"}
     */
    SENDFILE(24),

    /**
     * 获取群成员列表
     * 返回的wxid为群主wxid，wxidlist数组为群成员列表wxid
     * {"funid":33,"WeChatID":1,"wxid":"12345678937@chatroom"}
     */
    GROUPMEMBERLIST(33),
    /**
     * 获取好友列表
     * 包含微信、微信群、公众号等列表信息
     * {"funid":40,"WeChatID":1}
     */
    FRIENDLIST(40),
    /**
     * 查询信息
     * {"funid":43,"WeChatID":1,"wxid":"wxid_cxkcxkcxkcxkSB"}
     */
    FRIENTINFO(43),
    /**
     * 收款
     * {"funid":50,"WeChatID":1,"wxid":"wxid_cxkcxkcxkcxkSB","transferid":""}
     */
    TRANSFER(50),

    /**
     * 获取当前启动微信数目和登录信息
     * 如果未登录，会返回二维码地址，生成二维码微信扫码即可登录
     * {"funid":10}
     */
    LOGIN(10);

    private Integer funid;

    FunType(Integer funid) {
        this.funid = funid;
    }

    public Integer getFunid() {
        return funid;
    }
}
