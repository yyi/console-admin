package com.founder.console.web.utils;

import com.founder.Exception.OperationException;
import com.founder.exception.sysadmin.SysadminError;
import com.github.liaochong.myexcel.core.HtmlToExcelFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.poi.poifs.filesystem.DirectoryEntry;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.StopWatch;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;


@Component
@Slf4j
public class OfficeDocumentGeneratorUtil {

    public OfficeDocumentGeneratorUtil(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    @FunctionalInterface
    interface OfficeDocumentGenerator<T extends OutputStream> {
        void apply(String t, T u) throws Exception;
    }

    private final TemplateEngine templateEngine;

    @Resource(name = "PdfTextRenderer")
    private GenericObjectPool<ITextRenderer> pdfTextRendererObjectPool;


    private byte[] createOfficeDocument(String templateName, Map map, OfficeDocumentGenerator fun) {
        Assert.notNull(templateName, "The templateName can not be null");
        String processedHtml = getHtml(templateName, map);
        ByteArrayOutputStream bos = null;
        try {
            bos = new ByteArrayOutputStream();
            fun.apply(processedHtml, bos);
            return bos.toByteArray();
        } catch (Throwable e) {
            log.error("文件生成失败", e);
            throw new OperationException(SysadminError.PdfFileGenerateError, e);
        } finally {
            IOUtils.closeQuietly(bos);
        }
    }

    public byte[] createWordDoc(String templateName, Map map, String pdfFilePath) {
        return createOfficeDocument(templateName, map, this::createWordDocument);

    }

    public byte[] createExcel(String templateName, Map map, String pdfFilePath) {
        return createOfficeDocument(templateName, map, this::createExcelDocument);
    }


    public byte[] createPdf(String templateName, Map map, String pdfFilePath) {
        return createOfficeDocument(templateName, map, this::createPdfDocument);
    }

    private void createWordDocument(String html, OutputStream bos) throws Exception {
        POIFSFileSystem poifs = new POIFSFileSystem();
        DirectoryEntry directory = poifs.getRoot();
        directory.createDocument("文档名称", new ByteArrayInputStream(html.getBytes(StandardCharsets.UTF_8)));
        poifs.writeFilesystem(bos);
    }

    private void createExcelDocument(String html, OutputStream bos) throws Exception {
        Workbook workbook = HtmlToExcelFactory.readHtml(html).useDefaultStyle().build();
        workbook.write(bos);
    }

    private void createPdfDocument(String html, OutputStream bos) throws Exception {
        ITextRenderer renderer = null;
        try {
            StopWatch stopWatch = new StopWatch();
            if (log.isDebugEnabled())
                stopWatch.start("初始化");
            renderer = pdfTextRendererObjectPool.borrowObject();

            renderer.setDocumentFromString(html);
            renderer.layout();
            if (log.isDebugEnabled()) {
                stopWatch.stop();
                stopWatch.start("生成pdf");
            }
            renderer.createPDF(bos, false);
            renderer.finishPDF();
            if (log.isDebugEnabled()) {
                stopWatch.stop();
                log.debug(stopWatch.prettyPrint());
            }
        } finally {
            if (!Objects.isNull(renderer))
                pdfTextRendererObjectPool.returnObject(renderer);
        }
    }


    private String getHtml(String templateName, Map map) {
        Context ctx = new Context();
        if (map != null) {
            Iterator itMap = map.entrySet().iterator();
            while (itMap.hasNext()) {
                Map.Entry pair = (Map.Entry) itMap.next();
                ctx.setVariable(pair.getKey().toString(), pair.getValue());
            }
        }

        return templateEngine.process(templateName, ctx);
    }
}
