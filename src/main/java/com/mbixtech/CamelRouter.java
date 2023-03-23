package com.mbixtech;

import org.apache.camel.CamelContext;
import org.apache.camel.LoggingLevel;
import org.apache.camel.RuntimeCamelException;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.stereotype.Component;

@Component
public class CamelRouter extends RouteBuilder {

    @Override
    public void configure() throws Exception {

        // @formatter:off

        // Configure context setting
        CamelContext context = this.getContext();
        context.setUseMDCLogging(true);

        onException(Exception.class, RuntimeCamelException.class)
                .handled(true)

                .log(LoggingLevel.ERROR, "Exception caught - Generic")
                // Set body to empty to prevent a case that body is NULL
                .setBody().simple("{\n" +
                "    \"code\": \"20210715\",\n" +
                "    \"message\": \"142703\",\n" +
                "    \"errors\": [{\n" +
                "        \"code\": \"9\",\n" +
                "        \"reason\": \"99\",\n" +
                "    }]\n" +
                "}")
                // Note: Log error response msg to requester
                .log(LoggingLevel.ERROR, "${body}")
        ;

        restConfiguration()
                .apiContextPath("/api-doc")
                .apiProperty("api.title", "Online CBS Bill Payment API")
                .apiProperty("api.version", "1.0")
                .apiProperty("cors", "true")
                .apiProperty("base.path", "v1")
                .apiProperty("api.path", "/")
                .apiContextRouteId("doc-api")
                .component("servlet")
                .bindingMode(RestBindingMode.off)
        ;

        rest("/test").description("Echo Test API")
                // For Pre-Payment
                .post("/echo")
                .to("direct:echoAPI")
//                .post("/authen")
//                .to("direct:authenAPI")
//                .post("/soap-authen")
//                .to("direct:authenSOAP")
        ;

        from("direct:echoAPI").description("Echo API").routeId("echoAPI")
                .streamCaching()

                // Log request msg from requester
                .log(LoggingLevel.INFO, "Request Message is : ${body}")
                .log(LoggingLevel.INFO, "Request Header is : ${headers}")
        ;
//        from("direct:authenAPI").description("Authen API").routeId("authenAPI")
//                .streamCaching()
//
//                // Log request msg from requester
//                .log(LoggingLevel.INFO, "Request Message is : ${body}")
//                .log(LoggingLevel.INFO, "Request Header is : ${headers}")
//
////                .unmarshal(jsonPaymentRq)
////                .bean(MockService.class, "mapRqToRs")
////                .marshal(jsonPaymentRs)
//        ;
//        from("direct:authenSOAP").description("Authen SOAP API").routeId("authenSOAP")
//                .streamCaching()
//
//                // Log request msg from requester
//                .log(LoggingLevel.INFO, "Request Message is : ${body}")
//                .log(LoggingLevel.INFO, "Request Header is : ${headers}")
//
//                .bean(MockService.class, "mapSoapRqToSoapRs")
//        ;

        // @formatter:on
    }
}
