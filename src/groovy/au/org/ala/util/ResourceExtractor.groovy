package au.org.ala.util

import java.util.zip.ZipEntry
import java.util.zip.ZipFile

/**
 *
 * @author Doug Palmer &lt;Doug.Palmer@csiro.au&gt;
 *
 * Copyright (c) 2015 CSIRO
 */
class ResourceExtractor {
    /**
     * Copy an input stream to an output stream
     *
     * @param is The input stream
     * @param os The output stream
     */
    def copyStream(InputStream is, OutputStream os) {
        byte[] buffer = new byte[2048]
        int n

        while ((n = is.read(buffer)) >= 0)
            os.write(buffer, 0, n)
    }

    /**
     * Extract a resource from a URL to a target file.
     *
     * @param resource The source resource
     * @param target The target
     */
    def extractResource(URL resource, File target) {
        InputStream is = null
        OutputStream os = null

        target.parentFile.mkdirs()
        try {
            is = resource.openStream()
            os = new FileOutputStream(target)
            copyStream(is, os)
        } finally {
            if (is) is.close()
            if (os) os.close()
        }
    }

    /**
     * Unzip a file into a directory
     *
     * @param zip The zip file
     * @param dir The directory
     * @param deleteOnExit delete this file on exit
     */
    def unzip(File zip, File dir, boolean deleteOnExit) {
        ZipFile zf = new ZipFile(zip)

        for (ZipEntry entry: zf.entries()) {
            File of = new File(dir, entry.name)
            if (!of.parentFile.exists()) {
                of.parentFile.mkdirs()
                if (deleteOnExit)
                    of.parentFile.deleteOnExit()
            }
            InputStream is = zf.getInputStream(entry)
            OutputStream os = new FileOutputStream(of)

            copyStream(is, os)
            is.close()
            os.close()
            if (deleteOnExit)
                of.deleteOnExit()
        }
    }

    /**
     * Unzip a resource into a temporary directory
     * <p>
     * The resource and temporary directory are deleted on exit
     *
     * @param resource The resource
     *
     * @return The temporary directory
     */
    def unzipToTemp(URL resource, boolean deleteOnExit) {
        File zf = File.createTempFile("resource", ".zip")
        File location = File.createTempFile("unzipped", "")

        location.delete()
        location.mkdirs()
        if (deleteOnExit) {
            zf.deleteOnExit()
            location.deleteOnExit()
        }
        extractResource(resource, zf)
        unzip(zf, location, deleteOnExit)
        return location
    }

    /**
     * Remove the contents of a directory recursively
     *
     * @param dir The directory
     */
    def removeDir(File dir) {
        if (dir.isDirectory()) {
        for (File entry: dir.listFiles())
                 removeDir(entry)
        }
        dir.delete()
    }
}
