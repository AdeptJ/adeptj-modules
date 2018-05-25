package com.adeptj.modules.commons.xslt;

import org.osgi.annotation.versioning.ProviderType;

import javax.xml.transform.Source;

@ProviderType
public interface XsltProcessor {

    String XSLT_PROCESSOR_PROVIDER_JDK = "xslt.processor.provider=jdk";

    String XSLT_PROCESSOR_PROVIDER_SAXON = "xslt.processor.provider=saxon";

    <T> void process(Source xslSource, Source xmlSource, T outputTarget);
}
