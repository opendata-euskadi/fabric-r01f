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

import r01f.httpclient.jsse.security.krb5.Asn1Exception;
import r01f.httpclient.jsse.security.krb5.Config;
import r01f.httpclient.jsse.security.krb5.KrbException;
import r01f.httpclient.jsse.security.krb5.internal.util.KerberosFlags;
import r01f.httpclient.jsse.security.util.DerInputStream;
import r01f.httpclient.jsse.security.util.DerValue;

/**
 * Implements the ASN.1 KDCOptions type.
 *
 * <xmp>
 * KDCOptions   ::= KerberosFlags
 *      -- reserved(0),
 *      -- forwardable(1),
 *      -- forwarded(2),
 *      -- proxiable(3),
 *      -- proxy(4),
 *      -- allow-postdate(5),
 *      -- postdated(6),
 *      -- unused7(7),
 *      -- renewable(8),
 *      -- unused9(9),
 *      -- unused10(10),
 *      -- opt-hardware-auth(11),
 *      -- unused12(12),
 *      -- unused13(13),
 * -- 15 is reserved for canonicalize
 *      -- unused15(15),
 * -- 26 was unused in 1510
 *      -- disable-transited-check(26),
 *      -- renewable-ok(27),
 *      -- enc-tkt-in-skey(28),
 *      -- renew(30),
 *      -- validate(31)
 *
 * KerberosFlags   ::= BIT STRING (SIZE (32..MAX))
 *                      -- minimum number of bits shall be sent,
 *                      -- but no fewer than 32
 *
 * </xmp>
 *
 * <p>
 * This definition reflects the Network Working Group RFC 4120
 * specification available at
 * <a href="http://www.ietf.org/rfc/rfc4120.txt">
 * http://www.ietf.org/rfc/rfc4120.txt</a>.
 *
 * <p>
 * This class appears as data field in the initial request(KRB_AS_REQ)
 * or subsequent request(KRB_TGS_REQ) to the KDC and indicates the flags
 * that the client wants to set on the tickets.
 *
 * The optional bits are:
 * <UL>
 *  <LI>KDCOptions.RESERVED
 *  <LI>KDCOptions.FORWARDABLE
 *  <LI>KDCOptions.FORWARDED
 *  <LI>KDCOptions.PROXIABLE
 *  <LI>KDCOptions.PROXY
 *  <LI>KDCOptions.ALLOW_POSTDATE
 *  <LI>KDCOptions.POSTDATED
 *  <LI>KDCOptions.RENEWABLE
 *  <LI>KDCOptions.RENEWABLE_OK
 *  <LI>KDCOptions.ENC_TKT_IN_SKEY
 *  <LI>KDCOptions.RENEW
 *  <LI>KDCOptions.VALIDATE
 *  </UL>
 * <p> Various checks must be made before honoring an option. The restrictions
 * on the use of some options are as follows:
 * <ol>
 * <li> FORWARDABLE, FORWARDED, PROXIABLE, RENEWABLE options may be set in
 * subsequent request only if the ticket_granting ticket on which it is based has
 * the same options (FORWARDABLE, FORWARDED, PROXIABLE, RENEWABLE) set.
 * <li> ALLOW_POSTDATE may be set in subsequent request only if the
 * ticket-granting ticket on which it is based also has its MAY_POSTDATE flag set.
 * <li> POSTDATED may be set in subsequent request only if the
 * ticket-granting ticket on which it is based also has its MAY_POSTDATE flag set.
 * <li> RENEWABLE or RENEW may be set in subsequent request only if the
 * ticket-granting ticket on which it is based also has its RENEWABLE flag set.
 * <li> POXY may be set in subsequent request only if the ticket-granting ticket
 * on which it is based also has its PROXIABLE flag set, and the address(es) of
 * the host from which the resulting ticket is to be valid should be included
 * in the addresses field of the request.
 * <li>FORWARDED, PROXY, ENC_TKT_IN_SKEY, RENEW, VALIDATE are used only in
 * subsequent requests.
 * </ol><p>
 */

public class KDCOptions extends KerberosFlags {

    public final int KDC_OPT_PROXIABLE = 0x10000000;
    public final int KDC_OPT_RENEWABLE_OK = 0x00000010;
    public final int KDC_OPT_FORWARDABLE = 0x40000000;


    // KDC Options

    public static final int RESERVED        = 0;
    public static final int FORWARDABLE     = 1;
    public static final int FORWARDED       = 2;
    public static final int PROXIABLE       = 3;
    public static final int PROXY           = 4;
    public static final int ALLOW_POSTDATE  = 5;
    public static final int POSTDATED       = 6;
    public static final int UNUSED7         = 7;
    public static final int RENEWABLE       = 8;
    public static final int UNUSED9         = 9;
    public static final int UNUSED10        = 10;
    public static final int UNUSED11        = 11;
    public static final int RENEWABLE_OK    = 27;
    public static final int ENC_TKT_IN_SKEY = 28;
    public static final int RENEW           = 30;
    public static final int VALIDATE        = 31;

    private boolean DEBUG = Krb5.DEBUG;

    public KDCOptions() {
        super(Krb5.KDC_OPTS_MAX + 1);
        setDefault();
    }

    public KDCOptions(int size, byte[] data) throws Asn1Exception {
        super(size, data);
        if ((size > data.length * BITS_PER_UNIT) || (size > Krb5.KDC_OPTS_MAX + 1))
            throw new Asn1Exception(Krb5.BITSTRING_BAD_LENGTH);
    }

    /**
     * Constructs a KDCOptions from the specified bit settings.
     *
     * @param data the bits to be set for the KDCOptions.
     * @exception Asn1Exception if an error occurs while decoding an ASN1
     * encoded data.
     *
     */
    public KDCOptions(boolean[] data) throws Asn1Exception {
        super(data);
        if (data.length > Krb5.KDC_OPTS_MAX + 1) {
            throw new Asn1Exception(Krb5.BITSTRING_BAD_LENGTH);
        }
    }

    public KDCOptions(DerValue encoding) throws Asn1Exception, IOException {
        this(encoding.getUnalignedBitString(true).toBooleanArray());
    }

    /**
     * Constructs a KDCOptions from the passed bit settings.
     *
     * @param options the bits to be set for the KDCOptions.
     *
     */
    public KDCOptions(byte[] options) {
        super(options.length * BITS_PER_UNIT, options);
    }

    /**
     * Parse (unmarshal) a KDCOptions from a DER input stream.  This form
     * parsing might be used when expanding a value which is part of
     * a constructed sequence and uses explicitly tagged type.
     *
     * @param data the Der input stream value, which contains one or more
     * marshaled value.
     * @param explicitTag tag number.
     * @param optional indicate if this data field is optional
     * @return an instance of KDCOptions.
     * @exception Asn1Exception if an error occurs while decoding an ASN1 encoded data.
     * @exception IOException if an I/O error occurs while reading encoded data.
     *
     */

    public static KDCOptions parse(DerInputStream data, byte explicitTag, boolean optional) throws Asn1Exception, IOException {
        if ((optional) && (((byte)data.peekByte() & (byte)0x1F) != explicitTag))
            return null;
        DerValue der = data.getDerValue();
        if (explicitTag != (der.getTag() & (byte)0x1F))  {
            throw new Asn1Exception(Krb5.ASN1_BAD_ID);
        } else {
            DerValue subDer = der.getData().getDerValue();
            return new KDCOptions(subDer);
        }
    }

    /**
     * Sets the value(true/false) for one of the <code>KDCOptions</code>.
     *
     * @param option an option bit.
     * @param value true if the option is selected, false if the option is not selected.
     * @exception ArrayIndexOutOfBoundsException if array index out of bound occurs.

     */
    public void set(int option, boolean value) throws ArrayIndexOutOfBoundsException {
        super.set(option, value);
    }

    /**
     * Gets the value(true/false) for one of the <code>KDCOptions</code>.
     *
     * @param option an option bit.
     * @return value true if the option is selected, false if the option is not selected.
     * @exception ArrayIndexOutOfBoundsException if array index out of bound occurs.

     */

    public boolean get(int option) throws ArrayIndexOutOfBoundsException {
        return super.get(option);
    }


    private void setDefault() {
        try {

            Config config = Config.getInstance();

            /*
             * First see if the IBM hex format is being used.
             * If not, try the Sun's string (boolean) format.
             */

            int options =config.getDefaultIntValue("kdc_default_options",
                    "libdefaults");

            if ((options & RENEWABLE_OK) == RENEWABLE_OK) {
                set(RENEWABLE_OK, true);
            } else {
                if (config.getDefaultBooleanValue("renewable", "libdefaults")) {
                    set(RENEWABLE_OK, true);
                }
            }
            if ((options & PROXIABLE) == PROXIABLE) {
                set(PROXIABLE, true);
            } else {
                if (config.getDefaultBooleanValue("proxiable", "libdefaults")) {
                    set(PROXIABLE, true);
                }
            }

            if ((options & FORWARDABLE) == FORWARDABLE) {
                set(FORWARDABLE, true);
            } else {
                if (config.getDefaultBooleanValue("forwardable", "libdefaults")) {
                    set(FORWARDABLE, true);
                }
            }
        } catch (KrbException e) {
            if (DEBUG) {
                System.out.println("Exception in getting default values for " +
                        "KDC Options from the configuration ");
                e.printStackTrace();

            }
        }
    }
}
