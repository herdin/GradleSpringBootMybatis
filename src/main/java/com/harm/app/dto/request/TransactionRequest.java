package com.harm.app.dto.request;

public class TransactionRequest {
    private String card_no;
    private String tr_dtime;
    private String vehc_id;
    private String route_id;
    private String tr_amt;
    private String tr_type;

    public TransactionRequest(String card_no, String tr_dtime, String vehc_id, String route_id, String tr_amt, String tr_type) {
        this.card_no = card_no;
        this.tr_dtime = tr_dtime;
        this.vehc_id = vehc_id;
        this.route_id = route_id;
        this.tr_amt = tr_amt;
        this.tr_type = tr_type;
    }

    public String getCard_no() {
        return card_no;
    }

    public void setCard_no(String card_no) {
        this.card_no = card_no;
    }

    public String getTr_dtime() {
        return tr_dtime;
    }

    public void setTr_dtime(String tr_dtime) {
        this.tr_dtime = tr_dtime;
    }

    public String getVehc_id() {
        return vehc_id;
    }

    public void setVehc_id(String vehc_id) {
        this.vehc_id = vehc_id;
    }

    public String getRoute_id() {
        return route_id;
    }

    public void setRoute_id(String route_id) {
        this.route_id = route_id;
    }

    public String getTr_amt() {
        return tr_amt;
    }

    public void setTr_amt(String tr_amt) {
        this.tr_amt = tr_amt;
    }

    public String getTr_type() {
        return tr_type;
    }

    public void setTr_type(String tr_type) {
        this.tr_type = tr_type;
    }

    @Override
    public String toString() {
        return "TransactionRequest{" +
                "card_no='" + card_no + '\'' +
                ", tr_dtime='" + tr_dtime + '\'' +
                ", vehc_id='" + vehc_id + '\'' +
                ", route_id='" + route_id + '\'' +
                ", tr_amt='" + tr_amt + '\'' +
                ", tr_type='" + tr_type + '\'' +
                '}';
    }
}
