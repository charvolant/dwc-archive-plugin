package au.org.ala.util

import org.apache.commons.logging.Log

import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipOutputStream

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

        for (ZipEntry entry : zf.entries()) {
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
            for (File entry : dir.listFiles())
                removeDir(entry)
        }
        dir.delete()
    }

    /**
     * Clean the contents of a directory.
     * <p>
     * Any files that have not been accessed after a certain time
     * are deleted.
     *
     * @param dir The directory/file to clean
     * @param before The expiry date for the file
     * @param thisDir Remove this directory if no longer relevant
     * @param log Log deletions to this log if not null
     *
     * @return True if the directory/file has been deleted
     */
    boolean cleanDir(File dir, Date before, boolean thisDir, Log log = null) {
        if (dir.isDirectory()) {
            def deleted = thisDir && (dir.lastModified() < before.time)
            for (File entry: dir.listFiles())
                deleted = cleanDir(entry, before, true, log) && deleted
            if (deleted) {
                deleted = dir.delete()
                if (log) log.debug "Removed empty directory ${dir}"
            }
            return deleted
        } else {
            if (dir.lastModified() < before.time) {
                if (log) log.debug "Expired ${dir} last modified ${new Date(dir.lastModified)}"
                return dir.delete()
            } else
                return false
        }
    }

    /**
     * Copy a zip file directory into a zip file output stream
     * <p>
     *     Subdirectories are recursively followed.
     *
     * @param dir The directory
     * @param prefix The path prefix to use (null for none)
     * @param os The zip file output stream
     */
    def addZip(File dir, String prefix, ZipOutputStream os) {
        dir.listFiles().each { file ->
            def path = prefix == null ? file.name : prefix + "/" + file.name
            if (file.directory)
                addZip(file, path, os)
            else {
                os.putNextEntry(new ZipEntry(path))
                def is = new FileInputStream(file)
                copyStream(is, os)
                is.close()
            }
        }
    }

    /**
     * Zip a directory into a zip file
     *
     * @param dir The directory
     * @param zipFile The zip file to create
     */
    def zip(File dir, File zipFile) {
        def zfo = new FileOutputStream(zipFile)
        ZipOutputStream os = new ZipOutputStream(zfo)

        try {
            addZip(dir, null, os)
        } finally {
            os.close()
        }
    }
}
