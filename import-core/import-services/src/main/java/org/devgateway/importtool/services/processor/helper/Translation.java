package org.devgateway.importtool.services.processor.helper;

/**
 * @author Octavian Ciubotaru
 */
public class Translation {

    private final String srcLang;

    private final String dstLang;

    private final String srcText;

    private final String dstText;

    public Translation(String srcLang, String dstLang, String srcText, String dstText) {
        this.srcLang = srcLang;
        this.dstLang = dstLang;
        this.srcText = srcText;
        this.dstText = dstText;
    }

    public String getSrcLang() {
        return srcLang;
    }

    public String getDstLang() {
        return dstLang;
    }

    public String getSrcText() {
        return srcText;
    }

    public String getDstText() {
        return dstText;
    }
}
