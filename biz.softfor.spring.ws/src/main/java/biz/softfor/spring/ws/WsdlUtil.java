package biz.softfor.spring.ws;

import java.io.FileOutputStream;
import java.io.OutputStream;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.ws.wsdl.wsdl11.DefaultWsdl11Definition;
import org.springframework.ws.wsdl.wsdl11.Wsdl11Definition;
import org.springframework.xml.transform.TransformerFactoryUtils;
import org.springframework.xml.xsd.XsdSchemaCollection;
import org.springframework.xml.xsd.commons.CommonsXsdSchemaCollection;

public class WsdlUtil {

  public static DefaultWsdl11Definition wsdl11Definition(
    XsdSchemaCollection xsds
  , String location
  , String portTypeName
  , String targetNamespace
  ) {
    DefaultWsdl11Definition result = new DefaultWsdl11Definition();
    result.setLocationUri(location);
    result.setPortTypeName(portTypeName);
    result.setTargetNamespace(targetNamespace);
    result.setSchemaCollection(xsds);
    return result;
  }

  public static void wsdl2Output(Wsdl11Definition wsdlDef, OutputStream os)
  throws TransformerConfigurationException, TransformerException {
    Transformer tsfr = TransformerFactoryUtils.newInstance().newTransformer();
    tsfr.setOutputProperty(OutputKeys.INDENT, "yes");
    DOMResult domResult = new DOMResult();
    tsfr.transform(wsdlDef.getSource(), domResult);
    DOMSource domSource = new DOMSource(domResult.getNode());
    tsfr.transform(domSource, new StreamResult(os));
  }

  public static void xsd2Wsdl(
    String wsdlPath
  , String location
  , String portType
  , String targetNamespace
  , String... xsdPath
  ) throws Exception {
    Resource[] rs = new Resource[xsdPath.length];
    for(int i = 0; i < xsdPath.length; ++i) {
      rs[i] = new FileSystemResource(xsdPath[i]);
    }
    CommonsXsdSchemaCollection sc = new CommonsXsdSchemaCollection(rs);
    sc.setInline(true);
    sc.afterPropertiesSet();
    DefaultWsdl11Definition wsdlDef
    = wsdl11Definition(sc, location, portType, targetNamespace);
    wsdlDef.afterPropertiesSet();
    wsdl2Output(wsdlDef, new FileOutputStream(wsdlPath));
  }

}
