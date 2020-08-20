package org.devgateway.importtool.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.jpa.domain.AbstractPersistable;

/**
 * @author Octavian Ciubotaru
 */
@Entity
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Table(uniqueConstraints = @UniqueConstraint(name = "mt_uq", columnNames = {"srclang", "dstlang", "srctext"}))
public class MachineTranslation extends AbstractPersistable<Long> {

    @Column(length = 2, nullable = false)
    private String srcLang;

    @Column(length = 2, nullable = false)
    private String dstLang;

    @Column(nullable = false, columnDefinition = "text")
    private String srcText;

    @Column(nullable = false, columnDefinition = "text")
    private String dstText;

    public MachineTranslation() {
    }

    public MachineTranslation(String srcLang, String dstLang, String srcText, String dstText) {
        this.srcLang = srcLang;
        this.dstLang = dstLang;
        this.srcText = srcText;
        this.dstText = dstText;
    }

    public String getSrcLang() {
        return srcLang;
    }

    public void setSrcLang(String srcLang) {
        this.srcLang = srcLang;
    }

    public String getDstLang() {
        return dstLang;
    }

    public void setDstLang(String dstLang) {
        this.dstLang = dstLang;
    }

    public String getSrcText() {
        return srcText;
    }

    public void setSrcText(String srcText) {
        this.srcText = srcText;
    }

    public String getDstText() {
        return dstText;
    }

    public void setDstText(String dsrText) {
        this.dstText = dsrText;
    }
}
