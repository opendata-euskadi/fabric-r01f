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

package r01f.httpclient.jsse.security.krb5;

import java.io.IOException;
import java.math.BigInteger;

import r01f.httpclient.jsse.security.krb5.internal.APOptions;
import r01f.httpclient.jsse.security.krb5.internal.Krb5;
import r01f.httpclient.jsse.security.krb5.internal.KrbApErrException;
import r01f.httpclient.jsse.security.util.DerOutputStream;
import r01f.httpclient.jsse.security.util.DerValue;

/**
 * Implements the ASN.1 AP-REQ type.
 *
 * <xmp>
 * AP-REQ               ::= [APPLICATION 14] SEQUENCE {
 *      pvno            [0] INTEGER (5),
 *      msg-type        [1] INTEGER (14),
 *      ap-options      [2] APOptions,
 *      ticket          [3] Ticket,
 *      authenticator   [4] EncryptedData -- Authenticator
 * }
 * </xmp>
 *
 * <p>
 * This definition reflects the Network Working Group RFC 4120
 * specification available at
 * <a href="http://www.ietf.org/rfc/rfc4120.txt">
 * http://www.ietf.org/rfc/rfc4120.txt</a>.
 */

public class APReq {
        public int pvno;
        public int msgType;
        public APOptions apOptions;
        public Ticket ticket;
        public EncryptedData authenticator;

        public APReq(
                APOptions new_apOptions,
                Ticket new_ticket,
                EncryptedData new_authenticator
        ) {
                pvno = Krb5.PVNO;
                msgType = Krb5.KRB_AP_REQ;
                apOptions = new_apOptions;
                ticket = new_ticket;
                authenticator = new_authenticator;
        }

        public APReq(byte[] data) throws Asn1Exception,IOException, KrbApErrException, RealmException {
        init(new DerValue(data));
        }

    public APReq(DerValue encoding) throws Asn1Exception, IOException, KrbApErrException, RealmException {
                init(encoding);
        }

        /**
         * Initializes an APReq object.
         * @param encoding a single DER-encoded value.
         * @exception Asn1Exception if an error occurs while decoding an ASN1 encoded data.
         * @exception IOException if an I/O error occurs while reading encoded data.
         * @exception KrbApErrException if the value read from the DER-encoded data stream does not match the pre-defined value.
         * @exception RealmException if an error occurs while parsing a Realm object.
         */
        private void init(DerValue encoding) throws Asn1Exception,
           IOException, KrbApErrException, RealmException {
                DerValue der, subDer;
        if (((encoding.getTag() & (byte)0x1F) != Krb5.KRB_AP_REQ)
                        || (encoding.isApplication() != true)
                        || (encoding.isConstructed() != true))
                        throw new Asn1Exception(Krb5.ASN1_BAD_ID);
                der = encoding.getData().getDerValue();
                if (der.getTag() != DerValue.tag_Sequence)
                   throw new Asn1Exception(Krb5.ASN1_BAD_ID);
                subDer = der.getData().getDerValue();
        if ((subDer.getTag() & (byte)0x1F) != (byte)0x00)
            throw new Asn1Exception(Krb5.ASN1_BAD_ID);
        pvno = subDer.getData().getBigInteger().intValue();
        if (pvno != Krb5.PVNO)
                                throw new KrbApErrException(Krb5.KRB_AP_ERR_BADVERSION);
                subDer = der.getData().getDerValue();
                if ((subDer.getTag() & (byte)0x1F) != (byte)0x01)
                        throw new Asn1Exception(Krb5.ASN1_BAD_ID);
                msgType = subDer.getData().getBigInteger().intValue();
                if (msgType != Krb5.KRB_AP_REQ)
                         throw new KrbApErrException(Krb5.KRB_AP_ERR_MSG_TYPE);
                apOptions = APOptions.parse(der.getData(), (byte)0x02, false);
                ticket = Ticket.parse(der.getData(), (byte)0x03, false);
                authenticator = EncryptedData.parse(der.getData(), (byte)0x04, false);
                if (der.getData().available() > 0)
                        throw new Asn1Exception(Krb5.ASN1_BAD_ID);
        }

        /**
         * Encodes an APReq object.
         * @return byte array of encoded APReq object.
         * @exception Asn1Exception if an error occurs while decoding an ASN1 encoded data.
         * @exception IOException if an I/O error occurs while reading encoded data.
         */
        public byte[] asn1Encode() throws Asn1Exception, IOException {
        DerOutputStream bytes = new DerOutputStream();
            DerOutputStream temp = new DerOutputStream();
                temp.putInteger(BigInteger.valueOf(pvno));
                bytes.write(DerValue.createTag(DerValue.TAG_CONTEXT, true, (byte)0x00), temp);
                temp = new DerOutputStream();
                temp.putInteger(BigInteger.valueOf(msgType));
                bytes.write(DerValue.createTag(DerValue.TAG_CONTEXT, true, (byte)0x01), temp);
                bytes.write(DerValue.createTag(DerValue.TAG_CONTEXT, true, (byte)0x02), apOptions.asn1Encode());
                bytes.write(DerValue.createTag(DerValue.TAG_CONTEXT, true, (byte)0x03), ticket.asn1Encode());
                bytes.write(DerValue.createTag(DerValue.TAG_CONTEXT, true, (byte)0x04), authenticator.asn1Encode());
            temp = new DerOutputStream();
                temp.write(DerValue.tag_Sequence, bytes);
                @SuppressWarnings("resource")
				DerOutputStream apreq = new DerOutputStream();
                apreq.write(DerValue.createTag(DerValue.TAG_APPLICATION, true, (byte)0x0E), temp);
                return apreq.toByteArray();

        }

}
