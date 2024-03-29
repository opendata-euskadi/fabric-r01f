/*
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

/*
 *  (C) Copyright IBM Corp. 1999 All Rights Reserved.
 *  Copyright 1997 The Open Group Research Institute.  All rights reserved.
 */

package r01f.httpclient.jsse.security.krb5.internal.crypto;

import java.security.InvalidKeyException;

import javax.crypto.spec.DESKeySpec;

import r01f.httpclient.jsse.security.krb5.Checksum;
import r01f.httpclient.jsse.security.krb5.KrbCryptoException;
import r01f.httpclient.jsse.security.krb5.internal.Krb5;

public class DesMacKCksumType extends CksumType {

    public DesMacKCksumType() {
    }

    public int confounderSize() {
        return 0;
    }

    public int cksumType() {
        return Checksum.CKSUMTYPE_DES_MAC_K;
    }

    public boolean isSafe() {
        return true;
    }

    public int cksumSize() {
        return 16;
    }

    public int keyType() {
        return Krb5.KEYTYPE_DES;
    }

    public int keySize() {
        return 8;
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
     *
     * @modified by Yanni Zhang, 12/08/99.
     */
    public byte[] calculateKeyedChecksum(byte[] data, int size, byte[] key,
        int usage) throws KrbCryptoException {
        //check for weak keys
        try {
            if (DESKeySpec.isWeak(key, 0)) {
                key[7] = (byte)(key[7] ^ 0xF0);
            }
        } catch (InvalidKeyException ex) {
            // swallow, since it should never happen
        }
        byte[] ivec = new byte[key.length];
        System.arraycopy(key, 0, ivec, 0, key.length);
        byte[] cksum = Des.des_cksum(ivec, data, key);
        return cksum;
    }

    public boolean verifyKeyedChecksum(byte[] data, int size,
        byte[] key, byte[] checksum, int usage) throws KrbCryptoException {
        byte[] new_cksum = calculateKeyedChecksum(data, data.length, key, usage);
        return isChecksumEqual(checksum, new_cksum);
    }

}
