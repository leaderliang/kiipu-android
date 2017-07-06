package com.mycreat.kiipu;

import com.mycreat.kiipu.utils.LogUtil;
import com.samskivert.mustache.Mustache;

import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void mustacheTest(){
        //测试来源 https://github.com/samskivert/jmustache
        String tmpl = "{{#persons}}{{name}}: {{age}}  {{nullValued}}  {{doesNotExist}} <br /> {{/persons}}\n";
        String resultHtml = Mustache.compiler().nullValue("NULL").defaultValue("DEFAULT").compile(tmpl).execute(new Object() {
            Object persons = Arrays.asList(new Person("Elvis", 75), new Person("Madonna", 52));
        });
        assertTrue(resultHtml.contains("DEFAULT"));

        resultHtml = Mustache.compiler().withFormatter(new Mustache.Formatter() {
            public String format (Object value) {
                if (value instanceof Date) return _fmt.format((Date)value);
                else return String.valueOf(value);
            }
            protected DateFormat _fmt = new SimpleDateFormat("yyyy/MM/dd");
        }).compile("{{msg}}: {{today}}").execute(new Object() {
            String msg = "Date";
            Date today = new Date();
        });
        assertTrue(resultHtml.matches(".+[0-9]+/[0-9]+/[0-9]+"));

        tmpl = "{{#things}}{{^-first}}, {{/-first}}{{this}}{{/things}}";
        resultHtml = Mustache.compiler().compile(tmpl).execute(new Object() {
            List<String> things = Arrays.asList("one", "two", "three");
        });

        assertTrue(resultHtml.equals("one, two, three"));

        tmpl = "My favorite things:\n{{#things}}{{-index}}. {{this}}\n{{/things}}";
        resultHtml = Mustache.compiler().compile(tmpl).execute(new Object() {
            List<String> things = Arrays.asList("Peanut butter", "Pen spinning", "Handstands");
        });

        assertTrue(resultHtml.matches("My favorite things:\n([1-3]\\.\\s+.+\n*.*)+"));

        //自动指定模板目录查找对应名称的模板
//        final File templateDir = ...;
//        Mustache.Compiler c = Mustache.compiler().withLoader(new Mustache.TemplateLoader() {
//            public Reader getTemplate (String name) {
//                return new FileReader(new File(templateDir, name));
//            }
//        });
//        String tmpl = "...{{>subtmpl}}...";
//        c.compile(tmpl).execute();
    }

    class Person {
        public final String name;
        String exists = "Say";
        String nullValued = null;
        // String doesNotExist
        public Person (String name, int age) {
            this.name = name;
            _age = age;
        }
        public int getAge () {
            return _age;
        }
        protected int _age;
    }


}