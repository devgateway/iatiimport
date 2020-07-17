package org.devgateway.importtool.services;

import org.apache.commons.lang3.StringUtils;
import org.devgateway.importtool.dao.MachineTranslationRepository;
import org.devgateway.importtool.model.MachineTranslation;
import org.devgateway.importtool.services.processor.helper.ActionStatus;
import org.devgateway.importtool.services.processor.helper.ISourceProcessor;
import org.devgateway.importtool.services.processor.helper.InternalDocument;
import org.devgateway.importtool.services.processor.helper.Processor;
import org.devgateway.importtool.services.processor.helper.Translation;
import org.parboiled.common.ImmutableList;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;

/**
 * @author Octavian Ciubotaru
 */
@Component
public class ProjectTranslator {

    private final WorkflowService workflowService;

    private final MachineTranslationRepository machineTranslationRepository;

    private final ProjectTranslationSettings settings;

    private final TranslationClient translationClient;

    public ProjectTranslator(ProjectTranslationSettings settings,
            WorkflowService workflowService,
            MachineTranslationRepository machineTranslationRepository) {
        this.settings = settings;
        this.workflowService = workflowService;
        this.machineTranslationRepository = machineTranslationRepository;

        translationClient = new TranslationClient(settings.getUsername(), settings.getPassword());
    }

    public boolean isEnabled() {
        return settings.isEnabled();
    }

    public void translate(String xml) {
        try {
            translate(extractDocs(xml));
        } catch (RuntimeException e) {
            throw new RuntimeException("Failed to translate.", e);
        }
    }

    public void translate(List<InternalDocument> docs) {
        try {
            String srcLang = settings.getSourceLanguage();
            String dstLang = settings.getDestinationLanguage();

            Set<String> texts = extractTexts(docs, srcLang, dstLang).stream()
                    .map(StringUtils::normalizeSpace)
                    .collect(toSet());

            translate(srcLang, dstLang, texts);
        } catch (RuntimeException e) {
            throw new RuntimeException("Failed to translate.", e);
        }
    }

    private void translate(String srcLang, String dstLang, Set<String> texts) {
        if (texts.isEmpty()) {
            return;
        }

        Set<String> alreadyTranslated = machineTranslationRepository.find(srcLang, dstLang, texts).stream()
                .map(MachineTranslation::getSrcText)
                .collect(toSet());
        texts.removeAll(alreadyTranslated);

        if (texts.isEmpty()) {
            return;
        }

        Map<String, String> translatedTexts = translationClient.translate(texts, srcLang, dstLang);

        List<MachineTranslation> newTranslations = translatedTexts.entrySet().stream()
                .map(e -> new MachineTranslation(srcLang, dstLang, e.getKey(), e.getValue()))
                .collect(toList());

        machineTranslationRepository.saveAll(newTranslations);
    }


    private Set<String> extractTexts(List<InternalDocument> docs, String srcLang, String dstLang) {
        boolean inclDesc = isInclDesc();

        Set<String> texts = new HashSet<>();
        for (InternalDocument doc : docs) {
            texts.addAll(extractTextsFromDoc(doc, srcLang, dstLang, inclDesc));
        }

        return texts;
    }

    private boolean isInclDesc() {
        TranslationClient.TranslationConfig config = translationClient.getConfig();
        return config.getMaxChars() != null && config.getMaxChars() == 0;
    }

    private Set<String> extractTextsFromDoc(InternalDocument doc, String srcLang, String dstLang, boolean inclDesc) {
        Set<String> texts = new HashSet<>();
        List<String> mlFields = inclDesc ? ImmutableList.of("title", "description") : ImmutableList.of("title");
        for (String mlFieldName : mlFields) {
            Map<String, String> values = doc.getMultilangFields().get(mlFieldName);
            String srcLangValue = values.get(srcLang);
            String dstLangValue = values.get(dstLang);
            if (dstLangValue == null && srcLangValue != null) {
                texts.add(srcLangValue);
            }
        }
        return texts;
    }

    private List<InternalDocument> extractDocs(String xml) {
        byte[] bytes = xml.getBytes();

        return workflowService.getWorkflows().stream()
                .flatMap(w -> extractDocs(w.getSourceProcessor(), bytes).stream())
                .collect(toList());
    }

    private List<InternalDocument> extractDocs(Processor processorDesc, byte[] bytes) {
        ISourceProcessor processor;
        try {
            processor = (ISourceProcessor) Class.forName(processorDesc.getClassName()).newInstance();
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            throw new RuntimeException("Failed to instantiate source processor", e);
        }

        processor.setFromDataStore(true);
        processor.setInput(new ByteArrayInputStream(bytes));
        processor.setActionStatus(new ActionStatus());

        try {
            return processor.getDocuments();
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse documents.", e);
        }
    }

    public void loadTranslations(List<InternalDocument> docs) {
        String srcLang = settings.getSourceLanguage();
        String dstLang = settings.getDestinationLanguage();

        Set<String> texts = extractTexts(docs, srcLang, dstLang);

        if (texts.size() > 0) {
            Map<String, String> textsToNormalized = texts.stream()
                    .collect(toMap(Function.identity(), StringUtils::normalizeSpace));


            Map<String, String> translations =
                    machineTranslationRepository.find(srcLang, dstLang, textsToNormalized.values()).stream()
                            .collect(toMap(MachineTranslation::getSrcText, MachineTranslation::getDstText));

            for (InternalDocument doc : docs) {
                doc.setTranslations(extractTextsFromDoc(doc, srcLang, dstLang, true).stream()
                        .map(t -> new Translation(srcLang, dstLang, t, translations.get(textsToNormalized.get(t))))
                        .filter(t -> t.getDstText() != null)
                        .collect(toList()));
            }
        }
    }
}
