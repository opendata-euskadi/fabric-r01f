/*
 * Created on Feb 22, 2007
 *
 * @author ie00165h - Alex Lara
 * (c) 2007 EJIE: Eusko Jaurlaritzako Informatika Elkartea
 */
package r01f.mail.simple;

import java.io.File;

import r01f.mime.MimeType;
import r01f.mime.MimeTypes;
import r01f.types.Path;

abstract class SimpleMailSenderBase {
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTANTS
/////////////////////////////////////////////////////////////////////////////////////////
    public static final MimeType MIME_HTML = MimeTypes.HTML; 
    public static final MimeType MIME_TEXT = MimeTypes.TEXT_PLAIN;
    
/////////////////////////////////////////////////////////////////////////////////////////
//  PRIVATE METHODS
/////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Deletes the attachment files temp files
     * @param attachs
     */
    protected static void _deleteTempFiles(final Path[] attachedFilesPaths) {
        for (int i = 0; i < attachedFilesPaths.length; i++) {
            File file = new File(attachedFilesPaths[i].asAbsoluteString());
            file.delete();
        }
    }
}
