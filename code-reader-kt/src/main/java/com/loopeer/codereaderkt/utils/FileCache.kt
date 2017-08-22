package com.loopeer.codereaderkt.utils

import android.content.Context
import android.content.pm.PackageManager
import android.os.Environment
import com.loopeer.codereaderkt.CodeReaderApplication
import com.loopeer.codereaderkt.model.DirectoryNode
import java.io.File
import java.util.*


class FileCache {
    companion object {
        private val EXTERNAL_STORAGE_PERMISSION = "android.permission.WRITE_EXTERNAL_STORAGE"
        private var instance: FileCache? = null
        private val cachePath = Environment.getExternalStorageDirectory().toString() + "/CodeReader/repo/"
        private var cacheDir: File? = null
        val cacheDirPath = "/repo/"

    }


    init {
        cacheDir = if (hasSDCard() && hasExternalStoragePermission(CodeReaderApplication.getAppContext())) {
            createFilePath(cachePath)
        } else {
            createFilePath("${CodeReaderApplication.getAppContext()!!.cacheDir}$cacheDirPath")//字符串拼接
        }
    }

    fun createFilePath(filePath: String): File {
        return createFilePath(File(filePath))
    }

    fun createFilePath(file: File): File {
        if (!file.exists()) {
            file.mkdirs()//mkdir()和mkdirs区别 前者只会建立一级目录，后者可建多级
        }
        return file
    }

    fun getInstance(): FileCache {
        if (null == instance) {
            instance = FileCache()
        }
        return instance as FileCache
    }

    fun hasSDCard(): Boolean {
        return Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
        //是否存在SD卡
    }

    fun getFileDirectoryNode(): DirectoryNode? {
        var file = File(cacheDir, "cardStackView")
        if (file.listFiles() == null || file.listFiles().isEmpty()) return null
        return getFileDirectory(file)
    }

    fun getCacheDir(): File? {
        return cacheDir
    }

    fun getRepoAbsolutePath(repoName: String): String =
            getCacheDir()!!.path + File.separator + repoName


    fun getFileDirectory(file: File): DirectoryNode? {
        if (file == null)
            return null
        var directoryNode = DirectoryNode()
        directoryNode.name = file.name
        directoryNode.absolutePath = file.absolutePath
        if (file.isDirectory){
            directoryNode.isDirectory = true
            directoryNode.pathNodes = ArrayList<DirectoryNode>()
            for (c in file.listFiles()){
                if (c.name.startsWith(".")||c.name.startsWith("_")) continue
                var childNode = getFileDirectory(c)
                (directoryNode.pathNodes as ArrayList<DirectoryNode>).add(childNode!!)
            }
            if (!directoryNode.pathNodes.isEmpty()){
//                Collections.sort(directoryNode.pathNodes)
            }
        }
        return directoryNode
    }

    fun hasExternalStoragePermission(context: Context): Boolean {
        var perm: Int = context.checkCallingOrSelfPermission(EXTERNAL_STORAGE_PERMISSION)
        return perm == PackageManager.PERMISSION_GRANTED//是否已经获取sd卡读写权限
    }

    fun deleteFilesByDirectory(directory: File?) {
        if (directory != null && directory.exists() && directory.list() != null) {
            for (item in directory.listFiles()) {
                if (item.isDirectory) {
                    deleteFilesByDirectory(item)
                } else {
                    item.delete()
                }
            }
        }
    }

}