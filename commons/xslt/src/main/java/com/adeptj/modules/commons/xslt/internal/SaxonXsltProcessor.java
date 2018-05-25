package com.adeptj.modules.commons.xslt.internal;

import com.adeptj.modules.commons.utils.Loggers;
import com.adeptj.modules.commons.xslt.XsltProcessingException;
import com.adeptj.modules.commons.xslt.XsltProcessor;
import net.sf.saxon.s9api.Processor;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.Serializer;
import net.sf.saxon.s9api.Xslt30Transformer;
import net.sf.saxon.s9api.XsltCompiler;
import net.sf.saxon.s9api.XsltExecutable;
import net.sf.saxon.s9api.XsltTransformer;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;

import javax.xml.transform.Source;
import java.lang.invoke.MethodHandles;

import static com.adeptj.modules.commons.xslt.XsltProcessor.XSLT_PROCESSOR_PROVIDER_SAXON;

@Designate(ocd = SaxonXsltProcessorConfig.class)
@Component(property = XSLT_PROCESSOR_PROVIDER_SAXON)
public class SaxonXsltProcessor implements XsltProcessor {

    private static final Logger LOGGER = Loggers.get(MethodHandles.lookup().lookupClass());

    private Processor processor;

    private SaxonXsltProcessorConfig config;

    @Override
    public <T> void process(Source xslSource, Source xmlSource, T outputTarget) {
        try {
            XsltCompiler compiler = this.processor.newXsltCompiler();
            XsltExecutable executable = compiler.compile(xslSource);
            Serializer serializer = this.processor.newSerializer();
            // serializer.setOutputWriter();
            if (this.config.loadXslt30Transformer()) {
                Xslt30Transformer transformer = executable.load30();
                transformer.applyTemplates(xmlSource, serializer);
            } else {
                XsltTransformer transformer = executable.load();
                transformer.setSource(xmlSource);
                transformer.setDestination(serializer);
                transformer.transform();
            }
        } catch (SaxonApiException ex) {
            LOGGER.error(ex.getMessage(), ex);
            throw new XsltProcessingException(ex);
        }
    }

    // INTERNAL

    @Activate
    protected void start(SaxonXsltProcessorConfig config) {
        this.config = config;
        this.processor = new Processor(this.config.licensedEdition());
    }
}
