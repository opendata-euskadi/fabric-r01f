/*
 * Copyright 2005 Sun Microsystems, Inc.  All Rights Reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Sun Microsystems, Inc., 4150 Network Circle, Santa Clara,
 * CA 95054 USA or visit www.sun.com if you need additional information or
 * have any questions.
 */

package r01f.httpclient.jsse.security.krb5.internal.crypto;

import java.security.GeneralSecurityException;

import r01f.httpclient.jsse.security.krb5.Checksum;
import r01f.httpclient.jsse.security.krb5.KrbCryptoException;
import r01f.httpclient.jsse.security.krb5.internal.Krb5;

/**
 * This class encapsulates the checksum type for HMAC RC4
 *
 * @author Seema Malkani
 */

public class HmacMd5ArcFourCksumType extends CksumType {

    public HmacMd5ArcFourCksumType() {
    }

    public int confounderSize() {
        return 8;
    }

    public int cksumType() {
        return Checksum.CKSUMTYPE_HMAC_MD5_ARCFOUR;
    }

    public boolean isSafe() {
        return true;
    }

    public int cksumSize() {
        return 16;  // bytes
    }

    public int keyType() {
        return Krb5.KEYTYPE_ARCFOUR_HMAC;
    }

    public int keySize() {
        return 16;   // bytes
    }

    public byte[] calculateChecksum(byte[] data, int size) {
        return null;
    }

    /**
     * Calculates keyed checksum.
     * @param data the data used to generate the checksum.
     * @param size length of the data.
     * @param key the key used to encrypt the checksum.
     * @return keyed checksum.
     */
    public byte[] calculateKeyedChecksum(byte[] data, int size, byte[] key,
        int usage) throws KrbCryptoException {

         try {
             return ArcFourHmac.calculateChecksum(key, usage, data, 0, size);
         } catch (GeneralSecurityException e) {
             KrbCryptoException ke = new KrbCryptoException(e.getMessage());
             ke.initCause(e);
             throw ke;
         }
    }

    /**
     * Verifies keyed checksum.
     * @param data the data.
     * @param size the length of data.
     * @param key the key used to encrypt the checksum.
     * @param checksum
     * @return true if verification is successful.
     */
    public boolean verifyKeyedChecksum(byte[] data, int size,
        byte[] key, byte[] checksum, int usage) throws KrbCryptoException {

         try {
             byte[] newCksum = ArcFourHmac.calculateChecksum(key, usage,
                 data, 0, size);

             return isChecksumEqual(checksum, newCksum);
         } catch (GeneralSecurityException e) {
             KrbCryptoException ke = new KrbCryptoException(e.getMessage());
             ke.initCause(e);
             throw ke;
         }
     }
}
