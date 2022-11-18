package it.gov.pagopa.service;

import it.gov.pagopa.db.entity.TableConfig;
import it.gov.pagopa.vpos.dto.request.BPWXmlRequest;
import it.gov.pagopa.vpos.dto.request.Request3dsV2;
import it.gov.pagopa.vpos.dto.request.XmlData;
import it.gov.pagopa.vpos.dto.response.ThreeDSAuthorizationRequest0;
import it.gov.pagopa.vpos.dto.response.ThreeDSAuthorizationRequest1;
import it.gov.pagopa.vpos.dto.response.ThreeDSAuthorizationRequest2;
import it.gov.pagopa.vpos.dto.response.VposHeader;
import it.gov.pagopa.vpos.entity.Transaction3DsEntity;
import it.gov.pagopa.vpos.service.Transaction3DsService;
import it.gov.pagopa.vpos.service.VposService;
import it.gov.pagopa.vpos.utils.VposUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class VposServiceTest {
    @InjectMocks
    private VposService vposService;

    @Mock
    private ConfigService configService;

    @Mock
    private Transaction3DsService transaction3DsService;

    @Test
    public void step0Test() throws Exception {
        try(MockedStatic<VposUtils> utilsMock = Mockito.mockStatic(VposUtils.class)) {
            TableConfig httpResponse = new TableConfig();
            httpResponse.setPropertyValue("200");

            TableConfig step0Response = new TableConfig();
            step0Response.setPropertyValue("00");

            when(configService.getOptionalByKey(any())).thenReturn(Optional.of(httpResponse));
            utilsMock.when(() -> VposUtils.unmarshallEng(any())).thenReturn(createStep0Request());
            when(configService.getByKey(any())).thenReturn(step0Response);
            doNothing().when(transaction3DsService).save(any());

            assertNotNull(vposService.getMock(null));
        }
    }

    @Test
    public void step0And1Test() throws Exception {
        try(MockedStatic<VposUtils> utilsMock = Mockito.mockStatic(VposUtils.class)) {
            TableConfig httpResponse = new TableConfig();
            httpResponse.setPropertyValue("200");

            TableConfig step0Response = new TableConfig();
            step0Response.setPropertyValue("25");

            when(configService.getOptionalByKey(any())).thenReturn(Optional.of(httpResponse));
            utilsMock.when(() -> VposUtils.unmarshallEng(any())).thenReturn(createStep0Request());
            when(configService.getByKey(any())).thenReturn(step0Response);
            doNothing().when(transaction3DsService).save(any());

            assertNotNull(vposService.getMock(null));
        }
    }

    @Test
    public void step0And2Test() throws Exception {
        try(MockedStatic<VposUtils> utilsMock = Mockito.mockStatic(VposUtils.class)) {
            TableConfig httpResponse = new TableConfig();
            httpResponse.setPropertyValue("200");

            TableConfig step0Response = new TableConfig();
            step0Response.setPropertyValue("26");

            utilsMock.when(() -> VposUtils.unmarshallEng(any())).thenReturn(createStep0Request());
            when(configService.getOptionalByKey(any())).thenReturn(Optional.of(httpResponse));
            when(configService.getByKey(any())).thenReturn(step0Response);
            when(transaction3DsService.getByThreeDSServerTransId(any())).thenReturn(new Transaction3DsEntity());
            doNothing().when(transaction3DsService).save(any());

            assertNotNull(vposService.getMock(null));
        }
    }

    @Test
    public void step1Test() throws Exception {
        try(MockedStatic<VposUtils> utilsMock = Mockito.mockStatic(VposUtils.class)) {
            TableConfig httpResponse = new TableConfig();
            httpResponse.setPropertyValue("200");

            TableConfig step1Response = new TableConfig();
            step1Response.setPropertyValue("00");

            Transaction3DsEntity transaction3DsEntity = new Transaction3DsEntity();
            transaction3DsEntity.setAmount("5000");
            transaction3DsEntity.setOrderId("123456");
            transaction3DsEntity.setCurrency("978");
            transaction3DsEntity.setPan("9998500000000015");

            when(configService.getOptionalByKey(any())).thenReturn(Optional.of(httpResponse));
            utilsMock.when(() -> VposUtils.unmarshallEng(any())).thenReturn(createStep1Request());
            when(configService.getByKey(any())).thenReturn(step1Response);
            when(transaction3DsService.getByThreeDSServerTransId(any())).thenReturn(transaction3DsEntity);

            assertNotNull(vposService.getMock(null));
        }
    }

    @Test
    public void step2Test() throws Exception {
        try(MockedStatic<VposUtils> utilsMock = Mockito.mockStatic(VposUtils.class)) {
            TableConfig httpResponse = new TableConfig();
            httpResponse.setPropertyValue("200");

            TableConfig step0Response = new TableConfig();
            step0Response.setPropertyValue("00");

            Transaction3DsEntity transaction3DsEntity = new Transaction3DsEntity();
            transaction3DsEntity.setAmount("5000");
            transaction3DsEntity.setOrderId("123456");
            transaction3DsEntity.setCurrency("978");
            transaction3DsEntity.setPan("9998500000000015");

            when(configService.getOptionalByKey(any())).thenReturn(Optional.of(httpResponse));
            utilsMock.when(() -> VposUtils.unmarshallEng(any())).thenReturn(createStep2Request());
            when(transaction3DsService.getByThreeDSServerTransId(any())).thenReturn(transaction3DsEntity);

            assertNotNull(vposService.getMock(null));
        }
    }

    @Test
    public void orderStatusTest() throws Exception {
        try(MockedStatic<VposUtils> utilsMock = Mockito.mockStatic(VposUtils.class)) {
            BPWXmlRequest request = createStep0Request();
            request.getRequest().setOperation("ORDERSTATUS");

            TableConfig httpResponse = new TableConfig();
            httpResponse.setPropertyValue("200");
            TableConfig orderStatus = new TableConfig();
            orderStatus.setPropertyValue("00");
            TableConfig transactionStatus = new TableConfig();
            transactionStatus.setPropertyValue("00");

            utilsMock.when(() -> VposUtils.unmarshallEng(any())).thenReturn(request);
            when(configService.getOptionalByKey(any())).thenReturn(Optional.of(httpResponse));
            when(configService.getByKey(any())).thenReturn(orderStatus);
            when(configService.getByKey(any())).thenReturn(transactionStatus);

            assertNotNull(vposService.getMock(null));
        }
    }

    @Test
    public void refundTest() throws Exception {
        try(MockedStatic<VposUtils> utilsMock = Mockito.mockStatic(VposUtils.class)) {
            BPWXmlRequest request = createStep0Request();
            request.getRequest().setOperation("REFUND");

            TableConfig httpResponse = new TableConfig();
            httpResponse.setPropertyValue("200");

            utilsMock.when(() -> VposUtils.unmarshallEng(any())).thenReturn(request);
            when(configService.getOptionalByKey(any())).thenReturn(Optional.of(httpResponse));

            assertNotNull(vposService.getMock(null));
        }
    }

    private static BPWXmlRequest createStep0Request() {
        BPWXmlRequest request = new BPWXmlRequest();
        request.setRelease("02");
        request.setRequest(new Request3dsV2("THREEDSAUTHORIZATION0", "2015-02-08T12:02:00.000", "0dbfff7cab9cbdcd96fc854a1717221b8c5c31ca"));

        XmlData data = new XmlData();
        ThreeDSAuthorizationRequest0 step0Data = new ThreeDSAuthorizationRequest0();
        VposHeader header = new VposHeader("000000000000003", "oper0001", "12345678901234567890123452289000");
        step0Data.setHeader(header);
        step0Data.setOrderID("1234567890");
        step0Data.setPan("9998500000000015");
        step0Data.setCvv2("123");
        step0Data.setExpDate("0409");
        step0Data.setAmount("4450");
        step0Data.setCurrency("978");
        step0Data.setExponent("2");
        step0Data.setAccountingMode("I");
        step0Data.setNetwork("01");
        step0Data.setEmailCH("EmailCH");
        step0Data.setNameCH("Jon Snow");
        step0Data.setUserid("user1");
        step0Data.setOpDescr("CallCenterRequest1037");
        step0Data.setProductRef("12345678");
        step0Data.setName("Jon");
        step0Data.setSurname("Snow");
        step0Data.setTaxID("SNWJNO96A01F205L");
        step0Data.setThreeDSData("BASE64ENCODED3DSDATA=");
        step0Data.setNotifUrl("https://mydomain.com/challengeNotification");
        step0Data.setTRecurr("C");
        step0Data.setCRecurr("PST581426946");

        ThreeDSAuthorizationRequest1 step1Data = new ThreeDSAuthorizationRequest1();
        step1Data.setThreeDSMtdComplInd("Y");
        step1Data.setThreeDSTransId("df4b3490-db44-4a88-9619-ab173ff76fbe");
        step1Data.setHeader(header);

        data.setThreeDSAuthorizationRequest0(step0Data);
        data.setThreeDSAuthorizationRequest1(step1Data);

        request.setData(data);

        return request;
    }

    private static BPWXmlRequest createStep1Request() {
        BPWXmlRequest request = new BPWXmlRequest();
        request.setRelease("02");
        request.setRequest(new Request3dsV2("THREEDSAUTHORIZATION1", "2015-02-08T12:02:00.000", "0dbfff7cab9cbdcd96fc854a1717221b8c5c31ca"));

        XmlData data = new XmlData();
        VposHeader header = new VposHeader("000000000000003", "oper0001", "12345678901234567890123452289000");
        ThreeDSAuthorizationRequest1 step1Data = new ThreeDSAuthorizationRequest1();
        step1Data.setThreeDSMtdComplInd("Y");
        step1Data.setThreeDSTransId("df4b3490-db44-4a88-9619-ab173ff76fbe");
        step1Data.setHeader(header);

        data.setThreeDSAuthorizationRequest1(step1Data);

        request.setData(data);

        return request;
    }

    private static BPWXmlRequest createStep2Request() {
        BPWXmlRequest request = new BPWXmlRequest();
        request.setRelease("02");
        request.setRequest(new Request3dsV2("THREEDSAUTHORIZATION2", "2015-02-08T12:02:00.000", "0dbfff7cab9cbdcd96fc854a1717221b8c5c31ca"));

        XmlData data = new XmlData();
        VposHeader header = new VposHeader("000000000000003", "oper0001", "12345678901234567890123452289000");
        ThreeDSAuthorizationRequest2 step2Data = new ThreeDSAuthorizationRequest2();
        step2Data.setThreeDSTransId("df4b3490-db44-4a88-9619-ab173ff76fbe");
        step2Data.setHeader(header);

        data.setThreeDSAuthorizationRequest2(step2Data);

        request.setData(data);

        return request;
    }
}
