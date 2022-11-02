package it.gov.pagopa.vpos.dto;

import java.math.BigInteger;
import java.util.Date;

/**
 * The Enum AuthRequestEnum.
 */
public enum AuthRequest3dsV2Enum {

	REQUEST_ROOT(true, "BPWXmlRequest", null, null, 0),
	RELEASE(false, "Release", String.class, null, 2),
	REQUEST(true, "Request3dsV2", null, null, 0),
	OPERATION(true, "Operation", String.class, null, 0),
	TIMESTAMP(true, "Timestamp", Date.class, "yyyy-MM-dd'T'HH:mm:ss.SSS", 23),
	MAC(true, "MAC", String.class, null, 0),
	DATA(true, "Data", null, null, 0),
	THREE_DS_AUTH_REQ_0(true, "ThreeDSAuthorizationRequest0", null, null, 0),
	THREE_DS_AUTH_REQ_1(true, "ThreeDSAuthorizationRequest1", null, null, 0),
	THREE_DS_AUTH_REQ_2(true, "ThreeDSAuthorizationRequest2", null, null, 0),
	REQUEST_HEADER(true, "Header", null, null, 0),
	SHOP_ID(true, "ShopID", String.class, null, 15),
	OPERATOR_ID(true, "OperatorID", String.class, null, 18),
	REQ_REF_NUMBER(true, "ReqRefNum", String.class, null, 32),

	// Authorization request0 - step1
	ORDER_ID(true, "OrderID", String.class, null, 50),
	PAN(true, "Pan", String.class, null, 19),
	CVV2(false, "CVV2", String.class, null, 4),
	CREATE_PAN_ALIAS(false, "CreatePanAlias", String.class, null, 1),
	EXPIRE_DATE(true, "ExpDate", String.class, null, 4),
	AMOUNT(true, "Amount", BigInteger.class, null, 8),
	CURRENCY(true, "Currency", String.class, null, 3),
	EXPONENT(true, "Exponent", String.class, null, 1),
	ACCOUNT_MODE(true, "AccountingMode", String.class, null, 1),
	NETWORK(true, "Network", String.class, null, 2),
	USER_ID(false, "Userid", String.class, null, 30),
	OPERATION_DESCRIPTION(false, "OpDescr", String.class, null, 100),
	PRODUCT_REF(false, "ProductRef", String.class, null, 200000),
	NAME(false, "Name", String.class, null, 20),
	SURNAME(false, "Surname", String.class, null, 20),
	TAX_ID(false, "TaxID", String.class, null, 20),
	THREE_DS_DATA(true, "ThreeDSData", String.class, null, 20),
	NOTIFY_URL(true, "NotifURL", String.class, null, 20),
	EMAIL_CH(true, "EmailCH", String.class, null, 50),
	NAME_CH(true, "NameCH", String.class, null, 20),
	ACQUIRER(false, "Acquirer", String.class, null, 5),
	IP_ADDRESS(false, "IpAddress", String.class, null, 15),
	USRAUTHFLAG(false, "Acquirer", String.class, null, 1),
	OPTIONS(false, "Acquirer", String.class, null, 26),
	ANTIFRAUD(false, "Acquirer", String.class, null, 100),
	TRECURR(false, "Acquirer", String.class, null, 1),
	CRECURR(false, "Acquirer", String.class, null, 50),
	INSTALLMENTSNUMBER(false, "Acquirer", String.class, null, 2),
	THREEDSMTDNOTIFURL(false, "ThreeDSMTDNotifUrl", String.class, null, 256),
	CHALLENGEWINSIZE(false, "ChallengeWinSize", String.class, null, 2),
	CPROF(false, "CProf", String.class, null, 2),


	//Authorization request1 - step2
	THREE_DS_MTD_COMPL_IND(true, "ThreeDSMtdComplInd", String.class, null, 20),

	// Authorization request1 and request2 - step3
	THREE_DS_TRANS_ID(true, "ThreeDSTransID", String.class, null, 36),

	//ThreeDSMethodResponse
	THREE_DS_METHOD_DATA(true, "ThreeDSMethodData", String.class, null, 200000),
	THREE_DS_METHOD_URL(true, "ThreeDSMethodUrl", String.class, null, 200000),

	//Challenge response
	CREQ(true, "CReq", String.class, null, 200000),
	ACS_URL(true, "AcsUrl", String.class, null, 200000);

	private boolean mandatory;
	private String tagName;
	private Class<?> type;
	private String format;
	private int length;

	private AuthRequest3dsV2Enum(boolean mandatory, String tagName, Class<?> type, String format, int length) {
		this.mandatory = mandatory;
		this.tagName = tagName;
		this.type = type;
		this.format = format;
		this.length = length;
	}

	/**
	 * @return the mandatory
	 */
	public boolean isMandatory() {
		return mandatory;
	}

	/**
	 * @param mandatory
	 *            the mandatory to set
	 */
	public void setMandatory(boolean mandatory) {
		this.mandatory = mandatory;
	}

	/**
	 * @return the tagName
	 */
	public String getTagName() {
		return tagName;
	}

	/**
	 * @param tagName
	 *            the tagName to set
	 */
	public void setTagName(String tagName) {
		this.tagName = tagName;
	}

	/**
	 * @return the type
	 */
	public Class<?> getType() {
		return type;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(Class<?> type) {
		this.type = type;
	}

	/**
	 * @return the format
	 */
	public String getFormat() {
		return format;
	}

	/**
	 * @param format
	 *            the format to set
	 */
	public void setFormat(String format) {
		this.format = format;
	}

	/**
	 * @return the length
	 */
	public int getLength() {
		return length;
	}

	/**
	 * @param length
	 *            the length to set
	 */
	public void setLength(int length) {
		this.length = length;
	}

	/**
	 * Gets the root element.
	 *
	 * @return the root element
	 */
	public static AuthRequest3dsV2Enum getRootElement() {
		return REQUEST_ROOT;
	}
}
