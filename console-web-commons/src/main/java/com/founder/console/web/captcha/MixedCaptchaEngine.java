package com.founder.console.web.captcha;

import com.octo.captcha.component.image.backgroundgenerator.BackgroundGenerator;
import com.octo.captcha.component.image.backgroundgenerator.UniColorBackgroundGenerator;
import com.octo.captcha.component.image.color.RandomListColorGenerator;
import com.octo.captcha.component.image.color.RandomRangeColorGenerator;
import com.octo.captcha.component.image.deformation.ImageDeformation;
import com.octo.captcha.component.image.deformation.ImageDeformationByFilters;
import com.octo.captcha.component.image.fontgenerator.FontGenerator;
import com.octo.captcha.component.image.fontgenerator.RandomFontGenerator;
import com.octo.captcha.component.image.textpaster.DecoratedRandomTextPaster;
import com.octo.captcha.component.image.textpaster.TextPaster;
import com.octo.captcha.component.image.textpaster.textdecorator.BaffleTextDecorator;
import com.octo.captcha.component.image.textpaster.textdecorator.TextDecorator;
import com.octo.captcha.component.image.wordtoimage.DeformedComposedWordToImage;
import com.octo.captcha.component.image.wordtoimage.WordToImage;
import com.octo.captcha.component.word.wordgenerator.RandomWordGenerator;
import com.octo.captcha.component.word.wordgenerator.WordGenerator;
import com.octo.captcha.engine.image.ListImageCaptchaEngine;
import com.octo.captcha.image.gimpy.GimpyFactory;

import java.awt.*;
import java.awt.image.ImageFilter;

public class MixedCaptchaEngine extends ListImageCaptchaEngine {
    private static final String CODE = "东南西北红中发123456789abcdefghjklmnopqrstuvwxyz一二三四五六七八九";

    private static Font font;

    //what's the fuck!ugly
    public static void setFont(Font fontParam) {
        font = fontParam;
    }

    @Override
    protected void buildInitialFactories() {

        // 图片和字体大小设置
        int minWordLength = 5;
        int fontSize = 18;
        int maxWordLength = 5;
        int imageWidth = 120;
        int imageHeight = 40;

        WordGenerator wordGenerator = new RandomWordGenerator(CODE);


        RandomRangeColorGenerator cgen = new RandomRangeColorGenerator(
                new int[]{0, 150}, new int[]{0, 150},
                new int[]{0, 150});
        TextDecorator[] textDecorators = new TextDecorator[1];
        textDecorators[0] = new BaffleTextDecorator(1, cgen);
        // word2image components
        TextPaster randomPaster = new DecoratedRandomTextPaster(minWordLength,
                maxWordLength, new RandomListColorGenerator(new Color[]{
                new Color(23, 170, 27), new Color(220, 34, 11),
                new Color(23, 67, 172)}), textDecorators);
        BackgroundGenerator background = new UniColorBackgroundGenerator(
                imageWidth, imageHeight, Color.white);
        Font[] fontsList = new Font[]{font};

        FontGenerator fontGenerator = new RandomFontGenerator(fontSize, fontSize,
                fontsList);

        ImageDeformation postDef = new ImageDeformationByFilters(
                new ImageFilter[]{});
        ImageDeformation backDef = new ImageDeformationByFilters(
                new ImageFilter[]{});
        ImageDeformation textDef = new ImageDeformationByFilters(
                new ImageFilter[]{});

        WordToImage word2image = new DeformedComposedWordToImage(fontGenerator,
                background, randomPaster, backDef, textDef, postDef);

        addFactory(new GimpyFactory(wordGenerator, word2image));
    }

}