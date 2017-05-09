package data;

public class LinkToTextComparator {

    public LinkToTextComparator(){
        super();
    }

    public boolean compareLinkWithText(String textHTML, String text){
        String textWithoutHTML = deleteLinkTag(textHTML);

        return text.equals(textWithoutHTML.trim());//Trim para quitar espacios antes y después
    }

    private String deleteLinkTag(String textHTML){

        for(int i = 0; i < textHTML.length(); i++){
            if(textHTML.charAt(i) == '<'){
                if(textHTML.charAt(i + 1) == 'a'){

                    textHTML = borrarTag(textHTML, i);
                    textHTML = deleteLinkTag(textHTML);

                }

                else if (textHTML.charAt(i + 1) == '/'){
                    if(textHTML.charAt(i + 2) == 'a'){
                        textHTML = borrarTag(textHTML, i);
                        textHTML = deleteLinkTag(textHTML);
                    }
                }
            }

        }//Fin for

        return textHTML;
    }

    private String borrarTag(String textHTML, int inicioTag){

        int finTag = 0;

        for(int i = inicioTag; i < textHTML.length(); i++){
            if(textHTML.charAt(i) == '>'){
                finTag = i;
                break;
            }

        }

        String enlaceABorrar = textHTML.substring(inicioTag, finTag + 1);
        return textHTML.replace(enlaceABorrar, "");

    }
}
