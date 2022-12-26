package com.readbin.main;

import java.math.BigInteger;

public class FaretableTransferDTO {
	Integer transferIndex;
	Integer group;

	Integer designator;

	BigInteger trips;

	BigInteger expResolution;

	BigInteger expOffset;

	String description; // print text or display text

	String text; // ticket description

	String acceptanceRule;

	String oppDirSmRoute = "N";

	String sameDirSmRoute = "N";

	String oppDirDifRoute = "N";

	String sameDirDifRoute = "N";

	String capFlag = "N";

	String holdFlag = "N";

	String upgredFlag = "N";

	String payreqFlag = "N";

	public String getAcceptanceRule() {
		return acceptanceRule;
	}

	public void setAcceptanceRule(String acceptanceRule) {
		this.acceptanceRule = acceptanceRule;
	}

	public Integer getTransferIndex() {
		return transferIndex;
	}

	public void setTransferIndex(Integer transferIndex) {
		this.transferIndex = transferIndex;
	}

	public Integer getGroup() {
		return group;
	}

	public void setGroup(Integer group) {
		this.group = group;
	}

	public Integer getDesignator() {
		return designator;
	}

	public void setDesignator(Integer designator) {
		this.designator = designator;
	}

	public BigInteger getTrips() {
		return trips;
	}

	public void setTrips(BigInteger trips) {
		this.trips = trips;
	}

	public BigInteger getExpResolution() {
		return expResolution;
	}

	public void setExpResolution(BigInteger expResolution) {
		this.expResolution = expResolution;
	}

	public BigInteger getExpOffset() {
		return expOffset;
	}

	public void setExpOffset(BigInteger expOffset) {
		this.expOffset = expOffset;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getOppDirSmRoute() {
		return oppDirSmRoute;
	}

	public void setOppDirSmRoute(String oppDirSmRoute) {
		this.oppDirSmRoute = oppDirSmRoute;
	}

	public String getSameDirSmRoute() {
		return sameDirSmRoute;
	}

	public void setSameDirSmRoute(String sameDirSmRoute) {
		this.sameDirSmRoute = sameDirSmRoute;
	}

	public String getOppDirDifRoute() {
		return oppDirDifRoute;
	}

	public void setOppDirDifRoute(String oppDirDifRoute) {
		this.oppDirDifRoute = oppDirDifRoute;
	}

	public String getSameDirDifRoute() {
		return sameDirDifRoute;
	}

	public void setSameDirDifRoute(String sameDirDifRoute) {
		this.sameDirDifRoute = sameDirDifRoute;
	}

	public String getCapFlag() {
		return capFlag;
	}

	public void setCapFlag(String capFlag) {
		this.capFlag = capFlag;
	}

	public String getHoldFlag() {
		return holdFlag;
	}

	public void setHoldFlag(String holdFlag) {
		this.holdFlag = holdFlag;
	}

	public String getUpgredFlag() {
		return upgredFlag;
	}

	public void setUpgredFlag(String upgredFlag) {
		this.upgredFlag = upgredFlag;
	}

	public String getPayreqFlag() {
		return payreqFlag;
	}

	public void setPayreqFlag(String payreqFlag) {
		this.payreqFlag = payreqFlag;
	}

}
