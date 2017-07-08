package com.mycreat.kiipu.db;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.mycreat.kiipu.db.bookmark.model.Template;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.mycreat.kiipu.db.test", appContext.getPackageName());
    }

    /**
     * 测试模板数据库增删改查
     */
    @Test
    public void template(){
        Context appContext = InstrumentationRegistry.getTargetContext();
        //存储
        KiipuDBUtils.Companion.saveTemplate(appContext, new Template(0L, "zhihu_question", "", "", 1, ""));

        //查询
        List<Template> templates = KiipuDBUtils.Companion.getTemplate(appContext, "zhihu_question");
        assertEquals(templates.size(), 1);

        //更新
        Template template1 = templates.get(0);
        assertEquals(template1.getName(), "zhihu_question");
        template1.setUrl("tmpl");
        template1.setVersion_code(2);
        KiipuDBUtils.Companion.updateTemplate(appContext, templates.get(0));
        Template template2 = KiipuDBUtils.Companion.getTemplate(appContext, "zhihu_question").get(0);
        Template template3 = KiipuDBUtils.Companion.getTemplate(appContext, template1.getId()).get(0);
        assertEquals(template1.getName(), template3.getName());
        assertEquals(template2.getName(), template3.getName());
        assertNotEquals(template1.getVersion_code(), template2.getVersion_code());
        assertNotEquals(template1.getVersion_code(), template3.getVersion_code());

        //删除
        KiipuDBUtils.Companion.delTemplate(appContext, template3.getId());
        assertTrue(KiipuDBUtils.Companion.getTemplate(appContext, "zhihu_question").size() == 0);
    }
}
