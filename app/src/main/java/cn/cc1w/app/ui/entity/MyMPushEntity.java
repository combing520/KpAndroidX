package cn.cc1w.app.ui.entity;

/**
 * @author kpinfo
 * on 2021-01-04
 */
public class MyMPushEntity {
    private ContentBean content;
    private String msgId;
    private int msgType;

    public ContentBean getContent() {
        return content;
    }

    public void setContent(ContentBean content) {
        this.content = content;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public int getMsgType() {
        return msgType;
    }

    public void setMsgType(int msgType) {
        this.msgType = msgType;
    }

    public static class ContentBean {
        private String content;
        private int nid;
        private String ticker;
        private String title;
        private PushEntity payload;

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public int getNid() {
            return nid;
        }

        public void setNid(int nid) {
            this.nid = nid;
        }

        public String getTicker() {
            return ticker;
        }

        public void setTicker(String ticker) {
            this.ticker = ticker;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public PushEntity getPayload() {
            return payload;
        }

        public void setPayload(PushEntity payload) {
            this.payload = payload;
        }

        @Override
        public String toString() {
            return "ContentBean{" +
                    "content='" + content + '\'' +
                    ", nid=" + nid +
                    ", ticker='" + ticker + '\'' +
                    ", title='" + title + '\'' +
                    ", payload=" + payload +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "MyMPushEntity{" +
                "content=" + content +
                ", msgId='" + msgId + '\'' +
                ", msgType=" + msgType +
                '}';
    }
}

