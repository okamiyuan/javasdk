package com.bestpay;


import com.bestpay.annotations.Comment;
import com.bestpay.dto.TestObjectReqDTO;
import com.bestpay.enums.EncyType;
import com.bestpay.enums.InstitutionTypeEnum;
import com.bestpay.enums.MapiVersionEnum;
import com.bestpay.enums.SignType;
import com.bestpay.handler.H5Handler;
import com.bestpay.handler.Sm2Handler;
import com.bestpay.handler.SvsHandler;
import com.bestpay.util.RequestSDK;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CountDownLatch;

/**
 * @Author youhao
 * @Date 2022-08-09
 **/
@SpringBootTest(classes = Application.class)
@RunWith(value = SpringRunner.class)
@Slf4j
public class RegTests {

    public String requestUrlHost = "https://mapi.test.bestpay.net";//综测公网
    //    public String requestUrlHost = "https://mapi-office.test.bestpay.net";//综测办公网
    public String urlPrefix = "/gapi";
    public String urlPrefixMapiOne = "/mapi";
    public String urlPrefixMapiOneMockRoute = "/gapi/mapi";

    public File guomiCerFile;
    public File guomiKeyFile;
    public File svsCerFile;
    public File svsKeyFile;

    @Before
    public void before() {
        try {
            guomiCerFile = new ClassPathResource("reg/guomi-key-cert/天翼电子商务有限公司.cer").getFile();
            guomiKeyFile = new ClassPathResource("reg/guomi-key-cert/安全中心国密算法验证.key").getFile();
            svsCerFile = new ClassPathResource("reg/svs-key-cert/zcc天翼电子商务有限公司.cer").getFile();
            svsKeyFile = new ClassPathResource("reg/svs-key-cert/zcc专属有限公司.P12").getFile();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void contextLoad() {
    }

    /*
    1.0
     */
    @Test
    public void doRequestMapiOne() {
        //私有参数
        Map<String, Object> paramsMap = prepareParamMap();

        //公有参数
        Map<String, Object> responseMap = new RequestSDK()
                .setAgreeId("20220802030100293866960491905048")
                .setRequestUrl(requestUrlHost + urlPrefixMapiOne + "/yh/test/base")
                .setMerchantNo("8562986202109020002")
                .setInstitutionType("")
                .setParamMap(paramsMap)
                .build()
                .doPost();
        log.info("responseMap:" + responseMap);
    }

    /*
    2.0
     */
    @Test
    public void doRequest() {
        //私有参数
        Map<String, Object> paramsMap = prepareParamMap();

        //公有参数
        Map<String, Object> responseMap = new RequestSDK()
                .setAgreeId("20220802030100293866960491905048")
                .setRequestUrl(requestUrlHost + urlPrefix + "/yh/test/base")
                .setMerchantNo("8562986202109020002")
                .setInstitutionType("")
                .setParamMap(paramsMap)
                .build()
                .doPost();
        log.info("responseMap:" + responseMap);
    }

    /*
    1.0模拟路由
     */
    @Test
    public void doRequestMapiOneMockRoute() {
        //私有参数
        Map<String, Object> paramsMap = prepareParamMap();

        //公有参数
        Map<String, Object> responseMap = new RequestSDK()
                .setAgreeId("20220802030100293866960491905048")
                .setRequestUrl(requestUrlHost + urlPrefixMapiOneMockRoute + "/yh/test/base")
                .setMerchantNo("8562986202109020002")
                .setInstitutionType("")
                .setParamMap(paramsMap)
                .build()
                .doPost();
        log.info("responseMap:" + responseMap);
    }


    /*
    svsSignGuomiUp
     */
    @Test
    public void testGuomiSign() {
        //私有参数
        Map<String, Object> paramsMap = prepareParamMap();

        //公有参数
        Map<String, Object> responseMap = new RequestSDK()
                .setAgreeId("4234235435435646")
                .setRequestUrl(requestUrlHost + urlPrefix + "/yh/test/sign")
//                .setRequestUrl("https://mapi.test.bestpay.net/gapi/zcc/httpEnc/svs//testInterface")
                .setParamMap(paramsMap)
                //明文返回为GUO_MI
                .setSignType(SignType.GUO_MI.getCode())
                .setMerchantNo("8562986202109020002")
                .setInstitutionType("")
                .setMerchantPrivateKeyFile(guomiKeyFile)
                .setBestPayPublicKeyFile(guomiCerFile)
                .setCasn("")
                .setLogEnable(Boolean.TRUE)
                .build()
                .doPost();
        log.info("responseMap:" + responseMap.toString());
    }

    /*
    svsSignUpDown
     */
    @Test
    public void testGuomiSignReturnSign() throws Exception {

        //私有参数
        Map<String, Object> paramsMap = prepareParamMap();

        RequestSDK requestSDK = new RequestSDK()
                .setAgreeId("4234235435435646")
//                .setRequestUrl("https://mapi.test.bestpay.net/gapi/zcc/httpEnc/svs//testInterface")
                .setRequestUrl(requestUrlHost + urlPrefix + "/yh/test/sign")
                .setParamMap(paramsMap)
                .setSignType(SignType.GUO_MI_RETURN.getCode())
                .setEncyType(null)
                .setMerchantNo("8562986202109020002")
                .setInstitutionType("")
//                .setMerchantPrivateKeyPath("C:\\Users\\尤浩/IdeaProjects/mapi-provider-test/mbp-mapi-consumer/src/main/resources/generalCertificate/安全中心国密算法验证.key")
//                .setBestPayPublicKeyPath("C:\\Users\\尤浩\\IdeaProjects\\mapi-provider-test\\mbp-mapi-consumer\\src\\main\\resources\\generalCertificate\\天翼电子商务有限公司.cer")
                .setMerchantPrivateKeyPath(guomiKeyFile.getAbsolutePath())
                .setBestPayPublicKeyPath(guomiCerFile.getAbsolutePath())
                .setCasn("")
                .build();
        Map<String, Object> responseMap = requestSDK
                .doPost();
        log.info("responseMap:" + responseMap.toString());
        Sm2Handler.verify(responseMap, requestSDK);
    }

    /*
    svsEncGuomiUp
     */
    @Test
    public void testGuomiEnc() {
        //私有参数
        Map<String, Object> paramsMap = prepareParamMap();

        RequestSDK requestSDK = new RequestSDK()
                .setAgreeId("20210727030100159454368297386051")
                .setRequestUrl(requestUrlHost + urlPrefix + "/yh/test/encry")
                .setParamMap(paramsMap)
                //明文返回为GUO_MI
                .setEncyType(EncyType.GUO_MI.getCode())
                .setMerchantNo("8562986202109020002")
                .setInstitutionType("MERCHANT")
                .setAesIV("WUNnZWFxZGpnZmc=")
                .setCasn("")
//                .setMerchantPrivateKeyPath("C:\\Users\\尤浩/IdeaProjects/mapi-provider-test/mbp-mapi-consumer/src/main/resources/generalCertificate/安全中心国密算法验证.key")
//                .setBestPayPublicKeyPath("C:\\Users\\尤浩\\IdeaProjects\\mapi-provider-test\\mbp-mapi-consumer\\src\\main\\resources\\generalCertificate\\天翼电子商务有限公司.cer")
                .setMerchantPrivateKeyFile(guomiKeyFile)
                .setBestPayPublicKeyFile(guomiCerFile)
                .setLogEnable(Boolean.TRUE)
                .build();
        Map<String, Object> responseMap = requestSDK
                .doPost();
        log.info("responseMap:" + responseMap.toString());
    }

    /*
    svsEncGuoUpDown
     */
    @Test
    public void testGuomiEncReturnEnc() throws Exception {
        //私有参数
        Map<String, Object> paramsMap = prepareParamMap();

        RequestSDK requestSDK = new RequestSDK()
                .setAgreeId("20210727030100159454368297386051")
//                .setRequestUrl("https://mapi.test.bestpay.net/gapi/zcc/httpEnc/svs/smxtestInterface")
                .setRequestUrl(requestUrlHost + urlPrefix + "/yh/test/encry")
                .setParamMap(paramsMap)
                .setEncyType(EncyType.GUO_MI_RETURN.getCode())
                .setMerchantNo("8562986202109020002")
                .setInstitutionType("MERCHANT")
                .setAesIV("WUNnZWFxZGpnZmc=")
                .setCasn("")
//                .setMerchantPrivateKeyPath("C:\\Users\\尤浩/IdeaProjects/mapi-provider-test/mbp-mapi-consumer/src/main/resources/generalCertificate/安全中心国密算法验证.key")
//                .setBestPayPublicKeyPath("C:\\Users\\尤浩\\IdeaProjects\\mapi-provider-test\\mbp-mapi-consumer\\src\\main\\resources\\generalCertificate\\天翼电子商务有限公司.cer")
                .setMerchantPrivateKeyFile(guomiKeyFile)
                .setBestPayPublicKeyFile(guomiCerFile)
                .build();
        Map<String, Object> responseMap = requestSDK
                .doPost();
        log.info("responseMap:" + responseMap.toString());
        Map<String, Object> resultMap = (Map<String, Object>) responseMap.get("result");
        Sm2Handler.decryptBySmx(resultMap, requestSDK);
    }

    /**
     * svsSignUp
     */
    @Test
    public void testSvsSignUp() {
        //私有参数
        Map<String, Object> paramsMap = prepareParamMap();
        //公有参数
        Map<String, Object> responseMap = new RequestSDK()
                .setAgreeId("4234235435435646")
                .setRequestUrl(requestUrlHost + urlPrefix + "/yh/test/sign/noNestedObject")
                .setParamMap(paramsMap)
                .setSignType(SignType.SVS.getCode())
                .setMerchantNo("2021888888")
                .setInstitutionType(InstitutionTypeEnum.EMPTY.getCode())
                .setMerchantPrivateKeyFile(svsKeyFile)
                .setBestPayPublicKeyFile(svsCerFile)
                .setMerchantPrivateKeyAllis("conname")
                .setMerchantPrivateKeyPassword("41714772")
                .setCasn("")
                .setLogEnable(Boolean.TRUE)
                .build()
                .doPost();
        log.info("responseMap:" + responseMap.toString());
    }

    /**
     * svsSignUoDown
     */
    @Test
    public void testSvsSignWithReturnSign() throws Exception {
        //私有参数
        Map<String, Object> paramsMap = prepareParamMap();
        //公有参数
        RequestSDK requestSDK = new RequestSDK()
                .setAgreeId("4234235435435646")
                .setRequestUrl(requestUrlHost + urlPrefix + "/yh/test/sign/noNestedObject")
                .setParamMap(paramsMap)
                .setSignType(SignType.SVS_RETURN.getCode())
                .setMerchantNo("2021888888")
                .setInstitutionType(InstitutionTypeEnum.EMPTY.getCode())
                .setMerchantPrivateKeyFile(svsKeyFile)
                .setBestPayPublicKeyFile(svsCerFile)
                .setMerchantPrivateKeyAllis("conname")
                .setMerchantPrivateKeyPassword("41714772")
                .setCasn("")
                .setLogEnable(Boolean.TRUE)
                .build();
        Map<String, Object> responseMap = requestSDK
                .doPost();
        log.info("responseMap:" + responseMap.toString());
        SvsHandler.verify(responseMap, requestSDK);
    }
    /**
     * svsSignUpDown-责任链
     */
//    @Test
//    public void testSvsSignWithReturnSignProxyChain() throws Exception {
//        //私有参数
//        Map<String, Object> paramsMap = prepareParamMap();
//        paramsMap.put("protocolModeEnum","2021888888");
//        //公有参数
//        RequestSDK requestSDK = new RequestSDK()
//                .setAgreeId("4234235435435646")
//                .setRequestUrl(requestUrlHost + urlPrefix + "/yh/test/sign/proxyChain")
//                .setParamMap(paramsMap)
//                .setSignType(SignType.SVS_RETURN.getCode())
//                .setMerchantNo("2021888888")
//                .setInstitutionType(InstitutionTypeEnum.MERCHANT.getCode())
//                .setMerchantPrivateKeyFile(svsKeyFile)
//                .setBestPayPublicKeyFile(svsCerFile)
//                .setMerchantPrivateKeyAllis("conname")
//                .setMerchantPrivateKeyPassword("41714772")
//                .setCasn("")
//                .setLogEnable(Boolean.TRUE)
//                .build();
//        Map<String, Object> responseMap = requestSDK
//                .doPost();
//        log.info("responseMap:" + responseMap.toString());
//        SvsHandler.verify(responseMap, requestSDK);
//    }

    /**
     * svsSign-责任链
     */
    @Test
    public void testSvsSignProxyChain() {
        //私有参数
        Map<String, Object> paramsMap = prepareParamMap();
        //公有参数
        RequestSDK requestSDK = new RequestSDK()
                .setAgreeId("4234235435435646")
                .setRequestUrl(requestUrlHost + urlPrefix + "/yh/test/sign/proxyChain")
                .setParamMap(paramsMap)
                .setSignType(SignType.SVS.getCode())
                .setMerchantNo("2021888888")
                .setInstitutionType(InstitutionTypeEnum.EMPTY.getCode())
                .setMerchantPrivateKeyFile(svsKeyFile)
                .setBestPayPublicKeyFile(svsCerFile)
                .setMerchantPrivateKeyAllis("conname")
                .setMerchantPrivateKeyPassword("41714772")
                .setCasn("")
                .setLogEnable(Boolean.TRUE)
                .build();
        Map<String, Object> responseMap = requestSDK
                .doPost();
        log.info("responseMap:" + responseMap.toString());
    }

    /**
     * svsEnc
     */
    @Test
    public void testSvsEnc() {
        //私有参数
        Map<String, Object> paramsMap = prepareParamMap();
        //公有参数
        RequestSDK requestSDK = new RequestSDK()
                .setAgreeId("4234235435435646")
                .setRequestUrl(requestUrlHost + urlPrefix + "/yh/test/encry")
                .setParamMap(paramsMap)
                .setEncyType(EncyType.SVS.getCode())
                .setMerchantNo("2021888888")
                .setInstitutionType(InstitutionTypeEnum.MERCHANT.getCode())
                .setMerchantPrivateKeyFile(svsKeyFile)
                .setBestPayPublicKeyFile(svsCerFile)
                .setMerchantPrivateKeyAllis("conname")
                .setMerchantPrivateKeyPassword("41714772")
                .setAesIV("bVR0V2VPWXBCanc=")
                .setCasn("")
                .setLogEnable(Boolean.TRUE)
                .build();
        Map<String, Object> responseMap = requestSDK
                .doPost();
        log.info("responseMap:" + responseMap.toString());
    }

    /**
     * svsEncUpDown
     */
    @Test
    public void testSvsEncWithReturnEnc() throws Exception {
        //私有参数
        Map<String, Object> paramsMap = prepareParamMap();
        //公有参数
        RequestSDK requestSDK = new RequestSDK()
                .setAgreeId("4234235435435646")
                .setRequestUrl(requestUrlHost + urlPrefix + "/yh/test/encry")
                .setParamMap(paramsMap)
                .setEncyType(EncyType.SVS_RETURN.getCode())
                .setMerchantNo("2021888888")
                .setInstitutionType(InstitutionTypeEnum.MERCHANT.getCode())
                .setMerchantPrivateKeyFile(svsKeyFile)
                .setBestPayPublicKeyFile(svsCerFile)
                .setMerchantPrivateKeyAllis("conname")
                .setMerchantPrivateKeyPassword("41714772")
                .setAesIV("bVR0V2VPWXBCanc=")
                .setCasn("")
                .setLogEnable(Boolean.TRUE)
                .build();
        Map<String, Object> responseMap = requestSDK
                .doPost();
        log.info("responseMap:" + responseMap.toString());
        Map<String, Object> resultMap = (Map<String, Object>) responseMap.get("result");
        SvsHandler.decrypt(resultMap, requestSDK);
    }

    /**
     * 请求前加上headers
     */
    @Test
    public void doWithHeaders() {
        //私有参数
        Map<String, Object> paramsMap = prepareParamMap();
        //公有参数
        Map<String, Object> responseMap = new RequestSDK()
                .setAgreeId("4234235435435646")
                .setRequestUrl(requestUrlHost + urlPrefix + "/yh/test/sign")
//                .setRequestUrl("https://mapi.test.bestpay.net/gapi/zcc/httpEnc/svs//testInterface")
                .setParamMap(paramsMap)
                //明文返回为GUO_MI
                .setSignType(SignType.GUO_MI.getCode())
                .setMerchantNo("8562986202109020002")
                .setInstitutionType("")
                .setMerchantPrivateKeyFile(guomiKeyFile)
                .setBestPayPublicKeyFile(guomiCerFile)
                .setCasn("")
//                .setMerchantPrivateKeyPath("C:\\Users\\尤浩/IdeaProjects/mapi-provider-test/mbp-mapi-consumer/src/main/resources/generalCertificate/安全中心国密算法验证.key")
//                .setBestPayPublicKeyPath("C:\\Users\\尤浩\\IdeaProjects\\mapi-provider-test\\mbp-mapi-consumer\\src\\main\\resources\\generalCertificate\\天翼电子商务有限公司.cer")
                .setLogEnable(Boolean.TRUE)
                .build()
                .doPost(new HashMap<>(), 5000, 5000);
        log.info("responseMap:" + responseMap.toString());
    }


    @Comment("默认参数测试")
    @Test
    public void testDefaultParams() {
        //私有参数
        HashMap<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("date", "2022-10-20 34:23:33");
        paramsMap.put("string", "yh1997");
        paramsMap.put("booleanNum", "true");
        paramsMap.put("floatNum", 5.0f);

        //公有参数
        Map<String, Object> responseMap = new RequestSDK()
                .setAgreeId("20220802030100293866960491905048")
                .setRequestUrl(requestUrlHost + urlPrefix + "/yh/test/defaultParams")
                .setMerchantNo("8562986202109020002")
                .setInstitutionType("")
                .setParamMap(paramsMap)
                .setSignType(SignType.GUO_MI.getCode())
                .setMerchantPrivateKeyPath(guomiKeyFile.getAbsolutePath())
                .setBestPayPublicKeyPath(guomiCerFile.getAbsolutePath())
                .build()
                .doPost();
        log.info("responseMap:" + responseMap);
    }


    /**
     * H5上行-获取加密因子
     */
    @Test
    public void testApplyLoginFactor() {
        //私有参数
        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("productNo", "18206889179");

        //公有参数
        Map<String, Object> responseMap = new RequestSDK()
                .setAgreeId("20220802030100293866960491905048")
                .setRequestUrl(requestUrlHost + urlPrefix + "/mapi-gateway/applyLoginFactor")
                .setMerchantNo("8562986202109020002")
                .setInstitutionType("")
                .setParamMap(paramsMap)
                .build()
                .doPost();
        log.info("responseMap:" + responseMap);
    }

    /**
     * H5Up
     */
    @Test
    public void testh5UpEnc() {
        //私有参数
        Map<String, Object> paramsMap = prepareParamMap();

        //公有参数
        Map<String, Object> responseMap = new RequestSDK()
                .setAgreeId("20220802030100293866960491905048")
                .setRequestUrl(requestUrlHost + urlPrefix + "/yh/test/encry/h5")
                .setProductNo("18206889179")
                .setApplyLoginFactorRequestUrl(requestUrlHost + urlPrefix + "/mapi-gateway/applyLoginFactor")
                .setParamMap(paramsMap)
                .setEncyType(EncyType.H5.getCode())
                .setLogEnable(Boolean.TRUE)
                .build()
                .doPost();
        log.info("responseMap:" + responseMap);
    }

    /**
     * H5Up-1.0
     */
    @Test
    public void testh5EncMapiOne() {
        //私有参数
        Map<String, Object> paramsMap = prepareParamMap();

        //公有参数
        Map<String, Object> responseMap = new RequestSDK()
                .setAgreeId("20220802030100293866960491905048")
                .setRequestUrl(requestUrlHost + urlPrefixMapiOne + "/yh/test/encry/h5")
                .setProductNo("18206889179")
//                .setApplyLoginFactorRequestUrl(requestUrlHost + urlPrefix + "/mapi-gateway/applyLoginFactor")
                .setApplyLoginFactorRequestUrl(requestUrlHost + urlPrefix + "/api/applyLoginFactor")
                .setParamMap(paramsMap)
                .setEncyType(EncyType.H5.getCode())
                .setLogEnable(Boolean.TRUE)
                .build()
                .doPost();
        log.info("responseMap:" + responseMap);
    }

    /**
     * h5UpDown
     */
    @Test
    public void testH5UpDown() throws Exception {
        //私有参数
        Map<String, Object> paramsMap = prepareParamMap();

//        //公有参数
        RequestSDK requestSDK = new RequestSDK()
                .setAgreeId("20220802030100293866960491905048")
                .setOpenId("18206889179")
                .setRequestUrl(requestUrlHost + urlPrefix + "/yh/test/encry/h5")
                .setAuthGetPublicKeyRequestUrl(requestUrlHost + urlPrefix + "/mapi-gateway/auth/getPublicKey")
                .setAuthGetAesKeyRequestUrl(requestUrlHost + urlPrefix + "/mapi-gateway/auth/getAesKey")
                .setParamMap(paramsMap)
                .setEncyType(EncyType.H5_RETURN.getCode())
                .setLogEnable(Boolean.TRUE)
                .build();
        Map<String, Object> responseMap = requestSDK
                .doPost();
        log.info("responseMap:" + responseMap);
        Map<String, Object> resMap = H5Handler.decrypt(responseMap, requestSDK);
        log.info("resMap:{}", resMap);
    }

    /**
     * h5UpDown-1.0
     */
    @Test
    public void testh5UpDownMapiOne() throws Exception {
        //私有参数
        Map<String, Object> paramsMap = prepareParamMap();

//        //公有参数
        RequestSDK requestSDK = new RequestSDK()
                .setAgreeId("20220802030100293866960491905048")
                .setOpenId("18206889179")
                .setRequestUrl(requestUrlHost + urlPrefixMapiOne + "/yh/test/encry/h5/updown")
                .setAuthGetPublicKeyRequestUrl(requestUrlHost + urlPrefixMapiOne + "/auth/getPublicKey")
                .setAuthGetAesKeyRequestUrl(requestUrlHost + urlPrefixMapiOne + "/auth/getAesKey")
                .setParamMap(paramsMap)
                .setEncyType(EncyType.H5_RETURN.getCode())
                .setLogEnable(Boolean.TRUE)
                .build();
        Map<String, Object> responseMap = requestSDK
                .doPost();
        log.info("responseMap:" + responseMap);
        Map<String, Object> resMap = H5Handler.decrypt(responseMap, requestSDK);
        log.info("resMap:{}", resMap);
    }

    /**
     * svsEnc-1.0
     */
//    @Test
//    public void testSvsEncMapiOne() throws Exception {
//        //私有参数
//        Map<String, Object> paramsMap = prepareParamMap();
//        //公有参数
//        RequestSDK requestSDK = new RequestSDK()
//                .setAgreeId("4234235435435646")
//                .setRequestUrl(requestUrlHost + urlPrefixMapiOne + "/yh/test/svs/encry")
//                .setParamMap(paramsMap)
//                .setEncyType(EncyType.SVS_PROXY_CHAIN.getCode())
//                .setMerchantNo("2021888888")
//                .setInstitutionType(InstitutionTypeEnum.MERCHANT.getCode())
//                .setMerchantPrivateKeyFile(svsKeyFile)
//                .setBestPayPublicKeyFile(svsCerFile)
//                .setMerchantPrivateKeyAllis("conname")
//                .setMerchantPrivateKeyPassword("41714772")
//                .setAesIV("bVR0V2VPWXBCanc=")
//                .setCasn("")
//                .setLogEnable(Boolean.TRUE)
//                .setMapiVersion(MapiVersionEnum.Mapi.getValue())
//    .setRequestWithsignTypeOrEncType(false)
//                .build();
//        Map<String, Object> responseMap = requestSDK
//                .doPost();
//        log.info("responseMap:" + responseMap.toString());
//        Map<String, Object> resultMap = (Map<String, Object>) responseMap.get("result");
//        Map<String, Object> decryptedRes = SvsHandler.decrypt(resultMap, requestSDK);
//        log.info("decryptedRes:{}",decryptedRes);
//    }

    /**
     * svsEnc-责任链
     */
    @Test
    public void testSvsEncProxyChain() throws Exception {
        //私有参数
        Map<String, Object> paramsMap = prepareParamMap();
        //公有参数
        RequestSDK requestSDK = new RequestSDK()
                .setAgreeId("4234235435435646")
                .setRequestUrl(requestUrlHost + urlPrefix + "/yh/test/encry/proxyChain")
                .setParamMap(paramsMap)
                .setEncyType(EncyType.SVS_PROXY_CHAIN.getCode())
                .setMerchantNo("2021888888")
                .setInstitutionType(InstitutionTypeEnum.MERCHANT.getCode())
                .setMerchantPrivateKeyFile(svsKeyFile)
                .setBestPayPublicKeyFile(svsCerFile)
                .setMerchantPrivateKeyAllis("conname")
                .setMerchantPrivateKeyPassword("41714772")
                .setAesIV("bVR0V2VPWXBCanc=")
                .setCasn("")
                .setLogEnable(Boolean.TRUE)
                .build();
        Map<String, Object> responseMap = requestSDK
                .doPost();
        log.info("responseMap:" + responseMap.toString());
    }

    /**
     * svsSignUp-1.0
     * 发现1.0使用的是merchantNo,而非signMerchantNo  而且优先级以institutionCode为最高
     */
    @Test
    public void testSvsSignUpMapiOne() {
        //私有参数
        Map<String, Object> paramsMap = prepareParamMap();
        //公有参数
        Map<String, Object> responseMap = new RequestSDK()
                .setAgreeId("44444444444444444444")
                .setRequestUrl(requestUrlHost + urlPrefixMapiOne + "/yh/test/svs/signUp")
                .setParamMap(paramsMap)
                .setSignType(SignType.SVS.getCode())
                .setMerchantNo("2021888888")
                .setInstitutionType(InstitutionTypeEnum.MERCHANT.getCode())
                .setMerchantPrivateKeyFile(svsKeyFile)
                .setBestPayPublicKeyFile(svsCerFile)
                .setMerchantPrivateKeyAllis("conname")
                .setMerchantPrivateKeyPassword("41714772")
                .setCasn("")
                .setLogEnable(Boolean.TRUE)
                .setMapiVersion(MapiVersionEnum.Mapi.getValue())
                .setRequestWithsignTypeOrEncType(false)
                .build()
                .doPost();
        log.info("responseMap:" + responseMap.toString());

    }

    /**
     * svs-signUp-1.0-模拟路由
     */
    @Test
    public void testSvsSignMapiOwnMockRoute() {
        //私有参数
        Map<String, Object> paramsMap = prepareParamMap();
        //公有参数
        Map<String, Object> responseMap = new RequestSDK()
                .setAgreeId("4234235435435646")
                .setRequestUrl(requestUrlHost + urlPrefixMapiOneMockRoute + "/yh/test/svsSignUp/route")
                .setParamMap(paramsMap)
                .setSignType(SignType.SVS.getCode())
                .setMerchantNo("2021888888")
                .setInstitutionType(InstitutionTypeEnum.MERCHANT.getCode())
                .setMerchantPrivateKeyFile(svsKeyFile)
                .setBestPayPublicKeyFile(svsCerFile)
                .setMerchantPrivateKeyAllis("conname")
                .setMerchantPrivateKeyPassword("41714772")
                .setCasn("")
                .setLogEnable(Boolean.TRUE)
                .setMapiVersion(MapiVersionEnum.Mapi.getValue())
                .setRequestWithsignTypeOrEncType(false)
                .build()
                .doPost();
        log.info("responseMap:" + responseMap.toString());

    }
    /**
     * svsSignUp-1.0-路由
     */
    @Test
    public void testSvsSignUpMapiOneRoute() {
        //私有参数
        Map<String, Object> paramsMap = prepareParamMap();
        //公有参数
        Map<String, Object> responseMap = new RequestSDK()
                .setAgreeId("44444444444444444444")
                .setRequestUrl(requestUrlHost + urlPrefixMapiOne + "/yh/test/svs/signUp/route")
                .setParamMap(paramsMap)
                .setSignType(SignType.SVS.getCode())
                .setMerchantNo("2021888888")
                .setInstitutionType(InstitutionTypeEnum.MERCHANT.getCode())
                .setMerchantPrivateKeyFile(svsKeyFile)
                .setBestPayPublicKeyFile(svsCerFile)
                .setMerchantPrivateKeyAllis("conname")
                .setMerchantPrivateKeyPassword("41714772")
                .setCasn("")
                .setLogEnable(Boolean.TRUE)
                .setMapiVersion(MapiVersionEnum.Mapi.getValue())
                .setRequestWithsignTypeOrEncType(false)
                .build()
                .doPost();
        log.info("responseMap:" + responseMap.toString());

    }

    /**
     * svsSignUpDown-1.0
     * merchantNo不同、
     * protocolModeEnum不同、和2.0责任链是一样的 但是和非责任链不同
     * wrapper不同 和2.0责任链是一样的 但是和非责任链不同
     * translateResultDataSort()不同
     * institutionType在上行验签的时候不同 2.0只允许传空或者企业类
     * date序列化处理不同
     */
    @Test
    public void testSvsSignWithReturnSignMapiOne() throws Exception {
        //私有参数
        Map<String, Object> paramsMap = prepareParamMap();
        paramsMap.put("protocolModeEnum", "2021888888");
        //公有参数
        RequestSDK requestSDK = new RequestSDK()
                .setAgreeId("4234235435435646")
                .setRequestUrl(requestUrlHost + urlPrefixMapiOne + "/yh/test/svs/signUpDown")
                .setParamMap(paramsMap)
                .setSignType(SignType.SVS_RETURN.getCode())
                .setMerchantNo("2021888888")
                .setInstitutionType(InstitutionTypeEnum.MERCHANT.getCode())
                .setMerchantPrivateKeyFile(svsKeyFile)
                .setBestPayPublicKeyFile(svsCerFile)
                .setMerchantPrivateKeyAllis("conname")
                .setMerchantPrivateKeyPassword("41714772")
                .setCasn("")
                .setMapiVersion(MapiVersionEnum.Mapi.getValue())
                .setRequestWithsignTypeOrEncType(false)
                .setLogEnable(Boolean.TRUE)
                .build();
        Map<String, Object> responseMap = requestSDK
                .doPost();
        log.info("responseMap:" + responseMap.toString());
        SvsHandler.verify(responseMap, requestSDK);
    }
    /**
     * svsSignUpDown-1.0-模拟路由
     */
    @Test
    public void testSvsSignUpDownMapiOneMockRoute() throws Exception {
        //私有参数
        Map<String, Object> paramsMap = prepareParamMap();
        paramsMap.put("protocolModeEnum", "2021888888");
        //公有参数
        RequestSDK requestSDK = new RequestSDK()
                .setAgreeId("4545454545454")
                .setRequestUrl(requestUrlHost + urlPrefixMapiOneMockRoute + "/yh/test/svsSignUpDown/route")
//                .setRequestUrl(requestUrlHost + urlPrefixMapiOneMockRoute + "/commonService/queryRebateByPage")
                .setParamMap(paramsMap)
                .setSignType(SignType.SVS_RETURN.getCode())
                .setMerchantNo("2021888888")
                .setInstitutionType(InstitutionTypeEnum.MERCHANT.getCode())
                .setMerchantPrivateKeyFile(svsKeyFile)
                .setBestPayPublicKeyFile(svsCerFile)
                .setMerchantPrivateKeyAllis("conname")
                .setMerchantPrivateKeyPassword("41714772")
                .setCasn("")
                .setMapiVersion(MapiVersionEnum.Mapi.getValue())
                .setRequestWithsignTypeOrEncType(false)
                .setRequestWithsignTypeOrEncType(false)
                .setLogEnable(Boolean.TRUE)
                .build();
        Map<String, Object> responseMap = requestSDK
                .doPost();
        log.info("responseMap:" + responseMap.toString());
        SvsHandler.verify(responseMap, requestSDK);
    }
    /**
     * svsSignUpDown-1.0-路由
     */
    @Test
    public void testSvsSignWithReturnSignMapiOneRoute() throws Exception {
        //私有参数
        Map<String, Object> paramsMap = prepareParamMap();
        paramsMap.put("protocolModeEnum", "2021888888");
        //公有参数
        RequestSDK requestSDK = new RequestSDK()
                .setAgreeId("4234235435435646")
                .setRequestUrl(requestUrlHost + urlPrefixMapiOne + "/yh/test/svs/signUpDown/route")
                .setParamMap(paramsMap)
                .setSignType(SignType.SVS_RETURN.getCode())
                .setMerchantNo("2021888888")
                .setInstitutionType(InstitutionTypeEnum.MERCHANT.getCode())
                .setMerchantPrivateKeyFile(svsKeyFile)
                .setBestPayPublicKeyFile(svsCerFile)
                .setMerchantPrivateKeyAllis("conname")
                .setMerchantPrivateKeyPassword("41714772")
                .setCasn("")
                .setMapiVersion(MapiVersionEnum.Mapi.getValue())
                .setRequestWithsignTypeOrEncType(false)
                .setLogEnable(Boolean.TRUE)
                .build();
        Map<String, Object> responseMap = requestSDK
                .doPost();
        log.info("responseMap:" + responseMap.toString());
        SvsHandler.verify(responseMap, requestSDK);
    }

    /**
     * svsEncUpDown-责任链
     */
//    @Test
//    public void testSvsEncWithReturnEncProxyChain() throws Exception {
//        //私有参数
//        Map<String, Object> paramsMap = prepareParamMap();
//        //公有参数
//        RequestSDK requestSDK = new RequestSDK()
//                .setAgreeId("4234235435435646")
//                .setRequestUrl(requestUrlHost + urlPrefix + "/yh/test/encry/proxyChain")
//                .setParamMap(paramsMap)
//                .setEncyType(EncyType.SVS_RETURN_PROXY_CHAIN.getCode())
//                .setMerchantNo("2021888888")
//                .setInstitutionType(InstitutionTypeEnum.MERCHANT.getCode())
//                .setMerchantPrivateKeyFile(svsKeyFile)
//                .setBestPayPublicKeyFile(svsCerFile)
//                .setMerchantPrivateKeyAllis("conname")
//                .setMerchantPrivateKeyPassword("41714772")
//                .setAesIV("bVR0V2VPWXBCanc=")
//                .setCasn("")
//                .setLogEnable(Boolean.TRUE)
//                .build();
//        Map<String, Object> responseMap = requestSDK
//                .doPost();
//        log.info("responseMap:" + responseMap.toString());
//        Map<String, Object> resultMap = (Map<String, Object>) responseMap.get("result");
//        Map<String, Object> decryptedRes = SvsHandler.decrypt(resultMap, requestSDK);
//        log.info("decryptedRes:{}",decryptedRes);
//    }
    @Test
    public void testConcurrent() throws InterruptedException {
        int concurrentNum = 50;
        CountDownLatch countDownLatch = new CountDownLatch(concurrentNum);
        for (int i = 0; i < concurrentNum; i++) {
            new Thread(() -> {
                try {
                    testH5UpDown();
                } catch (Exception e) {
                    log.error("exception when test,e=", e);
                } finally {
                    countDownLatch.countDown();
                }
            }).start();
        }
        countDownLatch.await();
    }

    private Map<String, Object> prepareParamMap() {
        //私有参数
        Map<String, Object> paramsMap = new TreeMap<>();
//        paramsMap.put("date", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        paramsMap.put("string", "fefwefewfefewf");
        paramsMap.put("booleanNum", null);
        paramsMap.put("floatNum", 322423.0f);
        TestObjectReqDTO testObjectReqDTO = new TestObjectReqDTO();
        testObjectReqDTO.setDate(new Date());
        testObjectReqDTO.setString("今天如给热个人g后突然好天气好");
        paramsMap.put("testObjectReqDTO", testObjectReqDTO);

        List<TestObjectReqDTO> testObjectReqDTOList = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            TestObjectReqDTO e = new TestObjectReqDTO();
            e.setDate(new Date());
            testObjectReqDTOList.add(e);
        }
        paramsMap.put("testObjectReqDTOList", testObjectReqDTOList);

        ArrayList<String> strings = new ArrayList<>(5);
        strings.add("1");
        strings.add("2");
        strings.add("3");
        paramsMap.put("stringList", strings);
        Map<String, Object> stringObjectHashMap = new HashMap<>();
        stringObjectHashMap.put("regret", 666);
        stringObjectHashMap.put("string in map", "22");
        stringObjectHashMap.put("list in map", Arrays.asList(1, 2, 3));
        stringObjectHashMap.put("date in map", new Date());
        stringObjectHashMap.put("null in map", null);
        paramsMap.put("map", stringObjectHashMap);
        return paramsMap;
    }

    /**
     * 动态参数-add
     */
    @Test
    public void testAdd(){
        //私有参数
        Map<String, Object> paramsMap = prepareParamMap();
        //公有参数
        Map<String, Object> responseMap = new RequestSDK()
                .setAgreeId("44444444433344444")
                .setRequestUrl(requestUrlHost + urlPrefixMapiOne + "/yh/test/add")
                .setParamMap(paramsMap)
                .setSignType(SignType.SVS.getCode())
                .setMerchantNo("2021888888")
                .setInstitutionType(InstitutionTypeEnum.MERCHANT.getCode())
                .setMerchantPrivateKeyFile(svsKeyFile)
                .setBestPayPublicKeyFile(svsCerFile)
                .setMerchantPrivateKeyAllis("conname")
                .setMerchantPrivateKeyPassword("41714772")
                .setCasn("")
                .setLogEnable(Boolean.TRUE)
                .setMapiVersion(MapiVersionEnum.Mapi.getValue())
                .setRequestWithsignTypeOrEncType(false)
                .build()
                .doPost();


        log.info("responseMap:" + responseMap.toString());
    }
    /**
     * 动态参数-add-1.0
     */
    @Test
    public void testAddMapiOne(){
        //私有参数
        Map<String, Object> paramsMap = prepareParamMap();
        //公有参数
        Map<String, Object> responseMap = new RequestSDK()
                .setAgreeId("4234235435435646")
                .setRequestUrl(requestUrlHost + urlPrefixMapiOne + "/yh/test/add")
                .setParamMap(paramsMap)
                .setSignType(SignType.SVS.getCode())
                .setMerchantNo("2021888888")
                .setInstitutionType(InstitutionTypeEnum.MERCHANT.getCode())
                .setMerchantPrivateKeyFile(svsKeyFile)
                .setBestPayPublicKeyFile(svsCerFile)
                .setMerchantPrivateKeyAllis("conname")
                .setMerchantPrivateKeyPassword("41714772")
                .setCasn("")
                .setLogEnable(Boolean.TRUE)
                .setMapiVersion(MapiVersionEnum.Mapi.getValue())
                .setRequestWithsignTypeOrEncType(false)
                .build()
                .doPost();
        log.info("responseMap:" + responseMap.toString());
    }
    /**
     * 动态参数-requestIp
     */
    @Test
    public void testRequestIp(){
        //私有参数
        Map<String, Object> paramsMap = prepareParamMap();
        //公有参数
        Map<String, Object> responseMap = new RequestSDK()
                .setAgreeId("4441156345655444")
                .setRequestUrl(requestUrlHost + urlPrefix + "/yh/test/requestIp")
                .setParamMap(paramsMap)
                .setSignType(SignType.SVS.getCode())
                .setMerchantNo("2021888888")
                .setInstitutionType(InstitutionTypeEnum.EMPTY.getCode())
                .setMerchantPrivateKeyFile(svsKeyFile)
                .setBestPayPublicKeyFile(svsCerFile)
                .setMerchantPrivateKeyAllis("conname")
                .setMerchantPrivateKeyPassword("41714772")
                .setCasn("")
                .setLogEnable(Boolean.TRUE)
                .setRequestWithsignTypeOrEncType(false)
                .build()
                .doPost();


        log.info("responseMap:" + responseMap.toString());
    }
    /**
     * 动态参数-requestIp-1.0
     */
    @Test
    public void testRequestIpMapiOne(){
        //私有参数
        Map<String, Object> paramsMap = prepareParamMap();
        //公有参数
        Map<String, Object> responseMap = new RequestSDK()
                .setAgreeId("44444334433355444")
                .setRequestUrl(requestUrlHost + urlPrefixMapiOne + "/yh/test/requestIp")
                .setParamMap(paramsMap)
                .setSignType(SignType.SVS.getCode())
                .setMerchantNo("2021888888")
                .setInstitutionType(InstitutionTypeEnum.MERCHANT.getCode())
                .setMerchantPrivateKeyFile(svsKeyFile)
                .setBestPayPublicKeyFile(svsCerFile)
                .setMerchantPrivateKeyAllis("conname")
                .setMerchantPrivateKeyPassword("41714772")
                .setCasn("")
                .setLogEnable(Boolean.TRUE)
                .setRequestWithsignTypeOrEncType(false)
                .setMapiVersion(MapiVersionEnum.Mapi.getValue())
                .build()
                .doPost();


        log.info("responseMap:" + responseMap.toString());
    }
    /**
     * 动态参数-wrap
     */
    @Test
    public void testWrap(){
        //私有参数
        Map<String, Object> paramsMap = prepareParamMap();
        //公有参数
        Map<String, Object> responseMap = new RequestSDK()
                .setAgreeId("4441999999655444")
                .setRequestUrl(requestUrlHost + urlPrefix + "/yh/test/wrap")
                .setParamMap(paramsMap)
                .setSignType(SignType.SVS.getCode())
                .setMerchantNo("2021888888")
                .setInstitutionType(InstitutionTypeEnum.EMPTY.getCode())
                .setMerchantPrivateKeyFile(svsKeyFile)
                .setBestPayPublicKeyFile(svsCerFile)
                .setMerchantPrivateKeyAllis("conname")
                .setMerchantPrivateKeyPassword("41714772")
                .setCasn("")
                .setLogEnable(Boolean.TRUE)
                .setRequestWithsignTypeOrEncType(false)
                .build()
                .doPost();


        log.info("responseMap:" + responseMap.toString());
    }
    /**
     * 动态参数-wrap-1.0
     */
    @Test
    public void testWrapMapiOne(){
        //私有参数
        Map<String, Object> paramsMap = prepareParamMap();
        //公有参数
        Map<String, Object> responseMap = new RequestSDK()
                .setAgreeId("44444332222444")
                .setRequestUrl(requestUrlHost + urlPrefixMapiOne + "/yh/test/wrap")
                .setParamMap(paramsMap)
                .setSignType(SignType.SVS.getCode())
                .setMerchantNo("2021888888")
                .setInstitutionType(InstitutionTypeEnum.MERCHANT.getCode())
                .setMerchantPrivateKeyFile(svsKeyFile)
                .setBestPayPublicKeyFile(svsCerFile)
                .setMerchantPrivateKeyAllis("conname")
                .setMerchantPrivateKeyPassword("41714772")
                .setCasn("")
                .setLogEnable(Boolean.TRUE)
                .setRequestWithsignTypeOrEncType(false)
                .setMapiVersion(MapiVersionEnum.Mapi.getValue())
                .build()
                .doPost();


        log.info("responseMap:" + responseMap.toString());
    }


    @Test
    public void testEquals() {
        String s1 = "{result={\"string\":\"ergre热狗如果如果让他trhtrhrthtr\",\"floatNum\":322423.0,\"testObjectReqDTO\":{\"date\":\"2022-10-28T11:28:10\",\"string\":\"今天如给热个人g后突然好天气好\",\"class\":\"com.bestpay.mbp.api.out.index.service.api.model.request.test.TestObjectReqDTO\"}}, success=true, errorCode=null, errorMsg=null}";


        String s2 = "{result={\"string\":\"ergre热狗如果如果让他trhtrhrthtr\",\"floatNum\":322423.0,\"testObjectReqDTO\":{\"date\":\"2022-10-28T11:26:56\",\"string\":\"今天如给热个人g后突然好天气好\",\"class\":\"com.bestpay.mbp.api.out.index.service.api.model.request.test.TestObjectReqDTO\"}}, success=true, errorCode=null, errorMsg=null}";

        log.info("eq:{}", s1.equals(s2));
    }

}
