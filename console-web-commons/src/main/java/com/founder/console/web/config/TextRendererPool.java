package com.founder.console.web.config;

import com.lowagie.text.pdf.BaseFont;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.xhtmlrenderer.pdf.ITextFontResolver;
import org.xhtmlrenderer.pdf.ITextRenderer;

public class TextRendererPool  extends BasePooledObjectFactory<ITextRenderer> {

     private  String fontPath;

    public TextRendererPool(String fontPath) {
        this.fontPath=fontPath;
    }

    @Override
    public ITextRenderer create() throws Exception {
        ITextRenderer renderer = new ITextRenderer();
        ITextFontResolver fontResolver = renderer.getFontResolver();

        fontResolver.addFont(fontPath, BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
        return renderer;
    }

    @Override
    public PooledObject<ITextRenderer> wrap(ITextRenderer iTextRenderer) {
        return new DefaultPooledObject<>(iTextRenderer);

    }

    @Override
    public void passivateObject(PooledObject<ITextRenderer> p) throws Exception {
        p.getObject().finishPDF();
    }
}
