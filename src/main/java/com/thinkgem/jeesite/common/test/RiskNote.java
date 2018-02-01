package com.thinkgem.jeesite.common.test;

import java.io.Serializable;
import java.util.Date;

/**
 * redis实体类测试
 * 
 * @author hephe
 *
 */
public class RiskNote implements Serializable {
	private static final long serialVersionUID = 4758331879028183605L;

	private Integer ApplId;
	private Integer allqyorg3monNum;
	private Double loanF6endAmt;

	private String isHighRisk1;
	private Date createDate;
	private String risk1Detail;

	public Integer getApplId() {
		return ApplId;
	}
	public void setApplId(Integer applId) {
		ApplId = applId;
	}
	public Integer getAllqyorg3monNum() {
		return allqyorg3monNum;
	}
	public void setAllqyorg3monNum(Integer allqyorg3monNum) {
		this.allqyorg3monNum = allqyorg3monNum;
	}
	public Double getLoanF6endAmt() {
		return loanF6endAmt;
	}
	public void setLoanF6endAmt(Double loanF6endAmt) {
		this.loanF6endAmt = loanF6endAmt;
	}
	public String getIsHighRisk1() {
		return isHighRisk1;
	}
	public void setIsHighRisk1(String isHighRisk1) {
		this.isHighRisk1 = isHighRisk1;
	}
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	public String getRisk1Detail() {
		return risk1Detail;
	}
	public void setRisk1Detail(String risk1Detail) {
		this.risk1Detail = risk1Detail;
	}
	public Integer getRisk2() {
		return risk2;
	}
	public void setRisk2(Integer risk2) {
		this.risk2 = risk2;
	}
	public String getRisk3() {
		return risk3;
	}
	public void setRisk3(String risk3) {
		this.risk3 = risk3;
	}
	public String getCreditpaymonth() {
		return creditpaymonth;
	}
	public void setCreditpaymonth(String creditpaymonth) {
		this.creditpaymonth = creditpaymonth;
	}
	private Integer risk2;
	private String risk3;
	private String creditpaymonth;
}
