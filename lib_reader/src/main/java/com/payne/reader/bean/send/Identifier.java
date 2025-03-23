package com.payne.reader.bean.send;

import com.payne.reader.util.ArrayUtils;
import com.payne.reader.util.CheckUtils;

import java.security.InvalidParameterException;
import java.util.Objects;

/**
 * @author naz
 * Date 2020/9/3
 */
public final class Identifier {
    private byte[] identifiers;

    Identifier(Builder builder) {
        this.identifiers = builder.identifiers;
    }

    /**
     * Get the reader identification code (12 bytes).
     *
     * @return Byte array
     */
    public byte[] getIdentifiers() {
        return identifiers;
    }

    /**
     * Build a new {@link Identifier}.
     */
    public static class Builder {
        private byte[] identifiers;

        public Builder() {
            this.identifiers = null;
        }

        /**
         * Set the reader ID
         *
         * @param identifiers Byte array identifier
         */
        public Builder setIdentifiers(byte[] identifiers) {
            Objects.requireNonNull(identifiers);
            int demandLength = 12;
            if (identifiers.length < demandLength) {
                byte[] src = identifiers;
                identifiers = new byte[demandLength];
                System.arraycopy(src, 0, identifiers, identifiers.length - src.length, src.length);
            } else if (identifiers.length > demandLength) {
                byte[] src = identifiers;
                identifiers = new byte[demandLength];
                System.arraycopy(src, 0, identifiers, 0, identifiers.length);
            }
            this.identifiers = identifiers;
            return this;
        }

        /**
         * Set the reader ID
         *
         * @param hexIdentifier Hexadecimal string identification code
         * @return this
         */
        public Builder setIdentifiers(String hexIdentifier) {
            Objects.requireNonNull(hexIdentifier);
            if (CheckUtils.isNotHexString(hexIdentifier)) {
                throw new InvalidParameterException("hexIdentifier must be a hexadecimal string!");
            }
            byte[] identifiers = ArrayUtils.hexStringToBytes(hexIdentifier);
            return setIdentifiers(identifiers);
        }

        /**
         * Create the {@link Identifier} instance using the configured values.
         *
         * @return instance
         */
        public Identifier build() {
            if (identifiers == null) {
                identifiers = new byte[12];
            }
            return new Identifier(this);
        }
    }
}
