package cn.cc1w.app.ui.entity;

import java.util.List;

/**
 * 签到信息 实体类
 */
public class SignInfoEntity {
    private int code;
    private String message;
    private boolean success;
    private SignInfo data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public SignInfo getData() {
        return data;
    }

    public void setData(SignInfo data) {
        this.data = data;
    }

    public static class SignInfo {
        private String credit_day;
        private String credit_week;
        private String credit_mon;
        private String credit_total;
        private boolean day_signin;
        private String signin_number;
        private List<SigninBean> signin;
        private List<CreditRecordBean> credit_record;

        public String getCredit_day() {
            return credit_day;
        }

        public void setCredit_day(String credit_day) {
            this.credit_day = credit_day;
        }

        public String getCredit_week() {
            return credit_week;
        }

        public void setCredit_week(String credit_week) {
            this.credit_week = credit_week;
        }

        public String getCredit_mon() {
            return credit_mon;
        }

        public void setCredit_mon(String credit_mon) {
            this.credit_mon = credit_mon;
        }

        public String getCredit_total() {
            return credit_total;
        }

        public void setCredit_total(String credit_total) {
            this.credit_total = credit_total;
        }

        public boolean isDay_signin() {
            return day_signin;
        }

        public void setDay_signin(boolean day_signin) {
            this.day_signin = day_signin;
        }

        public String getSignin_number() {
            return signin_number;
        }

        public void setSignin_number(String signin_number) {
            this.signin_number = signin_number;
        }

        public List<SigninBean> getSignin() {
            return signin;
        }

        public void setSignin(List<SigninBean> signin) {
            this.signin = signin;
        }

        public List<CreditRecordBean> getCredit_record() {
            return credit_record;
        }

        public void setCredit_record(List<CreditRecordBean> credit_record) {
            this.credit_record = credit_record;
        }

        public static class SigninBean {
            private boolean attendance;
            private String credit;
            private String remark;

            public boolean isAttendance() {
                return attendance;
            }

            public void setAttendance(boolean attendance) {
                this.attendance = attendance;
            }

            public String getCredit() {
                return credit;
            }

            public void setCredit(String credit) {
                this.credit = credit;
            }

            public String getRemark() {
                return remark;
            }

            public void setRemark(String remark) {
                this.remark = remark;
            }
        }

        public static class CreditRecordBean {
            private String name;
            private String remark;
            private String top_credit;
            private String record_sum;

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getRemark() {
                return remark;
            }

            public void setRemark(String remark) {
                this.remark = remark;
            }

            public String getTop_credit() {
                return top_credit;
            }

            public void setTop_credit(String top_credit) {
                this.top_credit = top_credit;
            }

            public String getRecord_sum() {
                return record_sum;
            }

            public void setRecord_sum(String record_sum) {
                this.record_sum = record_sum;
            }
        }
    }
}
