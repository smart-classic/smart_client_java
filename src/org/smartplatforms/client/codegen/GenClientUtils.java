/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.smartplatforms.client.codegen;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 *
 * @author nate
 */
public class GenClientUtils {
    public static final int STATE_START = 0;
    public static final int STATE_CONSTANT = 1;
    public static final int STATE_VARIABLE = 2;
    public static final int STATE_END = 99999;

    private Map<String,String> javaDocTable = null;

    public GenClientUtils() {
        javaDocTable = new HashMap<String,String>();
        javaDocTable.put("recordId","server's record ID");
        javaDocTable.put("externalId", "external ID");
        javaDocTable.put("allergyId", "server's internal ID for this allergy document");
        javaDocTable.put("medicationId", "server's internal ID for this medication document");
        javaDocTable.put("fulfillmentId", "server's internal ID for this fulfillment document");
        javaDocTable.put("medExternalId", "ID assigned to the medication document encompassing this fulfillment");
        javaDocTable.put("noteId", "server's internal ID for this note document");
        javaDocTable.put("problemId", "server's internal ID for this problem document");
        javaDocTable.put("userId", "server's internal ID for this user");
    }


    public void tokensToSig(
            List<String> tokenList,
            StringBuffer methSig,
            List<String> paramTypedList,
            StringBuffer javaDoc,
            StringBuffer requestURL_SB,
            String path,
            int[] stateA) {
        int state = stateA[0];
        for (int ii = 0; ii < tokenList.size(); ii++) {
            String token = tokenList.get(ii);

            if (token.startsWith("{")) {
                methSig.append("_X");
                String aParam = token.substring(1,token.length() -1);
                StringBuffer aParamSB = new StringBuffer();

                String pType = "String ";
                if (aParam.equals("attachment_num")) {
                    pType = "Integer ";
                }

                int uix = aParam.indexOf('_');
                while (uix != -1) {
                    aParamSB.append(aParam.substring(0,uix));
                    aParam = aParam.substring(uix +1);
                    if (aParam.length() > 0) {
                        aParamSB.append(Character.toUpperCase(aParam.charAt(0)));
                        aParam = aParam.substring(1);
                    }
                    uix = aParam.indexOf('_');
                }
                aParamSB.append(aParam);
                paramTypedList.add(pType + aParamSB.toString());
                javaDoc.append("\n    * @param " + aParamSB + " " + javaDocTable.get(aParamSB.toString()));

                if (state == STATE_CONSTANT) { requestURL_SB.append("/\" + "); }
                if (state == STATE_VARIABLE) { requestURL_SB.append(" + \"/\" + "); }
                requestURL_SB.append(aParamSB);
                state = STATE_VARIABLE;
            } else {
                String tokenC = dashToCamel(token);
                if (state != STATE_START) { methSig.append("_"); }
                methSig.append(tokenC);

                if (state == STATE_START) { requestURL_SB.append("\""); }
                else if (state == STATE_CONSTANT) {
                    requestURL_SB.append("/");
                }
                else if (state == STATE_VARIABLE) { requestURL_SB.append(" + \"/"); }
                requestURL_SB.append(token);

                state = STATE_CONSTANT;
            }
        }
        stateA[0] = state;
    }

    /** Java does not allow '-' a part of a method name */
    private String dashToCamel(String frag) {
        StringBuffer retVal = new StringBuffer(frag);
        int dix = retVal.indexOf("-");
        while (dix != -1) {
            System.err.println("dash to camel: " + retVal.toString());
            // will fail if traling dash
            String uc = "" + Character.toUpperCase(retVal.charAt(dix +1));
            retVal.replace(dix, dix +2, uc); // replace "-x" with "X"
            dix = retVal.indexOf("-");
        }
        return retVal.toString();
    }

}
