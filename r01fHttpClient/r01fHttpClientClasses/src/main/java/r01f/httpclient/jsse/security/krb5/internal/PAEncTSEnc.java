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

package r01f.httpclient.jsse.security.krb5.internal;

import java.io.IOException;
import java.math.BigInteger;

import r01f.httpclient.jsse.security.krb5.Asn1Exception;
import r01f.httpclient.jsse.security.util.DerOutputStream;
import r01f.httpclient.jsse.security.util.DerValue;

/**
 * Implements the ASN.1 PAEncTSEnc type.
 *
 * <xmp>
 * PA-ENC-TS-ENC                ::= SEQUENCE {
 *      patimestamp     [0] KerberosTime -- client's time --,
 *      pausec          [1] Microseconds OPTIONAL
 * }
 * </xmp>
 *
 * <p>
 * This definition reflects the Network Working Group RFC 4120
 * specification available at
 * <a href="http://www.ietf.org/rfc/rfc4120.txt">
 * http://www.ietf.org/rfc/rfc4120.txt</a>.
 */

public class PAEncTSEnc {
    public KerberosTime pATimeStamp;
    public Integer pAUSec; //optional

    public PAEncTSEnc(
                      KerberosTime new_pATimeStamp,
                      Integer new_pAUSec
                          ) {
        pATimeStamp = new_pATimeStamp;
        pAUSec = new_pAUSec;
    }

    public PAEncTSEnc() {
        KerberosTime now = new KerberosTime(KerberosTime.NOW);
        pATimeStamp = now;
        pAUSec = Integer.valueOf(now.getMicroSeconds());
    }

    /**
     * Constructs a PAEncTSEnc object.
     * @param encoding a Der-encoded data.
     * @exception Asn1Exception if an error occurs while decoding an ASN1 encoded data.
     * @exception IOException if an I/O error occurs while reading encoded data.
     */
    public PAEncTSEnc(DerValue encoding) throws Asn1Exception, IOException {
        DerValue der;
        if (encoding.getTag() != DerValue.tag_Sequence) {
            throw new Asn1Exception(Krb5.ASN1_BAD_ID);
        }
        pATimeStamp = KerberosTime.parse(encoding.getData(), (byte)0x00, false);
        if (encoding.getData().available() > 0) {
            der = encoding.getData().getDerValue();
            if ((der.getTag() & 0x1F) == 0x01) {
                pAUSec = Integer.valueOf(der.getData().getBigInteger().intValue());
            }
            else throw new Asn1Exception(Krb5.ASN1_BAD_ID);
        }
        if (encoding.getData().available() > 0)
            throw new Asn1Exception(Krb5.ASN1_BAD_ID);
    }


    /**
     * Encodes a PAEncTSEnc object.
     * @return the byte array of encoded PAEncTSEnc object.
     * @exception Asn1Exception if an error occurs while decoding an ASN1 encoded data.
     * @exception IOException if an I/O error occurs while reading encoded data.
     */
    @SuppressWarnings("resource")
	public byte[] asn1Encode() throws Asn1Exception, IOException {
        DerOutputStream bytes = new DerOutputStream();
        DerOutputStream temp = new DerOutputStream();
        bytes.write(DerValue.createTag(DerValue.TAG_CONTEXT, true, (byte)0x00), pATimeStamp.asn1Encode());
        if (pAUSec != null) {
            temp = new DerOutputStream();
            temp.putInteger(BigInteger.valueOf(pAUSec.intValue()));
            bytes.write(DerValue.createTag(DerValue.TAG_CONTEXT, true, (byte)0x01), temp);
        }
        temp = new DerOutputStream();
        temp.write(DerValue.tag_Sequence, bytes);
        return temp.toByteArray();
    }
}
