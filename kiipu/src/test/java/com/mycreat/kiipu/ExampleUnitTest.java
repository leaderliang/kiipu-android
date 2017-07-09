package com.mycreat.kiipu;

import android.util.Log;

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
    @Test
    public void topics(){
        String template = "<!doctype html>\n" +
                "<html>\n" +
                "<head>\n" +
                "  <meta charset=\"utf-8\">\n" +
                "  <title>{{title}}</title>\n" +
                "  <meta name=\"viewport\" content=\"width=device-width,minimum-scale=1.0,maximum-scale=1.0,user-scalable=no,initial-scale=1,minimal-ui\">\n" +
                "  <style>\n" +
                "    html{line-height:1.15;-ms-text-size-adjust:100%;-webkit-text-size-adjust:100%;}body{margin:0;-webkit-tap-highlight-color: rgba(0,0,0,0);}article,aside,footer,header,nav,section{display:block;}h1{font-size:2em;margin:0.67em 0;}figcaption,figure,main{display:block;}figure{margin:1em 40px;}hr{box-sizing:content-box;height:0;overflow:visible;}pre{font-family:monospace,monospace;font-size:1em;}a{background-color:transparent;-webkit-text-decoration-skip:objects;}abbr[title]{border-bottom:none;text-decoration:underline;text-decoration:underline dotted;}b,strong{font-weight:inherit;}b,strong{font-weight:bolder;}code,kbd,samp{font-family:monospace,monospace;font-size:1em;}dfn{font-style:italic;}mark{background-color:#ff0;color:#000;}small{font-size:80%;}sub,sup{font-size:75%;line-height:0;position:relative;vertical-align:baseline;}sub{bottom:-0.25em;}sup{top:-0.5em;}audio,video{display:inline-block;}audio:not([controls]){display:none;height:0;}img{border-style:none;}svg:not(:root){overflow:hidden;}button,input,optgroup,select,textarea{font-family:sans-serif;font-size:100%;line-height:1.15;margin:0;}button,input{overflow:visible;}button,select{text-transform:none;}button,html [type=\"button\"],[type=\"reset\"],[type=\"submit\"]{-webkit-appearance:button;}button::-moz-focus-inner,[type=\"button\"]::-moz-focus-inner,[type=\"reset\"]::-moz-focus-inner,[type=\"submit\"]::-moz-focus-inner{border-style:none;padding:0;}button:-moz-focusring,[type=\"button\"]:-moz-focusring,[type=\"reset\"]:-moz-focusring,[type=\"submit\"]:-moz-focusring{outline:1px dotted ButtonText;}fieldset{padding:0.35em 0.75em 0.625em;}legend{box-sizing:border-box;color:inherit;display:table;max-width:100%;padding:0;white-space:normal;}progress{display:inline-block;vertical-align:baseline;}textarea{overflow:auto;}[type=\"checkbox\"],[type=\"radio\"]{box-sizing:border-box;padding:0;}[type=\"number\"]::-webkit-inner-spin-button,[type=\"number\"]::-webkit-outer-spin-button{height:auto;}[type=\"search\"]{-webkit-appearance:textfield;outline-offset:-2px;}[type=\"search\"]::-webkit-search-cancel-button,[type=\"search\"]::-webkit-search-decoration{-webkit-appearance:none;}::-webkit-file-upload-button{-webkit-appearance:button;font:inherit;}details,menu{display:block;}summary{display:list-item;}canvas{display:inline-block;}template{display:none;}[hidden]{display:none;}\n" +
                "  </style>\n" +
                "  <style>\n" +
                "    ul{margin:0;padding:0}li{list-style:none}a{text-decoration:none}h1,p{margin:0;padding:0}.wrap{padding:16px 20px;color:#262626}.tags{display:flex}.tags .tag{display:inline-block;height:30px;padding:0 12px;font-size:14px;line-height:30px;vertical-align:top;background:#eef4fa;border-radius:100px;margin:3px 5px 3px 0}.tags .tag a{color:#3e7ac2}.title{font-size:18px;margin-top:14px;margin-bottom:8px}.content{font-size:15px;line-height:25px}.actions{display:flex;margin-top:12px}.actions .btn{color:#0f88eb;border:1px solid #0f88eb;border-radius:3px;cursor:pointer;background:0;font-size:14px;text-align:center;line-height:30px;padding:0 12px}.actions .btn:hover{background-color:rgba(13,121,209,.06)}.actions .btn:active{background-color:rgba(13,121,209,.06)}\n" +
                "  </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "  <div class=\"wrap\">\n" +
                "    <ul class=\"tags\">\n" +
                "      {{#topics}}\n" +
                "        <li class=\"tag\">\n" +
                "          <a href=\"https://www.zhihu.com/topic/{{id}}\" target=\"_blank\">{{name}}</a>\n" +
                "        </li>\n" +
                "      {{/topics}}\n" +
                "    </ul>\n" +
                "    <h1 class=\"title\">{{title}}</h1>\n" +
                "    <p class=\"content\">\n" +
                "      {{note}}\n" +
                "    </p>\n" +
                "    <section class=\"actions\">\n" +
                "      <a class=\"btn\" href=\"{{url}}\" target=\"_blank\">查看问题</a>\n" +
                "    </section>\n" +
                "  </div>\n" +
                "</body>\n" +
                "</html>\n";
        String resultHtml = Mustache.compiler().compile(template).execute(new Object() {
            List<Topic> topics = Arrays.asList(new Topic(1, "topic1"), new Topic(2, "topic2"));
            String title = "topics";
            String note = "test";
            String url = "http://www.kiipu.com";
        });
        Log.d("Test", resultHtml);
        assertTrue(resultHtml.contains("topic1")
                && resultHtml.contains("topic2")
                && resultHtml.contains("http://www.kiipu.com")
                && resultHtml.contains("topics")
                && resultHtml.contains("test"));

    }

    class Topic {
        int id;
        String name;

        public Topic(int id, String name) {
            this.id = id;
            this.name = name;
        }
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