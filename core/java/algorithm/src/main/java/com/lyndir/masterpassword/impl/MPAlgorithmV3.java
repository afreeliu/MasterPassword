//==============================================================================
// This file is part of Master Password.
// Copyright (c) 2011-2017, Maarten Billemont.
//
// Master Password is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// Master Password is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You can find a copy of the GNU General Public License in the
// LICENSE file.  Alternatively, see <http://www.gnu.org/licenses/>.
//==============================================================================

package com.lyndir.masterpassword.impl;

import com.google.common.primitives.Bytes;
import com.lyndir.lhunath.opal.system.CodeUtils;
import com.lyndir.masterpassword.MPAlgorithm;
import com.lyndir.masterpassword.MPKeyPurpose;
import java.util.Arrays;


/**
 * @author lhunath, 2014-08-30
 * @see Version#V3
 */
public class MPAlgorithmV3 extends MPAlgorithmV2 {

    @Override
    public byte[] masterKey(final String fullName, final char[] masterPassword) {

        byte[] fullNameBytes       = fullName.getBytes( mpw_charset() );
        byte[] fullNameLengthBytes = toBytes( fullNameBytes.length );

        String keyScope = MPKeyPurpose.Authentication.getScope();
        logger.trc( "keyScope: %s", keyScope );

        // Calculate the master key salt.
        logger.trc( "masterKeySalt: keyScope=%s | #fullName=%s | fullName=%s",
                    keyScope, CodeUtils.encodeHex( fullNameLengthBytes ), fullName );
        byte[] masterKeySalt = Bytes.concat( keyScope.getBytes( mpw_charset() ), fullNameLengthBytes, fullNameBytes );
        logger.trc( "  => masterKeySalt.id: %s", CodeUtils.encodeHex( toID( masterKeySalt ) ) );

        // Calculate the master key.
        logger.trc( "masterKey: scrypt( masterPassword, masterKeySalt, N=%d, r=%d, p=%d )",
                    scrypt_N(), scrypt_r(), scrypt_p() );
        byte[] masterPasswordBytes = toBytes( masterPassword );
        byte[] masterKey           = scrypt( masterKeySalt, masterPasswordBytes, mpw_dkLen() );
        Arrays.fill( masterKeySalt, (byte) 0 );
        Arrays.fill( masterPasswordBytes, (byte) 0 );
        if (masterKey == null)
            throw new IllegalStateException( "Could not derive master key." );
        logger.trc( "  => masterKey.id: %s", CodeUtils.encodeHex( toID( masterKey ) ) );

        return masterKey;
    }

    // Configuration

    @Override
    public Version version() {
        return MPAlgorithm.Version.V3;
    }
}
