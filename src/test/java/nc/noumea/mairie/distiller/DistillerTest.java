/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nc.noumea.mairie.distiller;

import com.lowagie.text.pdf.PdfReader;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;

import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileInputStream;
import jcifs.smb.SmbFileOutputStream;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

/**
 *
 * @author salad74
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class DistillerTest {

    private Configuration conf;
    private String testFileName;
    private String destinationPath;
    private String as400User;
    private NtlmPasswordAuthentication auth;
    private String pdfDirectory;
    Logger logger = Logger.getLogger(DistillerTest.class.getName());

    /**
     * Uploads test image to remote samba share
     */
    @Before
    public void init() {
        try {
            conf = new PropertiesConfiguration("distiller.properties");
            testFileName = conf.getString("image.to.distill");
            Assert.assertNotNull(testFileName);

            destinationPath = conf.getString("target.directory");
            Assert.assertNotNull(destinationPath);

            as400User = conf.getString("as400.user");
            Assert.assertNotNull(as400User);

            auth = new NtlmPasswordAuthentication(as400User);
            Assert.assertNotNull(auth);
            
            pdfDirectory = conf.getString("pdf.directory");
            Assert.assertNotNull(pdfDirectory);
            

        } catch (ConfigurationException ex) {
            conf = null;
        }
    }

    @Test
    public void aTestTestConf() {
        Assert.assertNotNull(conf);
    }

    @Test
    public void bCleanUpTestFiles(){
        // delete input test image
        try{
            SmbFile dFile = new SmbFile(destinationPath + "/" + testFileName, auth);
            if(dFile.exists()){
                dFile.delete();
            }

            dFile = new SmbFile(destinationPath + "/" + testFileName, auth);
            Assert.assertFalse(dFile.exists());
        }
        catch(Exception ex){
            Assert.assertNull(ex);
        }
        
        // delete distilled pdf
        try{
            String pdfFileName = FilenameUtils.removeExtension(testFileName) + ".pdf";
            SmbFile sFile = new SmbFile(pdfDirectory + pdfFileName, auth);
            if(sFile.exists()){
                sFile.delete();
            }
            sFile = new SmbFile(pdfDirectory + pdfFileName, auth);
            Assert.assertFalse(sFile.exists());
        }
        catch(Exception ex){
            Assert.assertNull(ex);
        }
    }
    @Test
    public void cUploadImage() {
        try {
            logger.info("testFileName : " + testFileName);
            logger.info("destinationPath : " + destinationPath);
            logger.info("as400User : " + as400User);
            
            URL url = getClass().getResource("/" + testFileName);
            File testFile = new File(url.getFile());
            Assert.assertTrue(testFile.exists());
                    
            FileInputStream stream = new FileInputStream(testFile);

            SmbFile dFile = new SmbFile(destinationPath + "/" + testFileName, auth);
            SmbFileOutputStream outStream = new SmbFileOutputStream(dFile);
            outStream.write(IOUtils.toByteArray(stream));
            outStream.flush();
            outStream.close();
            stream.close();
            Assert.assertTrue(dFile.exists());
            Assert.assertTrue(dFile.canRead());
            Assert.assertNotNull(dFile.getUncPath());
        } catch (Exception ex) {
            ex.printStackTrace();
            Assert.assertNull(ex);
        }
        
        // image should have been uploaded
        
    }


    @Test
    public void dWaitForDistiller(){
        try{
            
            boolean exists = true;
            do {
                SmbFile sFile = new SmbFile(destinationPath + "/" + testFileName, auth);
                exists = sFile.exists();
                logger.info("image still waiting for being processed by distiller.");
                Thread.sleep(500);
            } while (exists);
            SmbFile sFile = new SmbFile(destinationPath + "/" + testFileName, auth);
            Assert.assertFalse(sFile.exists());
            logger.info("image now being processed by distiller.");
        }
        catch(Exception ex){
            ex.printStackTrace();
        	Assert.assertNull(ex);
        }
    }
    
    @Test
    public void eCheckDistillation(){
        try{
            
            boolean distilled = false;
            String pdfFileName = FilenameUtils.removeExtension(testFileName) + ".pdf";
            
            logger.info("pdf filename : " + pdfDirectory + pdfFileName);
            SmbFile sFile = new SmbFile(pdfDirectory + pdfFileName, auth);
            distilled = sFile.exists();
            while(distilled == false) {
                sFile = new SmbFile(pdfDirectory + pdfFileName, auth);
                distilled = sFile.exists();
                logger.info("image still being processed by distiller...");
                Thread.sleep(100);
            }
            sFile = new SmbFile(pdfDirectory + pdfFileName, auth);
            Assert.assertTrue(sFile.exists());
            logger.info("image has been been processed by distiller into a pdf.");
        }
        catch(Exception ex){
        	ex.printStackTrace();
        	Assert.assertNull(ex);
        }
    }
    
    @Test
    public void fTestPdfNotCorrupted(){
        // get the stream of the smb file
        try{
            String pdfFileName = FilenameUtils.removeExtension(testFileName) + ".pdf";
            SmbFile sFile = new SmbFile(pdfDirectory + pdfFileName, auth);
            SmbFileInputStream stream = new SmbFileInputStream(sFile);
            
            PdfReader reader = new PdfReader(stream);
            Assert.assertNotNull(reader);
            int nbPages = reader.getNumberOfPages();
            Assert.assertTrue("A not corrupted pdf has at least one page", nbPages > 0);
            reader.close();
            stream.close();
        
        }
        catch(Exception ex){
            ex.printStackTrace();
            Assert.assertNull(ex);
        }
        
    }

}
