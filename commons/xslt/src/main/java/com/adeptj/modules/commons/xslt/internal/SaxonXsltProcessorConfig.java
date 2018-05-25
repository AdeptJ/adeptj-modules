package com.adeptj.modules.commons.xslt.internal;

import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "AdeptJ Saxon XSLT Processor Config")
@interface SaxonXsltProcessorConfig {

    boolean licensedEdition();

    boolean loadXslt30Transformer();
}
