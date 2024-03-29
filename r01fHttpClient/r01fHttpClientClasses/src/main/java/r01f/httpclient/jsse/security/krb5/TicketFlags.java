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

import r01f.httpclient.jsse.security.krb5.internal.KDCOptions;
import r01f.httpclient.jsse.security.krb5.internal.Krb5;
import r01f.httpclient.jsse.security.krb5.internal.LoginOptions;
import r01f.httpclient.jsse.security.krb5.internal.util.KerberosFlags;
import r01f.httpclient.jsse.security.util.DerInputStream;
import r01f.httpclient.jsse.security.util.DerValue;

/**
 * Implements the ASN.1TicketFlags type.
 *
 *    TicketFlags ::= BIT STRING
 *                  {
 *                   reserved(0),
 *                   forwardable(1),
 *                   forwarded(2),
 *                   proxiable(3),
 *                   proxy(4),
 *                   may-postdate(5),
 *                   postdated(6),
 *                   invalid(7),
 *                   renewable(8),
 *                   initial(9),
 *                   pre-authent(10),
 *                   hw-authent(11)
 *                  }
 */
public class TicketFlags extends KerberosFlags {
    public TicketFlags() {
        super(Krb5.TKT_OPTS_MAX + 1);
    }

    public TicketFlags (boolean[] flags) throws Asn1Exception {
        super(flags);
        if (flags.length > Krb5.TKT_OPTS_MAX + 1) {
            throw new Asn1Exception(Krb5.BITSTRING_BAD_LENGTH);
        }
    }

    public TicketFlags(int size, byte[] data) throws Asn1Exception {
        super(size, data);
        if ((size > data.length * BITS_PER_UNIT) || (size > Krb5.TKT_OPTS_MAX + 1))
            throw new Asn1Exception(Krb5.BITSTRING_BAD_LENGTH);
    }

    public TicketFlags(DerValue encoding) throws IOException, Asn1Exception {
        this(encoding.getUnalignedBitString(true).toBooleanArray());
    }

    /**
     * Parse (unmarshal) a ticket flag from a DER input stream.  This form
     * parsing might be used when expanding a value which is part of
     * a constructed sequence and uses explicitly tagged type.
     *
     * @exception Asn1Exception on error.
     * @param data the Der input stream value, which contains one or more marshaled value.
     * @param explicitTag tag number.
     * @param optional indicate if this data field is optional
     * @return an instance of TicketFlags.
     *
     */
    public static TicketFlags parse(DerInputStream data, byte explicitTag, boolean optional) throws Asn1Exception, IOException {
        if ((optional) && (((byte)data.peekByte() & (byte)0x1F) != explicitTag))
            return null;
        DerValue der = data.getDerValue();
        if (explicitTag != (der.getTag() & (byte)0x1F))  {
            throw new Asn1Exception(Krb5.ASN1_BAD_ID);
        }
        else {
            DerValue subDer = der.getData().getDerValue();
            return new TicketFlags(subDer);
        }
    }

    public Object clone() {
        try {
            return new TicketFlags(this.toBooleanArray());
        }
        catch (Exception e) {
            return null;
        }
    }

    public boolean match(LoginOptions options) {
        boolean matched = false;
        //We currently only consider if forwardable renewable and proxiable are match
        if (this.get(Krb5.TKT_OPTS_FORWARDABLE) == (options.get(KDCOptions.FORWARDABLE))) {
            if (this.get(Krb5.TKT_OPTS_PROXIABLE) == (options.get(KDCOptions.PROXIABLE))) {
                if (this.get(Krb5.TKT_OPTS_RENEWABLE) == (options.get(KDCOptions.RENEWABLE))) {
                    matched = true;
                }
            }
        }
        return matched;
    }
    public boolean match(TicketFlags flags) {
        boolean matched = true;
        for (int i = 0; i <= Krb5.TKT_OPTS_MAX; i++) {
            if (this.get(i) != flags.get(i)) {
                return false;
            }
        }
        return matched;
    }


    /**
     * Returns the string representative of ticket flags.
     */
    public String toString() {
        StringBuffer sb = new StringBuffer();
        boolean[] flags = toBooleanArray();
        for (int i = 0; i < flags.length; i++) {
            if (flags[i] == true) {
                switch (i) {
                case 0:
                    sb.append("RESERVED;");
                    break;
                case 1:
                    sb.append("FORWARDABLE;");
                    break;
                case 2:
                    sb.append("FORWARDED;");
                    break;
                case 3:
                    sb.append("PROXIABLE;");
                    break;
                case 4:
                    sb.append("PROXY;");
                    break;
                case 5:
                    sb.append("MAY-POSTDATE;");
                    break;
                case 6:
                    sb.append("POSTDATED;");
                    break;
                case 7:
                    sb.append("INVALID;");
                    break;
                case 8:
                    sb.append("RENEWABLE;");
                    break;
                case 9:
                    sb.append("INITIAL;");
                    break;
                case 10:
                    sb.append("PRE-AUTHENT;");
                    break;
                case 11:
                    sb.append("HW-AUTHENT;");
                    break;
                }
            }
        }
        String result = sb.toString();
        if (result.length() > 0) {
            result = result.substring(0, result.length() - 1);
        }
        return result;
    }
}
