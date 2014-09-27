package com.kylerandolph.PasswordPopularity;

import com.google.common.base.Charsets;
import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnel;
import com.google.common.hash.PrimitiveSink;

import java.io.*;

/**
 * PopularityStore - a store of banned passwords
 * <p>
 *     Fill this data structure with a list of publicly leaked passwords.
 *     It may reduce poor password selection.
 *
 */
public class PopularityStore
{
    /** The anticipated number of passwords being put in the store */
    private static final int NUM_PASSWORDS = 100;

    /** The bloom filter that backs this store */
    protected BloomFilter<String> passwordBloomFilter;

    /** Needed for serialization */
    private static Funnel<String> passwordFunnel = new Funnel<String>() {
        @Override
        public void funnel(String s, PrimitiveSink primitiveSink) {
            primitiveSink.putString(s, Charsets.UTF_8);
        }
    };

    public PopularityStore() {
        passwordBloomFilter = BloomFilter.create(passwordFunnel, NUM_PASSWORDS);
    }

    public PopularityStore(BloomFilter<String> bf) {
        passwordBloomFilter = bf;
    }

    public void add(String password) {
        passwordBloomFilter.put(password);
    }

    /** By default the bloom filter has a 3% false positive rate. */
    public boolean mightContain(String password) {
        return passwordBloomFilter.mightContain(password);
    }

    /** Serialization methods. */

    public void writeToFile(File f) throws IOException {
        FileOutputStream fos = new FileOutputStream(f);
        OutputStream buffer = new BufferedOutputStream(fos);
        passwordBloomFilter.writeTo(buffer);
    }

    public static PopularityStore loadFromFile(String path) throws IOException {
        File f = new File(path);
        FileInputStream fis = new FileInputStream(f);
        InputStream buffer = new BufferedInputStream(fis);
        BloomFilter<String> bloomFilter = BloomFilter.readFrom(buffer, passwordFunnel);
        return new PopularityStore(bloomFilter);
    }
}
