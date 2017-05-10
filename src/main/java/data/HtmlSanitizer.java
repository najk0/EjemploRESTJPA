package data;

import com.google.common.collect.Sets;
import net.htmlparser.jericho.*;

import java.util.List;
import java.util.Set;

import static net.htmlparser.jericho.CharacterReference.encode;
import static net.htmlparser.jericho.CharacterReference.decode;

public class HtmlSanitizer {

    private HtmlSanitizer() {
    }

    private static final Set<String> VALID_ELEMENTS = Sets.newHashSet(HTMLElementName.A);


    private static final Set<String> VALID_ATTRIBUTES = Sets.newHashSet("href", "title");

    private static final Object VALID_MARKER = new Object();

    public static String sanitize(Source source) {
        source.fullSequentialParse();
        OutputDocument doc = new OutputDocument(source);
        List<Tag> tags = source.getAllTags();
        int pos = 0;
        for (Tag tag : tags) {
            if (processTag(tag, doc))
                tag.setUserData(VALID_MARKER);
            else
                doc.remove(tag);
            reencodeTextSegment(source, doc, pos, tag.getBegin());
            pos = tag.getEnd();
        }
        reencodeTextSegment(source, doc, pos, source.getEnd());
        return doc.toString();
    }

    private static boolean processTag(Tag tag, OutputDocument doc) {
        String elementName = tag.getName();
        if (!VALID_ELEMENTS.contains(elementName))
            return false;
        if (tag.getTagType() == StartTagType.NORMAL) {
            Element element = tag.getElement();
            if (HTMLElements.getEndTagRequiredElementNames().contains(
                    elementName)) {
                if (element.getEndTag() == null)
                    return false;
            } else if (HTMLElements.getEndTagOptionalElementNames().contains(
                    elementName)) {
                if (elementName == HTMLElementName.LI && !isValidLITag(tag))
                    return false;
                if (element.getEndTag() == null)
                    doc.insert(element.getEnd(), getEndTagHTML(elementName));

            }
            doc.replace(tag, getStartTagHTML(element.getStartTag()));
        } else if (tag.getTagType() == EndTagType.NORMAL) {
            if (tag.getElement() == null)
                return false;
            if (elementName == HTMLElementName.LI && !isValidLITag(tag))
                return false;
            doc.replace(tag, getEndTagHTML(elementName));
        } else {
            return false;
        }
        return true;
    }

    private static boolean isValidLITag(Tag tag) {
        Element parentElement = tag.getElement().getParentElement();
        if (parentElement == null
                || parentElement.getStartTag().getUserData() != VALID_MARKER)
            return false;
        return parentElement.getName() == HTMLElementName.UL
                || parentElement.getName() == HTMLElementName.OL;
    }

    private static void reencodeTextSegment(Source source, OutputDocument doc,
                                            int begin, int end) {
        if (begin >= end)
            return;
        Segment textSegment = new Segment(source, begin, end);
        String encodedText = encode(decode(textSegment));
        doc.replace(textSegment, encodedText);
    }

    private static CharSequence getStartTagHTML(StartTag startTag) {
        StringBuilder sb = new StringBuilder();
        sb.append('<').append(startTag.getName());
        for (Attribute attribute : startTag.getAttributes()) {
            if (VALID_ATTRIBUTES.contains(attribute.getKey())) {
                sb.append(' ').append(attribute.getName());
                if (attribute.getValue() != null) {
                    sb.append("=\"");
                    sb.append(CharacterReference.encode(attribute.getValue()));
                    sb.append('"');
                }
            }
        }
        if (startTag.getElement().getEndTag() == null
                && !HTMLElements.getEndTagOptionalElementNames().contains(
                startTag.getName()))
            sb.append('/');
        sb.append('>');
        return sb;
    }

    private static String getEndTagHTML(String tagName) {
        return "</" + tagName + '>';
    }

}