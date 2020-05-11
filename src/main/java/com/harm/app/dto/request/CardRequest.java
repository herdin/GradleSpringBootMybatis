package com.harm.app.dto.request;

import javax.validation.constraints.NotBlank;

public class CardRequest {
    private String userId;
    private String cardNo;
    private String cardStaNm;
    private String cardPrdId;
    private String cardPrdNm;
    private String cardPrdCrgNm;

    public CardRequest(String userId, String cardNo, String cardStaNm, String cardPrdId, String cardPrdNm, String cardPrdCrgNm) {
        this.userId = userId;
        this.cardNo = cardNo;
        this.cardStaNm = cardStaNm;
        this.cardPrdId = cardPrdId;
        this.cardPrdNm = cardPrdNm;
        this.cardPrdCrgNm = cardPrdCrgNm;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCardNo() {
        return cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    public String getCardStaNm() {
        return cardStaNm;
    }

    public void setCardStaNm(String cardStaNm) {
        this.cardStaNm = cardStaNm;
    }

    public String getCardPrdId() {
        return cardPrdId;
    }

    public void setCardPrdId(String cardPrdId) {
        this.cardPrdId = cardPrdId;
    }

    public String getCardPrdNm() {
        return cardPrdNm;
    }

    public void setCardPrdNm(String cardPrdNm) {
        this.cardPrdNm = cardPrdNm;
    }

    public String getCardPrdCrgNm() {
        return cardPrdCrgNm;
    }

    public void setCardPrdCrgNm(String cardPrdCrgNm) {
        this.cardPrdCrgNm = cardPrdCrgNm;
    }

    @Override
    public String toString() {
        return "CardReqeust{" +
                "userId='" + userId + '\'' +
                ", cardNo='" + cardNo + '\'' +
                ", cardStaNm='" + cardStaNm + '\'' +
                ", cardPrdId='" + cardPrdId + '\'' +
                ", cardPrdNm='" + cardPrdNm + '\'' +
                ", cardPrdCrgNm='" + cardPrdCrgNm + '\'' +
                '}';
    }
}
