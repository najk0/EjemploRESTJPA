
import data.Article;

import net.sf.classifier4J.summariser.SimpleSummariser;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;

public class SplitTest {


    String text1 = "Hay controversia sobre su fiabilidad y precisión.18 La revista científica" +
            " Nature declaró en ?diciembre de 2005 que la Wikipedia. en inglés.... era casi tan exacta en " +
            "artículos científicos! como la Encyclopaedia Britannica.19 El estudio .   se realizó " +
            "comparando 42.5 artículos de.5 ambas obras por un comité de. expertos sin." +
            " que estos supieran " +
            "de cuál de. las dos enciclopedias provenían. ";


    String text2 ="Neutralidad del contenido[editar]\n" +
            "Wikipedia es un proyecto dirigido a —literalmente— todo el mundo y editable por —literalmente— cualquier persona. Esta característica constituye una de sus mayores ventajas, pero también genera un problema: existen personas que introducen —deliberadamente o no— información parcial, bien creando un planteamiento sesgado, o bien omitiendo puntos de vista que no comparten o que no les interesan.77\n" +
            "La política que se encarga de combatir dicho problema es el Punto de vista neutral, que, básicamente, establece la necesidad absoluta e innegociable de reunir en los artículos susceptibles de polémica todos los puntos de vista significativos. Además, las licencias de contenido libre garantizan que dicho contenido pueda ser reeditado cuantas veces sea necesario y por cualquier persona si el propósito de la edición es la mejora. Según Jimmy Wales, uno de los fundadores de Wikipedia, la colaboración produce efectos positivos y ampliamente aceptados.78\n" +
            "Para el ingeniero informático formado en filosofía Joaquín Siabra Fraile, Wikipedia es primero un conjunto de reglas y procedimientos y, solo después, contenidos, las reglas son un mecanismo virtual diseñado para conseguir unos contenidos fruto del consenso racional. Para Siabra se dan en el wikipedista lo que Habermas consideradaba como criterios del participante en una situación ideal de comunicación: verdad, rectitud y veracidad.79 80\n" +
            "Relevancia del contenido[editar]\n" +
            "Wikipedia, debido a su condición de enciclopedia electrónica sin ánimo de lucro y en crecimiento permanente tanto en lo referente a su contenido como a su número de editores —cuya inmensa mayoría colabora de forma altruista—, admite información que no tendría cabida en una enciclopedia convencional, limitada esta última por el espacio físico —número de tomos— en el que se confina dicha información, por el número de editores contratados por la editorial y por el tiempo dedicado a confeccionar la obra. Sin embargo, no toda la información tiene cabida, y existen criterios de relevancia establecidos por consenso comunitario, de tal modo que, aunque no se rechaza a priori ningún artículo, se investigan las nuevas creaciones y se descartan las que no cumplen determinados requisitos. Por ejemplo, no se admiten los artículos autopromocionales; esto es, que ninguna persona puede publicar un artículo sobre sí misma, sobre una persona cercana —familiar, por ejemplo— o sobre la empresa en la que trabaja. Cuando el tema goza de una mínima —y necesaria— relevancia, llega a captar la atención de —cuando menos— algún medio de comunicación —libro, revista, periódico— reputado, y es a estos últimos a los que, según las normas de Wikipedia, debe acudirse para crear el artículo. Estos criterios están contemplados en varias políticas, a saber, «Páginas de autopromoción»,81 «Criterios para el borrado rápido»,82 «Wikipedia no es una fuente primaria»,83 «Verificabilidad»84 y «Fuentes fiables».85\n" +
            "Según declaraciones oficiosas de un «bibliotecario» —administrador— colombiano de la Wikipedia en español, los «bibliotecarios» de esta Wikipedia eliminan más artículos que los de otras versiones de Wikipedia.86\n" +
            "Licencia de contenido[editar]\n" +
            " \n" +
            "\n" +
            "Icono de CC-BY-SA, que simboliza a Creative Commons Atribution Share-Alike —en español, atribución y compartir la licencia similar de Creative Commons—.\n" +
            "El contenido textual está bajo las licencias GNU y Creative Commons; la última versión se actualizó mediante una votación entre el 12 de abril y el 3 de mayo de 2009.87 Mediante el lema La enciclopedia libre se entendía, al modo del software libre, como un producto de distribución gratuita y sin restricciones. Así también, se considera software por la codificación de MediaWiki, bajo GPL. Esta redistribución presenta el requisito de que debe ser acreditada, es decir, debe mencionarse su atribución, ya que la licencia incluye Share-Alike; además, es necesario conservar esta licencia —u otra similar— evitando su protección de distribución.88 Sin embargo, se había estado trabajando en el cambio a licencias Creative Commons, porque la GFDL, inicialmente diseñada para manuales de software, no es adecuada para trabajos de referencia en línea y porque las dos licencias son incompatibles.89\n" +
            "A cada autor, editor o ilustrador que contribuye a la enciclopedia siempre se le atribuyen los derechos de autor según la convención de Berna.90 Un autor también puede copiar contenido de otro con el permiso correspondiente —sobre todo en el caso de contenido con licencia libre—, sin embargo no podrá usarse un contenido que prohíbe su distribución, reproducción o modificación de una manera ilícita, porque ello limitaría su utilización con seguridad y acarrearía problemas con las políticas. Por ello, en la Wikipedia en inglés puede aparecer cualquier imagen ilustrativa siempre que lo haga de manera lícita.91\n" +
            "A excepción del contenido y varias de sus características, el isologo de esta marca corporativa está protegido, por lo que no se permite su distribución sin previa autorización.";

    String text3 ="Algunos proyectos, como el de la Stanford Encyclopedia of Philosophy, o el de la ahora abandonada Nupedia, se basan en políticas editoriales tradicionales y de autoría de artículos tipo «escritura por expertos». Ocasionalmente aparecen sitios web como h2g2 o everything2 que siguen unas pautas generales preestablecidas, donde los artículos solo pueden ser redactados y controlados por cada persona de forma individual.49\n" +
            "Contrariamente, proyectos como Wikipedia, Susning.nu o la Enciclopedia Libre son wikis en los que los artículos son desarrollados por numerosos autores, y no existe un criterio de revisión formal. Wikipedia es la enciclopedia más grande en cuanto a número de artículos o palabras jamás escrita. Al contrario de lo que sucede con muchas otras, su contenido está liberado bajo licencias de contenido abierto.\n" +
            "Wikipedia dispone de un conjunto de políticas y convenciones que sirven para decidir qué información debe o no incluirse.50 Estas políticas se utilizan para resolver disputas relativas a la creación, edición y borrado de artículos, así como a la transferencia de estos a un proyecto hermano por no tratarse de información enciclopédica (véase la sección Fundación Wikimedia y los proyectos hermanos).\n" +
            "Cultura[editar]" +
            "Un ejemplo de una guerra de ediciones en el artículo España.\n" +
            "La cultura de la sociedad ha variado, según el estado, en cada versión. En el caso principal, la Wikipedia en español cualquier persona tiene la posibilidad de crear un artículo nuevo y casi cualquier visitante puede editar el contenido, a excepción de los artículos que se encuentran protegidos. Sin embargo, en la inglesa los usuarios no registrados no pueden comenzar artículos desde cero. Wikipedia fue creada con la idea de producir textos de calidad a partir de la colaboración entre usuarios, a semejanza de los proyectos de desarrollo de aplicaciones libres.\n" +
            "Los artículos evolucionan con el paso del tiempo, y esto es visible en su historial de ediciones. Habitualmente, una parte de las ediciones son vandálicas —de contenido no relacionado con Wikipedia o con información falsa—, y en ocasiones editores con puntos de vista encontrados producen lo que se conoce como guerra de ediciones. Esta se produce cuando dos o más editores entran en un ciclo de reversiones mutuas debido a disputas causadas por diferencias de opinión sobre el contenido del artículo. No hay que confundir vandalismo —que frecuentemente afecta una sola vez a un artículo o artículos— con guerra de ediciones, la cual afecta repetidas veces a un mismo artículo en un breve lapso. Entre los artículos vandalizados frecuentemente en la edición en español destacan: George W. Bush, Benedicto XVI o Testigos de Jehová; mientras que artículos con fuertes guerras de ediciones son Cuba o Comunidad Valenciana, debido a la disparidad entre las opiniones de sus redactores.\n" +
            "Cada capítulo de Wikipedia cuenta con un grupo de personal, encargado en la cooperación. Dentro de esta lista se mencionan a administradores, cuyas funciones fundamentales son hacer mantenimiento —tal como borrar artículos, bloquear vándalos y otras funciones— y estar al servicio del cumplimiento de las normas que la rigen. El capítulo con más administradores es la Wikipedia en inglés, con un total de más de 1600.";


    String testIn1 = "";
    String testIn2 = ".";
    String testIn3 = "";
    String testIn4 = "";
    String testIn5 = "";
    String testIn6 = "";
    String testIn7 = "";

    Article article = new Article();


    @Test
    public void test() throws Exception{

        String text = FileUtils.readFileToString(new File("C:\\articulo.txt"), "UTF-8");
        SimpleSummariser summariser = new SimpleSummariser();
        String result = summariser.summarise(text , 2);

        text2 = summariser.summarise(text2 , 2);
        text3 = summariser.summarise(text3 , 3);

        Assert.assertTrue(article.spliter("").size()==0);
        Assert.assertTrue(article.spliter("    ").size()==0);
        Assert.assertTrue(article.spliter(".").size()==0);
        Assert.assertTrue(article.spliter("..").size()==1);
        Assert.assertTrue(article.spliter("..").get(0).equals(".."));
        Assert.assertTrue(article.spliter("...").size()==1);
        Assert.assertTrue(article.spliter("...").get(0).equals("..."));
        Assert.assertTrue(article.spliter(".    .").size()==0);
        Assert.assertTrue(article.spliter("  .   .  ").size()==0);

        Assert.assertTrue(article.spliter("a").size()==1);
        Assert.assertTrue(article.spliter("a").get(0).equals("a"));

        Assert.assertTrue(article.spliter("a.").size()==1);
        Assert.assertTrue(article.spliter("a.").get(0).equals("a."));

        Assert.assertTrue(article.spliter(".a").size()==1);
        Assert.assertTrue(article.spliter(".a").get(0).equals("a"));

        Assert.assertTrue(article.spliter(".a.").size()==1);
        Assert.assertTrue(article.spliter(".a.").get(0).equals("a."));

        Assert.assertTrue(article.spliter(".a .").size()==1);
        Assert.assertTrue(article.spliter(".a .").get(0).equals("a ."));

        Assert.assertTrue(article.spliter(".a. ").size()==1);
        Assert.assertTrue(article.spliter(".a. ").get(0).equals("a."));

        Assert.assertTrue(article.spliter(".a.b").size()==2);
        Assert.assertTrue(article.spliter(".a.b").get(0).equals("a."));
        Assert.assertTrue(article.spliter(".a.b").get(1).equals("b"));

        Assert.assertTrue(article.spliter(".a.b.").size()==2);
        Assert.assertTrue(article.spliter(".a.b.").get(0).equals("a."));
        Assert.assertTrue(article.spliter(".a.b.").get(1).equals("b."));

        Assert.assertTrue(article.spliter("5.2b.").size()==1);
        Assert.assertTrue(article.spliter("5.2b.").get(0).equals("5.2b."));

        Assert.assertTrue(article.spliter("aaa. cc5.2b.").size()==2);
        Assert.assertTrue(article.spliter("aaa. cc5.2b.").get(0).equals("aaa."));
        Assert.assertTrue(article.spliter("aaa. cc5.2b.").get(1).equals(" cc5.2b."));

        Assert.assertTrue(article.spliter("aaa... cc5.2b.").size()==2);
        Assert.assertTrue(article.spliter("aaa... cc5.2b.").get(0).equals("aaa..."));
        Assert.assertTrue(article.spliter("aaa... cc5.2b.").get(1).equals(" cc5.2b."));

        Assert.assertTrue(article.spliter("aaa..cc5.2b.").size()==1);
        Assert.assertTrue(article.spliter("aaa..cc5.2b.").get(0).equals("aaa..cc5.2b."));

        Assert.assertTrue(article.spliter("aaa? cc5.2b.").size()==2);
        Assert.assertTrue(article.spliter("aaa? cc5.2b.").get(0).equals("aaa?"));
        Assert.assertTrue(article.spliter("aaa? cc5.2b.").get(1).equals(" cc5.2b."));

        Assert.assertTrue(article.spliter("aaa! cc5.2b.").size()==2);
        Assert.assertTrue(article.spliter("aaa! cc5.2b.").get(0).equals("aaa!"));
        Assert.assertTrue(article.spliter("aaa! cc5.2b.").get(1).equals(" cc5.2b."));



        Assert.assertTrue(article.spliter(text2).size()==3);





        Assert.assertTrue(article.spliter(text3).size()==5);




        Assert.assertTrue(article.spliter(result).size()==3);


    }



}


