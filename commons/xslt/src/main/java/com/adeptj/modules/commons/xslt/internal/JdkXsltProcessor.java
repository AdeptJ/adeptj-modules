package com.adeptj.modules.commons.xslt.internal;

import com.adeptj.modules.commons.utils.Loggers;
import com.adeptj.modules.commons.xslt.XsltProcessingException;
import com.adeptj.modules.commons.xslt.XsltProcessor;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import java.lang.invoke.MethodHandles;

import static com.adeptj.modules.commons.xslt.XsltProcessor.XSLT_PROCESSOR_PROVIDER_JDK;

@Designate(ocd = JdkXsltProcessorConfig.class)
@Component(property = XSLT_PROCESSOR_PROVIDER_JDK)
public class JdkXsltProcessor implements XsltProcessor {

    private static final Logger LOGGER = Loggers.get(MethodHandles.lookup().lookupClass());

    private String xsl;

    private TransformerFactory transformerFactory;

    @Override
    public <T> void process(Source xslSource, Source xmlSource, T outputTarget) {
        try {
            if (outputTarget instanceof Result) {
                this.transformerFactory.newTransformer(xslSource).transform(xmlSource, (Result) outputTarget);
                return;
            }
            throw new IllegalStateException("outputTarget argument must be an instance of javax.xml.process.Result");
        } catch (TransformerException ex) {
            LOGGER.error(ex.getMessage(), ex);
            throw new XsltProcessingException(ex);
        }
    }

    // INTERNAL

    @Activate
    protected void start(JdkXsltProcessorConfig config) {
        this.transformerFactory = TransformerFactory.newInstance();
        this.transformerFactory.setAttribute(null, null);
        this.xsl = "ajjjsj";
    }
}
