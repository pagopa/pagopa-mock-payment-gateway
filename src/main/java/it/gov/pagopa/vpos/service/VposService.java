package it.gov.pagopa.vpos.service;

import com.google.gson.Gson;
import it.gov.pagopa.db.entity.TableConfig;
import it.gov.pagopa.service.ConfigService;
import it.gov.pagopa.vpos.dto.AuthRequest3dsV2Enum;
import it.gov.pagopa.vpos.dto.request.*;
import it.gov.pagopa.vpos.dto.response.*;
import it.gov.pagopa.vpos.entity.Transaction3DsEntity;
import it.gov.pagopa.vpos.utils.MacBuilder3dsV2;
import it.gov.pagopa.vpos.utils.VposConstants;
import it.gov.pagopa.vpos.utils.VposCreditCardGenerator;
import it.gov.pagopa.vpos.utils.VposUtils;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

@Service
@Log4j2
public class VposService {
    private static final String RETURN_CODE_OK = "00";
    private static final String RETURN_CODE_ERROR_INVALID_MAC = "04";
    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    private static final String THREEDSAUTHORIZATION0 = "THREEDSAUTHORIZATION0";
    private static final String THREEDSAUTHORIZATION1 = "THREEDSAUTHORIZATION1";
    private static final String THREEDSAUTHORIZATION2 = "THREEDSAUTHORIZATION2";
    private static final String METHOD = "Method";
    private static final String CHALLENGE = "Challenge";
    private static final String AUTHORIZATION = "Authorization";

    @Autowired
    private Transaction3DsService transaction3DsService;

    @Autowired
    private ConfigService configService;

    @Value("${HOST_URL}")
    private String host;

    public BPWXmlResponse getMock(String data) throws Exception {
        Function<String, Optional<TableConfig>> findByKeyOrThrow = key -> configService.getOptionalByKey(key);

        TableConfig httResponse = findByKeyOrThrow.apply(VposConstants.VPOS_HTTP_CODE_RESPONSE).orElseThrow(Exception::new);
        HttpStatus resolve = HttpStatus.valueOf(Integer.parseInt(httResponse.getPropertyValue()));
        if (resolve != HttpStatus.OK) throw new ResponseStatusException(resolve);

        BPWXmlRequest xmlRequest = VposUtils.unmarshallEng(data);
        XmlData requestData = xmlRequest.getData();
        String operation = xmlRequest.getRequest().getOperation();
        String returnCode = null;
        String transactionStatus;

        switch (operation) {
            case "ORDERSTATUS":
                OrderStatus orderStatus = requestData.getOrderStatus();
                returnCode = configService.getByKey(VposConstants.VPOS_ORDER_STATUS_RESPONSE).getPropertyValue();
                transactionStatus = configService.getByKey(VposConstants.VPOS_TRANSACTION_STATUS).getPropertyValue();
                return createOrderStatusResponse(orderStatus, returnCode, transactionStatus);
            case "ACCOUNTING":
            case "REFUND":
                returnCode = RETURN_CODE_OK;
                return createContabOrRefundResponse3ds2(returnCode);
            default:
                break;
        }

        ThreeDSAuthorizationRequest0 request0 = requestData.getThreeDSAuthorizationRequest0();
        ThreeDSAuthorizationRequest1 request1 = requestData.getThreeDSAuthorizationRequest1();
        ThreeDSAuthorizationRequest2 request2 = requestData.getThreeDSAuthorizationRequest2();

        if (!checkMac3dsV2(xmlRequest)) {
            returnCode = RETURN_CODE_ERROR_INVALID_MAC;
        }

        if (request0 != null) {
            returnCode = configService.getByKey(VposConstants.VPOS_STEP0_3DS2_RESPONSE).getPropertyValue();
        } else if (request1 != null) {
            returnCode = configService.getByKey(VposConstants.VPOS_STEP1_3DS2_RESPONSE).getPropertyValue();
        } else if (request2 != null) {
            returnCode = transaction3DsService.getByThreeDSServerTransId(request2.getThreeDSTransId()).getOutcome();
        }

        return createAuthResponse3ds2(requestData, returnCode);
    }

    private BPWXmlResponse createAuthResponse3ds2(XmlData data, String returnCode) throws Exception {
        BPWXmlResponse response = new BPWXmlResponse();
        response.setTimestamp(SIMPLE_DATE_FORMAT.format(new Date()));
        response.setMac(generateAuthorizationMac(response.getTimestamp(), returnCode));
        String transId = null;

        switch (getResponseType(returnCode)) {
            case METHOD:
                ThreeDSMethod threeDSMethod = renspondMethodUrl(data);
                data.setThreeDSMethod(threeDSMethod);
                transId = threeDSMethod.getThreeDSTransId();
                break;
            case CHALLENGE:
                ThreeDSChallenge threeDSChallenge = respondChallengeUrl(data);
                data.setThreeDSChallenge(threeDSChallenge);
                transId = threeDSChallenge.getThreeDSTransId();
                break;
            case AUTHORIZATION:
                data.setThreeDSAuthorization(respondAuthorization(data));
                data.setPanAliasData(createPanAlias());
                break;
        }

        ThreeDSAuthorizationRequest0 threeDSAuthorizationRequest0 = data.getThreeDSAuthorizationRequest0();
        if (threeDSAuthorizationRequest0 != null) {
            saveTransaction(returnCode, transId, threeDSAuthorizationRequest0);
        }

        response.setData(data);
        response.setResult(returnCode);
        return response;
    }

    private void saveTransaction(String returnCode, String id, ThreeDSAuthorizationRequest0 threeDSAuthorizationRequest0) {
        Transaction3DsEntity transaction3DsEntity = new Transaction3DsEntity();
        transaction3DsEntity.setOutcome(returnCode);
        transaction3DsEntity.setThreeDSServerTransId(id);
        transaction3DsEntity.setOrderId(threeDSAuthorizationRequest0.getOrderID());
        transaction3DsEntity.setNotifyUrl(threeDSAuthorizationRequest0.getNotifUrl());
        transaction3DsEntity.setThreeDSMTDNotifUrl(threeDSAuthorizationRequest0.getThreeDSMtdNotifUrl());
        transaction3DsEntity.setPan(threeDSAuthorizationRequest0.getPan());
        transaction3DsEntity.setAmount(threeDSAuthorizationRequest0.getAmount());
        transaction3DsEntity.setCurrency(threeDSAuthorizationRequest0.getCurrency());

        transaction3DsService.save(transaction3DsEntity);
    }

    private ThreeDSMethod renspondMethodUrl(XmlData data) {
        ThreeDSMethod method = new ThreeDSMethod();
        String threeDSTransId = UUID.randomUUID().toString();
        method.setThreeDSTransId(threeDSTransId);
        method.setThreeDSMethodData(createBase64MethodData3Ds(threeDSTransId, data.getThreeDSAuthorizationRequest0().getThreeDSMtdNotifUrl()));
        method.setThreeDSMethodUrl(normalizeUrl(String.format("%s/issuer/3ds2.0/method", host)));
        method.setMac(generate3DSMethodMac(method));

        return method;
    }

    private ThreeDSChallenge respondChallengeUrl(XmlData data) {
        ThreeDSAuthorizationRequest1 threeDSAuthorizationRequest1 = data.getThreeDSAuthorizationRequest1();
        String transId = threeDSAuthorizationRequest1 != null ? threeDSAuthorizationRequest1.getThreeDSTransId() : UUID.randomUUID().toString();
        ThreeDSChallenge challenge = new ThreeDSChallenge();
        challenge.setThreeDSTransId(transId);
        challenge.setAcsUrl(normalizeUrl(String.format("%s/issuer/3ds2.0/challenge", host)));
        challenge.setCReq(createBase64MethodData3Ds(transId, null));
        challenge.setMac(generateChallengeMac(challenge));

        if (threeDSAuthorizationRequest1 != null) {
            Transaction3DsEntity byThreeDSServerTransId = transaction3DsService.getByThreeDSServerTransId(transId);
            byThreeDSServerTransId.setThreeDSMtdComplInd(threeDSAuthorizationRequest1.getThreeDSMtdComplInd());
            transaction3DsService.save(byThreeDSServerTransId);
        }

        return challenge;
    }

    private ThreeDSAuthorization respondAuthorization(XmlData data3DSV2) throws Exception {
        Function<String, Optional<Transaction3DsEntity>> findByKeyOrThrow = key ->
                Optional.ofNullable(transaction3DsService.getByThreeDSServerTransId(key));

        String amount;
        String orderID;
        String currency;
        String pan;
        ThreeDSAuthorizationRequest0 threeDSAuthorizationRequest0 = data3DSV2.getThreeDSAuthorizationRequest0();
        if (threeDSAuthorizationRequest0 != null) {
            amount = threeDSAuthorizationRequest0.getAmount();
            orderID = threeDSAuthorizationRequest0.getOrderID();
            currency = threeDSAuthorizationRequest0.getCurrency();
            pan = threeDSAuthorizationRequest0.getPan();
        } else {
            ThreeDSAuthorizationRequest1 threeDSAuthorizationRequest1 = data3DSV2.getThreeDSAuthorizationRequest1();
            ThreeDSAuthorizationRequest2 threeDSAuthorizationRequest2 = data3DSV2.getThreeDSAuthorizationRequest2();
            String transId = threeDSAuthorizationRequest1 != null ? threeDSAuthorizationRequest1.getThreeDSTransId() : threeDSAuthorizationRequest2.getThreeDSTransId();
            Transaction3DsEntity transaction3DsEntity = findByKeyOrThrow.apply(transId).orElseThrow(Exception::new);
            amount = transaction3DsEntity.getAmount();
            orderID = transaction3DsEntity.getOrderId();
            currency = transaction3DsEntity.getCurrency();
            pan = transaction3DsEntity.getPan();
        }
        ThreeDSAuthorization authorization = new ThreeDSAuthorization();
        authorization.setPaymentType("03");
        authorization.setAuthorizationType("I");
        authorization.setTransactionID(RandomStringUtils.randomAlphanumeric(10));
        authorization.setNetwork(VposCreditCardGenerator.getCircuitCodeFromPan(pan));
        authorization.setOrderID(orderID);
        authorization.setTransactionAmount(amount);
        authorization.setAuthorizedAmount(amount);
        authorization.setCurrency(currency);
        authorization.setExponent("2");
        authorization.setAccountedAmount("0");
        authorization.setRefundedAmount("0");
        authorization.setTransactionResult("00");
        authorization.setTimestamp(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(new Date()));
        authorization.setAuthorizationNumber(RandomStringUtils.randomNumeric(8));
        authorization.setAcquirerBIN("453997");
        authorization.setMerchantID("000000000000476");
        authorization.setTransactionStatus("02");
        authorization.setResponseCodeISO("00");
        authorization.setRrn(RandomStringUtils.randomNumeric(12));
        authorization.setMac("0EA6645D79E9752BE05800BE9CFE623CE3973395");
        return authorization;
    }

    private String generateChallengeMac(ThreeDSChallenge challenge) {
        MacBuilder3dsV2 macBuilder = new MacBuilder3dsV2();
        macBuilder.addElement(AuthRequest3dsV2Enum.THREE_DS_TRANS_ID, challenge.getThreeDSTransId());
        macBuilder.addElement(AuthRequest3dsV2Enum.CREQ, challenge.getCReq());
        macBuilder.addElement(AuthRequest3dsV2Enum.ACS_URL, challenge.getAcsUrl());

        return macBuilder.toSha1Hex(StandardCharsets.ISO_8859_1);
    }

    private static String generate3DSMethodMac(ThreeDSMethod method) {
        MacBuilder3dsV2 macBuilder = new MacBuilder3dsV2();
        macBuilder.addElement(AuthRequest3dsV2Enum.THREE_DS_TRANS_ID, method.getThreeDSTransId());
        macBuilder.addElement(AuthRequest3dsV2Enum.THREE_DS_METHOD_DATA, method.getThreeDSMethodData());
        macBuilder.addElement(AuthRequest3dsV2Enum.THREE_DS_METHOD_URL, method.getThreeDSMethodUrl());

        return macBuilder.toSha1Hex(StandardCharsets.ISO_8859_1);
    }

    private static String createBase64MethodData3Ds(String transId, String url) {
        MethodData3Ds methodData3Ds = new MethodData3Ds();
        methodData3Ds.setThreeDSServerTransID(transId);
        methodData3Ds.setThreeDSMethodNotificationUrl(url);

        return Base64Utils.encodeToString(new Gson().toJson(methodData3Ds).getBytes());
    }

    public static String normalizeUrl(String url) {
        try {
            return new URI(url).normalize().toString();
        } catch (URISyntaxException e) {
            log.error(e);
            return null;
        }
    }

    private static String getResponseType(String returnCode) {
        String responseType = "";
        if (StringUtils.equals(returnCode, "00")) {
            responseType = AUTHORIZATION;
        } else if (StringUtils.equals(returnCode, "25")) {
            responseType = METHOD;
        } else if (StringUtils.equals(returnCode, "26")) {
            responseType = CHALLENGE;
        }

        return responseType;
    }

    private static BPWXmlResponse createOrderStatusResponse(OrderStatus requestData, String returnCode, String transactionStatus) {
        BPWXmlResponse response = new BPWXmlResponse();
        response.setTimestamp(SIMPLE_DATE_FORMAT.format(new Date()));
        response.setResult(returnCode);
        response.setMac("BB617A8F6E65A78B8F3958D701EBB6B22D36DA9F");
        XmlData data = new XmlData();
        data.setOrderStatus(requestData);
        data.setAuthorization(createAuthorization(transactionStatus));
        data.setNumberOfItems(1);
        data.setProductRef("XYZABC");
        data.setPanAliasData(createPanAlias());
        response.setData(data);

        return response;
    }

    private static ThreeDSAuthorization createAuthorization(String transactionStatus) {
        ThreeDSAuthorization authorization = new ThreeDSAuthorization();
        authorization.setPaymentType("03");
        authorization.setAuthorizationType("I");
        authorization.setTransactionID(RandomStringUtils.randomAlphanumeric(10));
        authorization.setNetwork("01");
        authorization.setOrderID("pos91");
        authorization.setTransactionAmount("4450");
        authorization.setAuthorizedAmount("4450");
        authorization.setCurrency("978");
        authorization.setExponent("2");
        authorization.setAccountedAmount("0");
        authorization.setRefundedAmount("0");
        authorization.setTransactionResult("00");
        authorization.setTimestamp(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(new Date()));
        authorization.setAuthorizationNumber(RandomStringUtils.randomNumeric(8));
        authorization.setAcquirerBIN("453997");
        authorization.setMerchantID("000000000000476");
        authorization.setTransactionStatus(transactionStatus);
        authorization.setResponseCodeISO("00");
        authorization.setRrn(RandomStringUtils.randomNumeric(12));
        authorization.setMac("0EA6645D79E9752BE05800BE9CFE623CE3973395");

        return authorization;
    }

    private static PanAliasData createPanAlias() {
        PanAliasData panAliasData = new PanAliasData();
        String alias = "alias";
        panAliasData.setPanAlias(alias);
        String exp = "exp";
        panAliasData.setPanAliasExpDate(exp);
        String tail = "tail";
        panAliasData.setPanAliasTail(tail);
        panAliasData.setMac(DigestUtils.sha1Hex(String.format("%s&%s&%s", alias, exp, tail)));

        return panAliasData;
    }

    private static BPWXmlResponse createContabOrRefundResponse3ds2(String returnCode) {
        BPWXmlResponse response = new BPWXmlResponse();
        response.setMac(generateAuthorizationMac(null, returnCode));
        response.setResult(returnCode);

        return response;
    }

    private static String generateAuthorizationMac(String timestamp, String returnCode) {
        MacBuilder3dsV2 macBuilder = new MacBuilder3dsV2();
        macBuilder.addString(timestamp);
        macBuilder.addString(returnCode);
        macBuilder.addString("3Me3-RDpGW-Zyn2mysYMhf-h5-mt2Vfmx-m8-P8-e3ncB--La4hkFEfStdk-2avPs-qyFwEXeGdDRzWZruu-M7xpX-7HmdDPWsWT");

        return macBuilder.toSha1Hex(StandardCharsets.ISO_8859_1);
    }

    private static boolean checkMac3dsV2(BPWXmlRequest bpwXmlRequest) {
        return StringUtils.equalsIgnoreCase(calculateMac3dsV2(bpwXmlRequest), bpwXmlRequest.getRequest().getMac());
    }

    private static String calculateMac3dsV2(BPWXmlRequest bpwXmlRequest) {
        MacBuilder3dsV2 macBuilder = new MacBuilder3dsV2();
        Request3dsV2 request3dsV2 = bpwXmlRequest.getRequest();
        String operazione = request3dsV2.getOperation();
        macBuilder.addElement(AuthRequest3dsV2Enum.OPERATION, operazione);
        macBuilder.addElement(AuthRequest3dsV2Enum.TIMESTAMP, request3dsV2.getTimestamp());
        XmlData data = bpwXmlRequest.getData();
        switch (operazione) {
            case THREEDSAUTHORIZATION0:
                buildMAC0(data, macBuilder);
                break;
            case THREEDSAUTHORIZATION1:
                buildMAC1(data, macBuilder);
                break;
            case THREEDSAUTHORIZATION2:
                buildMAC2(data, macBuilder);
                break;
        }
        macBuilder.addString("3Me3-RDpGW-Zyn2mysYMhf-h5-mt2Vfmx-m8-P8-e3ncB--La4hkFEfStdk-2avPs-qyFwEXeGdDRzWZruu-M7xpX-7HmdDPWsWT");
        return macBuilder.toSha1Hex(StandardCharsets.ISO_8859_1);
    }

    private static void buildMAC0(XmlData data, MacBuilder3dsV2 macBuilder) {
        ThreeDSAuthorizationRequest0 request0 = data.getThreeDSAuthorizationRequest0();
        buildHeaderMAC(macBuilder, request0.getHeader(), request0.getOrderID());
        macBuilder.addElement(AuthRequest3dsV2Enum.PAN, request0.getPan());
        String cvv2 = request0.getCvv2();
        addIfPresent(macBuilder, cvv2, AuthRequest3dsV2Enum.CVV2);
        macBuilder.addElement(AuthRequest3dsV2Enum.EXPIRE_DATE, request0.getExpDate());
        macBuilder.addElement(AuthRequest3dsV2Enum.AMOUNT, request0.getAmount());
        macBuilder.addElement(AuthRequest3dsV2Enum.CURRENCY, request0.getCurrency());
        macBuilder.addElement(AuthRequest3dsV2Enum.ACCOUNT_MODE, request0.getAccountingMode());
        macBuilder.addElement(AuthRequest3dsV2Enum.NETWORK, request0.getNetwork());
        String emailCH = request0.getEmailCH();
        addIfPresent(macBuilder, emailCH, AuthRequest3dsV2Enum.EMAIL_CH);
        String userid = request0.getUserid();
        addIfPresent(macBuilder, userid, AuthRequest3dsV2Enum.USER_ID);
        addIfPresent(macBuilder, request0.getOpDescr(), AuthRequest3dsV2Enum.OPERATION_DESCRIPTION);
        addIfPresent(macBuilder, request0.getName(), AuthRequest3dsV2Enum.NAME);
        addIfPresent(macBuilder, request0.getSurname(), AuthRequest3dsV2Enum.SURNAME);
        addIfPresent(macBuilder, request0.getTaxID(), AuthRequest3dsV2Enum.TAX_ID);
        macBuilder.addElement(AuthRequest3dsV2Enum.THREE_DS_DATA, request0.getThreeDSData());
        macBuilder.addElement(AuthRequest3dsV2Enum.NAME_CH, request0.getNameCH());
        addIfPresent(macBuilder, request0.getNotifUrl(), AuthRequest3dsV2Enum.NOTIFY_URL);
        addIfPresent(macBuilder, request0.getThreeDSMtdNotifUrl(), AuthRequest3dsV2Enum.THREEDSMTDNOTIFURL);
    }

    private static void addIfPresent(MacBuilder3dsV2 macBuilder, String field, AuthRequest3dsV2Enum v2Enum) {
        if (StringUtils.isNotBlank(field))
            macBuilder.addElement(v2Enum, field);
    }

    private static void buildHeaderMAC(MacBuilder3dsV2 macBuilder, VposHeader header, String orderID) {
        macBuilder.addElement(AuthRequest3dsV2Enum.SHOP_ID, header.getShopID());

        if (StringUtils.isNotBlank(orderID))
            macBuilder.addElement(AuthRequest3dsV2Enum.ORDER_ID, orderID);

        macBuilder.addElement(AuthRequest3dsV2Enum.OPERATOR_ID, header.getOperatorID());
        macBuilder.addElement(AuthRequest3dsV2Enum.REQ_REF_NUMBER, header.getReqRefNum());
    }

    private static void buildMAC1(XmlData data, MacBuilder3dsV2 macBuilder) {
        ThreeDSAuthorizationRequest1 request1 = data.getThreeDSAuthorizationRequest1();
        buildHeaderMAC(macBuilder, request1.getHeader(), null);
        macBuilder.addElement(AuthRequest3dsV2Enum.THREE_DS_TRANS_ID, request1.getThreeDSTransId());
        macBuilder.addElement(AuthRequest3dsV2Enum.THREE_DS_MTD_COMPL_IND, request1.getThreeDSMtdComplInd());
    }

    private static void buildMAC2(XmlData data, MacBuilder3dsV2 macBuilder) {
        ThreeDSAuthorizationRequest2 request2 = data.getThreeDSAuthorizationRequest2();
        buildHeaderMAC(macBuilder, request2.getHeader(), null);
        macBuilder.addElement(AuthRequest3dsV2Enum.THREE_DS_TRANS_ID, request2.getThreeDSTransId());
    }
}
