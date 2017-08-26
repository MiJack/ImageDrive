package cn.mijack.imagedrive.entity

import cn.mijack.imagedrive.entity.Image
import cn.mijack.imagedrive.entity.Media
import java.security.cert.CertPath
import java.util.*

/**
 * @author admin
 * @date 2017/8/26
 */
class Folder : Media {
    var count: Int = 0
    var data: List<Image> = ArrayList()

    constructor(path: String) : super(path)

}