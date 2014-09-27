package com.kylerandolph.PasswordPopularity;

import com.google.common.io.ByteSink;
import com.google.common.io.ByteStreams;
import com.google.common.io.Files;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Unit test for simple PopularityStore.
 */
public class PopularityStoreTest
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public PopularityStoreTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( PopularityStoreTest.class );
    }

    private PopularityStore fakePopularityStore() {
        PopularityStore p = new PopularityStore();
        p.add("monkey");
        p.add("ninja");
        p.add("pizza");
        return p;
    }

    public void testMightContain()
    {
        PopularityStore p = fakePopularityStore();
        assertTrue(p.mightContain("ninja"));
        assertTrue(p.mightContain("pizza"));
        assertFalse(p.mightContain("donkey"));
    }

    public void testAdditions()
    {
        PopularityStore p = fakePopularityStore();
        p.add("pirate");
        assertTrue(p.mightContain("pirate"));
        assertTrue(p.mightContain("pizza"));
        assertFalse(p.mightContain("pirates"));
    }

    private String serializeStore(PopularityStore store, File f) throws IOException {
        store.writeToFile(f);

        return f.getAbsolutePath();
    }

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    File fizile;
    File fizile2;

    @Before
    public void bebefore() throws IOException {
        fizile2 = File.createTempFile("dont", "care");
        fizile = temporaryFolder.newFile("whateva");
    }

    public void testSerialization() throws IOException
    {
        PopularityStore p_to_store = fakePopularityStore();
        String tempPath = serializeStore(p_to_store, fizile2);

        PopularityStore p = PopularityStore.loadFromFile(tempPath);

        assertNotNull(p);
        assertTrue(p.mightContain("ninja"));
        assertTrue(p.mightContain("pizza"));
        assertFalse(p.mightContain("donkey"));
    }

    public void testLoadFromTextFile() throws IOException {
        InputStream in = this.getClass().getClassLoader().getResourceAsStream("myspace.txt");
        byte[] data = ByteStreams.toByteArray(in);
        fizile = temporaryFolder.newFile("myspace.txt");
        String path = fizile.getAbsolutePath();
        ByteSink out = Files.asByteSink(fizile);
        out.write(data);

        PopularityStore popularityStore = PopularityStore.loadFromTextFile(path);

        assertTrue(popularityStore.mightContain("nirvana1"));
    }

}
