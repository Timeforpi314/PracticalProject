package com.sasha;

// java implementation of https://github.com/mjosaarinen/tiny_sha3/blob/master/sha3.c

public class Sha3 {
    static int KECCAKF_ROUNDS = 24;

    static long[] keccakf_rndc = new long[]{
            Long.parseUnsignedLong("0000000000000001", 16),
            Long.parseUnsignedLong("0000000000008082", 16),
            Long.parseUnsignedLong("800000000000808a", 16),
            Long.parseUnsignedLong("8000000080008000", 16),
            Long.parseUnsignedLong("000000000000808b", 16),
            Long.parseUnsignedLong("0000000080000001", 16),
            Long.parseUnsignedLong("8000000080008081", 16),
            Long.parseUnsignedLong("8000000000008009", 16),
            Long.parseUnsignedLong("000000000000008a", 16),
            Long.parseUnsignedLong("0000000000000088", 16),
            Long.parseUnsignedLong("0000000080008009", 16),
            Long.parseUnsignedLong("000000008000000a", 16),
            Long.parseUnsignedLong("000000008000808b", 16),
            Long.parseUnsignedLong("800000000000008b", 16),
            Long.parseUnsignedLong("8000000000008089", 16),
            Long.parseUnsignedLong("8000000000008003", 16),
            Long.parseUnsignedLong("8000000000008002", 16),
            Long.parseUnsignedLong("8000000000000080", 16),
            Long.parseUnsignedLong("000000000000800a", 16),
            Long.parseUnsignedLong("800000008000000a", 16),
            Long.parseUnsignedLong("8000000080008081", 16),
            Long.parseUnsignedLong("8000000000008080", 16),
            Long.parseUnsignedLong("0000000080000001", 16),
            Long.parseUnsignedLong("8000000080008008", 16)
    };

    static int[] keccakf_rotc = {
            1,  3,  6,  10, 15, 21, 28, 36, 45, 55, 2,  14,
            27, 41, 56, 8,  25, 43, 62, 18, 39, 61, 20, 44
    };

    static int[] keccakf_piln = {
            10, 7,  11, 17, 18, 3, 5,  16, 8,  21, 24, 4,
            15, 23, 19, 13, 12, 2, 20, 14, 22, 9,  6,  1
    };

    long ROTL64(long x, long y) {
        return (((x) << (y)) | ((x) >> (64 - (y))));
    }

    public byte[] sha3(byte[] in, int mdlen) {
        Sha3Context ctx = new Sha3Context();

        sha3_init(ctx, mdlen);
        sha3_update(ctx, in);
        return sha3_final(ctx);
    }

    public void sha3_init(Sha3Context ctx, int mdlen) {
        ctx.mdlen = mdlen;
        ctx.rsiz = 200 - 2 * mdlen;
        ctx.pt = 0;

//        for ( int i = 0; i < 200; i++) {
//            ctx.b[i] = 0;
//        }

        for ( int i = 0; i < 25; i++) {
            ctx.q[i] = 0;
        }
    }

    void sha3_update(Sha3Context ctx, byte[] data) {

    }

    byte[] sha3_final(Sha3Context ctx) {

        return new byte[] { 0 };
    }

    void sha3_keccakf(long[] st) {
        int i, j, r;
        long t;
        long[] bc = new long[5];

        // convert to little endian
        for (i = 0; i < 25; i++) {
            st[i] = Long.reverseBytes(st[i]);
        }

        // do some math shit
        for (r = 0; r < KECCAKF_ROUNDS; r++) {

            // Theta
            for (i = 0; i < 5; i++)
                bc[i] = st[i] ^ st[i + 5] ^ st[i + 10] ^ st[i + 15] ^ st[i + 20];

            for (i = 0; i < 5; i++) {
                t = bc[(i + 4) % 5] ^ ROTL64(bc[(i + 1) % 5], 1);
                for (j = 0; j < 25; j += 5)
                    st[j + i] ^= t;
            }

            // Rho Pi
            t = st[1];
            for (i = 0; i < 24; i++) {
                j = keccakf_piln[i];
                bc[0] = st[j];
                st[j] = ROTL64(t, keccakf_rotc[i]);
                t = bc[0];
            }

            //  Chi
            for (j = 0; j < 25; j += 5) {
                for (i = 0; i < 5; i++)
                    bc[i] = st[j + i];
                for (i = 0; i < 5; i++)
                    st[j + i] ^= (~bc[(i + 1) % 5]) & bc[(i + 2) % 5];
            }

            //  Iota
            st[0] ^= keccakf_rndc[r];
        }

        // convert back to big endian
        for (i = 0; i < 25; i++) {
            st[i] = Long.reverseBytes(st[i]);
        }
    }
}
