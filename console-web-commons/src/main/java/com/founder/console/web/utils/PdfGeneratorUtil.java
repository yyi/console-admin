package com.founder.console.web.utils;

import com.founder.Exception.OperationException;
import com.founder.exception.sysadmin.SysadminError;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.StopWatch;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;

import javax.annotation.Resource;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;


@Component
@Slf4j
public class PdfGeneratorUtil {

    @Autowired
    private TemplateEngine templateEngine;

    @Resource(name = "PdfTextRenderer")
    private GenericObjectPool<ITextRenderer> pdfTextRendererObjectPool;


    public byte[] createPdf(String templateName, Map map, String pdfFilePath) {
        Assert.notNull(templateName, "The templateName can not be null");
        Context ctx = new Context();
        if (map != null) {
            Iterator itMap = map.entrySet().iterator();
            while (itMap.hasNext()) {
                Map.Entry pair = (Map.Entry) itMap.next();
                ctx.setVariable(pair.getKey().toString(), pair.getValue());
            }
        }

        String processedHtml = templateEngine.process(templateName, ctx);
        ITextRenderer renderer = null;
        try {
            final File outputFile = new File(pdfFilePath);

            if (outputFile.exists()) {
                throw new IOException("文件已存在！");
            }

            ByteArrayOutputStream bos = new ByteArrayOutputStream(100 * 1024);

//            ITextRenderer renderer = new ITextRenderer();
//            ITextFontResolver fontResolver = renderer.getFontResolver();
//
//            fontResolver.addFont(sunFontPath, BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
            StopWatch stopWatch = new StopWatch();
            if (log.isDebugEnabled())
                stopWatch.start("初始化");
            renderer = pdfTextRendererObjectPool.borrowObject();

            renderer.setDocumentFromString(processedHtml);
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
            log.debug("PDF created successfully");
            byte[] data = bos.toByteArray();
            FileUtils.writeByteArrayToFile(outputFile, data);
            return data;
        } catch (Throwable e) {
            log.error("pdf文件生成失败", e);
            throw new OperationException(SysadminError.PdfFileGenerateError, e);
        } finally {
            if (!Objects.isNull(renderer))
                pdfTextRendererObjectPool.returnObject(renderer);

        }
    }
}