package org.devgateway.importtool.dao;

import java.util.Collection;
import java.util.List;

import javax.transaction.Transactional;

import org.devgateway.importtool.model.MachineTranslation;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

/**
 * @author Octavian Ciubotaru
 */
@Transactional
public interface MachineTranslationRepository extends CrudRepository<MachineTranslation, Long> {

    @Query("from MachineTranslation "
            + "where srcLang=:srcLang "
            + "and dstLang=:dstLang "
            + "and srcText in :srcTexts")
    List<MachineTranslation> find(
            @Param("srcLang") String srcLang,
            @Param("dstLang") String dstLang,
            @Param("srcTexts") Collection<String> srcTexts);
}
