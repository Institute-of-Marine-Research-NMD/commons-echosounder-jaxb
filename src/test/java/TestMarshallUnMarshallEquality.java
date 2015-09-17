
import java.io.File;
import java.io.IOException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 *
 * @author kjetilf
 */
public class TestMarshallUnMarshallEquality {

    private File tempFile;

    @Before
    public void before() throws JAXBException, IOException {
        tempFile = File.createTempFile("test", ".xml");
        JAXBContext jc = JAXBContext.newInstance("no.imr.nmdapi.generic.nmdechosounder.domain.luf20");
        Unmarshaller u = jc.createUnmarshaller();
        u.setEventHandler(
            new ValidationEventHandler() {
                public boolean handleEvent(ValidationEvent event ) {
                    throw new RuntimeException(event.getMessage(),
                                       event.getLinkedException());
            }
        });
        Object o = u.unmarshal(Thread.currentThread().getContextClassLoader().getResourceAsStream("luf20_2010205.xml"));
        Marshaller marshall = jc.createMarshaller();
        marshall.marshal(o, tempFile);
    }

    @Test
    public void testEq() throws ParserConfigurationException, SAXException, IOException, XPathExpressionException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document preXML = builder.parse(Thread.currentThread().getContextClassLoader().getResourceAsStream("luf20_2010205.xml"));
        Document postXML = builder.parse(tempFile);

        XPathFactory xpathfactory = XPathFactory.newInstance();
        XPath xpath = xpathfactory.newXPath();

        XPathExpression exprCountDist = xpath.compile("count(//*[local-name()='distance'])");
        XPathExpression exprCountFreq = xpath.compile("count(//*[local-name()='frequency'])");
        XPathExpression exprCountSa = xpath.compile("count(//*[local-name()='sa'])");


        Object preResultDistance = exprCountDist.evaluate(preXML, XPathConstants.NUMBER);
        Object postResultDistance = exprCountDist.evaluate(postXML, XPathConstants.NUMBER);

        Object preResultFreq = exprCountFreq.evaluate(preXML, XPathConstants.NUMBER);
        Object postResultFreq = exprCountFreq.evaluate(postXML, XPathConstants.NUMBER);

        Object preResultSa = exprCountSa.evaluate(preXML, XPathConstants.NUMBER);
        Object postResultSa = exprCountSa.evaluate(postXML, XPathConstants.NUMBER);

        assertEquals(preResultDistance, postResultDistance);
        assertEquals(preResultFreq, postResultFreq);
        assertEquals(preResultSa, postResultSa);
    }

    @After
    public void after()  {
        tempFile.delete();
    }


}
