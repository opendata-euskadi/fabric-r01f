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

import java.security.AccessController;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyFactorySpi;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PrivilegedAction;
import java.security.Provider;
import java.security.PublicKey;
import java.security.interfaces.ECKey;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECPrivateKeySpec;
import java.security.spec.ECPublicKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * KeyFactory for EC keys. Keys must be instances of PublicKey or PrivateKey
 * and getAlgorithm() must return "EC". For such keys, it supports conversion
 * between the following:
 *
 * For public keys:
 *  . PublicKey with an X.509 encoding
 *  . ECPublicKey
 *  . ECPublicKeySpec
 *  . X509EncodedKeySpec
 *
 * For private keys:
 *  . PrivateKey with a PKCS#8 encoding
 *  . ECPrivateKey
 *  . ECPrivateKeySpec
 *  . PKCS8EncodedKeySpec
 *
 * @since   1.6
 * @author  Andreas Sterbenz
 */
@SuppressWarnings("serial")
public final class ECKeyFactory extends KeyFactorySpi {

    // Used by translateKey() and the SunPKCS11 provider
    public final static KeyFactory INSTANCE;

    // Internal provider object we can obtain the KeyFactory and
    // AlgorithmParameters from. Used by ECParameters and AlgorithmId.
    // This can go away once we have EC always available in the SUN provider.
    // Used by ECParameters and AlgorithmId.
    public final static Provider ecInternalProvider;

    static {
        final Provider p = new Provider("SunEC-Internal", 1.0d, null) {};
        AccessController.doPrivileged(new PrivilegedAction<Void>() {
            public Void run() {
                p.put("KeyFactory.EC", "sun.security.ec.ECKeyFactory");
                p.put("AlgorithmParameters.EC", "sun.security.ec.ECParameters");
                p.put("Alg.Alias.AlgorithmParameters.1.2.840.10045.2.1", "EC");
                return null;
            }
        });
        try {
            INSTANCE = KeyFactory.getInstance("EC", p);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        ecInternalProvider = p;
    }

    public ECKeyFactory() {
        // empty
    }

    /**
     * Static method to convert Key into a useable instance of
     * ECPublicKey or ECPrivateKey. Check the key and convert it
     * to a Sun key if necessary. If the key is not an EC key
     * or cannot be used, throw an InvalidKeyException.
     *
     * The difference between this method and engineTranslateKey() is that
     * we do not convert keys of other providers that are already an
     * instance of ECPublicKey or ECPrivateKey.
     *
     * To be used by future Java ECDSA and ECDH implementations.
     */
    public static ECKey toECKey(Key key) throws InvalidKeyException {
        if (key instanceof ECKey) {
            ECKey ecKey = (ECKey)key;
            checkKey(ecKey);
            return ecKey;
        } else {
            return (ECKey)INSTANCE.translateKey(key);
        }
    }

    /**
     * Check that the given EC key is valid.
     */
    private static void checkKey(ECKey key) throws InvalidKeyException {
        // check for subinterfaces, omit additional checks for our keys
        if (key instanceof ECPublicKey) {
            if (key instanceof ECPublicKeyImpl) {
                return;
            }
        } else if (key instanceof ECPrivateKey) {
            if (key instanceof ECPrivateKeyImpl) {
                return;
            }
        } else {
            throw new InvalidKeyException("Neither a public nor a private key");
        }
        // ECKey does not extend Key, so we need to do a cast
        String keyAlg = ((Key)key).getAlgorithm();
        if (keyAlg.equals("EC") == false) {
            throw new InvalidKeyException("Not an EC key: " + keyAlg);
        }
        // XXX further sanity checks about whether this key uses supported
        // fields, point formats, etc. would go here
    }

    /**
     * Translate an EC key into a Sun EC key. If conversion is
     * not possible, throw an InvalidKeyException.
     * See also JCA doc.
     */
    protected Key engineTranslateKey(Key key) throws InvalidKeyException {
        if (key == null) {
            throw new InvalidKeyException("Key must not be null");
        }
        String keyAlg = key.getAlgorithm();
        if (keyAlg.equals("EC") == false) {
            throw new InvalidKeyException("Not an EC key: " + keyAlg);
        }
        if (key instanceof PublicKey) {
            return implTranslatePublicKey((PublicKey)key);
        } else if (key instanceof PrivateKey) {
            return implTranslatePrivateKey((PrivateKey)key);
        } else {
            throw new InvalidKeyException("Neither a public nor a private key");
        }
    }

    // see JCA doc
    protected PublicKey engineGeneratePublic(KeySpec keySpec)
            throws InvalidKeySpecException {
        try {
            return implGeneratePublic(keySpec);
        } catch (InvalidKeySpecException e) {
            throw e;
        } catch (GeneralSecurityException e) {
            throw new InvalidKeySpecException(e);
        }
    }

    // see JCA doc
    protected PrivateKey engineGeneratePrivate(KeySpec keySpec)
            throws InvalidKeySpecException {
        try {
            return implGeneratePrivate(keySpec);
        } catch (InvalidKeySpecException e) {
            throw e;
        } catch (GeneralSecurityException e) {
            throw new InvalidKeySpecException(e);
        }
    }

    // internal implementation of translateKey() for public keys. See JCA doc
    private PublicKey implTranslatePublicKey(PublicKey key)
            throws InvalidKeyException {
        if (key instanceof ECPublicKey) {
            if (key instanceof ECPublicKeyImpl) {
                return key;
            }
            ECPublicKey ecKey = (ECPublicKey)key;
            return new ECPublicKeyImpl(
                ecKey.getW(),
                ecKey.getParams()
            );
        } else if ("X.509".equals(key.getFormat())) {
            byte[] encoded = key.getEncoded();
            return new ECPublicKeyImpl(encoded);
        } else {
            throw new InvalidKeyException("Public keys must be instance "
                + "of ECPublicKey or have X.509 encoding");
        }
    }

    // internal implementation of translateKey() for private keys. See JCA doc
    private PrivateKey implTranslatePrivateKey(PrivateKey key)
            throws InvalidKeyException {
        if (key instanceof ECPrivateKey) {
            if (key instanceof ECPrivateKeyImpl) {
                return key;
            }
            ECPrivateKey ecKey = (ECPrivateKey)key;
            return new ECPrivateKeyImpl(
                ecKey.getS(),
                ecKey.getParams()
            );
        } else if ("PKCS#8".equals(key.getFormat())) {
            return new ECPrivateKeyImpl(key.getEncoded());
        } else {
            throw new InvalidKeyException("Private keys must be instance "
                + "of ECPrivateKey or have PKCS#8 encoding");
        }
    }

    // internal implementation of generatePublic. See JCA doc
    private PublicKey implGeneratePublic(KeySpec keySpec)
            throws GeneralSecurityException {
        if (keySpec instanceof X509EncodedKeySpec) {
            X509EncodedKeySpec x509Spec = (X509EncodedKeySpec)keySpec;
            return new ECPublicKeyImpl(x509Spec.getEncoded());
        } else if (keySpec instanceof ECPublicKeySpec) {
            ECPublicKeySpec ecSpec = (ECPublicKeySpec)keySpec;
            return new ECPublicKeyImpl(
                ecSpec.getW(),
                ecSpec.getParams()
            );
        } else {
            throw new InvalidKeySpecException("Only ECPublicKeySpec "
                + "and X509EncodedKeySpec supported for EC public keys");
        }
    }

    // internal implementation of generatePrivate. See JCA doc
    private PrivateKey implGeneratePrivate(KeySpec keySpec)
            throws GeneralSecurityException {
        if (keySpec instanceof PKCS8EncodedKeySpec) {
            PKCS8EncodedKeySpec pkcsSpec = (PKCS8EncodedKeySpec)keySpec;
            return new ECPrivateKeyImpl(pkcsSpec.getEncoded());
        } else if (keySpec instanceof ECPrivateKeySpec) {
            ECPrivateKeySpec ecSpec = (ECPrivateKeySpec)keySpec;
            return new ECPrivateKeyImpl(ecSpec.getS(), ecSpec.getParams());
        } else {
            throw new InvalidKeySpecException("Only ECPrivateKeySpec "
                + "and PKCS8EncodedKeySpec supported for EC private keys");
        }
    }

    @SuppressWarnings("unchecked")
	protected <T extends KeySpec> T engineGetKeySpec(Key key, Class<T> keySpec)
            throws InvalidKeySpecException {
        try {
            // convert key to one of our keys
            // this also verifies that the key is a valid EC key and ensures
            // that the encoding is X.509/PKCS#8 for public/private keys
            key = engineTranslateKey(key);
        } catch (InvalidKeyException e) {
            throw new InvalidKeySpecException(e);
        }
        if (key instanceof ECPublicKey) {
            ECPublicKey ecKey = (ECPublicKey)key;
            if (ECPublicKeySpec.class.isAssignableFrom(keySpec)) {
                return (T) new ECPublicKeySpec(
                    ecKey.getW(),
                    ecKey.getParams()
                );
            } else if (X509EncodedKeySpec.class.isAssignableFrom(keySpec)) {
                return (T) new X509EncodedKeySpec(key.getEncoded());
            } else {
                throw new InvalidKeySpecException
                        ("KeySpec must be ECPublicKeySpec or "
                        + "X509EncodedKeySpec for EC public keys");
            }
        } else if (key instanceof ECPrivateKey) {
            if (PKCS8EncodedKeySpec.class.isAssignableFrom(keySpec)) {
                return (T) new PKCS8EncodedKeySpec(key.getEncoded());
            } else if (ECPrivateKeySpec.class.isAssignableFrom(keySpec)) {
                ECPrivateKey ecKey = (ECPrivateKey)key;
                return (T) new ECPrivateKeySpec(
                    ecKey.getS(),
                    ecKey.getParams()
                );
            } else {
                throw new InvalidKeySpecException
                        ("KeySpec must be ECPrivateKeySpec or "
                        + "PKCS8EncodedKeySpec for EC private keys");
            }
        } else {
            // should not occur, caught in engineTranslateKey()
            throw new InvalidKeySpecException("Neither public nor private key");
        }
    }
}
