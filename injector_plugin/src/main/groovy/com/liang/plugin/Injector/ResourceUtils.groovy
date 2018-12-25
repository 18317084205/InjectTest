package com.liang.plugin.Injector

import org.gradle.api.Project

import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipOutputStream

public class ResourceUtils {
    def unZip(File src, String savePath) throws IOException {
        def count = -1;
        def index = -1;
        def flag = false;
        def file1 = null;
        def is = null;
        def fos = null;
        def bos = null;

        ZipFile zipFile = new ZipFile(src);
        Enumeration<?> entries = zipFile.entries();

        while (entries.hasMoreElements()) {
            def buf = new byte[2048];
            ZipEntry entry = (ZipEntry) entries.nextElement();
            def filename = entry.getName();

            filename = savePath + filename;
            File file2 = file(filename.substring(0, filename.lastIndexOf('/')));

            if (!file2.exists()) {
                file2.mkdirs()
            }

            if (!filename.endsWith("/")) {

                file1 = file(filename);
                file1.createNewFile();
                is = zipFile.getInputStream(entry);
                fos = new FileOutputStream(file1);
                bos = new BufferedOutputStream(fos, 2048);

                while ((count = is.read(buf)) > -1) {
                    bos.write(buf, 0, count);
                }

                bos.flush();

                fos.close();
                is.close();

            }
        }

        zipFile.close();

    }

    def zipFolder(String srcPath, String savePath) throws IOException {
        def saveFile = file(savePath)
        saveFile.delete()
        saveFile.createNewFile()
        def outStream = new ZipOutputStream(new FileOutputStream(saveFile))
        def srcFile = file(srcPath)
        zipFile(srcFile.getAbsolutePath() + File.separator, "", outStream)
        outStream.finish()
        outStream.close()
    }

    def zipFile(String folderPath, String fileString, ZipOutputStream out) throws IOException {
        File srcFile = file(folderPath + fileString)
        if (srcFile.isFile()) {
            def zipEntry = new ZipEntry(fileString)
            def inputStream = new FileInputStream(srcFile)
            out.putNextEntry(zipEntry)
            def len
            def buf = new byte[2048]
            while ((len = inputStream.read(buf)) != -1) {
                out.write(buf, 0, len)
            }
            out.closeEntry()
        } else {
            def fileList = srcFile.list()
            if (fileList.length <= 0) {
                def zipEntry = new ZipEntry(fileString + File.separator)
                out.putNextEntry(zipEntry)
                out.closeEntry()
            }

            for (def i = 0; i < fileList.length; i++) {
                zipFile(folderPath, fileString.equals("") ? fileList[i] : fileString + File.separator + fileList[i], out)
            }
        }
    }


    def replaceResIdInRText(File textSymbolOutputFile, String newPkgIdStr) {
        println textSymbolOutputFile.path
        def list1 = []
        textSymbolOutputFile.withReader('UTF-8') { reader ->
            reader.eachLine {
                if (it.contains('0x7f')) {
                    it = it.replace('0x7f', newPkgIdStr)
                }
                list1.add(it + "\n")
            }
        }
        textSymbolOutputFile.withWriter('UTF-8') { writer ->
            list1.each {
                writer.write(it)
            }
        }
    }

    static def replaceResIdInJavaDir(File srcFile, Project project) {
        print("project replaceResIdInJavaDir..." + srcFile.path + "\n")
        if (srcFile.isFile()) {
            print("project name..." + srcFile.name + "\n")
            if (srcFile.name == "R.java") {
                def list = []
                project.file(srcFile).withReader('UTF-8') { reader ->
                    reader.eachLine {
                        if (it.contains("=")) {
                            def str = it.split(" ")
                            if (str.length > 5) {
                                it = it.replace('static', "static final")
                                print("project replace reader..." + it + "\n")
                            }
                        }
                        list.add(it + "\n")
                    }
                }
                project.file(srcFile).withWriter('UTF-8') { writer ->
                    list.each {
                        print("project replace write..." + it )
                        writer.write(it)
                    }
                }
            }
        } else {
            def fileList = srcFile.listFiles()
            fileList.each {
                print("project fileList..." + it.name + "\n")
                replaceResIdInJavaDir(it, project)
            }
//            for (def i = 0; i < fileList.length; i++) {
//                replaceResIdInJavaDir(fileList[i], project)
//            }
        }
    }

    def replaceResIdInResDir(String resPath, int newPkgId) throws Exception {
        File resFile = file(resPath)
        if (resFile.isFile()) {
            if (resPath.endsWith(".xml")) {
                replaceResIdInXml(resFile, newPkgId)
            }
        } else {
            def fileList = resFile.list()
            if (fileList == null || fileList.length <= 0) {
                return
            }
            for (def i = 0; i < fileList.length; i++) {
                replaceResIdInResDir(resPath + File.separator + fileList[i], newPkgId)
            }
        }
    }

    def replaceResIdInXml(File resFile, int newPkgId) throws Exception {
        def buf = resFile.bytes

        for (def i = 0; i + 7 < buf.length; i = i + 4) {
            if ((buf[i] & 0xFF) == 0x80 && buf[i + 1] == 0x01 && buf[i + 2] == 0x08 && buf[i + 3] == 0x00) {
                int chunkSize = ((buf[i + 7] & 0xFF) << 24) + ((buf[i + 6] & 0xFF) << 16) + ((buf[i + 5] & 0xFF) << 8) + (buf[i + 4] & 0xFF)
                for (def j = i + 8; j < i + chunkSize; j = j + 4) {
                    if (buf[j + 3] == 0x7f) {
                        buf[j + 3] = newPkgId
                        println resFile.name + "," + j
                    }
                }
                i = i + chunkSize - 4
                continue
            }
            if (buf[i] == 0x08 && buf[i + 1] == 0x00 && buf[i + 2] == 0x00 && (buf[i + 3] == 0x01 || buf[i + 3] == 0x02)) {
                if (buf[i + 7] == 0x7f) {
                    buf[i + 7] = newPkgId
                    //println resFile.name + "," + (i+7)
                }
            }
        }

        def outStream = new FileOutputStream(resFile)
        outStream.write(buf, 0, buf.length)
        outStream.flush()
        outStream.close()
    }

    def replaceResIdInArsc(File resFile, int newPkgId, String pkgName) throws Exception {
        def buf = resFile.bytes
        def dynamicRefBytes = getDynamicRef(pkgName, newPkgId)
        int size = buf.length + dynamicRefBytes.length
        buf[4] = size & 0x000000ff
        buf[5] = (size & 0x0000ff00) >> 8
        buf[6] = (size & 0x00ff0000) >> 16
        buf[7] = (size & 0xff000000) >> 24

        for (def i = 0; i + 15 < buf.length;) {
            if (buf[i] == 0x00 && buf[i + 1] == 0x02 && buf[i + 8] == 0x7F && buf[i + 9] == 0x00 && buf[i + 10] == 0x00 && buf[i + 11] == 0x00) {
                //println "packagePosition:" + i
                int headSize = ((buf[i + 3] & 0xFF) << 8) + (buf[i + 2] & 0xFF)
                int pkgSize = ((buf[i + 7] & 0xFF) << 24) + ((buf[i + 6] & 0xFF) << 16) + ((buf[i + 5] & 0xFF) << 8) + (buf[i + 4] & 0xFF) + dynamicRefBytes.length
                buf[i + 4] = pkgSize & 0x000000ff
                buf[i + 5] = (pkgSize & 0x0000ff00) >> 8
                buf[i + 6] = (pkgSize & 0x00ff0000) >> 16
                buf[i + 7] = (pkgSize & 0xff000000) >> 24

                buf[i + 8] = newPkgId
                i += headSize
                continue
            }
            if (buf[i] == 0x01 && buf[i + 1] == 0x02 && buf[i + 9] == 0x00 && buf[i + 10] == 0x00 && buf[i + 11] == 0x00) {
                int offsetStart = i + ((buf[i + 3] & 0xFF) << 8) + (buf[i + 2] & 0xFF)
                int offsetSize = ((buf[i + 15] & 0xFF) << 24) + ((buf[i + 14] & 0xFF) << 16) + ((buf[i + 13] & 0xFF) << 8) + (buf[i + 12] & 0xFF)
                int dataStart = offsetStart + offsetSize * 4
                int dataEnd = i + ((buf[i + 7] & 0xFF) << 24) + ((buf[i + 6] & 0xFF) << 16) + ((buf[i + 5] & 0xFF) << 8) + (buf[i + 4] & 0xFF) - 1
                //println "chuck start " + i + " offsetStart " + offsetStart + " offsetSize " + offsetSize + " dataStart " + dataStart + " dataEnd " + dataEnd
                if (offsetStart < dataStart && dataStart < dataEnd && dataEnd < buf.length) {
                    //println "chuck start " + i
                    replaceResIdInArscConfigList(buf, offsetStart, offsetSize, dataStart, dataEnd, newPkgId)
                    i = dataEnd + 1
                    continue
                }
            }
            i = i + 4
        }

        def outStream = new FileOutputStream(resFile)
        outStream.write(buf, 0, buf.length)
        outStream.write(dynamicRefBytes)
        outStream.flush()
        outStream.close()
    }

    def replaceResIdInArscConfigList(byte[] buf, int offsetStart, int offsetSize, int dataStart, int dataEnd, int newPkgId) throws Exception {
        //println "offsetStart " + offsetStart + " offsetSize " + offsetSize + " dataStart " + dataStart + " dataEnd " + dataEnd
        if (offsetSize == 1) {
            replaceResIdInArscEntry(buf, dataStart, dataEnd, newPkgId)
        } else {
            int lastoffset = dataStart
            for (def i = offsetStart + 4; i + 3 < dataStart; i = i + 4) {
                if (buf[i] == -1 && buf[i + 1] == -1 && buf[i + 2] == -1 && buf[i + 3] == -1) {
                    continue
                }
                int offset = dataStart + ((buf[i + 3] & 0xFF) << 24) + ((buf[i + 2] & 0xFF) << 16) + ((buf[i + 1] & 0xFF) << 8) + (buf[i] & 0xFF)
                replaceResIdInArscEntry(buf, lastoffset, offset, newPkgId)
                lastoffset = offset
            }
            replaceResIdInArscEntry(buf, lastoffset, dataEnd, newPkgId)
        }
    }

    def replaceResIdInArscEntry(byte[] buf, int entryStart, int entryEnd, int newPkgId) {
        //println "entryStart " + entryStart + " entryEnd " + entryEnd
        if (buf[entryStart] == 0x08 && buf[entryStart + 1] == 0x00 && buf[entryStart + 2] == 0x00 && buf[entryStart + 3] == 0x00) {
            if (entryStart + 15 > entryEnd) {
                return
            }
            if (buf[entryStart + 8] == 0x08 && buf[entryStart + 9] == 0x00 && buf[entryStart + 10] == 0x00 && buf[entryStart + 11] == 0x01 && buf[entryStart + 15] == 0x7F) {
                buf[entryStart + 15] = newPkgId
                //println entryStart+15
            }
        }
        if (buf[entryStart] == 0x10 && buf[entryStart + 1] == 0x00 && buf[entryStart + 2] == 0x01 && buf[entryStart + 3] == 0x00) {
            if (entryStart + 15 > entryEnd) {
                return
            }
            if (buf[entryStart + 11] == 0x7F) {
                buf[entryStart + 11] = newPkgId
                //println entryStart+11
            }
            int size = ((buf[entryStart + 15] & 0xFF) << 24) + ((buf[entryStart + 14] & 0xFF) << 16) + ((buf[entryStart + 13] & 0xFF) << 8) + (buf[entryStart + 12] & 0xFF)
            for (def i = 0; i < size; i++) {
                if (buf[entryStart + 19 + i * 12] == 0x7F) {
                    buf[entryStart + 19 + i * 12] = newPkgId
                    //println entryStart+19+i*12
                }
                if (buf[entryStart + 20 + i * 12] == 0x08 && buf[entryStart + 21 + i * 12] == 0x00 && buf[entryStart + 22 + i * 12] == 0x00 && (buf[entryStart + 23 + i * 12] == 0x01 || buf[entryStart + 23 + i * 12] == 0x02) && buf[entryStart + 27 + i * 12] == 0x7F) {
                    buf[entryStart + 27 + i * 12] = newPkgId
                    //println entryStart+27+i*12
                }
            }
        }
    }

    def getDynamicRef(String pkgName, int newPkgId) {
        int typeLength = 2
        int headSizeLength = 2
        int totalSizeLength = 4
        int countLength = 4
        int pkgIdLength = 4

        def pkgbyte = pkgName.bytes
        int pkgLength = pkgbyte.length * 2
        if (pkgLength % 4 != 0) {
            pkgLength += 2
        }
        if (pkgLength < 256) {
            pkgLength = 256
        }


        def pkgBuf = new byte[typeLength + headSizeLength + totalSizeLength + countLength + pkgIdLength + pkgLength]

        pkgBuf[0] = 0x03
        pkgBuf[1] = 0x02

        pkgBuf[typeLength] = 0x0c
        pkgBuf[typeLength + 1] = 0x00

        pkgBuf[typeLength + headSizeLength] = pkgBuf.length & 0x000000ff
        pkgBuf[typeLength + headSizeLength + 1] = (pkgBuf.length & 0x0000ff00) >> 8
        pkgBuf[typeLength + headSizeLength + 2] = (pkgBuf.length & 0x00ff0000) >> 16
        pkgBuf[typeLength + headSizeLength + 3] = (pkgBuf.length & 0xff000000) >> 24

        pkgBuf[typeLength + headSizeLength + totalSizeLength] = 0x01

        pkgBuf[typeLength + headSizeLength + totalSizeLength + countLength] = newPkgId

        for (int i = 0; i < pkgbyte.length; i++) {
            pkgBuf[typeLength + headSizeLength + totalSizeLength + countLength + pkgIdLength + i * 2] = pkgbyte[i]
        }

        return pkgBuf
    }
}
