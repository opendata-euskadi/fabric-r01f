/*
 * Copyright 2006 Sun Microsystems, Inc.  All Rights Reserved.
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

package r01f.httpclient.jsse.security.provider.ec;

import java.io.IOException;
import java.math.BigInteger;
import java.security.AlgorithmParameters;
import java.security.InvalidKeyException;
import java.security.interfaces.ECPrivateKey;
import java.security.spec.ECParameterSpec;
import java.security.spec.InvalidParameterSpecException;

import r01f.httpclient.jsse.security.pkcs.PKCS8Key;
import r01f.httpclient.jsse.security.util.DerInputStream;
import r01f.httpclient.jsse.security.util.DerOutputStream;
import r01f.httpclient.jsse.security.util.DerValue;
import r01f.httpclient.jsse.security.x509.AlgorithmId;

/**
 * Key implementation for EC private keys.
 *
 * ASN.1 syntax for EC private keys from SEC 1 v1.5 (draft):
 *
 * <pre>
 * EXPLICIT TAGS
 *
 * ECPrivateKey ::= SEQUENCE {
 *   version INTEGER { ecPrivkeyVer1(1) } (ecPrivkeyVer1),
 *   privateKey OCTET STRING,
 *   parameters [0] ECDomainParameters {{ SECGCurveNames }} OPTIONAL,
 *   publicKey [1] BIT STRING OPTIONAL
 * }
 * </pre>
 *
 * We currently ignore the optional parameters and publicKey fields. We
 * require that the parameters are encoded as part of the AlgorithmIdentifier,
 * not in the private key structure.
 *
 * @since   1.6
 * @author  Andreas Sterbenz
 */
public final class ECPrivateKeyImpl extends PKCS8Key implements ECPrivateKey {

    private static final long serialVersionUID = 88695385615075129L;

    private BigInteger s;       // private value
    private ECParameterSpec params;

    /**
     * Construct a key from its encoding. Called by the ECKeyFactory and
     * the SunPKCS11 code.
     */
    public ECPrivateKeyImpl(byte[] encoded) throws InvalidKeyException {
        decode(encoded);
    }

    /**
     * Construct a key from its components. Used by the
     * KeyFactory and the SunPKCS11 code.
     */
    @SuppressWarnings("resource")
	public ECPrivateKeyImpl(BigInteger s, ECParameterSpec params)
            throws InvalidKeyException {
        this.s = s;
        this.params = params;
        // generate the encoding
        algid = new AlgorithmId
            (AlgorithmId.EC_oid, ECParameters.getAlgorithmParameters(params));
        try {
            DerOutputStream out = new DerOutputStream();
            out.putInteger(1); // version 1
            byte[] privBytes = ECParameters.trimZeroes(s.toByteArray());
            out.putOctetString(privBytes);
            DerValue val =
                new DerValue(DerValue.tag_Sequence, out.toByteArray());
            key = val.toByteArray();
        } catch (IOException exc) {
            // should never occur
            throw new InvalidKeyException(exc);
        }
    }

    // see JCA doc
    public String getAlgorithm() {
        return "EC";
    }

    // see JCA doc
    public BigInteger getS() {
        return s;
    }

    // see JCA doc
    public ECParameterSpec getParams() {
        return params;
    }

    /**
     * Parse the key. Called by PKCS8Key.
     */
    protected void parseKeyBits() throws InvalidKeyException {
        try {
            DerInputStream in = new DerInputStream(key);
            DerValue derValue = in.getDerValue();
            if (derValue.tag != DerValue.tag_Sequence) {
                throw new IOException("Not a SEQUENCE");
            }
            DerInputStream data = derValue.data;
            int version = data.getInteger();
            if (version != 1) {
                throw new IOException("Version must be 1");
            }
            byte[] privData = data.getOctetString();
            s = new BigInteger(1, privData);
            while (data.available() != 0) {
                DerValue value = data.getDerValue();
                if (value.isContextSpecific((byte)0)) {
                    // ignore for now
                } else if (value.isContextSpecific((byte)1)) {
                    // ignore for now
                } else {
                    throw new InvalidKeyException("Unexpected value: " + value);
                }
            }
            AlgorithmParameters algParams = this.algid.getParameters();
            if (algParams == null) {
                throw new InvalidKeyException("EC domain parameters must be "
                    + "encoded in the algorithm identifier");
            }
            params = algParams.getParameterSpec(ECParameterSpec.class);
        } catch (IOException e) {
            throw new InvalidKeyException("Invalid EC private key", e);
        } catch (InvalidParameterSpecException e) {
            throw new InvalidKeyException("Invalid EC private key", e);
        }
    }

    // return a string representation of this key for debugging
    public String toString() {
        return "Sun EC private key, " + params.getCurve().getField().getFieldSize()
            + " bits\n  private value:  "
            + s + "\n  parameters: " + params;
    }

}
