package com.founder.console.web.tag;

import com.founder.console.web.utils.WebUtils;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.model.*;
import org.thymeleaf.processor.element.AbstractElementTagProcessor;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.templatemode.TemplateMode;

import javax.servlet.http.HttpServletRequest;

public class CsrfElementProcessor extends AbstractElementTagProcessor {
    private static final String ELEMENT_NAME = "csrf";

    private static final int PRECEDENCE = 300;

    public CsrfElementProcessor(String dialectPrefix) {
        super(
                TemplateMode.HTML, // This processor will apply only to HTML mode
                dialectPrefix, // Prefix to be applied to name for matching
                ELEMENT_NAME, // Tag name: match specifically this tag
                true, // Apply dialect prefix to tag name
                null, // No attribute name: will match by tag name
                false, // No prefix to be applied to attribute name
                PRECEDENCE); // Precedence (inside dialect's own precedence)

    }

    @Override
    protected void doProcess(ITemplateContext context,
                             IProcessableElementTag iProcessableElementTag,
                             IElementTagStructureHandler structureHandler) {
        final IModelFactory modelFactory = context.getModelFactory();
        final IModel model = modelFactory.createModel();

        IOpenElementTag elementTag = modelFactory.createOpenElementTag("input");
        modelFactory.setAttribute(elementTag, "type", "hidden");
        modelFactory.setAttribute(elementTag, "name", "_csrf");
        modelFactory.setAttribute(elementTag, "value", "random");
        modelFactory.createCloseElementTag("input");
        HttpServletRequest request= WebUtils.getCurrentHttpRequest();
        structureHandler.replaceWith("<input type='hidden' value='12345' />",false);
    }
}
